package edu.jose.vazquez.actividad3.models;

public class Pacientes {
    private String nombre;
    private int edad;
    private String enfermedad;
    private int prioridad;

    public Pacientes(String nombre, int edad, String enfermedad, int prioridad) {
        this.nombre = nombre;
        this.edad = edad;
        this.enfermedad = enfermedad;
        this.prioridad = prioridad;
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
}
