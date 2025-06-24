import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.security.SecureRandom;
import java.util.ArrayList;

public class SnakeGame extends JFrame implements ActionListener, KeyListener {

    private ArrayList<Point> snakeSegments = new ArrayList<>();
    private Point foodPosition;
    private Direction direction = Direction.UP;
    private Timer gameTimer;
    private boolean gameOver = false;
    private boolean isPaused = false;
    private int score = 0;
    private SecureRandom random = new SecureRandom();

    public SnakeGame() {
        initializeWindow();
        initializeGame();
    }

    private void initializeWindow() {
        setTitle("Snake Game");
        setSize(GameConfig.WINDOW_WIDTH, GameConfig.WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setFocusable(true);
        addKeyListener(this);
        setLocationRelativeTo(null);
    }

    private void initializeGame() {
        initializeGameState();
    }

    private void initializeGameState() {
        gameOver = false;
        score = 0;
        direction = Direction.UP;
        initializeSnake();
        generateFood();
    }

    private void initializeSnake() {
        snakeSegments.clear();
        int centerX = GameConfig.WINDOW_WIDTH / 2;
        int centerY = GameConfig.WINDOW_HEIGHT / 2;

        snakeSegments.add(new Point(centerX, centerY));
        snakeSegments.add(new Point(centerX - GameConfig.CELL_SIZE, centerY));
        snakeSegments.add(new Point(centerX - 2 * GameConfig.CELL_SIZE, centerY));
    }

    public void startGame() {
        gameTimer = new Timer(GameConfig.GAME_SPEED, this);
        gameTimer.start();
        setVisible(true);
    }

    private void resetGame() {
        if (gameTimer != null) {
            gameTimer.stop();
        }

        initializeGameState();

        gameTimer = new Timer(GameConfig.GAME_SPEED, this);
        gameTimer.start();
    }

    public void paint(Graphics g) {
        super.paint(g);
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, GameConfig.WINDOW_WIDTH, GameConfig.WINDOW_HEIGHT);

        if (gameOver) {
            renderGameOver(g);
        } else if (isPaused) {
            renderPaused(g);
        } else {
            renderGame(g);
        }
    }

    private void renderGame(Graphics g) {
        for (int i = 0; i < snakeSegments.size(); i++) {
            g.setColor(i == 0 ? Color.GREEN : Color.PINK);
            Point p = snakeSegments.get(i);
            g.fillRect(p.x, p.y, GameConfig.CELL_SIZE, GameConfig.CELL_SIZE);
        }

        g.setColor(Color.RED);
        g.fillRect(foodPosition.x, foodPosition.y, GameConfig.CELL_SIZE, GameConfig.CELL_SIZE);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Score: " + score, 10, 50);
    }

    private void renderGameOver(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString("Game Over! Score: " + score,
                GameConfig.WINDOW_WIDTH / 2 - 120,
                GameConfig.WINDOW_HEIGHT / 2);
        g.drawString("Press R to restart",
                GameConfig.WINDOW_WIDTH / 2 - 80,
                GameConfig.WINDOW_HEIGHT / 2 + 30);
    }

    private void renderPaused(Graphics g) {
        g.setColor(Color.YELLOW);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString("Game Paused", GameConfig.WINDOW_WIDTH / 2 - 80, GameConfig.WINDOW_HEIGHT / 2);
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        g.drawString("Press P to resume", GameConfig.WINDOW_WIDTH / 2 - 60, GameConfig.WINDOW_HEIGHT / 2 + 30);
    }

    public void actionPerformed(ActionEvent e) {
        if (gameOver || isPaused) return;

        Point head = getNextHeadPosition();

        if (isOutOfBounds(head) || checkSelfCollision(head)) {
            endGame();
            return;
        }

        moveSnake(head);
        handleFoodConsumption(head);
        repaint();
    }

    private Point getNextHeadPosition() {
        Point head = new Point(snakeSegments.get(0));
        head.translate(direction.dx * GameConfig.CELL_SIZE, direction.dy * GameConfig.CELL_SIZE);
        return head;
    }

    private boolean isOutOfBounds(Point p) {
        return p.x < 0 || p.x >= GameConfig.WINDOW_WIDTH ||
                p.y < GameConfig.HEADER_HEIGHT || p.y >= GameConfig.WINDOW_HEIGHT;
    }

    private boolean checkSelfCollision(Point head) {
        for (Point segment : snakeSegments) {
            if (head.equals(segment)) return true;
        }
        return false;
    }

    private void moveSnake(Point head) {
        snakeSegments.add(0, head);
        snakeSegments.remove(snakeSegments.size() - 1);
    }

    private void handleFoodConsumption(Point head) {
        if (head.equals(foodPosition)) {
            score += 10;
            generateFood();
            snakeSegments.add(snakeSegments.get(snakeSegments.size() - 1));
        }
    }

    private void endGame() {
        gameOver = true;
        gameTimer.stop();
    }

    private void generateFood() {
        boolean validPosition = false;
        while (!validPosition) {
            int maxX = (GameConfig.WINDOW_WIDTH - GameConfig.CELL_SIZE) / GameConfig.CELL_SIZE;
            int maxY = (GameConfig.WINDOW_HEIGHT - GameConfig.HEADER_HEIGHT - GameConfig.CELL_SIZE) / GameConfig.CELL_SIZE;

            int foodX = random.nextInt(maxX) * GameConfig.CELL_SIZE;
            int foodY = random.nextInt(maxY) * GameConfig.CELL_SIZE + GameConfig.HEADER_HEIGHT;

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
        } else if (key == KeyEvent.VK_P) {
            isPaused = !isPaused;
        } else {
            switch (key) {
                case KeyEvent.VK_UP:
                    if (direction != Direction.DOWN) direction = Direction.UP;
                    break;
                case KeyEvent.VK_RIGHT:
                    if (direction != Direction.LEFT) direction = Direction.RIGHT;
                    break;
                case KeyEvent.VK_DOWN:
                    if (direction != Direction.UP) direction = Direction.DOWN;
                    break;
                case KeyEvent.VK_LEFT:
                    if (direction != Direction.RIGHT) direction = Direction.LEFT;
                    break;
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

    enum Direction {
        UP(0, -1), RIGHT(1, 0), DOWN(0, 1), LEFT(-1, 0);
        final int dx, dy;
        Direction(int dx, int dy) { this.dx = dx; this.dy = dy; }
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
            game.startGame();
            if (game != null) {
            }
        });
    }
}