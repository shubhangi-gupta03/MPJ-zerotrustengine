package com.zerotrust.auth_service.service;

import com.zerotrust.auth_service.exception.AuthException;
import com.zerotrust.auth_service.repository.UserRepository;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Random;

@Service
public class MfaService {
    private final StringRedisTemplate redisTemplate;
    private final JavaMailSender mailSender;
    private final UserRepository userRepository;
    private final int otpTtlMinutes;
    private final Random random = new Random();

    public MfaService(
            StringRedisTemplate redisTemplate,
            JavaMailSender mailSender,
            UserRepository userRepository,
            @Value("${auth.mfa.otp-ttl-minutes:5}") int otpTtlMinutes
    ) {
        this.redisTemplate = redisTemplate;
        this.mailSender = mailSender;
        this.userRepository = userRepository;
        this.otpTtlMinutes = otpTtlMinutes;
    }

    public void sendOtp(String usernameOrEmail) {
        var user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> new AuthException(HttpStatus.NOT_FOUND, "User not found"));
        String otp = String.format("%06d", random.nextInt(1_000_000));
        redisTemplate.opsForValue().set("otp:" + user.getUsername(), otp, Duration.ofMinutes(otpTtlMinutes));
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false);
            helper.setTo(user.getEmail());
            helper.setSubject("Your OTP for Zero Trust Login");
            helper.setText("Your OTP is: " + otp + ". It expires in " + otpTtlMinutes + " minutes.", false);
            mailSender.send(message);
        } catch (Exception ex) {
            throw new AuthException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to send OTP");
        }
    }

    public boolean verifyOtp(String usernameOrEmail, String otp) {
        var user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> new AuthException(HttpStatus.NOT_FOUND, "User not found"));
        String key = "otp:" + user.getUsername();
        String stored = redisTemplate.opsForValue().get(key);
        if (stored != null && stored.equals(otp)) {
            redisTemplate.delete(key);
            return true;
        }
        return false;
    }
}
