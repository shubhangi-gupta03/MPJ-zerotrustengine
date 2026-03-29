import { Link } from "react-router-dom";
import { motion } from "framer-motion";
import { ReactNode } from "react";

const links = ["dashboard", "users", "devices", "behavior", "risk", "policies", "audit", "reports", "settings"];

export function PageShell({ title, children }: { title: string; children: ReactNode }) {
  return (
    <div className="min-h-screen bg-[var(--bg-primary)] text-[var(--text-primary)]">
      <div className="mx-auto grid max-w-[1400px] grid-cols-[220px_1fr] gap-6 p-6">
        <aside className="glass-card p-4">
          <h1 className="mb-4 font-['Syne'] text-xl font-bold">ZeroTrust</h1>
          <nav className="space-y-2 text-sm">
            {links.map((l) => (
              <Link className="block rounded px-3 py-2 hover:bg-white/10" key={l} to={`/${l}`}>
                {l}
              </Link>
            ))}
          </nav>
        </aside>
        <main>
          <motion.div initial={{ opacity: 0, y: 10 }} animate={{ opacity: 1, y: 0 }} className="glass-card p-4">
            <h2 className="font-['Syne'] text-2xl">{title}</h2>
          </motion.div>
          <div className="mt-4">{children}</div>
        </main>
      </div>
    </div>
  );
}
