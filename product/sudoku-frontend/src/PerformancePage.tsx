// Page for benchmarking algorithms and displaying average performance metrics
import { Button } from "@/components/ui/button";
import { getDifficulties, calcAvg, uploadPuzzles } from "@/api/sudokuApi";
import type { CalcAvgResponse, Difficulty } from "@/types/sudoku";
import { useState, useEffect } from "react";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import { Bar, BarChart, CartesianGrid, XAxis, YAxis } from "recharts";
import { ChartContainer } from "@/components/ui/chart";

function PerformancePage() {
  const [difficulties, setDifficulties] = useState<Difficulty[]>([]);
  const [difficulty, setDifficulty] = useState<Difficulty | "RANDOM" | "">("");

  const [puzzleCount, setPuzzleCount] = useState<number>(15);
  const [results, setResults] = useState<CalcAvgResponse | null>(null);
  const [file, setFile] = useState<File | null>(null);
  const [fileInputKey, setFileInputKey] = useState<number>(0);

  const [loading, setLoading] = useState<boolean>(false);

  const chartData = getChartData();
  const topThreeData = getTopThreeChartData();

  // Fetch difficulty options on first render
  useEffect(() => {
    async function fetchDifficulties() {
      const result = await getDifficulties();
      setDifficulties(result);
    }
    fetchDifficulties();
  }, []);

  // Runs calculation using uploaded file if present, otherwise generates puzzles
  async function runCalculation() {
    setLoading(true);

    let result;
    if (file) {
      result = await uploadPuzzles(file);
    } else if (difficulty === "RANDOM" || difficulty === "") {
      result = await calcAvg(puzzleCount, undefined);
    } else {
      result = await calcAvg(puzzleCount, difficulty);
    }

    if (!result) {
      alert("Failed to calculate. Check your file is formatted correctly.");
      setFile(null);
      setFileInputKey((prev) => prev + 1);
      setLoading(false);
      return;
    }
    setResults(result);
    setLoading(false);
  }

  // Validates uploaded file is a .txt before accepting
  function handleFile(e: React.ChangeEvent<HTMLInputElement>) {
    if (e.target.files && e.target.files[0]) {
      const selected = e.target.files[0];
      if (!selected.name.endsWith(".txt")) {
        alert("Please upload a .txt file");
        e.target.value = "";
        return;
      }
      setFile(selected);
    }
  }
  // Maps results into charts for all algorithms
  function getChartData() {
    if (!results) {
      return [];
    }
    const data = Object.keys(results.avgBacktracks).map((name) => ({
      name,
      backtracks: results.avgBacktracks[name],
      nodesVisited: results.avgNodesVisited[name],
      time: results.avgTime[name],
    }));
    return data;
  }

  // Returns chart data for the three algorithms with the fewest backtracks
  function getTopThreeChartData() {
    if (!results) {
      return [];
    }
    const data = Object.keys(results.avgBacktracks).map((name) => ({
      name,
      backtracks: results.avgBacktracks[name],
      nodesVisited: results.avgNodesVisited[name],
      time: results.avgTime[name],
    }));

    data.sort((a, b) => a.backtracks - b.backtracks); //sorts algorithm in ascending order of backtracks
    const topThree = [];
    for (let i = 0; i < 3 && i < data.length; i++) {
      topThree.push(data[i]);
    }
    return topThree;
  }

  return (
    <>
      {/* Whole screen */}
      <div className="mx-auto flex w-full max-w-7xl flex-col gap-6 p-6">
        {/* Title and headings */}
        <header className="space-y-2">
          <h1 className="text-3xl font-semibold tracking-tight">
            Comparison of Algorithms
          </h1>
          <p className="text-sm text-muted-foreground">
            Run a selected amount of puzzles and compare the averages between
            different algorithms.
          </p>
        </header>

        {/* Drop downs and buttons */}
        <div className="flex flex-row gap-6">
          {/* Difficulty drop down */}
          <Select
            value={difficulty}
            onValueChange={(val) => setDifficulty(val as Difficulty | "RANDOM")}
          >
            <SelectTrigger className="w-60">
              <SelectValue placeholder="Select difficulty" />
            </SelectTrigger>
            <SelectContent side="bottom">
              <SelectItem value="RANDOM">RANDOM</SelectItem>
              {difficulties.map((diff) => (
                <SelectItem key={diff} value={diff}>
                  {diff}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>
          <input
            type="number"
            min={1}
            max={100}
            value={puzzleCount}
            onChange={(e) => setPuzzleCount(Number(e.target.value))}
            onBlur={() =>
              setPuzzleCount(Math.min(100, Math.max(1, puzzleCount)))
            }
            className="w-20 rounded-md border px-3 py-2 text-sm"
            placeholder="Count"
          />

          {/* Calculate averages button.
          It is disabled if a difficulty isn't selected */}
          <Button
            onClick={runCalculation}
            disabled={(!difficulty && !file) || loading}
          >
            {loading ? "Calculating..." : "Calculate Averages"}
          </Button>
          <input
            key={fileInputKey}
            type="file"
            accept=".txt"
            onChange={handleFile}
            className="text-sm"
          />
        </div>

        <div className="rounded-lg border p-4 space-y-3">
          <div className="text-sm text-muted-foreground ">
            Backtracking: Tries each possible value and backtracks when a
            conflict is found. <br />
            AC-3: Runs arc consistency once at the start to prune domains, then
            hands off to basic backtracking. <br />
            MRV: Always picks the empty cell with the fewest possible values
            during search, reducing wrong guesses. <br />
            AC-3 + MRV: Runs AC-3 once before search and then passes to MRV cell
            selection during backtracking. <br />
            MAC: Runs AC-3 after every assignment during backtracking to detect
            impossible values early.
            <br />
            MAC + MRV: Combines MAC with MRV's smart cell selection.
          </div>
        </div>

        {results && ( //only render if results exist
          <>
            <h2 className="text-xl font-semibold">Table of Averages</h2>
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Algorithm</TableHead>
                  <TableHead>Average Backtracks</TableHead>
                  <TableHead>Average Nodes Visited</TableHead>
                  <TableHead>Average Time (ms)</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {Object.keys(results.avgBacktracks).map((name) => (
                  <TableRow key={name}>
                    <TableCell>{name}</TableCell>
                    <TableCell>
                      {results.avgBacktracks[name].toFixed(2)}
                    </TableCell>
                    <TableCell>
                      {results.avgNodesVisited[name].toFixed(2)}
                    </TableCell>
                    <TableCell>{results.avgTime[name].toFixed(2)}</TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>

            <h2 className="text-xl font-semibold">Top 3 Algorithms</h2>
            <p className="text-sm text-muted-foreground">
              The three algorithms with the fewest average backtracks.
            </p>

            <div className="flex flex-row gap-6 mt-10">
              {/* Top 3 algorithms */}
              <div className="w-full">
                <p className="text-sm font-medium text-center mb-2">
                  Average Backtracks
                </p>
                <ChartContainer
                  config={{
                    backtracks: { label: "Backtracks" },
                  }}
                  className="aspect-auto h-[250px] w-full"
                >
                  <BarChart
                    accessibilityLayer
                    data={topThreeData}
                    margin={{
                      left: 20,
                      right: 12,
                      bottom: 30,
                    }}
                  >
                    <CartesianGrid vertical={false} />
                    <XAxis
                      dataKey="name"
                      label={{
                        value: "Algorithm",
                        position: "insideBottom",
                        offset: -5,
                      }}
                    />
                    <YAxis
                      label={{
                        value: "Backtracks",
                        angle: -90,
                        position: "insideLeft",
                      }}
                    />
                    <XAxis dataKey="name" />
                    <YAxis />
                    <Bar dataKey="backtracks" fill="#ff964b" />
                  </BarChart>
                </ChartContainer>
              </div>
              <div className="w-full">
                <p className="text-sm font-medium text-center mb-2">
                  Average Nodes Visited
                </p>
                <ChartContainer
                  config={{
                    nodesVisited: { label: "Nodes Visited" },
                  }}
                  className="aspect-auto h-[250px] w-full"
                >
                  <BarChart
                    accessibilityLayer
                    data={topThreeData}
                    margin={{
                      left: 20,
                      right: 12,
                      bottom: 30,
                    }}
                  >
                    <CartesianGrid vertical={false} />
                    <XAxis
                      dataKey="name"
                      label={{
                        value: "Algorithm",
                        position: "insideBottom",
                        offset: -5,
                      }}
                    />
                    <YAxis
                      label={{
                        value: "Nodes Visited",
                        angle: -90,
                        position: "insideLeft",
                      }}
                    />
                    <XAxis dataKey="name" />
                    <YAxis />
                    <Bar dataKey="nodesVisited" fill="#8bc9ff" />
                  </BarChart>
                </ChartContainer>
              </div>
            </div>
            <h2 className="text-xl font-semibold">All Algorithms</h2>
            <p className="text-sm text-muted-foreground">
              Comparison across all six algorithms.
            </p>
            <div className="flex flex-row gap-6 mt-10">
              <div className="w-full">
                <p className="text-sm font-medium text-center mb-2">
                  Average Backtracks
                </p>
                <ChartContainer
                  config={{
                    backtracks: { label: "Backtracks" },
                  }}
                  className="aspect-auto h-[250px] w-full"
                >
                  <BarChart
                    accessibilityLayer
                    data={chartData}
                    margin={{
                      left: 20,
                      right: 12,
                      bottom: 30,
                    }}
                  >
                    <CartesianGrid vertical={false} />
                    <XAxis
                      dataKey="name"
                      label={{
                        value: "Algorithm",
                        position: "insideBottom",
                        offset: -5,
                      }}
                    />
                    <YAxis
                      label={{
                        value: "Backtracks",
                        angle: -90,
                        position: "insideLeft",
                      }}
                    />
                    <XAxis dataKey="name" />
                    <YAxis />ß
                    <Bar dataKey="backtracks" fill="#ffaacd" />
                  </BarChart>
                </ChartContainer>
              </div>
              <div className="w-full">
                <p className="text-sm font-medium text-center mb-2">
                  Average Nodes Visited
                </p>
                <ChartContainer
                  config={{
                    nodesVisited: { label: "Nodes Visited" },
                  }}
                  className="aspect-auto h-[250px] w-full"
                >
                  <BarChart
                    accessibilityLayer
                    data={chartData}
                    margin={{
                      left: 20,
                      right: 12,
                      bottom: 30,
                    }}
                  >
                    <CartesianGrid vertical={false} />
                    <XAxis
                      dataKey="name"
                      label={{
                        value: "Algorithm",
                        position: "insideBottom",
                        offset: -5,
                      }}
                    />
                    <YAxis
                      label={{
                        value: "Nodes Visited",
                        angle: -90,
                        position: "insideLeft",
                      }}
                    />
                    <XAxis dataKey="name" />
                    <YAxis />
                    <Bar dataKey="nodesVisited" fill="#ffd3f5" />
                  </BarChart>
                </ChartContainer>
              </div>
            </div>
          </>
        )}
      </div>
    </>
  );
}
export default PerformancePage;
