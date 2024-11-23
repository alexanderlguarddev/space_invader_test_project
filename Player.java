import java.awt.event.KeyEvent;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Point;
import java.util.ArrayList;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class Player extends Entity implements GamepadListener, Target, Shooter
{
    // STATIC PROPIETIES --------------------
    public static final int IMAGE_WIDTH = 17;
    public static final int IMAGE_HEIGHT = 8;
    
    // PRIVATE STATIC -----------------------
    private static final int FRAMES_WAIT_LIMIT_BULLET = 2;
    // --------------------------------------
    
    // PROPETIES ----------------------------
    private int borderLeft;
    private int borderRight;
    
    private boolean amIDead;
    private boolean blockMovement;
    private int beatCount;
    
    private Point startPosition;
    
    private Bullet[] bullets;
    
    private int bulletsWaitFramesCount;
    
    private int lives;
    
    private ArrayList<EssenceDestroyedListener> essenceDestroyedlisteners;
    private ArrayList<LivesListener> livesListeners;
    // --------------------------------------
    
    public Player(int x, int y, int lives) {
        super(x, y, IMAGE_WIDTH, IMAGE_HEIGHT);
        
        essenceDestroyedlisteners = new ArrayList<EssenceDestroyedListener>();
        livesListeners = new ArrayList<LivesListener>();
        
        startPosition = new Point(x, y);
        
        registerNewStrip("player", "/player.png", 0, 4);
        registerNewStrip("player_death", "/player.png", 1, 4);
        registerNewStrip("player_newlife", "/player.png", 2, 4);
        
        setCurrentStrip("player");
        setAnimationspeed(20);
        
        // CALCULATE BORDER -----------------------
        borderLeft = Game.Setup.HORIXONTAL_PLAYABLE_OFFSET;
        borderRight = Game.Setup.WIDTH - Game.Setup.HORIXONTAL_PLAYABLE_OFFSET;
        
        amIDead = false;
        blockMovement = false;
        beatCount = 0;
        bulletsWaitFramesCount = 0;
        
        startBullet();
        
        this.lives = lives;
    }
    
    // STATIC METHODS -----------------------
    public static BufferedImage LoadLiveImage() {
        return AnimationPackage.LoadImage("/player.png", 3, 0, IMAGE_WIDTH, IMAGE_HEIGHT);
    }
    // --------------------------------------
    
    // PUBLIC -------------------------------
    public void restartPosition() { forceMove(startPosition.x, startPosition.y); }
    
    public void removeLiveListener(LivesListener listener) {
        for (int i = 0; i < livesListeners.size(); i++)
            if(livesListeners.get(i) == listener)
                livesListeners.remove(i); 
    }
    
    public void removeEssenceDestroyedlistener(EssenceDestroyedListener listener) { 
        for (int i = 0; i < essenceDestroyedlisteners.size(); i++)
            if(essenceDestroyedlisteners.get(i) == listener)
                essenceDestroyedlisteners.remove(i);
    }
    // --------------------------------------
    
    // PRIVATE ------------------------------
    // BULLET CONTROL -----------------------
    private void startBullet() {
        bullets = new Bullet[5];
        
        for(int i = 0; i < 5; i++) bullets[i] = null;
    }
    
    private void removeBullet(Bullet bullet) {
        for(int i = 0; i < 5; i++)
            if(bullets[i] == bullet) bullets[i] = null;
    }
    
    private int getAvaibleIndexToShot() {
        if (bulletsWaitFramesCount > 0) return -1;
        
        for(int i = 0; i < 5; i++)
            if (bullets[i] == null) return i;
        
        return -1;
    }
    
    private void shot() {
        int index = -1;
        
        if ((index = getAvaibleIndexToShot()) != -1) {
            int bullet_x = middleHorizontalBorder();
            
            int bullet_y = y() - (Bullet.IMAGE_HEIGHT * Game.Setup.RATE);
            
            bullets[index] = new Bullet(bullet_x, bullet_y, Bullet.Kind.TYPE4, -1, this);
            
            bulletsWaitFramesCount = FRAMES_WAIT_LIMIT_BULLET;
        }
    }
    
    // DEATH CONTROL ------------------------
    private void startDeath() {
        amIDead = true;
        blockMovement = true;
        
        releaseRight();
        releaseLeft();
        
        setCurrentStrip("player_death");
        lives--;
        for (LivesListener listener : livesListeners) listener.onLostLive(this, lives);
    }
    // --------------------------------------
    
    // PUBLIC -------------------------------
    public void addLivesListener(LivesListener listener) { livesListeners.add(listener); }
    public void addEssenceDestroyedlistener(EssenceDestroyedListener listener) { essenceDestroyedlisteners.add(listener); }
    
    // GETTERS ------------------------------
    public boolean hasBeenHit() { return amIDead; }
    // --------------------------------------
    
    // OVERRIDES ----------------------------
    // SHOOTER ------------------------------
    @Override
    public void onMissingBullet(Bullet bullet) {
        removeBullet(bullet);
    }
    
    @Override
    public void onHitBullet(Target target, Bullet bullet){
        removeBullet(bullet);
    }
    
    // CUSTOME EVENTS -----------------------
    @Override
    public void onEndStrip() {
        if (!amIDead) {
            if (bulletsWaitFramesCount > 0) bulletsWaitFramesCount--;
        }
        
        if (amIDead) {
            if (checkCurrentStrip("player_death")) {
                if (lives > 0) {
                    forceMove(startPosition.x, startPosition.y);
                    setCurrentStrip("player_newlife");
                    beatCount = 0;
                } else {
                    for(EssenceDestroyedListener listener : essenceDestroyedlisteners) listener.onEssenceDestroyed(this);
                }
            }
            
            if (checkCurrentStrip("player_newlife")) {
                if (beatCount >= 2) {
                    amIDead = false;
                    blockMovement = false;
                    bulletsWaitFramesCount = 0;
                    setCurrentStrip("player");
                }
                
                beatCount++;
            }
        }
    }
    
    // GamepadListener ----------------------
    @Override
    public void keyMovemenetPressed(int key) {
        if (blockMovement) return;
        
        switch(key) {
            case Game.Setup.Controls.KEY_RIGHT:
                if (nextRightBorder() <=  borderRight) moveRight();
                else releaseRight();
                break;
            case Game.Setup.Controls.KEY_LEFT:
                if (borderLeft <= nextLeftBorder()) moveLeft();
                else releaseLeft();
                break;
        }
    }
    
    @Override
    public void keyMovemenetReleased(int key) {
        if (blockMovement) return;
        
        switch(key) {
            case Game.Setup.Controls.KEY_RIGHT:
                releaseRight();
                break;
            case Game.Setup.Controls.KEY_LEFT:
                releaseLeft();
                break;
        }
    }
    
    @Override
    public void keyActionTyped(int key) {
        switch(key) {
            case Game.Setup.Controls.KEY_ACTION_1:
            case Game.Setup.Controls.KEY_ACTION_1_Alt:
                shot();
                break;
        }
    }
    
    // Target -------------------------------
    @Override
    public void onHit(Shooter shooter) {
        if (!amIDead) startDeath();
    }
    
    // RENDERABLE ---------------------------
    @Override
    public void update() {
        super.update();
        
        for(Bullet bullet : bullets) if(bullet != null) bullet.update();
    }
    
    @Override
    public void render(Graphics g) {
        super.render(g);
        
        for(Bullet bullet : bullets) if(bullet != null) bullet.render(g);
    }
    // --------------------------------------
    
    // --------------------------------------
    
    // --------------------------------------
}