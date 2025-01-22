package main;
import ressources.Variables;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JFrame;

public class Main {
	static Variables variables = new Variables();
	
    public static void main(String[] args) {
    	//déclaration autres classes

    	final JFrame frame = new JFrame("BAC");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);
        frame.getContentPane().setLayout(null);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);
        
        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
            	updateSize(frame);
            }
        });

    }
    
   public static void updateSize(JFrame frame)
   {
	   variables.setHeight(frame.getSize().height);
	   variables.setWidth(frame.getSize().width);
   }
}