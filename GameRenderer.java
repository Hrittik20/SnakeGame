import java.awt.*;

public class GameRenderer implements Renderer {

    @Override
    public void render(Graphics g, SnakeGame game) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, GameConfig.WINDOW_WIDTH, GameConfig.WINDOW_HEIGHT);

        if (game.isGameOver()) {
            renderGameOver(g, game);
        } else if (game.isPaused()) {
            renderPaused(g);
        } else {
            renderGame(g, game);
        }
    }

    private void renderGame(Graphics g, SnakeGame game) {
        for (int i = 0; i < game.getSnakeSegments().size(); i++) {
            g.setColor(i == 0 ? Color.GREEN : Color.PINK);
            Point p = game.getSnakeSegments().get(i);
            g.fillRect(p.x, p.y, GameConfig.CELL_SIZE, GameConfig.CELL_SIZE);
        }

        g.setColor(Color.RED);
        Point food = game.getFoodPosition();
        g.fillRect(food.x, food.y, GameConfig.CELL_SIZE, GameConfig.CELL_SIZE);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Score: " + game.getScore(), 10, 50);
    }

    private void renderGameOver(Graphics g, SnakeGame game) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString("Game Over! Score: " + game.getScore(), GameConfig.WINDOW_WIDTH / 2 - 120, GameConfig.WINDOW_HEIGHT / 2);
        g.drawString("Press R to restart", GameConfig.WINDOW_WIDTH / 2 - 80, GameConfig.WINDOW_HEIGHT / 2 + 30);
    }

    private void renderPaused(Graphics g) {
        g.setColor(Color.YELLOW);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString("Game Paused", GameConfig.WINDOW_WIDTH / 2 - 80, GameConfig.WINDOW_HEIGHT / 2);
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        g.drawString("Press P to resume", GameConfig.WINDOW_WIDTH / 2 - 60, GameConfig.WINDOW_HEIGHT / 2 + 30);
    }
}
