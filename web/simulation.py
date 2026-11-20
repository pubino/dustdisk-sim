"""DustDisk.java, Data3d.java and DustDiskCanvas.java numerical model.

Positions are in AU, velocities in AU/year, and masses in Earth masses.
"""

from dataclasses import dataclass
from math import atan2, cos, isfinite, radians, sin, sqrt, tau

import numpy as np
from numpy.typing import NDArray

G = 1.183804e-4
MAX_BODIES = 4000
Vector = tuple[float, float, float]
Ephemeris = tuple[float, float, float, float, float, float]
Floats = NDArray[np.float64]
Mask = NDArray[np.bool_]


@dataclass(frozen=True)
class MassiveBody:
    name: str
    mass: float
    radius: float
    color: str
    ephemeris: Ephemeris | None = None


SOLAR_EPHEMERIS = (
    MassiveBody(
        "Sun",
        333000,
        0.0046,
        "#ffff4d",
        (334.36660, -10.62249, 141.02, 54.21, 0.988971838437219, 0.3689321),
    ),
    # Mercury's zero collision radius is the result of integer division in Data3d.java.
    MassiveBody(
        "Mercury",
        3.3022e23 / 59.72e23,
        0,
        "#ff8080",
        (340.09041, -10.09606, 254.73, 118.50, 1.33489932319922, -19.0427712),
    ),
    MassiveBody(
        "Venus",
        0.817,
        0.0000413,
        "#00ff00",
        (325.64052, -14.99464, 178.22, 58.39, 1.68468287895148, 3.1649484),
    ),
    MassiveBody("Earth", 1, 0.00004301, "#647cff"),
    MassiveBody(
        "Mars",
        0.108,
        0.0000227,
        "#ff3939",
        (280.94399, -23.54146, 106.65, 5.95, 1.88809615264125, -12.8809197),
    ),
    MassiveBody(
        "Jupiter",
        1898.6e24 / 5.972e24,
        69911 / 149668992,
        "#ffffff",
        (197.38656, -5.75284, -7.93, 3.86, 4.72068178258518, -20.1840422),
    ),
    MassiveBody(
        "Saturn",
        5.68461e26 / 5.972e24,
        60268 / 149668992,
        "#ffff4d",
        (112.80648, 21.86089, -7.49, 1.42, 8.29690048890332, 19.0503238),
    ),
    MassiveBody(
        "Uranus",
        86.832e24 / 5.972e24,
        25559 / 149668992,
        "#ffff99",
        (338.47273, -9.82498, 7.99, 3.19, 21.0470705432051, 2.5567636),
    ),
    MassiveBody(
        "Neptune",
        102.43e24 / 5.972e24,
        24624 / 149668992,
        "#99ffff",
        (318.14819, -16.23793, 5.26, 1.60, 31.0107494216548, -8.3027453),
    ),
    MassiveBody(
        "Pluto",
        1.314e22 / 5.972e24,
        870 / 149668992,
        "#99ff99",
        (263.97486, -15.20032, 2.74, 0.26, 31.2623582636109, -26.6429753),
    ),
    MassiveBody(
        "Comet Halley",
        2.2e14 / 5.972e24,
        16 / 149668992,
        "#ffffff",
        (129.85464, 0.12027, -4.78, 1.90, 28.5415895526607, 12.6144319),
    ),
    MassiveBody(
        "Eris",
        1.67e22 / 5.972e24,
        1300 / 149668992,
        "#ff6666",
        (23.79895, -5.68257, 0.89, 0.75, 97.5764534858654, 21.4755946),
    ),
    MassiveBody(
        "Sedna",
        1e21 / 5.972e24,
        1400 / 149668992,
        "#66ff99",
        (48.85224, 5.80750, 0.50, 0.48, 89.4067339131441, 27.0225597),
    ),
    MassiveBody(
        "Moon",
        7.347e22 / 5.972e24,
        1737 / 149668992,
        "#ffff99",
        (120.12896, 25.74265, 1738.94, -364.81, 0.002710808329734, -0.0084885),
    ),
)
SUN_JUPITER = (SOLAR_EPHEMERIS[0], SOLAR_EPHEMERIS[5])


@dataclass(frozen=True)
class BodySet:
    massive: tuple[MassiveBody, ...]
    masses: Floats
    radii: Floats
    positions: Floats
    velocities: Floats


def _ecliptic(ephemeris: Ephemeris) -> tuple[Vector, Vector]:
    ra, dec, rate_ra, rate_dec, distance, radial_rate = ephemeris
    ra, dec = ra * tau / 360, dec * tau / 360
    rate_ra = rate_ra * (tau / (360 * 3600)) * (24 * 365.25)
    rate_dec = rate_dec * (tau / (360 * 3600)) * (24 * 365.25)
    radial_rate = radial_rate * 3600 * 24 * 365.25 / 149597870.691
    cos_ra, sin_ra, cos_dec, sin_dec = cos(ra), sin(ra), cos(dec), sin(dec)
    tilt = 23.5 * tau / 360

    def rotate(x: float, y: float, z: float) -> Vector:
        return x, y * cos(tilt) + z * sin(tilt), -y * sin(tilt) + z * cos(tilt)

    return (
        rotate(
            distance * cos_dec * cos_ra, distance * cos_dec * sin_ra, distance * sin_dec
        ),
        rotate(
            radial_rate * cos_dec * cos_ra
            - distance * sin_dec * cos_ra * rate_dec
            - distance * sin_ra * rate_ra,
            radial_rate * cos_dec * sin_ra
            - distance * sin_dec * sin_ra * rate_dec
            + distance * cos_ra * rate_ra,
            radial_rate * sin_dec + distance * cos_dec * rate_dec,
        ),
    )


def _incline(x: float, y: float, node: float, tilt: float) -> Vector:
    x_node = cos(node) * x + sin(node) * y
    y_node = -sin(node) * x + cos(node) * y
    x_tilt = cos(tilt) * x_node
    return (
        cos(node) * x_tilt - sin(node) * y_node,
        sin(node) * x_tilt + cos(node) * y_node,
        -sin(tilt) * x_node,
    )


class _JavaRandom:
    """Java's 48-bit generator, used to reproduce the dust distribution from a seed."""

    def __init__(self, seed: int) -> None:
        self.state = seed ^ 0x5DEECE66D

    def _bits(self, count: int) -> int:
        self.state = (self.state * 0x5DEECE66D + 11) & ((1 << 48) - 1)
        return self.state >> (48 - count)

    def random(self) -> float:
        return ((self._bits(26) << 27) + self._bits(27)) / (1 << 53)


def _solar_dust_location(rng: _JavaRandom) -> tuple[float, float]:
    while True:
        x, y = 40 * (rng.random() - 0.5), 40 * (rng.random() - 0.5)
        radius_squared = x * x + y * y
        if 0.2**2 < radius_squared < 20**2:
            return sqrt(radius_squared), atan2(y, x)


def initial_conditions(scenario: str, count: int, seed: int) -> BodySet:
    if scenario not in ("solar", "disk"):
        raise ValueError("Choose a valid scenario.")
    solar = scenario == "solar"
    massive = SOLAR_EPHEMERIS if solar else SUN_JUPITER
    if type(count) is not int or not len(massive) <= count <= MAX_BODIES:
        raise ValueError(f"Use {len(massive)} to {MAX_BODIES} bodies.")
    if type(seed) is not int or not 0 <= seed <= 2**32 - 1:
        raise ValueError("The seed must be an unsigned 32-bit integer.")

    positions = np.zeros((count, 3))
    velocities = np.zeros_like(positions)
    masses = np.zeros(count)
    radii = np.zeros(count)
    for index, body in enumerate(massive):
        masses[index], radii[index] = body.mass, body.radius
        if solar and body.ephemeris is not None:
            positions[index], velocities[index] = _ecliptic(body.ephemeris)

    # The solar dust uses Mercury (index 1) in this ratio, as in Data3d.java.
    radius = 5
    primary = masses[0] / (masses[0] + masses[1])
    secondary = masses[1] / (masses[0] + masses[1])
    omega = sqrt(G * masses[0] / (primary * radius**3))
    if not solar:
        positions[:2, 0] = [-secondary * radius, primary * radius]
        velocities[:2, 1] = [-omega * secondary * radius, omega * primary * radius]

    rng = _JavaRandom(seed)
    for index in range(len(massive), count):
        if solar:
            dust_radius, angle = _solar_dust_location(rng)
        else:
            angle = tau * index / count
        node = tau * rng.random()
        tilt = radians(10) * (rng.random() - 0.5)
        if not solar:
            dust_radius = radius * (1 + (rng.random() - 0.5) / 2)
        speed = omega * sqrt(radius**3 / dust_radius**3) * primary * dust_radius
        positions[index] = _incline(
            primary * dust_radius * cos(angle),
            primary * dust_radius * sin(angle),
            node,
            tilt,
        )
        velocities[index] = _incline(
            -speed * sin(angle), speed * cos(angle), node, tilt
        )
        if solar:
            positions[index] += positions[0]
            velocities[index] += velocities[0]

    velocities -= np.sum(masses[:, None] * velocities, axis=0) / np.sum(masses)
    return BodySet(massive, masses, radii, positions, velocities)


class Simulation:
    def __init__(self, bodies: BodySet, integrator: str) -> None:
        if integrator not in ("leapfrog", "hermite"):
            raise ValueError("Choose a valid integrator.")
        self.bodies = bodies
        self.integrator = integrator
        self.positions = bodies.positions.copy()
        self.velocities = bodies.velocities.copy()
        self.hidden = np.zeros(len(self.positions), dtype=np.bool_)
        self.elapsed = 0.0
        combined_radii = bodies.radii + bodies.radii[: len(bodies.massive), None]
        self.collision_squared = combined_radii * combined_radii
        self.collision_cubed = self.collision_squared * combined_radii
        self.acceleration, self.jerk, _ = self._forces(
            self.positions, self.velocities, initial=True
        )

    def _forces(
        self, positions: Floats, velocities: Floats, *, initial: bool = False
    ) -> tuple[Floats, Floats, Mask]:
        acceleration = np.zeros_like(positions)
        jerk = np.zeros_like(positions)
        collision = np.zeros(len(positions), dtype=np.bool_)
        for source, body in enumerate(self.bodies.massive):
            delta = positions[source] - positions
            distance_squared = np.sum(delta * delta, axis=1)
            distance_squared[source] = np.inf
            radius_squared = self.collision_squared[source]
            strength = G * body.mass
            if self.integrator == "hermite":
                inside = distance_squared < radius_squared
                effective_squared = np.maximum(distance_squared, radius_squared)
                denominator = effective_squared * np.sqrt(effective_squared)
                relative_velocity = velocities[source] - velocities
                radial_rate = (
                    np.sum(delta * relative_velocity, axis=1) / effective_squared
                )
                # The Java refresh applies the radial jerk term only inside the
                # collision radius. Its initialization applies the term everywhere.
                if not initial:
                    radial_rate *= inside
                derivative = relative_velocity - 3 * radial_rate[:, None] * delta
                jerk += strength * derivative / denominator[:, None]
            else:
                distance = np.sqrt(distance_squared)
                distance_cubed = distance * distance * distance
                collision_cubed = self.collision_cubed[source]
                collision |= distance_cubed < collision_cubed
                denominator = np.maximum(distance_cubed, collision_cubed)
            acceleration += strength * delta / denominator[:, None]
        return acceleration, jerk, collision

    def _leapfrog(self, dt: float, accrete: bool) -> None:
        self.velocities += self.acceleration * (dt / 2)
        self.positions += self.velocities * dt
        self.acceleration, self.jerk, collisions = self._forces(
            self.positions, self.velocities
        )
        self.velocities += self.acceleration * (dt / 2)
        if accrete:
            # Java colors colliding bodies black and continues integrating them.
            self.hidden |= collisions

    def _hermite(self, dt: float) -> None:
        predicted_positions = self.positions + (
            self.velocities * dt
            + self.acceleration * dt * dt / 2
            + self.jerk * dt * dt * dt / 6
        )
        predicted_velocities = self.velocities + (
            self.acceleration * dt + self.jerk * dt * dt / 2
        )
        acceleration, jerk, _ = self._forces(predicted_positions, predicted_velocities)
        velocities = (
            self.velocities
            + (self.acceleration + acceleration) * dt / 2
            + (self.jerk - jerk) * dt * dt / 12
        )
        self.positions = (
            self.positions
            + (self.velocities + velocities) * dt / 2
            + (self.acceleration - acceleration) * dt * dt / 12
        )
        self.velocities, self.acceleration, self.jerk = velocities, acceleration, jerk

    def advance(self, steps: int, dt: float, accrete: bool) -> None:
        if type(steps) is not int or not 0 <= steps <= 100:
            raise ValueError("Use at most 100 steps per frame.")
        if not isfinite(dt) or not 1e-7 <= dt <= 1:
            raise ValueError("Use a time step between 0.0000001 and 1 year.")
        with np.errstate(over="raise", invalid="raise", divide="raise"):
            for _ in range(steps):
                if self.integrator == "hermite":
                    self._hermite(dt)
                else:
                    self._leapfrog(dt, accrete)
                self.elapsed += dt

    def energy(self) -> float:
        """Original diagnostic, twice the conventional total energy."""
        count = len(self.bodies.massive)
        masses = self.bodies.masses[:count]
        positions = self.positions[:count]
        velocities = self.velocities[:count]
        left, right = np.triu_indices(count, k=1)
        distances = np.linalg.norm(positions[left] - positions[right], axis=1)
        kinetic = np.sum(masses * np.sum(velocities * velocities, axis=1))
        potential = -2 * G * np.sum(masses[left] * masses[right] / distances)
        return float(kinetic + potential)

    def metadata(self) -> list[tuple[str, float, str]]:
        return [(body.name, body.radius, body.color) for body in self.bodies.massive]

    def frame(self) -> tuple[float, float, bytes, bytes]:
        return (
            self.elapsed,
            self.energy(),
            self.positions.astype("<f8", copy=False).tobytes(),
            self.hidden.tobytes(),
        )


def create(scenario: str, count: int, integrator: str, seed: int) -> Simulation:
    return Simulation(initial_conditions(scenario, count, seed), integrator)
