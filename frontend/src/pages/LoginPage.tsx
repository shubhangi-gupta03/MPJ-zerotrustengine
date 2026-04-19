import { motion } from "framer-motion";

import { useNavigate } from "react-router-dom";

export default function LoginPage() {
  const navigate = useNavigate();

  const handleLogin = (e: React.FormEvent) => {
    e.preventDefault();
    navigate("/dashboard");
  };

  return (
    <div className="flex min-h-screen items-center justify-center bg-[var(--bg-primary)] p-6">
      <motion.form 
        onSubmit={handleLogin}
        initial={{ opacity: 0, scale: 0.95 }} 
        animate={{ opacity: 1, scale: 1 }} 
        className="glass-card w-full max-w-md space-y-4 p-8"
      >
        <h1 className="font-['Syne'] text-3xl font-bold">ZeroTrust</h1>
        <input className="w-full rounded-lg border border-white/20 bg-transparent p-3" placeholder="Email" />
        <input className="w-full rounded-lg border border-white/20 bg-transparent p-3" placeholder="Password" type="password" />
        <button type="submit" className="w-full rounded-lg bg-gradient-to-r from-cyan-400 to-blue-700 p-3 font-semibold">Sign In</button>
      </motion.form>
    </div>
  );
}
