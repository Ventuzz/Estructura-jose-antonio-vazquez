package edu.jose.vazquez.avanceproyecto.process;

import edu.jose.vazquez.avanceproyecto.models.Pacientes;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.sql.*;
import java.time.LocalDate;
import java.time.Period;

public class HospitalManager {

    private static final String URL  = "jdbc:mysql://localhost:3306/emergencysys";
    private static final String USER = "root";
    private static final String PASS = "admin";

    private static Connection getCon() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }


    private final HashMap<String, Pacientes> salaEspera = new HashMap<>();
    private final List<Pacientes> pacientesAtendidos = new ArrayList<>();
    private final List<Pacientes> registroPacientes = new ArrayList<>();

    private final List<String> tratamientosDisponibles = List.of(
        "Analgésicos cada 8 horas","Antibióticos cada 12 horas","Antiinflamatorios cada 8 horas",
        "Antihistamínicos cada 24 horas","Antipiréticos cada 6 horas","Antisépticos cada 8 horas",
        "Broncodilatadores cada 12 horas","Corticosteroides cada 8 horas","Diuréticos cada 12 horas",
        "Inmunosupresores cada 24 horas"
    );
    private final List<String> doctoresDisponibles = List.of(
        "Dr. Smith","Dr. Johnson","Dr. Williams","Dr. House","Dr. Jones",
        "Dra. Grey","Dra. Yang","Dr. Avery","Dr. Shepherd","Dr. Karev",
        "Dra. Torres","Dr. O'Malley"
    );


    public void addPaciente(String nombre, int edad, String enfermedad, int prioridad) { 
        Pacientes p = new Pacientes(nombre, edad, enfermedad, prioridad);
        p.setEstado("En espera");
        p.setTratamiento(null);
        p.setDoctor(null);
        salaEspera.put(p.getNombre(), p);
        registroPacientes.add(p);

        try (Connection con = getCon()) {
            con.setAutoCommit(false);
            try {
                int pacienteId = ensurePaciente(con, nombre);
                abrirAtencion(con, pacienteId, prioridad, enfermedad);
                con.commit();
            } catch (SQLException e) {
                con.rollback();
                System.out.println("✗ Error insertando en BD: " + e.getMessage());
            } finally {
                con.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.out.println("✗ Error de conexión: " + e.getMessage());
        }
    }

    private String getDoctorNombreById(Connection con, int doctorId) throws SQLException {
    try (PreparedStatement ps = con.prepareStatement(
            "SELECT nombre FROM doctores WHERE doctor_id=?")) {
        ps.setInt(1, doctorId);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getString(1);
        }
    }
    return "(doctor desconocido)";
}

public Pacientes attendPaciente(String nombre) {
    Pacientes paciente = getPacienteIgnoreCase(nombre);
    if (paciente != null) {
        salaEspera.remove(paciente.getNombre());
        pacientesAtendidos.add(paciente);
        paciente.setEstado("Atendido");
        paciente.setTratamiento(randomTratamiento());
    }

    try (Connection con = getCon()) {
        con.setAutoCommit(false);
        try {
            Integer pacienteId = findPacienteIdByNombre(con, nombre);
            if (pacienteId == null) {
                if (paciente == null) {
                    System.out.println("❌ El paciente no existe en la base de datos ni en memoria. ❌");
                    con.rollback();
                    return null;
                }
                pacienteId = ensurePaciente(con, nombre);
            }

            Long atencionId = findPendienteAtencionId(con, pacienteId);
            if (atencionId == null) {
                atencionId = abrirAtencion(con, pacienteId,
                        (paciente != null ? paciente.getPrioridad() : 3), nombre);
            }

            Integer doctorId = ensureRandomDoctor(con);
            String doctorNombre = getDoctorNombreById(con, doctorId);

            String dx = (paciente != null ? paciente.getEnfermedad() : "Diagnóstico no especificado");
            String tx = (paciente != null ? paciente.getTratamiento() : randomTratamiento());

            atender(con, atencionId, doctorId, dx, tx);

            if (paciente != null) {
                paciente.setDoctor(doctorNombre);
            }

            con.commit();
        } catch (SQLException e) {
            con.rollback();
            System.out.println("✗ Error actualizando en BD: " + e.getMessage());
        } finally {
            con.setAutoCommit(true);
        }
    } catch (SQLException e) {
        System.out.println("✗ Error de conexión: " + e.getMessage());
    }

    return paciente;
}

    public void cargarPacientesDesdeBD() {
        try (Connection con = getCon();
             PreparedStatement ps = con.prepareStatement("SELECT nombre_completo, fecha_nacimiento, sexo, diagnostico, prioridad FROM pacientes" +
                                                        " JOIN atenciones USING(paciente_id) WHERE estado='EN_ESPERA'")) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String nombre = rs.getString("nombre_completo");
                    String enfermedad = rs.getString("diagnostico");
                    int prioridad = rs.getInt("prioridad");
                    int edad = Period.between(rs.getDate("fecha_nacimiento").toLocalDate(), LocalDate.now()).getYears();
                    Pacientes p = new Pacientes(nombre, edad, enfermedad, prioridad);
                    salaEspera.put(nombre, p);
                    registroPacientes.add(p);
                }
            }
        } catch (SQLException e) {
            System.out.println("✗ Error cargando pacientes desde BD: " + e.getMessage());
        }
    }

    public boolean searchPaciente(String nombre) {
        final String sql = """
            SELECT a.atencion_id, p.nombre_completo, p.fecha_nacimiento, p.sexo,
                a.estado, a.prioridad, a.diagnostico, a.tratamiento,
                IFNULL(d.nombre,'(Sin asignar)') AS doctor,
                a.fecha_registro, a.dado_de_alta
            FROM atenciones a
            JOIN pacientes p ON p.paciente_id=a.paciente_id
            LEFT JOIN doctores d ON d.doctor_id=a.doctor_id
            WHERE LOWER(p.nombre_completo)=LOWER(?)
            ORDER BY a.fecha_registro DESC
            LIMIT 1
        """;
        try (Connection con = getCon();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    System.out.println("╭──────────────────────────────╮");
                    System.out.println("│ Paciente: " + rs.getString("nombre_completo"));
                    Date fn = rs.getDate("fecha_nacimiento");
                    if (fn != null) {
                        int edad = Period.between(fn.toLocalDate(), LocalDate.now()).getYears();
                        System.out.println("│ Edad: " + edad);
                    } else {
                        System.out.println("│ Edad: (desconocida)");
                    }
                    String sexo = rs.getString("sexo");
                    if (sexo != null) {
                        System.out.println("│ Sexo: " + sexo);
                    } else {
                        System.out.println("│ Sexo: (desconocido)");
                    }
                    System.out.println("│ Situación: " + rs.getString("diagnostico"));
                    System.out.println("│ Estado: " + rs.getString("estado"));
                    System.out.println("│ Prioridad: " + rs.getString("prioridad"));
                    System.out.println("│ Tratamiento: " + (rs.getString("tratamiento")==null?"(sin tratamiento)":rs.getString("tratamiento")));
                    System.out.println("│ Doctor: " + (rs.getString("doctor")==null?"(sin asignar)":rs.getString("doctor")));
                    System.out.println("╰──────────────────────────────╯");
                    return true;
                } else {
                    System.out.println("Paciente no encontrado: " + nombre);
                }
            }
        } catch (SQLException e) {
            System.out.println("✗ Error buscando en BD: " + e.getMessage());
        }
        return false;
    }

    public boolean eliminarPacientePorNombre(String nombre) {
        salaEspera.entrySet().removeIf(e -> e.getKey().equalsIgnoreCase(nombre));
        registroPacientes.removeIf(p -> p.getNombre().equalsIgnoreCase(nombre));
        pacientesAtendidos.removeIf(p -> p.getNombre().equalsIgnoreCase(nombre));


        try (Connection con = getCon()) {
            con.setAutoCommit(false);
            try {
                Integer pacienteId = findPacienteIdByNombre(con, nombre);
                if (pacienteId == null) {
                    con.rollback();
                    return false;
                }
                try (PreparedStatement delA = con.prepareStatement("DELETE FROM atenciones WHERE paciente_id=?")) {
                    delA.setInt(1, pacienteId);
                    delA.executeUpdate();
                }
                try (PreparedStatement delP = con.prepareStatement("DELETE FROM pacientes WHERE paciente_id=?")) {
                    delP.setInt(1, pacienteId);
                    delP.executeUpdate();
                }
                con.commit();
                return true;
            } catch (SQLException e) {
                con.rollback();
                System.out.println("✗ Error eliminando en BD: " + e.getMessage());
                return false;
            } finally {
                con.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.out.println("✗ Error de conexión: " + e.getMessage());
            return false;
        }
    }
    public void showPacientesEnEspera() {
        if (salaEspera.isEmpty()) {
            System.out.println("No hay pacientes en sala de espera (memoria).");
            listarPendientesDesdeBD();
            return;
        }
        for (Pacientes p : salaEspera.values()) {
            System.out.println("- " + p.getNombre());
        }
    }
    public void showRegistroPacientes() {
        final String sql = """
            SELECT a.atencion_id, p.nombre_completo, a.estado, a.prioridad,
                a.diagnostico, a.tratamiento,
                a.fecha_registro, a.dado_de_alta,
                IFNULL(d.nombre,'(Sin asignar)') AS doctor
            FROM atenciones a
            JOIN pacientes p ON p.paciente_id=a.paciente_id
            LEFT JOIN doctores d ON d.doctor_id=a.doctor_id
            ORDER BY a.fecha_registro DESC
        """;
        try (Connection con = getCon();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {
            boolean any = false;
            while (rs.next()) {
                any = true;
                System.out.println("╭──────────────────────────────╮");
                System.out.println("│ Paciente: " + rs.getString("nombre_completo"));
                System.out.println("│ ID único: " + rs.getLong("atencion_id"));
                System.out.println("│ Estado: " + rs.getString("estado")  );
                System.out.println("│ Prioridad: " + rs.getInt("prioridad"));
                System.out.println("│ Diagnóstico: " + nullSafe(rs.getString("diagnostico")));
                System.out.println("│ Tratamiento: " + nullSafe(rs.getString("tratamiento")));
                System.out.println("│ Doctor: " + rs.getString("doctor"));
                System.out.println("│ Registro: " + rs.getTimestamp("fecha_registro"));
                System.out.println("│ Alta: " + (rs.getTimestamp("dado_de_alta") != null ? rs.getTimestamp("dado_de_alta") : "(Por determinar)"));
                System.out.println("╰──────────────────────────────╯");
            }
            if (!any) System.out.println("No hay atenciones en la base de datos.");
        } catch (SQLException e) {
            System.out.println("✗ Error consultando BD: " + e.getMessage());
            if (registroPacientes.isEmpty()) {
                System.out.println("No hay pacientes en el registro (memoria).");
                return;
            }
            for (Pacientes p : registroPacientes) {
                System.out.println("╭──────────────────────────────╮");
                System.out.println("│ " + p.getNombre());
                System.out.println("│ Edad: " + p.getEdad());
                System.out.println("│ Situación: " + p.getEnfermedad());
                System.out.println("│ Estado: " + p.getEstado());
                System.out.println("│ Prioridad: " + p.getPrioridad());
                System.out.println("│ Tratamiento: " + (p.getTratamiento()==null?"(sin tratamiento)":p.getTratamiento()));
                System.out.println("│ Doctor: " + (p.getDoctor()==null?"(sin asignar)":p.getDoctor()));
                System.out.println("╰──────────────────────────────╯");
            }
        }
    }

    public void sortPacientesPorPrioridad() {
        List<Pacientes> pacientesList = new ArrayList<>(salaEspera.values());
        pacientesList.sort(Comparator.comparingInt(Pacientes::getPrioridad));
        for (Pacientes paciente : pacientesList) {
            System.out.println("╭───────────────────╮");
            System.out.println("│ " + paciente.getNombre());
            System.out.println("│ Prioridad: " + paciente.getPrioridad() + "      │");
            System.out.println("╰───────────────────╯");
        }
    }

    public void guardarDatosPaciente(String nombre, java.sql.Date fechaNacimiento, String sexo) {
        try (Connection con = getCon()) {
            con.setAutoCommit(false);
            try {
                Integer pacienteId = findPacienteIdByNombre(con, nombre);
                if (pacienteId == null) {
                    try (PreparedStatement ins = con.prepareStatement(
                            "INSERT INTO pacientes (nombre_completo, fecha_nacimiento, sexo) VALUES (?,?,?)")) {
                        ins.setString(1, nombre);
                        if (fechaNacimiento == null) ins.setNull(2, Types.DATE); else ins.setDate(2, fechaNacimiento);
                        if (sexo == null || sexo.isBlank()) ins.setNull(3, Types.CHAR); else ins.setString(3, sexo);
                        ins.executeUpdate();
                    }
                } else {
                    try (PreparedStatement upd = con.prepareStatement(
                            "UPDATE pacientes SET fecha_nacimiento=?, sexo=? WHERE paciente_id=?")) {
                        if (fechaNacimiento == null) upd.setNull(1, Types.DATE); else upd.setDate(1, fechaNacimiento);
                        if (sexo == null || sexo.isBlank()) upd.setNull(2, Types.CHAR); else upd.setString(2, sexo);
                        upd.setInt(3, pacienteId);
                        upd.executeUpdate();
                    }
                }
                con.commit();
            } catch (SQLException e) {
                con.rollback();
                System.out.println("✗ Error guardando datos de paciente: " + e.getMessage());
            } finally {
                con.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.out.println("✗ Error de conexión: " + e.getMessage());
        }
    }

    private int ensurePaciente(Connection con, String nombreCompleto) throws SQLException {
        Integer id = findPacienteIdByNombre(con, nombreCompleto);
        if (id != null) return id;

        try (PreparedStatement ins = con.prepareStatement(
                "INSERT INTO pacientes (nombre_completo) VALUES (?)",
                Statement.RETURN_GENERATED_KEYS)) {
            ins.setString(1, nombreCompleto);
            ins.executeUpdate();
            try (ResultSet rs = ins.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        throw new SQLException("No se pudo crear paciente");
    }

    private Integer findPacienteIdByNombre(Connection con, String nombreCompleto) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(
                "SELECT paciente_id FROM pacientes WHERE LOWER(nombre_completo)=LOWER(?) LIMIT 1")) {
            ps.setString(1, nombreCompleto);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return null;
    }

private long abrirAtencion(Connection con, int pacienteId, int prioridad, String diagnostico) throws SQLException {
    if (diagnostico == null || diagnostico.isBlank()) {
        throw new SQLException("❌ El diagnóstico no puede estar vacío. ❌");
    }

    try (PreparedStatement ps = con.prepareStatement(
        "INSERT INTO atenciones (paciente_id, prioridad, estado, diagnostico) VALUES (?,?, 'EN_ESPERA', ?)",
        Statement.RETURN_GENERATED_KEYS)) {
        ps.setInt(1, pacienteId);
        ps.setInt(2, prioridad);
        ps.setString(3, diagnostico);
        ps.executeUpdate();
        try (ResultSet rs = ps.getGeneratedKeys()) {
            if (rs.next()) return rs.getLong(1);
        }
    }
    throw new SQLException("No se pudo abrir atención");
}


    private Long findPendienteAtencionId(Connection con, int pacienteId) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(
                "SELECT atencion_id FROM atenciones WHERE paciente_id=? AND estado='EN_ESPERA' ORDER BY fecha_registro ASC LIMIT 1")) {
            ps.setInt(1, pacienteId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong(1);
            }
        }
        return null;
    }

    private int atender(Connection con, long atencionId, Integer doctorId, String dx, String tx) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(
            "UPDATE atenciones SET estado='ATENDIDO', doctor_id=?, diagnostico=?, tratamiento=?, dado_de_alta=NOW() WHERE atencion_id=?")) {
            if (doctorId == null) ps.setNull(1, Types.INTEGER); else ps.setInt(1, doctorId);
            if (dx == null || dx.isBlank()) ps.setNull(2, Types.VARCHAR); else ps.setString(2, dx);
            if (tx == null || tx.isBlank()) ps.setNull(3, Types.VARCHAR); else ps.setString(3, tx);
            ps.setLong(4, atencionId);
            return ps.executeUpdate();
        }
    }

private Integer ensureRandomDoctor(Connection con) throws SQLException {
    try (PreparedStatement q = con.prepareStatement("SELECT doctor_id FROM doctores ORDER BY RAND() LIMIT 1");
         ResultSet rs = q.executeQuery()) {
        if (rs.next()) return rs.getInt(1);
    }

    String nombre = randomDoctorNombre();
    try (PreparedStatement ins = con.prepareStatement(
            "INSERT INTO doctores (nombre) VALUES (?)", Statement.RETURN_GENERATED_KEYS)) {
        ins.setString(1, nombre);
        ins.executeUpdate();
        try (ResultSet rs = ins.getGeneratedKeys()) {
            if (rs.next()) return rs.getInt(1);
        }
    }
    throw new SQLException("No se pudo asegurar un doctor");
}

    private void listarPendientesDesdeBD() {
        final String sql = """
            SELECT a.atencion_id, p.nombre_completo, a.prioridad, a.fecha_registro
            FROM atenciones a
            JOIN pacientes p ON p.paciente_id = a.paciente_id
            WHERE a.estado='EN_ESPERA'
            ORDER BY a.prioridad ASC, a.fecha_registro ASC
        """;
        try (Connection con = getCon();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            System.out.println("Pendientes (BD):");
            System.out.println("ID | Paciente | Prio | Fecha registro");
            while (rs.next()) {
                System.out.printf("%d | %s | %d | %s%n",
                    rs.getLong("atencion_id"),
                    rs.getString("nombre_completo"),
                    rs.getInt("prioridad"),
                    rs.getTimestamp("fecha_registro"));
            }
        } catch (SQLException e) {
            System.out.println("✗ Error listando pendientes: " + e.getMessage());
        }
    }

    public void refreshPacientesDesdeBD() {
        salaEspera.clear();
        registroPacientes.clear();
        cargarPacientesDesdeBD();
    }

    public Pacientes getPacienteIgnoreCase(String nombre) {
        
        for (Pacientes p : registroPacientes) {
            if (p.getNombre().equalsIgnoreCase(nombre)) {
                return p;
            }
        }



        return null;
    }

    public void updatePaciente(String nombreCambiar, String nombre, String fechaNacimiento, String sexo) {
        Pacientes p = getPacienteIgnoreCase(nombreCambiar);
        if (p == null) {
            System.out.println("❌ No se puede actulizar un registro de un paciente atendido. ❌");
            return;
            
        }
        if (p != null) {

            try (Connection con = getCon();
                 PreparedStatement ps = con.prepareStatement(
                         "UPDATE pacientes SET nombre_completo=?, fecha_nacimiento=?, sexo=? WHERE paciente_id=?")) {
                ps.setString(1, nombre != null && !nombre.isBlank() ? nombre : p.getNombre());
                ps.setDate(2, fechaNacimiento != null && !fechaNacimiento.isBlank() ? Date.valueOf(fechaNacimiento) : p.getFechaNacimiento());
                ps.setString(3, sexo != null && !sexo.isBlank() ? sexo : p.getSexo());
                Integer pacienteId = findPacienteIdByNombre(con, p.getNombre());
                ps.setInt(4, pacienteId);
                ps.executeUpdate();
                refreshPacientesDesdeBD();
                System.out.println("✅ Información del paciente actualizada exitosamente. ✅");
            } catch (SQLException e) {
                System.out.println("✗ Error actualizando paciente: " + e.getMessage());
            }
        }


    }

    private String randomTratamiento() {
        return tratamientosDisponibles.get(new Random().nextInt(tratamientosDisponibles.size()));
    }

    private String randomDoctorNombre() {
        return doctoresDisponibles.get(new Random().nextInt(doctoresDisponibles.size()));
    }

    private static String nullSafe(String s) {
        return s == null ? "(Por determinar)" : s;
    }
}