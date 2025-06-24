import java.awt.*;
import java.util.List;

public class GameRenderer {

    private static final Font SCORE_FONT = new Font("Arial", Font.BOLD, 16);
    private static final Font GAME_OVER_FONT = new Font("Arial", Font.BOLD, 24);

    public void render(Graphics g, boolean gameOver, int score, List<Point> snakeSegments, Point foodPosition,
                       int windowWidth, int windowHeight, int cellSize) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, windowWidth, windowHeight);

        if (!gameOver) {
            renderSnake(g, snakeSegments, cellSize);
            renderFood(g, foodPosition, cellSize);
            renderScore(g, score);
        } else {
            renderGameOver(g, score, windowWidth, windowHeight);
        }
    }

    private void renderSnake(Graphics g, List<Point> snake, int size) {
        for (int i = 0; i < snake.size(); i++) {
            g.setColor(i == 0 ? Color.GREEN : Color.PINK);
            Point p = snake.get(i);
            g.fillRect(p.x, p.y, size, size);
        }
    }

    private void renderFood(Graphics g, Point food, int size) {
        g.setColor(Color.RED);
        g.fillRect(food.x, food.y, size, size);
    }

    private void renderScore(Graphics g, int score) {
        g.setColor(Color.WHITE);
        g.setFont(SCORE_FONT);
        g.drawString("Score: " + score, 10, 50);
    }

    private void renderGameOver(Graphics g, int score, int width, int height) {
        g.setColor(Color.WHITE);
        g.setFont(GAME_OVER_FONT);
        g.drawString("Game Over! Score: " + score, width / 2 - 120, height / 2);
        g.drawString("Press R to restart", width / 2 - 80, height / 2 + 30);
    }
}
