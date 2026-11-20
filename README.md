# Dust Disk Simulator

[Open the browser simulator](https://pubino.github.io/dustdisk-sim/).

Robert Vanderbei’s Dust Disk Simulator models the motion of dust around the Sun and planets. It is part of [Vanderbei’s applications](https://pubino.github.io/vanderbei-apps#dustdisk-sim).

Choose **Dusty Solar System** to start from the February 2005 ephemeris or **Dust Disk Face-On** to explore a ring near Jupiter’s orbit. Select **Start** to advance the simulation. You can follow a body, change the inclination, zoom, show trails, or view the initial positions. The body count includes the massive bodies and the dust. Warp is the number of integration steps per displayed frame.

## Numerical model

The [Python solver](web/simulation.py) ports the two scenarios and the fixed-step Midpoint (Leapfrog) and Hermite integrators from the original Java application. It uses AU, years, and Earth masses. Dust particles respond to the massive bodies and have zero mass. In Leapfrog, **Accrete** hides particles when they enter a body’s collision radius. Their positions continue to be integrated, as in the Java version. The energy display uses the original diagnostic, twice the conventional total energy.

The browser uses a fixed seed with Java’s pseudorandom number generator so that Reset reproduces the initial dust distribution. It limits the body count to 4,000 and warp to 100 steps per frame. The site follows [StackImages](https://github.com/PrincetonUniversity/StackImages) in running Python and NumPy in a Web Worker with a pinned, self-hosted Pyodide runtime. GitHub Pages serves the static files.

## Local development

Run the preview at [localhost:8000](http://localhost:8000/).

```sh
docker-compose up --build preview
```

Run the same checks as GitHub Actions. The tests run the Java solver headlessly against the Python solver and exercise the built site in Chromium. The Pages workflow publishes `build/site/` after these checks pass on `main`.

```sh
docker-compose run --build --rm -T js-check
docker-compose run --build --rm -T test
```

To build and preview directly on macOS or Linux, use Python 3.13 with uv.

```sh
uv sync --locked
uv run python scripts/build_site.py
uv run python -m http.server 8000 --bind 127.0.0.1 --directory build/site
```

The original Java application remains available with a JDK. Run `make run`, or use `make app` to build the macOS app.
