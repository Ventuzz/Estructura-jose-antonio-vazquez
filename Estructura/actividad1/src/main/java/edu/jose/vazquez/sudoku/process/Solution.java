package edu.jose.vazquez.sudoku.process;
import edu.jose.vazquez.sudoku.models.Sudoku;

public class Solution {

    public boolean isValidSudoku(Sudoku sudoku) {
        int[][] board = sudoku.getBoard();
        // Check rows, columns, and 3x3 boxes
        for (int i = 0; i < 9; i++) {
            if (!isValidRow(board, i) || !isValidColumn(board, i) || !isValidBox(board, i)) {
                return false;
            }
        }
        return true;
    }

    private boolean isValidRow(int[][] board, int row) {
        boolean[] seen = new boolean[9];
        for (int col = 0; col < 9; col++) {
            int num = board[row][col];
            if (num < 1 || num > 9 || seen[num - 1]) {
                return false;
            }
            seen[num - 1] = true;
        }
        return true;
    }

    private boolean isValidColumn(int[][] board, int col) {
        boolean[] seen = new boolean[9];
        for (int row = 0; row < 9; row++) {
            int num = board[row][col];
            if (num < 1 || num > 9 || seen[num - 1]) {
                return false;
            }
            seen[num - 1] = true;
        }
        return true;
    }

    private boolean isValidBox(int[][] board, int box) {
        boolean[] seen = new boolean[9];
        int startRow = (box / 3) * 3;
        int startCol = (box % 3) * 3;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int num = board[startRow + i][startCol + j];
                if (num < 1 || num > 9 || seen[num - 1]) {
                    return false;
                }
                seen[num - 1] = true;
            }
        }
        return true;
    }

}
