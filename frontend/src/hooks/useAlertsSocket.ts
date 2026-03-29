import { useEffect } from "react";
import { useAppStore } from "../store/appStore";
import { ThreatEvent } from "../types";

export function useAlertsSocket(): void {
  const addThreat = useAppStore((s) => s.addThreat);

  useEffect(() => {
    const ws = new WebSocket(import.meta.env.VITE_WS_URL ?? "ws://localhost:8086/ws/events");
    ws.onmessage = (event) => {
      const threat: ThreatEvent = {
        id: crypto.randomUUID(),
        message: event.data,
        severity: "HIGH",
        time: new Date().toISOString()
      };
      addThreat(threat);
    };
    return () => ws.close();
  }, [addThreat]);
}
