package edu.jose.vazquez.actividad2.ui;
import edu.jose.vazquez.actividad2.models.Game;
import edu.jose.vazquez.actividad2.process.GameManager;
import java.io.IOException;


public class CLI {
    static GameManager gameManager = new GameManager();

    public static void runApp(){
        cleanScreen();
        showEmoji();
        java.util.Scanner scanner = new java.util.Scanner(System.in);
        int option = -1;
        while (option != 7) {
            showMenu();
            while (true) {
                System.out.print("Seleccione una opción: ");
                String input = scanner.nextLine();
                if (input.isEmpty()) {
                    System.out.println("❌ Por favor, no deje el campo vacío e ingrese un número a continuación. ❌");
                    continue;
                }
                try {
                    option = Integer.parseInt(input);
                } catch (NumberFormatException e) {
                    System.out.println("❌ Por favor, ingrese un carácter válido, le recomiendo que ingrese un número del 1 al 7. ❌");
                    continue;
                }
                if (option >= 1 && option <= 7) {
                    break;
                }
                System.out.println("❌ Opción inválida. Por favor, seleccione una opción del 1 al 7. ❌");
            }
            switch (option) {
                case 1:
                    cleanScreen();
                    System.out.println("╔══════════════════════════════════════════════════╗");
                    System.out.println("║               AGREGAR NUEVO JUEGO                ║");
                    System.out.println("╚══════════════════════════════════════════════════╝");
                    String name = "";
                    while (true) {
                        System.out.print("Ingrese el nombre del juego: ");
                        name = scanner.nextLine();
                        if (!name.trim().isEmpty()) {
                            break;
                        }
                        System.out.println("❌ El nombre del juego no puede estar vacío. ❌");
                    }
                    String genre = "";
                    while (true) {
                        System.out.print("Ingrese el género del juego: ");
                        genre = scanner.nextLine();
                        if (!genre.trim().isEmpty()) {
                            break;
                        }
                        System.out.println("❌ El género del juego no puede estar vacío. ❌");
                    }
                    int releaseYear = -1;
                    while (true) {
                        System.out.print("Ingrese el año de lanzamiento del juego: ");
                        String yearInput = scanner.nextLine();
                        try {
                            releaseYear = Integer.parseInt(yearInput);
                            if (releaseYear > 0) {
                                break;
                            }
                            System.out.println("❌ Por favor, ingrese un año válido, no puede ser negativo. ❌");
                        } catch (NumberFormatException e) {
                            System.out.println("❌ Por favor, ingrese un número válido, no puede estar vacío ni contener letras. ❌");
                        }
                    }
                    if (gameManager.getGameIgnoreCase(name) != null) {
                        System.out.println("❌ El juego ya existe. ❌");
                        System.out.print("Presione Enter para volver al menú principal...");
                        scanner.nextLine();
                        cleanScreen();
                        break;
                    }
                    
                    gameManager.addGame(name, genre, releaseYear);
                    System.out.println("╔══════════════════════════════════════════════════╗");
                    System.out.println("║        ✅ JUEGO AGREGADO EXITOSAMENTE ✅        ║");
                    System.out.println("╚══════════════════════════════════════════════════╝");
                    System.out.println("  ♦ Nombre: " + name);
                    System.out.println("  ♦ Género: " + genre);
                    System.out.println("  ♦ Año de lanzamiento: " + releaseYear);
                    System.out.println();
                    System.out.print("Presione Enter para volver al menú principal...");
                    scanner.nextLine();
                    cleanScreen();
                    break;
                case 2:
                    cleanScreen();
                    System.out.println("╔══════════════════════════════════════════════════╗");
                    System.out.println("║            ❌ ELIMINAR JUEGO  ❌                ║");
                    System.out.println("╚══════════════════════════════════════════════════╝");
                    System.out.println("Estos son los juegos disponibles:");
                    gameManager.listGames();
                    String removeName;
                    while (true) {
                        System.out.print("Ingrese el nombre del juego a eliminar: ");
                        removeName = scanner.nextLine().trim(); 
                        if (removeName.isEmpty()) {
                            System.out.println("❌ El nombre no puede estar vacío. Intente nuevamente. ❌");
                        } else {
                            break; 
                        }
                    }
                    gameManager.removeGame(removeName);
                    System.out.print("Presione Enter para volver al menú principal...");
                    scanner.nextLine();
                    cleanScreen();
                    break;
                case 3:
                    cleanScreen();
                    System.out.println("╔══════════════════════════════════════════════════╗");
                    System.out.println("║               ACTUALIZAR JUEGO   🤔              ║");
                    System.out.println("╚══════════════════════════════════════════════════╝");
                    gameManager.listGames();
                    String updateName;
                    while (true) {
                        System.out.print("Ingrese el nombre del juego a actualizar: ");
                        updateName = scanner.nextLine().trim();
                        if (updateName.isEmpty()) {
                            System.out.println("❌ El nombre no puede estar vacío. ❌");
                            continue;
                        }
                        break;
                    }

                    Game gameToUpdate = gameManager.getGameIgnoreCase(updateName);
                    if (gameToUpdate == null) {
                        System.out.println("╔══════════════════════════════════════════════════╗");
                        System.out.println("║           ❌ JUEGO NO ENCONTRADO ❌             ║");
                        System.out.println("╚══════════════════════════════════════════════════╝");
                        System.out.print("Presione Enter para volver al menú principal...");
                        scanner.nextLine();
                        break;
                    }else{
                        System.out.println("╔══════════════════════════════════════════════════╗");
                        System.out.println("║           ✅ JUEGO ENCONTRADO ✅                ║");
                        System.out.println("╚══════════════════════════════════════════════════╝");
                        System.out.println("  ♦ Nombre: " + gameToUpdate.getName());
                        System.out.println("  ♦ Género: " + gameToUpdate.getGenre());
                        System.out.println("  ♦ Año de lanzamiento: " + gameToUpdate.getReleaseYear());
                        System.out.println();
                    }
                    String newGenre;
                    while (true) {
                        System.out.print("Ingrese el nuevo género del juego: ");
                        newGenre = scanner.nextLine().trim();
                        if (newGenre.isEmpty()) {
                            System.out.println("❌ El género no puede estar vacío. ❌");
                            continue;
                        }
                        break;
                    }

                    int newReleaseYear;
                    while (true) {
                        System.out.print("Ingrese el nuevo año de lanzamiento del juego: ");
                        String yearStr = scanner.nextLine().trim();
                        try {
                            newReleaseYear = Integer.parseInt(yearStr);
                            if (newReleaseYear <= 0) {
                                System.out.println("❌ El año debe ser mayor que 0. ❌");
                                continue;
                            }
                            break;
                        } catch (NumberFormatException e) {
                            System.out.println("❌ Ingrese un número válido para el año. ❌");
                        }
                    }

                    gameManager.updateGame(updateName, newGenre, newReleaseYear);

                    System.out.println("╔══════════════════════════════════════════════════╗");
                    System.out.println("║       ✅ JUEGO ACTUALIZADO EXITOSAMENTE ✅      ║");
                    System.out.println("╚══════════════════════════════════════════════════╝");
                    System.out.print("Presione Enter para volver al menú principal...");
                    scanner.nextLine();
                    cleanScreen();
                    break;
                case 4:
                    cleanScreen();
                    System.out.println("╔══════════════════════════════════════════════════╗");
                    System.out.println("║                   BUSCAR JUEGO                   ║");
                    System.out.println("╚══════════════════════════════════════════════════╝");
                    System.out.println();
                    while (true) {
                        System.out.print("Ingrese el nombre del juego a buscar: ");
                        String searchName = scanner.nextLine();
                        if (!searchName.trim().isEmpty()) {
                            gameManager.searchGame(searchName);
                            break;
                        } else {
                            System.out.println("❌ El nombre del juego no puede estar vacío. ❌");
                        }
                    }
                    System.out.println();
                    System.out.print("Presione Enter para volver al menú principal...");
                    scanner.nextLine();
                    cleanScreen();
                    break;
                case 5:
                    cleanScreen();
                    System.out.println("╔══════════════════════════════════════════════════╗");
                    System.out.println("║                   LISTA DE JUEGOS                ║");
                    System.out.println("╚══════════════════════════════════════════════════╝");
                    System.out.println();
                    gameManager.listGames();
                    System.out.print("Presione Enter para volver al menú principal...");
                    scanner.nextLine();
                    cleanScreen();
                    break;
                case 6:
                    cleanScreen();
                    menuSort();
                    break;
                case 7:
                    System.out.println("Saliendo...");
                    break;
                default:
                    System.out.println("❌ Opción no válida. ❌");
            }
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

    public static void showMenu() {
         System.out.println("╔══════════════════════════════════════════════════╗");
        System.out.println("║                     MENÚ                         ║");
        System.out.println("╠══════════════════════════════════════════════════╣");
        System.out.println("║ 1. Agregar Juego                                 ║");
        System.out.println("║ 2. Eliminar Juego                                ║");
        System.out.println("║ 3. Actualizar Juego                              ║");
        System.out.println("║ 4. Buscar Juego                                  ║");
        System.out.println("║ 5. Listar Juegos                                 ║");
        System.out.println("║ 6. Ordenar Juegos                                ║");
        System.out.println("║ 7. Salir                                         ║");
        System.out.println("╚══════════════════════════════════════════════════╝");
    }

    public static void showMenuSort() {
        System.out.println("╔══════════════════════════════════════════════════╗");
        System.out.println("║                ORDENAR JUEGOS                    ║");
        System.out.println("╠══════════════════════════════════════════════════╣");
        System.out.println("║ 1. Por Nombre                                    ║");
        System.out.println("║ 2. Por Género                                    ║");
        System.out.println("║ 3. Por Año de Lanzamiento                        ║");
        System.out.println("║ 4. Volver al Menú Principal                      ║");
        System.out.println("╚══════════════════════════════════════════════════╝");
    }

    public static void showEmoji(){
        try {
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
    public static void menuSort(){
        java.util.Scanner scanner = new java.util.Scanner(System.in);
        int option = -1;
        while(option!=4){
            showMenuSort();
                System.out.print("Seleccione una opción: ");
                String input = scanner.nextLine();
                if (input.isEmpty()) {
                    System.out.println("❌ Por favor, no deje el campo vacío e ingrese un número a continuación. ❌");
                    continue;
                }
                try {
                    option = Integer.parseInt(input);
                } catch (NumberFormatException e) {
                    System.out.println("❌ Por favor, ingrese un carácter válido, le recomiendo que ingrese un número del 1 al 4. ❌");
                    continue;
                }
                if (option >= 1 && option <= 4) {
                    break;
                }
                System.out.println("❌ Opción inválida. Por favor, seleccione una opción del 1 al 4. ❌");
            }
            switch (option) {
                case 1:
                    cleanScreen();
                    System.out.println("╔══════════════════════════════════════════════════╗");
                    System.out.println("║               ORDENANDO POR NOMBRE               ║");
                    System.out.println("╚══════════════════════════════════════════════════╝");
                    gameManager.sortGamesByName();
                    System.out.print("Presione Enter para volver al menú principal...");
                    scanner.nextLine();
                    cleanScreen();
                    break;
                case 2:
                    cleanScreen();
                    System.out.println("╔══════════════════════════════════════════════════╗");
                    System.out.println("║               ORDENANDO POR GÉNERO               ║");
                    System.out.println("╚══════════════════════════════════════════════════╝");
                    gameManager.sortGamesByGenre();
                    System.out.print("Presione Enter para volver al menú principal...");
                    scanner.nextLine();
                    cleanScreen();
                    break;
                case 3:
                    cleanScreen();
                    System.out.println("╔══════════════════════════════════════════════════╗");
                    System.out.println("║               ORDENANDO POR AÑO                  ║");
                    System.out.println("╚══════════════════════════════════════════════════╝");
                    gameManager.sortGamesByReleaseYear();
                    System.out.println("Presione Enter para volver al menú principal...");
                    scanner.nextLine();
                    cleanScreen();
                    break;
                case 4:
                    cleanScreen();
                    System.out.println("Volviendo al menú principal...");
                    break;
                default:
                    System.out.println("❌ Opción no válida. ❌");
            }
        }
    }
