import java.awt.Dimension;

public class Size
{
    // PROPETIES ----------------------------
    private Dimension spriteSize;
    private Dimension renderSize;
    // --------------------------------------
    
    public Size(int w, int h) {
        int rate = Game.Setup.RATE;
        
        spriteSize = new Dimension(w, h);
        renderSize = new Dimension(w * rate, h * rate);
    }
    
    // PUBLIC -------------------------------
    // GETTERS ------------------------------
    public int spriteWidth() { return spriteSize.width; }
    public int spriteHeight() { return spriteSize.height; }
    public int renderWidth() { return renderSize.width; }
    public int renderHeight() { return renderSize.height; }
    // --------------------------------------
}