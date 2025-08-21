import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class FloppyBird extends JPanel implements ActionListener, KeyListener {
    private final int WIDTH = 800, HEIGHT = 600;
    private final int BIRD_SIZE = 20;
    private final int PIPE_WIDTH = 60;
    private final int PIPE_GAP = 150;

    private Timer timer;
    private int birdY = HEIGHT / 2;
    private int velocity = 0;
    private int gravity = 1;
    private boolean gameOver = false;
    private int score = 0;

    private ArrayList<Rectangle> pipes;
    private Random rand;

    public FloppyBird() {
        JFrame frame = new JFrame("Floppy Bird");
        frame.setSize(WIDTH, HEIGHT);
        frame.add(this);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addKeyListener(this);
        frame.setResizable(false);
        frame.setVisible(true);

        rand = new Random();
        pipes = new ArrayList<>();

        timer = new Timer(20, this); // 50 FPS
        timer.start();

        addPipe(true);
        addPipe(true);
    }

    public void addPipe(boolean start) {
        int height = 50 + rand.nextInt(300);
        int x = start ? WIDTH + pipes.size() * 300 : pipes.get(pipes.size() - 1).x + 300;
        pipes.add(new Rectangle(x, 0, PIPE_WIDTH, height));
        pipes.add(new Rectangle(x, height + PIPE_GAP, PIPE_WIDTH, HEIGHT - height - PIPE_GAP));
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        setBackground(Color.cyan);

        // Draw bird
        g.setColor(Color.red);
        g.fillRect(100, birdY, BIRD_SIZE, BIRD_SIZE);

        // Draw pipes
        g.setColor(Color.green.darker());
        for (Rectangle pipe : pipes) {
            g.fillRect(pipe.x, pipe.y, pipe.width, pipe.height);
        }

        // Draw ground
        g.setColor(Color.green);
        g.fillRect(0, HEIGHT - 100, WIDTH, 100);
        g.setColor(Color.green);
        g.fillRect(0, HEIGHT - 100, WIDTH, 20);

        // Draw score
        g.setColor(Color.Black);
        g.setFont(new Font("Arial", Font.BOLD, 32));
        g.drawString("Score: " + score, 20, 40);

        // Game over message
        if (gameOver) {
            g.setColor(Color.black);
            g.setFont(new Font("Arial", Font.BOLD, 64));
            g.drawString("Game Over", WIDTH / 2 - 180, HEIGHT / 2 - 20);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int speed = 10;

        if (!gameOver) {
            velocity += gravity;
            birdY += velocity;

            ArrayList<Rectangle> toRemove = new ArrayList<>();

            for (Rectangle pipe : pipes) {
                pipe.x -= speed;
                if (pipe.x + PIPE_WIDTH < 0) {
                    toRemove.add(pipe);
                }

                // Check collision
                if (pipe.intersects(new Rectangle(100, birdY, BIRD_SIZE, BIRD_SIZE))) {
                    gameOver = true;
                }
            }

            pipes.removeAll(toRemove);

            if (pipes.size() < 6) {
                addPipe(false);
            }

            // Bird hits ground or top
            if (birdY > HEIGHT - 100 - BIRD_SIZE || birdY < 0) {
                gameOver = true;
            }

            // Increase score
            for (Rectangle pipe : pipes) {
                if (pipe.y == 0 && pipe.x + PIPE_WIDTH == 100) {
                    score++;
                }
            }
        }

        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (gameOver) {
            // Restart game
            birdY = HEIGHT / 2;
            velocity = 0;
            score = 0;
            pipes.clear();
            addPipe(true);
            addPipe(true);
            gameOver = false;
        }

        if (e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_UP) {
            if (velocity > 0) velocity = 0;
            velocity -= 10;
        }
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        new FloppyBird();
    }
}
