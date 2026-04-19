import { PageShell } from "../components/PageShell";

const RISK_ALERTS = [
  { id: 1, type: "Data Breach", entity: "HR Department", level: "Critical", status: "Open" },
  { id: 2, type: "Malware Infection", entity: "Device WIN-B82", level: "High", status: "Investigating" },
  { id: 3, type: "DDoS Attempt", entity: "API Gateway", level: "Medium", status: "Mitigated" },
  { id: 4, type: "Privilege Escalation", entity: "User 'guest'", level: "Low", status: "Resolved" },
];

export default function RiskPage() {
  return (
    <PageShell title="Risk Insights">
      <div className="grid gap-6 md:grid-cols-2 mb-6">
        <div className="glass-card p-6 flex flex-col items-center justify-center">
          <div className="relative flex h-48 w-48 items-center justify-center rounded-full border-8 border-yellow-500/20 shadow-[0_0_40px_rgba(234,179,8,0.1)]">
            <svg className="absolute inset-0 h-full w-full -rotate-90 transform">
              <circle cx="96" cy="96" r="88" fill="none" stroke="currentColor" strokeWidth="8" className="text-yellow-500" strokeDasharray="552" strokeDashoffset="180" />
            </svg>
            <div className="text-center">
              <span className="text-5xl font-bold text-white">68</span>
              <p className="text-sm text-yellow-400 mt-1">Medium Risk</p>
            </div>
          </div>
          <p className="mt-6 text-white/60 text-sm">Overall Organizational Risk Score</p>
        </div>
        
        <div className="glass-card p-6">
          <h3 className="mb-4 font-semibold">Active Risk Alerts</h3>
          <div className="space-y-3">
            {RISK_ALERTS.map((alert) => (
              <div key={alert.id} className="flex items-center justify-between bg-white/5 p-3 rounded-lg border border-white/5">
                <div>
                  <p className="font-medium text-white">{alert.type}</p>
                  <p className="text-xs text-white/50">{alert.entity}</p>
                </div>
                <div className="text-right">
                  <p className={`text-xs font-bold uppercase tracking-wider ${
                    alert.level === 'Critical' ? 'text-red-400' :
                    alert.level === 'High' ? 'text-orange-400' :
                    alert.level === 'Medium' ? 'text-yellow-400' : 'text-green-400'
                  }`}>{alert.level}</p>
                  <p className="text-xs text-white/40 mt-1">{alert.status}</p>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>
    </PageShell>
  );
}
