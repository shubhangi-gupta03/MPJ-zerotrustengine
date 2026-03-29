import { motion } from "framer-motion";

export default function MfaVerifyPage() {
  return (
    <div className="flex min-h-screen items-center justify-center bg-[var(--bg-primary)] p-6">
      <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} className="glass-card w-full max-w-md p-8">
        <h2 className="mb-4 font-['Syne'] text-2xl">MFA Verification</h2>
        <div className="grid grid-cols-6 gap-2">
          {Array.from({ length: 6 }).map((_, i) => (
            <input key={i} className="rounded border border-white/20 bg-transparent p-3 text-center" maxLength={1} />
          ))}
        </div>
      </motion.div>
    </div>
  );
}
