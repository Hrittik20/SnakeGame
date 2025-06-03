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

    private ArrayList<Point> s = new ArrayList<>();
    private Point f;
    private int d = 0; // 0=up, 1=right, 2=down, 3=left
    private Timer t;
    private boolean gameOver = false;
    private int score = 0;
    private SecureRandom r = new SecureRandom();


    public SnakeGame() {
        this.setTitle("Snake Game");
        this.setSize(width, height);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.addKeyListener(this);
        this.setFocusable(true);

        s.add(new Point(400, 300));
        s.add(new Point(380, 300));
        s.add(new Point(360, 300));

        generateFood();

        t = new Timer(100, this);
        t.start();

        this.setVisible(true);
    }

    public void paint(Graphics g) {
        super.paint(g);

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, width, height);

        if (!gameOver) {
            // Draw snake
            for (int i = 0; i < s.size(); i++) {
                if (i == 0) {
                    g.setColor(Color.GREEN);
                } else {
                    g.setColor(Color.PINK);
                }
                g.fillRect(s.get(i).x, s.get(i).y, 20, 20);
            }

            // Draw food
            g.setColor(Color.RED);
            g.fillRect(f.x, f.y, 20, 20);

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
            Point head = new Point(s.get(0));

            switch (d) {
                case 0: head.y -= 20; break; // up
                case 1: head.x += 20; break; // right
                case 2: head.y += 20; break; // down
                case 3: head.x -= 20; break; // left
            }

            if (head.x < 0 || head.x >= width || head.y < 50 || head.y >= height) {
                gameOver = true;
                t.stop();
                return;
            }

            for (int i = 0; i < s.size(); i++) {
                if (head.equals(s.get(i))) {
                    gameOver = true;
                    t.stop();
                    return;
                }
            }

            s.add(0, head);

            if (head.x == f.x && head.y == f.y) {
                score += 10;
                generateFood();
            } else {
                s.remove(s.size() - 1);
            }
        }
        repaint();
    }

    private void generateFood() {
        boolean validPosition = false;

        while (!validPosition) {
            f = new Point((r.nextInt((width-20)/20))*20, (r.nextInt((height-70)/20))*20 + 50);

            validPosition = true;
            for (Point segment : s) {
                if (segment.equals(f)) {
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
                s.clear();
                s.add(new Point(400, 300));
                s.add(new Point(380, 300));
                s.add(new Point(360, 300));
                d = 0;
                generateFood();
                t.restart();
            }
        } else {
            if (key == KeyEvent.VK_UP && d != 2) {
                d = 0;
            } else if (key == KeyEvent.VK_RIGHT && d != 3) {
                d = 1;
            } else if (key == KeyEvent.VK_DOWN && d != 0) {
                d = 2;
            } else if (key == KeyEvent.VK_LEFT && d != 1) {
                d = 3;
            }
        }
    }

    public void keyReleased(KeyEvent e) {}
    public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        new SnakeGame();
    }
}