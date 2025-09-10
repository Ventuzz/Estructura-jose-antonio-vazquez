package edu.jose.vazquez.avanceproyectodb.process;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DoctorService {

    // Clase interna para representar una Especialidad
    public static class Especialidad {
        public int id;
        public String nombre;

        @Override
        public String toString() {
            return nombre; // Esto permite que el JComboBox muestre el nombre
        }
    }

    public static class Doctor {
        public int id;
        public String nombre;
        public String correo;
        public String telefono;
        public int idEspecialidad;
        public String nombreEspecialidad; // Para mostrar en la tabla

         @Override
        public String toString() {
            return nombre; // Usado en el JComboBox de Atenciones
        }
    }

    /**
     * Obtiene una lista de todas las especialidades de la base de datos.
     */
    public List<Especialidad> listarEspecialidades() {
        List<Especialidad> out = new ArrayList<>();
        String sql = "SELECT id_especialidad, especialidad FROM especialidad ORDER BY especialidad";
        try (Connection con = Database.get();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Especialidad e = new Especialidad();
                e.id = rs.getInt("id_especialidad");
                e.nombre = rs.getString("especialidad");
                out.add(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error listando especialidades", e);
        }
        return out;
    }

    public List<Doctor> listar(String filtro) {
        List<Doctor> out = new ArrayList<>();
        String sql = "SELECT d.doctor_id, d.nombre, d.correo, d.telefono, e.especialidad, d.id_especialidad " +
                     "FROM doctores d JOIN especialidad e ON d.id_especialidad = e.id_especialidad " +
                     (filtro == null || filtro.isBlank() ? "" : "WHERE d.nombre LIKE ? OR d.correo LIKE ? OR e.especialidad LIKE ? ") +
                     "ORDER BY d.doctor_id DESC";
        try (Connection con = Database.get(); PreparedStatement ps = con.prepareStatement(sql)) {
            if (sql.contains("LIKE")) {
                String p = "%" + filtro + "%";
                ps.setString(1, p);
                ps.setString(2, p);
                ps.setString(3, p);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Doctor d = new Doctor();
                    d.id = rs.getInt("doctor_id");
                    d.nombre = rs.getString("nombre");
                    d.correo = rs.getString("correo");
                    d.telefono = rs.getString("telefono");
                    d.nombreEspecialidad = rs.getString("especialidad");
                    d.idEspecialidad = rs.getInt("id_especialidad");
                    out.add(d);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error listando doctores", e);
        }
        return out;
    }
    
    /**
    * Obtiene doctores filtrando por especialidad para el panel de atención.
    */
    public List<Doctor> listarPorEspecialidad(int especialidadId) {
        List<Doctor> out = new ArrayList<>();
        String sql = "SELECT doctor_id, nombre FROM doctores WHERE id_especialidad = ? ORDER BY nombre";
        try (Connection con = Database.get(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, especialidadId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Doctor d = new Doctor();
                    d.id = rs.getInt("doctor_id");
                    d.nombre = rs.getString("nombre");
                    out.add(d);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error listando doctores por especialidad", e);
        }
        return out;
    }


    public int crear(Doctor d) {
        String sql = "INSERT INTO doctores(nombre, correo, telefono, id_especialidad) VALUES(?,?,?,?)";
        try (Connection con = Database.get();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, d.nombre);
            ps.setString(2, d.correo);
            ps.setString(3, d.telefono);
            ps.setInt(4, d.idEspecialidad);
            ps.executeUpdate();
            try (ResultSet k = ps.getGeneratedKeys()) {
                if (k.next()) return k.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error creando doctor",e);
        }
    }

    public void actualizar(Doctor d) {
        String upd = "UPDATE doctores SET nombre=?, correo=?, telefono=?, id_especialidad=? WHERE doctor_id=?";
        try (Connection con = Database.get(); PreparedStatement ps = con.prepareStatement(upd)) {
            ps.setString(1, d.nombre);
            ps.setString(2, d.correo);
            ps.setString(3, d.telefono);
            ps.setInt(4, d.idEspecialidad);
            ps.setInt(5, d.id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error actualizando doctor", e);
        }
    }
}
