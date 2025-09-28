package edu.jose.vazquez.avanceproyectodb.models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import edu.jose.vazquez.avanceproyectodb.process.Database;

public class PacienteService {

    public static class Paciente {
        public int id;
        public String nombreCompleto;
        public String estado;
        public int prioridad;
        public String fechaNacimiento;
        public String sexo;
        public String telefono;
        public String correo;
    }

        public static void marcarAtendido(int pacienteId) {
        final String sql = "UPDATE pacientes SET estado = 'ATENDIDO' WHERE paciente_id = ?";
        try (Connection con = Database.get();
            PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, pacienteId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error marcando paciente como ATENDIDO", e);
        }
    }

    public List<Paciente> listar(String filtro) {
        String sql = "SELECT paciente_id, nombre_completo, sexo, telefono, email, fecha_nacimiento, estado, prioridad " +
                     "FROM pacientes " +
                     (filtro == null || filtro.isBlank() ? "" : "WHERE nombre_completo LIKE ? ") +
                     "ORDER BY paciente_id DESC";
        List<Paciente> out = new ArrayList<>();
        try (Connection con = Database.get();
             PreparedStatement ps = con.prepareStatement(sql)) {
            if (sql.contains("LIKE")) ps.setString(1, "%" + filtro + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Paciente p = new Paciente();
                    p.id = rs.getInt("paciente_id");
                    p.nombreCompleto = rs.getString("nombre_completo");
                    p.sexo = rs.getString("sexo");
                    p.telefono = rs.getString("telefono");
                    p.correo = rs.getString("email");
                    p.fechaNacimiento = rs.getString("fecha_nacimiento");
                    p.estado = rs.getString("estado");
                    p.prioridad = rs.getInt("prioridad");
                    out.add(p);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error listando pacientes", e);
        }
        return out;
    }

    public int crear(Paciente p) {
        String estado = (p.estado == null || p.estado.isBlank()) ? "EN_ESPERA" : p.estado;
        int prioridad = (p.prioridad <= 0) ? 3 : p.prioridad;

        String sql = "INSERT INTO pacientes(nombre_completo, estado, prioridad, fecha_nacimiento, sexo, telefono, email) " +
                     "VALUES(?,?,?,?,?,?,?)";
        try (Connection con = Database.get();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, p.nombreCompleto);
            ps.setString(2, estado);
            ps.setInt(3, prioridad);
            ps.setString(4, p.fechaNacimiento);
            ps.setString(5, p.sexo);
            ps.setString(6, p.telefono);
            ps.setString(7, p.correo);
            ps.executeUpdate();

            try (ResultSet k = ps.getGeneratedKeys()) {
                if (k.next()) return k.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error creando paciente", e);
        }
    }
    
    public boolean ultimoEstadoEsAlta(int pacienteId) {
        String sql = "SELECT estado FROM pacientes WHERE paciente_id=?";
        try (Connection con = Database.get();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, pacienteId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String estado = rs.getString(1);
                    return "ATENDIDO".equalsIgnoreCase(estado);
                }
            }
        } catch (java.sql.SQLException e) {
            throw new RuntimeException("Error consultando estado del paciente", e);
        }
        return false;
    }

    public void actualizarBasico(Paciente p) {
        String update = "UPDATE pacientes SET nombre_completo=?, estado=?, prioridad=?, " +
                        "fecha_nacimiento=?, sexo=?, telefono=?, email=? WHERE paciente_id=?";
        try (Connection con = Database.get(); PreparedStatement ps = con.prepareStatement(update)) {
            ps.setString(1, p.nombreCompleto);
            ps.setString(2, p.estado);
            ps.setInt(3, p.prioridad);
            ps.setString(4, p.fechaNacimiento);
            ps.setString(5, p.sexo);
            ps.setString(6, p.telefono);
            ps.setString(7, p.correo);
            ps.setInt(8, p.id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error actualizando paciente", e);
        }
    }
}

