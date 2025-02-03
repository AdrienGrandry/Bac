package produit;

import database.Requete;
import style.Style;
import ressources.ColorXml;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Produit extends JPanel
{
    private static final long serialVersionUID = 1L;
    private ActionListener changeTab;

    public Produit(final JFrame parentFrame)
    {
        final ColorXml color = new ColorXml();
        final Requete requete = new Requete();

        setBackground(Color.decode(color.xmlReader("background")));
        setLayout(new BorderLayout(10, 10));

        JPanel entete = new JPanel();
        entete.setBackground(Color.decode(color.xmlReader("background")));
        entete.setLayout(new BorderLayout(10, 10));

        JLabel titre = new JLabel("Produit", SwingConstants.CENTER);
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

        JButton button = new JButton("AJOUTER UN PRODUIT");
        Style.applyButtonStyle(button);
        button.setPreferredSize(new Dimension(250, 40));
        button.setMaximumSize(new Dimension(250, 40));
        button.setMinimumSize(new Dimension(250, 40));
        button.setBorder(BorderFactory.createLineBorder(Color.black));

        buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(comboBox);
        buttonPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        buttonPanel.add(button);
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
                String selectedPeriod = (String) comboBox.getSelectedItem();
                String sql = null;

                switch (selectedPeriod)
                {
                    case "Tous":
                        sql = "SELECT numero AS 'Numéro', libelle AS 'Libellé', lieu AS 'Lieu' FROM produit ORDER BY numero";
                        break;
                    case "Salle":
                        sql = "SELECT numero AS 'Numéro', libelle AS 'Libellé', lieu AS 'Lieu' FROM produit WHERE lieu LIKE 'salle' ORDER BY numero";
                        break;
                    case "Cafétéria":
                        sql = "SELECT numero AS 'Numéro', libelle AS 'Libellé', lieu AS 'Lieu' FROM produit WHERE lieu LIKE 'cafeteria' ORDER BY numero";
                        break;
                }

                tableauPanel.removeAll(); 
                JPanel tab = requete.executeQueryAndReturnPanel(sql, tableauPanel.getHeight(), tableauPanel.getWidth(), "pair_impair");
                final JTable table = (JTable) ((JScrollPane) tab.getComponent(0)).getViewport().getView();

                table.addMouseListener(new MouseAdapter()
                {
                    @Override
                    public void mouseClicked(MouseEvent e)
                    {
                        int row = table.rowAtPoint(e.getPoint());
                        if (row >= 0) {
                            TableModel model = table.getModel();
                            String numeroProduitStr = model.getValueAt(row, 0).toString();
                            String libelleProduit = model.getValueAt(row, 1).toString();
                            String lieuProduit = model.getValueAt(row, 2).toString();

                            int numeroProduit = Integer.parseInt(numeroProduitStr);
                            int id = getIdRow(numeroProduit, libelleProduit, lieuProduit);

                            AddProduit fenetreProduit = new AddProduit(parentFrame, id);
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
                    }
                });

                tableauPanel.add(tab);
                revalidate();
                repaint();
            }
        };

        comboBox.addActionListener(changeTab);
        changeTab.actionPerformed(null);
       
        button.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                AddProduit fenetreProduit = new AddProduit(parentFrame, 0);
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
    }

    public int getIdRow(int numeroProduit, String libelleProduit, String lieuProduit)
    {
        int id = -1;
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:Database.db"))
        {
            String query = "SELECT id FROM produit WHERE numero = ? AND libelle = ? AND lieu = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query))
            {
                preparedStatement.setInt(1, numeroProduit);
                preparedStatement.setString(2, libelleProduit);
                preparedStatement.setString(3, lieuProduit);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next())
                {
                    id = resultSet.getInt("id");
                }
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return id;
    }
}