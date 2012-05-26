package mini.nega.max.project.Visualization;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JButton;

/**
 * Square Class Implementation
 * 
 * @author Amine Elkhalsi <aminekhalsi@hotmail.com>
 */
public class Square extends JButton implements MouseListener
{
    /*
     * Attributes
     */
    
    // The state of the square, can be one of {"", "nought", "cross"}
    private String state;
    private Image img;
    
    
    
    /*
     * Constructors
     */
    
    public Square()
    {
        this.state = "";
        
        // Open an image to put it as a background image
        try
        {
            img = ImageIO.read(new File("back.png"));
        }
        catch (IOException e){}
        
        // Add the mouse listener
        this.addMouseListener(this);
    }
    
    public Square(String state)
    {
        this.state = state;
        
        // Open an image to put it as a background image
        try
        {
            img = ImageIO.read(new File("back.png"));
        }
        catch (IOException e){}
        
        // Add the mouse listener
        this.addMouseListener(this);
    }
    
    
    
    /*
     * @Overrides
     */
    
    @Override
    public void paintComponent(Graphics g)
    {
        // Draw the square
        Graphics2D g2d = (Graphics2D)g;
        g2d.drawRect(0, 0, this.getWidth(), this.getHeight());
        g2d.setColor(Color.black);
        
        // Draw it's content depending on its state
        switch (this.state)
        {
            case "nought" :
                g2d.setColor(Color.black);
                g2d.drawOval(5, 5, this.getWidth()-10, this.getHeight()-10);
                break;
            case "cross" :
                g2d.setColor(Color.black);
                g2d.drawLine(0, 0, this.getWidth(), this.getHeight());
                g2d.drawLine(this.getWidth(), 0, 0, this.getHeight());
                break;
            case "" :
                g2d.drawImage(img, 0, 0, this.getWidth(), this.getHeight(), this);
                break;
        }
    }
    
    
    
    /*
     * @Implementation Overrides
     */
    
    @Override
    public void mouseClicked(MouseEvent e)
    {
        if (!((this.state.equals("cross")) || (this.state.equals("nought"))))
            this.state = "cross";
    }

    @Override
    public void mousePressed(MouseEvent e)
    {
        
    }

    @Override
    public void mouseReleased(MouseEvent e)
    {
        
    }

    @Override
    public void mouseEntered(MouseEvent e)
    {
        repaint();
    }

    @Override
    public void mouseExited(MouseEvent e)
    {
        repaint();
    }
    
    /*
     * Getters and Setters
     */
    
    public String getState()
    {
        return state;
    }

    public void setState(String state)
    {
        this.state = state;
    }
}