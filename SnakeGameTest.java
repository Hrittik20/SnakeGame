import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.awt.*;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

public class SnakeGameTest {


    private SnakeGameLogic game;

    @BeforeEach
    public void setUp() {
        game = new SnakeGameLogic();
    }

    @Test
    public void testSnakeHitsWallEndsGame() {
        ArrayList<Point> snake = new ArrayList<>();
        snake.add(new Point(0, SnakeGameLogic.HEADER_HEIGHT));
        game.setSnakeSegments(snake);
        game.setDirection(3);
        game.update();
        assertTrue(game.isGameOver());
    }

    @Test
    public void testSnakeEatsFoodAndGrows() {
        ArrayList<Point> snake = new ArrayList<>();
        snake.add(new Point(100, 100));
        game.setSnakeSegments(snake);
        game.setFoodPosition(new Point(100, 80));
        game.setDirection(0);

        int oldSize = game.getSnakeSegments().size();
        int oldScore = game.getScore();

        game.update();

        assertEquals(oldSize + 1, game.getSnakeSegments().size());
        assertEquals(oldScore + 10, game.getScore());
    }

    @Test
    public void testSnakeMovesForwardWithoutEating() {
        ArrayList<Point> snake = new ArrayList<>();
        snake.add(new Point(100, 100));
        game.setSnakeSegments(snake);
        game.setFoodPosition(new Point(300, 300));
        game.setDirection(0);

        int oldSize = game.getSnakeSegments().size();
        game.update();

        assertEquals(oldSize, game.getSnakeSegments().size());
    }

    @Test
    public void testSnakeCollidesWithItself() {
        ArrayList<Point> snake = new ArrayList<>();

        snake.add(new Point(100, 100));
        snake.add(new Point(100, 120));
        snake.add(new Point(120, 120));
        snake.add(new Point(120, 100));
        game.setSnakeSegments(snake);
        game.setDirection(1);
        game.update();
        assertTrue(game.isGameOver());
    }

    @Test
    public void testFoodIsNotOnSnake() {
        for (int i = 0; i < 50; i++) {
            game.generateFood();
            assertFalse(game.getSnakeSegments().contains(game.getFoodPosition()));
        }
    }

    @Test
    public void testSnakeEatsFoodAtTopEdge() {
        ArrayList<Point> snake = new ArrayList<>();
        snake.add(new Point(100, SnakeGameLogic.HEADER_HEIGHT + 20));
        game.setSnakeSegments(snake);
        game.setFoodPosition(new Point(100, SnakeGameLogic.HEADER_HEIGHT));
        game.setDirection(0); // up

        game.update();

        assertEquals(2, game.getSnakeSegments().size());
        assertEquals(10, game.getScore());
    }

    @Test
    public void testInitialSnakeLengthIsThree() {
        ArrayList<Point> snake = game.getSnakeSegments();
        assertEquals(3, snake.size());
    }

    @Test
    public void testGameIsNotOverOnStart() {
        assertFalse(game.isGameOver());
    }

}
