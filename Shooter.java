public interface Shooter
{
    void onMissingBullet(Bullet bullet);
    void onHitBullet(Target target, Bullet bullet);
}