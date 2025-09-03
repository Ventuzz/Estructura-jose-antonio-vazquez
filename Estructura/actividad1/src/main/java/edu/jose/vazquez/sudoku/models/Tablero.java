package edu.jose.vazquez.sudoku.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;


public class Tablero {

    private int[][] celdas;

    public Tablero() {
        celdas = new int[3][3];
    }

    public int getCelda(int fila, int col) {
        return celdas[fila][col];
    }

    public boolean setCelda(int fila, int col, int valor) {
        if (valor < 1 || valor > 9) {
            System.out.println("El número debe estar entre 1 y 9.");
            return false;
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (celdas[i][j] == valor) {
                    System.out.println("El número " + valor + " ya está en el tablero.");
                    return false;
                }
            }
        }

        celdas[fila][col] = valor;
        return true;
    }

    public boolean esVacia(int fila, int col) {
        return celdas[fila][col] == 0;
    }

    public void imprimir() {
        for (int r = 0; r < 3; r++) {
            if (r % 3 == 0) System.out.println("+-------+");
            for (int c = 0; c < 3; c++) {
                if (c % 3 == 0) System.out.print("| ");
                int v = celdas[r][c];
                System.out.print(v == 0 ? ". " : (v + " "));
            }
            System.out.println("|");
        }
        System.out.println("+-------+");
    }

    public boolean esCuadradoMagico() {
        int sumaObjetivo = 15;

        for (int i = 0; i < 3; i++) {
            int sumaFila = 0;
            for (int j = 0; j < 3; j++) {
                sumaFila += celdas[i][j];
            }
            if (sumaFila != sumaObjetivo) {
                return false;
            }
        }

        for (int j = 0; j < 3; j++) {
            int sumaColumna = 0;
            for (int i = 0; i < 3; i++) {
                sumaColumna += celdas[i][j];
            }
            if (sumaColumna != sumaObjetivo) {
                return false;
            }
        }

        int sumaDiagonal1 = celdas[0][0] + celdas[1][1] + celdas[2][2];
        int sumaDiagonal2 = celdas[0][2] + celdas[1][1] + celdas[2][0];

        return sumaDiagonal1 == sumaObjetivo && sumaDiagonal2 == sumaObjetivo;
    }

    public void darPista() {
        List<Integer> numerosPosibles = new ArrayList<>();
        for (int i = 1; i <= 9; i++) {
            numerosPosibles.add(i);
        }

        List<Integer> numerosDisponibles = new ArrayList<>(numerosPosibles);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (celdas[i][j] != 0) {
                    numerosDisponibles.remove(Integer.valueOf(celdas[i][j]));
                }
            }
        }

        if (numerosDisponibles.isEmpty()) {
            System.out.println("¡No hay más números disponibles para dar una pista!");
            return;
        }

        Random random = new Random();
        int numeroPista = numerosDisponibles.get(random.nextInt(numerosDisponibles.size()));
        List<int[]> celdasVacias = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (celdas[i][j] == 0) {
                    celdasVacias.add(new int[]{i, j});
                }
            }
        }
        int[] celdaSeleccionada = celdasVacias.get(random.nextInt(celdasVacias.size()));
        if (setCelda(celdaSeleccionada[0], celdaSeleccionada[1], numeroPista)) {
            System.out.println("Pista: El número " + numeroPista + " se ha colocado en la celda (" +
                    celdaSeleccionada[0] + ", " + celdaSeleccionada[1] + ")");

        }
    }

    public void ingresarNumero() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Ingrese fila (0, 1, 2): ");
        int fila = scanner.nextInt();
        System.out.print("Ingrese columna (0, 1, 2): ");
        int col = scanner.nextInt();
        System.out.print("Ingrese número (1-9): ");
        int valor = scanner.nextInt();

        if (fila < 0 || fila > 2 || col < 0 || col > 2) {
            System.out.println("Las coordenadas deben estar entre 0 y 2.");
        } else if (!esVacia(fila, col)) {
            System.out.println("Esa celda ya está ocupada.");
        } else {
            boolean exito = setCelda(fila, col, valor);
            if (exito) {
                System.out.println("Número " + valor + " colocado en (" + fila + ", " + col + ")");

            }
        }
    }

    public boolean estaLleno() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (celdas[i][j] == 0) {
                    return false; 
                }
            }
        }
        return true;
    }

    public void verificarYMostrarResultado() {
        if (esCuadradoMagico()) {
            System.out.println("¡Felicidades! El tablero es un cuadrado mágico.");
        } else {
            System.out.println("Lo siento, el tablero no es un cuadrado mágico.");
        }

        Scanner scanner = new Scanner(System.in);
        System.out.print("¿Quieres jugar de nuevo? (s/n): ");
        String respuesta = scanner.nextLine();

        if (respuesta.equalsIgnoreCase("s")) {
            reiniciarTablero();
            System.out.println("¡Nuevo juego iniciado! Aquí está el tablero vacío:");
            imprimir();
        } else {
            System.out.println("¡Gracias por jugar! ¡Hasta luego!");
        }
    }

    public void reiniciarTablero() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                celdas[i][j] = 0; 
            }
        }
    }
}

