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
        // First test, when player plays, computer will play at the central square
        while(true)
        {
            if (!isHumanTurn())
            {
                Square tempSquares[][] = playNext(this.panel.getSquares());
                if (tempSquares != null)
                    this.panel.setSquares(tempSquares);
            }
            
            SimpleDialog dlg = null;
            switch (f(this.panel.getSquares()))
            {
                case 0 :
                    dlg = new SimpleDialog(this, "And the winner is...", "Draw !");
                    resetGame();
                    break;
                case 1 :
                    dlg = new SimpleDialog(this, "And the winner is...", "Computer won !");
                    resetGame();
                    break;
                case -1 :
                    dlg = new SimpleDialog(this, "And the winner is...", "You won !!!");
                    resetGame();
                    break;
            }
        }
    }
    
    // This method plays a nought at the indicated line and column
    private void play(int line, int column)
    {
        Square squares[][] = this.panel.getSquares();
        squares[line][column].setState("nought");
        this.panel.setSquares(squares);
        this.panel.repaint();
    }
    
    // This method plays a nought in the next empty square
    private Square[][] playNext(Square[][] node)
    {
        Square squares[][] = node;
        
        // For each line
        for (int l = 0; l < this.panel.getM(); l++)
        {
            // And for each sqaure in each line
            for (int c = 0; c < this.panel.getM(); c++)
            {
                // If it's an empty square
                if (squares[l][c].getState().equals(""))
                {
                    squares[l][c].setState("nought");
                    this.panel.setSquares(squares);
                    this.panel.repaint();
                    return squares;
                }
            }
        }
        
        // There is no empty space !
        return null;
    }
    
    // This game reinitilizes the game to the starting point
    private void resetGame()
    {
        Square squares[][] = this.panel.getSquares();
        // For each line
        for (int l = 0; l < this.panel.getM(); l++)
        {
            // And for each sqaure in each line
            for (int c = 0; c < this.panel.getM(); c++)
            {
                // Make it empty
                squares[l][c].setState("");
            }
        }
        
        this.panel.setSquares(squares);
    }
    
    // Return true if the reached state corresponds to a leaf
    private boolean isLeaf(Square[][] node)
    {
        Square squares[][] = node;
        
        /* The actual game is a leaf if... */
        
        /* ...there is a complete LINE, COLUMN or DIAGONAL filled with the same type of chip */
        
        // counter of crosses in a same diagonal (from Top Left to Bottom Right)
        int diagonalCrossesCounter = 0;
        // counter of noughts in a same diagonal
        int diagonalNoughtsCounter = 0;
        for (int l = 0; l < this.panel.getM(); l++)
        {
            // For each line, we count the number of crosses
            int horizontalCrossesCounter = 0;
            // ...and the number of noughts
            int horizontalNoughtsCounter = 0;
            
            // We do the same for columns
            int verticalCrossesCounter = 0;
            // ...and the number of noughts
            int verticalNoughtsCounter = 0;
            
            
            
            for (int c = 0; c < this.panel.getM(); c++)
            {
                // If the square contains a cross
                if (squares[l][c].getState().equals("cross"))
                    horizontalCrossesCounter++;
                // Or if it contains a nought
                if (squares[l][c].getState().equals("nought"))
                    horizontalNoughtsCounter++;
                
                // We then do the same for columns
                if (squares[c][l].getState().equals("cross"))
                    verticalCrossesCounter++;
                if (squares[c][l].getState().equals("nought"))
                    verticalNoughtsCounter++;
                
                // ... same thing for diagonals (from TOP LEFT to BOTTOM RIGHT)
                if (l == c)
                {
                    if (squares[l][c].getState().equals("cross"))
                        diagonalCrossesCounter++;
                    if (squares[l][c].getState().equals("nought"))
                        diagonalNoughtsCounter++;
                }
            }
            
            // It's a leaf, if there is a line all filled with crosses
            if (horizontalCrossesCounter == this.panel.getM())
                return true;
            // It's a leaf, if there is a line all filled with noughts
            if (horizontalNoughtsCounter == this.panel.getM())
                return true;
            // It's a leaf, if there is a column all filled with crosses
            if (verticalCrossesCounter == this.panel.getM())
                return true;
            // It's a leaf, if there is a column all filled with crosses
            if (verticalNoughtsCounter == this.panel.getM())
                return true;
            // It's a leaf, if there is a diagonal line all filled with crosses
            if (diagonalCrossesCounter == this.panel.getM())
                return true;
            // It's a leaf, if there is a diagonal line all filled with noughts
            if (diagonalNoughtsCounter == this.panel.getM())
                return true;
        }
        
        /* ...or if there is an opposite diagonal (from UP RIGHT to BOTTOM LEFT) */
        // counter of crosses in a same diagonal
        diagonalCrossesCounter = 0;
        // counter of noughts in a same diagonal
        diagonalNoughtsCounter = 0;
        
        for (int i = 0 ; i < this.panel.getM() ; i++)
        {
            if (squares[i][this.panel.getM() - i - 1].getState().equals("cross"))
                diagonalCrossesCounter++;
            if (squares[i][this.panel.getM() - i - 1].getState().equals("nought"))
                diagonalNoughtsCounter++;    
        }
        
        // It's a leaf, if there is a diagonal line all filled with crosses
        if (diagonalCrossesCounter == this.panel.getM())
            return true;
        // It's a leaf, if there is a diagonal line all filled with noughts
        if (diagonalNoughtsCounter == this.panel.getM())
            return true;
        
        /* ...or finally if it's a draw ! => All the grid is filled */
        // Count the number of unfilled squares
        int unfilledCounter = 0;
        for (int l = 0; l < this.panel.getM(); l++)
        {
            for (int c = 0; c < this.panel.getM(); c++)
            {
                // If there is a remaining empty square
                if (squares[l][c].getState().equals(""))
                    // The game still goes on => not a leaf
                    unfilledCounter++;
            }
        }
        
        // It's a draw !
        if (unfilledCounter == this.panel.getM())
            return true;
        
        // Else, it means it's NOT a leaf
        return false;
    }
    
    // Return true if the reached state corresponds to a leaf
    private int f(Square[][] node)
    {
        Square squares[][] = node;
        
        /* Calculates f(node) when the actual game is a leaf if... */
        
        /* 
         1 : Computer wins
        -1 : Human wins
         0 : Draw
         */
        
        /* ...there is a complete LINE, COLUMN or DIAGONAL filled with the same type of chip */
        
        // counter of crosses in a same diagonal (from Top Left to Bottom Right)
        int diagonalCrossesCounter = 0;
        // counter of noughts in a same diagonal
        int diagonalNoughtsCounter = 0;
        for (int l = 0; l < this.panel.getM(); l++)
        {
            // For each line, we count the number of crosses
            int horizontalCrossesCounter = 0;
            // ...and the number of noughts
            int horizontalNoughtsCounter = 0;
            
            // We do the same for columns
            int verticalCrossesCounter = 0;
            // ...and the number of noughts
            int verticalNoughtsCounter = 0;
            
            
            
            for (int c = 0; c < this.panel.getM(); c++)
            {
                // If the square contains a cross
                if (squares[l][c].getState().equals("cross"))
                    horizontalCrossesCounter++;
                // Or if it contains a nought
                if (squares[l][c].getState().equals("nought"))
                    horizontalNoughtsCounter++;
                
                // We then do the same for columns
                if (squares[c][l].getState().equals("cross"))
                    verticalCrossesCounter++;
                if (squares[c][l].getState().equals("nought"))
                    verticalNoughtsCounter++;
                
                // ... same thing for diagonals (from TOP LEFT to BOTTOM RIGHT)
                if (l == c)
                {
                    if (squares[l][c].getState().equals("cross"))
                        diagonalCrossesCounter++;
                    if (squares[l][c].getState().equals("nought"))
                        diagonalNoughtsCounter++;
                }
            }
            
            // It's a leaf, if there is a line all filled with crosses
            if (horizontalCrossesCounter == this.panel.getM())
                return -1;
            // It's a leaf, if there is a line all filled with noughts
            if (horizontalNoughtsCounter == this.panel.getM())
                return 1;
            // It's a leaf, if there is a column all filled with crosses
            if (verticalCrossesCounter == this.panel.getM())
                return -1;
            // It's a leaf, if there is a column all filled with crosses
            if (verticalNoughtsCounter == this.panel.getM())
                return 1;
            // It's a leaf, if there is a diagonal line all filled with crosses
            if (diagonalCrossesCounter == this.panel.getM())
                return -1;
            // It's a leaf, if there is a diagonal line all filled with noughts
            if (diagonalNoughtsCounter == this.panel.getM())
                return 1;
        }
        
        /* ...or if there is an opposite diagonal (from UP RIGHT to BOTTOM LEFT) */
        // counter of crosses in a same diagonal
        diagonalCrossesCounter = 0;
        // counter of noughts in a same diagonal
        diagonalNoughtsCounter = 0;
        
        for (int i = 0 ; i < this.panel.getM() ; i++)
        {
            if (squares[i][this.panel.getM() - i - 1].getState().equals("cross"))
                diagonalCrossesCounter++;
            if (squares[i][this.panel.getM() - i - 1].getState().equals("nought"))
                diagonalNoughtsCounter++;    
        }
        
        // It's a leaf, if there is a diagonal line all filled with crosses
        if (diagonalCrossesCounter == this.panel.getM())
            return -1;
        // It's a leaf, if there is a diagonal line all filled with noughts
        if (diagonalNoughtsCounter == this.panel.getM())
            return 1;
        
        /* ...or finally if it's a draw ! => All the grid is filled */
        // Count the number of unfilled squares
        int unfilledCounter = 0;
        for (int l = 0; l < this.panel.getM(); l++)
        {
            for (int c = 0; c < this.panel.getM(); c++)
            {
                // If there is a remaining empty square
                if (squares[l][c].getState().equals(""))
                    // The game still goes on => not a leaf
                    unfilledCounter++;
            }
        }
        
        // It's a draw !
        if (unfilledCounter == this.panel.getM())
            return 0;
        
        // Else, it means it's NOT a leaf. 99 : Error => Calling f(node) for a node that is not a leaf
        return 99;
    }
    
    // This method returns the number of crosses (X) played in the game
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
    
    // This method returns the number of noughts (O) played in the game
    private int countOInGame()
    {
        int counter = 0;
        Square squares[][] = this.panel.getSquares();
        
        // For each line
        for (int l = 0; l < this.panel.getM(); l++)
        {
            // And for each column
            for (int c = 0; c < this.panel.getM(); c++)
            {
                // If you find a nought, then increment the counter
                if (squares[l][c].getState().equals("nought"))
                    counter++;
            }
        }
        
        return counter;
    }
    
    // This method tells if it's the human's turn or not
    private boolean isHumanTurn()
    {
        // Count how many times both players played
        int xPlayed = countXInGame() + countOInGame();
        
        // If it's an even number, then it means that it's still the human's turn
        if (xPlayed % 2 == 0)
            return true;
        // Odd number means it's computer's turn
        else
            return false;
    }
    
    // Calcultates the square of a number
    private int sqr(int m)
    {
        return (m * m);
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