package edu.jose.vazquez.avanceproyectodb.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;


public class MainDashboard extends JFrame {
  private static final AtomicReference<MainDashboard> INSTANCE = new AtomicReference<>();
  private final ContentRouter router;

  public MainDashboard() {
    super("EmergenSys");

    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setMinimumSize(new Dimension(1100, 700));
    setLocationByPlatform(true);

    // ------- Header -------
    JPanel header = new JPanel(new BorderLayout());
    header.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
    header.setBackground(Colors.PRIMARY);
    JLabel title = new JLabel("EmergenSys");
    title.setForeground(Color.WHITE);
    title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));
    header.add(title, BorderLayout.WEST);

    // ------- Side + Router -------
    SideNav side = new SideNav();
    router = new ContentRouter();
    router.register("PACIENTES", new edu.jose.vazquez.avanceproyectodb.models.PacientesPanel());
    router.register("MEDICOS",   new edu.jose.vazquez.avanceproyectodb.models.DoctoresPanel());
    router.register("ATENCION",  new edu.jose.vazquez.avanceproyectodb.models.AtencionPanel());
    router.register("REPORTES",  new edu.jose.vazquez.avanceproyectodb.models.ReportesPanel());
    router.register("AYUDA",     new edu.jose.vazquez.avanceproyectodb.models.AyudaPanel());
    router.show("PACIENTES");
    side.onNavigate(key -> router.show(key));

    JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, side, router);
    split.setDividerLocation(220);
    split.setResizeWeight(0);
    split.setContinuousLayout(true);
    split.setBorder(BorderFactory.createEmptyBorder());

    getContentPane().setLayout(new BorderLayout());
    getContentPane().add(header, BorderLayout.NORTH);
    getContentPane().add(split, BorderLayout.CENTER);

    // ------- Icono (prefiere logo_512_full.png; si no, usa logo.png/.jpg o .ico) -------
    // ------- Icono nítido desde recursos (classpath), sin rutas absolutas -------
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


    // ------- Tamaño/posición inicial -------
    pack();
    setLocationRelativeTo(null);
    setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
    // setVisible(true) SOLO en launch()
  }

  public static void launch() {
    SwingUtilities.invokeLater(() -> {
      MainDashboard existing = INSTANCE.get();
      if (existing != null) {
        existing.toFront();
        existing.requestFocus();
        return;
      }
      try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
      MainDashboard created = new MainDashboard();
      if (INSTANCE.compareAndSet(null, created)) {
        created.setVisible(true); // único lugar donde hacemos visible la ventana
      } else {
        created.dispose();
        MainDashboard inst = INSTANCE.get();
        if (inst != null) { inst.toFront(); inst.requestFocus(); }
      }
    });
  }

  // ===================== Helpers de icono =====================

  /**
   * Prefiere logo_512_full.png; si no existe, intenta logo.png / logo.jpg;
   * si hay .ico (logo_square_multi.ico o logo.ico), lo usa directamente.
   * Si nada de lo anterior está, toma la primera imagen disponible.
   */
  private static List<Image> loadAppIcons(File dir) throws Exception {
    List<Image> icons = new ArrayList<>();
    if (dir == null || !dir.isDirectory()) return icons;

    File[] prefer = {
        new File(dir, "logo_512_full.png"),
        new File(dir, "logo.png"),
        new File(dir, "logo.jpg"),
        new File(dir, "logo_square_multi.ico"),
        new File(dir, "logo.ico")
    };

    for (File f : prefer) {
      if (f.exists()) {
        if (f.getName().toLowerCase().endsWith(".ico")) {
          URL u = f.toURI().toURL();
          icons.add(Toolkit.getDefaultToolkit().getImage(u));
          return icons;
        } else {
          return loadIconVariantsFromFile(f);
        }
      }
    }

    // Fallback: primera imagen cualquiera en la carpeta
    File[] any = dir.listFiles(g -> g.isFile() && g.getName().matches("(?i).+\\.(png|jpg|jpeg|ico)"));
    if (any != null && any.length > 0) {
      File f = any[0];
      if (f.getName().toLowerCase().endsWith(".ico")) {
        icons.add(Toolkit.getDefaultToolkit().getImage(f.toURI().toURL()));
      } else {
        icons.addAll(loadIconVariantsFromFile(f));
      }
    }
    return icons;
  }

  /** Genera variantes nítidas (16..512 px). Si existen `logo_XX.png` en la carpeta, se usan en preferencia. */
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

}

