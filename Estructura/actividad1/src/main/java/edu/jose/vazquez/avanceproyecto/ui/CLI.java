package edu.jose.vazquez.avanceproyecto.ui;

import java.io.IOException;

import edu.jose.vazquez.avanceproyecto.models.Pacientes;
import edu.jose.vazquez.avanceproyecto.process.HospitalManager;

public class CLI {
        static HospitalManager hospitalManager = new HospitalManager();

    public static void runApp(){
        showLoadingBar();
        cleanScreen();
        showEmoji();
        java.util.Scanner scanner = new java.util.Scanner(System.in);
        int option = -1;
        while (option != 6) {
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
                    System.out.println("❌ Por favor, ingrese un carácter válido, le recomiendo que ingrese un número del 1 al 6. ❌");
                    continue;
                }
                if (option >= 1 && option <= 6) {
                    break;
                }
                System.out.println("❌ Opción inválida. Por favor, seleccione una opción del 1 al 6. ❌");
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
                    showLoadingBarMenu();
                    cleanScreen();
                    break;
                case 2:
                    cleanScreen();
                    System.out.println("╔══════════════════════════════════════════════════╗");
                    System.out.println("║                 SALA DE ESPERA                   ║");
                    System.out.println("╚══════════════════════════════════════════════════╝");
                    hospitalManager.showPacientesEnEspera();
                    System.out.println();
                    System.out.print("Presione Enter para volver al menú principal...");
                    scanner.nextLine();
                    showLoadingBarMenu();
                    cleanScreen();
                    break;
                case 3:
                    cleanScreen();
                    System.out.println("╔══════════════════════════════════════════════════╗");
                    System.out.println("║            PRIORIDAD DE PACIENTES                ║");
                    System.out.println("╚══════════════════════════════════════════════════╝");
                    hospitalManager.sortPacientesPorPrioridad();
                    System.out.println();
                    System.out.print("Presione Enter para volver al menú principal...");
                    scanner.nextLine();
                    showLoadingBarMenu();
                    cleanScreen();
                    break;
                case 4:
                    cleanScreen();
                    System.out.println("╔══════════════════════════════════════════════════╗");
                    System.out.println("║               ATENCIÓN DE PACIENTE               ║");
                    System.out.println("╚══════════════════════════════════════════════════╝");
                    
                    hospitalManager.sortPacientesPorPrioridad();
                    System.out.println();
                    while(true){
                        System.out.print("Ingrese el nombre del paciente a atender: ");
                        String nombreAtender = scanner.nextLine().trim();
                        if(!nombreAtender.isEmpty()){
                            Pacientes pacienteAtendido = hospitalManager.attendPaciente(nombreAtender);
                            if (pacienteAtendido != null) {
                                System.out.println("╔══════════════════════════════════════════════════╗");
                                System.out.println("║        ✅ PACIENTE ATENDIDO EXITOSAMENTE ✅      ║");
                                System.out.println("╚══════════════════════════════════════════════════╝");
                                System.out.println("  💊 Nombre: " + pacienteAtendido.getNombre());
                                System.out.println("  💊 Estado: " + pacienteAtendido.getEstado());
                                System.out.println("  💊 Tratamiento: " + pacienteAtendido.getTratamiento());
                                System.out.println("  💊 Doctor: " + pacienteAtendido.getDoctor());
                            break;
                            } else {
                                System.out.println("❌ Paciente no encontrado. ❌");
                                break;
                            }
                        }else{
                            System.out.println("❌ El nombre no puede estar vacío. ❌");
                        }
                    }
                    System.out.println();
                    System.out.println("Presione Enter para volver al menú principal...");
                    scanner.nextLine();
                    showLoadingBarMenu();
                    cleanScreen();
                    break;
                case 5:
                    cleanScreen();
                    System.out.println("╔══════════════════════════════════════════════════╗");
                    System.out.println("║               REGISTRO DE PACIENTES              ║");
                    System.out.println("╚══════════════════════════════════════════════════╝");
                    hospitalManager.showRegistroPacientes();
                    System.out.print("Presione Enter para volver al menú principal...");
                    scanner.nextLine();
                    showLoadingBarMenu();
                    cleanScreen();
                    break;
                case 6:
                    cleanScreen();
                    showLoadingBar();
                    System.out.println("╔══════════════════════════════════════════════════╗");
                    System.out.println("║                   ¡Hasta luego!                  ║");
                    System.out.println("╚══════════════════════════════════════════════════╝");
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

    public static void showLoadingBar() {
            final int total = 50; 
            String bar = "";
            System.out.println();
            for (int i = 0; i <= total; i++) {
                int progress = (i * 100) / total;
                bar = "[" + "|".repeat(i) + " ".repeat(total - i) + "]";
                if (progress < 40) {
                    System.out.print("\u001B[31m"+(bar) + " " + progress + "%\r"+"\u001B[0m");
                } else if (progress < 80) {
                    System.out.print("\u001B[33m"+(bar) + " " + progress + "%\r"+"\u001B[0m");
                } else {
                    System.out.print("\u001B[32m"+(bar) + " " + progress + "%\r"+"\u001B[0m");
                }
        
                try {
                    Thread.sleep(60); 
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        System.out.println(); 
    }

    public static void showLoadingBarMenu() {
            final int total = 50; 
            String bar = "";
            System.out.println();
            for (int i = 0; i <= total; i++) {
                int progress = (i * 100) / total;
                bar = "[" + "|".repeat(i) + " ".repeat(total - i) + "]";
                if (progress < 40) {
                    System.out.print("\u001B[31m"+(bar) + " " + progress + "%\r"+"\u001B[0m");
                } else if (progress < 80) {
                    System.out.print("\u001B[33m"+(bar) + " " + progress + "%\r"+"\u001B[0m");
                } else {
                    System.out.print("\u001B[32m"+(bar) + " " + progress + "%\r"+"\u001B[0m");
                }
        
                try {
                    Thread.sleep(15); 
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        System.out.println(); 
    }
    
    public static void showMenu() {
         System.out.println("╔════════════════════════════════════════════════════╗");
        System.out.println("║             🏥 HOSPITAL MANAGER 🏥                 ║");
        System.out.println("╠════════════════════════════════════════════════════╣");
        System.out.println("║ 1. Agregar Paciente                                ║");
        System.out.println("║ 2. Mostrar lista de pacientes en espera            ║");
        System.out.println("║ 3. Mostrar lista de pacientes por prioridad        ║");
        System.out.println("║ 4. Atender un paciente                             ║");
        System.out.println("║ 5. Mostrar registro del hospital                   ║");
        System.out.println("║ 6. Salir                                           ║");
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
