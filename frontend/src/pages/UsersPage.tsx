import { PageShell } from "../components/PageShell";

const MOCK_USERS = [
  { id: "U-101", name: "Alice Smith", email: "alice.smith@example.com", role: "Admin", riskScore: 12, status: "Active", lastLogin: "10 mins ago" },
  { id: "U-102", name: "Bob Johnson", email: "bob.j@example.com", role: "Developer", riskScore: 68, status: "Review", lastLogin: "1 hour ago" },
  { id: "U-103", name: "Charlie Davis", email: "cdavis@example.com", role: "Analyst", riskScore: 5, status: "Active", lastLogin: "5 mins ago" },
  { id: "U-104", name: "Diana Prince", email: "diana.p@example.com", role: "Manager", riskScore: 89, status: "Blocked", lastLogin: "2 days ago" },
  { id: "U-105", name: "Evan Wright", email: "evan.w@example.com", role: "Developer", riskScore: 24, status: "Active", lastLogin: "3 hours ago" },
];

export default function UsersPage() {
  return (
    <PageShell title="User Management">
      <div className="glass-card mb-6 flex items-center justify-between p-4">
        <div className="flex gap-4">
          <input 
            type="text" 
            placeholder="Search users..." 
            className="w-64 rounded-lg border border-white/10 bg-white/5 p-2 text-sm text-white placeholder-white/40 focus:border-cyan-400 focus:outline-none"
          />
          <select className="rounded-lg border border-white/10 bg-[#11131a] p-2 text-sm text-white/80 focus:border-cyan-400 focus:outline-none">
            <option>All Roles</option>
            <option>Admin</option>
            <option>Developer</option>
            <option>Manager</option>
          </select>
        </div>
        <button className="rounded-lg bg-gradient-to-r from-cyan-400 to-blue-600 px-4 py-2 font-medium text-white hover:brightness-110">
          + Invite User
        </button>
      </div>

      <div className="glass-card overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left text-sm">
            <thead className="bg-white/5 text-white/60">
              <tr>
                <th className="p-4 font-semibold">User</th>
                <th className="p-4 font-semibold">Role</th>
                <th className="p-4 font-semibold">Risk Score</th>
                <th className="p-4 font-semibold">Status</th>
                <th className="p-4 font-semibold">Last Login</th>
                <th className="p-4 font-semibold text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-white/10">
              {MOCK_USERS.map((user) => (
                <tr key={user.id} className="transition-colors hover:bg-white/5">
                  <td className="p-4">
                    <div className="font-medium text-white">{user.name}</div>
                    <div className="text-xs text-white/50">{user.email}</div>
                  </td>
                  <td className="p-4">{user.role}</td>
                  <td className="p-4">
                    <div className="flex items-center gap-2">
                      <div className="h-2 w-16 overflow-hidden rounded-full bg-white/10">
                        <div 
                          className={`h-full ${user.riskScore > 75 ? 'bg-red-500' : user.riskScore > 30 ? 'bg-yellow-500' : 'bg-green-500'}`}
                          style={{ width: `${user.riskScore}%` }}
                        />
                      </div>
                      <span className="text-xs">{user.riskScore}</span>
                    </div>
                  </td>
                  <td className="p-4">
                    <span className={`inline-flex items-center rounded-full px-2 py-1 text-xs font-medium ${
                      user.status === 'Active' ? 'bg-green-500/20 text-green-400' : 
                      user.status === 'Blocked' ? 'bg-red-500/20 text-red-400' : 
                      'bg-yellow-500/20 text-yellow-400'
                    }`}>
                      {user.status}
                    </span>
                  </td>
                  <td className="p-4 text-white/70">{user.lastLogin}</td>
                  <td className="p-4 text-right">
                    <button className="text-cyan-400 hover:text-cyan-300">View Details</button>
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
