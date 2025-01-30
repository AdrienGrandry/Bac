package database;

import ressources.ColorXml;
import style.Style;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

public class Database extends JPanel
{
    private static final long serialVersionUID = 1L;

    public Database(JFrame parentFrame)
    {
        final ColorXml color = new ColorXml();
        final Requete requete = new Requete();

        setBackground(Color.decode(color.xmlReader("background")));
        setLayout(new BorderLayout(10, 10));

        JPanel entete = new JPanel();
        entete.setBackground(Color.decode(color.xmlReader("background")));
        entete.setLayout(new BorderLayout(10, 10));
        add(entete, BorderLayout.NORTH);

        JLabel titre = new JLabel("Base de donn�es", SwingConstants.CENTER);
        titre.setFont(new Font("Arial", Font.BOLD, 40));
        titre.setForeground(Color.decode(color.xmlReader("foreground")));
        titre.setBorder(new EmptyBorder(10, 10, 10, 10));
        entete.add(titre, BorderLayout.NORTH);

        final JTextArea textArea = new JTextArea();
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(new Font("Arial", Font.PLAIN, 15));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 100));

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Color.decode(color.xmlReader("background")));
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        centerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        entete.add(centerPanel, BorderLayout.CENTER);

        final JButton button = new JButton("VALIDER . . .");
        Style.applyButtonStyle(button);
        button.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        button.setPreferredSize(new Dimension(200, 50));
        entete.add(button, BorderLayout.SOUTH);

        final JPanel tableauPanel = new JPanel();
        tableauPanel.setBackground(Color.decode(color.xmlReader("background")));
        add(tableauPanel, BorderLayout.WEST);

        button.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                tableauPanel.removeAll();
                JPanel tab = requete.executeQueryAndReturnPanel(textArea.getText(), tableauPanel.getSize().height, getSize().width, "pair_impair");

                tableauPanel.add(tab);

                revalidate();
                repaint();
            }
        });
    }
}