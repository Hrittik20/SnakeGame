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
    private static final int HEADER_HEIGHT = 50;

    private ArrayList<Point> snakeSegments = new ArrayList<>();
    private Point foodPosition;
    private int direction = 0; // 0=up, 1=right, 2=down, 3=left
    private Timer gameTimer;
    private boolean gameOver = false;
    private int score = 0;
    private SecureRandom random = new SecureRandom();

    public SnakeGame() {
        initializeWindow();
        initializeGame();
    }

    private void initializeWindow() {
        setTitle("Snake Game");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setFocusable(true);
        addKeyListener(this);

        // Center the window on screen
        setLocationRelativeTo(null);
    }

    private void initializeGame() {
        initializeGameState();
    }

    private void initializeGameState() {
        gameOver = false;
        score = 0;
        direction = 0;
        initializeSnake();
        generateFood();
    }

    private void initializeSnake() {
        snakeSegments.clear();
        int centerX = WINDOW_WIDTH / 2;
        int centerY = WINDOW_HEIGHT / 2;

        snakeSegments.add(new Point(centerX, centerY));
        snakeSegments.add(new Point(centerX - CELL_SIZE, centerY));
        snakeSegments.add(new Point(centerX - 2 * CELL_SIZE, centerY));
    }

    public void startGame() {
        gameTimer = new Timer(GAME_SPEED, this);
        gameTimer.start();
        setVisible(true);
    }

    private void resetGame() {
        if (gameTimer != null) {
            gameTimer.stop();
        }

        initializeGameState();

        gameTimer = new Timer(GAME_SPEED, this);
        gameTimer.start();
    }

    public void paint(Graphics g) {
        super.paint(g);

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);

        if (!gameOver) {
            for (int i = 0; i < snakeSegments.size(); i++) {
                g.setColor(i == 0 ? Color.GREEN : Color.PINK);
                g.fillRect(snakeSegments.get(i).x, snakeSegments.get(i).y, CELL_SIZE, CELL_SIZE);
            }

            g.setColor(Color.RED);
            g.fillRect(foodPosition.x, foodPosition.y, CELL_SIZE, CELL_SIZE);

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 16));
            g.drawString("Score: " + score, 10, 50);
        } else {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 24));
            g.drawString("Game Over! Score: " + score, WINDOW_WIDTH/2 - 120, WINDOW_HEIGHT/2);
            g.drawString("Press R to restart", WINDOW_WIDTH/2 - 80, WINDOW_HEIGHT/2 + 30);
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (!gameOver) {
            Point head = new Point(snakeSegments.get(0));

            switch (direction) {
                case 0 -> head.y -= CELL_SIZE;
                case 1 -> head.x += CELL_SIZE;
                case 2 -> head.y += CELL_SIZE;
                case 3 -> head.x -= CELL_SIZE;
            }

            if (head.x < 0 || head.x >= WINDOW_WIDTH || head.y < HEADER_HEIGHT || head.y >= WINDOW_HEIGHT) {
                gameOver = true;
                gameTimer.stop();
                return;
            }

            for (Point segment : snakeSegments) {
                if (head.equals(segment)) {
                    gameOver = true;
                    gameTimer.stop();
                    return;
                }
            }

            snakeSegments.add(0, head);

            if (head.equals(foodPosition)) {
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
            int maxX = (WINDOW_WIDTH - CELL_SIZE) / CELL_SIZE;
            int maxY = (WINDOW_HEIGHT - HEADER_HEIGHT - CELL_SIZE) / CELL_SIZE;

            int foodX = random.nextInt(maxX) * CELL_SIZE;
            int foodY = random.nextInt(maxY) * CELL_SIZE + HEADER_HEIGHT;

            foodPosition = new Point(foodX, foodY);

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
            if (key == KeyEvent.VK_R) {
                resetGame();
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

    @Override
    public void dispose() {
        if (gameTimer != null && gameTimer.isRunning()) {
            gameTimer.stop();
        }
        super.dispose();
    }
}

class GameLauncher {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                System.err.println("Could not set system look and feel: " + e.getMessage());
            }

            SnakeGame game = new SnakeGame();
            if (game != null) {
            }
        });
    }
}