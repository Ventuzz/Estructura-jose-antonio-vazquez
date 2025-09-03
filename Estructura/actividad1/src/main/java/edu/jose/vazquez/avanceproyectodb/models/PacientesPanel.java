package edu.jose.vazquez.avanceproyectodb.models;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import edu.jose.vazquez.avanceproyectodb.process.Database;

public class PacientesPanel extends JPanel {
    private final JTextField tfNombre = new JTextField(20);
    private final JTextField tfFecha  = new JTextField(10);
    private final JComboBox<String> cbSexo = new JComboBox<>(new String[]{"M","F","Otro"});
    private final JTextField tfTel   = new JTextField(12);
    private final JTextField tfEmail = new JTextField(18);
    private final JComboBox<String> cbEstado = new JComboBox<>(new String[]{"En espera","En evaluacion","Atendido"});
    private final JSpinner spPrioridad = new JSpinner(new SpinnerNumberModel(3,1,5,1));
    private final JCheckBox chkInconsciente = new JCheckBox("Paciente inconsciente");
    private final JTextField tfBuscar = new JTextField(18);

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID","Nombre","Sexo","Teléfono","Email","Fecha Nac.","Estado","Prioridad"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(model);
    private Integer selectedPacienteId = null;

    private final PacienteService pacienteService = new PacienteService();
    private final AtencionService atencionService = new AtencionService();

    public PacientesPanel() {
        setLayout(new BorderLayout(10,10));

        // Form
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Paciente"));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5,5,5,5); c.anchor = GridBagConstraints.WEST;
        int r=0;

        c.gridx=0; c.gridy=r; form.add(new JLabel("Nombre completo:"), c);
        c.gridx=1;            form.add(tfNombre, c); r++;
        c.gridx=0; c.gridy=r; form.add(new JLabel("Fecha nac. (yyyy-MM-dd):"), c);
        c.gridx=1;            form.add(tfFecha, c); r++;
        c.gridx=0; c.gridy=r; form.add(new JLabel("Sexo:"), c);
        c.gridx=1;            form.add(cbSexo, c); r++;
        c.gridx=0; c.gridy=r; form.add(new JLabel("Teléfono:"), c);
        c.gridx=1;            form.add(tfTel, c); r++;
        c.gridx=0; c.gridy=r; form.add(new JLabel("Correo:"), c);
        c.gridx=1;            form.add(tfEmail, c); r++;
        c.gridx=0; c.gridy=r; form.add(new JLabel("Estado:"), c);
        c.gridx=1;            form.add(cbEstado, c); r++;
        c.gridx=0; c.gridy=r; form.add(new JLabel("Prioridad (1-5):"), c);
        c.gridx=1;            form.add(spPrioridad, c); r++;
        c.gridx=0; c.gridy=r; c.gridwidth=2; form.add(chkInconsciente, c); r++;
        c.gridwidth=1;

        JButton btnCrear = new JButton("Registrar");
        JButton btnGuardar = new JButton("Guardar cambios");
        JButton btnRefrescar = new JButton("Refrescar");
        JButton btnLimpiar = new JButton("Limpiar");
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actions.add(btnCrear); actions.add(btnGuardar); actions.add(btnRefrescar); actions.add(btnLimpiar);

        JPanel search = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnBuscar = new JButton("Buscar");
        JButton btnTodos  = new JButton("Todos");
        btnBuscar.addActionListener(e -> cargarTabla(tfBuscar.getText().trim()));
        btnTodos.addActionListener(e -> { tfBuscar.setText(""); cargarTabla(null); });
        search.add(new JLabel("Buscar:")); search.add(tfBuscar); search.add(btnBuscar); search.add(btnTodos);

        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        form.setAlignmentX(LEFT_ALIGNMENT);
        actions.setAlignmentX(LEFT_ALIGNMENT);
        search.setAlignmentX(LEFT_ALIGNMENT);
        left.add(form); left.add(Box.createVerticalStrut(8));
        left.add(actions); left.add(Box.createVerticalStrut(8));
        left.add(search);
        left.setPreferredSize(new Dimension(440, 0));

        JScrollPane tableScroll = new JScrollPane(table);
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, tableScroll);
        split.setDividerLocation(460); split.setResizeWeight(0.0); split.setOneTouchExpandable(true);
        add(split, BorderLayout.CENTER);

        // Listeners
        chkInconsciente.addActionListener(e -> {
            boolean inc = chkInconsciente.isSelected();
            boolean enable = !inc;
            tfNombre.setEnabled(enable); tfFecha.setEnabled(enable); cbSexo.setEnabled(enable);
            tfTel.setEnabled(enable); tfEmail.setEnabled(enable);
            cbEstado.setEnabled(enable); spPrioridad.setEnabled(enable);
            if (inc) {
                tfNombre.setText("Paciente Inconsciente");
                tfFecha.setText(""); cbSexo.setSelectedIndex(0);
                tfTel.setText(""); tfEmail.setText("");
                cbEstado.setSelectedItem("En evaluacion"); spPrioridad.setValue(1);
            }
        });

        btnCrear.addActionListener(e -> registrar());
        btnGuardar.addActionListener(e -> guardarCambios());
        btnRefrescar.addActionListener(e -> cargarTabla(null));
        btnLimpiar.addActionListener(e -> limpiar());

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { selectedPacienteId = null; return; }
            selectedPacienteId = (Integer) model.getValueAt(row, 0);
            tfNombre.setText(String.valueOf(model.getValueAt(row, 1)));
            cbSexo.setSelectedItem(String.valueOf(model.getValueAt(row, 2)));
            tfTel.setText(String.valueOf(model.getValueAt(row, 3)));
            tfEmail.setText(String.valueOf(model.getValueAt(row, 4)));
            tfFecha.setText(String.valueOf(model.getValueAt(row, 5)));
            cbEstado.setSelectedItem(String.valueOf(model.getValueAt(row, 6)));
            spPrioridad.setValue(Integer.parseInt(String.valueOf(model.getValueAt(row, 7))));
            chkInconsciente.setSelected("Paciente Inconsciente".equals(tfNombre.getText()));
        });

        cargarTabla(null);
    }

    private void registrar() {
        try {
            if (!tfTel.getText().trim().isBlank() && !tfTel.getText().trim().matches("\\d+")) {
                JOptionPane.showMessageDialog(this, "El teléfono debe ser numérico.", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
            var p = new PacienteService.Paciente();
            p.nombre = (chkInconsciente.isSelected() || tfNombre.getText().isBlank()) ? "Paciente Inconsciente" : tfNombre.getText().trim();
            p.sexo = (String) cbSexo.getSelectedItem();
            p.telefono = tfTel.getText().trim();
            p.correo = tfEmail.getText().trim();
            p.fechaNacimiento = tfFecha.getText().trim();
            p.estado = chkInconsciente.isSelected() ? "En evaluacion" : (String) cbEstado.getSelectedItem();
            p.prioridad = chkInconsciente.isSelected() ? 1 : (Integer) spPrioridad.getValue();

            int id = pacienteService.crear(p);

            if (chkInconsciente.isSelected()) {
                String dx = JOptionPane.showInputDialog(this, "Diagnóstico (obligatorio)\nEstado: En evaluacion, Prioridad: 1");
                if (dx == null || dx.isBlank()) throw new IllegalArgumentException("El diagnóstico es obligatorio.");
                atencionService.registrar(id, 0, 1, "En evaluacion", dx, "");
            }

            cargarTabla(null);
            JOptionPane.showMessageDialog(this, "Paciente registrado. ID: " + id);
            limpiar();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validación", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.toString(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void guardarCambios() {
        try {
            if (selectedPacienteId == null) {
                JOptionPane.showMessageDialog(this, "Selecciona un paciente de la tabla.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (pacienteService.ultimoEstadoEsAlta(selectedPacienteId)) {
                JOptionPane.showMessageDialog(this, "El paciente está 'Atendido'. No se puede actualizar.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!tfTel.getText().trim().isBlank() && !tfTel.getText().trim().matches("\\d+")) {
                JOptionPane.showMessageDialog(this, "El teléfono debe ser numérico.", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }

            var p = new PacienteService.Paciente();
            p.id = selectedPacienteId;
            p.nombre = tfNombre.getText().trim();
            p.sexo = (String) cbSexo.getSelectedItem();
            p.telefono = tfTel.getText().trim();
            p.correo = tfEmail.getText().trim();
            p.fechaNacimiento = tfFecha.getText().trim();
            p.estado = (String) cbEstado.getSelectedItem();
            p.prioridad = (Integer) spPrioridad.getValue();

            pacienteService.actualizarBasico(p);
            cargarTabla(tfBuscar.getText().trim().isBlank()? null : tfBuscar.getText().trim());
            JOptionPane.showMessageDialog(this, "Paciente actualizado (auditoría registrada).");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.toString(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarTabla(String filtro) {
        model.setRowCount(0);
        for (var p : pacienteService.listar(filtro)) {
            model.addRow(new Object[]{
                    p.id, p.nombre, p.sexo, p.telefono, p.correo,
                    p.fechaNacimiento==null? "" : p.fechaNacimiento,
                    p.estado, p.prioridad
            });
        }
    }

    private void limpiar() {
        selectedPacienteId = null;
        chkInconsciente.setSelected(false);
        tfNombre.setEnabled(true); tfFecha.setEnabled(true); cbSexo.setEnabled(true);
        tfTel.setEnabled(true); tfEmail.setEnabled(true); cbEstado.setEnabled(true); spPrioridad.setEnabled(true);
        tfNombre.setText(""); tfFecha.setText(""); cbSexo.setSelectedIndex(0);
        tfTel.setText(""); tfEmail.setText(""); cbEstado.setSelectedItem("En espera"); spPrioridad.setValue(3);
        table.clearSelection();
        tfNombre.requestFocusInWindow();
    }
}
