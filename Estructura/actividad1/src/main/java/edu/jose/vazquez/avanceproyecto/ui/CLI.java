package edu.jose.vazquez.avanceproyecto.ui;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.sql.Date;
import edu.jose.vazquez.avanceproyecto.models.Pacientes;
import edu.jose.vazquez.avanceproyecto.process.HospitalManager;

public class CLI {
    static HospitalManager hospitalManager = new HospitalManager();
    static int i = 0;
    public static void runApp(){
        showLoadingBar();
        cleanScreen();
        showEmoji();
        hospitalManager.cargarPacientesDesdeBD();
        java.util.Scanner scanner = new java.util.Scanner(System.in);
        int option = -1;
        while (option != 8) {
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
                    System.out.println("❌ Por favor, ingrese un carácter válido, le recomiendo que ingrese un número del 1 al 8. ❌");
                    continue;
                }
                if (option >= 1 && option <= 8 ) {
                    break;
                }
                System.out.println("❌ Opción inválida. Por favor, seleccione una opción del 1 al 8. ❌");
            }

            switch (option) {
                case 1:
                    cleanScreen();
                    System.out.println("╔══════════════════════════════════════════════════╗");
                    System.out.println("║             AGREGAR NUEVO PACIENTE               ║");
                    System.out.println("╚══════════════════════════════════════════════════╝");
                    System.out.println("╔══════════════════════════════════════════════════╗");
                    System.out.println("║          ¿El paciente esta consciente?           ║");
                    System.out.println("╚══════════════════════════════════════════════════╝");
                    boolean inconsciente = false;
                    while (inconsciente == false) {
                        boolean consciente = false;
                        System.out.print("Ingrese 1 si está consciente o 0 si no: ");
                        String input = scanner.nextLine().trim();
                        
                        if (input.equals("1")) {
                            String nombre = "";
                            while (true) {
                                System.out.print("Ingrese el nombre del paciente: ");
                                nombre = scanner.nextLine().trim();
                                if (!nombre.isEmpty()) break;
                                System.out.println("❌ El nombre del paciente no puede estar vacío. ❌");
                            }

                            int prioridad = -1;
                            while (true) {
                                System.out.println("╔══════════════════════════════════════════════════╗");
                                System.out.println("║               ESTADO DEL PACIENTE                ║");
                                System.out.println("╚══════════════════════════════════════════════════╝");
                                System.out.println("╔══════════════════════════════════════════════════╗");
                                System.out.println("║  1. Crítico                                      ║");
                                System.out.println("║  2. Grave                                        ║");
                                System.out.println("║  3. Moderado                                     ║");
                                System.out.println("║  4. Leve                                         ║");
                                System.out.println("║  5. Estable                                      ║");
                                System.out.println("╚══════════════════════════════════════════════════╝");
                                System.out.print("Ingrese el número correspondiente al estado del paciente: ");
                                String prioridadInput = scanner.nextLine();
                                try {
                                    prioridad = Integer.parseInt(prioridadInput);
                                    if (prioridad >= 1 && prioridad <= 5) break;
                                    System.out.println("❌ Por favor, ingrese un estado válido (1..5). ❌");
                                } catch (NumberFormatException e) {
                                    System.out.println("❌ Por favor, ingrese un número válido. ❌");
                                }
                            }

                            LocalDate fechaNac = null;
                            while (true) {
                                System.out.println("╔══════════════════════════════════════════════════╗");
                                System.out.println("║           FECHA DE NACIMIENTO (AAAA-MM-DD)       ║");
                                System.out.println("╚══════════════════════════════════════════════════╝");
                                System.out.print("Ingrese la fecha de nacimiento: ");
                                String dob = scanner.nextLine().trim();
                                try {
                                    fechaNac = LocalDate.parse(dob); 
                                    if (fechaNac.isAfter(LocalDate.now())) {
                                        System.out.println("❌ La fecha no puede ser futura. ❌");
                                        continue;
                                    }
                                    break;
                                } catch (Exception e) {
                                    System.out.println("❌ Formato inválido. Ejemplo válido: 1998-11-30 ❌");
                                }
                            }

                            String sexo = null;
                            while (true) {
                                System.out.println("╔══════════════════════════════════════════════════╗");
                                System.out.println("║                      SEXO                        ║");
                                System.out.println("╚══════════════════════════════════════════════════╝");
                                System.out.print("Ingrese el sexo (M/F/O): ");
                                String sx = scanner.nextLine().trim().toUpperCase();
                                if (sx.equals("M") || sx.equals("F") || sx.equals("O")) {
                                    sexo = sx;
                                    break;
                                }
                                System.out.println("❌ Valor inválido. Use M (Masculino), F (Femenino) u O (Otro). ❌");
                            }

                            String enfermedad = "";
                            while (true) {
                                System.out.println("╔══════════════════════════════════════════════════╗");
                                System.out.println("║             DIAGNÓSTICO DEL PACIENTE             ║");
                                System.out.println("╚══════════════════════════════════════════════════╝");
                                System.out.print("Ingrese el diagnóstico del paciente: ");
                                enfermedad = scanner.nextLine().trim();
                                if (!enfermedad.isEmpty()) break;
                                System.out.println("❌ El diagnóstico no puede estar vacío. ❌");
                            }

                            int edad = Period.between(fechaNac, LocalDate.now()).getYears();

                            hospitalManager.addPaciente(nombre, edad, enfermedad, prioridad);

                            hospitalManager.guardarDatosPaciente(
                                nombre,
                                Date.valueOf(fechaNac),   
                                sexo
                            );

                            System.out.println("╔══════════════════════════════════════════════════╗");
                            System.out.println("║        ✅ PACIENTE AGREGADO EXITOSAMENTE ✅      ║");
                            System.out.println("╚══════════════════════════════════════════════════╝");
                            System.out.println("  💊 Nombre: " + nombre);
                            System.out.println("  💊 Fecha de nacimiento: " + fechaNac);
                            System.out.println("  💊 Sexo: " + sexo);
                            System.out.println("  💊 Edad: " + edad);
                            System.out.println("  💊 Diagnóstico: " + enfermedad + " 😷");
                            System.out.println("  💊 Prioridad: " + prioridad);
                            System.out.println();
                            System.out.print("Presione Enter para volver al menú principal...");
                            scanner.nextLine();
                            showLoadingBarMenu();
                            cleanScreen();
                            break;
                        } else if (input.equals("0")) {
                            consciente = false;
                            int prioridad = 1;
                            
                            String nombre = "Paciente inconsciente " + (++i);
                            
                            hospitalManager.addPaciente(nombre, 0, "En evaluación", prioridad);
                            hospitalManager.guardarDatosPaciente(
                                nombre,
                                Date.valueOf(LocalDate.now()),   
                                "No especificado"
                            );
                            break;
                        }
                        System.out.println("❌ Entrada inválida. ❌");
                        
                    }
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
                        } else {
                            System.out.println("❌ El nombre no puede estar vacío. ❌");
                        }
                    }
                    System.out.println();
                    System.out.print("Presione Enter para volver al menú principal...");
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
                    System.out.println("╔══════════════════════════════════════════════════╗");
                    System.out.println("║               BÚSQUEDA DE PACIENTES              ║");
                    System.out.println("╚══════════════════════════════════════════════════╝");
                    System.out.print("Ingrese el nombre del paciente a buscar: ");
                    String nombreBuscar = scanner.nextLine().trim();
                    hospitalManager.searchPaciente(nombreBuscar);
                    System.out.print("Presione Enter para volver al menú principal...");
                    scanner.nextLine();
                    showLoadingBarMenu();
                    cleanScreen();
                    break;
                case 7:
                    cleanScreen();
                    System.out.println("╔══════════════════════════════════════════════════════════╗");
                    System.out.println("║       ACTUALIZAR INFORMACIÓN DE UN PACIENTE EN ESPERA    ║");
                    System.out.println("╚══════════════════════════════════════════════════════════╝");
                    System.out.print("Ingrese el nombre del paciente a actualizar: ");
                    String nombreCambiar = scanner.nextLine().trim();
                    boolean pacienteEncontrado = hospitalManager.searchPaciente(nombreCambiar);

                    if (!pacienteEncontrado) {
                        System.out.println("❌ Paciente no encontrado. Regresando al menú principal. ❌");
                        System.out.print("Presione Enter para continuar...");
                        scanner.nextLine();
                        showLoadingBarMenu();
                        cleanScreen();
                        break;
                    }

                    System.out.print("¿Desea actualizar la información de este paciente? (s/n): ");
                    String confirm = scanner.nextLine().trim();
                    if (confirm.equalsIgnoreCase("s")) {
                        String nombre = "";
                        String fechaNacimiento = "";
                        String sexo = "";

                        System.out.print("Ingrese el nuevo nombre del paciente: ");
                        nombre = scanner.nextLine().trim();
                        if (nombre.isEmpty()) {
                            System.out.println("❌ El nombre no puede estar vacío. Cancelando la actualización. ❌");
                            System.out.print("Presione Enter para volver al menú principal...");
                            scanner.nextLine();
                            showLoadingBarMenu();
                            cleanScreen();
                            break;
                        }

                        System.out.print("Ingrese la nueva fecha de nacimiento del paciente (YYYY-MM-DD): ");
                        fechaNacimiento = scanner.nextLine().trim();
                        if (!fechaNacimiento.matches("\\d{4}-\\d{2}-\\d{2}")) {
                            System.out.println("❌ Formato de fecha inválido. Cancelando la actualización. ❌");
                            System.out.print("Presione Enter para volver al menú principal...");
                            scanner.nextLine();
                            showLoadingBarMenu();
                            cleanScreen();
                            break;
                        }

                        System.out.print("Ingrese el nuevo sexo del paciente (M/F/O): ");
                        sexo = scanner.nextLine().trim();
                        if (!sexo.matches("^(M|F|O)$")) {
                            System.out.println("❌ Sexo inválido. Cancelando la actualización. ❌");
                            System.out.print("Presione Enter para volver al menú principal...");
                            scanner.nextLine();
                            showLoadingBarMenu();
                            cleanScreen();
                            break;
                        }

                        hospitalManager.updatePaciente(nombreCambiar, nombre, fechaNacimiento, sexo);

                    } else {
                        System.out.println("Actualización cancelada, regreso al menú principal.");
                        showLoadingBarMenu();
                        cleanScreen();
                        break;
                    }

                    System.out.print("Presione Enter para volver al menú principal...");
                    scanner.nextLine();
                    showLoadingBarMenu();
                    cleanScreen();
                    break;
                case 8:
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
            try { Thread.sleep(60); } catch (InterruptedException e) { e.printStackTrace(); }
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
            try { Thread.sleep(15); } catch (InterruptedException e) { e.printStackTrace(); }
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
        System.out.println("║ 6. Buscar Paciente                                 ║");
        System.out.println("║ 7. Cambiar información de un paciente en espera    ║");
        System.out.println("║ 8. Salir                                           ║");
        System.out.println("╚════════════════════════════════════════════════════╝");
    }

    public static void showEmoji(){
        try {
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
