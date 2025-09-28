package edu.jose.vazquez.avanceproyectodb.ui;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
public class SideNav extends JPanel{
  private final Map<String,JButton> buttons=new LinkedHashMap<>(); private Consumer<String> navigate;
  public SideNav(){ setLayout(new BorderLayout()); setBackground(Colors.SIDE_BG);
    JLabel brand=new JLabel("  \uD83C\uDFE5  DashBoard"); brand.setForeground(Colors.SIDE_TEXT);
    brand.setFont(brand.getFont().deriveFont(Font.BOLD,18f));
    brand.setBorder(BorderFactory.createEmptyBorder(16,12,16,12)); add(brand,BorderLayout.NORTH);
    JPanel list=new JPanel(); list.setLayout(new BoxLayout(list,BoxLayout.Y_AXIS)); list.setOpaque(false);
    addButton(list,"PACIENTES","Pacientes"); addButton(list,"MEDICOS","Médicos"); addButton(list,"ATENCION","Atención"); addButton(list,"REPORTES","Reportes"); addButton(list,"AYUDA","Ayuda");
    JScrollPane sp=new JScrollPane(list); sp.setBorder(BorderFactory.createEmptyBorder()); sp.getViewport().setOpaque(false); sp.setOpaque(false);
    add(sp,BorderLayout.CENTER);
  }
  private void addButton(JPanel container,String key,String text){
    JButton b=new JButton(text){ @Override public boolean isDefaultButton(){ return false; }};
    b.setAlignmentX(Component.LEFT_ALIGNMENT); b.setFocusPainted(false); b.setHorizontalAlignment(SwingConstants.LEFT);
    b.setForeground(Colors.SIDE_TEXT); b.setOpaque(false); b.setContentAreaFilled(false); b.setBorderPainted(false);
    b.setFont(b.getFont().deriveFont(18f)); // tamaño de letra
    b.setFont(b.getFont().deriveFont(java.awt.Font.BOLD, 18f)); // pal grosor
    b.setBorder(BorderFactory.createEmptyBorder(12,16,12,12));
    b.addActionListener(e->{ if(navigate!=null) navigate.accept(key); highlight(key); });
    buttons.put(key,b); container.add(b);
    b.addMouseListener(new java.awt.event.MouseAdapter(){ @Override public void mouseEntered(java.awt.event.MouseEvent e){ b.setForeground(Color.WHITE);} @Override public void mouseExited(java.awt.event.MouseEvent e){ b.setForeground(Colors.SIDE_TEXT);} });
  }
  private void highlight(String key){ for (var e:buttons.entrySet()){ boolean sel=e.getKey().equals(key); e.getValue().setFont(e.getValue().getFont().deriveFont(sel?Font.BOLD:Font.PLAIN)); } }
  public void onNavigate(Consumer<String> n){ this.navigate=n; }
}
