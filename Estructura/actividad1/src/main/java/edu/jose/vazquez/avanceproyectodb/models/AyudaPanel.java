package edu.jose.vazquez.avanceproyectodb.models;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.BasicButtonUI;

public class AyudaPanel extends JPanel {

    /* ===== Paleta y estilos para igualar otros paneles ===== */
    private static final Color BG      = new Color(0xF4F7FB);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color STROKE  = new Color(0xE6EBF0);
    private static final Color TITLE   = new Color(0x0B1525);
    private static final Color MUTED   = new Color(0x516173);

    // Contacto y ticket
    private final JTextField tfNombre   = new JTextField(24);
    private final JTextField tfEmail    = new JTextField(24);
    private final JTextField tfTelefono = new JTextField(16);
    private final JComboBox<String> cbTipo = new JComboBox<>(Ayuda.TIPOS_PROBLEMA);
    private final JTextArea taDescripcion  = new JTextArea(8, 26);

    // FAQs
    private final JTextField tfBuscar = new JTextField(22);
    private final DefaultListModel<Ayuda.FaqItem> faqModel = new DefaultListModel<>();
    private final JList<Ayuda.FaqItem> faqList = new JList<>(faqModel);
    private final JTextArea taRespuesta = new JTextArea();

    private final Ayuda ayuda = new Ayuda();

    public AyudaPanel() {
        setLayout(new BorderLayout(12,12));
        setBorder(new EmptyBorder(12,12,12,12));
        setBackground(BG);

        // ===== Header con título =====
        JPanel headerWrap = new JPanel(new BorderLayout());
        headerWrap.setOpaque(false);
        JLabel header = new JLabel("Ayuda y Soporte");
        header.setForeground(TITLE);
        header.setFont(header.getFont().deriveFont(Font.BOLD, 22f));
        headerWrap.add(header, BorderLayout.WEST);
        JPanel hair = new JPanel(); hair.setPreferredSize(new Dimension(1,1)); hair.setBackground(STROKE);
        headerWrap.add(hair, BorderLayout.SOUTH);
        add(headerWrap, BorderLayout.NORTH);

        // ===== Columna izquierda: Contacto & Ticket en card =====
        JPanel contactCard = new CardPanel();
        contactCard.setLayout(new GridBagLayout());
        contactCard.setBorder(new EmptyBorder(16,16,16,16));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8,10,8,10);
        c.anchor = GridBagConstraints.WEST;
        c.fill   = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;

        int r=0;
        // Título de la card
        JLabel tContact = new JLabel("Contacto y Ticket");
        tContact.setFont(tContact.getFont().deriveFont(Font.BOLD, 16f));
        tContact.setForeground(TITLE);
        c.gridx=0; c.gridy=r; c.gridwidth=2; c.weightx=1; contactCard.add(tContact, c); r++;
        c.gridwidth=1;

        c.gridx=0; c.gridy=r; c.weightx=0; contactCard.add(label("Nombre:"), c);
        c.gridx=1; c.gridy=r; c.weightx=1; contactCard.add(tfNombre, c); r++;

        c.gridx=0; c.gridy=r; c.weightx=0; contactCard.add(label("Email:"), c);
        c.gridx=1; c.gridy=r; c.weightx=1; contactCard.add(tfEmail, c); r++;

        c.gridx=0; c.gridy=r; c.weightx=0; contactCard.add(label("Teléfono:"), c);
        c.gridx=1; c.gridy=r; c.weightx=1; contactCard.add(tfTelefono, c); r++;

        c.gridx=0; c.gridy=r; c.weightx=0; contactCard.add(label("Tipo de problema:"), c);
        lockCompact(cbTipo);
        c.gridx=1; c.gridy=r; c.weightx=0; contactCard.add(cbTipo, c); r++;

        c.gridx=0; c.gridy=r; c.weightx=0; c.fill=GridBagConstraints.NONE; contactCard.add(label("Descripción:"), c);
        taDescripcion.setLineWrap(true); taDescripcion.setWrapStyleWord(true);
        JScrollPane spDesc = new JScrollPane(taDescripcion);
        spDesc.setBorder(BorderFactory.createLineBorder(new Color(0xEEF2F6)));
        c.gridx=1; c.gridy=r; c.weightx=1; c.fill=GridBagConstraints.BOTH; c.ipady=84;
        contactCard.add(spDesc, c); r++; c.ipady=0; c.fill=GridBagConstraints.HORIZONTAL;

        JButton btnCorreo = softButton("Enviar Ticket");
        JPanel contactActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        contactActions.setOpaque(false);
        contactActions.add(btnCorreo);

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        contactCard.setAlignmentX(LEFT_ALIGNMENT);
        contactActions.setAlignmentX(LEFT_ALIGNMENT);
        left.add(contactCard);
        left.add(Box.createVerticalStrut(10));
        left.add(contactActions);
        left.setPreferredSize(new Dimension(420, 0));

        // ===== Columna derecha: FAQs en card =====
        JPanel faqCard = new CardPanel();
        faqCard.setLayout(new BorderLayout(10,10));
        faqCard.setBorder(new EmptyBorder(16,16,16,16));

        JLabel tFaq = new JLabel("FAQs");
        tFaq.setFont(tFaq.getFont().deriveFont(Font.BOLD, 16f));
        tFaq.setForeground(TITLE);

        JPanel search = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        search.setOpaque(false);
        search.add(label("Buscar:"));
        search.add(tfBuscar);

        JPanel faqNorth = new JPanel(new BorderLayout());
        faqNorth.setOpaque(false);
        faqNorth.add(tFaq, BorderLayout.NORTH);
        faqNorth.add(search, BorderLayout.SOUTH);

        faqCard.add(faqNorth, BorderLayout.NORTH);

        faqList.setVisibleRowCount(14);
        faqList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        faqList.setFont(faqList.getFont().deriveFont(Font.PLAIN, 14f));
        JScrollPane spList = new JScrollPane(faqList);
        spList.setBorder(BorderFactory.createLineBorder(new Color(0xEEF2F6)));

        taRespuesta.setEditable(false);
        taRespuesta.setLineWrap(true);
        taRespuesta.setWrapStyleWord(true);
        taRespuesta.setFont(taRespuesta.getFont().deriveFont(Font.PLAIN, 14f));
        JScrollPane spResp = new JScrollPane(taRespuesta);
        spResp.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(0xEEF2F6)),
                "Respuesta",
                0, 0, UIManager.getFont("Label.font").deriveFont(Font.BOLD, 12f), MUTED
        ));

        JSplitPane splitFaq = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, spList, spResp);
        splitFaq.setDividerLocation(380);
        splitFaq.setResizeWeight(0.45);
        splitFaq.setBorder(BorderFactory.createEmptyBorder());
        faqCard.add(splitFaq, BorderLayout.CENTER);

        // ===== Ensamble centro =====
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, faqCard);
        split.setDividerLocation(440);
        split.setResizeWeight(0);
        split.setBorder(BorderFactory.createEmptyBorder());

        add(split, BorderLayout.CENTER);

        /* ===== Eventos (tu lógica intacta) ===== */
        btnCorreo.addActionListener(e -> enviarTicket());
        tfBuscar.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { filtrarFaq(); }
            @Override public void removeUpdate(DocumentEvent e) { filtrarFaq(); }
            @Override public void changedUpdate(DocumentEvent e) { filtrarFaq(); }
        });
        faqList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Ayuda.FaqItem it = faqList.getSelectedValue();
                taRespuesta.setText(it == null ? "" : it.respuesta);
                taRespuesta.setCaretPosition(0);
            }
        });

        // Carga inicial
        recargarFaq();
        if (!faqModel.isEmpty()) faqList.setSelectedIndex(0);
    }

    /* ================= Lógica mínima (sin cambios) ================= */
private void enviarTicket() {
    Ayuda.Ticket t = new Ayuda.Ticket();
    t.nombre = tfNombre.getText().trim();
    t.email = tfEmail.getText().trim();
    t.telefono = tfTelefono.getText().trim();
    t.tipoProblema = (String) cbTipo.getSelectedItem();
    t.descripcion = taDescripcion.getText().trim();

    if (t.nombre.isBlank() || t.email.isBlank() || t.descripcion.isBlank()) {
        JOptionPane.showMessageDialog(this, "Nombre, Email y Descripción son obligatorios.",
                "Validación", JOptionPane.WARNING_MESSAGE);
        return;
    }
    try {
        Ayuda.OnEnviarTicket cb = ayuda.getOnEnviarTicket();
        if (cb != null) cb.enviar(t); // callback externo si existe

        JOptionPane.showMessageDialog(this, "Gracias por contactarnos. Te responderemos pronto vía correo",
                "Correo", JOptionPane.INFORMATION_MESSAGE);

        // --- LIMPIEZA DE CAMPOS TRAS ÉXITO ---
        tfNombre.setText("");
        tfEmail.setText("");
        tfTelefono.setText("");
        taDescripcion.setText("");
        if (cbTipo.getItemCount() > 0) cbTipo.setSelectedIndex(0);
        tfNombre.requestFocusInWindow();
        // -------------------------------------

    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "No se pudo enviar: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
    }
}


    private void recargarFaq() {
        faqModel.clear();
        for (Ayuda.FaqItem it : ayuda.getFaq()) faqModel.addElement(it);
    }

    private void filtrarFaq() {
        String q = tfBuscar.getText().toLowerCase(Locale.ROOT).trim();
        List<Ayuda.FaqItem> all = ayuda.getFaq();
        List<Ayuda.FaqItem> filtered = all.stream().filter(f -> f.matches(q)).collect(Collectors.toList());
        faqModel.clear();
        for (Ayuda.FaqItem it : filtered) faqModel.addElement(it);
        taRespuesta.setText("");
        if (!faqModel.isEmpty()) faqList.setSelectedIndex(0);
    }

    private static void lockCompact(JComponent comp) {
        Dimension p = comp.getPreferredSize();
        comp.setPreferredSize(p);
        comp.setMinimumSize(p);
        comp.setMaximumSize(p);
    }

    /* ================== Estética reutilizable ================== */
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
    private static class CardPanel extends JPanel {
        CardPanel(){ setOpaque(false); }
        @Override protected void paintComponent(java.awt.Graphics g){
            java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
            g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(CARD_BG);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
            g2.setColor(STROKE);
            g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 16, 16);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}

