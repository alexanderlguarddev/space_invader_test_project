import java.util.ArrayList;
import java.awt.Dimension;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.event.FocusListener;
import java.awt.event.FocusEvent;

public class Panel extends JPanel
{
    // PROPETIES ----------------------------
    private ArrayList<Renderable> renderablesEntities;
    private boolean focus;
    // --------------------------------------
    
    public Panel(int width, int height) {
        renderablesEntities = new ArrayList<Renderable>();
        
        Dimension size = new Dimension(width, height);
        
        setMinimumSize(size);
        setPreferredSize(size);
        setMaximumSize(size);
        focus = false;
        
        this.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                focus = true;
            }

            @Override
            public void focusLost(FocusEvent e) {
                focus = false;
            }
        });
    }
    
    // PUBLIC -------------------------------
    // SETUP --------------------------------
    public void addRenderListener(Renderable listener) { renderablesEntities.add(listener); }
    // GETTER -------------------------------
    public boolean hasFocus() {
        return this.focus;
    }
    // --------------------------------------
    
    // OVERRIDE -----------------------------
    // JPANEL -------------------------------
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        for (Renderable listener : renderablesEntities)
            listener.render(g);
    }
    // --------------------------------------
}