import { PageShell } from "../components/PageShell";

export default function SettingsPage() {
  return (
    <PageShell title="System Settings">
      <div className="grid gap-6 lg:grid-cols-4">
        <div className="lg:col-span-1 space-y-2">
          <button className="w-full text-left px-4 py-2 bg-white/10 text-white font-medium rounded-lg border-l-4 border-cyan-500">General Settings</button>
          <button className="w-full text-left px-4 py-2 text-white/60 hover:bg-white/5 hover:text-white transition-colors rounded-lg border-l-4 border-transparent">API Keys & Tokens</button>
          <button className="w-full text-left px-4 py-2 text-white/60 hover:bg-white/5 hover:text-white transition-colors rounded-lg border-l-4 border-transparent">Integrations</button>
          <button className="w-full text-left px-4 py-2 text-white/60 hover:bg-white/5 hover:text-white transition-colors rounded-lg border-l-4 border-transparent">Team Members</button>
          <button className="w-full text-left px-4 py-2 text-red-400 hover:bg-red-500/10 transition-colors rounded-lg border-l-4 border-transparent mt-8">Danger Zone</button>
        </div>

        <div className="lg:col-span-3 space-y-6">
          <div className="glass-card p-6">
            <h3 className="text-lg font-semibold text-white mb-4">Instance Configuration</h3>
            <div className="space-y-4 max-w-lg">
              <div>
                <label className="block text-sm text-white/60 mb-1">Organization Name</label>
                <input type="text" defaultValue="Acme Corp Security" className="w-full bg-black/20 border border-white/10 rounded-lg p-2 text-white focus:outline-none focus:border-cyan-500" />
              </div>
              <div>
                <label className="block text-sm text-white/60 mb-1">Support Email</label>
                <input type="email" defaultValue="secops@acme.com" className="w-full bg-black/20 border border-white/10 rounded-lg p-2 text-white focus:outline-none focus:border-cyan-500" />
              </div>
            </div>
            <button className="mt-6 px-4 py-2 bg-gradient-to-r from-cyan-500 to-blue-600 text-white font-medium rounded hover:brightness-110 transition-all">Save Changes</button>
          </div>

          <div className="glass-card p-6">
            <h3 className="text-lg font-semibold text-white mb-4">Global Security Defaults</h3>
            <div className="space-y-4">
              <label className="flex items-center gap-3 cursor-pointer group">
                <div className="w-10 h-6 bg-cyan-500 rounded-full relative transition-colors">
                  <div className="absolute right-1 top-1 w-4 h-4 bg-white rounded-full"></div>
                </div>
                <div>
                  <p className="text-white group-hover:text-cyan-400 transition-colors">Enforce MFA globally</p>
                  <p className="text-xs text-white/40">Require Multi-Factor Authentication for every backend service login.</p>
                </div>
              </label>
              
              <label className="flex items-center gap-3 cursor-pointer group mt-4">
                <div className="w-10 h-6 bg-white/10 rounded-full relative transition-colors group-hover:bg-white/20">
                  <div className="absolute left-1 top-1 w-4 h-4 bg-white/50 rounded-full"></div>
                </div>
                <div>
                  <p className="text-white group-hover:text-white/80 transition-colors">Strict Device Compliance</p>
                  <p className="text-xs text-white/40">Immediately reject any session from non-compliant devices regardless of user role.</p>
                </div>
              </label>
            </div>
          </div>
        </div>
      </div>
    </PageShell>
  );
}
