package edu.jose.vazquez.avanceproyectodb.models;

import edu.jose.vazquez.avanceproyectodb.process.DoctorService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class AtencionPanel extends JPanel {

    private final JTextField tfPacienteId = new JTextField(6);
    // Se reemplaza el DoctorID por ComboBox de especialidad y doctor
    private final JComboBox<DoctorService.Especialidad> cbEspecialidad = new JComboBox<>();
    private final JComboBox<DoctorService.Doctor> cbDoctor = new JComboBox<>();
    
    private final JSpinner spPrioridad = new JSpinner(new SpinnerNumberModel(3, 1, 5, 1));
    private final JComboBox<String> cbEstado = new JComboBox<>(new String[]{"EN_EVALUACION", "EN_ESPERA", "ATENDIDO"});
    private final JTextField tfDx = new JTextField(22);
    private final JTextField tfTx = new JTextField(22);

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"AtenciónID", "PacienteID", "DoctorID", "Diagnóstico", "Tratamiento", "Registro", "Alta"}, 0) {
        @Override
        public boolean isCellEditable(int r, int c) {
            return false;
        }
    };
    private final JTable table = new JTable(model);

    private final AtencionService atencionService = new AtencionService();
    private final PacienteService pacienteService = new PacienteService();
    private final DoctorService doctorService = new DoctorService();

    public AtencionPanel() {
        setLayout(new BorderLayout(10, 10));
        cargarEspecialidades();

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Registrar Atención por Especialidad"));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.anchor = GridBagConstraints.WEST;
        int r = 0;

        c.gridx = 0; c.gridy = r; form.add(new JLabel("Paciente ID:"), c);
        c.gridx = 1; form.add(tfPacienteId, c); r++;

        // --- Nuevos campos para seleccionar doctor ---
        c.gridx = 0; c.gridy = r; form.add(new JLabel("Especialidad Requerida:"), c);
        c.gridx = 1; form.add(cbEspecialidad, c); r++;

        c.gridx = 0; c.gridy = r; form.add(new JLabel("Asignar Doctor:"), c);
        c.gridx = 1; form.add(cbDoctor, c); r++;
        // --- Fin de nuevos campos ---

        c.gridx = 0; c.gridy = r; form.add(new JLabel("Prioridad (1-5):"), c);
        c.gridx = 1; form.add(spPrioridad, c); r++;

        c.gridx = 0; c.gridy = r; form.add(new JLabel("Estado Paciente:"), c);
        c.gridx = 1; form.add(cbEstado, c); r++;

        c.gridx = 0; c.gridy = r; form.add(new JLabel("Diagnóstico:"), c);
        c.gridx = 1; form.add(tfDx, c); r++;

        c.gridx = 0; c.gridy = r; form.add(new JLabel("Tratamiento:"), c);
        c.gridx = 1; form.add(tfTx, c); r++;

        JButton btnRegistrar = new JButton("Registrar atención");
        JButton btnHistorial = new JButton("Ver historial del paciente");
        JButton btnAlta = new JButton("Dar de alta (última atención)");
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actions.add(btnRegistrar);
        actions.add(btnHistorial);
        actions.add(btnAlta);

        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        form.setAlignmentX(Component.LEFT_ALIGNMENT);
        actions.setAlignmentX(Component.LEFT_ALIGNMENT);
        left.add(form);
        left.add(Box.createVerticalStrut(8));
        left.add(actions);
        left.setPreferredSize(new Dimension(420, 0));

        JScrollPane tableScroll = new JScrollPane(table);
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, tableScroll);
        split.setDividerLocation(440);
        split.setResizeWeight(0.0);
        split.setOneTouchExpandable(true);
        add(split, BorderLayout.CENTER);

        // --- Listeners ---
        cbEspecialidad.addActionListener(e -> cargarDoctoresPorEspecialidad());
        btnRegistrar.addActionListener(e -> registrar());
        btnHistorial.addActionListener(e -> cargarHistorial());
        btnAlta.addActionListener(e -> darAlta());
    }

    private void cargarEspecialidades() {
        try {
            cbEspecialidad.removeAllItems();
            doctorService.listarEspecialidades().forEach(cbEspecialidad::addItem);
            // Cargar doctores de la primera especialidad por defecto
            if (cbEspecialidad.getItemCount() > 0) {
                cbEspecialidad.setSelectedIndex(0);
                cargarDoctoresPorEspecialidad();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar especialidades: " + ex.getMessage());
        }
    }

    private void cargarDoctoresPorEspecialidad() {
        DoctorService.Especialidad selected = (DoctorService.Especialidad) cbEspecialidad.getSelectedItem();
        if (selected == null) return;
        try {
            cbDoctor.removeAllItems();
            doctorService.listarPorEspecialidad(selected.id).forEach(cbDoctor::addItem);
        } catch (Exception ex) {
             JOptionPane.showMessageDialog(this, "Error al cargar doctores: " + ex.getMessage());
        }
    }

    private void registrar() {
        try {
            int pacienteId = Integer.parseInt(tfPacienteId.getText().trim());
            
            DoctorService.Doctor doctorSeleccionado = (DoctorService.Doctor) cbDoctor.getSelectedItem();
            if (doctorSeleccionado == null) {
                JOptionPane.showMessageDialog(this, "Debe seleccionar un doctor.", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (pacienteService.ultimoEstadoEsAlta(pacienteId)) {
                JOptionPane.showMessageDialog(this, "El paciente está 'ATENDIDO'. No se puede registrar nueva atención.",
                        "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int prioridad = (Integer) spPrioridad.getValue();
            String estado = (String) cbEstado.getSelectedItem();
            String dx = tfDx.getText().trim();
            String tx = tfTx.getText().trim();

            atencionService.registrar(pacienteId, doctorSeleccionado.id, prioridad, estado, dx, tx);
            JOptionPane.showMessageDialog(this, "Atención registrada exitosamente.");
            cargarHistorial();

        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "El ID del paciente es inválido.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException iae) {
            JOptionPane.showMessageDialog(this, iae.getMessage(), "Validación", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.toString(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarHistorial() {
        model.setRowCount(0);
        try {
            int pacienteId = Integer.parseInt(tfPacienteId.getText().trim());
            for (var a : atencionService.listarPorPaciente(pacienteId)) {
                model.addRow(new Object[]{
                        a.atencionId, a.pacienteId, a.doctorId, a.diagnostico,
                        a.tratamiento, a.fechaRegistro, a.alta
                });
            }
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Paciente ID inválido para cargar historial.", "Aviso", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.toString(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void darAlta() {
        try {
            int pacienteId = Integer.parseInt(tfPacienteId.getText().trim());
            boolean ok = atencionService.darDeAltaUltima(pacienteId);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Última atención marcada como 'Alta' y paciente actualizado a 'ATENDIDO'.");
                cargarHistorial();
            } else {
                JOptionPane.showMessageDialog(this, "No se encontraron atenciones pendientes para este paciente.",
                        "Aviso", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Paciente ID inválido.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.toString(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
