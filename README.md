# Dust Disk Simulator

A Java-based N-body gravitational simulator for studying protoplanetary dust disk dynamics. This simulation models dust particle behavior around stars and planetary systems.

## Overview

This simulation models:
- Protoplanetary dust disk dynamics
- Solar system with dust particles
- Face-on and edge-on viewing orientations
- Particle accretion onto larger bodies
- Multiple numerical integrators (Midpoint/Leapfrog, Hermite)

## Running the Application

### Prerequisites

- Java 11 or later (tested with Java 25)

### Quick Start

```bash
make run
# Or build macOS app
make app
open dist/DustDiskSimulator.app
```

### Usage

1. Select a scenario: **Dusty Solar System** or **Dust Disk Face-On**
2. Adjust parameters: dt, warp, n (number of bodies), orientation angle
3. Click **Start** to begin
4. Use **+/-** to zoom, **Accrete** to enable particle merging

## Project Structure

```
dustdisk-sim/
├── astro/galaxy/
│   ├── DustDisk.java
│   ├── DustDiskCanvas.java
│   ├── Body3d.java
│   └── Data3d.java
├── myutil/
├── scripts/
├── Makefile
└── README.md
```

## License

Educational/Research use.
