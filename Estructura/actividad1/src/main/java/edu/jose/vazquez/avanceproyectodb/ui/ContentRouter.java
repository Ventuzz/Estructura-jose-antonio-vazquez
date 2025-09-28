package edu.jose.vazquez.avanceproyectodb.ui;
import javax.swing.*; import java.awt.*; import java.util.*;
public class ContentRouter extends JPanel{
  private final CardLayout card=new CardLayout();
  private final Map<String,JComponent> views=new LinkedHashMap<>();
  public ContentRouter(){ setLayout(card); setBackground(Colors.BG); }
  public void register(String key,JComponent v){ views.put(key,v); add(wrap(v),key); }
  public void show(String key){ card.show(this,key); }
  private JComponent wrap(JComponent c){ JPanel p=new JPanel(new BorderLayout()); p.setBackground(Colors.BG); p.add(c,BorderLayout.CENTER); p.setBorder(BorderFactory.createEmptyBorder(16,16,16,16)); return p; }
}
