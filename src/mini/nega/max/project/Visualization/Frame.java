package mini.nega.max.project.Visualization;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Scanner;
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
    
    /** Content panel of the frame */
    private Panel panel;
    
    /** Maximal depth that the constructed tree can reach. Just for bigminmax and negamax algorithms */
    private int maxDepth;
    
    
    /*
     * Constructors
     */
    
    public Frame(int gridSize, String algorithm)
    {
        // Set frame parameters
        maxDepth = 0;
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
        if ((algorithm.toLowerCase().equals("minmax")) || (algorithm.toLowerCase().equals("bigminmax")) || (algorithm.toLowerCase().equals("negamax")) || (algorithm.toLowerCase().equals("alphabeta")))
        {
            // If it's about the bigminmax or negamax algorithms that limits the size of the constructed tree
            if (!((algorithm.toLowerCase().equals("minmax"))))
            {
                int resp = 0;
                while (resp <= 0)
                {
                    // So we need to ask for the maximal depth
                    System.out.print("Please specify the maximal depth : ");
                    Scanner sc = new Scanner(System.in);
                    resp = sc.nextInt();
                    this.maxDepth = resp;
                }
            }
            
            this.setVisible(true);
            
            // Call Main Loop
            loop(algorithm);
        }
        else
        {
            // If the algorithm is unknown, show error
            System.err.println("Warning : Specified algorithm is unknown !");
            System.err.println("=> Value must be one of {\"minmax\", \"bigminmax\", \"negamax\", \"alphabeta\"}");
        }
    }
    
    
    
    /*
     * Main Loop + Mini-Max Algorithm Implementation
     */
    
    /** Frame Main Loop : Here is where the flow of play is been implemented */
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
                    case "negamax":
                        negaMaxDecision(this.panel.getSquares());
                        break;
                    case "alphabeta":
                        alphaBetaDecision(this.panel.getSquares());
                        break;
                }
            }
        }
    }
    
    /** Decide by Alpha-Beta algorithm which is the best position to play... then play it ! */
    private void alphaBetaDecision(Square[][] givenSquares)
    {
        // Get a copy of the given squares
        Square squares[][] = new Square[this.panel.getM()][this.panel.getM()];
        for (int l = 0 ; l < this.panel.getM() ; l++)
        {
            for (int c = 0 ; c < this.panel.getM() ; c++)
            {
                Square s = new Square(givenSquares[l][c].getState());
                squares[l][c] = s;
            }
        }
        
        // These array lists, as their respective names implies, will contain all the returned values of the minMaxAlgorithm
        ArrayList<Integer> values = new ArrayList<>();
        ArrayList<Integer> lines = new ArrayList<>();
        ArrayList<Integer> cols = new ArrayList<>();
        
        // We'll save the maximal returned value from the Min-Max algorithm
        int alpha = Integer.MIN_VALUE;
        int alphaResult;
        
        // For each possible move to play
        for (int l = 0 ; l < this.panel.getM() ; l++)
        {
            for (int c = 0 ; c < this.panel.getM() ; c++)
            {
                if (squares[l][c].getState().equals(""))
                {
                    // Play the next possible move
                    Square[][] tempSqr = play(l, c, squares);
                    alphaResult = alphaBetaValue(tempSqr, alpha, Integer.MAX_VALUE, this.maxDepth);
                    
                    // Since we start from the source node, we have to maximize
                    if (alphaResult > alpha)
                    {
                        // Get the last returned value from the alpha-beta algorithm
                        alpha = alphaResult;
                        
                        // Save the move
                        values.add(alpha);
                        cols.add(c);
                        lines.add(l);
                    }
                    
                    // Cancel last simulated move
                    squares[l][c].setState("");
                }
            }
        }
        
        
        // Calculate the maximal move value index
        int maxValueIndex = 0;
        for (int i = 1 ; i < values.size() ; i++)
        {
            if (values.get(i) == alpha)
            {
                maxValueIndex = i;
            }
        }
        
        // Play it !
        play2(lines.get(maxValueIndex),cols.get(maxValueIndex), givenSquares);
        
        // Show total simulated combinations
        System.out.println("There were " + countPossibilities + " simulated moves combination.");
        countPossibilities = 0;
        
        // Repaint the frame
        this.repaint();
    }
    
    /** Global variable that counts possibilities */
    int countPossibilities = 0;
    
    /** This is the Alpha-Beta algorithm implementation */
    private int alphaBetaValue(Square[][] node, int givenAlpha, int givenBeta, int pmax)
    {
        countPossibilities++;
        
        // Recover and store the two given values of alpha and beta
        int alpha = givenAlpha;
        int beta = givenBeta;
        
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
            {
                // If it's about minimizing
                if (isHumanTurn(node))
                {
                    int betaResult;
                    
                    // For each possible move from this point
                    for (int l = 0; l < this.panel.getM(); l++)
                    {
                        for (int c = 0; c < this.panel.getM(); c++)
                        {
                            countPossibilities++;
                            // Empty = Possible move
                            if (node[l][c].getState().equals(""))
                            {
                                // Play the next possible move
                                betaResult = alphaBetaValue(play(l, c, node), alpha, beta, pmax - 1);
                                node[l][c].setState("");
                                
                                if (betaResult < beta)
                                    beta = betaResult;
                                
                                // If it's a beta cut
                                if (beta <= alpha)
                                    return beta;
                            }
                        }
                    }
                    return beta;
                }
                // If it's about maximizing
                else
                {
                    int alphaResult;
                    
                    // For each possible move from this point
                    for (int l = 0; l < this.panel.getM(); l++)
                    {
                        for (int c = 0; c < this.panel.getM(); c++)
                        {
                            // Empty = Possible move
                            if (node[l][c].getState().equals(""))
                            {
                                countPossibilities++;
                                // Play the next possible move
                                alphaResult = alphaBetaValue(play(l, c, node), alpha, beta, pmax - 1);
                                node[l][c].setState("");
                                
                                if (alphaResult > alpha)
                                    alpha = alphaResult;
                                
                                // If it's an alpha cut
                                if (alpha >= beta)
                                    return alpha;
                            }
                        }
                    }
                    return alpha;
                }
            }
        }
    }
    
    /** Decide by MinMax which is the best position to play... then play it ! */
    private void minMaxDecision(Square[][] givenSquares)
    {
        // Get a copy of the given squares
        Square squares[][] = new Square[this.panel.getM()][this.panel.getM()];
        for (int l = 0 ; l < this.panel.getM() ; l++)
        {
            for (int c = 0 ; c < this.panel.getM() ; c++)
            {
                Square s = new Square(givenSquares[l][c].getState());
                squares[l][c] = s;
            }
        }
        
        // These array lists, as their respective names implies, will contain all the returned values of the minMaxAlgorithm
        ArrayList<Integer> values = new ArrayList<>();
        ArrayList<Integer> lines = new ArrayList<>();
        ArrayList<Integer> cols = new ArrayList<>();
        
        // We'll save the maximal returned value from the Min-Max algorithm
        int alpha = Integer.MIN_VALUE;
        int alphaResult;
        
        // For each possible move to play
        for (int l = 0 ; l < this.panel.getM() ; l++)
        {
            for (int c = 0 ; c < this.panel.getM() ; c++)
            {
                if (squares[l][c].getState().equals(""))
                {
                    // Play the next possible move
                    Square[][] tempSqr = play(l, c, squares);
                    alphaResult = minMaxValue(tempSqr);
                    
                    // Since we start from the source node, we have to maximize
                    if (alphaResult > alpha)
                    {
                        alpha = alphaResult;
                        values.add(alpha);
                        cols.add(c);
                        lines.add(l);
                    }
                    
                    // Cancel last simulated move
                    squares[l][c].setState("");
                }
            }
        }
        
        
        // Calculate the maximal move value index
        int maxValueIndex = 0;
        for (int i = 1 ; i < values.size() ; i++)
        {
            if (values.get(i) == alpha)
            {
                maxValueIndex = i;
            }
        }
        
        // Play it !
        play2(lines.get(maxValueIndex),cols.get(maxValueIndex), givenSquares);
        
        // Show total simulated combinations
        System.out.println("There were " + countPossibilities + " simulated moves combination.");
        countPossibilities = 0;
        
        // Repaint the frame
        this.repaint();
    }
    
    /** This is the Min-Max algorithm implementation */
    private int minMaxValue(Square[][] node)
    {
        countPossibilities++;
        // If the current given node is a leaf, return it's f(node) value
        if (isLeaf(node))
            return f(node);
        else
        {
                // If it's about minimizing
                if (isHumanTurn(node))
                {
                    int beta = Integer.MAX_VALUE;
                    int betaResult;
                    
                    // For each possible move from this point
                    for (int l = 0; l < this.panel.getM(); l++)
                    {
                        for (int c = 0; c < this.panel.getM(); c++)
                        {
                            // Empty = Possible move
                            if (node[l][c].getState().equals(""))
                            {
                                countPossibilities++;
                                // Play the next possible move
                                betaResult = minMaxValue(play(l, c, node));
                                node[l][c].setState("");
                                
                                // If it's about maximizing or minimizing
                                if (betaResult < beta)
                                    beta = betaResult;
                            }
                        }
                    }
                    return beta;
                }
                // If it's about maximizing
                else
                {
                    int alpha = Integer.MIN_VALUE;
                    int alphaResult;
                    
                    // For each possible move from this point
                    for (int l = 0; l < this.panel.getM(); l++)
                    {
                        for (int c = 0; c < this.panel.getM(); c++)
                        {
                            countPossibilities++;
                            // Empty = Possible move
                            if (node[l][c].getState().equals(""))
                            {
                                // Play the next possible move
                                alphaResult = minMaxValue(play(l, c, node));
                                node[l][c].setState("");
                                
                                // If it's about maximizing or minimizing
                                if (alphaResult > alpha)
                                    alpha = alphaResult;
                            }
                        }
                    }
                    return alpha;
                }
        }
    }
    
    /** Nearly the same as the above, now we limit exploration using a heuristic
        Decide which is the best position to play... then play it ! */
    private void minMaxDecision_withHeuristic(Square[][] givenSquares)
    {
        countPossibilities++;
        
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
                    squares[l][c].setState("nought");
                    
                    values.add(minMaxValue_withHeuristic(squares, this.maxDepth));
                    cols.add(c);
                    lines.add(l);
                    
                    // Cancel last simulated move
                    squares[l][c].setState("");
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
        
        // Show total simulated combinations
        System.out.println("There were " + countPossibilities + " simulated moves combination.");
        countPossibilities = 0;
        
        // Repaint the frame
        this.repaint();
    }
    
    /** This is the Min-Max algorithm implementation. Now we limit exploration using a heuristic method */
    private int minMaxValue_withHeuristic(Square[][] node, int pmax)
    {
        countPossibilities++;
        
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
                // Minimization
                if (isHumanTurn(node))
                {
                    int beta = Integer.MAX_VALUE;
                    int betaResult;
                    
                    // For each possible move from this point
                    for (int l = 0; l < this.panel.getM(); l++)
                    {
                        for (int c = 0; c < this.panel.getM(); c++)
                        {
                            // Empty = Possible move
                            if (node[l][c].getState().equals(""))
                            {
                                // Play the next possible move
                                betaResult = minMaxValue_withHeuristic(play(l, c, node), pmax-1);
                                node[l][c].setState("");
                                
                                // If it's about maximizing or minimizing
                                if (betaResult < beta)
                                    beta = betaResult;
                            
                                countPossibilities++;
                            }
                        }
                    }
                    return beta;
                }
                // Maximization
                else
                {
                    int alpha = Integer.MIN_VALUE;
                    int alphaResult;
                    
                    // For each possible move from this point
                    for (int l = 0; l < this.panel.getM(); l++)
                    {
                        for (int c = 0; c < this.panel.getM(); c++)
                        {
                            // Empty = Possible move
                            if (node[l][c].getState().equals(""))
                            {
                                // Play the next possible move
                                alphaResult = minMaxValue_withHeuristic(play(l, c, node), pmax-1);
                                node[l][c].setState("");
                                
                                // If it's about maximizing or minimizing
                                if (alphaResult > alpha)
                                    alpha = alphaResult;
                                
                                countPossibilities++;
                            }
                        }
                    }
                    return alpha;
                }
            }
        }
    }
    
    /** NegaMaxDecision method. Decide which is the best position to play... then play it ! */
    private void negaMaxDecision(Square[][] givenSquares)
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
                    squares[l][c].setState("nought");
                    
                    values.add(0 - negaMaxValue(squares, this.maxDepth));
                    cols.add(c);
                    lines.add(l);
                    
                    // Cancel last simulated move
                    squares[l][c].setState("");
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
        
        // Show total simulated combinations
        System.out.println("There were " + countPossibilities + " simulated moves combination.");
        countPossibilities = 0;
        
        // Repaint the frame
        this.repaint();
    }
    
    /** This is the Nega-Max algorithm implementation. With heuristic and limited exploration */
    private int negaMaxValue(Square[][] node, int pmax)
    {
        countPossibilities++;
        
        // If the current given node is a leaf, return it's f(node) value
        if (isLeaf(node))
            return g(node);
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
                int alpha = Integer.MIN_VALUE;
                int alphaResult;
                
                // For each possible move from this point
                for (int l = 0; l < this.panel.getM(); l++)
                {
                    for (int c = 0; c < this.panel.getM(); c++)
                    {
                        // Empty = Possible move
                        if (node[l][c].getState().equals(""))
                        {
                            // Play the next possible move
                            alphaResult = 0 - negaMaxValue(play(l, c, node), pmax-1);
                            node[l][c].setState("");
                            
                            // Alway Maximize Negation
                            if (alphaResult > alpha)
                                alpha = alphaResult;
                        }
                    }
                 }
                countPossibilities++;
                return alpha;
            }
        }
    }
    
    
    
    /*
     * Internal Methods
     */
    
    /** This method returns an estimation of the situation of the computer at the reached state that corresponds to the given node */
    private int h(Square[][] node)
    {
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
                if (node[l][c].getState().equals("cross"))
                    horizontalCrossesCounter++;
                // Or if it contains a nought
                if (node[l][c].getState().equals("nought"))
                    horizontalNoughtsCounter++;
                
                // We then do the same for columns
                if (node[c][l].getState().equals("cross"))
                    verticalCrossesCounter++;
                if (node[c][l].getState().equals("nought"))
                    verticalNoughtsCounter++;
                
                // ... same thing for diagonals (from TOP LEFT to BOTTOM RIGHT)
                if (l == c)
                {
                    if (node[l][c].getState().equals("cross"))
                        diagonalCrossesCounter++;
                    if (node[l][c].getState().equals("nought"))
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
            if (node[i][this.panel.getM() - i - 1].getState().equals("cross"))
                diagonalCrossesCounter++;
            if (node[i][this.panel.getM() - i - 1].getState().equals("nought"))
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
    
    /** Calculates g function for the Nega-Max algorithm */
    private int g(Square[][] node)
    {
        if(!isHumanTurn(node))
            return f(node);
        else
            return -f(node);
    }
    
    /** MEMORY METHOD (makes copy of givenSquares) : This method plays a nought or a cross, depending on whose turn to play, at the indicated line and column */
    private Square[][] play(int line, int column, Square[][] givenSquares)
    {
        // If it's human's turn, put a cross
        if (isHumanTurn(givenSquares))
            givenSquares[line][column].setState("cross");
        // Else it's computer's turn, then put a nought
        else
            givenSquares[line][column].setState("nought");
        
        return givenSquares;
    }
    
    /** GRID METHOD (works directly on the givenSquares) : This method plays a nought or a cross, depending on whose turn to play, at the indicated line and column */
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
    
    /** This method plays a nought in the next empty square */
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
    
    /** This game reinitilizes the game to the starting point */
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
    
    /** Return true if the reached state corresponds to a leaf */
    private boolean isLeaf(Square[][] node)
    {
        /* The actual game is a leaf if... */
        
        /* ...there is a complete LINE, COLUMN or DIAGONAL filled with the same type of chip */
        
        // counter of "crosses in a same diagonal" (from Top Left to Bottom Right)
        int diagonalCrossesCounter = 0;
        // counter of "noughts in a same diagonal"
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
                if (node[l][c].getState().equals("cross"))
                    horizontalCrossesCounter++;
                // Or if it contains a nought
                if (node[l][c].getState().equals("nought"))
                    horizontalNoughtsCounter++;
                
                // We then do the same for columns
                if (node[c][l].getState().equals("cross"))
                    verticalCrossesCounter++;
                if (node[c][l].getState().equals("nought"))
                    verticalNoughtsCounter++;
                
                // ... same thing for diagonals (from TOP LEFT to BOTTOM RIGHT)
                if (l == c)
                {
                    if (node[l][c].getState().equals("cross"))
                        diagonalCrossesCounter++;
                    if (node[l][c].getState().equals("nought"))
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
            if (node[i][this.panel.getM() - i - 1].getState().equals("cross"))
                diagonalCrossesCounter++;
            if (node[i][this.panel.getM() - i - 1].getState().equals("nought"))
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
                if (node[l][c].getState().equals(""))
                    // The game still goes on => not a leaf
                    unfilledCounter++;
            }
        }
        
        // It's a draw !
        if (unfilledCounter == 0)
            return true;
        
        // Else, it means it's NOT a leaf
        return false;
    }
    
    /** Return the value that corresponds to the game result of a reached leaf */
    private int f(Square[][] leaf)
    {
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
                if (leaf[l][c].getState().equals("cross"))
                    horizontalCrossesCounter++;
                // Or if it contains a nought
                if (leaf[l][c].getState().equals("nought"))
                    horizontalNoughtsCounter++;
                
                // We then do the same for columns
                if (leaf[c][l].getState().equals("cross"))
                    verticalCrossesCounter++;
                if (leaf[c][l].getState().equals("nought"))
                    verticalNoughtsCounter++;
                
                // ... same thing for diagonals (from TOP LEFT to BOTTOM RIGHT)
                if (l == c)
                {
                    if (leaf[l][c].getState().equals("cross"))
                        diagonalCrossesCounter++;
                    if (leaf[l][c].getState().equals("nought"))
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
            if (leaf[i][this.panel.getM() - i - 1].getState().equals("cross"))
                diagonalCrossesCounter++;
            if (leaf[i][this.panel.getM() - i - 1].getState().equals("nought"))
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
                if (leaf[l][c].getState().equals(""))
                    // The game still goes on => not a leaf
                    unfilledCounter++;
            }
        }
        
        // It's a draw !
        if (unfilledCounter == 0)
            return 0;
        
        // Else, it means it's NOT a leaf. 99 : Error => Calling f(x) while x is not a leaf
        return 99;
    }
    
    /** This method returns the number of crosses (X) played in the game */
    private int countXInGame(Square[][] givenSquares)
    {
        int counter = 0;
        
        // For each line
        for (int l = 0; l < this.panel.getM(); l++)
        {
            // And for each column
            for (int c = 0; c < this.panel.getM(); c++)
            {
                // If you find a cross, then increment the counter
                if (givenSquares[l][c].getState().equals("cross"))
                    counter++;
            }
        }
        
        return counter;
    }
    
    /** This method returns the number of noughts (O) played in the game */
    private int countOInGame(Square[][] givenSquares)
    {
        int counter = 0;
        
        // For each line
        for (int l = 0; l < this.panel.getM(); l++)
        {
            // And for each column
            for (int c = 0; c < this.panel.getM(); c++)
            {
                // If you find a nought, then increment the counter
                if (givenSquares[l][c].getState().equals("nought"))
                    counter++;
            }
        }
        
        return counter;
    }
    
    /** This method tells if it's the human's turn or not */
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
    
    /** Calcultates the square of a number */
    private int sqr(int m)
    {
        return (m * m);
    }
    
    /** This method returns the min integer in a set of integers */
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
    
    /** This method returns the max integer in a set of integers */
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
}