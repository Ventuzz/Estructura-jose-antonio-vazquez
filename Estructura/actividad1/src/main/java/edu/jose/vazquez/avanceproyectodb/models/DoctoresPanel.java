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
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import edu.jose.vazquez.avanceproyectodb.process.DoctorService;

public class DoctoresPanel extends JPanel {
    /* ===== Ajustes de escala/tipografía (como PacientesPanel) ===== */
    private static final float UI_SCALE     = 1.12f; // escala global de fuentes
    private static final float TITLE_INC_PT = 4f;    // puntos extra al título

    /* ===== Paleta/UI ===== */
    private static final Color BG         = new Color(0xF4F7FB);
    private static final Color CARD_BG    = Color.WHITE;
    private static final Color STROKE     = new Color(0xE6EBF0);
    private static final Color TITLE      = new Color(0x0B1525);
    private static final Color MUTED      = new Color(0x516173);
    private static final Color GRID       = new Color(0xEDF2F7);

    // Fondo de la tabla (logo)
    // Cambia el nombre si tu PNG se llama diferente (p.ej. "/.../tabla_bg.png")
    private static final String TABLE_BG_RESOURCE = "/edu/jose/vazquez/avanceproyectodb/resources/Logo_EmergenSys.png";
    private static final float  TABLE_BG_ALPHA    = 0.22f;  // 0= invisible, 1= opaco

    // Transparencia por fila (0–255)
    private static final int ALPHA_EVEN      = 170;
    private static final int ALPHA_ODD       = 140;
    private static final int ALPHA_SELECTION = 235;

    /* ===== Campos (misma lógica) ===== */
    private final JTextField tfNombre   = new JTextField(20);
    private final JTextField tfCorreo   = new JTextField(20);
    private final JTextField tfTelefono = new JTextField(12);
    private final JComboBox<DoctorService.Especialidad> cbEspecialidad = new JComboBox<>();
    private final JTextField tfBuscar = new JTextField(16);

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID", "Nombre", "Correo", "Teléfono", "Especialidad"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(model);
    private Integer selectedDoctorId = null;

    private final DoctorService service = new DoctorService();

    public DoctoresPanel() {
        // Layout general
        setLayout(new BorderLayout(12, 12));
        setBorder(new EmptyBorder(12, 12, 12, 12));
        setBackground(BG);

        // ===== Header + Hero strip =====
        JPanel headerWrap = new JPanel(new BorderLayout());
        headerWrap.setOpaque(false);

        JLabel header = new JLabel("Registro de Doctores");
        header.setForeground(TITLE);
        header.setFont(header.getFont().deriveFont(Font.BOLD, header.getFont().getSize2D() + TITLE_INC_PT));
        headerWrap.add(header, BorderLayout.WEST);

        JPanel hero = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        hero.setOpaque(false);
        JLabel heroIcon = new JLabel("\u2695"); // símbolo médico
        heroIcon.setFont(heroIcon.getFont().deriveFont(20f));
        JLabel heroText = new JLabel("Administra alta, edición y búsqueda de médicos por especialidad");
        heroText.setForeground(MUTED);
        hero.add(heroIcon); hero.add(heroText);
        headerWrap.add(hero, BorderLayout.SOUTH);

        add(headerWrap, BorderLayout.NORTH);

        // ===== Formulario en Card (izquierda) =====
        JPanel form = new CardPanel();
        form.setLayout(new GridBagLayout());
        form.setBorder(new EmptyBorder(16,16,16,16));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10,12,10,12);
        c.anchor = GridBagConstraints.WEST;
        c.fill   = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;

        int r = 0;

        // Nombre
        c.gridx = 0; c.gridy = r; c.weightx = 0; c.fill = GridBagConstraints.NONE; form.add(label("Nombre:"), c);
        c.gridx = 1; c.gridy = r; c.weightx = 1; c.fill = GridBagConstraints.HORIZONTAL; form.add(tfNombre, c); r++;

        // Correo
        c.gridx = 0; c.gridy = r; c.weightx = 0; c.fill = GridBagConstraints.NONE; form.add(label("Correo:"), c);
        c.gridx = 1; c.gridy = r; c.weightx = 1; c.fill = GridBagConstraints.HORIZONTAL; form.add(tfCorreo, c); r++;

        // Teléfono
        c.gridx = 0; c.gridy = r; c.weightx = 0; c.fill = GridBagConstraints.NONE; form.add(label("Teléfono:"), c);
        c.gridx = 1; c.gridy = r; c.weightx = 1; c.fill = GridBagConstraints.HORIZONTAL; form.add(tfTelefono, c); r++;

        // Especialidad (compacta, sin objetos dummy)
        c.gridx = 0; c.gridy = r; c.weightx = 0; c.fill = GridBagConstraints.NONE; form.add(label("Especialidad:"), c);
        Dimension pref = cbEspecialidad.getPreferredSize();
        int anchoDeseado = new JLabel("____________________________").getPreferredSize().width + 24;
        cbEspecialidad.setPreferredSize(new Dimension(Math.max(pref.width, anchoDeseado), pref.height));
        cbEspecialidad.setMinimumSize(cbEspecialidad.getPreferredSize());
        cbEspecialidad.setMaximumSize(cbEspecialidad.getPreferredSize());
        c.gridx = 1; c.gridy = r; c.weightx = 0; c.fill = GridBagConstraints.NONE; form.add(cbEspecialidad, c); r++;

        // ===== Acciones =====
        JButton btnCrear   = softButton("Registrar");
        JButton btnGuardar = softButton("Guardar cambios");

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        actions.setOpaque(false);
        actions.add(btnCrear);
        actions.add(btnGuardar);

        // ===== Búsqueda (Card) =====
        JPanel search = new CardPanel();
        search.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 8));
        JButton btnBuscar = softButton("Buscar");
        JButton btnTodos  = softButton("Todos");
        search.add(label("Buscar:"));
        search.add(tfBuscar);
        search.add(btnBuscar);
        search.add(btnTodos);

        // ===== “Ayuda rápida” (relleno visual) =====
        JPanel tips = new CardPanel();
        tips.setLayout(new GridLayout(1, 3, 8, 8));
        tips.setBorder(new EmptyBorder(12,12,12,12));
        tips.add(makeTip("\u270D  Registrar", "Captura nombre, correo y teléfono.\nElige una especialidad."));
        tips.add(makeTip("\u260E  Contacto", "Formatea el teléfono de forma consistente.\nEj. 5551234567"));
        tips.add(makeTip("\uD83D\uDD0D  Búsqueda", "Filtra por nombre o muestra todos."));

        // Columna izquierda apilada
        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        form.setAlignmentX(LEFT_ALIGNMENT);
        actions.setAlignmentX(LEFT_ALIGNMENT);
        search.setAlignmentX(LEFT_ALIGNMENT);
        tips.setAlignmentX(LEFT_ALIGNMENT);
        left.add(form);
        left.add(Box.createVerticalStrut(10));
        left.add(actions);
        left.add(Box.createVerticalStrut(10));
        left.add(search);
        left.add(Box.createVerticalStrut(10));
        left.add(tips);
        left.setPreferredSize(new Dimension(480, 0));
        left.setMinimumSize(new Dimension(160, 0));

        // ===== Tabla (derecha) con fondo y filas traslúcidas =====
        styleTable(table);

        TranslucentCellRenderer translucent = new TranslucentCellRenderer();
        table.setDefaultRenderer(Object.class, translucent);
        table.setDefaultRenderer(Integer.class, translucent);

        JScrollPane sp = new JScrollPane();
        sp.setBorder(BorderFactory.createMatteBorder(1,1,1,1, STROKE));
        try {
            URL imgUrl = getClass().getResource(TABLE_BG_RESOURCE);
            if (imgUrl != null) {
                BufferedImage raw = ImageIO.read(imgUrl);
                BufferedImage boosted = boostImage(raw); // realce leve para que “viva” más
                ImageViewport vp = new ImageViewport(boosted, TABLE_BG_ALPHA, ImageViewport.Mode.COVER);
                vp.setView(table);
                sp.setViewport(vp);
            } else {
                sp.setViewportView(table);
            }
        } catch (Exception ex) {
            sp.setViewportView(table);
        }

        // Split
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, sp);
        split.setDividerLocation(580);
        split.setResizeWeight(0);
        split.setContinuousLayout(true);
        split.setBorder(BorderFactory.createEmptyBorder());
        add(split, BorderLayout.CENTER);

        /* ================= Listeners (lógica intacta) ================= */
        btnCrear.addActionListener(e -> registrar());
        btnGuardar.addActionListener(e -> guardarCambios());

        btnBuscar.addActionListener(e -> cargarTabla(tfBuscar.getText().trim()));
        btnTodos.addActionListener(e -> { tfBuscar.setText(""); cargarTabla(null); });

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { selectedDoctorId = null; return; }
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

        // Cargas iniciales
        cargarEspecialidades();
        cargarTabla(null);

        // Escalado global de fuentes (como en PacientesPanel)
        applyFontScale(this, UI_SCALE);
    }

    /* ================= Estética reutilizable ================= */
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
        // contraste leve
        float contrast = 1.08f;
        RescaleOp rescale = new RescaleOp(
                new float[]{contrast,contrast,contrast,1f},
                new float[]{0f,0f,0f,0f}, null
        );
        BufferedImage tmp = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_ARGB);
        rescale.filter(src, tmp);
        // sharpen leve
        float[] sharp = { 0,-1,0,  -1,5,-1,  0,-1,0 };
        ConvolveOp op = new ConvolveOp(new Kernel(3,3, sharp), ConvolveOp.EDGE_NO_OP, null);
        BufferedImage out = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_ARGB);
        op.filter(tmp, out);
        return out;
    }

    /* ================= Lógica ORIGINAL (igual que tu clase) ================= */

    private void cargarEspecialidades() {
        try {
            List<DoctorService.Especialidad> especialidades = service.listarEspecialidades();
            cbEspecialidad.removeAllItems();
            for (DoctorService.Especialidad esp : especialidades) {
                cbEspecialidad.addItem(esp);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "No se pudieron cargar las especialidades: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
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

    /* ===== Escalado de fuentes en árbol ===== */
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
}
