import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.awt.Graphics;

public class Sprite extends Essence
{
    // PROPETIES ----------------------------
    private int animationtick;
    private int animationindex; 
    private int animationspeed;
    
    HashMap<String, AnimationPackage> strips;
    
    private String currentStrip;
    // --------------------------------------
    
    public Sprite(int x, int y, int w, int h) {
        super(x, y, w, h);
        
        animationspeed = 15;
        animationindex = 0;
        animationtick = 0;
        
        strips = new HashMap<String, AnimationPackage>();
        
        currentStrip = "";
    }
    
    // PRIVATE ------------------------------
    // CHECK ANIMATION ----------------------
    private boolean canShowSprite() {
        return (strips.containsKey(currentStrip) && strips.get(currentStrip) != null && !strips.get(currentStrip).isEmpty());
    }
    
    // Control Animation --------------------
    private void updateAnimationTick() {
        if (animationtick >= animationspeed) {
            animationtick = 0;
            animationindex++;
            
            if (animationindex > getAnimationLimit()) {
                animationindex = 0;
                onEndStrip();
            }
            
            return;
        }
        
        animationtick++;
    }
    // --------------------------------------
    
    // PROTECTED ----------------------------
    // CHECKERS -----------------------------
    protected boolean checkCurrentStrip(String strip_name) { return currentStrip.equals(strip_name); }
    // PSEUDO EVENTS ------------------------
    protected void onEndStrip() {}
    // SETTERS ------------------------------
    protected void setCurrentStrip(String strip_name) { 
        currentStrip = strip_name; 
        animationtick = 0;
        animationindex = 0;
    }
    protected void setAnimationspeed(int speed) { animationspeed = speed; }
    
    // GETTERS ------------------------------
    protected int getAnimationLimit() {
        if (!strips.containsKey(currentStrip)) return -1;
        
        AnimationPackage animation = strips.get(currentStrip);
        
        if (animation == null || animation.isEmpty()) return -1;
        else return animation.getCurrentStripLimit();
    }
    
    // ANIMATION CONTROL --------------------
    protected void registerNewStrip(String title, String url, int row_index, int image_count) {
        AnimationPackage strip = new AnimationPackage(url, row_index, image_count, imageWidth(), imageHeight());
        
        if (!strip.isEmpty()) strips.put(title, strip);
    }
    // --------------------------------------
    
    // OVERRIDE -----------------------------
    // RENDERABLE ---------------------------
    @Override
    public void update() {
        super.update();
        
        updateAnimationTick();
    }
    
    @Override
    public void render(Graphics g) {
        super.render(g);
        
        if (canShowSprite()) {
            AnimationPackage strip = strips.get(currentStrip);
            
            g.drawImage(strip.get(animationindex), x(), y(), width(), height(), null);
        }
    }
    // --------------------------------------
    
    // --------------------------------------
}