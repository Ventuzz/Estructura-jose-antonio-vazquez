package edu.jose.vazquez.avanceproyecto.process;

import edu.jose.vazquez.avanceproyecto.models.Pacientes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class HospitalManager {
    private HashMap<String, Pacientes> salaEspera;
    private List<Pacientes> pacientesAtendidos;
    private List<Pacientes> registroPacientes;
    private List<String> tratamientosDisponibles = List.of(
        "Analgésicos cada 8 horas",
        "Antibióticos cada 12 horas",
        "Antiinflamatorios cada 8 horas",
        "Antihistamínicos cada 24 horas",
        "Antipiréticos cada 6 horas",
        "Antisépticos cada 8 horas",
        "Broncodilatadores cada 12 horas",
        "Corticosteroides cada 8 horas",
        "Diuréticos cada 12 horas",
        "Inmunosupresores cada 24 horas"
    );
    private List<String> doctoresDisponibles = List.of(
        "Dr. Smith",
        "Dr. Johnson",
        "Dr. Williams",
        "Dr. House",
        "Dr. Jones"
    );

    public HospitalManager() {
        salaEspera = new HashMap<>();
        pacientesAtendidos = new ArrayList<>();
        registroPacientes = new ArrayList<>();
        addPaciente("Juan Perez", 30, "Gripe", 2);
        addPaciente("Maria Lopez", 25, "Fractura", 1);
        addPaciente("Carlos Sanchez", 40, "Dolor de cabeza", 3);

    }

    public void addPaciente(String nombre, int edad, String enfermedad, int prioridad) {
        Pacientes paciente = new Pacientes(nombre, edad, enfermedad, prioridad);
        salaEspera.put(paciente.getNombre(), paciente);
        registroPacientes.add(paciente);
    }

    public Pacientes attendPaciente(String nombre) {
        Pacientes paciente= null;
        for (String key : salaEspera.keySet()) {
            if (key.equalsIgnoreCase(nombre)) {
                paciente = salaEspera.get(key);
                break;
            }
        }

        if (paciente != null) {
            salaEspera.remove(paciente.getNombre());
            pacientesAtendidos.add(paciente);
            paciente.setEstado("Atendido");
            String tratamiento = tratamientosDisponibles.get(new Random().nextInt(tratamientosDisponibles.size()));
            paciente.setTratamiento(tratamiento);
            String doctor = doctoresDisponibles.get(new Random().nextInt(doctoresDisponibles.size()));
            paciente.setDoctor(doctor);
            return paciente;
        } else {
            return null;
            }
    }



    public List<Pacientes> listarPacientesAtendidos() {
        return new ArrayList<>(pacientesAtendidos);
    }

    public void searchPaciente(String nombre) {
        Pacientes paciente = null;
        for (String key : salaEspera.keySet()) {
            if (key.equalsIgnoreCase(nombre)) {
                paciente = salaEspera.get(key);
                break;
            }
        }
        if (paciente != null) {
            System.out.println("Paciente encontrado: " + paciente.getNombre());
        } else {
            System.out.println("Paciente no encontrado.");
        }
    }

    public void showPacientesEnEspera() {
        if (salaEspera.isEmpty()) {
            System.out.println("No hay pacientes en la sala de espera.");
            return;
        }
        for (Pacientes paciente : salaEspera.values()) {
            System.out.println("- " + paciente.getNombre());
        }
    }

    public void showRegistroPacientes() {
        if (registroPacientes.isEmpty()) {
            System.out.println("No hay pacientes en el registro.");
            return;
        }
        for (Pacientes paciente : registroPacientes) {
            System.out.println("╭──────────────────────────────╮");
            System.out.println("│ " + paciente.getNombre() + "\n" + "│ Estado: " + paciente.getEstado()+ "\n" + "│ Tratamiento: " + paciente.getTratamiento()+ "\n" + "│ Doctor: " + paciente.getDoctor());
            System.out.println("╰──────────────────────────────╯");
        }
    }

    public void sortPacientesPorPrioridad() {
        List<Pacientes> pacientesList = new ArrayList<>(salaEspera.values());
        pacientesList.sort((p1, p2) -> Integer.compare(p1.getPrioridad(), p2.getPrioridad()));
        
        for (Pacientes paciente : pacientesList) {
            System.out.println("╭───────────────────╮");
            System.out.println("│ " + paciente.getNombre() +"\n" + "│ Prioridad: " + paciente.getPrioridad() + "      │");
            System.out.println("╰───────────────────╯");
        }
    }

    

}
