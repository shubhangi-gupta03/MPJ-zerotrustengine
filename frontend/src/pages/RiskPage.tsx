import { PageShell } from "../components/PageShell";
import { Pie, PieChart, ResponsiveContainer } from "recharts";

const data = [{ name: "LOW", value: 14 }, { name: "MEDIUM", value: 10 }, { name: "HIGH", value: 4 }, { name: "CRITICAL", value: 2 }];

export default function RiskPage() {
  return (
    <PageShell title="Risk Monitor">
      <div className="glass-card h-80 p-4">
        <ResponsiveContainer width="100%" height="100%">
          <PieChart><Pie data={data} dataKey="value" nameKey="name" /></PieChart>
        </ResponsiveContainer>
      </div>
    </PageShell>
  );
}
