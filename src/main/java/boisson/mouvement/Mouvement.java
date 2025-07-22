package boisson.mouvement;

import boisson.mouvement.AddMouvement;
import ressources.dataBase.Requete;
import ressources.Style;
import options.ColorXml;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class Mouvement extends JPanel
{
    private static final long serialVersionUID = 1L;
    private ActionListener changeTab;

    public Mouvement(final JFrame parentFrame)
    {
        final ColorXml color = new ColorXml();
        final Requete requete = new Requete();

        setBackground(Color.decode(color.xmlReader("background")));
        setLayout(new BorderLayout(10, 10));

        JPanel entete = new JPanel();
        entete.setBackground(Color.decode(color.xmlReader("background")));
        entete.setLayout(new BorderLayout(10, 10));

        JLabel titre = new JLabel("Mouvements", SwingConstants.CENTER);
        titre.setFont(new Font("Arial", Font.BOLD, 40));
        titre.setForeground(Color.decode(color.xmlReader("foreground")));
        titre.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.decode(color.xmlReader("background")));
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

        JButton button = new JButton("AJOUTER UN MOUVEMENT");
        Style.applyButtonStyle(button);
        button.setPreferredSize(new Dimension(250, 40));
        button.setMaximumSize(new Dimension(250, 40));
        button.setMinimumSize(new Dimension(250, 40));
        button.setBorder(BorderFactory.createLineBorder(Color.black));

        JButton buttonInventaire = new JButton("INVENTAIRE");
        Style.applyButtonStyle(buttonInventaire);
        buttonInventaire.setPreferredSize(new Dimension(250, 40));
        buttonInventaire.setMaximumSize(new Dimension(250, 40));
        buttonInventaire.setMinimumSize(new Dimension(250, 40));
        buttonInventaire.setBorder(BorderFactory.createLineBorder(Color.black));

        buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(button);
        buttonPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        buttonPanel.add(buttonInventaire);
        buttonPanel.add(Box.createHorizontalGlue());

        entete.add(titre, BorderLayout.NORTH);
        entete.add(buttonPanel, BorderLayout.CENTER);
        add(entete, BorderLayout.NORTH);

        final JPanel tableauPanel = new JPanel();
        tableauPanel.setBackground(Color.decode(color.xmlReader("background")));
        tableauPanel.setLayout(new BorderLayout());
        add(tableauPanel, BorderLayout.CENTER);
        tableauPanel.setPreferredSize(new Dimension(parentFrame.getWidth(), parentFrame.getHeight()));

        changeTab = new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                String sql =
                        "SELECT type AS 'Type', Libelle AS 'Nom du produit', quantite AS 'Quantite', " +
                        "strftime('%d/%m/%Y', date_creation) AS 'Date', description AS 'Description' " +
                        "FROM inclure " +
                        "JOIN mouvement ON mouvement.id = inclure.id_mouvement " +
                        "JOIN produit ON produit.id = inclure.id_produit ORDER BY date_creation DESC;";

                tableauPanel.removeAll();
                JPanel tab = null;
                tab = requete.executeQueryAndReturnPanel(sql, tableauPanel.getHeight(), tableauPanel.getWidth(),
                        "type_colonne");

                tableauPanel.add(tab);
                revalidate();
                repaint();
            }
        };

        changeTab.actionPerformed(null);

        button.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                AddMouvement fenetreProduit = null;

                try
                {
                    fenetreProduit = new AddMouvement(parentFrame);
                } catch (SQLException e1)
                {
                    e1.printStackTrace();
                }

                fenetreProduit.addWindowListener(new java.awt.event.WindowAdapter()
                {
                    @Override
                    public void windowClosed(java.awt.event.WindowEvent windowEvent)
                    {
                        changeTab.actionPerformed(null);
                    }
                });
                fenetreProduit.setVisible(true);
            }
        });

        buttonInventaire.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                Inventaire fenetreInvenaire = null;

                try
                {
                    fenetreInvenaire = new Inventaire(parentFrame);
                } catch (SQLException e1)
                {
                    e1.printStackTrace();
                }

                fenetreInvenaire.addWindowListener(new java.awt.event.WindowAdapter()
                {
                    @Override
                    public void windowClosed(java.awt.event.WindowEvent windowEvent)
                    {
                        changeTab.actionPerformed(null);
                    }
                });
                fenetreInvenaire.setVisible(true);
            }
        });
    }
}