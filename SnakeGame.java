import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.security.SecureRandom;
import java.util.ArrayList;

public class SnakeGame extends JFrame implements ActionListener, KeyListener {

    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 600;
    private static final int CELL_SIZE = 20;
    private static final int GAME_SPEED = 100;
    private int width = WINDOW_WIDTH, height = WINDOW_HEIGHT;

    private ArrayList<Point> snakeSegments = new ArrayList<>();
    private Point foodPosition;
    private int direction = 0; // 0=up, 1=right, 2=down, 3=left
    private Timer gameTimer;
    private boolean gameOver = false;
    private int score = 0;
    private SecureRandom random = new SecureRandom();


    public SnakeGame() {
        this.setTitle("Snake Game");
        this.setSize(width, height);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.addKeyListener(this);
        this.setFocusable(true);

        snakeSegments.add(new Point(400, 300));
        snakeSegments.add(new Point(380, 300));
        snakeSegments.add(new Point(360, 300));

        generateFood();

        gameTimer = new Timer(100, this);
        gameTimer.start();

        this.setVisible(true);
    }

    public void paint(Graphics g) {
        super.paint(g);

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, width, height);

        if (!gameOver) {
            // Draw snake
            for (int i = 0; i < snakeSegments.size(); i++) {
                if (i == 0) {
                    g.setColor(Color.GREEN);
                } else {
                    g.setColor(Color.PINK);
                }
                g.fillRect(snakeSegments.get(i).x, snakeSegments.get(i).y, 20, 20);
            }

            // Draw food
            g.setColor(Color.RED);
            g.fillRect(foodPosition.x, foodPosition.y, 20, 20);

            // Draw score
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 16));
            g.drawString("Score: " + score, 10, 50);
        } else {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 24));
            g.drawString("Game Over! Score: " + score, width/2 - 120, height/2);
            g.drawString("Press R to restart", width/2 - 80, height/2 + 30);
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (!gameOver) {
            Point head = new Point(snakeSegments.get(0));

            switch (direction) {
                case 0: head.y -= 20; break; // up
                case 1: head.x += 20; break; // right
                case 2: head.y += 20; break; // down
                case 3: head.x -= 20; break; // left
            }

            if (head.x < 0 || head.x >= width || head.y < 50 || head.y >= height) {
                gameOver = true;
                gameTimer.stop();
                return;
            }

            for (int i = 0; i < snakeSegments.size(); i++) {
                if (head.equals(snakeSegments.get(i))) {
                    gameOver = true;
                    gameTimer.stop();
                    return;
                }
            }

            snakeSegments.add(0, head);

            if (head.x == foodPosition.x && head.y == foodPosition.y) {
                score += 10;
                generateFood();
            } else {
                snakeSegments.remove(snakeSegments.size() - 1);
            }
        }
        repaint();
    }

    private void generateFood() {
        boolean validPosition = false;

        while (!validPosition) {
            foodPosition = new Point((random.nextInt((width-20)/20))*20, (random.nextInt((height-70)/20))*20 + 50);

            validPosition = true;
            for (Point segment : snakeSegments) {
                if (segment.equals(foodPosition)) {
                    validPosition = false;
                    break;
                }
            }
        }
    }

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (gameOver) {
            if (key == 82) { // R key
                gameOver = false;
                score = 0;
                snakeSegments.clear();
                snakeSegments.add(new Point(400, 300));
                snakeSegments.add(new Point(380, 300));
                snakeSegments.add(new Point(360, 300));
                direction = 0;
                generateFood();
                gameTimer.restart();
            }
        } else {
            if (key == KeyEvent.VK_UP && direction != 2) {
                direction = 0;
            } else if (key == KeyEvent.VK_RIGHT && direction != 3) {
                direction = 1;
            } else if (key == KeyEvent.VK_DOWN && direction != 0) {
                direction = 2;
            } else if (key == KeyEvent.VK_LEFT && direction != 1) {
                direction = 3;
            }
        }
    }

    public void keyReleased(KeyEvent e) {}
    public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        new SnakeGame();
    }
}