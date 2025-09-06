package edu.jose.vazquez.sudoku.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Tablero {

    private int[][] celdas;
    private Scanner scanner;

    public Tablero() {
        celdas = new int[3][3];
        scanner = new Scanner(System.in);
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
            if (r == 0) System.out.println("+-------+");
            for (int c = 0; c < 3; c++) {
                if (c == 0) System.out.print("| ");
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
        int fila, col, valor;
        String entrada;
        do {
            try {
                System.out.print("Ingrese fila (0, 1, 2): ");
                entrada = scanner.nextLine();
                fila = Integer.parseInt(entrada);
                if (fila >= 0 && fila <= 2) {
                    break;
                } else {
                    System.out.println("Las coordenadas de fila deben estar entre 0 y 2.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Por favor, ingrese un número para la fila.");
            }
        } while (true);
        do {
            try {
                System.out.print("Ingrese columna (0, 1, 2): ");
                entrada = scanner.nextLine();
                col = Integer.parseInt(entrada);
                if (col >= 0 && col <= 2) {
                    break;
                } else {
                    System.out.println("Las coordenadas de columna deben estar entre 0 y 2.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Por favor, ingrese un número para la columna.");
            }
        } while (true);
        do {
            try {
                System.out.print("Ingrese número (1-9): ");
                entrada = scanner.nextLine();
                valor = Integer.parseInt(entrada);
                if (valor >= 1 && valor <= 9) {
                    break;
                } else {
                    System.out.println("El número debe estar entre 1 y 9.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Por favor, ingrese un número para el valor.");
            }
        } while (true);
        if (!esVacia(fila, col)) {
            System.out.println("Esa celda ya está ocupada.");
        } else {
            boolean exito = setCelda(fila, col, valor);
            if (exito) {
                System.out.println("Número " + valor + " colocado en (" + fila + ", " + col + ")");
            }
        }
    }

    public void borrarNumero() {
        int fila, col;
        String entrada;

        do {
            try {
                System.out.print("Ingrese fila del número a borrar (0, 1, 2): ");
                entrada = scanner.nextLine();
                fila = Integer.parseInt(entrada);
                if (fila >= 0 && fila <= 2) {
                    break;
                } else {
                    System.out.println("Las coordenadas de fila deben estar entre 0 y 2.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Por favor, ingrese un número para la fila.");
            }
        } while (true);

        do {
            try {
                System.out.print("Ingrese columna del número a borrar (0, 1, 2): ");
                entrada = scanner.nextLine();
                col = Integer.parseInt(entrada);
                if (col >= 0 && col <= 2) {
                    break;
                } else {
                    System.out.println("Las coordenadas de columna deben estar entre 0 y 2.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Por favor, ingrese un número para la columna.");
            }
        } while (true);

        if (esVacia(fila, col)) {
            System.out.println("No hay ningún número en esa celda para borrar.");
        } else {
            int valorBorrado = celdas[fila][col];
            celdas[fila][col] = 0;
            System.out.println("Número " + valorBorrado + " borrado de (" + fila + ", " + col + ")");
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

    public void reiniciarTablero() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                celdas[i][j] = 0;
            }
        }
    }

    public void mostrarFibonacci(int n) {
        int a = 0, b = 1;
        System.out.println("╭─────────────────────────────────────────╮");
        System.out.println("│Los primeros " + n + " números de Fibonacci son: │");
        System.out.println("╰─────────────────────────────────────────╯");
        for (int i = 0; i < n; i++) {
            System.out.print(a + " ");
            int siguiente = a + b;
            a = b;
            b = siguiente;
        }
        System.out.println();
    }

    public boolean verificarYMostrarResultado() {
        imprimir();
        if (esCuadradoMagico()) {
            System.out.println("╔═════════════════════════════════════════════════════╗");
            System.out.println("║🎉¡Felicidades! El tablero es un cuadrado mágico.🎉 ║");
            System.out.println("╚═════════════════════════════════════════════════════╝");
        } else {
            System.out.println("╔══════════════════════════════════════════════════════╗");
            System.out.println("║ 😓Lo siento, el tablero no es un cuadrado mágico.😓 ║");
            System.out.println("╚══════════════════════════════════════════════════════╝");
        }
        String respuesta;
        do {
            System.out.print("🤔¿Quieres jugar de nuevo? 🤔(s/n): ");
            respuesta = scanner.nextLine();
            if (respuesta.equalsIgnoreCase("s")) {
                reiniciarTablero();
                return true;
            } else if (respuesta.equalsIgnoreCase("n")) {
                System.out.println("👋¡Gracias por jugar! ¡Hasta luego!👋");
                return false;
            } else {
                System.out.println("Entrada inválida. Intente de nuevo.");
            }
        } while (!respuesta.equalsIgnoreCase("s") && !respuesta.equalsIgnoreCase("n"));
        return false;
    }
}

