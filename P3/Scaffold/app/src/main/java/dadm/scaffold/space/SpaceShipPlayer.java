package dadm.scaffold.space;

import java.util.ArrayList;
import java.util.List;

import dadm.scaffold.R;
import dadm.scaffold.engine.GameEngine;
import dadm.scaffold.engine.Sprite;
import dadm.scaffold.input.InputController;

public class SpaceShipPlayer extends Sprite {

    private static final int INITIAL_BULLET_POOL_AMOUNT = 12;
    private static final long TIME_BETWEEN_BULLETS = 250;
    List<Bullet> bullets = new ArrayList<Bullet>();
    private long timeSinceLastFire;

    private int maxX;
    private int maxY;
    private double speedFactor;

    private int healthPoints;


    public SpaceShipPlayer(GameEngine gameEngine){
        super(gameEngine, R.drawable.ship_simple, 1, 3);
        speedFactor = pixelFactor * 100d / 1000d; // We want to move at 100px per second on a 400px tall screen
        maxX = gameEngine.width - imageWidth;
        maxY = gameEngine.height - imageHeight;

        initBulletPool(gameEngine);

        healthPoints = 1;
    }

    private void initBulletPool(GameEngine gameEngine) {
        for (int i=0; i<INITIAL_BULLET_POOL_AMOUNT; i++) {
            bullets.add(new Bullet(gameEngine));
        }
    }

    private Bullet getBullet() {
        if (bullets.isEmpty()) {
            return null;
        }
        return bullets.remove(0);
    }

    void releaseBullet(Bullet bullet) {
        bullets.add(bullet);
    }


    @Override
    public void startGame() {
        positionX = maxX / 2;
        positionY = maxY / 2;
    }

    @Override
    public void onUpdate(long elapsedMillis, GameEngine gameEngine) {
        // Get the info from the inputController
        updatePosition(elapsedMillis, gameEngine.theInputController);
        checkFiring(elapsedMillis, gameEngine);
    }

    @Override
    public void processCollision(GameEngine gameEngine, int collisionGroup) {
        // Si colisiona con un enemigo o un disparo enemigo se le baja 1 vida y si no le quedan
        // vidas llama a shipDestroyed()
        if (collisionGroup == 3 || collisionGroup == 4) {
            healthPoints--;
            if (healthPoints <= 0) {
                shipDestroyed(gameEngine);
            }
        }
    }

    public int getHealthPoints() {
        return healthPoints;
    }

    public void setHealthPoints(int healthPoints) {
        this.healthPoints = healthPoints;
    }

    /*
    Función que se llama cuando el jugador pierde todos sus health Points. Para el juego
     */
    private void shipDestroyed(GameEngine gameEngine) {
        gameEngine.stopGame();
    }

    private void updatePosition(long elapsedMillis, InputController inputController) {
        positionX += speedFactor * inputController.horizontalFactor * elapsedMillis;
        if (positionX < 0) {
            positionX = 0;
        }
        if (positionX > maxX) {
            positionX = maxX;
        }
        positionY += speedFactor * inputController.verticalFactor * elapsedMillis;
        if (positionY < 0) {
            positionY = 0;
        }
        if (positionY > maxY) {
            positionY = maxY;
        }
    }

    private void checkFiring(long elapsedMillis, GameEngine gameEngine) {
        if (gameEngine.theInputController.isFiring && timeSinceLastFire > TIME_BETWEEN_BULLETS) {
            timeSinceLastFire = 0;
            fireBasic(gameEngine);
        }
        else {
            timeSinceLastFire += elapsedMillis;
        }
    }

    /*
    Dispara un proyectil recto
     */
    private void fireBasic(GameEngine gameEngine) {
        Bullet bullet1 = getBullet();
        if (bullet1 == null) {
            return;
        }
        bullet1.init(this, positionX + imageWidth/2, positionY, 0);
        gameEngine.addGameObject(bullet1);
    }

    /*
    Dispara dos proyectiles en diagonal
     */
    private void fireDual(GameEngine gameEngine) {
        Bullet bullet1 = getBullet();
        if (bullet1 == null) {
            return;
        }
        Bullet bullet2 = getBullet();
        if (bullet2 == null) {
            bullet1.release();
            return;
        }
        bullet1.init(this, positionX + imageWidth/2, positionY, 1);
        gameEngine.addGameObject(bullet1);
        bullet2.init(this, positionX + imageWidth/2, positionY, 2);
        gameEngine.addGameObject(bullet2);
    }

}
