import { PageShell } from "../components/PageShell";
import { Area, AreaChart, CartesianGrid, ResponsiveContainer, XAxis, YAxis } from "recharts";
import { useAlertsSocket } from "../hooks/useAlertsSocket";
import { useAppStore } from "../store/appStore";

const series = Array.from({ length: 12 }).map((_, i) => ({ t: `${i * 5}m`, risk: 20 + Math.round(Math.random() * 70) }));

export default function DashboardPage() {
  useAlertsSocket();
  const threats = useAppStore((s) => s.threats);
  return (
    <PageShell title="Dashboard">
      <div className="grid gap-4 lg:grid-cols-3">
        <div className="glass-card col-span-2 h-80 p-4">
          <ResponsiveContainer width="100%" height="100%">
            <AreaChart data={series}>
              <CartesianGrid strokeDasharray="3 3" stroke="#334" />
              <XAxis dataKey="t" />
              <YAxis />
              <Area type="monotone" dataKey="risk" stroke="#00C2FF" fill="#00C2FF33" />
            </AreaChart>
          </ResponsiveContainer>
        </div>
        <div className="glass-card h-80 overflow-auto p-4">
          <h3 className="mb-2 font-semibold">Live Threat Feed</h3>
          <div className="space-y-2 text-sm">
            {threats.map((t) => <div className="rounded border-l-2 border-red-500 bg-white/5 p-2" key={t.id}>{t.message}</div>)}
          </div>
        </div>
      </div>
    </PageShell>
  );
}
