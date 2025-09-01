package edu.jose.vazquez.avanceproyectodb.models;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import edu.jose.vazquez.avanceproyectodb.process.Database;

public class AtencionService {

    public static class Atencion {
        public long atencionId;
        public int pacienteId;
        public Integer doctorId;
        public String diagnostico;
        public String tratamiento;
        public Timestamp fechaRegistro;
        public Timestamp alta;
    }

    /**
     * Firma compatible con tu UI anterior.
     * prioridad/estado ahora actualizan la tabla Pacientes.
     */
    public long registrar(int pacienteId, int doctorId, int prioridad, String estado, String dx, String tx) {
        if (dx==null || dx.isBlank()) throw new IllegalArgumentException("El diagnóstico es obligatorio.");

        String selPrev = "SELECT Estado, Prioridad FROM Pacientes WHERE PacienteID=?";
        String updPac  = "UPDATE Pacientes SET Estado=?, Prioridad=? WHERE PacienteID=?";
        String insAt   = "INSERT INTO Atenciones(AtencionID, PacienteID, DoctorID, Diagnostico, Tratamiento, FechaRegistro) " +
                         "VALUES(?,?,?,?,?,NOW())";

        try (Connection con = Database.get()) {
            con.setAutoCommit(false);

            // 1) Pre-estado para auditoría
            String prevJson = null;
            try (PreparedStatement ps = con.prepareStatement(selPrev)) {
                ps.setInt(1, pacienteId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        prevJson = String.format("{\"Estado\":%s,\"Prioridad\":%d}",
                                j(rs.getString(1)), rs.getInt(2));
                    }
                }
            }

            // 2) Actualiza Pacientes.Estado/Prioridad
            try (PreparedStatement ps = con.prepareStatement(updPac)) {
                ps.setString(1, (estado==null||estado.isBlank())? "En evaluacion" : estado);
                ps.setInt(2, prioridad<=0? 3 : prioridad);
                ps.setInt(3, pacienteId);
                ps.executeUpdate();
            }

            // Auditoría UPDATE Pacientes
            String nextJson = String.format("{\"Estado\":%s,\"Prioridad\":%d}",
                    j((estado==null||estado.isBlank())? "En evaluacion" : estado),
                    prioridad<=0? 3 : prioridad);
            auditUpdate(con, "Pacientes", pacienteId, prevJson, nextJson);

            // 3) Inserta Atención (AtencionID generado en app)
            long atencionId = System.currentTimeMillis();
            try (PreparedStatement ps = con.prepareStatement(insAt)) {
                ps.setLong(1, atencionId);
                ps.setInt(2, pacienteId);
                ps.setInt(3, doctorId);
                ps.setString(4, dx);
                ps.setString(5, tx);
                ps.executeUpdate();
            }

            // Auditoría INSERT Atenciones
            String attJson = String.format("{\"AtencionID\":%d,\"PacienteID\":%d,\"DoctorID\":%d," +
                            "\"Diagnostico\":%s,\"Tratamiento\":%s}", atencionId, pacienteId, doctorId,
                    j(dx), j(tx));
            auditInsert(con, "Atenciones", (int)atencionId, attJson); // RegistroID es int en historial; cabe si usas AUTO_INCREMENT. Si mantienes BIGINT, puedes truncar o cambiar a BIGINT en historial.

            con.commit();
            return atencionId;
        } catch (SQLException e) {
            throw new RuntimeException("Error registrando atención", e);
        }
    }

    public List<Atencion> listarPorPaciente(int pacienteId) {
        String sql = "SELECT AtencionID, PacienteID, DoctorID, Diagnostico, Tratamiento, FechaRegistro, Alta " +
                     "FROM Atenciones WHERE PacienteID=? ORDER BY FechaRegistro DESC";
        List<Atencion> out = new ArrayList<>();
        try (Connection con = Database.get(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, pacienteId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Atencion a = new Atencion();
                    a.atencionId = rs.getLong(1);
                    a.pacienteId = rs.getInt(2);
                    int d = rs.getInt(3);
                    a.doctorId = rs.wasNull()? null : d;
                    a.diagnostico = rs.getString(4);
                    a.tratamiento = rs.getString(5);
                    a.fechaRegistro = rs.getTimestamp(6);
                    a.alta = rs.getTimestamp(7);
                    out.add(a);
                }
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    public boolean darDeAltaUltima(int pacienteId) {
        String select = "SELECT AtencionID FROM Atenciones WHERE PacienteID=? ORDER BY FechaRegistro DESC LIMIT 1";
        String updAtt = "UPDATE Atenciones SET Alta=NOW() WHERE AtencionID=?";
        String updPac = "UPDATE Pacientes SET Estado='Atendido' WHERE PacienteID=?";

        try (Connection con = Database.get()) {
            con.setAutoCommit(false);

            Long atencionId = null;
            try (PreparedStatement ps = con.prepareStatement(select)) {
                ps.setInt(1, pacienteId);
                try (ResultSet rs = ps.executeQuery()) { if (rs.next()) atencionId = rs.getLong(1); }
            }
            if (atencionId == null) { con.rollback(); return false; }

            try (PreparedStatement ps = con.prepareStatement(updAtt)) {
                ps.setLong(1, atencionId);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = con.prepareStatement(updPac)) {
                ps.setInt(1, pacienteId);
                ps.executeUpdate();
            }

            // Auditoría UPDATE Atenciones + Pacientes
            auditUpdate(con, "Atenciones", atencionId.intValue(), "{\"Alta\":null}", "{\"Alta\":\"NOW()\"}");
            auditUpdate(con, "Pacientes", pacienteId, "{\"Estado\":\"(prev)\"}", "{\"Estado\":\"Atendido\"}");

            con.commit();
            return true;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    // ---- Helpers auditoría ----
    private void auditInsert(Connection con, String tabla, int id, String json) throws SQLException {
        String ins = "INSERT INTO historial_cambios(TablaAfectada, RegistroID, Accion, Usuario, DatosAnteriores, DatosNuevos) " +
                     "VALUES(?, ?, 'INSERT', USER(), NULL, CAST(? AS JSON))";
        try (PreparedStatement ps = con.prepareStatement(ins)) {
            ps.setString(1, tabla);
            ps.setInt(2, id);
            ps.setString(3, json);
            ps.executeUpdate();
        }
    }

    private void auditUpdate(Connection con, String tabla, int id, String prevJson, String nextJson) throws SQLException {
        String ins = "INSERT INTO historial_cambios(TablaAfectada, RegistroID, Accion, Usuario, DatosAnteriores, DatosNuevos) " +
                     "VALUES(?, ?, 'UPDATE', USER(), CAST(? AS JSON), CAST(? AS JSON))";
        try (PreparedStatement ps = con.prepareStatement(ins)) {
            ps.setString(1, tabla);
            ps.setInt(2, id);
            ps.setString(3, prevJson);
            ps.setString(4, nextJson);
            ps.executeUpdate();
        }
    }

    private static String j(String s){ return s==null? "null" : "\""+s.replace("\"","\\\"")+"\""; }
}
