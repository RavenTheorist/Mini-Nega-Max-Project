package mini.nega.max.project.Visualization;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * Content Panel Class
 * 
 * @author Amine Elkhalsi <aminekhalsi@hotmail.com>
 */
public class Panel extends JPanel
{
    /*
     * Panel Attributes
     */
    
    JButton button;
    private Image img;
    
    
    
    /*
     * Constructors
     */
    
    public Panel()
    {
        try
        {
            img = ImageIO.read(new File("back.png"));
        }
        catch (IOException e){}
    }
    
    
    
    /*
     * @Overrides
     */
    
    // This is the paintComponent method that will be called each time a modification occurs in the panel
    @Override
    public void paintComponent(Graphics g)
    {
        
        Graphics2D g2d = (Graphics2D)g;
        
        
        
        g2d.drawImage(img, 0, 0, this.getWidth(), this.getHeight(), this);
    }
}
