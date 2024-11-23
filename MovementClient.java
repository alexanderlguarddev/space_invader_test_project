import java.awt.Point;

public interface MovementClient
{
    Point getCurrentPosition();
    Hitbox getHitbox();
    void move(int dx, int dy);
}