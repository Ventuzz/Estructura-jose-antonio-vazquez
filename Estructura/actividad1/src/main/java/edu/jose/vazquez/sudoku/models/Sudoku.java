package edu.jose.vazquez.sudoku.models;

import java.util.*;

public class Sudoku {
    public static final int SIZE = 9;
    private final int[][] board;

    public Sudoku() {
        this.board = new int[SIZE][SIZE];
    }

    public Sudoku(int[][] grid) {
        this.board = new int[SIZE][SIZE];
        for (int r = 0; r < SIZE; r++) {
            System.arraycopy(grid[r], 0, this.board[r], 0, SIZE);
        }
    }

    public int[][] getBoard() {
        int[][] copy = new int[SIZE][SIZE];
        for (int r = 0; r < SIZE; r++) {
            System.arraycopy(board[r], 0, copy[r], 0, SIZE);
        }
        return copy;
    }

    public void setCell(int r, int c, int val) {
        board[r][c] = val;
    }

    public int getCell(int r, int c) {
        return board[r][c];
    }

    /* =================== Impresión =================== */
    public void print() {
        for (int r = 0; r < SIZE; r++) {
            if (r % 3 == 0) System.out.println("+-------+-------+-------+");
            for (int c = 0; c < SIZE; c++) {
                if (c % 3 == 0) System.out.print("| ");
                int v = board[r][c];
                System.out.print(v == 0 ? ". " : (v + " "));
            }
            System.out.println("|");
        }
        System.out.println("+-------+-------+-------+");
    }

    /* =================== Solver (Backtracking) =================== */
    public boolean solve() {
        int[] empty = findEmpty();
        if (empty == null) return true; // no hay vacíos
        int r = empty[0], c = empty[1];

        for (int num = 1; num <= 9; num++) {
            if (isSafe(r, c, num)) {
                board[r][c] = num;
                if (solve()) return true;
                board[r][c] = 0;
            }
        }
        return false;
    }

    private int[] findEmpty() {
        for (int r = 0; r < SIZE; r++)
            for (int c = 0; c < SIZE; c++)
                if (board[r][c] == 0) return new int[]{r, c};
        return null;
    }

    private boolean isSafe(int r, int c, int num) {
        return !usedInRow(r, num) && !usedInCol(c, num) && !usedInBox(r - r % 3, c - c % 3, num);
    }

    private boolean usedInRow(int r, int num) {
        for (int c = 0; c < SIZE; c++) if (board[r][c] == num) return true;
        return false;
    }

    private boolean usedInCol(int c, int num) {
        for (int r = 0; r < SIZE; r++) if (board[r][c] == num) return true;
        return false;
    }

    private boolean usedInBox(int boxRow, int boxCol, int num) {
        for (int r = 0; r < 3; r++)
            for (int c = 0; c < 3; c++)
                if (board[boxRow + r][boxCol + c] == num) return true;
        return false;
    }

    /* =================== Generador con solución única =================== */
    public static Sudoku generate(int clues, long seed) {
        if (clues < 17 || clues > 81) {
            throw new IllegalArgumentException("clues debe estar entre 17 y 81 (recomendado 22-40).");
        }
        Random rng = new Random(seed);

        // 1) Construye una solución completa:
        Sudoku full = new Sudoku();
        fillDiagonalBoxes(full, rng);
        // Completa el resto con solver
        if (!full.solve()) throw new IllegalStateException("No se pudo construir una solución base.");

        // 2) Crea el rompecabezas removiendo celdas y verificando unicidad:
        Sudoku puzzle = new Sudoku(full.getBoard());
        int filled = 81;
        // intentos para evitar bucles largos si se vuelve difícil mantener unicidad
        int attempts = 10000;

        while (filled > clues && attempts-- > 0) {
            int r = rng.nextInt(9);
            int c = rng.nextInt(9);
            if (puzzle.getCell(r, c) == 0) continue;

            int backup = puzzle.getCell(r, c);
            puzzle.setCell(r, c, 0);

            if (!hasUniqueSolution(puzzle, 2)) {
                // no única -> revertimos
                puzzle.setCell(r, c, backup);
            } else {
                filled--;
            }
        }
        return puzzle;
    }

    public static Sudoku generate(int clues) {
        return generate(clues, System.nanoTime());
    }

    private static void fillDiagonalBoxes(Sudoku s, Random rng) {
        for (int k = 0; k < 9; k += 3) fillBox(s, k, k, rng);
    }

    private static void fillBox(Sudoku s, int row, int col, Random rng) {
        List<Integer> nums = new ArrayList<>();
        for (int i = 1; i <= 9; i++) nums.add(i);
        Collections.shuffle(nums, rng);

        int idx = 0;
        for (int r = 0; r < 3; r++)
            for (int c = 0; c < 3; c++)
                s.setCell(row + r, col + c, nums.get(idx++));
    }

    /* =================== Verificación de unicidad =================== */
    private static boolean hasUniqueSolution(Sudoku puzzle, int limit) {
        int[][] grid = puzzle.getBoard();
        return countSolutions(grid, limit) == 1;
    }

    private static int countSolutions(int[][] grid, int limit) {
        // Backtracking que cuenta hasta 'limit' soluciones
        int[] pos = findEmptyIn(grid);
        if (pos == null) return 1;

        int r = pos[0], c = pos[1];
        int count = 0;
        for (int num = 1; num <= 9; num++) {
            if (isSafeIn(grid, r, c, num)) {
                grid[r][c] = num;
                count += countSolutions(grid, limit - count);
                grid[r][c] = 0;
                if (count >= limit) return count; // ya no necesitamos más
            }
        }
        return count;
    }

    private static int[] findEmptyIn(int[][] grid) {
        for (int r = 0; r < SIZE; r++)
            for (int c = 0; c < SIZE; c++)
                if (grid[r][c] == 0) return new int[]{r, c};
        return null;
    }

    private static boolean isSafeIn(int[][] grid, int r, int c, int num) {
        for (int i = 0; i < SIZE; i++) {
            if (grid[r][i] == num || grid[i][c] == num) return false;
        }
        int br = r - r % 3, bc = c - c % 3;
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                if (grid[br + i][bc + j] == num) return false;
        return true;
    }
}

