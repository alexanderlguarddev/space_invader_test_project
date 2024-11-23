public class Bullet extends Entity
{
    // STATIC PROPIETIES --------------------
    public static final int IMAGE_WIDTH = 3;
    public static final int IMAGE_HEIGHT = 8;
    
    public class Kind {
        public static final int TYPE1 = 0;
        public static final int TYPE2 = 1;
        public static final int TYPE3 = 2;
        public static final int TYPE4 = 3;
    }
    // --------------------------------------
    
    // PROPETIES ----------------------------
    private int dir;
    private Shooter shooter;
    // --------------------------------------
    
    public Bullet(int x, int y, int kind, int dir, Shooter shooter) {
        super(x, y, IMAGE_WIDTH, IMAGE_HEIGHT);
        
        this.dir = dir;
        this.shooter = shooter;
        
        registerNewStrip("shoot", "/shoots.png", kind, 4);
        setCurrentStrip("shoot");
        setAnimationspeed(50);
        
        if (dir >= 0) moveDown();
        else moveUp();
    }
    
    // PRIVATE ------------------------------
    private void controBulletMovement() {
        if (dir >= 0 && nextBottomBorder() > Game.Setup.HEIGHT) {
            releaseDown();
            shooter.onMissingBullet(this);
        }
        
        if (dir < 0 && nextTopBorder() < 0) {
            releaseUp();
            shooter.onMissingBullet(this);
        }
    }
    // --------------------------------------
    
    // OVERRIDES ----------------------------
    // OWN EVENT ----------------------------
    @Override
    public void onCollision(Essence other) {
        if (shooter instanceof Essence) {
            Essence _parent = (Essence) shooter;
            
            if (shooter != other) { // CHECK OF THIS IS NOT THE SHOOTER
                if (other instanceof Target) {
                    Target target = (Target) other;
                    
                    target.onHit(shooter);
                    shooter.onHitBullet(target, this);
                }
            }
        }
    }
    
    // UPDATE -------------------------------
    @Override
    public void update() {
        super.update();
        
        controBulletMovement();
        
        
    }
    
    // --------------------------------------
    
    // --------------------------------------
}