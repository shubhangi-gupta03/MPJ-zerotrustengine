export interface KpiCard {
  title: string;
  value: string;
  trend: string;
}

export interface ThreatEvent {
  id: string;
  message: string;
  severity: "LOW" | "MEDIUM" | "HIGH" | "CRITICAL";
  time: string;
}
