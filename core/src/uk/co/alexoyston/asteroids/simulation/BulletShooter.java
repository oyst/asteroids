package uk.co.alexoyston.asteroids.simulation;

public interface BulletShooter {
  public void onBulletHit(Bullet bullet, Entity hitEntity);
  public void onBulletDecay(Bullet bullet);
}
