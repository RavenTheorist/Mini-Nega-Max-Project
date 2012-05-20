package mini.nega.max.project.Visualization;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 * Content Panel Class Implementation
 * 
 * @author Amine Elkhalsi <aminekhalsi@hotmail.com>
 */
public class Panel extends JPanel implements KeyListener
{
    /*
     * Panel Attributes
     */
    
    // m is the dimension of the board
    private int m;
    
    // Every button represents a square
    private ArrayList<Square> squares;
    private Image img;
    
    
    
    /*
     * Constructors
     */
    
    public Panel(int m)
    {
        // Panel initialization
        this.m = m;
        this.addKeyListener(this);
        
        // Open an image to put it as a background image
        try
        {
            img = ImageIO.read(new File("back.png"));
        }
        catch (IOException e){}
        
        // Set a grid layout to Panel
        this.setLayout(new GridLayout(this.m, this.m));
        
        // Draw the main grid
        // Add all squares to Panel
        this.squares = new ArrayList<>();
        for (int i = 0 ; i < sqr(this.m) ; i++)
        {
            Square square = new Square();
            this.add(square);
            this.squares.add(square);
        }
    }
    
    
    
    /*
     * @Overrides
     */
    
    // This is the paintComponent method that will be called each time a modification occurs in the panel
    @Override
    public void paintComponent(Graphics g)
    {
        // We need a Graphics2D
        Graphics2D g2d = (Graphics2D)g;
        
        // Set the background image
        g2d.drawImage(img, 0, 0, this.getWidth(), this.getHeight(), this);
    }

    
    /*
     * Internal Methods
     */
    
    private int sqr(int m)
    {
        return (m * m);
    }
    
    
    
    /*
     * Getters and Setters
     */
    
    public int getM()
    {
        return m;
    }

    public void setM(int m)
    {
        this.m = m;
    }

    public ArrayList<Square> getSquares()
    {
        return squares;
    }

    public void setSquares(ArrayList<Square> squares)
    {
        this.squares = squares;
    }

    @Override
    public void keyTyped(KeyEvent e)
    {
        
    }

    @Override
    public void keyPressed(KeyEvent e)
    {
        if (e.getKeyCode() == KeyEvent.VK_UP)
        {
            System.out.println("salut!");
            this.squares.get(2).setState("nought");
            this.repaint();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        
    }
}
