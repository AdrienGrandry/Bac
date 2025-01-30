package stock;

import database.Requete;
import style.Style;

import ressources.ColorXml;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Stock extends JPanel
{
    private static final long serialVersionUID = 1L;
    private ActionListener changeTab;

    public Stock(JFrame parentFrame)
    {
        final ColorXml color = new ColorXml();
        final Requete requete = new Requete();

        setBackground(Color.decode(color.xmlReader("background")));
        setLayout(new BorderLayout(10, 10));

        JPanel entete = new JPanel();
        entete.setBackground(Color.decode(color.xmlReader("background")));
        entete.setLayout(new BorderLayout(10, 10));

        JLabel titre = new JLabel("Stock", SwingConstants.CENTER);
        titre.setFont(new Font("Arial", Font.BOLD, 40));
        titre.setForeground(Color.decode(color.xmlReader("foreground")));
        titre.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.decode(color.xmlReader("background")));
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

        String[] periods = {"Tous", "Salle", "Cafétéria"};

        final JComboBox<String> comboBox = new JComboBox<>(periods);
        Style.applyBoxStyle(comboBox);
        comboBox.setPreferredSize(new Dimension(250, 40));
        comboBox.setMaximumSize(new Dimension(250, 40));
        comboBox.setMinimumSize(new Dimension(250, 40));
        
        JPanel totalPanel = new JPanel();
        totalPanel.setPreferredSize(new Dimension(250, 40));
        totalPanel.setMaximumSize(new Dimension(250, 40));
        totalPanel.setMinimumSize(new Dimension(250, 40));
        Style.applyPanelStyle(totalPanel);
        
        final JLabel totalLabel = new JLabel();
        totalLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        totalLabel.setForeground(Color.decode(color.xmlReader("foreground")));
        totalPanel.add(totalLabel);
        
        buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(comboBox);
        buttonPanel.add(Box.createRigidArea(new Dimension(50, 0)));
        buttonPanel.add(totalPanel);
        buttonPanel.add(Box.createHorizontalGlue());
        

        entete.add(titre, BorderLayout.NORTH);
        entete.add(buttonPanel, BorderLayout.CENTER);
        add(entete, BorderLayout.NORTH);

        final JPanel tableauPanel = new JPanel();
        tableauPanel.setBackground(Color.decode(color.xmlReader("background")));
        tableauPanel.setLayout(new BorderLayout()); 
        add(tableauPanel, BorderLayout.CENTER);
        tableauPanel.setPreferredSize(new Dimension(parentFrame.getWidth(), parentFrame.getHeight()));
        
        changeTab = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedPeriod = (String) comboBox.getSelectedItem();
                String sql = null;

                switch (selectedPeriod) {
                    case "Tous":
                        sql = "Select numero as 'Numéro', libelle as 'Libelle', lieu as 'Lieu', stock as 'Stock', prix as 'Prix Unité', (prix*stock) as 'Prix total' from produit  where visible = 1 order by numero";
                        break;
                    case "Salle":
                        sql = "Select numero as 'Numéro', libelle as 'Libelle', lieu as 'Lieu', stock as 'Stock', prix as 'Prix Unité', (prix*stock) as 'Prix total' from produit  where visible = 1 and lieu='salle' order by numero";
                        break;
                    case "Cafétéria":
                        sql = "Select numero as 'Numéro', libelle as 'Libelle', lieu as 'Lieu', stock as 'Stock', prix as 'Prix Unité', (prix*stock) as 'Prix total' from produit  where visible = 1 and lieu='cafeteria' order by numero";
                        break;
                }

                tableauPanel.removeAll(); 
                JPanel tab = requete.executeQueryAndReturnPanel(sql, tableauPanel.getHeight(), tableauPanel.getWidth(), "pair_impair");

                tableauPanel.add(tab);
                revalidate();
                repaint();
                
                try (Connection connection = DriverManager.getConnection("jdbc:sqlite:Database.db"))
                {
                	String query = null;

                    switch (selectedPeriod) {
                        case "Tous":
                        	query = "SELECT SUM(stock * prix) AS valeur_totale_stock FROM produit where visible = 1";
                            break;
                        case "Salle":
                        	query = "SELECT SUM(stock * prix) AS valeur_totale_stock FROM produit where lieu = 'salle' and visible = 1";
                            break;
                        case "Cafétéria":
                        	query = "SELECT SUM(stock * prix) AS valeur_totale_stock FROM produit where lieu = 'cafeteria' and visible = 1";
                            break;
                    }
                	
                    try (Statement statement = connection.createStatement();
                         ResultSet resultSet = statement.executeQuery(query)) {

                        if (resultSet.next()) {
                            String valeurTotaleStock = resultSet.getString("valeur_totale_stock");
                            totalLabel.setText("Stock total : " + valeurTotaleStock+" €");
                        }

                    }
                    
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        };

        comboBox.addActionListener(changeTab);
        changeTab.actionPerformed(null);
    }
}