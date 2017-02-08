package uk.co.alexoyston.asteroids.simulation;

public class PhysicsParams {

	public static float updateDelta = 0.01f;

	public static int worldWidth = 700;
	public static int worldHeight = 700;

	public static int asteroidStartCount = 4;

	public static int saucersMax = 1;
	public static float saucersProbSmall = 0.3f;
	public static float saucersFreq = 0.5f;

	public static int asteroidStartSize = 2;
	public static float asteroidMinSpeed = 15f;
	public static float asteroidMaxSpeed = 25f;
	public static float asteroidSplitBoost = 7;

	public static float playerDrag = 0.3f;
	public static float playerRotationPower = 3f;
	public static float playerThrustPower = 120f;
	public static int playerMaxActiveShots = 3;
	public static float playerShotSpeed = 600f;
	public static float playerSpawnProtectDuration = 1f;
	public static int playerAsteroidHitScore = 100;
	public static int playerSaucerHitScore = 300;
	public static int playerSmallSaucerHitScore = 500;

	public static float saucerSpeed = 35f;
	public static float saucerTurnFreq = 1f;
	public static float saucerReloadTime = 0.7f;
	public static float saucerShotSpeed = 250f;

	public static float smallSaucerSpeed = 35f;
	public static float smallSaucerTurnFreq = 1f;
	public static float smallSaucerReloadTime = 0.7f;
	public static float smallSaucerShotSpeed = 250f;
	//TODO: public static float smallSaucerShotAccuracy = ?;

}
