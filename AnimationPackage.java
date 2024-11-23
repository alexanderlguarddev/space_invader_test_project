import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;

public class AnimationPackage
{
    // PROPETIES ----------------------------
    private BufferedImage[] animation;
    // --------------------------------------

    // STATIC -------------------------------
    public static BufferedImage LoadImage(String url, int row, int column, int width, int height) {
        InputStream is = AnimationPackage.class.getResourceAsStream(url);
        
        try {
            BufferedImage img = ImageIO.read(is);
            
            return img.getSubimage(column * width, row * height, width, height);
        } catch(IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
        
        return null;
    }
    // --------------------------------------
    
    // PUBLIC -------------------------------
    // ANIMATION ----------------------------
    public AnimationPackage(String url, int row_index, int images_count, int width, int height) {
        InputStream is = getClass().getResourceAsStream(url);
        
        try {
            BufferedImage img = ImageIO.read(is);
            
            animation = new BufferedImage[images_count];
            
            for (int i = 0; i < animation.length; i++)
                    animation[i] = img.getSubimage(i * width, row_index * height, width, height);
        } catch(IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    // CHECKERS -----------------------------
    public boolean isEmpty() {
        return animation.length == 0;
    }
    
    // GETTERS ------------------------------
    public int getCurrentStripLimit(){
        int limit = -1;
        
        for(int i = 0; i < animation.length; i++) {
            if (animation[i] == null) break;
            limit = i;
        }
        
        return limit;
    }
    
    public BufferedImage get(int index) { return animation[index]; }
    // --------------------------------------
}
