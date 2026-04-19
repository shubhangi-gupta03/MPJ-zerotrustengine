import { PageShell } from "../components/PageShell";

const MOCK_BEHAVIORS = [
  { id: "B-1", user: "alice.smith", event: "Unusual Working Hours", score: 85, predictedAction: "Block Access", timestamp: "10 mins ago" },
  { id: "B-2", user: "bob.j", event: "Impossible Travel (NY to London in 1h)", score: 98, predictedAction: "Force MFA", timestamp: "45 mins ago" },
  { id: "B-3", user: "dev_service", event: "High Data Exfiltration", score: 92, predictedAction: "Suspend Account", timestamp: "2 hours ago" },
  { id: "B-4", user: "charlie.d", event: "Multiple Device Logins", score: 60, predictedAction: "Flag for Review", timestamp: "5 hours ago" },
];

export default function BehaviorPage() {
  return (
    <PageShell title="Behavioral Analytics">
      <div className="mb-6 grid gap-4 md:grid-cols-3">
        <div className="glass-card p-5 border-t-2 border-red-500">
          <p className="text-white/60">Anomalies Detected (24h)</p>
          <h2 className="mt-2 text-4xl font-bold text-white">124</h2>
        </div>
        <div className="glass-card p-5 border-t-2 border-yellow-500">
          <p className="text-white/60">Accounts Under Review</p>
          <h2 className="mt-2 text-4xl font-bold text-white">14</h2>
        </div>
        <div className="glass-card p-5 border-t-2 border-cyan-500">
          <p className="text-white/60">Avg Confidence Score</p>
          <h2 className="mt-2 text-4xl font-bold text-white">94%</h2>
        </div>
      </div>

      <div className="glass-card">
        <div className="border-b border-white/10 p-4">
          <h3 className="font-semibold">Recent LSTM Model Triggers</h3>
        </div>
        <div className="overflow-x-auto p-4">
          <table className="w-full text-left text-sm">
            <thead className="bg-white/5 text-white/60">
              <tr>
                <th className="p-3">Trigger ID</th>
                <th className="p-3">User/Entity</th>
                <th className="p-3">Behavior Event</th>
                <th className="p-3">Anomaly Score</th>
                <th className="p-3">Predicted Resolution</th>
                <th className="p-3 text-right">Time</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-white/10">
              {MOCK_BEHAVIORS.map((b) => (
                <tr key={b.id} className="hover:bg-white/5 transition-colors">
                  <td className="p-3 text-white/50">{b.id}</td>
                  <td className="p-3 font-medium text-cyan-400">{b.user}</td>
                  <td className="p-3">{b.event}</td>
                  <td className="p-3">
                    <span className={`inline-block w-8 text-center rounded px-2 py-1 text-xs font-bold ${
                      b.score > 80 ? 'bg-red-500/20 text-red-400' : 'bg-yellow-500/20 text-yellow-400'
                    }`}>
                      {b.score}
                    </span>
                  </td>
                  <td className="p-3 text-white/80">{b.predictedAction}</td>
                  <td className="p-3 text-right text-white/50">{b.timestamp}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </PageShell>
  );
}
