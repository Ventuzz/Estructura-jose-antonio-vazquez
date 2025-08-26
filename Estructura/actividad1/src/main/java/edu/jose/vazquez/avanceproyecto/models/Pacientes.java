package edu.jose.vazquez.avanceproyecto.models;

public class Pacientes {
    private String nombre;
    private int edad;
    private String enfermedad;
    private String estado;
    private int prioridad;
    private String tratamiento;
    private String doctor;

    public Pacientes(String nombre, int edad, String enfermedad, int prioridad) {
        this.nombre = nombre;
        this.edad = edad;
        this.enfermedad = enfermedad;
        this.prioridad = prioridad;
        this.tratamiento = "Esperando diagnóstico";
        this.estado = "En espera";
        this.doctor = "No asignado";
    }

    public String getEstado() {
        return estado;
    }

    public String getDoctor() {
        return doctor;
    }

    public void setDoctor(String doctor) {
        this.doctor = doctor;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getNombre() {
        return nombre;
    }

    public int getPrioridad() {
        return prioridad;
    }

    public int getEdad() {
        return edad;
    }

    public String getEnfermedad() {
        return enfermedad;
    }

    public String getTratamiento() {
        return tratamiento;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public void setEnfermedad(String enfermedad) {
        this.enfermedad = enfermedad;
    }

    public void setPrioridad(int prioridad) {
        this.prioridad = prioridad;
    }

    public void setTratamiento(String tratamiento) {
        this.tratamiento = tratamiento;
    }
}
