import { PageShell } from "../components/PageShell";

const MOCK_DEVICES = [
  { id: "MAC-A39", os: "macOS 14.1", owner: "Alice Smith", compliance: "Compliant", risk: "Low", lastSeen: "2 mins ago" },
  { id: "WIN-B82", os: "Windows 11", owner: "Bob Johnson", compliance: "Warning", risk: "Medium", lastSeen: "15 mins ago" },
  { id: "LNX-C11", os: "Ubuntu 22.04", owner: "Charlie Davis", compliance: "Compliant", risk: "Low", lastSeen: "1 hour ago" },
  { id: "IOS-D44", os: "iOS 17.2", owner: "Diana Prince", compliance: "Non-Compliant", risk: "Critical", lastSeen: "Just now" },
  { id: "MAC-E55", os: "macOS 13.5", owner: "Evan Wright", compliance: "Compliant", risk: "Low", lastSeen: "4 hours ago" },
];

export default function DevicesPage() {
  return (
    <PageShell title="Device Trust Inventory">
      <div className="glass-card mb-6 grid gap-4 p-4 md:grid-cols-2 lg:grid-cols-4">
        {[
          { label: "Total Devices", value: "3,412" },
          { label: "Compliant", value: "3,300", color: "text-green-400" },
          { label: "Warnings", value: "84", color: "text-yellow-400" },
          { label: "Non-Compliant", value: "28", color: "text-red-400" },
        ].map((stat, i) => (
          <div key={i} className="border-r border-white/10 px-4 last:border-0">
            <p className="text-sm text-white/60">{stat.label}</p>
            <p className={`mt-1 text-2xl font-bold ${stat.color || "text-white"}`}>{stat.value}</p>
          </div>
        ))}
      </div>

      <div className="glass-card overflow-hidden">
        <div className="flex items-center justify-between border-b border-white/10 p-4">
          <h3 className="font-semibold">Registered Devices</h3>
          <button className="rounded bg-white/10 px-3 py-1 text-sm hover:bg-white/20">Filter</button>
        </div>
        <div className="overflow-x-auto">
          <table className="w-full text-left text-sm">
            <thead className="bg-white/5 text-white/60">
              <tr>
                <th className="p-4 font-semibold">Device ID</th>
                <th className="p-4 font-semibold">Operating System</th>
                <th className="p-4 font-semibold">Owner</th>
                <th className="p-4 font-semibold">Compliance</th>
                <th className="p-4 font-semibold">Risk Level</th>
                <th className="p-4 font-semibold">Last Seen</th>
                <th className="p-4 font-semibold text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-white/10">
              {MOCK_DEVICES.map((device) => (
                <tr key={device.id} className="transition-colors hover:bg-white/5">
                  <td className="p-4 font-medium text-white">{device.id}</td>
                  <td className="p-4 text-white/80">{device.os}</td>
                  <td className="p-4 text-cyan-400">{device.owner}</td>
                  <td className="p-4">
                    <span className={`inline-flex items-center gap-1.5 ${
                      device.compliance === 'Compliant' ? 'text-green-400' : 
                      device.compliance === 'Non-Compliant' ? 'text-red-400' : 
                      'text-yellow-400'
                    }`}>
                      <span className="h-2 w-2 rounded-full bg-current"></span>
                      {device.compliance}
                    </span>
                  </td>
                  <td className="p-4">
                    <span className={`rounded px-2 py-1 text-xs font-medium ${
                      device.risk === 'Critical' ? 'bg-red-500/20 text-red-500' : 
                      device.risk === 'Medium' ? 'bg-yellow-500/20 text-yellow-500' : 
                      'bg-white/10 text-white/60'
                    }`}>
                      {device.risk}
                    </span>
                  </td>
                  <td className="p-4 text-white/60">{device.lastSeen}</td>
                  <td className="p-4 text-right space-x-2">
                    <button className="text-white/60 hover:text-white">Revoke</button>
                    <button className="text-cyan-400 hover:text-cyan-300">View</button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </PageShell>
  );
}
