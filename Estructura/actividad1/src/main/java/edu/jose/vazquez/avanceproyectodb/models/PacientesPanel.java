package edu.jose.vazquez.avanceproyectodb.models;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class PacientesPanel extends JPanel {
    // ===== Ajustes de tamaño/escala =====
    private static final float UI_SCALE    = 1.15f; // <- sube/baja todo el panel
    private static final float TITLE_INC_PT = 4f;   // puntos extra al título

    // ---- Paleta ----
    private static final Color BG            = new Color(0xF4F7FB);
    private static final Color CARD_BG       = Color.WHITE;
    private static final Color STROKE        = new Color(0xE6EBF0);
    private static final Color TITLE         = new Color(0x0B1525);
    private static final Color MUTED         = new Color(0x516173);
    private static final Color TABLE_GRID    = new Color(0xEDF2F7);

    // ---- Imagen de fondo para la tabla ----
    private static final String TABLE_BG_RESOURCE = "/edu/jose/vazquez/avanceproyectodb/resources/Logo_EmergenSys.png";
    private static final float  TABLE_BG_ALPHA    = 0.22f; // opacidad del logo

    // ---- Transparencias de filas (0–255) ----
    private static final int ALPHA_EVEN      = 170; // filas pares
    private static final int ALPHA_ODD       = 140; // nones
    private static final int ALPHA_SELECTION = 235; // selección

    // ---- Campos (misma lógica) ----
    private final JTextField tfNombre = new JTextField(20);
    private final JTextField tfFecha  = new JTextField(10);
    private final JComboBox<String> cbSexo = new JComboBox<>(new String[]{"M","F","Otro"});
    private final JTextField tfLada = new JTextField(5);
    private final JTextField tfTel  = new JTextField(12);
    private final JTextField tfEmail = new JTextField(18);
    private final JComboBox<String> cbEstado = new JComboBox<>(new String[]{"EN_ESPERA","EN_EVALUACION","ATENDIDO"});
    private final JSpinner spPrioridad = new JSpinner(new SpinnerNumberModel(3,1,5,1));
    private final JCheckBox chkInconsciente = new JCheckBox("Paciente inconsciente/desconocido");
    private final JTextField tfBuscar = new JTextField(18);

    private final DefaultTableModel model = new DefaultTableModel(
        new Object[]{"ID","Nombre Completo","Sexo","Teléfono","Email","Fecha Nac.","Estado","Prioridad"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(model);
    private Integer selectedPacienteId = null;

    private final PacienteService pacienteService = new PacienteService();
    private final AtencionService atencionService = new AtencionService();

    public PacientesPanel() {
        setLayout(new BorderLayout(12,12));
        setBorder(new EmptyBorder(12,12,12,12));
        setBackground(BG);

        // ===== Header =====
        JPanel headerWrap = new JPanel(new BorderLayout());
        headerWrap.setOpaque(false);

        JLabel header = new JLabel("Registro de Pacientes");
        header.setForeground(TITLE);
        header.setFont(header.getFont().deriveFont(Font.BOLD, header.getFont().getSize2D() + TITLE_INC_PT));
        headerWrap.add(header, BorderLayout.WEST);

        // Hero strip (relleno visual amable)
        JPanel hero = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        hero.setOpaque(false);
        JLabel heroIcon = new JLabel("\u2695"); // símbolo médico
        heroIcon.setFont(heroIcon.getFont().deriveFont(20f));
        JLabel heroText = new JLabel("Administra altas, actualizaciones y consultas de pacientes");
        heroText.setForeground(MUTED);
        hero.add(heroIcon); hero.add(heroText);
        headerWrap.add(hero, BorderLayout.SOUTH);

        add(headerWrap, BorderLayout.NORTH);

        // ===== Form card =====
        JPanel formCard = new CardPanel();
        formCard.setLayout(new GridBagLayout());
        formCard.setBorder(new EmptyBorder(16,16,16,16));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10,12,10,12);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;

        // Hints
        tfNombre.setToolTipText("Nombre completo del paciente");
        tfFecha.setToolTipText("Formato: yyyy-MM-dd");
        tfEmail.setToolTipText("Correo (opcional)");
        tfLada.setToolTipText("LADA (2–5 dígitos)");
        tfTel.setToolTipText("Teléfono (6–14 dígitos)");

        // Filtros numéricos
        attachDigitsOnly(tfLada, 5);
        attachDigitsOnly(tfTel, 14);

        cbSexo.setPrototypeDisplayValue("Otro");      lockToPreferred(cbSexo);
        cbEstado.setPrototypeDisplayValue("EN_EVALUACION"); lockToPreferred(cbEstado);
        ((JSpinner.DefaultEditor) spPrioridad.getEditor()).getTextField().setColumns(2);
        lockToPreferred(spPrioridad);

        int r = 0;
        c.gridx=0; c.gridy=r; c.weightx=0; c.fill=GridBagConstraints.NONE; formCard.add(label("Nombre completo:"), c);
        c.gridx=1; c.gridy=r; c.weightx=1; c.fill=GridBagConstraints.HORIZONTAL; formCard.add(tfNombre, c); r++;

        c.gridx=0; c.gridy=r; c.weightx=0; c.fill=GridBagConstraints.NONE; formCard.add(label("Fecha nac. (yyyy-MM-dd):"), c);
        c.gridx=1; c.gridy=r; c.weightx=1; c.fill=GridBagConstraints.HORIZONTAL; formCard.add(tfFecha, c); r++;

        c.gridx=0; c.gridy=r; c.weightx=0; c.fill=GridBagConstraints.NONE; formCard.add(label("Sexo:"), c);
        c.gridx=1; c.gridy=r; c.weightx=0; c.fill=GridBagConstraints.NONE; formCard.add(cbSexo, c); r++;

        JPanel telRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        telRow.setOpaque(false);
        tfLada.setColumns(5); lockToPreferred(tfLada);
        telRow.add(label("LADA:")); telRow.add(tfLada);
        telRow.add(Box.createHorizontalStrut(6));
        telRow.add(label("Teléfono:")); telRow.add(tfTel);

        c.gridx=0; c.gridy=r; c.weightx=0; c.fill=GridBagConstraints.NONE; formCard.add(label("Contacto:"), c);
        c.gridx=1; c.gridy=r; c.weightx=1; c.fill=GridBagConstraints.HORIZONTAL; formCard.add(telRow, c); r++;

        c.gridx=0; c.gridy=r; c.weightx=0; c.fill=GridBagConstraints.NONE; formCard.add(label("Correo:"), c);
        c.gridx=1; c.gridy=r; c.weightx=1; c.fill=GridBagConstraints.HORIZONTAL; formCard.add(tfEmail, c); r++;

        c.gridx=0; c.gridy=r; c.weightx=0; c.fill=GridBagConstraints.NONE; formCard.add(label("Estado:"), c);
        c.gridx=1; c.gridy=r; c.weightx=0; c.fill=GridBagConstraints.NONE; formCard.add(cbEstado, c); r++;

        c.gridx=0; c.gridy=r; c.weightx=0; c.fill=GridBagConstraints.NONE; formCard.add(label("Prioridad (1-5):"), c);
        c.gridx=1; c.gridy=r; c.weightx=0; c.fill=GridBagConstraints.NONE; formCard.add(spPrioridad, c); r++;

        c.gridx=0; c.gridy=r; c.gridwidth=2; c.weightx=0; c.fill=GridBagConstraints.NONE; formCard.add(chkInconsciente, c); r++;
        c.gridwidth=1;

        // Botones (mismo estilo)
        JButton btnCrear   = softButton("Registrar");
        JButton btnGuardar = softButton("Guardar cambios");
        JButton btnLimpiar = softButton("Limpiar formulario");
        JButton btnActualizar= softButton("Actualizar"); 
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        actions.setOpaque(false);
        actions.add(btnCrear); actions.add(btnGuardar); actions.add(btnLimpiar); actions.add(btnActualizar); 


        // Búsqueda
        JPanel searchCard = new CardPanel();
        searchCard.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 8));
        searchCard.add(label("Buscar por nombre:"));
        tfBuscar.setColumns(18);
        JButton btnBuscar = softButton("Buscar");
        JButton btnTodos  = softButton("Todos");
        searchCard.add(tfBuscar); searchCard.add(btnBuscar); searchCard.add(btnTodos);

        // ===== Bloque “Ayuda rápida” (relleno visual) =====
        JPanel tips = new CardPanel();
        tips.setLayout(new GridLayout(1, 3, 8, 8));
        tips.setBorder(new EmptyBorder(12,12,12,12));
        tips.add(makeTip("\u270D  Registrar", "Captura datos mínimos.\nUsa NN si es desconocido."));
        tips.add(makeTip("\u23F3  Prioridad", "1 = Urgencia alta.\n5 = Baja prioridad."));
        tips.add(makeTip("\uD83D\uDCDE  Contacto", "LADA 2–5 dígitos.\nTel. 6–14 dígitos."));
        // (Si quieres otras frases, cámbialas sin tocar la lógica)

        // Columna izquierda
        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        formCard.setAlignmentX(LEFT_ALIGNMENT);
        actions.setAlignmentX(LEFT_ALIGNMENT);
        searchCard.setAlignmentX(LEFT_ALIGNMENT);
        tips.setAlignmentX(LEFT_ALIGNMENT);
        left.add(formCard);
        left.add(Box.createVerticalStrut(10));
        left.add(actions);
        left.add(Box.createVerticalStrut(10));
        left.add(searchCard);
        left.add(Box.createVerticalStrut(10));
        left.add(tips);
        left.setPreferredSize(new Dimension(480, 0));
        left.setMinimumSize(new Dimension(160, 0)); 

        // ===== Tabla + fondo (más “vivo”) =====
        styleTable(table);

        // Renderer translúcido (todas las columnas)
        TranslucentCellRenderer translucent = new TranslucentCellRenderer();
        table.setDefaultRenderer(Object.class, translucent);
        table.setDefaultRenderer(Integer.class, translucent);

        JScrollPane tableScroll = new JScrollPane();
        tableScroll.setBorder(BorderFactory.createMatteBorder(1,1,1,1, STROKE));

        try {
            URL imgUrl = getClass().getResource(TABLE_BG_RESOURCE);
            if (imgUrl != null) {
                BufferedImage raw = ImageIO.read(imgUrl);
                BufferedImage boosted = boostImage(raw);
                ImageViewport vp = new ImageViewport(boosted, TABLE_BG_ALPHA, ImageViewport.Mode.COVER);
                vp.setView(table);
                tableScroll.setViewport(vp);
            } else {
                tableScroll.setViewportView(table);
            }
        } catch (Exception ex) {
            tableScroll.setViewportView(table);
        }

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, tableScroll);
        split.setDividerLocation(580);
        split.setResizeWeight(0);
        split.setContinuousLayout(true);
        split.setBorder(BorderFactory.createEmptyBorder());
        add(split, BorderLayout.CENTER);

        // Listeners (misma lógica)
        chkInconsciente.addActionListener(e -> {
            if (chkInconsciente.isSelected()) {
                limpiar();
                tfNombre.setText("NN");
                cbEstado.setSelectedItem("EN_EVALUACION");
                spPrioridad.setValue(1);
            }
        });

        btnCrear.addActionListener(e -> registrar());
        btnGuardar.addActionListener(e -> guardarCambios());
        btnLimpiar.addActionListener(e -> limpiar());
        btnBuscar.addActionListener(e -> cargarTabla(tfBuscar.getText().trim()));
        btnTodos.addActionListener(e -> { tfBuscar.setText(""); cargarTabla(null); });
        btnActualizar.addActionListener(e -> refrescarTabla());


        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { selectedPacienteId = null; return; }
            selectedPacienteId = (Integer) model.getValueAt(row, 0);
            tfNombre.setText(String.valueOf(model.getValueAt(row, 1)));
            cbSexo.setSelectedItem(String.valueOf(model.getValueAt(row, 2)));
            String fullTel = String.valueOf(model.getValueAt(row, 3));
            splitTelefono(fullTel);
            tfEmail.setText(String.valueOf(model.getValueAt(row, 4)));
            tfFecha.setText(String.valueOf(model.getValueAt(row, 5)));
            cbEstado.setSelectedItem(String.valueOf(model.getValueAt(row, 6)));
            spPrioridad.setValue(model.getValueAt(row, 7));
        });

        // === Escalado global de fuentes del panel ===
        applyFontScale(this, UI_SCALE);

        cargarTabla(null);
    }

    private void refrescarTabla() {
    String q = tfBuscar.getText().trim();
    cargarTabla(q.isBlank() ? null : q);  // respeta el filtro actual si hay texto
}


    // ===== Estética =====
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
        t.setOpaque(false); // deja ver el fondo
        t.setRowHeight(30); // un poco más alto por fuente mayor
        t.setGridColor(TABLE_GRID);

        JTableHeader h = t.getTableHeader();
        h.setBackground(new Color(0xF3F7FB));
        h.setForeground(new Color(0x0B1525));
        h.setFont(h.getFont().deriveFont(h.getFont().getSize2D() * 1.10f).deriveFont(Font.BOLD)); // header más grande
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

    // Card redondeada
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

    // Renderer translúcido por celda (corrige errores previos)
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

            int alpha = selected ? ALPHA_SELECTION : (row >= 0 ? ((row % 2 == 0) ? ALPHA_EVEN : ALPHA_ODD) : ALPHA_EVEN);
            g2.setColor(new Color(255,255,255, alpha));
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.dispose();

            super.paintComponent(g);
        }
    }

    // Viewport con imagen de fondo (cover)
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
                for (int y = 0; y < h; y += ih) for (int x = 0; x < w; x += iw)
                    g2.drawImage(img, x, y, this);
            } else if (mode == Mode.CONTAIN) {
                double scale = Math.min(w / (double)iw, h / (double)ih);
                int sw = (int)Math.round(iw*scale), sh = (int)Math.round(ih*scale);
                int x = (w - sw)/2, y = (h - sh)/2;
                g2.drawImage(img, x, y, sw, sh, this);
            } else { // COVER
                double scale = Math.max(w / (double)iw, h / (double)ih);
                int sw = (int)Math.round(iw*scale), sh = (int)Math.round(ih*scale);
                int x = (w - sw)/2, y = (h - sh)/2;
                g2.drawImage(img, x, y, sw, sh, this);
            }
            g2.dispose();
        }
    }

    // Boost de contraste/claridad (ligero) para “vivir” el logo
    private static BufferedImage boostImage(BufferedImage src){
        float contrast = 1.08f;
        float brightness = 1.00f;
        RescaleOp rescale = new RescaleOp(
                new float[]{contrast,contrast,contrast,1f},
                new float[]{(brightness-1f)*255f,(brightness-1f)*255f,(brightness-1f)*255f,0f},
                null
        );
        BufferedImage tmp = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_ARGB);
        rescale.filter(src, tmp);

        float[] sharp = { 0,-1,0,  -1,5,-1,  0,-1,0 };
        ConvolveOp op = new ConvolveOp(new Kernel(3,3, sharp), ConvolveOp.EDGE_NO_OP, null);
        BufferedImage out = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_ARGB);
        op.filter(tmp, out);
        return out;
    }

    // ===== Helpers UI =====
    private static void lockToPreferred(JComponent comp) {
        Dimension pref = comp.getPreferredSize();
        comp.setPreferredSize(pref);
        comp.setMinimumSize(pref);
        comp.setMaximumSize(pref);
    }
    private static void attachDigitsOnly(JTextField field, int maxLen) {
        ((AbstractDocument) field.getDocument()).setDocumentFilter(new DigitsOnlyFilter(maxLen));
        field.setInputVerifier(new InputVerifier() {
            @Override public boolean verify(JComponent input) {
                String s = ((JTextField)input).getText().trim();
                return s.isEmpty() || s.matches("\\d{1,"+maxLen+"}");
            }
        });
    }
    private static class DigitsOnlyFilter extends DocumentFilter {
        private final int maxLen;
        private final Pattern p = Pattern.compile("\\d*");
        DigitsOnlyFilter(int maxLen) { this.maxLen = maxLen; }
        @Override public void insertString(FilterBypass fb, int off, String str, AttributeSet a) throws BadLocationException {
            if (str == null) return;
            String clean = str.replaceAll("\\D", "");
            if (clean.isEmpty()) return;
            if (!p.matcher(clean).matches()) return;
            int newLen = fb.getDocument().getLength() + clean.length();
            if (newLen > maxLen) clean = clean.substring(0, Math.max(0, maxLen - fb.getDocument().getLength()));
            if (!clean.isEmpty()) super.insertString(fb, off, clean, a);
        }
        @Override public void replace(FilterBypass fb, int off, int len, String str, AttributeSet a) throws BadLocationException {
            String clean = (str == null) ? "" : str.replaceAll("\\D", "");
            StringBuilder current = new StringBuilder();
            try { current.append(fb.getDocument().getText(0, fb.getDocument().getLength())); } catch (BadLocationException ignored) {}
            String result = current.replace(off, off + len, clean).toString();
            if (result.length() > maxLen) result = result.substring(0, maxLen);
            if (!result.matches("\\d{0,"+maxLen+"}")) return;
            super.replace(fb, 0, fb.getDocument().getLength(), result, a);
        }
    }

    // ===== Teléfono/LADA =====
    private static final Pattern PAREN_LADA = Pattern.compile("^\\s*\\((\\d{2,5})\\)\\s*(\\d.*)$");
    private static final Pattern PLUSCC     = Pattern.compile("^\\s*\\+\\d{1,3}\\s*\\((\\d{2,5})\\)\\s*(\\d.*)$");

    private void splitTelefono(String full) {
        tfLada.setText(""); tfTel.setText("");
        if (full == null) return;
        Matcher m1 = PAREN_LADA.matcher(full);
        Matcher m2 = PLUSCC.matcher(full);
        if (m1.find()) {
            tfLada.setText(m1.group(1));
            tfTel.setText(m1.group(2).replaceAll("\\D", ""));
        } else if (m2.find()) {
            tfLada.setText(m2.group(1));
            tfTel.setText(m2.group(2).replaceAll("\\D", ""));
        } else {
            tfTel.setText(full.replaceAll("\\D", ""));
        }
    }
    private String buildTelefono() {
        String lada = tfLada.getText().trim();
        String tel  = tfTel.getText().trim();
        if (!lada.isEmpty() && !lada.matches("\\d{2,5}"))
            throw new IllegalArgumentException("La LADA debe tener 2 a 5 dígitos.");
        if (!tel.isEmpty() && !tel.matches("\\d{6,14}"))
            throw new IllegalArgumentException("El teléfono debe tener solo dígitos (6-14).");
        if (lada.isEmpty()) return tel;
        if (tel.isEmpty())  return "(" + lada + ")";
        return "(" + lada + ") " + tel;
    }

    // ===== Lógica original (sin cambios) =====
    private void registrar() {
        try {
            var p = new PacienteService.Paciente();
            p.nombreCompleto = tfNombre.getText().trim();
            if (p.nombreCompleto.isBlank()) {
                JOptionPane.showMessageDialog(this, "El nombre completo es obligatorio.", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
            p.sexo = (String) cbSexo.getSelectedItem();
            p.telefono = buildTelefono();
            p.correo = tfEmail.getText().trim();
            p.fechaNacimiento = tfFecha.getText().trim().isBlank() ? null : tfFecha.getText().trim();
            p.estado = (String) cbEstado.getSelectedItem();
            p.prioridad = (Integer) spPrioridad.getValue();

            int id = pacienteService.crear(p);
            cargarTabla(null);
            JOptionPane.showMessageDialog(this, "Paciente registrado. ID: " + id);
            limpiar();

        } catch (IllegalArgumentException iae) {
            JOptionPane.showMessageDialog(this, iae.getMessage(), "Validación", JOptionPane.WARNING_MESSAGE);
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
                JOptionPane.showMessageDialog(this, "El paciente está 'ATENDIDO'. No se puede actualizar.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            var p = new PacienteService.Paciente();
            p.id = selectedPacienteId;
            p.nombreCompleto = tfNombre.getText().trim();
            p.sexo = (String) cbSexo.getSelectedItem();
            p.telefono = buildTelefono();
            p.correo = tfEmail.getText().trim();
            p.fechaNacimiento = tfFecha.getText().trim().isBlank() ? null : tfFecha.getText().trim();
            p.estado = (String) cbEstado.getSelectedItem();
            p.prioridad = (Integer) spPrioridad.getValue();

            pacienteService.actualizarBasico(p);
            cargarTabla(tfBuscar.getText().trim().isBlank()? null : tfBuscar.getText().trim());
            JOptionPane.showMessageDialog(this, "Paciente actualizado.");
        } catch (IllegalArgumentException iae) {
            JOptionPane.showMessageDialog(this, iae.getMessage(), "Validación", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.toString(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarTabla(String filtro) {
        model.setRowCount(0);
        for (var p : pacienteService.listar(filtro)) {
            model.addRow(new Object[]{
                p.id, p.nombreCompleto, p.sexo, p.telefono, p.correo,
                p.fechaNacimiento==null? "" : p.fechaNacimiento,
                p.estado, p.prioridad
            });
        }
    }

    private void limpiar() {
        selectedPacienteId = null;
        tfNombre.setText(""); tfFecha.setText(""); cbSexo.setSelectedIndex(0);
        tfLada.setText(""); tfTel.setText(""); tfEmail.setText("");
        cbEstado.setSelectedItem("EN_ESPERA"); spPrioridad.setValue(3);
        table.clearSelection();
        tfNombre.requestFocusInWindow();
    }

    // ===== Escala fuentes recursivamente =====
    private static void applyFontScale(Component root, float scale) {
        if (root == null || scale == 1f) return;
        Font f = root.getFont();
        if (f != null) {
            root.setFont(f.deriveFont(f.getSize2D() * scale));
        }
        if (root instanceof Container) {
            for (Component child : ((Container) root).getComponents()) {
                applyFontScale(child, scale);
            }
        }
    }
}
