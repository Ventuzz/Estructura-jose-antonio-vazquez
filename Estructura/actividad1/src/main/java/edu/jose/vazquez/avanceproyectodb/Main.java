package edu.jose.vazquez.avanceproyectodb;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.SwingUtilities;

public class Main {
  private static final AtomicBoolean launched = new AtomicBoolean(false);

  private static void openOnce() {
    if (launched.compareAndSet(false, true)) {
      edu.jose.vazquez.avanceproyectodb.ui.MainDashboard.launch();
    }
  }

  public static void main(String[] args){
    SwingUtilities.invokeLater(() -> {
      edu.jose.vazquez.avanceproyectodb.process.HospitalLoader loader =
          new edu.jose.vazquez.avanceproyectodb.process.HospitalLoader();
      loader.addWindowListener(new WindowAdapter() {
        @Override public void windowClosed(WindowEvent e) { openOnce(); }
        @Override public void windowClosing(WindowEvent e) { openOnce(); }
      });
      loader.setLocationRelativeTo(null);
      loader.setVisible(true);
    });
  }
}
