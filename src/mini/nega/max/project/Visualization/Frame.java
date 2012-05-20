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
    
    // True if the player already played its turn, else false
    private boolean played;
    
    /*
     * Constructors
     */
    
    public Frame()
    {
        // Set frame parameters
        this.played = false;
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
        
        // Call Main Loop
        loop();
    }
    
    
    
    /*
     * Internal Methods
     */
    
    // Frame Main Loop
    private void loop()
    {
        while(isHumanTurn())
        {
            
        }
        play(1, 1);
        
    }
    
    // This method plays a nought at the indicated line and column
    private void play(int line, int column)
    {
        Square squares[][] = this.panel.getSquares();
        squares[line][column].setState("nought");
        this.panel.setSquares(squares);
        this.panel.repaint();
    }
    
    // This method returns the number of crosses X played in the game
    private int countXInGame()
    {
        int counter = 0;
        Square squares[][] = this.panel.getSquares();
        
        // For each line
        for (int l = 0; l < this.panel.getM(); l++)
        {
            // And for each column
            for (int c = 0; c < this.panel.getM(); c++)
            {
                // If you find a cross, then increment the counter
                if (squares[l][c].getState().equals("cross"))
                    counter++;
            }
        }
        
        return counter;
    }
    
    // This method say if it's the human's turn or not
    private boolean isHumanTurn()
    {
        // Count how many times the human played
        int xPlayed = countXInGame();
        
        // If it's an even number, then it means that it's still the human's turn
        if (xPlayed % 2 == 0)
            return true;
        // Odd number means it's computer's turn
        else
            return false;
    }
    
    
    /*
     * Getters and Setters
     */
    
    public boolean isPlayed()
    {
        return played;
    }

    public void setPlayed(boolean played)
    {
        this.played = played;
    }
}