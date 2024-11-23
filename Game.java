import java.awt.event.WindowFocusListener;
import java.awt.event.WindowEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.Graphics;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.awt.image.BufferedImage;

public class Game implements Runnable, Renderable, WindowFocusListener, KeyListener, EssenceDestroyedListener, LivesListener
{
    // SETUP --------------------------------
    public static class Setup {
        public static final int RATE = 3;
        public static final int FPS_SET = 120;
        public static final int UPS_SET = 200;
        public static final int WIDTH = 1280;
        public static final int HEIGHT = 720;
        public static final String GAME_TITLE = "Space Invaders";
        public static final int HORIZONTAL_DEATH_ZONE = 240;
        public static final int HORIXONTAL_PLAYABLE_OFFSET = 140;
        public static final int TOP_OFFSET = 100;
        public static final int PROBABILITY_BULLET_LVL1 = 2000;
        public static final int PROBABILITY_BULLET_LVL2 = 1000;
        public static final int PROBABILITY_BULLET_LVL3 = 500;
        public static final long SEED = 18;
        // ASPECTS ----------------------------
        public static class Aspects {
            public static final Color BACKGROUND = Color.BLACK;
        }
        // CONTROLS ---------------------------
        public static class Controls {
            public static final int KEY_UP = KeyEvent.VK_UP;
            public static final int KEY_DOWN = KeyEvent.VK_DOWN;
            public static final int KEY_RIGHT = KeyEvent.VK_RIGHT;
            public static final int KEY_LEFT = KeyEvent.VK_LEFT;
            public static final int KEY_ACTION_1 = KeyEvent.VK_A;
            public static final int KEY_ACTION_1_Alt = KeyEvent.VK_SPACE; // #FIX SPACE BAR :(
            // STATES KEY
            public static class KeyStates {
                private static final int KEY_RELEASED = -1;
                private static final int KEY_PRESSED = 0;
                private static final int KEY_BLOCKED = 1;
            }
        }
    }
    // --------------------------------------
    
    // STATIC PROPIETIES --------------------
    private static final int HORIZONTALMOVEMENT_LEFT = -1;
    private static final int HORIZONTALMOVEMENT_STOP = 0;
    private static final int HORIZONTALMOVEMENT_RIGHT = 1;
    
    private static final int ENEMYMOVEMENT_FRAMESLIMIT_SLOW = 8;
    private static final int ENEMYMOVEMENT_FRAMESLIMIT_MEDIUM = 4;
    private static final int ENEMYMOVEMENT_FRAMESLIMIT_FAST = 2;
    
    private static final int UPDATEENEMYMOVEMENT_FRAMESLIMIT_SLOW = 30;
    private static final int UPDATEENEMYMOVEMENT_FRAMESLIMIT_MEDIUM = 22;
    private static final int UPDATEENEMYMOVEMENT_FRAMESLIMIT_FAST = 15;
    private static final int UPDATEENEMYMOVEMENT_FRAMESLIMIT_SUPERFAST = 10;
    
    private static final int ENEMYMOVEMENT_DOWN_FRAMESLIMIT_SLOW = 3;
    private static final int ENEMYMOVEMENT_DOWN_FRAMESLIMIT_MEDIUM = 4;
    private static final int ENEMYMOVEMENT_DOWN_FRAMESLIMIT_FAST = 6;
    
    // RANDOM -------------------------------
    private static Random RAND;
    // --------------------------------------
    
    // PROPETIES ----------------------------
    private Thread loop; // Bucle
    // VIEW ---------------------------------
    private Window window;
    private Panel panel;
    // Keyboard Control ---------------------
    private HashMap<Integer, Integer> mapKeyOnePress;
    // ENEMIES ------------------------------
    private Enemy[][] enemies;
    private boolean[][] enemies_states;
    // ENEMIES BULLET -----------------------
    private int enemyBulletProbability;
    // ENEMIES MOVEMENT ---------------------
    private int enemyMovementFramesCount;
    private boolean updatingEnemiesPosition;
    private int enemyRowBottomIndexToUpdatePosition;
    private int enemyMovementFramesLimit;
    
    // ENEMIES UPDATE MOVEMENT --------------
    private int enemyUpdateMovementFramesCount;
    private int enemyUpdateMovementFramesLimit;
    
    // DOWN MOVEMENT ENEMY ------------------
    private int enemyDownMovementFramesCount;
    private int enemyDownMovementFramesLimit;
    
    // BORDER SCREEN ------------------------
    private int rigthBorderScreen;
    private int leftBorderScreen;
    
    private int enemiesHorizontalMovementAxis;
    private int enemiesFormerHorizontalMovementAxis;
    
    // PLAYER -------------------------------
    private Player player;
    private int player_lives;
    
    // GAMEPAD LISTENER ---------------------
    private ArrayList<GamepadListener> gamepadListeners;
    
    // ESSENCES TO REMOVE -------------------
    private ArrayList<Essence> essenceToRemove;
    
    // SCORE --------------------------------
    private int score;
    private int attempts;
    // --------------------------------------
    
    // Entity Test --------------------------
    
    // --------------------------------------
    
    public Game() {
        init();
        initEntities();
        
        window = new Window(Setup.GAME_TITLE);
        panel = new Panel(Setup.WIDTH, Setup.HEIGHT);
        
        panel.addRenderListener(this);
        panel.addKeyListener(this);
        
        window.link(panel);
        
        window.show();
        panel.requestFocus();
        
        runLoop();
    }
    
    // REMOVE FROM LISTS --------------------
    public void removeGamepadListener(GamepadListener listener) { 
        for(int i = 0; i < gamepadListeners.size(); i++)
            if (gamepadListeners.get(i) == listener)
                gamepadListeners.remove(i); 
    }
    // --------------------------------------
    
    // PUBLIC -------------------------------
    public void addGamepadListener(GamepadListener listener) { gamepadListeners.add(listener); }
    // --------------------------------------
    
    // PRIVATES -----------------------------
    // TEST METHODS -------------------------
    private void randomDestroy() {
        Enemy enemyToDestroy = null;
        
        while (enemyToDestroy == null) {
            int rowIndex = randInt(0, enemies.length - 1);
            int columnIndex = randInt(0, enemies[rowIndex].length - 1);
            
            enemyToDestroy = enemies[rowIndex][columnIndex];
        }
        
        enemyToDestroy.destroy();
    }
    
    // INITS METHODS ------------------------
    private void init() {
        mapKeyOnePress = new HashMap<Integer, Integer>();
        gamepadListeners = new ArrayList<GamepadListener>();
        essenceToRemove = new ArrayList<Essence>();
        
        enemies = new Enemy[5][10];
        enemies_states = new boolean[5][10];
        
        enemiesHorizontalMovementAxis = HORIZONTALMOVEMENT_RIGHT;
        enemiesFormerHorizontalMovementAxis = HORIZONTALMOVEMENT_STOP;

        enemyMovementFramesLimit = ENEMYMOVEMENT_FRAMESLIMIT_SLOW;
        enemyUpdateMovementFramesLimit = UPDATEENEMYMOVEMENT_FRAMESLIMIT_SLOW;
        enemyDownMovementFramesLimit = ENEMYMOVEMENT_DOWN_FRAMESLIMIT_SLOW;
        enemyBulletProbability = Setup.PROBABILITY_BULLET_LVL1;
        
        enemyMovementFramesCount = 0;
        enemyUpdateMovementFramesCount = 0;
        enemyDownMovementFramesCount = 0;
        
        updatingEnemiesPosition = false;
        enemyRowBottomIndexToUpdatePosition = -1;
        
        // CALCULATE SCREEN ---------------------
        rigthBorderScreen = Setup.WIDTH - Setup.HORIXONTAL_PLAYABLE_OFFSET;
        leftBorderScreen = Setup.HORIXONTAL_PLAYABLE_OFFSET;
        // --------------------------------------
        
        score = 0;
        attempts = 0;
        
        RAND = new Random(Setup.SEED);
    }
    
    private void initEntities() {
        setupEnemies();
        setupPlayer();
    }
    
    private void runLoop() {
        loop = new Thread(this);
        loop.start();
    }
    
    // PLAYER CONTROL -----------------------
    private void setupPlayer() {
        player_lives = 3;
        
        int player_x = (Setup.WIDTH / 2) - ((Player.IMAGE_WIDTH * Setup.RATE) / 2);
        int player_y = Setup.HEIGHT - (Player.IMAGE_HEIGHT * Setup.RATE);
        
        player = new Player(player_x, player_y, player_lives);
        
        addGamepadListener(player);
        
        player.addLivesListener(this);
        player.addEssenceDestroyedlistener(this);
    }
    
    // Enemy Control ------------------------
    // CHECK ENEYMY BOTTOM DISTANCE ---------
    private void checkGameOverByDistance() {
        int row_index = getBottomRowIndexWithAtLeastAnEnemy();
        Enemy enemy = getFirstEnemyBottomByRowIndex(row_index);
        
        if (enemy != null) { // IF THIS IS NOT NULL
            int y_position = enemy.bottomBorder();
            
            if (y_position > (Setup.HEIGHT - 100)) {
                restartGame(true);
            }
        }
    }
    
    // CHECK ENEMY STATES -------------------
    private boolean isThereAtLeastAnEnemy() {
        for(int i = 0; i < enemies.length; i++)
            for (int j = 0; j < enemies[i].length; j++)
                if(enemies_states[i][j]) return true;
        
        return false;
    }
    
    // SET SPEED ----------------------------
    private void setEnemiesSpeed(int speed) {
        for(int i = 0; i < enemies.length; i++)
            for (int j = 0; j < enemies[i].length; j++)
                if(enemies_states[i][j]) enemies[i][j].setSpeed(speed);
    }
    // UPDATES LIMITS -----------------------
    private void updateLimits() {
        int totalEnemies = enemies.length * enemies[0].length;
        int enemyCounts = 0;
        
        for(int i = 0; i < enemies.length; i++)
            for (int j = 0; j < enemies[i].length; j++)
                if(enemies_states[i][j]) enemyCounts++;
                
        // CHANGE VALUES ACORDING ENEMYS SIZE
        if (enemyCounts == 1) {
            setEnemiesSpeed(Enemy.SUPERFAST_SPEED);
        } else if (enemyCounts < (totalEnemies / 4)) {
            enemyUpdateMovementFramesLimit = UPDATEENEMYMOVEMENT_FRAMESLIMIT_SUPERFAST;
        } else if (enemyCounts < (totalEnemies / 2)) {
            enemyMovementFramesLimit = ENEMYMOVEMENT_FRAMESLIMIT_FAST;
            enemyDownMovementFramesLimit = ENEMYMOVEMENT_DOWN_FRAMESLIMIT_FAST;
            enemyBulletProbability = Setup.PROBABILITY_BULLET_LVL3;
            setEnemiesSpeed(Enemy.FAST_SPEED);
            enemyUpdateMovementFramesLimit = UPDATEENEMYMOVEMENT_FRAMESLIMIT_FAST;
        } else if (enemyCounts < ((totalEnemies * 3) / 4)) {
            enemyMovementFramesLimit = ENEMYMOVEMENT_FRAMESLIMIT_MEDIUM;
            enemyDownMovementFramesLimit = ENEMYMOVEMENT_DOWN_FRAMESLIMIT_MEDIUM;
            enemyBulletProbability = Setup.PROBABILITY_BULLET_LVL2;
            setEnemiesSpeed(Enemy.MEDIUM_SPEED);
            enemyUpdateMovementFramesLimit = UPDATEENEMYMOVEMENT_FRAMESLIMIT_MEDIUM;
        }
    }
    // FIRE A BULLET ------------------------
    private void couldEnemyShoot(int column_index) {
        if (column_index >= enemies[0].length) return;
        
        int row_index = -1;
        if ((row_index = getEnemyBottomRowByColumnIndex(column_index)) == -1) {
            couldEnemyShoot(column_index + 1);
            return;
        }
        
        Enemy enemy = enemies[row_index][column_index];
        int rand_number = randInt(0, enemyBulletProbability);
        
        if (rand_number != 4) {
            couldEnemyShoot(column_index + 1);
            return;
        }
        
        if (!enemy.canShoot()) return;
        
        enemy.fire();
    }
    
    // MOVEMENT -----------------------------
    private void moveRightAllEnemiesByRowIndex(int row_index) {
        for (int j = 0; j < enemies[row_index].length; j++)
            if(enemies_states[row_index][j]) enemies[row_index][j].moveRight();
    }
    
    private void moveLeftAllEnemiesByRowIndex(int row_index) {
        for (int j = 0; j < enemies[row_index].length; j++)
            if(enemies_states[row_index][j]) enemies[row_index][j].moveLeft();
    }
    
    private void moveDownAllEnemies() {
        for(int i = 0; i < enemies.length; i++)
            for (int j = 0; j < enemies[i].length; j++)
                if(enemies_states[i][j]) enemies[i][j].moveDown();
    }
    
    private void stopMovementForAllEnemies() {
        for(int i = 0; i < enemies.length; i++)
            for (int j = 0; j < enemies[i].length; j++)
                if(enemies_states[i][j]) enemies[i][j].releaseAll();
    }
    
    // ALL ENEMIES --------------------------
    // GET INDEX INFORMATION ----------------
    private int getEnemyColumnIndexByHorizontalBorder(int horizontalAxis) {
        int index = (horizontalAxis >= 0)? 0 : enemies_states[0].length - 1;
        int sign = (horizontalAxis >= 0)? 1 : -1;
        
        return lookEnemiesBorderByColumns(index, sign);
    }
    
    private int getEnemyBottomRowByColumnIndex(int column_index) {
        for (int i = (enemies_states.length - 1); i >= 0; i--) 
            if (enemies_states[i][column_index]) return i;    
        
        return -1;
    }
    
    private Enemy getFirstEnemyBottomByRowIndex(int row_index) {
        for (int i = 0; i < enemies[row_index].length; i++) 
            if (enemies_states[row_index][i]) return enemies[row_index][i];    
        
        return null;
    }
    
    private int lookEnemiesBorderByColumns(int index, int sign) {
        if (index >= enemies_states[0].length) return -1; // IN CASE WE PASS THE LIMIT
        
        for (int i = 0; i < enemies_states.length; i++)
            if (enemies_states[i][index]) return index;
            
        return lookEnemiesBorderByColumns(index + sign, sign);
    }
    
    private int getBottomRowIndexWithAtLeastAnEnemy() {
        for(int i = (enemies.length - 1); i >= 0 ; i--)
            for (int j = 0; j < enemies[i].length; j++)
                if (enemies_states[i][j]) return i;
        
        return -1;
    }
    
    // GET SPECIFIC ENEMY -------------------
    private Enemy getFirstEnemyByBoder(int horizontalAxis) {
        int columnIndex, rowIndex;
        if ((columnIndex = getEnemyColumnIndexByHorizontalBorder(horizontalAxis)) < 0) return null;
        if ((rowIndex = getEnemyBottomRowByColumnIndex(columnIndex)) < 0) return null;
        
        return enemies[rowIndex][columnIndex];
    }
    
    // CONTOL AND SETUP ENEMIES -------------
    private void enemyMovement() {
        // Get Border Index with at least one enemy
        Enemy enemyBottomLeft = getFirstEnemyByBoder(1);
        Enemy enemyBottomRight = getFirstEnemyByBoder(-1);
        
        if (enemyBottomLeft == null && enemyBottomRight == null) { /* END GAME */ }
        
        if (!updatingEnemiesPosition) { // IF we are not updating the movement
            enemyMovementFramesCount++;
            
            if (enemyMovementFramesCount >= enemyMovementFramesLimit) { // IF WE CAN MOVE THE ENEMIES
                if (enemiesHorizontalMovementAxis == HORIZONTALMOVEMENT_RIGHT && enemyBottomRight.rightBorder() >= rigthBorderScreen) {
                    enemiesHorizontalMovementAxis = HORIZONTALMOVEMENT_STOP;
                    enemiesFormerHorizontalMovementAxis = HORIZONTALMOVEMENT_RIGHT;
                }
                
                if (enemiesHorizontalMovementAxis == HORIZONTALMOVEMENT_LEFT && enemyBottomLeft.leftBorder() <= leftBorderScreen) {
                    enemiesHorizontalMovementAxis = HORIZONTALMOVEMENT_STOP;
                    enemiesFormerHorizontalMovementAxis = HORIZONTALMOVEMENT_LEFT;
                }
                
                updatingEnemiesPosition = true;
                enemyRowBottomIndexToUpdatePosition = getBottomRowIndexWithAtLeastAnEnemy();
                enemyMovementFramesCount = 0;
                enemyUpdateMovementFramesCount = 0;
                enemyDownMovementFramesCount = 0;
            }
        }
        
        if (updatingEnemiesPosition) { // When we need to control every row movement
            if (enemiesHorizontalMovementAxis == HORIZONTALMOVEMENT_STOP) {
                if (enemyDownMovementFramesCount <= enemyDownMovementFramesLimit) {
                    moveDownAllEnemies();
                    enemyDownMovementFramesCount++;
                } else {
                    if (enemiesFormerHorizontalMovementAxis == HORIZONTALMOVEMENT_LEFT) 
                        enemiesHorizontalMovementAxis = HORIZONTALMOVEMENT_RIGHT;
                    else if (enemiesFormerHorizontalMovementAxis == HORIZONTALMOVEMENT_RIGHT) 
                        enemiesHorizontalMovementAxis = HORIZONTALMOVEMENT_LEFT;
                }
            } else {
                enemyUpdateMovementFramesCount++;
                
                if (enemyUpdateMovementFramesCount >= enemyUpdateMovementFramesLimit) {
                    if (enemiesHorizontalMovementAxis == HORIZONTALMOVEMENT_RIGHT) moveRightAllEnemiesByRowIndex(enemyRowBottomIndexToUpdatePosition);
                    if (enemiesHorizontalMovementAxis == HORIZONTALMOVEMENT_LEFT) moveLeftAllEnemiesByRowIndex(enemyRowBottomIndexToUpdatePosition);
                    enemyRowBottomIndexToUpdatePosition--;
                    enemyUpdateMovementFramesCount = 0;
                }
                
                if (enemyRowBottomIndexToUpdatePosition < 0) { 
                    updatingEnemiesPosition = false;
                }
            }
        }    
        
        //System.out.println("[Left: " + enemyBottomLeft + ", right: " + enemyBottomRight + "]");
    }
    
    private void restartGame(boolean gameover) {
        // REMOVE EVERY ENEMY
        for (int i = 0; i < enemies.length; i++) 
            for (int j = 0; j < enemies[i].length; j++)
                if (enemies_states[i][j]) {
                    enemies[i][j].removeHitBox();
                    enemies[i][j].removeEssenceDestroyedlistener(this);
                    enemies[i][j] = null;
                    enemies_states[i][j] = false;
                }
        
        setupEnemies();
        if (gameover) {
            if (player != null) {
                player.removeLiveListener(this);
                player.removeEssenceDestroyedlistener(this);
                player.removeHitBox();
                removeGamepadListener(player);
                player = null;
            }
            setupPlayer();
        }
        else player.restartPosition();
        
        enemyMovementFramesLimit = ENEMYMOVEMENT_FRAMESLIMIT_SLOW;
        enemyUpdateMovementFramesLimit = UPDATEENEMYMOVEMENT_FRAMESLIMIT_SLOW;
        enemyDownMovementFramesLimit = ENEMYMOVEMENT_DOWN_FRAMESLIMIT_SLOW;
        enemyBulletProbability = Setup.PROBABILITY_BULLET_LVL1;
        if (gameover) score = 0;
        
        enemyMovementFramesCount = 0;
        enemyUpdateMovementFramesCount = 0;
        enemyDownMovementFramesCount = 0;
        
        updatingEnemiesPosition = false;
        enemyRowBottomIndexToUpdatePosition = -1;
    }
    
    private void setupEnemies() {
        int realWidth = 16 * Setup.RATE;
        int realHeight = 8 * Setup.RATE;
        
        int screen_width = Setup.WIDTH - (Setup.HORIZONTAL_DEATH_ZONE * 2);
        int vertical_gap = 10;
        
        for (int i = 0; i < enemies.length; i++) {
            // GET THE SPACE BETWEEN EACH ALIEN
            int enemy_amount = enemies[i].length;
            int enemy_total_width = enemy_amount * realWidth;
            int avalible_space = screen_width - enemy_total_width;
            int offset_width = avalible_space / (enemy_amount - 1);
            // -------------------------------------------------------
            int level = Enemy.Kind.LEVEL1;
            
            if (i == 1 || i == 2) level = Enemy.Kind.LEVEL2;
            else if (i == 3 || i == 4) level = Enemy.Kind.LEVEL3;
            
            for (int j = 0; j < enemies[i].length; j++) {
                enemies[i][j] = new Enemy(Setup.HORIZONTAL_DEATH_ZONE + (j * (realWidth + offset_width)), Setup.TOP_OFFSET + (i * (vertical_gap  + realHeight)), level);
                
                enemies[i][j].setRowIndex(i);
                enemies[i][j].setColumnIndex(j);
                
                enemies[i][j].addEssenceDestroyedlistener(this);
            }
        }
        
        for (int i = 0; i < enemies_states.length; i++)
            for (int j = 0; j < enemies_states[i].length; j++)
                enemies_states[i][j] = true;
    }
    
    // mapKeyOnePress Control ---------------
    private void oneKeyPressed(int key) {
        if (!mapKeyOnePress.containsKey(key))
            mapKeyOnePress.put(key, Setup.Controls.KeyStates.KEY_RELEASED);
        if (mapKeyOnePress.get(key) == Setup.Controls.KeyStates.KEY_RELEASED) 
            mapKeyOnePress.replace(key, Setup.Controls.KeyStates.KEY_PRESSED);
    }
    
    private void oneKeyReleased(int key) {
        if (!mapKeyOnePress.containsKey(key)) 
            mapKeyOnePress.put(key, Setup.Controls.KeyStates.KEY_RELEASED);
        else 
            mapKeyOnePress.replace(key, Setup.Controls.KeyStates.KEY_RELEASED);
    }
    // --------------------------------------
    
    // STATIC METHODS -----------------------
    public static int randInt(int min, int max) {
        return Game.RAND.nextInt((max - min) + 1) + min;
    }
    
    // --------------------------------------
    
    // GRAPHICS TEXT INFO -------------------
    public void drawInfo(Graphics g) {
        int top = 50;
        int left = 50;
        
        g.setColor(Color.WHITE);
        g.drawString("PLAYER LIVES", left, top);
        
        // DRAW ICONS
        BufferedImage liveIcons = Player.LoadLiveImage();
        
        if (liveIcons != null && player_lives > 0) {
            top += 12;
            
            int liveIcon_width = (Player.IMAGE_WIDTH * (Setup.RATE - 1));
            int liveIcon_height = (Player.IMAGE_HEIGHT * (Setup.RATE - 1));
            
            for (int i = 0; i < player_lives; i++)
                g.drawImage(liveIcons, (left - 7) + (i * liveIcon_width), top, liveIcon_width, liveIcon_height, null);
                
            top += liveIcon_height + 30;
        }
        
        g.drawString("SCORE", left, top);
        top += 22;
        g.drawString(""+score, left, top);
        top += 30;
        g.drawString("Attempts", left, top);
        top += 22;
        g.drawString(""+attempts, left, top);
    }
    // --------------------------------------
    
    // OVERRIDES ----------------------------
    // LivesListener ------------------------
    @Override
    public void onLostLive(Essence obj, int lives) {
        player_lives = lives;
    }
    
    // KeyListener --------------------------
    @Override
    public void keyTyped(KeyEvent e) {
        int key = e.getKeyCode();
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        oneKeyPressed(key);
        switch (key) {
            case Setup.Controls.KEY_UP:
            case Setup.Controls.KEY_DOWN:
            case Setup.Controls.KEY_RIGHT:
            case Setup.Controls.KEY_LEFT:
                for(GamepadListener listener : gamepadListeners) listener.keyMovemenetPressed(key);
                break;
            case Game.Setup.Controls.KEY_ACTION_1:
                case Setup.Controls.KEY_ACTION_1_Alt:
                
                break;
        }
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        oneKeyReleased(key);
        
        switch (key) {
            case Setup.Controls.KEY_UP:
            case Setup.Controls.KEY_DOWN:
            case Setup.Controls.KEY_RIGHT:
            case Setup.Controls.KEY_LEFT:
                for(GamepadListener listener : gamepadListeners) listener.keyMovemenetReleased(key);
                break;
            case Setup.Controls.KEY_ACTION_1:
                
                break;
        }
    }
    
    // RENDERABLE ---------------------------
    @Override
    public void update() {
        // CHECK ELEMENTS TO REMOVE ----------
        for (Essence obj : essenceToRemove) {
            if (obj instanceof Enemy) {
                Enemy enemy = (Enemy)obj;
                
                int rowIndex = enemy.getRowIndex();
                int columnIndex = enemy.getColumnIndex();
                
                enemies[rowIndex][columnIndex].removeEssenceDestroyedlistener(this);
                enemies[rowIndex][columnIndex] = null;
                enemies_states[rowIndex][columnIndex] = false;
                
                if (!isThereAtLeastAnEnemy()) {
                    restartGame(false);
                }
            }
            
            if (obj instanceof Player) {
                player.removeLiveListener(this);
                player.removeEssenceDestroyedlistener(this);
                
                player = null;
                
                restartGame(true);
            }
        }
        
        if (essenceToRemove.size() > 0) essenceToRemove.clear();
        // -----------------------------------
        
        // CHECK MOVEMENT KEY ----------------
        mapKeyOnePress.forEach((key, state) -> {
            if (state == Setup.Controls.KeyStates.KEY_PRESSED) { // IF WE ALREADY PRESSED
                switch (key) {
                    case Setup.Controls.KEY_ACTION_1:
                    case Setup.Controls.KEY_ACTION_1_Alt:
                        for(GamepadListener listener : gamepadListeners) listener.keyActionTyped(key);
                        break;
                }
                mapKeyOnePress.replace(key, Setup.Controls.KeyStates.KEY_BLOCKED);
            }
        });
        // -----------------------------------
        
        // CONTROL MOVEMENT ENEMY ------------
        enemyMovement();
        // -----------------------------------
        
        for (int i = 0; i < enemies.length; i++)
            for (int j = 0; j < enemies[i].length; j++)
                if(enemies_states[i][j]) enemies[i][j].update();
                
        
        // CONTROL MOVEMENT FOR ALL ENEMIES -
        couldEnemyShoot(0);
        stopMovementForAllEnemies();
        // ----------------------------------
        
        // DEAD -----------------------------
        // Player ---------------------------
        if (player != null) { 
            if (player.hasBeenHit()) {
            
            }
            
            player.update();
        }
        
        checkGameOverByDistance();
    }
    
    @Override
    public void render(Graphics g) {
        // DRAW BACKGROUND
        g.setColor(Setup.Aspects.BACKGROUND);
        g.fillRect(0, 0, Setup.WIDTH, Setup.HEIGHT);
        
        // RENDER ENTITIES
        for (int i = 0; i < enemies.length; i++)
            for (int j = 0; j < enemies[i].length; j++)
                if(enemies_states[i][j]) enemies[i][j].render(g);
                
        if (player != null) player.render(g);
        
        // DRAW ------------------------------
        drawInfo(g);
        // -----------------------------------
    }
    
    // EssenceDestroyedListener -------------
    @Override
    public void onEssenceDestroyed(Essence essence) {
        if (essence instanceof Enemy) { 
            Enemy enemyDestroyed = (Enemy) essence;
            
            score += enemyDestroyed.getScore();
            
            int rowIndex = enemyDestroyed.getRowIndex();
            int columnIndex = enemyDestroyed.getColumnIndex();
            
            enemies[rowIndex][columnIndex].removeHitBox();
            
            updateLimits();
        }
        
        if (essence instanceof Player) { // If we have lost all the lives
            player.removeHitBox();
            removeGamepadListener(player);
            attempts++;
        }
        
        essenceToRemove.add(essence);
    }
    
    // FOCUS --------------------------------
    @Override
    public void windowGainedFocus(WindowEvent e) {}

    @Override
    public void windowLostFocus(WindowEvent e) {}
    
    // LOOP ---------------------------------
    @Override
    public void run() {
        double timePerFrame = 1000000000.0 / Setup.FPS_SET;
        double timePerUpdate = 1000000000.0 / Setup.UPS_SET;
            
        long previousTime = System.nanoTime();
        long lastcheck = System.currentTimeMillis();
        
        int frames = 0;
        int updates = 0;
        
        double deltaU = 0;
        double deltaF = 0;
        
        while(true) {
            long currentTime = System.nanoTime();
            
            deltaU += (currentTime - previousTime) / timePerUpdate;
            deltaF += (currentTime - previousTime) / timePerFrame;
            
            previousTime = currentTime;
            
            if(!panel.hasFocus()) {
                panel.requestFocus();
            }
            
            if (deltaU >= 1) {
                update();
                updates++;
                deltaU--;
            }
                
            if (deltaF >= 1) {
                panel.repaint();
                frames++;
                deltaF--;
            }
            
            // When the time has reached it limit
            if (System.currentTimeMillis() - lastcheck >= 1000) {
                frames = 0;
                updates = 0;
                lastcheck = System.currentTimeMillis();
            }
        }
    }
    // --------------------------------------
}