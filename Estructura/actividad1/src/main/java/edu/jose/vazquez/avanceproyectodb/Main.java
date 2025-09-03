package edu.jose.vazquez.avanceproyectodb;

import javax.swing.*;

import edu.jose.vazquez.avanceproyectodb.models.AtencionPanel;
import edu.jose.vazquez.avanceproyectodb.models.DoctoresPanel;
import edu.jose.vazquez.avanceproyectodb.models.PacientesPanel;

import java.awt.*;

public class Main {
    public static void main(String[] args) {
        // Look & Feel del sistema (opcional)
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}

        EventQueue.invokeLater(() -> {
            JFrame f = new JFrame("Gestión Hospitalaria (Swing)");
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setSize(1000, 650);

            JTabbedPane tabs = new JTabbedPane();
            tabs.addTab("Pacientes", new PacientesPanel());
            tabs.addTab("Doctores", new DoctoresPanel());
            tabs.addTab("Atención", new AtencionPanel());

            f.setContentPane(tabs);
            f.setLocationRelativeTo(null);
            f.setVisible(true);
        });
    }
}
