package edu.jose.vazquez.sudoku.ui;

import java.io.IOException;
import java.util.Scanner;

import edu.jose.vazquez.sudoku.models.Tablero;

public class CLI {

    public void runApp() {
        Tablero tablero = new Tablero();
        Scanner scanner = new Scanner(System.in);
        int opcion = -1;

        while (opcion != 5) {
            tablero.imprimir();
            menu();
            try {
                String entrada = scanner.nextLine();
                if (!entrada.trim().isEmpty()) {
                    opcion = Integer.parseInt(entrada);
                    switch (opcion) {
                        case 1:
                            tablero.ingresarNumero();
                            break;
                        case 2:
                            tablero.darPista();
                            break;
                        case 4:
                            System.out.println("╭────────────────────────╮");
                            System.out.println("│🐚 Serie de Fibonacci🐚 │");
                            System.out.println("╰────────────────────────╯");
                            while (true) {
                                System.out.print("Ingrese la cantidad de números a generar (mínimo 1): ");
                                try {
                                    String fibEntrada = scanner.nextLine();
                                    int n = Integer.parseInt(fibEntrada);

                                    if (n < 1) {
                                        System.out.println("El número debe ser al menos 1. Intente de nuevo.");
                                        continue;
                                    }
                                    System.out.println();
                                    tablero.mostrarFibonacci(n);
                                    
                                    String enter;
                                    do {
                                        System.out.println("Presione Enter para continuar...");
                                        enter = scanner.nextLine();
                                        if (!enter.isEmpty()) {
                                            System.out.println("❌ Entrada inválida. Debe presionar Enter.❌");
                                        }
                                    } while (!enter.isEmpty());
                                    
                                    break;
                                } catch (NumberFormatException e) {
                                    System.out.println("❌ Entrada inválida. Intente de nuevo.❌");
                                }
                            }
                            break;
                        case 3:
                            tablero.borrarNumero();
                            break;
                        case 5:
                            System.out.println("╭──────────────────────╮");
                            System.out.println("│  👋¡Hasta luego!👋  │");
                            System.out.println("╰──────────────────────╯");
                            break;
                        default:
                            System.out.println("❌ Opción inválida. Intente de nuevo.❌");
                            break;
                    }
                } else {
                    System.out.println("❌ Entrada inválida. Por favor, ingrese un número del menú.❌");
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Entrada inválida. Por favor, ingrese un número del menú.❌");
                opcion = -1;
            }

            if (tablero.estaLleno()) {
                boolean jugarDeNuevo = tablero.verificarYMostrarResultado();
                if (!jugarDeNuevo) {
                    opcion = 5;
                }
            }
        }
        scanner.close();
    }
    public static void showEmoji(){
        try {
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            new ProcessBuilder("cmd", "/c", "chcp 65001").inheritIO().start().waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
        public static void cleanScreen(){
            try {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
                //new ProcessBuilder("cmd", "/c", "chcp 65001").inheritIO().start().waitFor();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
    }
    public void menu() {
        System.out.println("╔════════════════════════════════════╗");
        System.out.println("║   ➕   SUDOKU & FIBONACCI    ➕    ║");
        System.out.println("╚════════════════════════════════════╝");
        System.out.println("╔════════════════════════════════════╗");
        System.out.println("║ 1. Ingresar un número en el Sudoku ║");
        System.out.println("║ 2. Dar una pista para el Sudoku    ║");
        System.out.println("║ 3. Borrar un número del Sudoku     ║");
        System.out.println("║ 4. Serie de Fibonacci              ║");
        System.out.println("║ 5. Salir                           ║");
        System.out.println("╚════════════════════════════════════╝");
    }
}
