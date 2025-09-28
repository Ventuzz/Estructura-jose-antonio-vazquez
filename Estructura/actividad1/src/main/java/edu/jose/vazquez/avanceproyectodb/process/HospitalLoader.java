// Archivo: src/main/java/edu/jose/vazquez/avanceproyectodb/process/HospitalLoader.java
package edu.jose.vazquez.avanceproyectodb.process;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import edu.jose.vazquez.avanceproyectodb.ui.MainDashboard;

public class HospitalLoader extends JFrame {
    // Colores base
    private final Color BG = new Color(0x0b1220);
    private final Color CARD_TOP = new Color(0x101b2e);
    private final Color CARD_BOTTOM = new Color(0x0b1525);
    private final Color TEXT = new Color(0xe6f2ff);
    private final Color MUTED = new Color(0x9db3cc);
    private final Color PRIMARY = new Color(0x2af2d1);
    private final Color ACCENT = new Color(0x06b6d4);

    // UI
    private final JLabel title = new JLabel("Inicializando servicios…", SwingConstants.CENTER);
    private final JLabel desc = new JLabel("Verificando credenciales", SwingConstants.CENTER);
    private final JLabel foot = new JLabel("Conectando a la base de datos  •  Seguridad SSL activa", SwingConstants.CENTER);
    private final RoundedProgressBar bar = new RoundedProgressBar(0, 100);


    private final RingPanel ring = new RingPanel();
    private final EKGPanel ekg = new EKGPanel();

    private final Timer animTimer;   // ~60 FPS
    private final Timer stepTimer;   // Avanza etapas del loader

    private int progress = 12;
    private int stepIndex = 0;

    private final Step[] steps = new Step[] {
        new Step("Inicializando servicios…", "Verificando credenciales", "Conectando a la base de datos", 22),
        new Step("Cargando módulos…", "Pacientes · Doctores · Atención", "Sincronizando catálogos", 46),
        new Step("Comprobando integridad…", "Consultando registros recientes", "Optimizando consultas SQL", 68),
        new Step("Aplicando políticas…", "Permisos y auditoría", "Cifrando sesión", 84),
        new Step("Todo listo", "Abriendo panel principal", "Listo para usar", 100)
    };

    public HospitalLoader() {
        super("EmergenSys – Cargando…");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(720, 420);
        setLocationRelativeTo(null);

        // ===== Icono/Logo desde ruta ABSOLUTA con variantes nítidas =====
try {
  // Intenta varios nombres comunes si quieres
  String[] candidates = {
      "/edu/jose/vazquez/avanceproyectodb/resources/logo.png",
  };

  java.awt.image.BufferedImage base = null;
  java.net.URL found = null;
  for (String path : candidates) {
    java.net.URL u = getClass().getResource(path);
    if (u != null) { 
      base = javax.imageio.ImageIO.read(u);
      found = u;
      break;
    }
  }

  if (base != null) {
    java.util.List<java.awt.Image> icons = new java.util.ArrayList<>();
    // Genera variantes nítidas (Windows usa varias)
    int[] sizes = {16, 20, 24, 28, 32, 40, 48, 64, 128, 256, 512};
    for (int s : sizes) {
      java.awt.image.BufferedImage scaled = new java.awt.image.BufferedImage(
          s, s, java.awt.image.BufferedImage.TYPE_INT_ARGB);
      java.awt.Graphics2D g = scaled.createGraphics();
      g.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION,
                         java.awt.RenderingHints.VALUE_INTERPOLATION_BICUBIC);
      g.setRenderingHint(java.awt.RenderingHints.KEY_RENDERING,
                         java.awt.RenderingHints.VALUE_RENDER_QUALITY);
      g.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
                         java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
      g.drawImage(base, 0, 0, s, s, null);
      g.dispose();

      // Sharpen leve
      float[] sharp = {0,-1,0, -1,5,-1, 0,-1,0};
      java.awt.image.Kernel k = new java.awt.image.Kernel(3,3, sharp);
      java.awt.image.ConvolveOp op = new java.awt.image.ConvolveOp(
          k, java.awt.image.ConvolveOp.EDGE_NO_OP, null);
      java.awt.image.BufferedImage out = new java.awt.image.BufferedImage(
          s, s, java.awt.image.BufferedImage.TYPE_INT_ARGB);
      op.filter(scaled, out);

      icons.add(out);
    }

    setIconImages(icons); // icono de la ventana
    try { java.awt.Taskbar.getTaskbar().setIconImage(icons.get(icons.size()-1)); } catch (Exception ignore) {}

  } else {
    System.err.println("Logo no encontrado en classpath. Asegúrate de colocarlo en src/main/resources/...");
  }
} catch (Exception ignore) {
  ignore.printStackTrace();
}

        JPanel root = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(BG);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        root.setLayout(new GridBagLayout());
        setContentPane(root);

        JPanel card = createCard();
        root.add(card, new GridBagConstraints());

        // Timers
        animTimer = new Timer(16, e -> { ring.tick(); ekg.tick(); });
        animTimer.start();

        stepTimer = new Timer(30, new StepAdvance());
        stepTimer.start();
    }

    private JPanel createCard() {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, CARD_TOP, 0, getHeight(), CARD_BOTTOM);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
                g2.setColor(new Color(255,255,255,30));
                g2.drawRoundRect(1, 1, getWidth()-3, getHeight()-3, 22, 22);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(22, 22, 22, 22));
        card.setLayout(new GridBagLayout());

        JLabel brandIcon = new JLabel(makeMedicalCrossIcon());
        JLabel brandText = new JLabel("EmergenSys");
        brandText.setForeground(TEXT);
        brandText.setFont(brandText.getFont().deriveFont(Font.BOLD, 18f));
        JLabel brandMuted = new JLabel("· Módulos de Gestión");
        brandMuted.setForeground(MUTED);

        JPanel brand = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        brand.setOpaque(false);
        brand.add(brandIcon);
        brand.add(brandText);
        brand.add(brandMuted);

        JPanel left = new JPanel(new BorderLayout());
        left.setOpaque(false);
        left.add(ring, BorderLayout.CENTER);

        JPanel right = new JPanel();
        right.setOpaque(false);
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));

        title.setForeground(TEXT);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));
        desc.setForeground(MUTED);
        desc.setAlignmentX(Component.LEFT_ALIGNMENT);
        desc.setBorder(BorderFactory.createEmptyBorder(6,0,0,0));

        bar.setAlignmentX(Component.LEFT_ALIGNMENT);
        bar.setForeground(PRIMARY);
        bar.setBackground(new Color(255,255,255,30));
        bar.setBorder(BorderFactory.createEmptyBorder());
        bar.setPreferredSize(new Dimension(300, 12));
        bar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 12));
        bar.setValue(progress);

        ekg.setAlignmentX(Component.LEFT_ALIGNMENT);

        foot.setForeground(MUTED);
        foot.setAlignmentX(Component.LEFT_ALIGNMENT);
        foot.setBorder(BorderFactory.createEmptyBorder(8,0,0,0));

        right.add(title);
        right.add(desc);
        right.add(Box.createVerticalStrut(12));
        right.add(bar);
        right.add(Box.createVerticalStrut(12));
        right.add(ekg);
        right.add(foot);

        JPanel grid = new JPanel(new GridBagLayout());
        grid.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0; c.gridy = 0; c.anchor = GridBagConstraints.WEST; c.insets = new Insets(0,0,12,0);
        c.gridwidth = 2; grid.add(brand, c);
        c.gridwidth = 1;
        c.gridy = 1; c.gridx = 0; c.insets = new Insets(0,0,0,16);
        grid.add(left, c);
        c.gridx = 1; c.fill = GridBagConstraints.HORIZONTAL; c.weightx = 1.0;
        grid.add(right, c);

        card.add(grid);
        return card;
    }

    private Icon makeMedicalCrossIcon() {
        int sz = 28;
        BufferedImage img = new BufferedImage(sz, sz, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        GradientPaint gp = new GradientPaint(0, 0, PRIMARY, sz, sz, ACCENT);
        g2.setPaint(gp);
        int r = 6;
        int w = sz, h = sz;
        int arm = sz/3;
        RoundRectangle2D vert = new RoundRectangle2D.Float(w/2f-arm/4f, 2, arm/2f, h-4, r, r);
        RoundRectangle2D horiz = new RoundRectangle2D.Float(2, h/2f-arm/4f, w-4, arm/2f, r, r);
        Area cross = new Area(vert); cross.add(new Area(horiz));
        g2.fill(cross);
        g2.dispose();
        return new ImageIcon(img);
    }

    // ===== Animaciones =====
    private static class Step {
        final String t, d, h; final int to;
        Step(String t, String d, String h, int to){ this.t=t; this.d=d; this.h=h; this.to=to; }
    }

    private class StepAdvance implements ActionListener {
        private long lastUpdate = System.currentTimeMillis();
        @Override public void actionPerformed(ActionEvent e) {
            long now = System.currentTimeMillis();
            float dt = Math.min(0.06f, (now - lastUpdate)/1000f);
            lastUpdate = now;
            int target = steps[Math.min(stepIndex, steps.length-1)].to;
            float newVal = progress + (target - progress) * (0.30f * dt * 50f); // velocidad
            progress = Math.round(newVal);
            bar.setValue(progress);

            if (progress >= target) {
                stepIndex++;
                if (stepIndex < steps.length) {
                    title.setText(steps[stepIndex].t);
                    desc.setText(steps[stepIndex].d);
                    foot.setText(steps[stepIndex].h);
                } else {
                    ((Timer)e.getSource()).stop();
                    animTimer.stop();
                    dispose();  // cerrar loader

                    // Abrir la ventana principal
                    SwingUtilities.invokeLater(MainDashboard::launch);
                }
            }
        }
    }

    private class RingPanel extends JComponent {
        private double angle = 0;
        private final int thickness = 10;
        private final Stroke stroke = new BasicStroke(thickness, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        public RingPanel(){ setPreferredSize(new Dimension(160,160)); setOpaque(false);}        
        void tick(){ angle = (angle + 6) % 360; repaint(); }
        @Override protected void paintComponent(Graphics g){
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int s = Math.min(getWidth(), getHeight());
            int pad = 12;
            int d = s - pad*2;
            int x = (getWidth()-d)/2, y = (getHeight()-d)/2;

            g2.setColor(new Color(255,255,255,25));
            g2.setStroke(new BasicStroke(thickness));
            g2.drawOval(x, y, d, d);

            g2.setStroke(stroke);
            g2.setPaint(new GradientPaint(x, y, PRIMARY, x+d, y+d, ACCENT));
            g2.draw(new Arc2D.Double(x, y, d, d, -angle, 80, Arc2D.OPEN));

            double rad = Math.toRadians(angle+80);
            double cx = x + d/2.0 + (d/2.0) * Math.cos(rad);
            double cy = y + d/2.0 - (d/2.0) * Math.sin(rad);
            int dot = 10;
            g2.setColor(ACCENT);
            g2.fill(new Ellipse2D.Double(cx-dot/2.0, cy-dot/2.0, dot, dot));
            g2.dispose();
        }
    }

    private class EKGPanel extends JComponent {
        private int offset = 0;
        private final int speed = 3;
        private final int EKG_HEIGHT = 70;
        public EKGPanel(){ setPreferredSize(new Dimension(360, EKG_HEIGHT)); setOpaque(false);}        
        void tick(){ offset = (offset + speed) % 600; repaint(); }
        @Override protected void paintComponent(Graphics g){
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.translate(-offset, 0);
            g2.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            GradientPaint gp = new GradientPaint(0, 0, ACCENT, getWidth(), 0, PRIMARY);
            g2.setPaint(gp);
            Path2D path = new Path2D.Float();
            float mid = EKG_HEIGHT/2f;
            path.moveTo(0, mid);
            path.lineTo(60, mid);
            path.lineTo(78, 14);
            path.lineTo(88, 58);
            path.lineTo(98, mid);
            path.lineTo(150, mid);
            path.lineTo(170, 10);
            path.lineTo(188, 64);
            path.lineTo(206, mid);
            path.lineTo(260, mid);
            path.lineTo(298, 20);
            path.lineTo(312, 52);
            path.lineTo(326, mid);
            path.lineTo(380, mid);
            path.lineTo(420, 12);
            path.lineTo(436, 60);
            path.lineTo(452, mid);
            path.lineTo(510, mid);
            path.lineTo(600, mid);
            g2.draw(path);
            g2.translate(600, 0);
            g2.draw(path);
            g2.dispose();
        }
    }

    // ===== Helper: genera variantes nítidas (bicúbico) a varios tamaños =====
    // ===== Helper: variantes nítidas con padding + sharpen =====
// Sin badge, sin fondo: solo tu PNG reescalado (full-bleed si tus logo_XX.png lo son)
private static java.util.List<java.awt.Image> loadIconVariantsFromFile(java.io.File srcImage) throws Exception {
    java.awt.image.BufferedImage base = javax.imageio.ImageIO.read(srcImage);

    int[] sizes = {16, 20, 24, 28, 32, 40, 48, 64, 128, 256, 512};
    java.util.List<java.awt.Image> out = new java.util.ArrayList<>(sizes.length);

    java.io.File dir = srcImage.getParentFile();
    String stem = "logo_"; // si existen logo_16.png, logo_24.png, etc., se usan primero

    for (int s : sizes) {
        java.io.File override = new java.io.File(dir, stem + s + ".png");
        java.awt.image.BufferedImage src = override.exists()
                ? javax.imageio.ImageIO.read(override)
                : base;

        // lienzo s×s transparente
        java.awt.image.BufferedImage scaled = new java.awt.image.BufferedImage(
                s, s, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g = scaled.createGraphics();
        g.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION, java.awt.RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setRenderingHint(java.awt.RenderingHints.KEY_RENDERING,     java.awt.RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,  java.awt.RenderingHints.VALUE_ANTIALIAS_ON);

        // reescalar “a cuadro” (no se dibuja ningún fondo)
        g.drawImage(src, 0, 0, s, s, null);
        g.dispose();

        // leve sharpen para que no se lave al bajar
        float[] sharp = {0,-1,0, -1,5,-1, 0,-1,0};
        java.awt.image.Kernel k = new java.awt.image.Kernel(3,3, sharp);
        java.awt.image.ConvolveOp op = new java.awt.image.ConvolveOp(k, java.awt.image.ConvolveOp.EDGE_NO_OP, null);
        java.awt.image.BufferedImage sharpened = new java.awt.image.BufferedImage(s, s, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        op.filter(scaled, sharpened);

        out.add(sharpened);
    }
    return out;
}

// Barra con esquinas redondeadas y color azul
private static class RoundedProgressBar extends JProgressBar {
    // color de la barra (azul) y del “track” (leve translúcido)
    private Color fill  = new Color(0x2196F3);          // azul
    private Color track = new Color(255, 255, 255, 60); // fondo suave
    private int arc = 14; // radio de las esquinas

    RoundedProgressBar(int min, int max) {
        super(min, max);
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder());
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // pista (track)
        g2.setColor(track);
        g2.fillRoundRect(0, 0, w, h, arc, arc);

        // progreso
        double pct = getPercentComplete();
        if (pct > 0) {
            int pw = (int) Math.round(w * pct);
            g2.setColor(fill);
            // truco para mantener los bordes redondeados a la izquierda y recto si corta a la derecha
            g2.fillRoundRect(0, 0, pw, h, arc, arc);
            if (pw < w) {
                g2.fillRect(pw - arc, 0, arc, h);
            }
        }

        // texto (si usas setStringPainted(true))
        if (isStringPainted()) {
            String s = getString();
            Font f = getFont();
            g2.setFont(f);
            var fm = g2.getFontMetrics(f);
            int sw = fm.stringWidth(s);
            int sh = fm.getAscent();
            g2.setColor(Color.WHITE);
            g2.drawString(s, (w - sw) / 2, (h + sh) / 2 - 1);
        }

        g2.dispose();
    }

    // Si quieres poder cambiar el color desde fuera:
    public void setFill(Color fill)  { this.fill = fill; repaint(); }
    public void setTrack(Color track){ this.track = track; repaint(); }
    public void setArc(int arc)      { this.arc = arc; repaint(); }
}

}



