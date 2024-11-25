package FinalProject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.sound.sampled.*;
import java.io.*;




public class Game extends JComponent {
    private int shipX;
    private int shipY;
    private int time = 500; //this should be enough time :\
    private int lives = 3;
    private int asteroidsHit = 0;
    private boolean gameOver = false;
    private ArrayList<Projectile> projectiles;
    private ArrayList<Asteroid> asteroids;
    private ArrayList<Rectangle> enemyRectangles;
    private Rectangle playerRectangle;
    private Timer timer;
    private JFrame frame;
    private boolean gameStarted = false;
    private ArrayList<PowerUp> powerUps;
    private int score;



    public Game(JFrame frame) {
        this.frame = frame;
        asteroids = new ArrayList<>();
        projectiles = new ArrayList<>();
        enemyRectangles = new ArrayList<>();
        shipX = frame.getWidth() / 2;
        shipY = frame.getHeight() - 50;
        playerRectangle = new Rectangle(shipX, shipY, 50, 50); 
        powerUps = new ArrayList<>();
        score = 0;
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent event) {
                if (!gameStarted) {
                    gameStarted = true;
                } else {
                    handleKeyPress(event);
                }
            }
        });


        timer = new Timer(50, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                if (!gameOver) {
                    updateScreen();
                    frame.repaint();
                }
            }
        });
        timer.start();
    }

    public void updateEnemyRectangles() {
        enemyRectangles.clear();
        for (Asteroid asteroid : asteroids) {
            enemyRectangles.add(new Rectangle(asteroid.getAsteroidX(), asteroid.getAsteroidY(), 30, 30));
        }
    }

    private void handleKeyPress(KeyEvent event) {
        int key = event.getKeyCode();
        if (key == KeyEvent.VK_A && shipX > 0) {
            shipX -= 25;
        } else if (key == KeyEvent.VK_D && shipX < frame.getWidth()) {
            shipX += 25;
        } else if (key == KeyEvent.VK_W && shipY > 0) {
            shipY -= 25;
        } else if (key == KeyEvent.VK_S && shipY < frame.getHeight() - playerRectangle.height) {
            shipY += 25;
        } else if (key == KeyEvent.VK_SPACE) {
            shoot();
        }
        playerRectangle.setLocation(shipX, shipY);
    }
    

    private void shoot() {
        projectiles.add(new Projectile(shipX, shipY)); 
        playSound();
    }
    

    private void checkForAsteroidCollisions() {
        for (int i = 0; i < enemyRectangles.size(); i++) {
            if (enemyRectangles.get(i).intersects(playerRectangle)) {
                enemyRectangles.remove(i);
                asteroids.remove(i);
                lives--;
                if (lives <= 0) {
                    gameOver = true;
                }
                break;
            }
        }
    }

    private void generateNewAsteroid() {
        Random rand = new Random();
        
        if (asteroids.size() < 10) { 
            if (rand.nextInt(100) < 10) { 
                Asteroid asteroid = new Asteroid(this);
                asteroids.add(asteroid);
                enemyRectangles.add(new Rectangle(asteroid.getAsteroidX(), asteroid.getAsteroidY(), 20, 20)); 
            }
        }
    }
    

    private void removeAsteroid(int index) {
        asteroids.get(index).setDestroyed(true);
        asteroids.remove(index);
        enemyRectangles.remove(index);
    }

    private void updateAsteroidLocation() {
        for (Asteroid asteroid : asteroids) {
            asteroid.updateAsteroid();
        }
    }

    private void checkProjectileCollisions() {
        ArrayList<Integer> projectilesToRemove = new ArrayList<>();
        for (int i = projectiles.size() - 1; i >= 0; i--) {
            Projectile projectile = projectiles.get(i);
            projectile.updateProjectilePosition();
            if (projectile.getYPosition() < 0) {
                projectilesToRemove.add(i);
            } else {
                for (int j = asteroids.size() - 1; j >= 0; j--) {
                    Asteroid asteroid = asteroids.get(j);
                    if (new Rectangle(asteroid.getAsteroidX(), asteroid.getAsteroidY(), 30, 30).contains(projectile.getXPosition(), projectile.getYPosition())) { 
                        projectilesToRemove.add(i);
                        removeAsteroid(j);
                        asteroidsHit++;
                        score += 10;
                        break;
                    }
                }
            }
        }
        for (int index : projectilesToRemove) {
            projectiles.remove(index);
        }
    }

    private void updateProjectiles() {
        for (Projectile projectile : projectiles) {
            projectile.updateProjectilePosition();
        }
    }

    private void updateScreen() {
        checkForAsteroidCollisions();
        updateAsteroidLocation();
        generateNewAsteroid();
        checkProjectileCollisions();
        updateProjectiles();
        generatePowerUp();
        checkForPowerUpCollection();
    }
    

    private void drawShip(Graphics graphics) {
        int[] xPoints = {shipX, shipX - 10, shipX + 10};
        int[] yPoints = {shipY, shipY + 20, shipY + 20};
    
        // Change color based on lives left
        if (lives == 3) {
            graphics.setColor(Color.GREEN);
        } else if (lives == 2) {
            graphics.setColor(Color.YELLOW);
        } else if (lives == 1) {
            graphics.setColor(Color.RED);
        }
    
        graphics.fillPolygon(xPoints, yPoints, 3);
    }
    

    private void drawAsteroids(Graphics graphics) {
        graphics.setColor(Color.GRAY);
        for (Asteroid asteroid : asteroids) {
            if (!asteroid.isDestroyed()) {
                graphics.fillOval(asteroid.getAsteroidX(), asteroid.getAsteroidY(), 30, 30); 
            }
        }
        updateEnemyRectangles();
    }

    private void drawProjectiles(Graphics graphics) {
        graphics.setColor(Color.YELLOW);
        for (Projectile projectile : projectiles) {
            graphics.fillRect(projectile.getXPosition(), projectile.getYPosition(), 6, 10); 
        }
    }

    private void setEndScreenText(Graphics graphics, String str) {
        graphics.setColor(Color.WHITE);
        graphics.drawString(str, frame.getWidth() / 2 - 100, frame.getHeight() / 2);
    }

    private void startScreenText(Graphics graphicsTitle, String s){
        graphicsTitle.setColor(Color.WHITE);
        graphicsTitle.drawString(s, frame.getWidth() / 2 - 100, frame.getHeight() / 2);
    }

    private void setGameOver(Graphics graphics) {
        String message;
        if (asteroids.isEmpty()) {
            message = "ALL ASTEROIDS DESTROYED, YOU WIN! Ws IN THE CHAT";
        } else if (lives <= 0) {
            message = "ALL LIVES LOST, YOU LOSE! L BOZO!";
        } else {
            time = 0;
            message = "OUT OF TIME, YOU LOSE! SLOWPOKE!";
        }
        setEndScreenText(graphics, message);
        graphics.setColor(Color.GREEN);
        graphics.drawString("GAME OVER!", frame.getWidth() / 2 - 50, frame.getHeight() / 2 + 30);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        


        graphics.setColor(Color.BLACK);
        graphics.fillRect(0, 0, frame.getWidth(), frame.getHeight());

        graphics.setColor(Color.WHITE);
        graphics.drawString("Asteroids Hit: " + asteroidsHit, 10, 20);

        graphics.drawString("Time Left: " + time, frame.getWidth() - 100, 20);

        if (!gameStarted) {
            startScreenText(graphics, "Use WASD to move. Press a key to start!");
        } else if (!gameOver) {
            drawShip(graphics);
            drawAsteroids(graphics);
            drawProjectiles(graphics);
            time -= 1;
            if (time <= 0 || lives <= 0) {
                gameOver = true;
                setGameOver(graphics);
            }
        } else {
            setGameOver(graphics);
        }

        for (PowerUp powerUp : powerUps) {
            powerUp.draw(graphics);
        }
    
        graphics.setColor(Color.WHITE);
        graphics.drawString("Score: " + score, 200, 20);

    }


    //modifications
    private void generatePowerUp() {
        if (powerUps.size() < 3 && Math.random() < 0.01) { // limit to 3 power-ups
            powerUps.add(new PowerUp(this));
        }
    }

    private void checkForPowerUpCollection() {
        for (PowerUp powerUp : powerUps) {
            if (!powerUp.isCollected() && playerRectangle.intersects(new Rectangle(powerUp.getX(), powerUp.getY(), 15, 15))) {
                powerUp.setCollected(true);
                score += 100; 
            }
        }
    }
    
    private void playSound() {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("shoot.wav").getAbsoluteFile());
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    

}
