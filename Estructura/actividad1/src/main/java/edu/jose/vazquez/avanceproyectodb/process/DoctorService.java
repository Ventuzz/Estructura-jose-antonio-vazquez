package edu.jose.vazquez.avanceproyectodb.process;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DoctorService {

    public static class Doctor {
        public int id;
        public String nombre;
        public String correo;
        public String telefono;
    }

    public List<Doctor> listar(String filtro) {
        List<Doctor> out = new ArrayList<>();
        String sql = "SELECT DoctorID, Nombre, Correo, Telefono FROM Doctores " +
                     (filtro==null || filtro.isBlank()? "" : "WHERE Nombre LIKE ? OR Correo LIKE ? ") +
                     "ORDER BY DoctorID DESC";
        try (Connection con = Database.get();
             PreparedStatement ps = con.prepareStatement(sql)) {
            if (sql.contains("LIKE")) {
                String p = "%"+filtro+"%";
                ps.setString(1, p); ps.setString(2, p);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Doctor d = new Doctor();
                    d.id = rs.getInt("DoctorID");
                    d.nombre = rs.getString("Nombre");
                    d.correo = rs.getString("Correo");
                    d.telefono = rs.getString("Telefono");
                    out.add(d);
                }
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    public int crear(Doctor d) {
        String sql = "INSERT INTO Doctores(Nombre, Correo, Telefono) VALUES(?,?,?)";
        try (Connection con = Database.get();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, d.nombre);
            ps.setString(2, d.correo);
            ps.setString(3, d.telefono);
            ps.executeUpdate();
            int id = 0;
            try (ResultSet k = ps.getGeneratedKeys()) { if (k.next()) id = k.getInt(1); }
            // Auditoría INSERT
            auditInsert(con, "Doctores", id, toDoctorJson(d.nombre, d.correo, d.telefono));
            return id;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public void actualizar(Doctor d) {
        String sel = "SELECT Nombre, Correo, Telefono FROM Doctores WHERE DoctorID=?";
        String upd = "UPDATE Doctores SET Nombre=?, Correo=?, Telefono=? WHERE DoctorID=?";
        try (Connection con = Database.get()) {
            con.setAutoCommit(false);

            String prev = null;
            try (PreparedStatement ps = con.prepareStatement(sel)) {
                ps.setInt(1, d.id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) prev = toDoctorJson(rs.getString(1), rs.getString(2), rs.getString(3));
                }
            }
            try (PreparedStatement ps = con.prepareStatement(upd)) {
                ps.setString(1, d.nombre);
                ps.setString(2, d.correo);
                ps.setString(3, d.telefono);
                ps.setInt(4, d.id);
                ps.executeUpdate();
            }
            auditUpdate(con, "Doctores", d.id, prev, toDoctorJson(d.nombre, d.correo, d.telefono));
            con.commit();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    // --- Auditoría helpers ---
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
    private static String toDoctorJson(String nombre, String correo, String tel) {
        return String.format("{\"Nombre\":%s,\"Correo\":%s,\"Telefono\":%s}", j(nombre), j(correo), j(tel));
    }
    private static String j(String s){ return s==null? "null" : "\""+s.replace("\"","\\\"")+"\""; }
}
