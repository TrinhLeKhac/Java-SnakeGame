import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Random;

// Includes everything in SnakeGame(JPanel)
// JPanel is a sub-class of JComponent
public class SnakeGame extends JPanel implements ActionListener, KeyListener {

    // *********************************** attribute of class *********************************************
    public static class Tile {
        int x;
        int y;
        Tile(int x, int y) {
            // Position of tile
            this.x = x;
            this.y = y;
        }
    }

    int boardWidth;
    int boardHeight;
    int tileSize = 25;

    Tile snakeHead;
    ArrayList<Tile> snakeBody;
    Tile food;
    Random random;

    // game Logic
    Timer gameLoop;
    int velocityX;
    int velocityY;

    boolean gameOver;

    // *********************************** end of attribute of class *********************************************


    // *********************************** constructor *********************************************
    public SnakeGame(int boardWidth, int boardHeight) {
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
        this.setPreferredSize(new Dimension(this.boardWidth, this.boardHeight));
        this.setBackground(Color.BLACK);
        this.addKeyListener(this);
        this.setFocusable(true);

        snakeHead = new Tile(5, 5);
        snakeBody = new ArrayList<>();

        random = new Random();
        food = new Tile(10, 10);
        placeFood();

        velocityX = 0;
        velocityY = 1;

        gameLoop = new Timer(100, this);
        gameLoop.start();

        gameOver = false;
    }

    // *********************************** end of constructor *********************************************

    // *********************************** paint component*********************************************
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
//        // Draw Grid
//        for (int i = 0; i < boardWidth/tileSize; i++) {
//            g.setColor(Color.WHITE);
//            // Draw vertical line connected 2 points [P1(i*tileSize, 0), P2(i*tileSize, boardHeight)]
//            g.drawLine(i * tileSize, 0, i * tileSize, boardHeight);
//
//            // Draw horizontal line connected 2 points [P3(0, i*tileSize), P4(boardWidth, i*tileSize)]
//            g.drawLine(0, i * tileSize, boardWidth, i * tileSize);
//        }

        // Draw Snake Head
        g.setColor(Color.GREEN);
//        g.fillRect(snakeHead.x * tileSize, snakeHead.y * tileSize, tileSize, tileSize);
        g.fill3DRect(snakeHead.x * tileSize, snakeHead.y * tileSize, tileSize, tileSize, true);

        // Draw Snake Body
        g.setColor(Color.GREEN);
        Tile bodyPart;
        for (int i = 0; i < snakeBody.size(); i++) {
            bodyPart = snakeBody.get(i);
//            g.fillRect(bodyPart.x * tileSize, bodyPart.y * tileSize, tileSize, tileSize);
            g.fill3DRect(bodyPart.x * tileSize, bodyPart.y * tileSize, tileSize, tileSize, true);
        }

        // Draw Food
        g.setColor(Color.RED);
//        g.fillRect(food.x * tileSize, food.y*tileSize, tileSize, tileSize);
        g.fill3DRect(food.x * tileSize, food.y*tileSize, tileSize, tileSize, true);

        // Draw score
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        if (gameOver) {
            g.setColor(Color.RED);
            g.drawString("Game over: " + snakeBody.size(), tileSize - 16, tileSize);
        } else {
            g.setColor(Color.GREEN);
            g.drawString("Score: " + snakeBody.size(), tileSize - 16, tileSize);
        }
    }

    // *********************************** end of paint component *********************************************

    // *********************************** support methods *********************************************
    public void placeFood() {
        food.x = random.nextInt(boardWidth/tileSize);
        food.y = random.nextInt(boardHeight/tileSize);
    }

    public boolean collision(Tile tile1, Tile tile2) {
        return tile1.x == tile2.x && tile1.y == tile2.y;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_UP && velocityY != 1) {
            velocityX = 0;
            velocityY = -1;
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN && velocityY != -1) {
            velocityX = 0;
            velocityY = 1;
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT && velocityX != 1) {
            velocityX = -1;
            velocityY = 0;
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT && velocityX != -1) {
            velocityX = 1;
            velocityY = 0;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    // *********************************** end of support methods *********************************************

    // *********************************** main method (logic of Snake Game) *********************************************
    public void move() {
        // Eat food
        if (collision(snakeHead, food)) {
            snakeBody.add(new Tile(food.x, food.y));
            placeFood();
        }

        // Snake Body
        for (int i = snakeBody.size() - 1; i >= 0; i--) {
            if (i == 0) {
                snakeBody.getFirst().x = snakeHead.x;
                snakeBody.getFirst().y = snakeHead.y;
            } else {
                snakeBody.get(i).x = snakeBody.get(i - 1).x;
                snakeBody.get(i).y = snakeBody.get(i - 1).y;
            }
        }

        // Snake Head
        snakeHead.x += velocityX;
        snakeHead.y += velocityY;

        // Game over
        // Collide with boundary
        if (
            snakeHead.x * tileSize < 0
            || snakeHead.x * tileSize == boardWidth
            || snakeHead.y * tileSize < 0
            || snakeHead.y * tileSize == boardHeight
        ) {
            gameOver = true;
        }

        // Snake head collide with snake body
        for (int i = 0; i < snakeBody.size(); i++) {
            if (collision(snakeHead, snakeBody.get(i))) {
                gameOver = true;
            }
        }

    }

    // *********************************** end of main method (logic of Snake Game) *********************************************

    // *********************************** re-render by interval (loop game) *********************************************
    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();  // repaint assign with method draw(Graphics g)
        if (gameOver) {
            gameLoop.stop();
        }
    }

    // *********************************** end of re-render *********************************************
}
