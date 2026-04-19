import { PageShell } from "../components/PageShell";

const MOCK_AUDITS = [
  { id: "A-5491", action: "Policy Updated", actor: "admin.user", target: "POL-02", time: "12:04 PM" },
  { id: "A-5490", action: "User Revoked", actor: "system.automator", target: "U-1442", time: "11:59 AM" },
  { id: "A-5489", action: "Login Failed", actor: "unknown.ip", target: "auth-service", time: "11:32 AM" },
  { id: "A-5488", action: "Service Restarted", actor: "sysadmin", target: "api-gateway", time: "09:00 AM" },
  { id: "A-5487", action: "Report Generated", actor: "auditor_1", target: "Weekly Compliance", time: "08:15 AM" },
];

export default function AuditPage() {
  return (
    <PageShell title="Audit Logs">
      <div className="glass-card">
        <div className="flex p-4 gap-4 border-b border-white/10 bg-black/20">
          <input className="bg-transparent border border-white/20 rounded px-3 py-1.5 text-sm flex-1 placeholder-white/30" placeholder="Search by Action, Actor, or Target ID..." />
          <input type="date" className="bg-transparent border border-white/20 rounded px-3 py-1.5 text-sm text-white/70" />
          <button className="bg-white/10 px-4 py-1.5 rounded text-sm hover:bg-white/20">Export CSV</button>
        </div>
        <table className="w-full text-left text-sm">
          <thead className="bg-white/5 text-white/60">
            <tr>
              <th className="p-4 font-semibold">Event ID</th>
              <th className="p-4 font-semibold">Time</th>
              <th className="p-4 font-semibold">Action</th>
              <th className="p-4 font-semibold">Actor</th>
              <th className="p-4 font-semibold">Target Resource</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-white/10">
            {MOCK_AUDITS.map((log) => (
              <tr key={log.id} className="hover:bg-white/5 font-mono text-xs">
                <td className="p-4 text-white/40">{log.id}</td>
                <td className="p-4 text-white/60">{log.time}</td>
                <td className="p-4 text-white">{log.action}</td>
                <td className="p-4 text-cyan-400">{log.actor}</td>
                <td className="p-4 text-blue-300">{log.target}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </PageShell>
  );
}
