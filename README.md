# Sudoku Solver: Comparing Constraint Satisfaction Algorithms

[![Java](https://img.shields.io/badge/Java-21-orange)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3-green)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-19-blue)](https://react.dev/)
[![TypeScript](https://img.shields.io/badge/TypeScript-5-blue)](https://www.typescriptlang.org/)

**Live app:** https://sudoku-frontend-1079750265164.europe-west2.run.app

A full-stack application for solving and benchmarking Sudoku puzzles using different constraint satisfaction and search techniques — plain backtracking, AC-3, MRV, MAC, and hybrids. The frontend lets you solve individual puzzles or run bulk benchmarks to compare algorithm performance visually.

![Algorithm comparison view](docs/screenshot.png)

## What this project is about

Sudoku is a Constraint Satisfaction Problem, which makes it a good testbed for comparing classical AI search techniques. Each algorithm is implemented behind a common `SudokuAlgorithm` interface (strategy pattern) so they can be swapped and benchmarked on the same puzzles under identical conditions. Metrics tracked: **backtracks**, **nodes visited**, and **runtime**.

## Results

Averaged over 50 generated puzzles per difficulty, plus 43 externally sourced hard puzzles.

| Algorithm       | Easy BT | Medium BT | Hard BT   | External BT  | Hard vs Baseline |
|-----------------|--------:|----------:|----------:|-------------:|-----------------:|
| Backtracking    |   21.42 |    353.62 |   124,202 |   ~7,300,000 |    baseline      |
| AC-3            |       0 |     ~43.5 |   101,721 |   ~7,282,557 |    −18.1%        |
| MRV             |       0 |      3.58 |    199.10 |    ~32,412   |    −99.84%       |
| AC-3 + MRV      |       0 |      3.58 |    199.10 |    ~32,412   |    −99.84%       |
| MAC             |       0 |      0.24 |    106.60 |    ~46,534   |    −99.91%       |
| **MAC + MRV**   |   **0** |  **0.84** | **63.26** | **~13,169**  |  **−99.95%**     |

*BT = average backtracks. Lower is better.*

**Key finding:** MAC + MRV was the strongest solver overall, reducing backtracks on hard puzzles by 99.95% vs the baseline. A single AC-3 preprocessing pass was effectively redundant once MRV was in place — AC-3 + MRV and MRV alone produced identical backtrack counts across every difficulty. The takeaway: constraint propagation only pays off when maintained throughout search (MAC), not just as preprocessing.

## Tech stack

Java 21 (solvers, JUnit, JaCoCo, Checkstyle, SpotBugs) · Spring Boot 3 REST API with Swagger · React 19 + TypeScript + Vite · GitHub Actions CI · Docker, deployed on Google Cloud Run.

## Architecture

```
┌─────────────────┐      HTTP       ┌──────────────────┐              ┌─────────────────┐
│  React frontend │ ──────────────▶ │  Spring Boot API │ ───────────▶ │  sudokuproject  │
│  (Vite + TS)    │  /api/sudoku    │                  │   depends    │  (core library) │
└─────────────────┘                 └──────────────────┘              └─────────────────┘
                                                                             │
                                                                             ▼
                                                              SudokuAlgorithm (strategy)
                                                              ├─ Backtracking
                                                              ├─ AC-3
                                                              ├─ MRV
                                                              ├─ AC-3 + MRV
                                                              ├─ MAC
                                                              └─ MAC + MRV
```

## Running it

**With Docker** (from `product/`):
```bash
docker compose up --build
```
Frontend at http://localhost:5173, API at http://localhost:8080 (Swagger at `/swagger-ui/index.html`).

**Without Docker** (Java 21, Maven, Node 20+):
```bash
cd product/sudokuproject && mvn clean install
cd ../sudoku-api && mvn spring-boot:run          # API on :8080
cd ../sudoku-frontend && npm install && npm run dev   # UI on :5173
```

**Using the app:** the Solver tab generates or uploads a puzzle (81 characters, digits `1-9`, `0` or `.` for empty) and solves it with a chosen algorithm. The Statistics tab benchmarks algorithms on generated puzzles or an uploaded file (one puzzle per line) and charts backtracks, nodes visited and runtime.

Deployment steps for Cloud Run are in [docs/deploy.md](docs/deploy.md).

## What's next

- Deploy the app to the cloud with a CD pipeline
- Add SAT-solver comparison as a baseline for the hardest known puzzles

---

Built as my final year project at Royal Holloway, University of London.
