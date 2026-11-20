import { draw } from "./view.js";

/**
 * @template {Element} T
 * @param {string} id
 * @param {{new(): T}} constructor
 */
function find(id, constructor) {
  const element = document.getElementById(id);
  if (!(element instanceof constructor)) throw new Error(`Missing element ${id}`);
  return element;
}

const canvas = find("sky", HTMLCanvasElement);
const loading = find("loading", HTMLElement);
const settings = find("settings", HTMLFormElement);
const controls = find("controls", HTMLFieldSetElement);
const scenario = find("scenario", HTMLSelectElement);
const integrator = find("integrator", HTMLSelectElement);
const count = find("count", HTMLInputElement);
const dt = find("dt", HTMLInputElement);
const warp = find("warp", HTMLInputElement);
const orientation = find("orientation", HTMLInputElement);
const center = find("center", HTMLSelectElement);
const play = find("play", HTMLButtonElement);
const reset = find("reset", HTMLButtonElement);
const zoomIn = find("zoom-in", HTMLButtonElement);
const zoomOut = find("zoom-out", HTMLButtonElement);
const trails = find("trails", HTMLInputElement);
const original = find("original", HTMLInputElement);
const accrete = find("accrete", HTMLInputElement);
const status = find("status", HTMLElement);
const error = find("error", HTMLElement);
const retry = find("retry", HTMLButtonElement);
const elapsed = find("elapsed", HTMLElement);
const extent = find("extent", HTMLElement);
const energy = find("energy", HTMLElement);
const visible = find("visible", HTMLElement);

const worker = new Worker(new URL("./worker.js", import.meta.url));
const integer = new Intl.NumberFormat();
let running = false;
let pending = false;
let resetting = false;
let generation = 0;
let zoom = 16;
/** @type {import("./protocol").Body[]} */
let bodies = [];
/** @type {import("./protocol").Frame | undefined} */
let frame;
/** @type {import("./protocol").Frame | undefined} */
let initial;

/** @param {import("./protocol").Request} request */
function send(request) {
  pending = true;
  worker.postMessage(request);
}

/** @param {boolean} value @param {string} message */
function setRunning(value, message) {
  running = value;
  play.textContent = value ? "Pause" : (frame?.[0] ? "Resume" : "Start");
  status.textContent = message;
  if (value) requestAnimationFrame(advance);
}

function advance() {
  if (!running || pending) return;
  if (!dt.checkValidity() || !warp.checkValidity()) {
    setRunning(false, "Correct the step or warp to resume.");
    return;
  }
  send({
    type: "advance", id: generation, steps: warp.valueAsNumber,
    dt: dt.valueAsNumber, accrete: accrete.checked,
  });
}

/** @param {boolean} clear */
function render(clear) {
  if (!frame || !initial || !orientation.checkValidity()) return;
  const { width, visibleDust } = draw(canvas, bodies, frame, initial, {
    center: Number(center.value), orientation: orientation.valueAsNumber,
    zoom, original: original.checked, trails: trails.checked,
  }, clear);
  elapsed.textContent = `${frame[0].toFixed(3)} yr`;
  extent.textContent = `${width.toPrecision(3)} AU`;
  visible.textContent = integer.format(visibleDust);
  energy.textContent = (frame[1] * 1e12).toExponential(5);
}

function resetSimulation() {
  if (!count.reportValidity()) return;
  resetting = true;
  setRunning(false, "Preparing the simulation…");
  play.disabled = true;
  reset.disabled = true;
  error.hidden = true;
  zoom = 16;
  generation++;
  send({
    type: "reset", id: generation, scenario: scenario.value, count: count.valueAsNumber,
    integrator: integrator.value, seed: 524,
  });
}

function showFatal() {
  worker.terminate();
  running = false;
  controls.disabled = true;
  loading.textContent = "Simulation unavailable";
  status.textContent = "";
  error.textContent = "Python could not load. Reload the simulator to try again.";
  error.hidden = false;
  retry.hidden = false;
}

worker.onerror = showFatal;
worker.onmessage = (/** @type {MessageEvent<import("./protocol").Reply>} */ { data }) => {
  if (data.type === "ready") {
    resetSimulation();
    return;
  }
  if (data.type === "fatal") {
    showFatal();
    return;
  }
  if (!("id" in data) || data.id !== generation) return;
  pending = false;
  if (data.type === "failed") {
    if (!frame) {
      showFatal();
      return;
    }
    setRunning(false, "Reset the simulation to continue.");
    play.disabled = true;
    reset.disabled = false;
    error.textContent = "The simulation could not continue. Try a smaller time step.";
    error.hidden = false;
    resetting = false;
    return;
  }
  frame = data.frame;
  if (data.type === "reset") {
    initial = frame;
    bodies = data.bodies;
    const defaultCenter = bodies.findIndex(([name]) => name === "Jupiter");
    center.replaceChildren(new Option("Origin", "-1"), ...bodies.map(([name], index) => new Option(name, String(index))));
    center.value = String(defaultCenter);
    accrete.disabled = integrator.value === "hermite";
    loading.hidden = true;
    controls.disabled = false;
    play.disabled = false;
    reset.disabled = false;
    resetting = false;
    setRunning(false, "Ready to start.");
  }
  render(data.type === "reset");
  if (running) requestAnimationFrame(advance);
};

settings.addEventListener("submit", (event) => {
  event.preventDefault();
  if (resetting || play.disabled) return;
  setRunning(!running, running ? "Paused." : "Running…");
});

scenario.addEventListener("change", () => {
  const solar = scenario.value === "solar";
  count.min = solar ? "14" : "2";
  count.value = solar ? "2000" : "1000";
  orientation.value = solar ? "0" : "70";
  warp.value = "50";
  dt.value = "0.01";
  resetSimulation();
});
integrator.addEventListener("change", resetSimulation);
count.addEventListener("change", resetSimulation);
reset.addEventListener("click", resetSimulation);
retry.addEventListener("click", () => window.location.reload());

for (const control of [orientation, center, original, trails]) {
  control.addEventListener("change", () => render(true));
}
zoomIn.addEventListener("click", () => {
  zoom = Math.max(0.125, zoom / 2);
  render(true);
});
zoomOut.addEventListener("click", () => {
  zoom = Math.min(256, zoom * 2);
  render(true);
});
