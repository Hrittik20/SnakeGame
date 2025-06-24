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
    private Direction direction = Direction.UP;
    private Timer gameTimer;
    private boolean gameOver = false;
    private int score = 0;
    private SecureRandom random = new SecureRandom();
    private GameRenderer renderer = new GameRenderer(); //

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

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        renderer.render(g, gameOver, score, snakeSegments, foodPosition,
                WINDOW_WIDTH, WINDOW_HEIGHT, CELL_SIZE);
    }

    public void actionPerformed(ActionEvent e) {
        if (!gameOver) {
            Point head = getNextHeadPosition();

            if (checkWallCollision(head) || checkSelfCollision(head)) {
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
        head.translate(direction.dx * CELL_SIZE, direction.dy * CELL_SIZE);
        return head;
    }

    private boolean checkWallCollision(Point head) {
        return isOutOfBounds(head);
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
        return p.x < 0 || p.x >= WINDOW_WIDTH || p.y < HEADER_HEIGHT || p.y >= WINDOW_HEIGHT;
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
        UP(0, -1),
        RIGHT(1, 0),
        DOWN(0, 1),
        LEFT(-1, 0);

        final int dx, dy;

        Direction(int dx, int dy) {
            this.dx = dx;
            this.dy = dy;
        }
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