package edu.jose.vazquez.sudoku.models;

public class Tablero {

    private int[][] celdas;

    public Tablero() {
        celdas = new int[6][6];
    }

    public int getCelda(int fila, int col) {
        return celdas[fila][col];
    }

    public void setCelda(int fila, int col, int valor) {
        celdas[fila][col] = valor;
    }

    public boolean esVacia(int fila, int col) {
        return celdas[fila][col] == 0;
    }

    public void imprimir() {
        for (int r = 0; r < 6; r++) {
            if (r % 3 == 0) System.out.println("+-------+-------+");
            for (int c = 0; c < 6; c++) {
                if (c % 3 == 0) System.out.print("| ");
                int v = celdas[r][c];
                System.out.print(v == 0 ? ". " : (v + " "));
            }
            System.out.println("|");
        }
        System.out.println("+-------+-------+");
    }
}
