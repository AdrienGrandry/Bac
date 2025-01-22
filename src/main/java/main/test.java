package main;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import java.util.Timer;
import java.util.TimerTask;

public class test extends JFrame {

    private boolean submenuVisible = false;
    private boolean needsRepaint = false;

    public test() {
        setTitle("Responsive Dropdown Menu");
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Timer to repaint every 0.5 seconds if needed
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (needsRepaint) {
                    repaint();
                    needsRepaint = false;
                }
            }
        }, 0, 500);

        // Canvas to draw the menu
        Canvas canvas = new Canvas() {
            @Override
            public void paint(Graphics g) {
                drawMenu(g, getWidth(), getHeight());
            }
        };

        canvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                int width = getWidth();
                int menuWidth = width / 5;
                int submenuHeight = 30;

                // Check if the submenu toggle area is clicked
                if (x > menuWidth * 2 && x < menuWidth * 3 && y > 50 && y < 80) {
                    submenuVisible = !submenuVisible;
                    needsRepaint = true;
                }
            }
        });

        canvas.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent evt) {
                needsRepaint = true;
            }
        });

        add(canvas);
        setVisible(true);
    }

    private void drawMenu(Graphics g, int width, int height) {
        int menuHeight = 50;
        int menuWidth = width / 5;
        int submenuHeight = 30;

        // Draw the main menu
        g.setColor(Color.GRAY);
        g.fillRect(0, 0, width, menuHeight);

        String[] menuItems = {"Home", "About Me", "Articles", "News", "Contact Us"};
        int x = 0;

        for (String item : menuItems) {
            g.setColor(Color.decode("#30A6E6"));
            g.fillRect(x, 10, menuWidth, 30);
            g.setColor(Color.WHITE);
            g.drawString(item, x + menuWidth / 10, 30);
            x += menuWidth;
        }

        // Draw the "Articles" submenu toggle
        g.setColor(Color.decode("#30A6E6"));
        g.fillRect(menuWidth * 2, menuHeight, menuWidth, submenuHeight);
        g.setColor(Color.WHITE);
        g.drawString("Articles on HTML5 & CSS3", menuWidth * 2 + 10, menuHeight + 20);

        if (submenuVisible) {
            // Draw the submenu
            g.setColor(Color.BLACK);
            g.fillRect(menuWidth * 2, menuHeight + submenuHeight, menuWidth, submenuHeight * 3);

            String[] submenuItems = {
                "Difference between SVG vs. Canvas",
                "New features in HTML5",
                "Creating links to sections within a webpage"
            };

            int y = menuHeight + submenuHeight * 2;
            for (String item : submenuItems) {
                g.setColor(Color.BLACK);
                g.fillRect(menuWidth * 2, y - submenuHeight, menuWidth, submenuHeight);
                g.setColor(Color.WHITE);
                g.drawString(item, menuWidth * 2 + 10, y - 10);
                y += submenuHeight;
            }
        }
    }

    public static void main(String[] args) {
        new test();
    }
}
