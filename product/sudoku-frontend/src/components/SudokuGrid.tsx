// Reusable component for rendering a 9x9 Sudoku grid.

import type { Grid } from "@/types/sudoku";

type SudokuGridProps = {
  grid: Grid;
  editable?: boolean; // cells become inputs
  onCellChange?: (row: number, col: number, value: number) => void;
  original?: Grid; // when given, cells that differ from it are shown in blue
};

export function SudokuGrid({
  grid,
  editable = false,
  onCellChange,
  original,
}: SudokuGridProps) {
  const cells = Array(81).fill(0);

  return (
    <div className="w-full max-w-full rounded-lg bg-white p-2 md:w-[540px]">
      <div className="w-full border-1 border-gray-400">
        <div className="grid aspect-square w-full grid-cols-9 gap-0">
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
              "aspect-square flex items-center justify-center text-lg sm:text-2xl",
              thickTop ? "border-t-2" : "border-t",
              thickLeft ? "border-l-2" : "border-l",
              thickBottom ? "border-b-2" : "border-b",
              thickRight ? "border-r-2" : "border-r",
              "border-gray-400",
            ].join(" ");

            return (
              <div key={index} className={borderClasses}>
                {editable ? (
                  <input
                    inputMode="numeric"
                    aria-label={`Row ${row + 1}, column ${col + 1}`}
                    value={value === 0 ? "" : value}
                    onFocus={(e) => e.target.select()}
                    onChange={(e) => {
                      // keep only the last character typed
                      const typed = e.target.value.slice(-1);
                      if (typed === "") {
                        onCellChange?.(row, col, 0);
                      } else if (/[1-9]/.test(typed)) {
                        onCellChange?.(row, col, Number(typed));
                      }
                    }}
                    className={[
                      "h-full w-full bg-transparent text-center outline-none focus:bg-blue-100",
                      original && value !== original[row][col]
                        ? "text-blue-600"
                        : "",
                    ].join(" ")}
                  />
                ) : value === 0 ? (
                  ""
                ) : (
                  value
                )}
              </div>
            );
          })}
        </div>
      </div>
    </div>
  );
}
