package edu.jose.vazquez.avanceproyectodb.models;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class AtencionPanel extends JPanel {

    private final JTextField tfPacienteId = new JTextField(6);
    private final JTextField tfDoctorId   = new JTextField(6);
    private final JSpinner spPrioridad    = new JSpinner(new SpinnerNumberModel(3, 1, 5, 1));
    private final JComboBox<String> cbEstado = new JComboBox<>(new String[]{"En evaluacion","En espera","Atendido"});
    private final JTextField tfDx = new JTextField(22);
    private final JTextField tfTx = new JTextField(22);

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"AtenciónID","PacienteID","DoctorID","Diagnóstico","Tratamiento","Registro","Alta"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(model);

    private final AtencionService atencionService = new AtencionService();
    private final PacienteService pacienteService = new PacienteService();

    public AtencionPanel() {
        setLayout(new BorderLayout(10,10));

        // --- Formulario
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Registrar atención"));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5,5,5,5); c.anchor = GridBagConstraints.WEST;
        int r=0;

        c.gridx=0; c.gridy=r; form.add(new JLabel("Paciente ID:"), c);
        c.gridx=1;            form.add(tfPacienteId, c); r++;

        c.gridx=0; c.gridy=r; form.add(new JLabel("Doctor ID:"), c);
        c.gridx=1;            form.add(tfDoctorId, c); r++;

        c.gridx=0; c.gridy=r; form.add(new JLabel("Prioridad (1-5):"), c);
        c.gridx=1;            form.add(spPrioridad, c); r++;

        c.gridx=0; c.gridy=r; form.add(new JLabel("Estado:"), c);
        c.gridx=1;            form.add(cbEstado, c); r++;

        c.gridx=0; c.gridy=r; form.add(new JLabel("Diagnóstico:"), c);
        c.gridx=1;            form.add(tfDx, c); r++;

        c.gridx=0; c.gridy=r; form.add(new JLabel("Tratamiento:"), c);
        c.gridx=1;            form.add(tfTx, c); r++;

        JButton btnRegistrar = new JButton("Registrar atención");
        JButton btnHistorial = new JButton("Ver historial");
        JButton btnAlta = new JButton("Dar de alta (última)");
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actions.add(btnRegistrar);
        actions.add(btnHistorial);
        actions.add(btnAlta);

        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        form.setAlignmentX(Component.LEFT_ALIGNMENT);
        actions.setAlignmentX(Component.LEFT_ALIGNMENT);
        left.add(form);
        left.add(Box.createVerticalStrut(8));
        left.add(actions);
        left.setMinimumSize(new Dimension(360, 0));
        left.setPreferredSize(new Dimension(420, 0));

        JScrollPane tableScroll = new JScrollPane(table);
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, tableScroll);
        split.setDividerLocation(440);
        split.setResizeWeight(0.0);
        split.setOneTouchExpandable(true);
        add(split, BorderLayout.CENTER);

        // --- Listeners
        btnRegistrar.addActionListener(e -> registrar());
        btnHistorial.addActionListener(e -> cargarHistorial());
        btnAlta.addActionListener(e -> darAlta());
    }

    private void registrar() {
        try {
            int pacienteId = Integer.parseInt(tfPacienteId.getText().trim());
            int doctorId = Integer.parseInt(tfDoctorId.getText().trim());

            // Bloqueo si el estado del paciente es "Atendido"
            if (pacienteService.ultimoEstadoEsAlta(pacienteId)) {
                JOptionPane.showMessageDialog(this, "El paciente está 'Atendido'. No se puede registrar nueva atención.",
                        "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int prioridad = (Integer) spPrioridad.getValue();
            String estado = (String) cbEstado.getSelectedItem();
            String dx = tfDx.getText().trim();
            String tx = tfTx.getText().trim();

            long attId = atencionService.registrar(pacienteId, doctorId, prioridad, estado, dx, tx);
            JOptionPane.showMessageDialog(this, "Atención registrada. ID: " + attId);
            cargarHistorial();

        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "IDs inválidos.", "Error", JOptionPane.ERROR_MESSAGE);
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
                        a.atencionId,
                        a.pacienteId,
                        a.doctorId,
                        a.diagnostico,
                        a.tratamiento,
                        a.fechaRegistro,
                        a.alta
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
                JOptionPane.showMessageDialog(this, "Última atención marcada como 'Alta' y paciente 'Atendido'.");
                cargarHistorial();
            } else {
                JOptionPane.showMessageDialog(this, "Sin atenciones para este paciente o ya estaba en 'Alta'.",
                        "Aviso", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Paciente ID inválido.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.toString(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
