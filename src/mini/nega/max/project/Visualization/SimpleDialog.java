package mini.nega.max.project.Visualization;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

/**
 * SimpleDialog Class Implementation (Purpose : Declare the winner)
 * 
 * @author Amine Elkhalsi <aminekhalsi@hotmail.com>
 */

public class SimpleDialog extends JDialog implements ActionListener
{
    /*
     * Constructors
     */
    
    public SimpleDialog(JFrame parent, String title, String message)
    {
        // Set default settings
        super(parent, title, true);
        
        if (parent != null)
        {
          Dimension parentSize = parent.getSize(); 
          Point p = parent.getLocation(); 
          setLocation(p.x + parentSize.width / 4, p.y + parentSize.height / 4);
        }
        
        // The message is the contentPane
        JPanel messagePane = new JPanel();
        messagePane.add(new JLabel(message));
        getContentPane().add(messagePane);
        JPanel buttonPane = new JPanel();
        
        // Add an "OK" button
        JButton button = new JButton("OK"); 
        buttonPane.add(button); 
        
        // Adding a listener to make the dialog box close when the "OK" button is clicked
        button.addActionListener(this);
        getContentPane().add(buttonPane, BorderLayout.SOUTH);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack(); 
        
        // Make the dialog box visible
        setVisible(true);
    }
    
    
    
    /*
     * @Implementation Overrides
     */
    
    @Override
    public void actionPerformed(ActionEvent e)
    {
        // Close the dialog box when button clicked
        setVisible(false); 
        dispose(); 
    }
}