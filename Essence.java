import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collections;
import java.awt.Point;

public class Essence implements Renderable, Comparable<Essence>
{
    // STATIC VALUES ------------------------
    private static final ArrayList<Essence> ELEMENTS = new ArrayList<Essence>();
    // --------------------------------------
    
    // PROPETIES ----------------------------
    private int id;
    private Size size;
    
    private boolean isHitboxHidden;
    
    protected Point position;
    protected Hitbox hitbox;
    // --------------------------------------

    public Essence(int x, int y, int image_width, int image_height) {
        id = GenerateID();
        
        position = new Point(x, y);
        size = new Size(image_width, image_height);
        hitbox = Hitbox.ByEssence(this);
        
        isHitboxHidden = true;// false to show the hitbox
        
        RegisterEntity(this);
    }
    
    // PROTECTED ----------------------------
    // GETTERS ------------------------------
    protected int imageWidth() { return size.spriteWidth(); }
    protected int imageHeight() { return size.spriteHeight(); }
    
    // FORCE --------------------------------
    protected void forceMove(int x, int y) {
        this.position.move(x, y);
        this.hitbox.forceMove(x, y);
    }
    // --------------------------------------
    
    // PUBLIC -------------------------------
    // SETUP --------------------------------
    public void removeHitBox() {
        hitbox.removeFromBoxes();
    }
    // GETTERS ------------------------------
    public int x() { return position.x; }
    public int y() { return position.y; }
    public int width() { return size.renderWidth(); }
    public int height() { return size.renderHeight(); }
    
    // SETTERS ------------------------------
    public void showHitbox() { isHitboxHidden = false; }
    public void hideHitbox() { isHitboxHidden = true; }
    // --------------------------------------
    
    // PSEU EVENTS --------------------------
    public void onCollision(Essence other) {}
    // --------------------------------------
    
    // STATIC METHODS -----------------------
    // PRIVATES -----------------------------
    private static int GenerateID() {
        int id = 1;
        
        Collections.sort(ELEMENTS); 
        if (ELEMENTS.size() > 0) id = ELEMENTS.get(ELEMENTS.size() - 1).id + 1;
        
        return id;
    }
    
    private static void RegisterEntity(Essence _self) { ELEMENTS.add(_self); }
    
    private static void RemoveEntity(Essence _self) { 
        for (int i = 0; i < ELEMENTS.size(); i++) {
            Essence entity = ELEMENTS.get(i);
            if (entity == _self) ELEMENTS.remove(i);
        } 
    }
    // --------------------------------------
    
    // OVERRIDE -----------------------------
    // Comparable<Essence> ------------------
    @Override 
    public int compareTo(Essence other) {
        int otherId = other.id;
        return this.id - otherId;
    }
    
    // Renderable ---------------------------
    public void update() {
        if (hitbox != null) hitbox.update();
    }
    
    public void render(Graphics g) {
        if (!isHitboxHidden) hitbox.show(g); // If we want we can show the hit box
    }
    
    // To String ----------------------------
    @Override
    public String toString() {
        return "Essence: { id: " + id + "}";
    }
    // --------------------------------------
    
    // --------------------------------------
}