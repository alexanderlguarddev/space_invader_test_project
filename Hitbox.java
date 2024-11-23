import java.util.ArrayList;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Point;
import java.awt.Graphics;
import java.awt.Dimension;

public class Hitbox extends Rectangle implements PositionListener
{
    // TYPES --------------------------------
    public enum BoxType
    {
        SOLID, ENTITY, HIT, BOX
    }
    // --------------------------------------
    
    // STATIC VALUES ------------------------
    private static final ArrayList<Hitbox> BOXES = new ArrayList<Hitbox>();
    // --------------------------------------
    
    // PROPETIES ----------------------------
    private Color wireColor;
    private Essence parent;
    private BoxType type;
    private Point offset;
    
    private boolean isSolid;
    private boolean isAnAttack;
    // --------------------------------------
    
    public Hitbox(int x, int y, int w, int h) {
        super(x, y, w, h);
        
        offset = new Point(0, 0);
        
        isSolid = false;
        isAnAttack = false;
        
        RegisterBox(this);
    }
    
    // PUBLIC -------------------------------
    // BOXES --------------------------------
    public void removeFromBoxes() {
        for(int i = 0; i < BOXES.size(); i++) {
            if(BOXES.get(i) == this)
                BOXES.remove(i);
        }
    }
    
    // UPDATES ------------------------------
    public void update() {
        Essence other = collisionParent();
        
        if (other != null) parent.onCollision(other);
    }
    
    // COLLISION ----------------------------
    public Hitbox collisionHitbox() {
        if (parent == null) return null;
        
        for (Hitbox box : BOXES)
            if (box != this)
                if (box.parent == null || box.parent != parent)
                    if (within(box)) return box;
        
        return null;
    }
    
    public ArrayList<Hitbox> collisionHitboxes() {
        if (parent == null) return null;
        
        ArrayList<Hitbox> colliders = new ArrayList<Hitbox>();
        
        for (Hitbox box : BOXES)
            if (box != this)
                if (box.parent == null || box.parent != parent)
                    if (within(box)) colliders.add(box);
        
        return colliders;
    }
    
    public Essence collisionParent() {
        if (parent == null) return null;
        
        for (Hitbox box : BOXES)
            if (box != this) 
                if (box.parent == null || box.parent != parent)
                    if (within(box) && box.parent != null) return box.parent;
        
        return null;
    }
    
    public boolean isThereASolidCollition() {
        if (parent == null) return false;
        
        for (Hitbox box : BOXES)
            if (box != this) 
                if (box.parent == null || box.parent != parent)
                    if (within(box) && box.isSolid) return true;
        
        return false;
    }
    
    public boolean isThereASolidCollitionInThePosition(int pos_x, int pos_y) {
        if (parent == null) return false;
        
        for (Hitbox box : BOXES) 
            if (box != this) 
                if (box.parent == null || box.parent != parent) 
                    if (within(box, pos_x, pos_y) && box.isSolid) 
                        return true;
        
        return false;
    }
    // SETTERS ------------------------------
    public void setWireColor(Color color) { wireColor = color; }
    public void defineAsSolid() {  isSolid = true; }
    public void defineAsPassable() {  isSolid = false; }
    public void defineAsAnAttack() { isAnAttack = true; }
    public void defineAsANonAttack() { isAnAttack = false; }
    
    // Render -------------------------------
    public void show(Graphics g) { // Graph the element
        g.setColor(wireColor);
        g.drawRect(x,y,width,height);
        
        //System.out.println("rect: [" + x + "," + y + "," + width + "," + height + "]");
    }
    
    // GETTERS ------------------------------
    public Essence getParent() { return parent; }
    
    // SETUP --------------------------------
    public void removeFromHistory() { RemoveBox(this); }
    
    // FORCE --------------------------------
    public void forceMove(int x, int y) { this.move(x, y); }
    // --------------------------------------
    
    // PRIVATES -----------------------------
    // CALCULATE COLLISION ------------------
    private boolean within(Hitbox other) {
        return within(other, x, y);
    }
    
    public boolean within(Hitbox other, int x_start, int y_start) {
        int xStart = x_start;
        int yStart = y_start;
        int xEnd = xStart + width;
        int yEnd = yStart + height;
        
        int other_xStart = other.x;
        int other_yStart = other.y;
        int other_xEnd = other_xStart + other.width;
        int other_yEnd = other_yStart + other.height;
        
        // Check if overslap
        boolean check_xStart = xStart >= other_xStart && xStart <= other_xEnd;
        boolean check_xEnd = xEnd >= other_xStart && xEnd <= other_xEnd;
        
        boolean check_yStart = yStart >= other_yStart && yStart <= other_yEnd;
        boolean check_yEnd = yEnd >= other_yStart && yEnd <= other_yEnd;
        
        boolean case1 = check_xStart && check_yStart;
        boolean case2 = check_xStart && check_yEnd;
        boolean case3 = check_xEnd && check_yStart;
        boolean case4 = check_xEnd && check_yEnd;
        
        return case1 || case2 || case3 || case4;
    }
    // --------------------------------------
    
    // STATIC METHODS -----------------------
    // CONSTRUCTORS -------------------------
    public static Hitbox ByEssence(Essence parent) {
        Hitbox hitbox = new Hitbox(parent.x(), parent.y(), parent.width(), parent.height());
        
        hitbox.parent = parent;
        hitbox.type = BoxType.ENTITY;
        hitbox.wireColor = Color.GREEN;
        
        return hitbox;
    }
    
    // PRIVATES -----------------------------
    private static void RegisterBox(Hitbox _self) { BOXES.add(_self); }
    
    private static void RemoveBox(Hitbox _self) { 
        for (int i = 0; i < BOXES.size(); i++) {
            Hitbox _box = BOXES.get(i);
            if (_box == _self) BOXES.remove(i);
        } 
    }
    // --------------------------------------
    
    // OVERRIDE -----------------------------
    // PositionListener ---------------------
    @Override 
    public void onMove(int dx, int dy) {
        super.translate(dx, dy);
    }
    // --------------------------------------
    
    // --------------------------------------
}