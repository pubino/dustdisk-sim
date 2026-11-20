/** @typedef {import("./protocol").Body} Body */
/** @typedef {import("./protocol").Frame} Frame */

/**
 * @param {HTMLCanvasElement} canvas
 * @param {Body[]} bodies
 * @param {Frame} current
 * @param {Frame} initial
 * @param {{center: number, orientation: number, zoom: number, original: boolean, trails: boolean}} view
 * @param {boolean} clear
 */
export function draw(canvas, bodies, current, initial, view, clear) {
  const context = canvas.getContext("2d");
  if (!context) throw new Error("Canvas rendering is unavailable.");
  const [elapsed, , , hidden] = current;
  const positions = unpack(view.original ? initial : current);
  const origins = unpack(initial);
  const center = view.original ? -1 : view.center;
  const offset = center * 3;
  let rotation = 0;
  let halfWidth = view.zoom;
  if (view.original) {
    rotation = Math.atan2(origins[4], origins[3]);
  } else if (center > 0) {
    if (elapsed > 0) rotation = Math.atan2(positions[offset + 1] - positions[1], positions[offset] - positions[0]);
    halfWidth *= relativeRadius(positions, offset) / relativeRadius(origins, offset);
  }

  const cosRotation = Math.cos(rotation);
  const sinRotation = Math.sin(rotation);
  const inclination = view.orientation * Math.PI / 180;
  const cosInclination = Math.cos(inclination);
  const sinInclination = Math.sin(inclination);
  const originX = center < 0 ? 0 : cosRotation * positions[offset] + sinRotation * positions[offset + 1];
  const originY = center < 0 ? 0 : -sinRotation * positions[offset] + cosRotation * positions[offset + 1];
  const scale = canvas.width / (2 * halfWidth);

  if (clear || !view.trails || view.original) {
    context.fillStyle = "#000";
    context.fillRect(0, 0, canvas.width, canvas.height);
  }
  let visibleDust = 0;
  for (let index = positions.length / 3 - 1; index >= 0; index--) {
    if (hidden[index]) continue;
    const body = bodies[index];
    const x = positions[index * 3];
    const y = positions[index * 3 + 1];
    const z = positions[index * 3 + 2];
    const projectedX = cosRotation * x + sinRotation * y;
    const projectedY = cosInclination * (-sinRotation * x + cosRotation * y) + sinInclination * z;
    const pixelX = canvas.width / 2 + (projectedX - originX) * scale;
    const pixelY = canvas.height / 2 - (projectedY - originY) * scale;
    if (!body) visibleDust++;
    if (pixelX < -10 || pixelX > canvas.width + 10 || pixelY < -10 || pixelY > canvas.height + 10) continue;

    const radius = body ? Math.max(3.2, body[1] * scale) : (view.trails ? 0.75 : 1.45);
    context.fillStyle = body?.[2] ?? dustColor(x, y, z);
    context.beginPath();
    context.arc(pixelX, pixelY, radius, 0, 2 * Math.PI);
    context.fill();
  }
  return { width: halfWidth * 2, visibleDust };
}

/** @param {Frame} frame */
function unpack(frame) {
  const bytes = frame[2];
  // Python encodes little-endian floats and returns one contiguous frame.
  return new Float64Array(bytes.buffer, bytes.byteOffset, bytes.byteLength / 8);
}

/** @param {Float64Array} positions @param {number} offset */
function relativeRadius(positions, offset) {
  return Math.hypot(positions[offset] - positions[0], positions[offset + 1] - positions[1], positions[offset + 2] - positions[2]);
}

/** @param {number} x @param {number} y @param {number} z */
function dustColor(x, y, z) {
  const t = Math.max(-1, Math.min(1, 10 * z / (Math.hypot(x, y, z) || 1)));
  const red = Math.round(255 * (1 + Math.min(t, 0)));
  const green = Math.round(255 * (1 - Math.abs(t) / 2));
  const blue = Math.round(255 * (1 - Math.max(t, 0)));
  return `rgb(${red} ${green} ${blue})`;
}
