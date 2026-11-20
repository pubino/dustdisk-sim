import csv
import io
import subprocess
import tempfile
import unittest
from collections import defaultdict
from pathlib import Path

import numpy as np

from web import simulation

ROOT = Path(__file__).resolve().parents[1]


class SimulationTest(unittest.TestCase):
    def test_matches_original_java_solver(self) -> None:
        with tempfile.TemporaryDirectory() as classes:
            sources = sorted((ROOT / "astro/galaxy").glob("*.java"))
            sources += sorted((ROOT / "myutil").glob("*.java"))
            subprocess.run(
                [
                    "javac",
                    "--release",
                    "25",
                    "-d",
                    classes,
                    *sources,
                    ROOT / "tests/JavaReference.java",
                ],
                check=True,
            )
            output = subprocess.check_output(
                [
                    "java",
                    "-Djava.awt.headless=true",
                    "--add-opens",
                    "java.base/java.lang=ALL-UNNAMED",
                    "-cp",
                    classes,
                    "astro.galaxy.JavaReference",
                ],
                text=True,
                timeout=30,
            )
        cases: dict[tuple[str, str, str, int], list[list[str]]] = defaultdict(list)
        for tag, *row in csv.reader(io.StringIO(output)):
            if tag == "REFERENCE":
                scenario, method, collision, step_text, *body = row
                cases[scenario, method, collision, int(step_text)].append(body)
        self.assertEqual(len(cases), 28)

        for scenario, method, collision, step in cases:
            with self.subTest(
                scenario=scenario, method=method, collision=collision, step=step
            ):
                records = cases[scenario, method, collision, step]
                scenario_id = "disk" if scenario == "Dust Disk Face-On" else "solar"
                integrator = "leapfrog" if method == "2" else "hermite"
                bodies = simulation.initial_conditions(scenario_id, len(records), 524)
                if collision == "true":
                    bodies.positions[-1] = bodies.positions[0] + [0.004, 0.0, 0.0]
                    bodies.velocities[-1] = bodies.velocities[0] + [0.0, 0.1, 0.0]
                state = simulation.Simulation(bodies, integrator)
                dt = 0.00001 if collision == "true" else 0.01
                for start in range(0, step, 100):
                    state.advance(min(100, step - start), dt, True)
                expected = np.array([row[5:] for row in records], dtype=float)
                actual = np.column_stack(
                    (state.positions, state.velocities, state.acceleration, state.jerk)
                )
                # Close encounters amplify roundoff over hundreds of steps.
                tolerance = 2e-9 if step == 250 else 3e-12
                np.testing.assert_allclose(
                    actual, expected, rtol=tolerance, atol=tolerance
                )
                np.testing.assert_allclose(
                    bodies.masses, [float(row[2]) for row in records]
                )
                np.testing.assert_allclose(
                    bodies.radii, [float(row[3]) for row in records]
                )
                np.testing.assert_array_equal(
                    state.hidden, [row[4] == "true" for row in records]
                )
                self.assertEqual(state.elapsed, float(records[0][1]))

    def test_rejects_invalid_inputs(self) -> None:
        with self.assertRaises(ValueError):
            simulation.initial_conditions("unknown", 20, 524)
        with self.assertRaises(ValueError):
            simulation.initial_conditions("solar", 13, 524)
        state = simulation.Simulation(
            simulation.initial_conditions("disk", 20, 524), "leapfrog"
        )
        for steps, dt in [(1, float("nan")), (1, 0.0), (101, 0.01)]:
            with self.subTest(steps=steps, dt=dt), self.assertRaises(ValueError):
                state.advance(steps, dt, False)

    def test_massless_particles_do_not_change_energy_or_massive_orbits(self) -> None:
        states = [
            simulation.Simulation(
                simulation.initial_conditions("disk", count, 524), "hermite"
            )
            for count in (2, 50)
        ]
        for state in states:
            state.advance(30, 0.01, True)
        np.testing.assert_array_equal(states[0].positions, states[1].positions[:2])
        self.assertEqual(states[0].energy(), states[1].energy())
