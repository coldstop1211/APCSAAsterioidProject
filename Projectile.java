package FinalProject;

public class Projectile {
    private int xPosition;
    private int yPosition;
    private int speed = 7;

    public Projectile(int xPosition, int yPosition) {
        this.xPosition = xPosition;
        this.yPosition = yPosition;
    }

    public int getXPosition() {
        return xPosition;
    }

    public int getYPosition() {
        return yPosition;
    }

    public void updateProjectilePosition() {
        yPosition -= speed;
    }
}

