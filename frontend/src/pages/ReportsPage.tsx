import { PageShell } from "../components/PageShell";

const MOCK_REPORTS = [
  { id: "REP-2026-Q1", name: "Q1 Security Audit & Compliance", type: "Compliance", lastGenerated: "Mar 15, 2026", size: "2.4 MB" },
  { id: "REP-INC-042", name: "Incident Report: DDoS Attempt", type: "Incident", lastGenerated: "Mar 02, 2026", size: "1.1 MB" },
  { id: "REP-ACC-881", name: "User Access Review (Feb)", type: "Access", lastGenerated: "Feb 28, 2026", size: "4.8 MB" },
];

export default function ReportsPage() {
  return (
    <PageShell title="Reports & Analytics">
      <div className="grid gap-6 md:grid-cols-3 mb-6">
        <div className="glass-card p-5 border-l-4 border-cyan-500">
          <h3 className="font-semibold text-lg text-white">Compliance Overview</h3>
          <p className="text-sm text-white/60 mt-2">Generate SOC2 and ISO27001 readiness reports for external auditors.</p>
          <button className="mt-4 px-4 py-2 bg-white/10 text-cyan-400 text-sm rounded hover:bg-white/20 transition-colors w-full">Generate Now</button>
        </div>
        <div className="glass-card p-5 border-l-4 border-purple-500">
          <h3 className="font-semibold text-lg text-white">Access Logs</h3>
          <p className="text-sm text-white/60 mt-2">Export aggregate access logs across all zero trust boundaries.</p>
          <button className="mt-4 px-4 py-2 bg-white/10 text-purple-400 text-sm rounded hover:bg-white/20 transition-colors w-full">Export CSV</button>
        </div>
        <div className="glass-card p-5 border-l-4 border-yellow-500">
          <h3 className="font-semibold text-lg text-white">Risk Summary</h3>
          <p className="text-sm text-white/60 mt-2">Download monthly summaries of behavioral and device risk metrics.</p>
          <button className="mt-4 px-4 py-2 bg-white/10 text-yellow-400 text-sm rounded hover:bg-white/20 transition-colors w-full">Download PDF</button>
        </div>
      </div>

      <div className="glass-card">
        <h3 className="font-semibold p-4 border-b border-white/10">Previously Generated Reports</h3>
        <table className="w-full text-left text-sm">
          <thead className="bg-white/5 text-white/60">
            <tr>
              <th className="p-4 font-semibold">Report ID</th>
              <th className="p-4 font-semibold">Name</th>
              <th className="p-4 font-semibold">Type</th>
              <th className="p-4 font-semibold">Generated Date</th>
              <th className="p-4 font-semibold">Size</th>
              <th className="p-4 font-semibold text-right">Download</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-white/10">
            {MOCK_REPORTS.map((report) => (
              <tr key={report.id} className="hover:bg-white/5 transition-colors">
                <td className="p-4 font-mono text-xs text-white/40">{report.id}</td>
                <td className="p-4 font-medium text-white">{report.name}</td>
                <td className="p-4">
                  <span className={`px-2 py-1 text-xs rounded border ${
                    report.type === 'Compliance' ? 'border-cyan-500 text-cyan-400' :
                    report.type === 'Incident' ? 'border-red-500 text-red-400' : 'border-purple-500 text-purple-400'
                  }`}>
                    {report.type}
                  </span>
                </td>
                <td className="p-4 text-white/60">{report.lastGenerated}</td>
                <td className="p-4 text-white/40">{report.size}</td>
                <td className="p-4 text-right">
                  <button className="text-cyan-400 hover:text-cyan-300 underline underline-offset-2">Download</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </PageShell>
  );
}
