package mini.nega.max.project;

import mini.nega.max.project.Visualization.Frame;

/**
 * Project's Main Class Implementation
 * 
 * @author Amine Elkhalsi <aminekhalsi@hotmail.com>
 * 
 */

public class MiniNegaMaxProject
{
    public static void main(String[] args)
    {
        // First argument : Dimension of the grid
        // Second Argument : The Algorithm => Value must be one of : {"minmax", "bigminmax", "negamax"}
        Frame f = new Frame(3, "BigMinMax");
    }
}