import { PageShell } from "../components/PageShell";

const MOCK_POLICIES = [
  { id: "POL-01", name: "Executive Access", scope: "VP & C-Level", rule: "MFA Every 24h", status: "Active" },
  { id: "POL-02", name: "Contractor Bounds", scope: "Contractors", rule: "Block after 6 PM", status: "Active" },
  { id: "POL-03", name: "High-Risk Devices", scope: "Risk > 80", rule: "Suspend Network Access", status: "Draft" },
  { id: "POL-04", name: "Offshore Logins", scope: "Non-US IPs", rule: "Require VPN & Hardware Key", status: "Active" },
];

export default function PoliciesPage() {
  return (
    <PageShell title="Policy Engine">
      <div className="mb-6 flex gap-4">
        <button className="bg-gradient-to-r from-cyan-500 to-blue-600 px-4 py-2 rounded-lg font-medium text-white shadow-lg hover:brightness-110">
          + Create New Policy
        </button>
        <button className="bg-white/10 px-4 py-2 rounded-lg font-medium text-white hover:bg-white/20">
          Import Ruleset
        </button>
      </div>

      <div className="glass-card grid gap-4 p-4 sm:grid-cols-2 lg:grid-cols-2">
        {MOCK_POLICIES.map((p) => (
          <div key={p.id} className="border border-white/10 rounded-lg p-5 hover:bg-white/5 transition-colors">
            <div className="flex justify-between items-start mb-4">
              <div>
                <h3 className="text-lg font-bold text-white">{p.name}</h3>
                <p className="text-sm text-cyan-400 mt-1">Applies to: {p.scope}</p>
              </div>
              <span className={`px-2 py-1 text-xs font-semibold rounded ${
                p.status === 'Active' ? 'bg-green-500/20 text-green-400' : 'bg-white/10 text-white/40'
              }`}>{p.status}</span>
            </div>
            
            <div className="bg-black/40 rounded p-3 text-sm font-mono text-white/80 mb-4">
              IF <span className="text-yellow-400">{p.scope}</span> THEN <span className="text-red-400">{p.rule}</span>
            </div>
            
            <div className="flex gap-3 mt-4 pt-4 border-t border-white/10">
              <button className="text-sm text-cyan-400 hover:text-cyan-300">Edit</button>
              <button className="text-sm text-white/50 hover:text-white">Disable</button>
              <button className="text-sm text-red-500 hover:text-red-400">Delete</button>
            </div>
          </div>
        ))}
      </div>
    </PageShell>
  );
}
