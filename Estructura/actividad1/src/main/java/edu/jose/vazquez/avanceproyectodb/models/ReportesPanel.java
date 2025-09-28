package edu.jose.vazquez.avanceproyectodb.models;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
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
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.awt.image.RescaleOp;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import edu.jose.vazquez.avanceproyectodb.process.Database;

public class ReportesPanel extends JPanel {

    private static final float UI_SCALE     = 1.10f;
    private static final float TITLE_INC_PT = 4f;

    private static final Color BG         = new Color(0xF4F7FB);
    private static final Color CARD_BG    = Color.WHITE;
    private static final Color STROKE     = new Color(0xE6EBF0);
    private static final Color TITLE      = new Color(0x0B1525);
    private static final Color MUTED      = new Color(0x516173);
    private static final Color GRID       = new Color(0xEDF2F7);

    private static final String TABLE_BG_RESOURCE = "/edu/jose/vazquez/avanceproyectodb/resources/Logo_EmergenSys.png";
    private static final float  TABLE_BG_ALPHA    = 0.16f; // 0..1 (sube para más visible)

    private static final int ALPHA_EVEN      = 170;
    private static final int ALPHA_ODD       = 140;
    private static final int ALPHA_SELECTION = 235;

    // Filtros y acciones
    private final JComboBox<DoctorItem> cbDoctor = new JComboBox<>();
    private final JButton btnRefrescar = softButton("Actualizar");

    // Tiempo por paciente
    private final javax.swing.JTextField tfPacienteId = new javax.swing.JTextField(8);
    private final JButton btnTiempoPaciente = softButton("Ver tiempo por ID");

    // KPIs
    private final KpiCard kpiPacientesPorDoctor = new KpiCard("Pacientes atendidos por el doctor");
    private final KpiCard kpiTiempoConsulta     = new KpiCard("Promedio de consultas (min)");
    private final KpiCard kpiTiempoTotalGlobal  = new KpiCard("Tiempo de consultas totales (min)");
    private final KpiCard kpiDadosAlta          = new KpiCard("Pacientes dados de alta");
    private final KpiCard kpiEnTratamiento      = new KpiCard("Pacientes en tratamiento");
    private final KpiCard kpiInconscientes      = new KpiCard("Pacientes actualmente inconscientes");

    // Top 5 doctores
    private final DefaultTableModel topModel = new DefaultTableModel(
            new Object[]{"Doctor", "Pacientes atendidos"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    
    private final JTable topTable = new JTable(topModel);

    // Gráfico del Top 5
    private final Top5Chart chart = new Top5Chart();

    public ReportesPanel() {
        setLayout(new BorderLayout(14,14));
        setBorder(new EmptyBorder(16,16,16,16));
        setBackground(BG);

        // ===== Barra superior =====
        JPanel top = new JPanel(new GridBagLayout());
        top.setOpaque(false);
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6,6,6,6);
        gc.anchor = GridBagConstraints.WEST;

        JLabel titulo = new JLabel("Conteos y Reportes");
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, titulo.getFont().getSize2D() + TITLE_INC_PT));
        titulo.setForeground(TITLE);

        cbDoctor.setPrototypeDisplayValue(new DoctorItem(99999, "_________________________"));

        gc.gridx=0; gc.gridy=0; gc.gridwidth=3; top.add(titulo, gc);
        gc.gridwidth=1;

        gc.gridx=0; gc.gridy=1; top.add(label("Doctor:"), gc);
        gc.gridx=1; gc.gridy=1; top.add(cbDoctor, gc);
        gc.gridx=2; gc.gridy=1; top.add(btnRefrescar, gc);

        gc.gridx=0; gc.gridy=2; top.add(label("Paciente ID:"), gc);
        gc.gridx=1; gc.gridy=2; top.add(tfPacienteId, gc);
        gc.gridx=2; gc.gridy=2; top.add(btnTiempoPaciente, gc);

        add(top, BorderLayout.NORTH);

        // ===== Centro: KPIs + Top5 + Chart =====
        JPanel center = new JPanel(new BorderLayout(14,14));
        center.setOpaque(false);

        JPanel grid = new JPanel(new GridLayout(2, 3, 14, 14));
        grid.setOpaque(false);
        grid.add(kpiPacientesPorDoctor);
        grid.add(kpiTiempoConsulta);
        grid.add(kpiTiempoTotalGlobal);
        grid.add(kpiDadosAlta);
        grid.add(kpiEnTratamiento);
        grid.add(kpiInconscientes);
        center.add(grid, BorderLayout.NORTH);

        // Tabla Top5 con fondo de logo (CONTAIN para no recortar)
        styleTable(topTable);
        TranslucentCellRenderer translucent = new TranslucentCellRenderer();
        topTable.setDefaultRenderer(Object.class, translucent);
        topTable.setDefaultRenderer(Integer.class, translucent);

        JPanel topBox = new JPanel(new BorderLayout());
        topBox.setOpaque(false);
        JLabel lblTop = new JLabel("Top 5 doctores por pacientes atendidos");
        lblTop.setBorder(new EmptyBorder(0,4,8,0));
        lblTop.setForeground(TITLE);
        lblTop.setFont(lblTop.getFont().deriveFont(Font.BOLD));
        topBox.add(lblTop, BorderLayout.NORTH);

        JScrollPane sp = new JScrollPane();
        sp.setBorder(BorderFactory.createMatteBorder(1,1,1,1, STROKE));
        try {
            URL imgUrl = getClass().getResource(TABLE_BG_RESOURCE);
            if (imgUrl != null) {
                BufferedImage raw = ImageIO.read(imgUrl);
                BufferedImage boosted = boostImage(raw);
                ImageViewport vp = new ImageViewport(boosted, TABLE_BG_ALPHA, ImageViewport.Mode.CONTAIN);
                vp.setView(topTable);
                sp.setViewport(vp);
            } else {
                sp.setViewportView(topTable);
            }
        } catch (Exception ex) {
            sp.setViewportView(topTable);
        }
        topBox.add(sp, BorderLayout.CENTER);

        // Tarjeta con gráfico
        JPanel chartCard = new CardPanel();
        chartCard.setLayout(new BorderLayout());
        chartCard.setBorder(new EmptyBorder(12,12,12,12));
        JLabel chartTitle = new JLabel("Resumen visual Top 5");
        chartTitle.setForeground(TITLE);
        chartTitle.setFont(chartTitle.getFont().deriveFont(Font.BOLD));
        chart.setPreferredSize(new Dimension(200, 220));
        chartCard.add(chartTitle, BorderLayout.NORTH);
        chartCard.add(chart, BorderLayout.CENTER);

        // Apilar tabla + gráfico
        JPanel stack = new JPanel(new BorderLayout(14,14));
        stack.setOpaque(false);
        stack.add(topBox, BorderLayout.CENTER);
        stack.add(chartCard, BorderLayout.SOUTH);

        center.add(stack, BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        // Listeners
        btnRefrescar.addActionListener(e -> refrescarTodo());
        cbDoctor.addActionListener(e -> recargarPacientesPorDoctor());
        btnTiempoPaciente.addActionListener(e -> mostrarTiempoPorPaciente());

        // Datos iniciales

        cargarDoctores();
        cbDoctor.setMaximumRowCount(Math.max(1, cbDoctor.getItemCount()));
        recargarTodo();


        // El gráfico escucha el modelo
        topModel.addTableModelListener(chart);

        applyFontScale(this, UI_SCALE);

        SwingUtilities.invokeLater(() -> {
        recargarTodo();          // KPIs + Top 5
        updateChartFromTopModel(); // redibuja la gráfica

        cbDoctor.setRenderer(new javax.swing.DefaultListCellRenderer(){
    @Override
    public java.awt.Component getListCellRendererComponent(
            javax.swing.JList<?> list, Object value, int index,
            boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (value instanceof DoctorItem di) setText(di.id + " – " + di.nombre);
        return this;
    }
});

});

    }


    private void updateChartFromTopModel() {
        throw new UnsupportedOperationException("Unimplemented method 'updateChartFromTopModel'");
    }

    /* ===================== Cargar doctores ===================== */

private void cargarDoctores() {
    javax.swing.DefaultComboBoxModel<DoctorItem> model = new javax.swing.DefaultComboBoxModel<>();
    cbDoctor.setModel(model);
    model.addElement(new DoctorItem(-1, "— Selecciona doctor —"));

    final String sql = "SELECT doctor_id, nombre FROM doctores ORDER BY nombre";
    try (java.sql.Connection con = Database.get();
         java.sql.PreparedStatement ps = con.prepareStatement(sql);
         java.sql.ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
            model.addElement(new DoctorItem(rs.getInt(1), rs.getString(2)));
        }
        if (model.getSize() > 1) cbDoctor.setSelectedIndex(1);
    } catch (java.sql.SQLException ex) {
        System.err.println("No se pudieron cargar doctores: " + ex.getMessage());
    }
}


    /* ===================== Recargas ===================== */

    private void recargarTodo() {
        recargarPacientesPorDoctor();
        recargarTiempoGlobal();
        recargarDadosAlta();
        recargarEnTratamiento();
        recargarInconscientes();
        recargarTop5Doctores(); // refresca también el gráfico
    }

public void refrescarTodo() {
    cargarDoctores();            // repobla el combo desde BD
    recargarTodo();              // recalcula KPIs + Top 5
    cbDoctor.setMaximumRowCount(Math.max(1, cbDoctor.getItemCount()));

}


// 1) KPI: total de ATENCIONES realizadas por el doctor seleccionado
private void recargarPacientesPorDoctor() {
    DoctorItem sel = (DoctorItem) cbDoctor.getSelectedItem();
    if (sel == null || sel.id < 0) { kpiPacientesPorDoctor.setValue("—"); return; }

    final String sql = "SELECT COUNT(*) FROM atenciones WHERE doctor_id = ?"; // ← antes DISTINCT pacientes
    try (Connection con = Database.get();
         PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setInt(1, sel.id);
        try (ResultSet rs = ps.executeQuery()) {
            kpiPacientesPorDoctor.setValue(rs.next() ? String.valueOf(rs.getInt(1)) : "0");
        }
    } catch (SQLException ex) {
        kpiPacientesPorDoctor.setValue("—");
        System.err.println("KPI atenciones por doctor: " + ex.getMessage());
    }
}

// 2) Top 5 doctores por ATENCIONES realizadas


    private void recargarTiempoGlobal() {
        final String sqlAvg = "SELECT ROUND(AVG(TIMESTAMPDIFF(MINUTE, fecha_registro, dado_de_alta)), 1) " +
                              "FROM atenciones WHERE dado_de_alta IS NOT NULL";
        final String sqlSum = "SELECT SUM(TIMESTAMPDIFF(MINUTE, fecha_registro, dado_de_alta)) " +
                              "FROM atenciones WHERE dado_de_alta IS NOT NULL";
        try (Connection con = Database.get()) {
            try (PreparedStatement ps = con.prepareStatement(sqlAvg);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getObject(1) != null) {
                    kpiTiempoConsulta.setValue(rs.getString(1) + " min");
                } else {
                    kpiTiempoConsulta.setValue("—");
                }
            }
            try (PreparedStatement ps = con.prepareStatement(sqlSum);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getObject(1) != null) {
                    kpiTiempoTotalGlobal.setValue(rs.getString(1) + " min");
                } else {
                    kpiTiempoTotalGlobal.setValue("0 min");
                }
            }
        } catch (SQLException ex) {
            kpiTiempoConsulta.setValue("—");
            kpiTiempoTotalGlobal.setValue("—");
            System.err.println("Tiempo global: " + ex.getMessage());
        }
    }

    private void recargarDadosAlta() {
        final String sql = "SELECT COUNT(*) FROM pacientes WHERE UPPER(estado) = 'ATENDIDO'";
        try (Connection con = Database.get();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            kpiDadosAlta.setValue(rs.next() ? String.valueOf(rs.getInt(1)) : "0");
        } catch (SQLException ex) {
            kpiDadosAlta.setValue("—");
            System.err.println("Dados de alta: " + ex.getMessage());
        }
    }

    private void recargarEnTratamiento() {
        final String sql = "SELECT COUNT(*) FROM pacientes WHERE UPPER(estado) <> 'ATENDIDO'";
        try (Connection con = Database.get();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            kpiEnTratamiento.setValue(rs.next() ? String.valueOf(rs.getInt(1)) : "0");
        } catch (SQLException ex) {
            kpiEnTratamiento.setValue("—");
            System.err.println("En tratamiento: " + ex.getMessage());
        }
    }

    private void recargarInconscientes() {
        final String sqlReal = "SELECT COUNT(*) FROM pacientes WHERE inconsciente = 1";
        final String sqlHeur = "SELECT COUNT(*) FROM pacientes WHERE UPPER(nombre_completo) = 'NN'";

        try (Connection con = Database.get()) {
            try (PreparedStatement ps = con.prepareStatement(sqlReal);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) { kpiInconscientes.setValue(String.valueOf(rs.getInt(1))); return; }
            } catch (SQLException ignore) { }

            try (PreparedStatement ps = con.prepareStatement(sqlHeur);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) kpiInconscientes.setValue(String.valueOf(rs.getInt(1)));
                else kpiInconscientes.setValue("0");
            }
        } catch (SQLException ex) {
            kpiInconscientes.setValue("—");
            System.err.println("Inconscientes: " + ex.getMessage());
        }
    }

private void recargarTop5Doctores() {
    topModel.setRowCount(0);
    final String sql =
        "SELECT d.nombre, COUNT(a.atencion_id) AS total " +
        "FROM doctores d " +
        "LEFT JOIN atenciones a ON a.doctor_id = d.doctor_id " +
        "GROUP BY d.doctor_id, d.nombre " +
        "ORDER BY total DESC, d.nombre ASC " +
        "LIMIT 5";
    try (Connection con = Database.get();
         PreparedStatement ps = con.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
            topModel.addRow(new Object[]{ rs.getString(1), rs.getInt(2) });
        }
    } catch (SQLException ex) {
        System.err.println("Top 5 doctores: " + ex.getMessage());
    }
}
    // ===== Acción: tiempo por paciente (ID) =====
    private void mostrarTiempoPorPaciente() {
        String raw = tfPacienteId.getText().trim();
        if (raw.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingresa un ID de paciente.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            int id = Integer.parseInt(raw);
            final String sql =
                "SELECT p.nombre_completo, " +
                "       COALESCE(SUM(TIMESTAMPDIFF(MINUTE, a.fecha_registro, a.dado_de_alta)), 0) AS minutos " +
                "FROM pacientes p " +
                "LEFT JOIN atenciones a ON a.paciente_id = p.paciente_id AND a.dado_de_alta IS NOT NULL " +
                "WHERE p.paciente_id = ? " +
                "GROUP BY p.paciente_id, p.nombre_completo";
            try (Connection con = Database.get();
                 PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    String nombre = "";
                    String mins = "0";
                    if (rs.next()) {
                        nombre = rs.getString("nombre_completo");
                        mins   = rs.getString("minutos");
                    }
                    JOptionPane.showMessageDialog(this,
                        "Tiempo total de " + nombre + ": " + mins + " min",
                        "Tiempo del paciente",
                        JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "ID inválido.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        }
    }

    /* ===================== UI Helpers ===================== */

    private static class DoctorItem {
        final int id; final String nombre;
        DoctorItem(int id, String nombre) { this.id = id; this.nombre = nombre; }
        @Override public String toString() { return nombre; }
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
        b.setBorder(BorderFactory.createEmptyBorder(8,12,8,12));
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
    private static class CardPanel extends JPanel {
        CardPanel(){ setOpaque(false); }
        @Override protected void paintComponent(Graphics g){
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(CARD_BG);
            g2.fill(new RoundRectangle2D.Float(0,0,getWidth(),getHeight(),16,16));
            g2.setColor(STROKE);
            g2.draw(new RoundRectangle2D.Float(0.5f,0.5f,getWidth()-1, getHeight()-1,16,16));
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

            if (mode == Mode.TILE) {
                for (int y = 0; y < h; y += ih) {
                    for (int x = 0; x < w; x += iw) {
                        g2.drawImage(img, x, y, this);
                    }
                }
            } else {
                double scale = (mode == Mode.CONTAIN)
                        ? Math.min(w/(double)iw, h/(double)ih)
                        : Math.max(w/(double)iw, h/(double)ih);
                int sw = (int)Math.round(iw*scale), sh = (int)Math.round(ih*scale);
                int x = (w - sw)/2, y = (h - sh)/2;
                g2.drawImage(img, x, y, sw, sh, this);
            }
            g2.dispose();
        }
    }
    private static BufferedImage boostImage(BufferedImage src){
        float contrast = 1.10f;
        RescaleOp rescale = new RescaleOp(
                new float[]{contrast,contrast,contrast,1f},
                new float[]{0f,0f,0f,0f}, null
        );
        BufferedImage tmp = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_ARGB);
        rescale.filter(src, tmp);
        float[] sharp = { 0,-1,0,  -1,5,-1,  0,-1,0 };
        ConvolveOp op = new ConvolveOp(new Kernel(3,3, sharp), ConvolveOp.EDGE_NO_OP, null);
        BufferedImage out = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_ARGB);
        op.filter(tmp, out);
        return out;
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

    // ===== Gráfico de barras enlazado al topModel =====
    private class Top5Chart extends JPanel implements TableModelListener {
        private String[] labels = new String[0];
        private int[] values = new int[0];

        Top5Chart(){
            setOpaque(false);
            setPreferredSize(new Dimension(200, 220));
        }

        @Override public void tableChanged(TableModelEvent e) {
            DefaultTableModel m = (DefaultTableModel) e.getSource();
            int n = m.getRowCount();
            labels = new String[n];
            values = new int[n];
            for (int i=0;i<n;i++){
                labels[i] = String.valueOf(m.getValueAt(i, 0));
                Object v = m.getValueAt(i, 1);
                values[i] = (v instanceof Number)? ((Number)v).intValue() : 0;
            }
            repaint();
        }

        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth(), h = getHeight();
            g2.setColor(CARD_BG);
            g2.fill(new RoundRectangle2D.Float(0,0,w,h,12,12));
            g2.setColor(STROKE);
            g2.draw(new RoundRectangle2D.Float(0.5f,0.5f,w-1,h-1,12,12));

            int pad = 16;
            int x0 = pad + 10;
            int y0 = h - pad - 28;
            int top = pad + 10;
            int availH = y0 - top;

            int n = values.length;
            if (n == 0) {
                g2.setColor(MUTED);
                g2.drawString("Sin datos para graficar.", pad+16, h/2);
                g2.dispose();
                return;
            }

            int max = 1;
            for (int v : values) if (v > max) max = v;

            int gap = 12;
            int barW = Math.max(14, (w - x0 - pad - (gap*(n-1))) / Math.max(1, n));
            int xx = x0;

            g2.setColor(new Color(0xE8EEF5));
            g2.drawLine(x0-8, top, x0-8, y0+2);
            g2.drawLine(x0-8, y0+2, w-pad, y0+2);

            for (int i=0;i<n;i++){
                int v = values[i];
                int bh = (int)Math.round(availH * (v/(double)max));
                int yy = y0 - bh;

                g2.setPaint(new java.awt.GradientPaint(0,yy, new Color(0x9ADCF5),
                                                       0,yy+bh, new Color(0x52B5E8)));
                g2.fillRoundRect(xx, yy, barW, bh, 8, 8);

                g2.setColor(TITLE);
                String vs = String.valueOf(v);
                int sw = g2.getFontMetrics().stringWidth(vs);
                g2.drawString(vs, xx + (barW - sw)/2, yy - 4);

                String lab = labels[i] == null? "" : labels[i];
                lab = lab.length() > 12 ? lab.substring(0, 11) + "…" : lab;
                int lw = g2.getFontMetrics().stringWidth(lab);
                g2.setColor(MUTED);
                g2.drawString(lab, xx + (barW - lw)/2, y0 + 18);

                xx += barW + gap;
            }
            g2.dispose();
        }
    }
    
private static class KpiCard extends javax.swing.JPanel {
    private final javax.swing.JLabel big   = new javax.swing.JLabel("—", javax.swing.SwingConstants.CENTER);
    private final javax.swing.JLabel label = new javax.swing.JLabel("",  javax.swing.SwingConstants.CENTER);

    KpiCard(String text) {
        setLayout(new java.awt.BorderLayout());
        setBackground(java.awt.Color.WHITE);
        setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(new java.awt.Color(230,235,240)),
            new javax.swing.border.EmptyBorder(14,14,14,14)
        ));
        big.setFont(big.getFont().deriveFont(java.awt.Font.BOLD, 24f));
        big.setForeground(new java.awt.Color(0x103C49));
        label.setText(text);
        label.setForeground(new java.awt.Color(0x5B6E7D));
        label.setFont(label.getFont().deriveFont(java.awt.Font.PLAIN, 12f));
        add(big,   java.awt.BorderLayout.CENTER);
        add(label, java.awt.BorderLayout.SOUTH);
    }

    void setValue(String v) { big.setText(v); }
}


}
