import java.awt.*;
import java.security.SecureRandom;
import java.util.ArrayList;

public class SnakeGameLogic {

    public static final int WINDOW_WIDTH = 800;
    public static final int WINDOW_HEIGHT = 600;
    public static final int CELL_SIZE = 20;
    public static final int HEADER_HEIGHT = 50;

    private ArrayList<Point> snakeSegments = new ArrayList<>();
    private Point foodPosition;
    private int direction = 0;
    private boolean gameOver = false;
    private int score = 0;
    private SecureRandom random = new SecureRandom();

    public SnakeGameLogic() {
        initializeSnake();
        generateFood();
    }

    public void initializeSnake() {
        snakeSegments.clear();
        int centerX = WINDOW_WIDTH / 2;
        int centerY = WINDOW_HEIGHT / 2;

        snakeSegments.add(new Point(centerX, centerY));
        snakeSegments.add(new Point(centerX - CELL_SIZE, centerY));
        snakeSegments.add(new Point(centerX - 2 * CELL_SIZE, centerY));
    }

    public void update() {
        if (gameOver) return;

        Point head = new Point(snakeSegments.get(0));

        switch (direction) {
            case 0 -> head.y -= CELL_SIZE;
            case 1 -> head.x += CELL_SIZE;
            case 2 -> head.y += CELL_SIZE;
            case 3 -> head.x -= CELL_SIZE;
        }

        if (head.x < 0 || head.x >= WINDOW_WIDTH || head.y < HEADER_HEIGHT || head.y >= WINDOW_HEIGHT) {
            gameOver = true;
            return;
        }

        for (Point segment : snakeSegments) {
            if (head.equals(segment)) {
                gameOver = true;
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

    public void generateFood() {
        boolean validPosition = false;

        while (!validPosition) {
            int maxX = (WINDOW_WIDTH - CELL_SIZE) / CELL_SIZE;
            int maxY = (WINDOW_HEIGHT - HEADER_HEIGHT - CELL_SIZE) / CELL_SIZE;

            int foodX = random.nextInt(maxX) * CELL_SIZE;
            int foodY = random.nextInt(maxY) * CELL_SIZE + HEADER_HEIGHT;

            Point newFood = new Point(foodX, foodY);
            validPosition = !snakeSegments.contains(newFood);

            if (validPosition) {
                foodPosition = newFood;
            }
        }
    }

    public void setDirection(int d) {
        this.direction = d;
    }

    public void setFoodPosition(Point p) {
        this.foodPosition = p;
    }

    public void setSnakeSegments(ArrayList<Point> segments) {
        this.snakeSegments = new ArrayList<>(segments);
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public int getScore() {
        return score;
    }

    public ArrayList<Point> getSnakeSegments() {
        return snakeSegments;
    }

    public Point getFoodPosition() {
        return foodPosition;
    }

}
