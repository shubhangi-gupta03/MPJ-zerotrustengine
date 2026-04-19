import { PageShell } from "../components/PageShell";
import { Area, AreaChart, CartesianGrid, ResponsiveContainer, Tooltip, XAxis, YAxis } from "recharts";
import { useAlertsSocket } from "../hooks/useAlertsSocket";
import { useAppStore } from "../store/appStore";
import { useEffect } from "react";
import { ThreatEvent } from "../types";

const series = [
  { t: "10:00", risk: 15 },
  { t: "10:05", risk: 18 },
  { t: "10:10", risk: 22 },
  { t: "10:15", risk: 19 },
  { t: "10:20", risk: 45 },
  { t: "10:25", risk: 36 },
  { t: "10:30", risk: 25 },
  { t: "10:35", risk: 18 },
  { t: "10:40", risk: 20 },
  { t: "10:45", risk: 15 },
];

const MOCK_THREATS: ThreatEvent[] = [
  { id: "1", message: "High risk login detected from unknown IP (192.168.1.45).", severity: "CRITICAL", time: "10:42 AM" },
  { id: "2", message: "Device MAC-A39 non-compliant: OS version outdated.", severity: "MEDIUM", time: "10:15 AM" },
  { id: "3", message: "Unusual data transfer pattern for user 'alice.smith'.", severity: "HIGH", time: "09:50 AM" },
  { id: "4", message: "Multiple failed MFA attempts for 'admin' account.", severity: "HIGH", time: "09:12 AM" },
];

export default function DashboardPage() {
  useAlertsSocket();
  const threats = useAppStore((s) => s.threats);
  const setThreats = useAppStore((s) => s.setThreats);

  useEffect(() => {
    if (threats.length === 0) {
      setThreats(MOCK_THREATS);
    }
  }, []);

  const displayThreats = threats.length > 0 ? threats : MOCK_THREATS;

  return (
    <PageShell title="Overview">
      <div className="mb-6 grid gap-4 md:grid-cols-2 lg:grid-cols-4">
        {[
          { label: "Active Devices", value: "1,248", desc: "98% Compliant", color: "text-blue-400" },
          { label: "Active Sessions", value: "842", desc: "+12% this hour", color: "text-green-400" },
          { label: "Avg Risk Score", value: "18", desc: "Low Risk", color: "text-emerald-400" },
          { label: "Critical Threats", value: "3", desc: "Requires Attention", color: "text-red-400" },
        ].map((stat, i) => (
          <div key={i} className="glass-card p-5 transition-transform hover:scale-[1.02]">
            <p className="text-sm font-medium text-white/60">{stat.label}</p>
            <h2 className={`mt-2 text-4xl font-bold ${stat.color}`}>{stat.value}</h2>
            <p className="mt-2 text-xs text-white/50">{stat.desc}</p>
          </div>
        ))}
      </div>

      <div className="grid gap-6 lg:grid-cols-3">
        <div className="glass-card col-span-2 flex h-96 flex-col p-5">
          <h3 className="mb-4 text-lg font-semibold">Network Risk Score Trend</h3>
          <div className="flex-1">
            <ResponsiveContainer width="100%" height="100%">
              <AreaChart data={series}>
                <defs>
                  <linearGradient id="colorRisk" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="5%" stopColor="#00C2FF" stopOpacity={0.4} />
                    <stop offset="95%" stopColor="#00C2FF" stopOpacity={0} />
                  </linearGradient>
                </defs>
                <CartesianGrid strokeDasharray="3 3" stroke="#ffffff10" vertical={false} />
                <XAxis dataKey="t" stroke="#ffffff50" tick={{ fill: "#ffffff80", fontSize: 12 }} tickMargin={10} />
                <YAxis stroke="#ffffff50" tick={{ fill: "#ffffff80", fontSize: 12 }} tickMargin={10} />
                <Tooltip
                  contentStyle={{ backgroundColor: "#11131a", borderColor: "#ffffff20", borderRadius: "8px" }}
                  itemStyle={{ color: "#00C2FF" }}
                />
                <Area type="monotone" dataKey="risk" stroke="#00C2FF" strokeWidth={3} fillOpacity={1} fill="url(#colorRisk)" />
              </AreaChart>
            </ResponsiveContainer>
          </div>
        </div>

        <div className="glass-card flex h-96 flex-col p-5">
          <h3 className="mb-4 flex items-center justify-between text-lg font-semibold">
            Live Threat Feed
            <span className="animate-pulse rounded-full bg-red-500/20 px-2 py-1 text-xs text-red-400">Live</span>
          </h3>
          <div className="custom-scrollbar flex-1 space-y-3 overflow-y-auto pr-2">
            {displayThreats.map((t) => (
              <div className="relative overflow-hidden rounded-lg border border-white/5 bg-white/5 p-3" key={t.id}>
                <div
                  className={`absolute bottom-0 left-0 top-0 w-1 ${
                    t.severity === "CRITICAL" ? "bg-red-500" : t.severity === "HIGH" ? "bg-orange-500" : "bg-yellow-500"
                  }`}
                ></div>
                <p className="text-sm text-white/90">{t.message}</p>
                <div className="mt-2 flex items-center justify-between">
                  <span className="text-xs font-semibold text-white/60">{t.severity}</span>
                  <span className="text-xs text-white/40">{t.time}</span>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>
    </PageShell>
  );
}
