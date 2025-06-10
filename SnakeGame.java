import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

// ========== MAIN GAME WINDOW CLASS ==========

public class SnakeGame extends JFrame {
    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 600;

    private GameEngine gameEngine;
    private GameRenderer gameRenderer;
    private InputHandler inputHandler;
    private Timer gameTimer;

    public SnakeGame() {
        initializeComponents();
        setupWindow();
        startGame();
    }

    private void initializeComponents() {
        gameEngine = new GameEngine(WINDOW_WIDTH, WINDOW_HEIGHT);
        gameRenderer = new GameRenderer();
        inputHandler = new InputHandler(gameEngine);
    }

    private void setupWindow() {
        setTitle("Snake Game");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        addKeyListener(inputHandler);
        setFocusable(true);
    }

    private void startGame() {
        gameTimer = new Timer(GameConstants.GAME_SPEED, e -> gameLoop());
        gameTimer.start();
        setVisible(true);
    }

    private void gameLoop() {
        if (!gameEngine.isGameOver()) {
            gameEngine.update();
        }
        repaint();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        gameRenderer.render(g, gameEngine, getWidth(), getHeight());
    }

    public void restartGame() {
        gameEngine.reset();
        gameTimer.restart();
    }
}

// ========== GAME CONSTANTS ==========

class GameConstants {
    public static final int CELL_SIZE = 20;
    public static final int GAME_SPEED = 100;
    public static final int HEADER_HEIGHT = 50;
    public static final int SCORE_PER_FOOD = 10;

    // Colors
    public static final Color BACKGROUND_COLOR = Color.BLACK;
    public static final Color SNAKE_HEAD_COLOR = Color.GREEN;
    public static final Color SNAKE_BODY_COLOR = Color.PINK;
    public static final Color FOOD_COLOR = Color.RED;
    public static final Color TEXT_COLOR = Color.WHITE;

    // Keys
    public static final int RESTART_KEY = KeyEvent.VK_R;
}

// ========== DIRECTION ENUM ==========

enum Direction {
    UP(0, -1, 0),
    RIGHT(1, 0, 1),
    DOWN(0, 1, 2),
    LEFT(-1, 0, 3);

    private final int deltaX;
    private final int deltaY;
    private final int code;

    Direction(int deltaX, int deltaY, int code) {
        this.deltaX = deltaX;
        this.deltaY = deltaY;
        this.code = code;
    }

    public Point move(Point current) {
        return new Point(
                current.x + deltaX * GameConstants.CELL_SIZE,
                current.y + deltaY * GameConstants.CELL_SIZE
        );
    }

    public boolean isOpposite(Direction other) {
        return (this.code + other.code) == 3 && Math.abs(this.code - other.code) == 2;
    }

    public static Direction fromCode(int code) {
        for (Direction dir : values()) {
            if (dir.code == code) return dir;
        }
        return UP; // default
    }
}

// ========== GAME ENGINE CLASS ==========

class GameEngine {
    private final int boardWidth;
    private final int boardHeight;
    private final SecureRandom random;

    private List<Point> snakeSegments;
    private Point foodPosition;
    private Direction currentDirection;
    private boolean gameOver;
    private int score;

    public GameEngine(int width, int height) {
        this.boardWidth = width;
        this.boardHeight = height;
        this.random = new SecureRandom();
        reset();
    }

    public void reset() {
        gameOver = false;
        score = 0;
        currentDirection = Direction.UP;
        initializeSnake();
        generateFood();
    }

    private void initializeSnake() {
        snakeSegments = new ArrayList<>();
        int centerX = boardWidth / 2;
        int centerY = boardHeight / 2;

        snakeSegments.add(new Point(centerX, centerY));
        snakeSegments.add(new Point(centerX - GameConstants.CELL_SIZE, centerY));
        snakeSegments.add(new Point(centerX - 2 * GameConstants.CELL_SIZE, centerY));
    }

    public void update() {
        if (gameOver) return;

        moveSnake();
        checkCollisions();
        handleFoodConsumption();
    }

    private void moveSnake() {
        if (snakeSegments.isEmpty()) return;

        Point newHead = currentDirection.move(snakeSegments.get(0));
        snakeSegments.add(0, newHead);
    }

    private void checkCollisions() {
        Point head = snakeSegments.get(0);

        // Check wall collisions
        if (isOutOfBounds(head)) {
            gameOver = true;
            return;
        }

        // Check self collision
        for (int i = 1; i < snakeSegments.size(); i++) {
            if (head.equals(snakeSegments.get(i))) {
                gameOver = true;
                return;
            }
        }
    }

    private boolean isOutOfBounds(Point point) {
        return point.x < 0 ||
                point.x >= boardWidth ||
                point.y < GameConstants.HEADER_HEIGHT ||
                point.y >= boardHeight;
    }

    private void handleFoodConsumption() {
        Point head = snakeSegments.get(0);

        if (head.equals(foodPosition)) {
            score += GameConstants.SCORE_PER_FOOD;
            generateFood();
        } else {
            // Remove tail if no food consumed
            snakeSegments.remove(snakeSegments.size() - 1);
        }
    }

    private void generateFood() {
        final int MAX_ATTEMPTS = 100;
        int attempts = 0;

        do {
            foodPosition = generateRandomPosition();
            attempts++;
        } while (!isValidFoodPosition() && attempts < MAX_ATTEMPTS);

        if (attempts >= MAX_ATTEMPTS) {
            // Fallback: place food at first available position
            findFirstAvailablePosition();
        }
    }

    private Point generateRandomPosition() {
        int maxX = (boardWidth - GameConstants.CELL_SIZE) / GameConstants.CELL_SIZE;
        int maxY = (boardHeight - GameConstants.HEADER_HEIGHT - GameConstants.CELL_SIZE) / GameConstants.CELL_SIZE;

        int x = random.nextInt(maxX) * GameConstants.CELL_SIZE;
        int y = random.nextInt(maxY) * GameConstants.CELL_SIZE + GameConstants.HEADER_HEIGHT;

        return new Point(x, y);
    }

    private boolean isValidFoodPosition() {
        return snakeSegments.stream().noneMatch(segment -> segment.equals(foodPosition));
    }

    private void findFirstAvailablePosition() {
        for (int y = GameConstants.HEADER_HEIGHT; y < boardHeight; y += GameConstants.CELL_SIZE) {
            for (int x = 0; x < boardWidth; x += GameConstants.CELL_SIZE) {
                Point candidate = new Point(x, y);
                if (snakeSegments.stream().noneMatch(segment -> segment.equals(candidate))) {
                    foodPosition = candidate;
                    return;
                }
            }
        }
    }

    public void changeDirection(Direction newDirection) {
        if (newDirection != null && !newDirection.isOpposite(currentDirection)) {
            currentDirection = newDirection;
        }
    }

    // Getters
    public List<Point> getSnakeSegments() { return new ArrayList<>(snakeSegments); }
    public Point getFoodPosition() { return new Point(foodPosition); }
    public boolean isGameOver() { return gameOver; }
    public int getScore() { return score; }
    public Direction getCurrentDirection() { return currentDirection; }
}

// ========== GAME RENDERER CLASS ==========

class GameRenderer {

    public void render(Graphics g, GameEngine gameEngine, int width, int height) {
        Graphics2D g2d = (Graphics2D) g;

        drawBackground(g2d, width, height);

        if (!gameEngine.isGameOver()) {
            drawSnake(g2d, gameEngine.getSnakeSegments());
            drawFood(g2d, gameEngine.getFoodPosition());
            drawScore(g2d, gameEngine.getScore());
        } else {
            drawGameOverScreen(g2d, gameEngine.getScore(), width, height);
        }
    }

    private void drawBackground(Graphics2D g, int width, int height) {
        g.setColor(GameConstants.BACKGROUND_COLOR);
        g.fillRect(0, 0, width, height);
    }

    private void drawSnake(Graphics2D g, List<Point> segments) {
        for (int i = 0; i < segments.size(); i++) {
            Point segment = segments.get(i);
            Color segmentColor = (i == 0) ? GameConstants.SNAKE_HEAD_COLOR : GameConstants.SNAKE_BODY_COLOR;

            g.setColor(segmentColor);
            g.fillRect(segment.x, segment.y, GameConstants.CELL_SIZE, GameConstants.CELL_SIZE);
        }
    }

    private void drawFood(Graphics2D g, Point foodPosition) {
        g.setColor(GameConstants.FOOD_COLOR);
        g.fillRect(foodPosition.x, foodPosition.y, GameConstants.CELL_SIZE, GameConstants.CELL_SIZE);
    }

    private void drawScore(Graphics2D g, int score) {
        g.setColor(GameConstants.TEXT_COLOR);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Score: " + score, 10, 30);
    }

    private void drawGameOverScreen(Graphics2D g, int score, int width, int height) {
        g.setColor(GameConstants.TEXT_COLOR);
        g.setFont(new Font("Arial", Font.BOLD, 24));

        String gameOverText = "Game Over! Score: " + score;
        String restartText = "Press R to restart";

        FontMetrics fm = g.getFontMetrics();
        int gameOverWidth = fm.stringWidth(gameOverText);
        int restartWidth = fm.stringWidth(restartText);

        g.drawString(gameOverText, (width - gameOverWidth) / 2, height / 2);
        g.drawString(restartText, (width - restartWidth) / 2, height / 2 + 40);
    }
}

// ========== INPUT HANDLER CLASS ==========

class InputHandler extends KeyAdapter {
    private final GameEngine gameEngine;
    private SnakeGame gameWindow;

    public InputHandler(GameEngine gameEngine) {
        this.gameEngine = gameEngine;
    }

    public void setGameWindow(SnakeGame gameWindow) {
        this.gameWindow = gameWindow;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();

        if (gameEngine.isGameOver()) {
            handleGameOverInput(keyCode);
        } else {
            handleGameplayInput(keyCode);
        }
    }

    private void handleGameOverInput(int keyCode) {
        if (keyCode == GameConstants.RESTART_KEY) {
            if (gameWindow != null) {
                gameWindow.restartGame();
            }
        }
    }

    private void handleGameplayInput(int keyCode) {
        Direction newDirection = null;

        switch (keyCode) {
            case KeyEvent.VK_UP:
                newDirection = Direction.UP;
                break;
            case KeyEvent.VK_DOWN:
                newDirection = Direction.DOWN;
                break;
            case KeyEvent.VK_LEFT:
                newDirection = Direction.LEFT;
                break;
            case KeyEvent.VK_RIGHT:
                newDirection = Direction.RIGHT;
                break;
        }

        if (newDirection != null) {
            gameEngine.changeDirection(newDirection);
        }
    }
}

// ========== APPLICATION LAUNCHER ==========

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