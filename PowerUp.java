package FinalProject;

import javax.swing.JComponent;
import java.awt.Graphics;
import java.awt.Color;

public class PowerUp {
    private int x, y;
    private boolean collected;
    @SuppressWarnings("unused")
    private JComponent component;

    public PowerUp(JComponent component) {
        this.component = component;
        this.x = (int)(Math.random() * component.getWidth());
        this.y = (int)(Math.random() * component.getHeight()-50);
        this.collected = false;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isCollected() {
        return collected;
    }

    public void setCollected(boolean collected) {
        this.collected = collected;
    }

    public void draw(Graphics g) {
        if (!collected) {
            g.setColor(Color.BLUE);
            g.fillOval(x, y, 15, 15);
        }
    }
}

