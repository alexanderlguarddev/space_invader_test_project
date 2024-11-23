import javax.swing.JFrame;
import java.awt.event.WindowFocusListener;
import java.awt.Component;

public class Window
{
    // PROPETIES ----------------------------
    private JFrame frame;
    // --------------------------------------
    public Window(String title) {
        frame = new JFrame();
        
        frame.setTitle(title);
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    // PUBLIC -------------------------------
    // SETUP --------------------------------
    public void link(Component comp) { frame.add(comp); }
    
    public void show() {
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        
        frame.setVisible(true);
    }
    
    public void addWindowFocusListener(WindowFocusListener listener) {
        frame.addWindowFocusListener(listener);
    }
    // --------------------------------------
}
