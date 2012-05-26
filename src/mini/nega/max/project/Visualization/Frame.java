package mini.nega.max.project.Visualization;

import java.awt.Color;
import java.util.ArrayList;
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
    
    public Frame(int gridSize, String algorithm)
    {
        // Set frame parameters
        this.played = false;
        this.panel = new Panel(gridSize);
        this.setSize(300, 300);
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
        loop(algorithm);
    }
    
    
    
    /*
     * Main Loop + Mini-Max Algorithm Implementation
     */
    
    // Frame Main Loop : Here is where the flow of play is been implemented
    private void loop(String algorithm)
    {
        while(true)
        {
            // If the actual game is a leaf <=> If the game reached its end
            SimpleDialog dlg;
            
            // Declare winner then reset the game
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
                    dlg = new SimpleDialog(this, "And the winner is...", "YOU WON !!!");
                    resetGame();
                    break;
            }
            
            // Everytime it's computer's turn
            if (!isHumanTurn(this.panel.getSquares()))
            {
                // Verify once more if there is a winner to declare
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
                            dlg = new SimpleDialog(this, "And the winner is...", "YOU WON !!!");
                            resetGame();
                            break;
                }
                
                // Call for the corresponding Min-Max or Nega-Max algorithm to play next move
                switch(algorithm.toLowerCase())
                {
                    case "minmax":
                        minMaxDecision(this.panel.getSquares());
                        break;
                    case "bigminmax":
                        minMaxDecision_withHeuristic(this.panel.getSquares());
                        break;
                }
            }
        }
    }
    
    // Decide which is the best position to play... then play it !
    private void minMaxDecision(Square[][] givenSquares)
    {
        // Get a copy of the given squares
        Square squares2[][] = givenSquares;
        Square squares[][] = new Square[this.panel.getM()][this.panel.getM()];
        for (int l = 0; l < this.panel.getM(); l++)
        {
            for (int c = 0; c < this.panel.getM(); c++)
            {
                Square s = new Square(givenSquares[l][c].getState());
                squares[l][c] = s;
            }
        }
        
        // These array lists, as their respective names implies, will contain all the returned values of the minMaxAlgorithm
        ArrayList<Integer> values = new ArrayList<>();
        ArrayList<Square[][]> moves = new ArrayList<>();
        ArrayList<Integer> lines = new ArrayList<>();
        ArrayList<Integer> cols = new ArrayList<>();
        
        // For each possible move to play
        for (int l = 0 ; l < this.panel.getM() ; l++)
        {
            for (int c = 0 ; c < this.panel.getM() ; c++)
            {
                // Empty square == New possible move
                if (squares[l][c].getState().equals(""))
                {
                    // We need to save every move with every corresponding Min-Max return value
                    Square tempSquares[][];
                    tempSquares = play(l, c, squares);
                    
                    moves.add(tempSquares);
                    values.add(minMaxValue(tempSquares, false));
                    
                    cols.add(c);
                    lines.add(l);
                }
            }
        }
        
        // Calculate the maximal move value
        int maxValue = values.get(0);
        int maxValueIndex = 0;
        for (int i = 1 ; i < values.size() ; i++)
        {
            if (values.get(i) > maxValue)
            {
                maxValue = values.get(i);
                maxValueIndex = i;
            }
        }
        
        // Play if there is a possible move
        play2(lines.get(maxValueIndex),cols.get(maxValueIndex), squares2);
        
        // Repaint the frame
        this.repaint();
    }
    
    // This is the Min-Max algorithm implementation
    private int minMaxValue(Square[][] node, boolean isMaxNode)
    {
        // If the current given node is a leaf, return it's f(node) value
        if (isLeaf(node))
            return f(node);
        else
        {
            // This will contain the returned values of each node
            ArrayList<Integer> vals = new ArrayList<>();
            
            // For each possible move from this point
            for (int l = 0; l < this.panel.getM(); l++)
            {
                for (int c = 0; c < this.panel.getM(); c++)
                {
                    // Empty = Possible move
                    if (node[l][c].getState().equals(""))
                    {
                        // Play the next possible move
                        vals.add(minMaxValue(play(l, c, node), !isMaxNode));
                    }
                }
                
                // If it's about maximizing or minimizing
                if (isMaxNode)
                    return maxOf(vals);
                else
                    return minOf(vals);
            }
        }
        return 0;
    }
    
    // Nearly the same as the above, now we limit exploration using a heuristic
    // Decide which is the best position to play... then play it !
    private void minMaxDecision_withHeuristic(Square[][] givenSquares)
    {
        // Get a copy of the given squares
        Square squares2[][] = givenSquares;
        Square squares[][] = new Square[this.panel.getM()][this.panel.getM()];
        for (int l = 0; l < this.panel.getM(); l++)
        {
            for (int c = 0; c < this.panel.getM(); c++)
            {
                Square s = new Square(givenSquares[l][c].getState());
                squares[l][c] = s;
            }
        }
        
        // These array lists, as their respective names implies, will contain all the returned values of the minMaxAlgorithm
        ArrayList<Integer> values = new ArrayList<>();
        ArrayList<Square[][]> moves = new ArrayList<>();
        ArrayList<Integer> lines = new ArrayList<>();
        ArrayList<Integer> cols = new ArrayList<>();
        
        // For each possible move to play
        for (int l = 0 ; l < this.panel.getM() ; l++)
        {
            for (int c = 0 ; c < this.panel.getM() ; c++)
            {
                // Empty square == New possible move
                if (squares[l][c].getState().equals(""))
                {
                    // We need to save every move with every corresponding Min-Max return value
                    Square tempSquares[][];
                    tempSquares = play(l, c, squares);
                    
                    moves.add(tempSquares);
                    values.add(minMaxValue_withHeuristic(tempSquares, false, 2));
                    
                    cols.add(c);
                    lines.add(l);
                }
            }
        }
        
        // Calculate the maximal move value
        int maxValue = values.get(0);
        int maxValueIndex = 0;
        for (int i = 1 ; i < values.size() ; i++)
        {
            if (values.get(i) > maxValue)
            {
                maxValue = values.get(i);
                maxValueIndex = i;
            }
        }
        
        // Play if there is a possible move
        play2(lines.get(maxValueIndex),cols.get(maxValueIndex), squares2);
        
        
        // Repaint the frame
        this.repaint();
    }
    
    // This is the Min-Max algorithm implementation. Now we limit exploration using a heuristic method
    private int minMaxValue_withHeuristic(Square[][] node, boolean isMaxNode, int pmax)
    {
        // If the current given node is a leaf, return it's f(node) value
        if (isLeaf(node))
            return f(node);
        else
        {
            // If limit reached and not a leaf
            if (pmax == 0)
            {
                // Call heuristic method to estimate the current situation
                return h(node);
            }
            else
            {
                // This will contain the returned values of each node
                ArrayList<Integer> vals = new ArrayList<>();
                
                // For each possible move from this point
                for (int l = 0; l < this.panel.getM(); l++)
                {
                    for (int c = 0; c < this.panel.getM(); c++)
                    {
                        // Empty = Possible move
                        if (node[l][c].getState().equals(""))
                        {
                            // Play the next possible move
                            vals.add(minMaxValue_withHeuristic(play(l, c, node), !isMaxNode, pmax-1));
                        }
                    }
                    
                    // If it's about maximizing or minimizing
                    if (isMaxNode)
                        return maxOf(vals);
                    else
                        return minOf(vals);
                }
            }
        }
        return 0;
    }
    
    // This method returns an estimation of the situation of the computer at the reached state that corresponds to the given node
    private int h(Square[][] node)
    {
        Square squares[][] = node;
        
        /* Calculates h(node) when the actual game is a leaf if... */
        
        /* 
         1 : Advantage
        -1 : Disadvantage
         0 : Nothing to say ; Neither advantage nor disadvantage
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
            
            // If there is a LINE that has m-1 crosses but no noughts => Disadvantage
            if ((horizontalCrossesCounter == this.panel.getM()-1) && (horizontalNoughtsCounter == 0))
                return -1;
            // But if there is a LINE that has m-1 noughts yet no crosses => Advantage
            else if ((horizontalNoughtsCounter == this.panel.getM()-1) && (horizontalCrossesCounter == 0))
                return 1;
            // If there is a COLUMN that has m-1 crosses but no noughts => Disadvantage
            if ((verticalCrossesCounter == this.panel.getM()-1) && (verticalNoughtsCounter == 0))
                return -1;
            // But if there is a COLUMN that has m-1 noughts yet no crosses => Advantage
            else if ((verticalNoughtsCounter == this.panel.getM()-1) && (verticalCrossesCounter == 0))
                return 1;
            // If there is a DIAGONAL that has m-1 crosses but no noughts => Disadvantage
            if ((diagonalCrossesCounter == this.panel.getM()-1) && (diagonalNoughtsCounter == 0))
                return -1;
            // But if there is a DIAGONAL that has m-1 noughts yet no crosses => Advantage
            else if ((diagonalNoughtsCounter == this.panel.getM()-1) && (diagonalCrossesCounter == 0))
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
        
        // If there is a DIAGONAL that has m-1 crosses but no noughts => Disadvantage
        if ((diagonalCrossesCounter == this.panel.getM()-1) && (diagonalNoughtsCounter == 0))
            return -1;
        // But if there is a DIAGONAL that has m-1 noughts yet no crosses => Advantage
        else if ((diagonalNoughtsCounter == this.panel.getM()-1) && (diagonalCrossesCounter == 0))
            return 1;
        
        // Else, neither advantage nor disadvantage
        return 0;
    }
    
    
    
    /*
     * Internal Methods
     */
    
    // MEMORY METHOD (makes copy of givenSquares) : This method plays a nought or a cross, depending on whose turn to play, at the indicated line and column
    private Square[][] play(int line, int column, Square[][] givenSquares)
    {
        // First copy the grid
        Square squares[][] = new Square[this.panel.getM()][this.panel.getM()];
        for (int l = 0; l < this.panel.getM(); l++)
        {
            for (int c = 0; c < this.panel.getM(); c++)
            {
                Square s = new Square(givenSquares[l][c].getState());
                squares[l][c] = s;
            }
        }
        
        // If it's human's turn, put a cross
        if (isHumanTurn(squares))
            squares[line][column].setState("cross");
        // Else it's computer's turn, then put a nought
        else
            squares[line][column].setState("nought");
        
        // Return the new grid
        return squares;
    }
    
    // GRID METHOD (works directly on the givenSquares) : This method plays a nought or a cross, depending on whose turn to play, at the indicated line and column
    private Square[][] play2(int line, int column, Square[][] givenSquares)
    {
        // First copy the grid
        Square squares[][] = givenSquares;
        
        // If it's human's turn, put a cross
        if (isHumanTurn(squares))
            squares[line][column].setState("cross");
        // Else it's computer's turn, then put a nought
        else
            squares[line][column].setState("nought");
        
        // Return the new grid
        return squares;
    }
    
    // This method plays a nought in the next empty square
    private Square[][] playNext(Square[][] node)
    {
        // First copy the grid
        Square squares[][] = new Square[this.panel.getM()][this.panel.getM()];
        for (int l = 0; l < this.panel.getM(); l++)
        {
            for (int c = 0; c < this.panel.getM(); c++)
            {
                Square s = new Square(node[l][c].getState());
                squares[l][c] = s;
            }
        }
        
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
        this.repaint();
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
    
    // Return the value that corresponds to the game result of a reached leaf
    private int f(Square[][] leaf)
    {
        Square squares[][] = leaf;
        
        /* Calculates f(leaf) when the actual game is a leaf if... */
        
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
                if (!squares[l][c].getState().equals(""))
                    // The game still goes on => not a leaf
                    unfilledCounter++;
            }
        }
        
        // It's a draw !
        if (unfilledCounter == sqr(this.panel.getM()))
            return 0;
        
        // Else, it means it's NOT a leaf. 99 : Error => Calling f(node) for a node that is not a leaf
        return 99;
    }
    
    // This method returns the number of crosses (X) played in the game
    private int countXInGame(Square[][] givenSquares)
    {
        int counter = 0;
        Square squares[][] = givenSquares;
        
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
    private int countOInGame(Square[][] givenSquares)
    {
        int counter = 0;
        Square squares[][] = givenSquares;
        
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
    private boolean isHumanTurn(Square[][] givenSquares)
    {
        // Count how many times both players played
        int xPlayed = countXInGame(givenSquares) + countOInGame(givenSquares);
        
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
    
    // This method returns the min integer in a set of integers
    private int minOf(ArrayList<Integer> vals)
    {
        if (!vals.isEmpty())
        {
        // Save first value
        int min = vals.get(0);
        
        // Compare it to all other values stored in vals
        for (int i = 1 ; i < vals.size() ; i++)
        {
            if (vals.get(i) < min)
                min = vals.get(i);
        }
        
        // Return the smallest value
        return min;
        }
        return 0;
    }
    
    // This method returns the max integer in a set of integers
    private int maxOf(ArrayList<Integer> vals)
    {
        if (!vals.isEmpty())
        {
            // Save first value
        int max = vals.get(0);
        
        // Compare it to all other values stored in vals
        for (int i = 1 ; i < vals.size() ; i++)
        {
            if (vals.get(i) > max)
                max = vals.get(i);
        }
        
        // Return the biggest value
        return max;
        }
        return 0;
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