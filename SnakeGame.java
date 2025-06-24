import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.security.SecureRandom;
import java.util.ArrayList;

public class SnakeGame extends JFrame implements ActionListener, KeyListener {

    private final Renderer renderer = new GameRenderer();

    private final ArrayList<Point> snakeSegments = new ArrayList<>();
    private Point foodPosition;
    private Direction direction = Direction.UP;
    private Timer gameTimer;
    private boolean gameOver = false;
    private boolean isPaused = false;
    private int score = 0;
    private final SecureRandom random = new SecureRandom();

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
        isPaused = false;
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
        renderer.render(g, this);
    }

    public void actionPerformed(ActionEvent e) {
        if (!gameOver && !isPaused) {
            Point head = getNextHeadPosition();

            if (isOutOfBounds(head) || checkSelfCollision(head)) {
                endGame();
                return;
            }

            moveSnake(head);
            handleFoodConsumption(head);
        }
        repaint();
    }

    private Point getNextHeadPosition() {
        Point head = new Point(snakeSegments.get(0));
        head.translate(direction.dx * GameConfig.CELL_SIZE, direction.dy * GameConfig.CELL_SIZE);
        return head;
    }

    private boolean checkSelfCollision(Point head) {
        for (Point segment : snakeSegments) {
            if (head.equals(segment)) {
                return true;
            }
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

    private boolean isOutOfBounds(Point p) {
        return p.x < 0 || p.x >= GameConfig.WINDOW_WIDTH ||
                p.y < GameConfig.HEADER_HEIGHT || p.y >= GameConfig.WINDOW_HEIGHT;
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

        if (key == KeyEvent.VK_P) {
            isPaused = !isPaused;
            repaint();
        } else if (gameOver && key == KeyEvent.VK_R) {
            resetGame();
        } else if (!isPaused) {
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

    public boolean isGameOver() { return gameOver; }
    public boolean isPaused() { return isPaused; }
    public ArrayList<Point> getSnakeSegments() { return snakeSegments; }
    public Point getFoodPosition() { return foodPosition; }
    public int getScore() { return score; }

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