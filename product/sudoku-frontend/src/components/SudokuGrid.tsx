// Reusable component for rendering a 9x9 Sudoku grid.

import type { Grid } from "@/types/sudoku";

export function SudokuGrid({ grid }: { grid: Grid }) {
  const cells = Array(81).fill(0);

  return (
    <div className="inline-block rounded-lg bg-border bg-white p-2">
      <div className="inline-block border-1 border-gray-400 ">
        <div className="grid grid-cols-9 gap-0">
          {cells.map((_, index) => {
            // Convert flat index to row and column
            const row = Math.floor(index / 9);
            const col = index % 9;
            const value = grid[row][col];

            // Thicker borders on 3x3 sub-grid edges
            const thickTop = row % 3 === 0;
            const thickLeft = col % 3 === 0;
            const thickBottom = row === 8;
            const thickRight = col === 8;

            const borderClasses = [
              "w-15 h-15 flex items-center justify-center",
              thickTop ? "border-t-2" : "border-t",
              thickLeft ? "border-l-2" : "border-l",
              thickBottom ? "border-b-2" : "border-b",
              thickRight ? "border-r-2" : "border-r",
              "border-gray-400",
            ].join(" ");

            return (
              <div key={index} className={borderClasses}>
                {value === 0 ? "" : value}
              </div>
            );
          })}
        </div>
      </div>
    </div>
  );
}
