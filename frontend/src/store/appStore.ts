import { create } from "zustand";
import { ThreatEvent } from "../types";

interface AppState {
  threats: ThreatEvent[];
  setThreats: (threats: ThreatEvent[]) => void;
  addThreat: (threat: ThreatEvent) => void;
}

export const useAppStore = create<AppState>((set) => ({
  threats: [],
  setThreats: (threats) => set({ threats }),
  addThreat: (threat) => set((state) => ({ threats: [threat, ...state.threats].slice(0, 100) }))
}));
