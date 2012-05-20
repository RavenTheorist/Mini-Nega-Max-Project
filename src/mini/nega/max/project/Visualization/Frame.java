package mini.nega.max.project.Visualization;

import java.awt.Color;
import javax.swing.JFrame;

/**
 * Frame Class Implementation
 * 
 * @author Amine Elkhalsi <aminekhalsi@hotmail.com>
 */

public class Frame extends JFrame
{
    /*
     * Frame Attributes
     */
    
    // Content panel of the frame
    private Panel panel;
    
    /*
     * Constructors
     */
    
    public Frame()
    {
        // Set frame parameters
        this.panel = new Panel(3);
        this.setSize(300, 300);
        this.setAlwaysOnTop(true);
        this.setBackground(Color.black);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Set panel parameters
        this.setContentPane(panel);
        
        // Screen title
        this.setTitle("Mini-Nega Max");
        
        // Set the frame visible
        this.setVisible(true);
        
        this.setFocusable(false);
    }
}