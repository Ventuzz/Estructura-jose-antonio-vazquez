package edu.jose.vazquez.avanceproyectodb.models;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import edu.jose.vazquez.avanceproyectodb.process.Database;

public class PacienteService {

    public static class Paciente {
        public int id;
        public String nombre;          // Nombre completo para la UI (Nombre + Apellido)
        public String apellido;        // opcional
        public String estado;          // 'En espera' | 'Atendido' | 'En evaluacion'
        public int prioridad;          // 1-5
        public String fechaNacimiento; // VARCHAR(30)
        public String sexo;
        public String telefono;        // string en UI, INT en BD
        public String correo;
    }

    public List<Paciente> listar(String filtro) {
        String sql = "SELECT PacienteID, Nombre, Apellido, Sexo, Telefono, Correo, FechaNacimiento, Estado, Prioridad " +
                     "FROM Pacientes " +
                     (filtro==null || filtro.isBlank()? "" : "WHERE CONCAT(Nombre,' ',Apellido) LIKE ? ") +
                     "ORDER BY PacienteID DESC";
        List<Paciente> out = new ArrayList<>();
        try (Connection con = Database.get();
             PreparedStatement ps = con.prepareStatement(sql)) {
            if (sql.contains("LIKE")) ps.setString(1, "%"+filtro+"%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Paciente p = new Paciente();
                    p.id = rs.getInt("PacienteID");
                    String n = rs.getString("Nombre");
                    String a = rs.getString("Apellido");
                    p.nombre = (n==null? "" : n) + (a==null||a.isBlank()? "" : " " + a);
                    p.sexo = rs.getString("Sexo");
                    Object telObj = rs.getObject("Telefono");
                    p.telefono = telObj==null? "" : String.valueOf(rs.getInt("Telefono"));
                    p.correo = rs.getString("Correo");
                    p.fechaNacimiento = rs.getString("FechaNacimiento");
                    p.estado = rs.getString("Estado");
                    p.prioridad = rs.getInt("Prioridad");
                    out.add(p);
                }
            }
        } catch (SQLException e) { throw new RuntimeException("Error listando pacientes", e); }
        return out;
    }

    public int crear(Paciente p) {
        // Derivar Nombre/Apellido si solo llega nombre completo
        String nombre, apellido;
        if (p.apellido != null && !p.apellido.isBlank()) {
            nombre = safe(p.nombre); apellido = safe(p.apellido);
        } else {
            String full = safe(p.nombre);
            int idx = full==null? -1 : full.trim().lastIndexOf(' ');
            if (idx > 0) { nombre = full.substring(0, idx).trim(); apellido = full.substring(idx+1).trim(); }
            else { nombre = full; apellido = "N/A"; }
        }
        String estado = (p.estado==null || p.estado.isBlank()) ? "En espera" : p.estado;
        int prioridad = (p.prioridad<=0) ? 3 : p.prioridad;

        String sql = "INSERT INTO Pacientes(Nombre, Apellido, Estado, Prioridad, FechaNacimiento, Sexo, Telefono, Correo) " +
                     "VALUES(?,?,?,?,?,?,?,?)";
        try (Connection con = Database.get();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, nombre);
            ps.setString(2, apellido);
            ps.setString(3, estado);
            ps.setInt(4, prioridad);
            if (isBlank(p.fechaNacimiento)) ps.setNull(5, Types.VARCHAR); else ps.setString(5, p.fechaNacimiento);
            ps.setString(6, p.sexo);
            if (isBlank(p.telefono)) ps.setNull(7, Types.INTEGER); else ps.setInt(7, Integer.parseInt(p.telefono));
            ps.setString(8, p.correo);
            ps.executeUpdate();

            int id = 0;
            try (ResultSet k = ps.getGeneratedKeys()) { if (k.next()) id = k.getInt(1); }

            // Auditoría INSERT
            auditInsert(con, "Pacientes", id,
                toPacienteJson(nombre, apellido, estado, prioridad, p.fechaNacimiento, p.sexo, p.telefono, p.correo));

            return id;
        } catch (SQLException e) { throw new RuntimeException("Error creando paciente", e); }
    }
    // Comprueba si el paciente ya está "Atendido" (equivale a dado de alta)
    public boolean ultimoEstadoEsAlta(int pacienteId) {
        String sql = "SELECT Estado FROM Pacientes WHERE PacienteID=?";
        try (java.sql.Connection con = Database.get();
            java.sql.PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, pacienteId);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String estado = rs.getString(1);
                    return "Atendido".equalsIgnoreCase(estado);
                }
            }
        } catch (java.sql.SQLException e) {
            throw new RuntimeException("Error consultando estado del paciente", e);
        }
        return false; // si no existe el paciente o no hay estado, no bloquea
    }

    /** Actualiza datos básicos y registra auditoría */
    public void actualizarBasico(Paciente p) {
        String select = "SELECT Nombre, Apellido, Estado, Prioridad, FechaNacimiento, Sexo, Telefono, Correo " +
                        "FROM Pacientes WHERE PacienteID=?";
        String update = "UPDATE Pacientes SET Nombre=?, Apellido=?, Estado=?, Prioridad=?, " +
                        "FechaNacimiento=?, Sexo=?, Telefono=?, Correo=? WHERE PacienteID=?";
        try (Connection con = Database.get()) {
            con.setAutoCommit(false);

            // JSON anterior
            String prevJSON = null;
            try (PreparedStatement psSel = con.prepareStatement(select)) {
                psSel.setInt(1, p.id);
                try (ResultSet rs = psSel.executeQuery()) {
                    if (rs.next()) {
                        prevJSON = toPacienteJson(rs.getString(1), rs.getString(2), rs.getString(3),
                                rs.getInt(4), rs.getString(5), rs.getString(6),
                                rs.getObject(7)==null? null : Integer.toString(rs.getInt(7)),
                                rs.getString(8));
                    }
                }
            }

            // Derivar nombre/apellido si no vienen separados
            String nombre, apellido;
            if (p.apellido != null && !p.apellido.isBlank()) {
                nombre = safe(p.nombre); apellido = safe(p.apellido);
            } else {
                String full = safe(p.nombre);
                int idx = full==null? -1 : full.trim().lastIndexOf(' ');
                if (idx > 0) { nombre = full.substring(0, idx).trim(); apellido = full.substring(idx+1).trim(); }
                else { nombre = full; apellido = "N/A"; }
            }

            try (PreparedStatement ps = con.prepareStatement(update)) {
                ps.setString(1, nombre);
                ps.setString(2, apellido);
                ps.setString(3, isBlank(p.estado) ? "En espera" : p.estado);
                ps.setInt(4, p.prioridad<=0? 3 : p.prioridad);
                if (isBlank(p.fechaNacimiento)) ps.setNull(5, Types.VARCHAR); else ps.setString(5, p.fechaNacimiento);
                ps.setString(6, p.sexo);
                if (isBlank(p.telefono)) ps.setNull(7, Types.INTEGER); else ps.setInt(7, Integer.parseInt(p.telefono));
                ps.setString(8, p.correo);
                ps.setInt(9, p.id);
                ps.executeUpdate();
            }

            String nextJSON = toPacienteJson(nombre, apellido,
                    isBlank(p.estado) ? "En espera" : p.estado,
                    p.prioridad<=0? 3 : p.prioridad,
                    p.fechaNacimiento, p.sexo, p.telefono, p.correo);

            auditUpdate(con, "Pacientes", p.id, prevJSON, nextJSON);
            con.commit();
        } catch (SQLException e) { throw new RuntimeException("Error actualizando paciente", e); }
    }

    // ===== Helpers auditoría =====
    private void auditInsert(Connection con, String tabla, long id, String json) throws SQLException {
        String ins = "INSERT INTO historial_cambios(TablaAfectada, RegistroID, Accion, Usuario, DatosAnteriores, DatosNuevos) " +
                     "VALUES(?, ?, 'INSERT', USER(), NULL, CAST(? AS JSON))";
        try (PreparedStatement ps = con.prepareStatement(ins)) {
            ps.setString(1, tabla);
            ps.setLong(2, id);
            ps.setString(3, json);
            ps.executeUpdate();
        }
    }

    private void auditUpdate(Connection con, String tabla, long id, String prevJson, String nextJson) throws SQLException {
        String ins = "INSERT INTO historial_cambios(TablaAfectada, RegistroID, Accion, Usuario, DatosAnteriores, DatosNuevos) " +
                     "VALUES(?, ?, 'UPDATE', USER(), CAST(? AS JSON), CAST(? AS JSON))";
        try (PreparedStatement ps = con.prepareStatement(ins)) {
            ps.setString(1, tabla);
            ps.setLong(2, id);
            ps.setString(3, prevJson);
            ps.setString(4, nextJson);
            ps.executeUpdate();
        }
    }

    private String toPacienteJson(String nombre, String apellido, String estado, int prioridad,
                                  String fechaNac, String sexo, String tel, String correo) {
        return String.format("{\"Nombre\":%s,\"Apellido\":%s,\"Estado\":%s,\"Prioridad\":%d," +
                             "\"FechaNacimiento\":%s,\"Sexo\":%s,\"Telefono\":%s,\"Correo\":%s}",
                j(nombre), j(apellido), j(estado), prioridad, j(fechaNac), j(sexo), j(tel), j(correo));
    }

    private static String safe(String s){ return (s==null||s.isBlank())? null : s; }
    private static boolean isBlank(String s){ return s==null || s.isBlank(); }
    private static String j(String s){ return s==null? "null" : "\""+s.replace("\"","\\\"")+"\""; }
}
