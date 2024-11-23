import java.awt.event.KeyEvent;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Point;
import java.util.ArrayList;
import java.awt.Graphics;

public class Enemy extends Entity implements Array2DInformacion, Shooter, Target
{
    // STATIC PROPIETIES --------------------
    public class Kind {
        public static final int LEVEL1 = 0;
        public static final int LEVEL2 = 1;
        public static final int LEVEL3 = 2;
    }
    public static final int SLOW_SPEED = 4;
    public static final int MEDIUM_SPEED = 6;
    public static final int FAST_SPEED = 10;
    public static final int SUPERFAST_SPEED = 14;
    
    private static final int[] SCORES = new int[] { 763, 530, 220 };
    // --------------------------------------
    
    // PROPETIES ----------------------------
    private ArrayList<EssenceDestroyedListener> essenceDestroyedlisteners;
    
    private int columnIndex;
    private int rowIndex;
    
    private boolean isDestroy;
    
    private boolean canFire;
    
    private int kind;
    
    private Bullet bullet;
    // --------------------------------------
    
    public Enemy (int x, int y, int kind) {
        super(x, y, 16, 8);
        
        setSpeed(SLOW_SPEED);
        
        // INIT -----------------------------------------
        essenceDestroyedlisteners = new ArrayList<EssenceDestroyedListener>();
        columnIndex = -1;
        rowIndex = -1;
        isDestroy = false;
        canFire = true;
        this.kind = kind;
        
        // LOAD STRIP SPRITES ---------------------------
        registerNewStrip("enemy", "/enemies.png", kind, 2);
        registerNewStrip("enemy_death", "/enemies.png", 3, 1);
        
        setCurrentStrip("enemy");
        setAnimationspeed(70);
    }
    
    // PUBLIC -------------------------------
    // GETTERS ------------------------------
    public boolean canShoot() { return canFire; }
    
    public int getScore() { return SCORES[kind]; }
    
    // LISTENERS ----------------------------
    public void addEssenceDestroyedlistener(EssenceDestroyedListener listener) { essenceDestroyedlisteners.add(listener); }
    
    public void removeEssenceDestroyedlistener(EssenceDestroyedListener listener) {
        for (int i = 0; i < essenceDestroyedlisteners.size(); i++) {
            if(essenceDestroyedlisteners.get(i) == listener)
                essenceDestroyedlisteners.remove(i);
        }
    }
    
    // BEHIVOR ------------------------------
    public void destroy() {
        isDestroy = true;
        setCurrentStrip("enemy_death");
    }
    
    public void fire() {
        if (canFire) {
            int shoot_x = middleHorizontalBorder();
            int shoot_y = bottomBorder();
            
            bullet = new Bullet(shoot_x, shoot_y, kind, 1, this);
            
            canFire = false;
        }
    }
    
    // --------------------------------------
    
    // OVERRIDES ----------------------------
    // OWN EVENTS ---------------------------
    // RENDERABLE ---------------------------
    @Override
    public void update() {
        super.update();
        
        if (bullet != null) bullet.update();
    }
    
    @Override
    public void render(Graphics g) {
        super.render(g);
        
        if (bullet != null) bullet.render(g);
    }
    
    // TARGET -------------------------------
    @Override
    public void onHit(Shooter shooter) {
        destroy();
    }
    
    // SHOOTER ------------------------------
    @Override
    public void onMissingBullet(Bullet bullet) {
        this.bullet = null;
        canFire = true;
    }
    
    @Override
    public void onHitBullet(Target target, Bullet bullet){
        this.bullet = null;
        canFire = true;
    }
    
    // OWNEVENT -----------------------------
    @Override
    public void onEndStrip() {
        if (isDestroy) {
            for(EssenceDestroyedListener listener : essenceDestroyedlisteners) listener.onEssenceDestroyed(this);
        }
    }
    
    // Array2DInformacion -------------------
    @Override
    public int getRowIndex(){
        return rowIndex;
    }
    
    @Override
    public int getColumnIndex(){
        return columnIndex;
    }
    
    @Override
    public void setRowIndex(int row_index){
        rowIndex = row_index;
    }
    
    @Override
    public void setColumnIndex(int column_index){
        columnIndex = column_index;
    }
    
    // --------------------------------------
    // --------------------------------------
}