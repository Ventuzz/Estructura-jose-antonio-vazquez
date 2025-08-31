package edu.jose.vazquez.avanceproyecto.models;

import java.sql.Date;

public class Pacientes {
    private String nombre;
    private int edad;
    private String enfermedad;
    private String estado;
    private int prioridad;
    private String tratamiento;
    private String doctor;
    private Date fechaIngreso;
    private Date fechaNacimiento;
    private Date fechaAlta;
    private String sexo;
    

    public Pacientes(String nombre, int edad, String enfermedad, int prioridad) {
        this.nombre = nombre;
        this.edad = edad;
        this.enfermedad = enfermedad;
        this.prioridad = prioridad;
        this.tratamiento = "Esperando diagnóstico";
        this.estado = "En espera";
        this.doctor = "No asignado";
        this.fechaIngreso = new Date(System.currentTimeMillis());
        this.fechaNacimiento = null;
        this.fechaAlta = null;
        this.sexo = "No especificado";
    }

    public String getEstado() {
        return estado;
    }
    public Date getFechaIngreso() {
        return fechaIngreso;
    }

    public Date getFechaAlta() {
        return fechaAlta;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public void setFechaIngreso(Date fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public void setFechaAlta(Date fechaAlta) {
        this.fechaAlta = fechaAlta;
    }

    public Date getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(Date fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
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
