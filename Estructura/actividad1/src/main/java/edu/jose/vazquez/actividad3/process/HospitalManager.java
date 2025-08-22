package edu.jose.vazquez.actividad3.process;
import java.util.LinkedList;
import java.util.Queue;

import edu.jose.vazquez.actividad3.models.Pacientes;

public class HospitalManager {
    private Queue<Pacientes> listaPacientes=new LinkedList<>();
    private Queue<Pacientes>[] pacientesPrioridad;


    public HospitalManager() {
        listaPacientes = new LinkedList<>();
        pacientesPrioridad = new LinkedList[6]; 
        addPaciente("Juan Perez", 30, "Gripe", 2);
        addPaciente("Maria Lopez", 25, "Fractura", 1);
        addPaciente("Pedro Gonzalez", 40, "Dolor de cabeza", 3);
        addPaciente("Ana Torres", 35, "Alergia", 4);
        addPaciente("Luis Ramirez", 28, "Infección", 2);
        addPaciente("Carla Jimenez", 50, "Fractura", 1);
        addPaciente("Javier Martinez", 45, "Dolor de espalda", 3);
        addPaciente("Sofia Rodriguez", 32, "Alergia", 4);
        addPaciente("Diego Torres", 29, "Gripe", 2);
        addPaciente("Christian Sahid", 20, "Mocos unu", 1);

        for (int i = 1; i <= 5; i++) {
            pacientesPrioridad[i] = new LinkedList<>();
        }
    }

    public void addPaciente(String nombre, int edad, String enfermedad, int prioridad) {
        Pacientes paciente = new Pacientes(nombre, edad, enfermedad, prioridad);
        listaPacientes.add(paciente);
    }


    public void showPacientes(){
        if (listaPacientes.isEmpty()) {
            System.out.println("No hay pacientes en la lista.");
            return;
        }
        System.out.println("╔══════════════════════════════════════════════════╗");
        System.out.println("║               LISTA DE PACIENTES                 ║");
        System.out.println("╚══════════════════════════════════════════════════╝");
        for (Pacientes paciente : listaPacientes) {
            System.out.println("╭──────────────────────────────╮");
            System.out.println("│  Nombre: " + paciente.getNombre() + "\n" + "│  Edad: " + paciente.getEdad() + "\n" + "│  Enfermedad 💊 : " + paciente.getEnfermedad() + "\n" + "│  Prioridad: " + paciente.getPrioridad() );
            System.out.println("╰──────────────────────────────╯");
        }
    }


    public void sortPacientesPorPrioridad(){
        while (!listaPacientes.isEmpty()) {
            Pacientes paciente = listaPacientes.poll();
            int prioridad = paciente.getPrioridad();
            if (prioridad >= 1 && prioridad <= 5) {
                pacientesPrioridad[prioridad].add(paciente);
            }
        }

        System.out.println("╔══════════════════════════════════════════════════╗");
        System.out.println("║           LISTA DE PACIENTES POR PRIORIDAD       ║");
        System.out.println("╚══════════════════════════════════════════════════╝");
        for (int i = 1; i <= 5; i++) {
            System.out.println("╔═════════════════════════════════════════════╗");
            System.out.println("║               PRIORIDAD " + i + "                   ║");
            System.out.println("╚═════════════════════════════════════════════╝");
            if (pacientesPrioridad[i].isEmpty()) {
                System.out.println("No hay pacientes con esta prioridad.");
            } else {
                for (Pacientes paciente : pacientesPrioridad[i]) {
                    System.out.println("╭──────────────────────────────╮");
                    System.out.println("│  Nombre: " + paciente.getNombre() + "\n" + "│  Edad: " + paciente.getEdad() + "\n" + "│  Enfermedad💊 : " + paciente.getEnfermedad());
                    System.out.println("╰──────────────────────────────╯");
                }
            }
        }
    }

}
