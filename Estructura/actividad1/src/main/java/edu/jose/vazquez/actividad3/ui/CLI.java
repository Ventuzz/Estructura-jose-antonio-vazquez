package edu.jose.vazquez.actividad3.ui;
import edu.jose.vazquez.actividad3.process.HospitalManager;

import java.io.IOException;


public class CLI {
    static HospitalManager hospitalManager = new HospitalManager();

    public static void runApp(){
        cleanScreen();
        showEmoji();
        java.util.Scanner scanner = new java.util.Scanner(System.in);
        int option = -1;
        while (option != 4) {
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
                    System.out.println("║               AGREGAR NUEVO PACIENTE             ║");
                    System.out.println("╚══════════════════════════════════════════════════╝");
                    String nombre = "";
                    while (true) {
                        System.out.print("Ingrese el nombre del paciente: ");
                        nombre = scanner.nextLine();
                        if (!nombre.trim().isEmpty()) {
                            break;
                        }
                        System.out.println("❌ El nombre del paciente no puede estar vacío. ❌");
                    }
                    int prioridad = -1;
                    while (true) {
                        System.out.println("╔══════════════════════════════════════════════════╗");
                        System.out.println("║               ESTADO DEL PACIENTE                ║");
                        System.out.println("╚══════════════════════════════════════════════════╝");
                        System.out.println("║  1. Crítico                                      ║");
                        System.out.println("║  2. Grave                                        ║");
                        System.out.println("║  3. Moderado                                     ║");
                        System.out.println("║  4. Leve                                         ║");
                        System.out.println("║  5. Estable                                      ║");
                        System.out.println("╚══════════════════════════════════════════════════╝");
                        System.out.print("Ingrese el numero correspondiente al estado del paciente: ");
                        String prioridadInput = scanner.nextLine();
                        try {
                            prioridad = Integer.parseInt(prioridadInput);
                            if (prioridad > 0 && prioridad <= 5) {
                                break;
                            }
                            System.out.println("❌ Por favor, ingrese un estado válido, no puede ser negativo. ❌");
                        } catch (NumberFormatException e) {
                            System.out.println("❌ Por favor, ingrese un número válido, no puede estar vacío ni contener letras. ❌");
                        }
                    }
                    int edad = -1;
                    while (true) {
                        System.out.println("╔══════════════════════════════════════════════════╗");
                        System.out.println("║               EDAD DEL PACIENTE                  ║");
                        System.out.println("╚══════════════════════════════════════════════════╝");
                        System.out.print("Ingrese la edad del paciente: ");
                        String ageInput = scanner.nextLine();
                        try {
                            edad = Integer.parseInt(ageInput);
                            if (edad > 0) {
                                break;
                            }
                            System.out.println("❌ Por favor, ingrese una edad válida, no puede ser negativa. ❌");
                        } catch (NumberFormatException e) {
                            System.out.println("❌ Por favor, ingrese un número válido, no puede estar vacío ni contener letras. ❌");
                        }
                    }
                    String enfermedad = "";
                    while (true) {
                        System.out.println("╔══════════════════════════════════════════════════╗");
                        System.out.println("║               ENFERMEDAD DEL PACIENTE            ║");
                        System.out.println("╚══════════════════════════════════════════════════╝");
                        System.out.print("Ingrese la enfermedad del paciente: ");
                        enfermedad = scanner.nextLine().trim();
                        if (!enfermedad.isEmpty()) {
                            break;
                        }
                        System.out.println("❌ La enfermedad no puede estar vacía. ❌");
                    }

                    hospitalManager.addPaciente(nombre, edad, enfermedad, prioridad);
                    System.out.println("╔══════════════════════════════════════════════════╗");
                    System.out.println("║        ✅ PACIENTE AGREGADO EXITOSAMENTE ✅      ║");
                    System.out.println("╚══════════════════════════════════════════════════╝");
                    System.out.println("  💊 Nombre: " + nombre);
                    System.out.println("  💊 Edad: " + edad);
                    System.out.println("  💊 Enfermedad: " + enfermedad + "😷");
                    System.out.println("  💊 Prioridad: " + prioridad);
                    System.out.println();
                    System.out.print("Presione Enter para volver al menú principal...");
                    scanner.nextLine();
                    cleanScreen();
                    break;
                case 2:
                    cleanScreen();
                    hospitalManager.showPacientes();
                    break;
                case 3:
                    cleanScreen();
                    hospitalManager.sortPacientesPorPrioridad();
                    break;
                case 4:
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
                
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
    }

    public static void showMenu() {
         System.out.println("╔════════════════════════════════════════════════════╗");
        System.out.println("║             🏥 HOSPITAL MANAGER 🏥                 ║");
        System.out.println("╠════════════════════════════════════════════════════╣");
        System.out.println("║ 1. Agregar Paciente                                ║");
        System.out.println("║ 2. Mostrar lista de pacientes por orden de llegada ║");
        System.out.println("║ 3. Mostrar lista de pacientes por prioridad        ║");
        System.out.println("║ 4. Salir                                           ║");
        System.out.println("╚════════════════════════════════════════════════════╝");
    }


    public static void showEmoji(){
        try {
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            //new ProcessBuilder("cmd", "/c", "chcp 65001").inheritIO().start().waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    }

