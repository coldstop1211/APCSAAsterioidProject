package FinalProject;

import javax.swing.JComponent;
import java.util.Random;

public class Asteroid {
    private int asteroidX;
    private int asteroidY;
    private boolean isDestroyed;
    private JComponent component;

    public Asteroid(JComponent component) {
        this.component = component;
        this.isDestroyed = false;
        this.asteroidX = new Random().nextInt(component.getWidth() - 30); 
        this.asteroidY = 0;
    }

    public int getAsteroidX() {
        return asteroidX;
    }

    public int getAsteroidY() {
        return asteroidY;
    }

    public boolean isDestroyed() {
        return isDestroyed;
    }

    public void setDestroyed(boolean isDestroyed) {
        this.isDestroyed = isDestroyed;
    }

    public void updateAsteroid() {
        if (!isDestroyed) {
            asteroidY += 5; 
            if (asteroidY > component.getHeight()) {
                asteroidY = 0;
                asteroidX = new Random().nextInt(component.getWidth() - 30); 
            }
        }
    }
}
