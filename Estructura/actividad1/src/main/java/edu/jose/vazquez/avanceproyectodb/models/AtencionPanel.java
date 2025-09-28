package edu.jose.vazquez.avanceproyectodb.models;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.awt.image.RescaleOp;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import edu.jose.vazquez.avanceproyectodb.process.Database;

public class AtencionPanel extends JPanel {

    private static final float UI_SCALE     = 1.12f;
    private static final float TITLE_INC_PT = 4f;

    private static final Color BG         = new Color(0xF4F7FB);
    private static final Color CARD_BG    = Color.WHITE;
    private static final Color STROKE     = new Color(0xE6EBF0);
    private static final Color TITLE      = new Color(0x0B1525);
    private static final Color MUTED      = new Color(0x516173);
    private static final Color GRID       = new Color(0xEDF2F7);

    private static final String TABLE_BG_RESOURCE = "/edu/jose/vazquez/avanceproyectodb/resources/Logo_EmergenSys.png";
    private static final float  TABLE_BG_ALPHA    = 0.22f;

    private static final int ALPHA_EVEN      = 180;
    private static final int ALPHA_ODD       = 160;
    private static final int ALPHA_SELECTION = 235;

    private final JTextField tfPacienteId   = new JTextField(10);
    private final JComboBox<EspecialidadItem> cbEspecialidad = new JComboBox<>();
    private final JComboBox<DoctorItem>       cbDoctor       = new JComboBox<>();
    private final JTextArea  taDiagnostico  = new JTextArea(3, 28);
    private final JTextArea  taTratamiento  = new JTextArea(3, 28);

    private final JButton btnRegistrar = softButton("Registrar atención");
    private final JButton btnRefrescar = softButton("Refrescar");
    private final JButton btnHistorial = softButton("Historial");
    private final JButton btnAlta      = softButton("Dar de alta");
    private final JCheckBox chkFiltrarTabla = new JCheckBox("Filtrar tabla por ID");

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{
                    "Atención ID", "Paciente ID", "Paciente",
                    "Doctor ID", "Doctor", "Especialidad",
                    "Fecha registro", "Fecha alta",
                    "Diagnóstico", "Tratamiento"
            }, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(model);

    public AtencionPanel() {
        setLayout(new BorderLayout(12,12));
        setBorder(new EmptyBorder(12,12,12,12));
        setBackground(BG);

        JPanel headerWrap = new JPanel(new BorderLayout());
        headerWrap.setOpaque(false);

        JLabel title = new JLabel("Registro de Atenciones");
        title.setFont(title.getFont().deriveFont(Font.BOLD, title.getFont().getSize2D() + TITLE_INC_PT));
        title.setForeground(TITLE);
        headerWrap.add(title, BorderLayout.WEST);

        JPanel hero = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        hero.setOpaque(false);
        JLabel heroIcon = new JLabel("\u2695");
        heroIcon.setFont(heroIcon.getFont().deriveFont(20f));
        JLabel heroText = new JLabel("Captura, consulta y alta de atenciones por paciente y especialidad");
        heroText.setForeground(MUTED);
        hero.add(heroIcon); hero.add(heroText);
        headerWrap.add(hero, BorderLayout.SOUTH);
        add(headerWrap, BorderLayout.NORTH);

        JPanel formCard = new CardPanel();
        formCard.setLayout(new GridBagLayout());
        formCard.setBorder(new EmptyBorder(16,16,16,16));
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(8,10,8,10);
        gc.anchor = GridBagConstraints.WEST;
        gc.fill   = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1;

        int y = 0;
        gc.gridx=0; gc.gridy=y; gc.weightx=0; formCard.add(label("Paciente ID:"), gc);
        tfPacienteId.setToolTipText("ID numérico del paciente");
        gc.gridx=1; gc.gridy=y; gc.weightx=1; formCard.add(tfPacienteId, gc); y++;

        gc.gridx=0; gc.gridy=y; gc.weightx=0; formCard.add(label("Especialidad:"), gc);
        cbEspecialidad.setPrototypeDisplayValue(new EspecialidadItem(999999, "____________________________"));
        gc.gridx=1; gc.gridy=y; gc.weightx=1; formCard.add(cbEspecialidad, gc); y++;

        gc.gridx=0; gc.gridy=y; gc.weightx=0; formCard.add(label("Doctor:"), gc);
        cbDoctor.setPrototypeDisplayValue(new DoctorItem(999999, "____________________________", -1));
        gc.gridx=1; gc.gridy=y; gc.weightx=1; formCard.add(cbDoctor, gc); y++;

        gc.gridx=0; gc.gridy=y; gc.weightx=0; formCard.add(label("Diagnóstico:"), gc);
        taDiagnostico.setLineWrap(true); taDiagnostico.setWrapStyleWord(true);
        gc.gridx=1; gc.gridy=y; gc.weightx=1; formCard.add(new JScrollPane(taDiagnostico), gc); y++;

        gc.gridx=0; gc.gridy=y; gc.weightx=0; formCard.add(label("Tratamiento:"), gc);
        taTratamiento.setLineWrap(true); taTratamiento.setWrapStyleWord(true);
        gc.gridx=1; gc.gridy=y; gc.weightx=1; formCard.add(new JScrollPane(taTratamiento), gc); y++;

        // Renderer correcto del combo (muestra "id - nombre")
        cbDoctor.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof DoctorItem) {
                    DoctorItem di = (DoctorItem) value;
                    lbl.setText(di.id + " - " + di.nombre);
                } else if (value != null) {
                    lbl.setText(value.toString());
                } else {
                    lbl.setText("");
                }
                return lbl;
            }
        });

        JPanel tips = new CardPanel();
        tips.setLayout(new GridLayout(1, 3, 8, 8));
        tips.setBorder(new EmptyBorder(12,12,12,12));
        tips.add(makeTip("\u23F3  Flujo", "Captura ID de paciente, elige especialidad/doctor.\nGuarda atención."));
        tips.add(makeTip("\uD83D\uDD0D  Consulta", "Usa ‘Historial’ para ver tiempos por atención y total."));
        tips.add(makeTip("\u2714\uFE0F  Alta", "Selecciona un registro sin alta y pulsa ‘Dar de alta’."));

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        formCard.setAlignmentX(LEFT_ALIGNMENT);
        tips.setAlignmentX(LEFT_ALIGNMENT);
        left.add(formCard);
        left.add(Box.createVerticalStrut(10));
        left.add(tips);
        left.setPreferredSize(new Dimension(560, 0));
        left.setMinimumSize(new Dimension(160, 0));

        styleTable(table);
        table.setAutoCreateRowSorter(true);

        TranslucentCellRenderer translucent = new TranslucentCellRenderer();
        table.setDefaultRenderer(Object.class, translucent);
        table.setDefaultRenderer(Integer.class, translucent);

        JScrollPane sp = new JScrollPane();
        sp.setBorder(BorderFactory.createMatteBorder(1,1,1,1, STROKE));
        try {
            URL imgUrl = getClass().getResource(TABLE_BG_RESOURCE);
            if (imgUrl != null) {
                BufferedImage raw = ImageIO.read(imgUrl);
                BufferedImage boosted = boostImage(raw);
                ImageViewport vp = new ImageViewport(boosted, TABLE_BG_ALPHA, ImageViewport.Mode.COVER);
                vp.setView(table);
                sp.setViewport(vp);
            } else {
                sp.setViewportView(table);
            }
        } catch (Exception ex) {
            sp.setViewportView(table);
        }

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, sp);
        split.setDividerLocation(560);
        split.setResizeWeight(0);
        split.setContinuousLayout(true);
        split.setOneTouchExpandable(true);
        split.setBorder(BorderFactory.createEmptyBorder());
        add(split, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        btnAlta.setEnabled(false);
        actions.add(chkFiltrarTabla);
        actions.add(btnHistorial);
        actions.add(btnRegistrar);
        actions.add(btnRefrescar);
        actions.add(btnAlta);
        add(actions, BorderLayout.SOUTH);

        table.getSelectionModel().addListSelectionListener(e -> {
            int viewRow = table.getSelectedRow();
            if (viewRow < 0) { btnAlta.setEnabled(false); return; }
            int row = table.convertRowIndexToModel(viewRow);
            Object fechaAlta = model.getValueAt(row, 7);
            btnAlta.setEnabled(fechaAlta == null);
        });

        btnRegistrar.addActionListener(e -> registrarAtencion());
        btnAlta.addActionListener(e -> darDeAltaPaciente());
        btnRefrescar.addActionListener(e -> refrescarTodo());

        btnHistorial.addActionListener(e -> abrirHistorialDialog());

        cbEspecialidad.addActionListener(e -> {
            EspecialidadItem esp = (EspecialidadItem) cbEspecialidad.getSelectedItem();
            int idEsp = (esp != null) ? esp.id : -1;
            cargarDoctoresPorEspecialidad(idEsp);
        });

        SwingUtilities.invokeLater(() -> {
            cargarEspecialidades();
            EspecialidadItem esp = (EspecialidadItem) cbEspecialidad.getSelectedItem();
            cargarDoctoresPorEspecialidad(esp != null ? esp.id : -1);
            cargarAtenciones();
            applyFontScale(this, UI_SCALE);
        });
    }

    private static JLabel label(String txt){
        JLabel l = new JLabel(txt);
        l.setForeground(MUTED);
        return l;
    }
    private static JButton softButton(String text){
        JButton b = new JButton(text);
        b.setUI(new BasicButtonUI());
        b.setOpaque(true);
        b.setContentAreaFilled(true);
        b.setBorderPainted(false);
        b.setBackground(new Color(0xEFF7FB));
        b.setForeground(new Color(0x0B1525));
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(9,14,9,14));
        return b;
    }
    private static void styleTable(JTable t){
        t.setFillsViewportHeight(true);
        t.setOpaque(false);
        t.setRowHeight(30);
        t.setGridColor(GRID);
        JTableHeader h = t.getTableHeader();
        h.setBackground(new Color(0xF3F7FB));
        h.setForeground(new Color(0x0B1525));
        h.setFont(h.getFont().deriveFont(h.getFont().getSize2D() * 1.10f).deriveFont(Font.BOLD));
        h.setBorder(BorderFactory.createMatteBorder(0,0,1,0, STROKE));
        h.setOpaque(true);
    }
    private static JPanel makeTip(String title, String lines) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        JLabel t = new JLabel(title);
        t.setForeground(new Color(0x0B1525));
        t.setFont(t.getFont().deriveFont(Font.BOLD));
        JTextArea b = new JTextArea(lines);
        b.setEditable(false);
        b.setOpaque(false);
        b.setForeground(new Color(0x516173));
        b.setLineWrap(true);
        b.setWrapStyleWord(true);
        p.add(t, BorderLayout.NORTH);
        p.add(b, BorderLayout.CENTER);
        return p;
    }
    private static class CardPanel extends JPanel {
        CardPanel(){ setOpaque(false); }
        @Override protected void paintComponent(Graphics g){
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(CARD_BG);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
            g2.setColor(STROKE);
            g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 16, 16);
            g2.dispose();
            super.paintComponent(g);
        }
    }
    private static class TranslucentCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setOpaque(false);
            setBorder(noFocusBorder);
            setForeground(Color.BLACK);
            return this;
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            JTable table = (JTable) SwingUtilities.getAncestorOfClass(JTable.class, this);
            int row = -1;
            boolean selected = false;
            if (table != null) {
                Point p = SwingUtilities.convertPoint(this, 0, 0, table);
                row = table.rowAtPoint(p);
                if (row >= 0) selected = table.isRowSelected(row);
            }

            int alpha;
            if (selected) alpha = ALPHA_SELECTION;
            else if (row >= 0) alpha = (row % 2 == 0) ? ALPHA_EVEN : ALPHA_ODD;
            else alpha = ALPHA_EVEN;

            g2.setColor(new Color(255,255,255, alpha));
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.dispose();
            super.paintComponent(g);
        }
    }
    private static class ImageViewport extends JViewport {
        enum Mode { COVER, CONTAIN, TILE }
        private final Image img;
        private final float alpha;
        private final Mode mode;
        ImageViewport(Image img, float alpha, Mode mode){
            this.img = img; this.alpha = Math.max(0f, Math.min(1f, alpha)); this.mode = mode;
            setOpaque(false);
        }
        @Override protected void paintComponent(Graphics g){
            super.paintComponent(g);
            if (img == null) return;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setComposite(AlphaComposite.SrcOver.derive(alpha));
            int w = getWidth(), h = getHeight();
            int iw = img.getWidth(this), ih = img.getHeight(this);
            if (iw <= 0 || ih <= 0) { g2.dispose(); return; }
            double scale = (mode == Mode.CONTAIN)
                    ? Math.min(w/(double)iw, h/(double)ih)
                    : Math.max(w/(double)iw, h/(double)ih);
            int sw = (int)Math.round(iw*scale), sh = (int)Math.round(ih*scale);
            int x = (w - sw)/2, y = (h - sh)/2;
            g2.drawImage(img, x, y, sw, sh, this);
            g2.dispose();
        }
    }
    private static BufferedImage boostImage(BufferedImage src){
        float contrast = 1.08f;
        RescaleOp rescale = new RescaleOp(
                new float[]{contrast,contrast,contrast,1f},
                new float[]{0f,0f,0f,0f}, null
        );
        BufferedImage tmp = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_ARGB);
        rescale.filter(src, tmp);
        float[] sharp = { 0,-1,0, -1,5,-1, 0,-1,0 };
        ConvolveOp op = new ConvolveOp(new Kernel(3,3, sharp), ConvolveOp.EDGE_NO_OP, null);
        BufferedImage out = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_ARGB);
        op.filter(tmp, out);
        return out;
    }

    private void cargarEspecialidades() {
        cbEspecialidad.removeAllItems();
        final String sql = "SELECT id_especialidad, especialidad FROM especialidad ORDER BY especialidad";
        try (Connection con = Database.get();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                cbEspecialidad.addItem(new EspecialidadItem(rs.getInt(1), rs.getString(2)));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error cargando especialidades: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // *** ARREGLADO: combina puente y columna directa con UNION; incluye fallbacks elegantes ***
    private void cargarDoctoresPorEspecialidad(int idEspecialidad) {
        DefaultComboBoxModel<DoctorItem> modelCb = new DefaultComboBoxModel<>();
        cbDoctor.setModel(modelCb);

        // Si no hay filtro, todos los doctores
        if (idEspecialidad <= 0) {
            final String sqlAll = "SELECT doctor_id, nombre FROM doctores ORDER BY nombre";
            try (Connection con = Database.get();
                 PreparedStatement ps = con.prepareStatement(sqlAll);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    modelCb.addElement(new DoctorItem(rs.getInt(1), rs.getString(2), -1));
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error cargando doctores: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
            ajustarDropSize();
            return;
        }

        // Intento 1: UNION (puente + directa)
        final String sqlUnion =
            "SELECT d.doctor_id, d.nombre FROM doctores d " +
            "JOIN doctor_especialidad de ON de.doctor_id = d.doctor_id " +
            "WHERE de.id_especialidad = ? " +
            "UNION " +
            "SELECT doctor_id, nombre FROM doctores WHERE id_especialidad = ? " +
            "ORDER BY nombre";
        boolean ok = false;
        try (Connection con = Database.get();
             PreparedStatement ps = con.prepareStatement(sqlUnion)) {
            ps.setInt(1, idEspecialidad);
            ps.setInt(2, idEspecialidad);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    modelCb.addElement(new DoctorItem(rs.getInt(1), rs.getString(2), idEspecialidad));
                }
            }
            ok = true;
        } catch (SQLException ignore) {
            // Puede fallar si no existe la tabla puente; seguimos a fallbacks
        }

        if (!ok && modelCb.getSize() == 0) {
            // Intento 2: solo puente
            final String sqlBridge =
                    "SELECT DISTINCT d.doctor_id, d.nombre " +
                    "FROM doctor_especialidad de " +
                    "JOIN doctores d ON d.doctor_id = de.doctor_id " +
                    "WHERE de.id_especialidad = ? " +
                    "ORDER BY d.nombre";
            try (Connection con = Database.get();
                 PreparedStatement ps = con.prepareStatement(sqlBridge)) {
                ps.setInt(1, idEspecialidad);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        modelCb.addElement(new DoctorItem(rs.getInt(1), rs.getString(2), idEspecialidad));
                    }
                }
            } catch (SQLException ignore2) {
                // quizá tampoco existe; probamos directa
            }
        }

        if (modelCb.getSize() == 0) {
            // Intento 3: solo columna directa
            final String sqlDirect =
                "SELECT doctor_id, nombre FROM doctores WHERE id_especialidad = ? ORDER BY nombre";
            try (Connection con = Database.get();
                 PreparedStatement ps = con.prepareStatement(sqlDirect)) {
                ps.setInt(1, idEspecialidad);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        modelCb.addElement(new DoctorItem(rs.getInt(1), rs.getString(2), idEspecialidad));
                    }
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error cargando doctores: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        if (modelCb.getSize() > 0) cbDoctor.setSelectedIndex(0);
        ajustarDropSize();
    }

    private void ajustarDropSize() {
        int n = cbDoctor.getItemCount();
        cbDoctor.setMaximumRowCount(Math.min(Math.max(8, n), 20)); // 8..20 filas visibles
    }

    private void cargarAtenciones() {
        model.setRowCount(0);
        final String sql =
                "SELECT a.atencion_id, p.paciente_id, p.nombre_completo, " +
                "       d.doctor_id, d.nombre AS doctor, e.especialidad, " +
                "       a.fecha_registro, a.dado_de_alta, " +
                "       a.diagnostico, a.tratamiento " +
                "FROM atenciones a " +
                "JOIN pacientes p ON p.paciente_id = a.paciente_id " +
                "LEFT JOIN doctores d ON d.doctor_id = a.doctor_id " +
                "LEFT JOIN especialidad e ON e.id_especialidad = d.id_especialidad " +
                "ORDER BY a.atencion_id DESC";
        try (Connection con = Database.get();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getLong("atencion_id"),
                        rs.getInt("paciente_id"),
                        rs.getString("nombre_completo"),
                        rs.getObject("doctor_id"),
                        rs.getString("doctor"),
                        rs.getString("especialidad"),
                        rs.getTimestamp("fecha_registro"),
                        rs.getTimestamp("dado_de_alta"),
                        rs.getString("diagnostico"),
                        rs.getString("tratamiento")
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error cargando atenciones: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        revalidate();
        repaint();
    }

    private void filtrarTablaPorPaciente() {
        String sPac = tfPacienteId.getText().trim();
        if (sPac.isEmpty()) { cargarAtenciones(); return; }

        int pacienteId;
        try { pacienteId = Integer.parseInt(sPac); }
        catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Paciente ID inválido.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        model.setRowCount(0);
        final String sql =
                "SELECT a.atencion_id, p.paciente_id, p.nombre_completo, " +
                "       d.doctor_id, d.nombre AS doctor, e.especialidad, " +
                "       a.fecha_registro, a.dado_de_alta, " +
                "       a.diagnostico, a.tratamiento " +
                "FROM atenciones a " +
                "JOIN pacientes p ON p.paciente_id = a.paciente_id " +
                "LEFT JOIN doctores d ON d.doctor_id = a.doctor_id " +
                "LEFT JOIN especialidad e ON e.id_especialidad = d.id_especialidad " +
                "WHERE a.paciente_id = ? " +
                "ORDER BY a.atencion_id DESC";
        try (Connection con = Database.get();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, pacienteId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    model.addRow(new Object[]{
                            rs.getLong("atencion_id"),
                            rs.getInt("paciente_id"),
                            rs.getString("nombre_completo"),
                            rs.getObject("doctor_id"),
                            rs.getString("doctor"),
                            rs.getString("especialidad"),
                            rs.getTimestamp("fecha_registro"),
                            rs.getTimestamp("dado_de_alta"),
                            rs.getString("diagnostico"),
                            rs.getString("tratamiento")
                    });
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al filtrar: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void registrarAtencion() {
        String sPac = tfPacienteId.getText().trim();
        if (sPac.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Captura el Paciente ID.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        final int pacienteId;
        try { pacienteId = Integer.parseInt(sPac); }
        catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Paciente ID inválido.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!existePaciente(pacienteId)) {
            JOptionPane.showMessageDialog(this, "No existe el paciente ID: " + pacienteId,
                    "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        DoctorItem selDoctor = (DoctorItem) cbDoctor.getSelectedItem();
        if (selDoctor == null || selDoctor.id <= 0) {
            JOptionPane.showMessageDialog(this, "Debes seleccionar un doctor para registrar la atención.",
                    "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }
        final int doctorId = selDoctor.id;

        String diagnostico = taDiagnostico.getText().trim();
        String tratamiento = taTratamiento.getText().trim();

        final String sql = "INSERT INTO atenciones (paciente_id, doctor_id, diagnostico, tratamiento) VALUES (?,?,?,?)";

        try (Connection con = Database.get();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, pacienteId);
            ps.setInt(2, doctorId);
            if (!diagnostico.isEmpty()) ps.setString(3, diagnostico); else ps.setNull(3, Types.VARCHAR);
            if (!tratamiento.isEmpty()) ps.setString(4, tratamiento); else ps.setNull(4, Types.VARCHAR);
            ps.executeUpdate();

            taDiagnostico.setText("");
            taTratamiento.setText("");
            if (chkFiltrarTabla.isSelected()) filtrarTablaPorPaciente(); else cargarAtenciones();

            JOptionPane.showMessageDialog(this, "Atención registrada correctamente.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "No se pudo registrar: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean existePaciente(int pacienteId) {
        final String sql = "SELECT 1 FROM pacientes WHERE paciente_id = ?";
        try (Connection con = Database.get();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, pacienteId);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error validando paciente: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private void darDeAltaPaciente() {
        String sPac = tfPacienteId.getText().trim();
        if (sPac.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Captura el Paciente ID para darlo de alta.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }
        final int pacienteId;
        try { pacienteId = Integer.parseInt(sPac); }
        catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Paciente ID inválido.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int ok = JOptionPane.showConfirmDialog(this,
                "¿Dar de alta al paciente " + pacienteId + "?\n(estado pasará a ATENDIDO)",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;

        final String SQL_UPD =
            "UPDATE pacientes SET estado = 'ATENDIDO' WHERE paciente_id = ?";

        try (Connection con = Database.get();
             PreparedStatement ps  = con.prepareStatement(SQL_UPD)) {
            ps.setInt(1, pacienteId);
            int n = ps.executeUpdate();

            if (n == 0) {
                JOptionPane.showMessageDialog(this, "No existe el paciente o ya estaba ATENDIDO.",
                        "Aviso", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Paciente " + pacienteId + " dado de alta.",
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }

            if (chkFiltrarTabla.isSelected()) filtrarTablaPorPaciente(); else cargarAtenciones();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al dar de alta: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void abrirHistorialDialog() {
        String sPac = tfPacienteId.getText().trim();
        if (sPac.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingresa un Paciente ID para ver el historial.",
                    "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int pacienteId;
        try { pacienteId = Integer.parseInt(sPac); }
        catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Paciente ID inválido.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this),
                "Historial del paciente " + pacienteId, Dialog.ModalityType.MODELESS);
        dlg.setLayout(new BorderLayout(8,8));
        dlg.setSize(900, 400);
        dlg.setLocationRelativeTo(this);

        JPanel head = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lblTiempo = new JLabel("Tiempo total: …");
        head.add(lblTiempo);
        dlg.add(head, BorderLayout.NORTH);

        DefaultTableModel tm = new DefaultTableModel(
                new Object[]{"Atención ID", "Doctor", "Especialidad", "Fecha registro", "Fecha alta", "Diagnóstico", "Tratamiento", "Tiempo Atención min"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable t = new JTable(tm);
        t.setFillsViewportHeight(true);
        t.setAutoCreateRowSorter(true);
        dlg.add(new JScrollPane(t), BorderLayout.CENTER);

        final String sql =
                "SELECT a.atencion_id, d.nombre AS doctor, e.especialidad, " +
                "       a.fecha_registro, a.dado_de_alta, a.diagnostico, a.tratamiento, " +
                "       TIMESTAMPDIFF(MINUTE, a.fecha_registro, a.dado_de_alta) AS minutos " +
                "FROM atenciones a " +
                "LEFT JOIN doctores d ON d.doctor_id = a.doctor_id " +
                "LEFT JOIN especialidad e ON e.id_especialidad = d.id_especialidad " +
                "WHERE a.paciente_id = ? " +
                "ORDER BY a.atencion_id DESC";
        final String sqlTotal =
                "SELECT COALESCE(SUM(TIMESTAMPDIFF(MINUTE, fecha_registro, dado_de_alta)),0) " +
                "FROM atenciones WHERE paciente_id = ? AND dado_de_alta IS NOT NULL";

        try (Connection con = Database.get()) {
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, pacienteId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        tm.addRow(new Object[]{
                                rs.getLong("atencion_id"),
                                rs.getString("doctor"),
                                rs.getString("especialidad"),
                                rs.getTimestamp("fecha_registro"),
                                rs.getTimestamp("dado_de_alta"),
                                rs.getString("diagnostico"),
                                rs.getString("tratamiento"),
                                rs.getObject("minutos")
                        });
                    }
                }
            }
            try (PreparedStatement ps = con.prepareStatement(sqlTotal)) {
                ps.setInt(1, pacienteId);
                try (ResultSet rs = ps.executeQuery()) {
                    String mins = "0";
                    if (rs.next() && rs.getObject(1) != null) mins = rs.getString(1);
                    lblTiempo.setText("Tiempo total: " + mins + " min");
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error cargando historial: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }

        dlg.setVisible(true);
    }

    private static class DoctorItem {
        final int id;
        final String nombre;
        final int idEspecialidad;
        DoctorItem(int id, String nombre, int idEspecialidad) {
            this.id = id; this.nombre = nombre; this.idEspecialidad = idEspecialidad;
        }
        @Override public String toString() { return nombre; }
    }
    private static class EspecialidadItem {
        final int id;
        final String nombre;
        EspecialidadItem(int id, String nombre) { this.id = id; this.nombre = nombre; }
        @Override public String toString() { return nombre; }
    }

    private static void applyFontScale(Component root, float scale) {
        if (root == null || scale == 1f) return;
        Font f = root.getFont();
        if (f != null) root.setFont(f.deriveFont(f.getSize2D() * scale));
        if (root instanceof Container) {
            for (Component child : ((Container) root).getComponents()) {
                applyFontScale(child, scale);
            }
        }
    }
    // Selecciona en cbEspecialidad por id
private void selectEspecialidadById(int idEsp) {
    for (int i = 0; i < cbEspecialidad.getItemCount(); i++) {
        EspecialidadItem it = cbEspecialidad.getItemAt(i);
        if (it != null && it.id == idEsp) {
            cbEspecialidad.setSelectedIndex(i);
            return;
        }
    }
}

// Selecciona en cbDoctor por id
private void selectDoctorById(int idDoc) {
    for (int i = 0; i < cbDoctor.getItemCount(); i++) {
        DoctorItem it = cbDoctor.getItemAt(i);
        if (it != null && it.id == idDoc) {
            cbDoctor.setSelectedIndex(i);
            return;
        }
    }
}

// Refresca combos (manteniendo selección) y tabla
private void refrescarTodo() {
    // Guardar selección actual
    Integer selEspId = null;
    EspecialidadItem selEsp = (EspecialidadItem) cbEspecialidad.getSelectedItem();
    if (selEsp != null) selEspId = selEsp.id;

    Integer selDocId = null;
    DoctorItem selDoc = (DoctorItem) cbDoctor.getSelectedItem();
    if (selDoc != null) selDocId = selDoc.id;

    // Recargar especialidades
    cargarEspecialidades();

    // Restaurar especialidad si se puede
    if (selEspId != null) selectEspecialidadById(selEspId);

    // Recargar doctores según especialidad actual
    EspecialidadItem esp = (EspecialidadItem) cbEspecialidad.getSelectedItem();
    int idEsp = (esp != null) ? esp.id : -1;
    cargarDoctoresPorEspecialidad(idEsp);

    // Restaurar doctor si sigue existiendo
    if (selDocId != null) selectDoctorById(selDocId);

    // Refrescar tabla (respetando filtro por ID si está activo)
    if (chkFiltrarTabla.isSelected()) {
        filtrarTablaPorPaciente();
    } else {
        cargarAtenciones();
    }
}

}
