package edu.jose.vazquez.avanceproyectodb.models;

import java.sql.*;
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

    public void registrar(int pacienteId, int doctorId, int prioridad, String estado, String dx, String tx) {
        if (dx == null || dx.isBlank()) throw new IllegalArgumentException("El diagnóstico es obligatorio.");

        String updPac = "UPDATE pacientes SET estado=?, prioridad=? WHERE paciente_id=?";
        String insAt = "INSERT INTO atenciones(paciente_id, doctor_id, diagnostico, tratamiento, fecha_registro) " +
                       "VALUES(?,?,?,?,NOW())";

        try (Connection con = Database.get()) {
            con.setAutoCommit(false);

            try (PreparedStatement ps = con.prepareStatement(updPac)) {
                ps.setString(1, (estado == null || estado.isBlank()) ? "EN_EVALUACION" : estado);
                ps.setInt(2, prioridad <= 0 ? 3 : prioridad);
                ps.setInt(3, pacienteId);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = con.prepareStatement(insAt)) {
                ps.setInt(1, pacienteId);
                ps.setInt(2, doctorId);
                ps.setString(3, dx);
                ps.setString(4, tx);
                ps.executeUpdate();
            }

            con.commit();
        } catch (SQLException e) {
            throw new RuntimeException("Error registrando atención", e);
        }
    }

    public List<Atencion> listarPorPaciente(int pacienteId) {
        String sql = "SELECT atencion_id, paciente_id, doctor_id, diagnostico, tratamiento, fecha_registro, dado_de_alta " +
                     "FROM atenciones WHERE paciente_id=? ORDER BY fecha_registro DESC";
        List<Atencion> out = new ArrayList<>();
        try (Connection con = Database.get(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, pacienteId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Atencion a = new Atencion();
                    a.atencionId = rs.getLong("atencion_id");
                    a.pacienteId = rs.getInt("paciente_id");
                    int d = rs.getInt("doctor_id");
                    a.doctorId = rs.wasNull() ? null : d;
                    a.diagnostico = rs.getString("diagnostico");
                    a.tratamiento = rs.getString("tratamiento");
                    a.fechaRegistro = rs.getTimestamp("fecha_registro");
                    a.alta = rs.getTimestamp("dado_de_alta");
                    out.add(a);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return out;
    }

    public boolean darDeAltaUltima(int pacienteId) {
        String select = "SELECT atencion_id FROM atenciones WHERE paciente_id=? AND dado_de_alta IS NULL ORDER BY fecha_registro DESC LIMIT 1";
        String updAtt = "UPDATE atenciones SET dado_de_alta=NOW() WHERE atencion_id=?";
        String updPac = "UPDATE pacientes SET estado='ATENDIDO' WHERE paciente_id=?";

        try (Connection con = Database.get()) {
            con.setAutoCommit(false);

            Long atencionId = null;
            try (PreparedStatement ps = con.prepareStatement(select)) {
                ps.setInt(1, pacienteId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) atencionId = rs.getLong(1);
                }
            }
            if (atencionId == null) {
                con.rollback();
                return false;
            }

            try (PreparedStatement ps = con.prepareStatement(updAtt)) {
                ps.setLong(1, atencionId);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = con.prepareStatement(updPac)) {
                ps.setInt(1, pacienteId);
                ps.executeUpdate();
            }

            con.commit();
            return true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
