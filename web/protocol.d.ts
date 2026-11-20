export type Body = [name: string, radius: number, color: string];
export type Frame = [elapsed: number, energy: number, positions: Uint8Array<ArrayBuffer>, hidden: Uint8Array<ArrayBuffer>];
export type Request =
  | { type: "reset"; id: number; scenario: string; count: number; integrator: string; seed: number }
  | { type: "advance"; id: number; steps: number; dt: number; accrete: boolean };
export type Reply =
  | { type: "ready" | "fatal" }
  | { type: "reset"; id: number; bodies: Body[]; frame: Frame }
  | { type: "frame"; id: number; frame: Frame }
  | { type: "failed"; id: number };
export interface Proxy<T> {
  toJs(options: { create_pyproxies: false }): T;
  destroy(): void;
}
export interface SimulationProxy {
  advance(steps: number, dt: number, accrete: boolean): void;
  frame(): Proxy<Frame>;
  metadata(): Proxy<Body[]>;
  destroy(): void;
}
export interface WorkerScope {
  importScripts(url: string): void;
  location: Location;
  loadPyodide(options: { indexURL: string }): Promise<{
    loadPackage(name: string): Promise<void>;
    FS: { writeFile(path: string, contents: string): void };
    runPython(code: string): (scenario: string, count: number, integrator: string, seed: number) => SimulationProxy;
  }>;
  onmessage: (event: MessageEvent<Request>) => void;
  postMessage(message: Reply, transfer?: ArrayBuffer[]): void;
}
