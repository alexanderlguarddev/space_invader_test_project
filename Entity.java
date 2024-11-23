import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.awt.Color;
import java.awt.event.KeyEvent;

public class Entity extends Sprite implements MovementClient
{
    // STATIC CONSTANTS ---------------------
    private static final int MOVEMENT_SPEED = 1;
    // --------------------------------------
    
    // PROPETIES ----------------------------
    private ArrayList<PositionListener> positionListeners;
    
    private Movement movement;
    
    private boolean isMovementDenied;
    // --------------------------------------
    
    public Entity(int x, int y, int w, int h) {
        super(x, y, w, h);
        
        movement = new Movement(this, MOVEMENT_SPEED);
        
        positionListeners = new ArrayList<PositionListener>(); 
        addPositionListener(hitbox);
        
        isMovementDenied = false;
    }
    
    // PUBLIC -------------------------------
    // MOVEMENT -----------------------------
    public void releaseAll() {
        movement.releaseUp();
        movement.releaseDown();
        movement.releaseRight();
        movement.releaseLeft();
    }
    
    // GETTERS ------------------------------
    // NEXT LIMITS --------------------------
    public int nextTopBorder() { return y() + movement.getSpeed(); }
    public int nextBottomBorder() { return y() + height() + movement.getSpeed(); }
    public int nextRightBorder() { return x() + width() + movement.getSpeed(); }
    public int nextLeftBorder() { return x() + movement.getSpeed(); }
    
    // CURRENT LIMITS -----------------------
    public int rightBorder() { return x() + width(); }
    public int leftBorder() { return x(); }
    public int bottomBorder() { return y() + height(); }
    public int topBorder() { return y(); }
    
    // CURRENT SPECIFIC POSITION ------------
    public int middleHorizontalBorder() { return x() + (width() / 2); }
    
    // LISTENERS ----------------------------
    public void addPositionListener(PositionListener listener) { positionListeners.add(listener); }
    // --------------------------------------
    
    // PROTECTED ----------------------------
    // SETTERS ------------------------------
    protected void denieMovement() { isMovementDenied = true; }
    protected void allowMovement() { isMovementDenied = false; }
    
    protected void setSpeed(int speed) { movement.setSpeed(speed); }
    
    // GETTERS ------------------------------
    protected boolean getIfMovementDenied() { return isMovementDenied; }
    
    // MOVE CONTROL -------------------------
    protected void moveUp() { movement.moveUp(); }
    protected void moveDown() { movement.moveDown(); }
    protected void moveRight() { movement.moveRight(); }
    protected void moveLeft() { movement.moveLeft(); }
    
    protected void releaseUp() { movement.releaseUp(); }
    protected void releaseDown() { movement.releaseDown(); }
    protected void releaseRight() { movement.releaseRight(); }
    protected void releaseLeft() { movement.releaseLeft(); }
    // --------------------------------------
    
    // OVERRIDE -----------------------------
    // RENDERABLE ---------------------------
    @Override
    public void update() {
        super.update();
        
        if (!isMovementDenied) {
            movement.update();
        }
    }
    
    // MovementClient -----------------------
    @Override 
    public Point getCurrentPosition() { return new Point(x(), y()); }
    
    @Override
    public Hitbox getHitbox() { return hitbox; }
    
    @Override 
    public void move(int dx, int dy) {
        position.translate(dx, dy);
        
        for(PositionListener listener : positionListeners) listener.onMove(dx, dy);
    }
    // --------------------------------------
    
    // --------------------------------------
}