const scope = /** @type {import("./protocol").WorkerScope} */ (/** @type {unknown} */ (self));

async function initialize() {
  const vendor = new URL("./vendor/", scope.location.href).href;
  scope.importScripts(new URL("pyodide.js", vendor).href);
  const python = await scope.loadPyodide({ indexURL: vendor });
  await python.loadPackage("numpy");
  const response = await fetch(new URL("simulation.py", scope.location.href));
  if (!response.ok) throw new Error("Cannot load simulation.py");
  python.FS.writeFile("/home/pyodide/simulation.py", await response.text());
  return python.runPython("from simulation import create\ncreate");
}

/**
 * @template T
 * @param {import("./protocol").Proxy<T>} proxy
 */
function convert(proxy) {
  try {
    return proxy.toJs({ create_pyproxies: false });
  } finally {
    proxy.destroy();
  }
}

initialize().then((create) => {
  /** @type {import("./protocol").SimulationProxy | undefined} */
  let simulation;
  scope.onmessage = ({ data }) => {
    try {
      if (data.type === "reset") {
        const next = create(data.scenario, data.count, data.integrator, data.seed);
        simulation?.destroy();
        simulation = next;
        const frame = convert(simulation.frame());
        const bodies = convert(simulation.metadata());
        scope.postMessage({ type: "reset", id: data.id, frame, bodies }, transfer(frame));
      } else if (data.type === "advance" && simulation) {
        simulation.advance(data.steps, data.dt, data.accrete);
        const frame = convert(simulation.frame());
        scope.postMessage({ type: "frame", id: data.id, frame }, transfer(frame));
      }
    } catch {
      scope.postMessage({ type: "failed", id: data.id });
    }
  };
  scope.postMessage({ type: "ready" });
}, () => scope.postMessage({ type: "fatal" }));

/** @param {import("./protocol").Frame} frame */
function transfer(frame) {
  return [frame[2].buffer, frame[3].buffer];
}
