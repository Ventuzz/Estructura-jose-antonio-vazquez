package edu.jose.vazquez.actividades.actividad1.ui;

import java.io.IOException;
import java.util.Scanner;

public class CLI {

    public static void runApp(){
        cleanScreen();
        showMenu();
        Scanner scanner = new Scanner(System.in);
        int option = -1;
        while (option != 4) {
            
            while (true) {
                String input = scanner.nextLine().trim();
                if (input.isEmpty()) {
                    cleanScreen();
                    System.out.println("Entrada vacía. Por favor, ingrese un número entre 1 y 4.");
                    showMenu();
                    continue;
                }
                try {
                    option = Integer.parseInt(input);
                    if (option < 1 || option > 4) {
                        System.out.println("Opción inválida. Intente de nuevo.");
                        showMenu();
                    } else {
                        break;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Entrada no válida. Por favor, ingrese un número entre 1 y 4.");
                    showMenu();
                }
            }
            switch (option) {
                case 1:
                    System.out.println("Estas son las playlists disponibles:");
                    System.out.println("Seleccione la playlist:");
                    // Aquí se llamaría al método para seleccionar una playlist
                    break;
            
                case 2:
                    System.out.println("Creando playlist...");
                    System.out.println("Seleccione el tipo de playlist:");
                    System.out.println("1. Playlist Normal");
                    System.out.println("2. Playlist Navegable");
                    System.out.println("3. Playlist Bucle");
                    int playlistType = scanner.nextInt();
                    scanner.nextLine(); // Consumir el salto de línea
                    switch (playlistType) {
                        case 1:
                            menuPlaylistNormal();
                            
                            // Aquí se llamaría al método para crear una playlist normal
                            break;
                        case 2:
                            menuPlaylistNavegable();
                            // Aquí se llamaría al método para crear una playlist navegable
                            break;
                        case 3:
                            menuPlaylistBucle();
                            // Aquí se llamaría al método para crear una playlist en bucle
                            break;
                        default:
                            System.out.println("Tipo de playlist no válido.");
                    }
                    break;
                case 3:
                    System.out.println("Eliminando playlist...");
                    System.out.println("Seleccione la playlist a eliminar:");
                    // Aquí se llamaría al método para eliminar una playlist

                    break;
                case 4:
                    System.out.println("Saliendo de la aplicación. ¡Hasta luego!");
                    break;
            }
        }
    }

    public static void cleanScreen(){
        try {
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
         }
    }

    public static void showMenu() {
        System.out.println("╔════════════════════════════════╗");
        System.out.println("║            Spotysix            ║");
        System.out.println("╠════════════════════════════════╣");
        System.out.println("║     1. Seleccionar playlist    ║");
        System.out.println("║     2. Crear playlist          ║");
        System.out.println("║     3. Eliminar playlist       ║");
        System.out.println("║     4. Salir                   ║");
        System.out.println("╚════════════════════════════════╝");
        System.out.print("Seleccione una opción: ");
    }

    public static void showMenuPlaylistNormal() {
        System.out.println("╔════════════════════════════════╗");
        System.out.println("║         Playlist Normal        ║");
        System.out.println("╠════════════════════════════════╣");
        System.out.println("║     1. Agregar canción         ║");
        System.out.println("║     2. Eliminar canción        ║");
        System.out.println("║     3. Buscar canción          ║");
        System.out.println("║     4. Actualmente sonando     ║");
        System.out.println("║     5. Salir                   ║");
        System.out.println("╚════════════════════════════════╝");
        System.out.print("Seleccione una opción: ");
    }

    public static void showMenuPlaylistNavegable() {
        System.out.println("╔════════════════════════════════╗");
        System.out.println("║       Playlist Navegable       ║");
        System.out.println("╠════════════════════════════════╣");
        System.out.println("║     1. Agregar canción         ║");
        System.out.println("║     2. Eliminar canción        ║");
        System.out.println("║     3. Buscar canción          ║");
        System.out.println("║     4. Listar canciones        ║");
        System.out.println("║     5. Actualmente sonando     ║");
        System.out.println("║     6. Siguiente canción       ║");
        System.out.println("║     7. Anterior canción        ║");
        System.out.println("║     8. Salir                   ║");
        System.out.println("╚════════════════════════════════╝");
        System.out.print("Seleccione una opción: ");
    }

    public static void showMenuPlaylistBucle() {
        System.out.println("╔════════════════════════════════╗");
        System.out.println("║         Playlist Bucle         ║");
        System.out.println("╠════════════════════════════════╣");
        System.out.println("║     1. Agregar canción         ║");
        System.out.println("║     2. Eliminar canción        ║");
        System.out.println("║     3. Buscar canción          ║");
        System.out.println("║     4. Listar canciones        ║");
        System.out.println("║     5. Actualmente sonando     ║");
        System.out.println("║     6. Siguiente canción       ║");
        System.out.println("║     7. Salir                   ║");
        System.out.println("╚════════════════════════════════╝");
        System.out.print("Seleccione una opción: ");
    }

    public static void menuPlaylistBucle(){
        showMenuPlaylistBucle();
        Scanner scanner = new Scanner(System.in);
        int option = -1;
        while (option != 7) {
            while (true) {
                String input = scanner.nextLine().trim();
                if (input.isEmpty()) {
                    System.out.println("Entrada vacía. Por favor, ingrese un número entre 1 y 7.");
                    showMenuPlaylistBucle();
                    continue;
                }
                try {
                    option = Integer.parseInt(input);
                    if (option < 1 || option > 7) {
                        System.out.println("Opción inválida. Intente de nuevo.");
                        showMenuPlaylistBucle();
                    } else {
                        break;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Entrada no válida. Por favor, ingrese un número entre 1 y 7.");
                    showMenuPlaylistBucle();
                }
            }
            switch (option) {
                case 1:
                    System.out.println("Agregando canción...");
                    // Aquí se llamaría al método para agregar una canción
                    break;
                case 2:
                    System.out.println("Eliminando canción...");
                    // Aquí se llamaría al método para eliminar una canción
                    break;
                case 3:
                    System.out.println("Buscando canción...");
                    // Aquí se llamaría al método para buscar una canción
                    break;
                case 4:
                    System.out.println("Listando canciones...");
                    // Aquí se llamaría al método para listar las canciones
                    break;
                case 5:
                    System.out.println("Actualmente sonando...");
                    // Aquí se llamaría al método para mostrar la canción que está sonando
                    break;
                case 6:
                    System.out.println("Siguiente canción...");
                    // Aquí se llamaría al método para ir a la siguiente canción
                    break;
                case 7:
                    System.out.println("Saliendo de la playlist en bucle.");
                    cleanScreen();
                    showMenu();
                    break;
            }
        }
    }

    public static void menuPlaylistNavegable(){
        showMenuPlaylistNavegable();
        Scanner scanner = new Scanner(System.in);
        int option = -1;
        while (option != 8) {
            while (true) {
                String input = scanner.nextLine().trim();
                if (input.isEmpty()) {
                    System.out.println("Entrada vacía. Por favor, ingrese un número entre 1 y 8.");
                    showMenuPlaylistNavegable();
                    continue;
                }
                try {
                    option = Integer.parseInt(input);
                    if (option < 1 || option > 8) {
                        System.out.println("Opción inválida. Intente de nuevo.");
                        showMenuPlaylistNavegable();
                    } else {
                        break;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Entrada no válida. Por favor, ingrese un número entre 1 y 8.");
                    showMenuPlaylistNavegable();
                }
            }
            switch (option) {
                case 1:
                    System.out.println("Agregando canción...");
                    // Aquí se llamaría al método para agregar una canción
                    break;
                case 2:
                    System.out.println("Eliminando canción...");
                    // Aquí se llamaría al método para eliminar una canción
                    break;
                case 3:
                    System.out.println("Buscando canción...");
                    // Aquí se llamaría al método para buscar una canción
                    break;
                case 4:
                    System.out.println("Listando canciones...");
                    // Aquí se llamaría al método para listar las canciones
                    break;
                case 5:
                    System.out.println("Actualmente sonando...");
                    // Aquí se llamaría al método para mostrar la canción que está sonando
                    break;
                case 6:
                    System.out.println("Siguiente canción...");
                    // Aquí se llamaría al método para ir a la siguiente canción
                    break;
                case 7:
                    System.out.println("Anterior canción...");
                    // Aquí se llamaría al método para ir a la canción anterior
                    break;
                case 8:
                    System.out.println("Saliendo de la playlist navegable.");
                    cleanScreen();
                    showMenu();
                    break;
            }
        }
    }

    public static void menuPlaylistNormal(){
        showMenuPlaylistNormal();
        Scanner scanner = new Scanner(System.in);
        int option = -1;
        while (option != 5) {
            while (true) {
                String input = scanner.nextLine().trim();
                if (input.isEmpty()) {
                    System.out.println("Entrada vacía. Por favor, ingrese un número entre 1 y 5.");
                    showMenuPlaylistNormal();
                    continue;
                }
                try {
                    option = Integer.parseInt(input);
                    if (option < 1 || option > 5) {
                        System.out.println("Opción inválida. Intente de nuevo.");
                        showMenuPlaylistNormal();
                    } else {
                        break;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Entrada no válida. Por favor, ingrese un número entre 1 y 5.");
                    showMenuPlaylistNormal();
                }
            }
            switch (option) {
                case 1:
                    System.out.println("Agregando canción...");
                    // Aquí se llamaría al método para agregar una canción
                    break;
                case 2:
                    System.out.println("Eliminando canción...");
                    // Aquí se llamaría al método para eliminar una canción
                    break;
                case 3:
                    System.out.println("Buscando canción...");
                    // Aquí se llamaría al método para buscar una canción
                    break;
                case 4:
                    System.out.println("Actualmente sonando...");
                    // Aquí se llamaría al método para mostrar la canción que está sonando
                    break;
                case 5:
                    System.out.println("Saliendo de la playlist normal.");
                    cleanScreen();
                    showMenu();
                    break;
            }
        }
    }


}
