package edu.jose.vazquez.avanceproyectodb.models;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import edu.jose.vazquez.avanceproyectodb.process.DoctorService;
import java.awt.*;
import java.util.List;

public class DoctoresPanel extends JPanel {
    private final JTextField tfNombre = new JTextField(20);
    private final JTextField tfCorreo = new JTextField(20);
    private final JTextField tfTelefono = new JTextField(12);
    // Se reemplaza el campo de texto por un ComboBox para las especialidades
    private final JComboBox<DoctorService.Especialidad> cbEspecialidad = new JComboBox<>();
    private final JTextField tfBuscar = new JTextField(16);

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID", "Nombre", "Correo", "Teléfono", "Especialidad"}, 0) {
        @Override
        public boolean isCellEditable(int r, int c) {
            return false;
        }
    };
    private final JTable table = new JTable(model);
    private Integer selectedDoctorId = null;

    private final DoctorService service = new DoctorService();

    public DoctoresPanel() {
        setLayout(new BorderLayout(10, 10));
        cargarEspecialidades();

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Doctor"));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.anchor = GridBagConstraints.WEST;
        int r = 0;
        c.gridx = 0; c.gridy = r; form.add(new JLabel("Nombre:"), c);
        c.gridx = 1; form.add(tfNombre, c); r++;
        c.gridx = 0; c.gridy = r; form.add(new JLabel("Correo:"), c);
        c.gridx = 1; form.add(tfCorreo, c); r++;
        c.gridx = 0; c.gridy = r; form.add(new JLabel("Teléfono:"), c);
        c.gridx = 1; form.add(tfTelefono, c); r++;
        c.gridx = 0; c.gridy = r; form.add(new JLabel("Especialidad:"), c);
        c.gridx = 1; form.add(cbEspecialidad, c); r++;

        JButton btnCrear = new JButton("Registrar");
        JButton btnGuardar = new JButton("Guardar cambios");
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actions.add(btnCrear);
        actions.add(btnGuardar);

        JPanel search = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnBuscar = new JButton("Buscar");
        JButton btnTodos = new JButton("Todos");
        btnBuscar.addActionListener(e -> cargarTabla(tfBuscar.getText().trim()));
        btnTodos.addActionListener(e -> {
            tfBuscar.setText("");
            cargarTabla(null);
        });
        search.add(new JLabel("Buscar:"));
        search.add(tfBuscar);
        search.add(btnBuscar);
        search.add(btnTodos);

        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        form.setAlignmentX(LEFT_ALIGNMENT);
        actions.setAlignmentX(LEFT_ALIGNMENT);
        search.setAlignmentX(LEFT_ALIGNMENT);
        left.add(form);
        left.add(Box.createVerticalStrut(8));
        left.add(actions);
        left.add(Box.createVerticalStrut(8));
        left.add(search);
        left.setPreferredSize(new Dimension(420, 0));

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, new JScrollPane(table));
        split.setDividerLocation(440);
        split.setResizeWeight(0.0);
        split.setOneTouchExpandable(true);
        add(split, BorderLayout.CENTER);

        btnCrear.addActionListener(e -> registrar());
        btnGuardar.addActionListener(e -> guardarCambios());

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                selectedDoctorId = null;
                return;
            }
            selectedDoctorId = (Integer) model.getValueAt(row, 0);
            tfNombre.setText(String.valueOf(model.getValueAt(row, 1)));
            tfCorreo.setText(String.valueOf(model.getValueAt(row, 2)));
            tfTelefono.setText(String.valueOf(model.getValueAt(row, 3)));
            
            // Seleccionar la especialidad correcta en el ComboBox
            String especialidadNombre = (String) model.getValueAt(row, 4);
            for (int i = 0; i < cbEspecialidad.getItemCount(); i++) {
                if (cbEspecialidad.getItemAt(i).nombre.equals(especialidadNombre)) {
                    cbEspecialidad.setSelectedIndex(i);
                    break;
                }
            }
        });

        cargarTabla(null);
    }

    private void cargarEspecialidades() {
        try {
            List<DoctorService.Especialidad> especialidades = service.listarEspecialidades();
            for (DoctorService.Especialidad esp : especialidades) {
                cbEspecialidad.addItem(esp);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "No se pudieron cargar las especialidades: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void registrar() {
        try {
            var d = new DoctorService.Doctor();
            d.nombre = tfNombre.getText().trim();
            d.correo = tfCorreo.getText().trim();
            d.telefono = tfTelefono.getText().trim();
            DoctorService.Especialidad selectedEsp = (DoctorService.Especialidad) cbEspecialidad.getSelectedItem();
            if (selectedEsp == null) {
                 JOptionPane.showMessageDialog(this, "Debe seleccionar una especialidad.", "Validación", JOptionPane.WARNING_MESSAGE);
                 return;
            }
            d.idEspecialidad = selectedEsp.id;
            
            if (d.nombre.isBlank() || d.correo.isBlank())
                throw new IllegalArgumentException("Nombre y correo son obligatorios.");
            
            service.crear(d);
            cargarTabla(null);
            JOptionPane.showMessageDialog(this, "Doctor registrado.");
            tfNombre.setText("");
            tfCorreo.setText("");
            tfTelefono.setText("");
            cbEspecialidad.setSelectedIndex(0);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.toString(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void guardarCambios() {
        try {
            if (selectedDoctorId == null) {
                JOptionPane.showMessageDialog(this, "Selecciona un doctor.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            var d = new DoctorService.Doctor();
            d.id = selectedDoctorId;
            d.nombre = tfNombre.getText().trim();
            d.correo = tfCorreo.getText().trim();
            d.telefono = tfTelefono.getText().trim();
            DoctorService.Especialidad selectedEsp = (DoctorService.Especialidad) cbEspecialidad.getSelectedItem();
             if (selectedEsp == null) {
                 JOptionPane.showMessageDialog(this, "Debe seleccionar una especialidad.", "Validación", JOptionPane.WARNING_MESSAGE);
                 return;
            }
            d.idEspecialidad = selectedEsp.id;

            if (d.nombre.isBlank() || d.correo.isBlank())
                throw new IllegalArgumentException("Nombre y correo son obligatorios.");
            
            service.actualizar(d);
            cargarTabla(tfBuscar.getText().trim().isBlank() ? null : tfBuscar.getText().trim());
            JOptionPane.showMessageDialog(this, "Doctor actualizado.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.toString(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarTabla(String filtro) {
        model.setRowCount(0);
        for (var d : service.listar(filtro)) {
            model.addRow(new Object[]{d.id, d.nombre, d.correo, d.telefono, d.nombreEspecialidad});
        }
    }
}