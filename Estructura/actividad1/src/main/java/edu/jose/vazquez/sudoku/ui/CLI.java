package edu.jose.vazquez.sudoku.ui;

import edu.jose.vazquez.sudoku.models.Tablero;


public class CLI {

    public void runApp() {
        Tablero tablero = new Tablero();
        java.util.Scanner scanner = new java.util.Scanner(System.in);
        int opcion= -1;
        while (opcion != 3) {
            tablero.imprimir();

            System.out.println("Opciones:");
            System.out.println("1. Ingresar un número");
            System.out.println("2. Dar una pista");
            System.out.println("3. Salir");

            System.out.print("Seleccione una opción: ");
            opcion = scanner.nextInt();

            switch (opcion) {
                case 1:
                    tablero.ingresarNumero();
                    break;
                case 2:
                    tablero.darPista();
                    break;
                case 3:
                    System.out.println("¡Hasta luego!");
                    break;
                default:
                    System.out.println("Opción inválida. Intente de nuevo.");
            }

            if (tablero.estaLleno()) {
                tablero.verificarYMostrarResultado();

                break;
            }
        }

    }

}
