import java.awt.Point;
import java.awt.Rectangle;

public class Movement
{
    // DIRECTIONS ---------------------------
    public enum Axis {
        IDLE, UP, DOWN, RIGHT, LEFT
    }
    // --------------------------------------
    
    // PROPETIES ----------------------------
    private int speed;
    
    private boolean up, down, right, left;
    private boolean blockUp, blockDown, blockRight, blockLeft;
    
    private Axis lastDirection;
    private Point lastPosition;
    
    private MovementClient client;
    // --------------------------------------
    
    public Movement(MovementClient client, int speed) {
        this.client = client;
        this.speed = speed;
        
        up = false;
        blockUp = false;
        down = false;
        blockDown = false;
        left = false;
        blockLeft = false;
        right = false;
        blockRight = false;
        
        lastDirection = Axis.IDLE;
    }
    
    // PUBLIC -------------------------------
    // GETTERS ------------------------------
    public int getSpeed() { return this.speed; }
    // SETTERS ------------------------------
    public void setSpeed(int speed) { this.speed = speed; }
    // AXIS CONTROL -------------------------
    public void moveUp() { up = true; }
    public void moveRight() { right = true; }
    public void moveDown() { down = true; }
    public void moveLeft() { left = true; }
    
    public void releaseUp() { up = false; }
    public void releaseRight() { right = false; }
    public void releaseDown() { down = false; }
    public void releaseLeft() { left = false; }
    
    // AXIS Check Movement ------------------
    public boolean isTryingToMoveUp() { return up; }
    public boolean isTryingToMoveRight() { return right; }
    public boolean isTryingToMoveDown() { return down; }
    public boolean isTryingToMoveLeft() { return left; }
    
    public boolean isTryingToMoveHorizontal() { return right || left; }
    public boolean isTryingToMoveVertical() { return up || down; }
    public boolean isTryingToMove() { return right || left || up || down; }
    
    // AXIS Check Blocked -------------------
    public boolean isUpMovementBlocked() { return blockUp; }
    public boolean isRightMovementBlocked() { return blockRight; }
    public boolean isDownMovementBlocked() { return blockDown; }
    public boolean isLeftMovementBlocked() { return blockLeft; }
    
    public boolean isHorizontalMovementBlocked() { return blockRight || blockLeft; }
    public boolean isVerticalMovementBlocked() { return blockUp || blockDown; }
    public boolean isMovementBlocked() { return blockRight || blockLeft || blockUp || blockDown; }
    
    // UPDATES ------------------------------
    public void update() {
        int xAxis = 0, yAxis = 0;
        resetBlockMovement();
        
        if (up) yAxis--;
        if (down) yAxis++;
        if (left) xAxis--;
        if (right) xAxis++;
        
        Point axis = checkMovement(xAxis, yAxis);
        
        client.move(axis.x, axis.y);
    }
    // --------------------------------------
    
    // PRIVATE ------------------------------
    // CONTROL PROPETIES --------------------
    private void resetBlockMovement() {
        blockUp = false;
        blockDown = false;
        blockRight = false;
        blockLeft =  false;
    }
    
    // MOVEMENT AND HITBOX CONTROL ----------
    private Point checkMovement(int xAxis, int yAxis) {
        Point position = client.getCurrentPosition();
        Hitbox hitbox = client.getHitbox();
        
        int _x = xAxis * speed;
        int _y = yAxis * speed;
        
        if (hitbox.isThereASolidCollitionInThePosition(position.x + _x, position.y)) {
            _x = 0; // RESTRICT THE MOVEMENTS
            if (xAxis > 0) blockRight = true;
            else if (xAxis < 0) blockLeft = true;
        }
        
        if (hitbox.isThereASolidCollitionInThePosition(position.x, position.y + _y)) {
            _y = 0; // RESTRICT THE MOVEMENTS
            if (yAxis > 0) blockDown = true;
            else if (yAxis < 0) blockUp = true;
        }
        
        return new Point(_x, _y);
    }
    // --------------------------------------
    
    // --------------------------------------
}