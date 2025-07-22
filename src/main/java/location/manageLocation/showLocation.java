package location.manageLocation;

import boisson.produit.AddProduit;
import ressources.dataBase.QueryResult;
import ressources.dataBase.Requete;
import ressources.Style;
import options.ColorXml;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.SQLException;

public class showLocation extends JPanel
{
    private static final long serialVersionUID = 1L;
    private ActionListener changeTab;

    public showLocation(final JFrame parentFrame)
    {
        final ColorXml color = new ColorXml();
        final Requete requete = new Requete();

        setBackground(Color.decode(color.xmlReader("background")));
        setLayout(new BorderLayout(10, 10));

        JPanel entete = new JPanel();
        entete.setBackground(Color.decode(color.xmlReader("background")));
        entete.setLayout(new BorderLayout(10, 10));

        JLabel titre = new JLabel("Gérer Location", SwingConstants.CENTER);
        titre.setFont(new Font("Arial", Font.BOLD, 40));
        titre.setForeground(Color.decode(color.xmlReader("foreground")));
        titre.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.decode(color.xmlReader("background")));
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

        String[] periods =
                { "Tous", "En cours", "Cloturée" };

        final JComboBox<String> comboBox = new JComboBox<>(periods);
        Style.applyBoxStyle(comboBox);
        comboBox.setPreferredSize(new Dimension(250, 40));
        comboBox.setMaximumSize(new Dimension(250, 40));
        comboBox.setMinimumSize(new Dimension(250, 40));

        buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(comboBox);
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
                        sql = "select Nom, Prenom, Date, case when location.IdSalle is not NULL AND  location.idCafeteria is NULL Then 'Salle' When location.IdSalle is NULL AND location.idCafeteria is not NULL Then 'Cafétéria' When location.IdSalle is not NULL AND location.idCafeteria is not NULL Then 'Salle + Cafétéria' else 'Rien' end as Lieu, case When salle.BarAsbl = 1 or Cafeteria.BarAsbl = 1 Then 'Boisson' else '' end as Boisson from location left join Salle on location.IdSalle = salle.idSalle left join Cafeteria on location.IdCafeteria = Cafeteria.idCafeteria ORDER BY Date";
                        break;
                    case "En cours":
                        sql = "select Nom, Prenom, Date, case when location.IdSalle is not NULL AND  location.idCafeteria is NULL Then 'Salle' When location.IdSalle is NULL AND location.idCafeteria is not NULL Then 'Cafétéria' When location.IdSalle is not NULL AND location.idCafeteria is not NULL Then 'Salle + Cafétéria' else 'Rien' end as Lieu, case When salle.BarAsbl = 1 or Cafeteria.BarAsbl = 1 Then 'Boisson' else '' end as Boisson from location left join Salle on location.IdSalle = salle.idSalle left join Cafeteria on location.IdCafeteria = Cafeteria.idCafeteria ORDER BY Date";
                        break;
                    case "Cloturée":
                        sql = "select Nom, Prenom, Date, case when location.IdSalle is not NULL AND  location.idCafeteria is NULL Then 'Salle' When location.IdSalle is NULL AND location.idCafeteria is not NULL Then 'Cafétéria' When location.IdSalle is not NULL AND location.idCafeteria is not NULL Then 'Salle + Cafétéria' else 'Rien' end as Lieu, case When salle.BarAsbl = 1 or Cafeteria.BarAsbl = 1 Then 'Boisson' else '' end as Boisson from location left join Salle on location.IdSalle = salle.idSalle left join Cafeteria on location.IdCafeteria = Cafeteria.idCafeteria ORDER BY Date";
                        break;
                }

                try
                {
                    tableauPanel.removeAll();
                    JPanel tab = null;
                    tab = requete.executeQueryAndReturnPanel(sql, tableauPanel.getHeight(), tableauPanel.getWidth(),
                            "lieuLocation");
                    final JTable table = (JTable) ((JScrollPane) tab.getComponent(0)).getViewport().getView();

                    table.addMouseListener(new MouseAdapter()
                    {
                        @Override
                        public void mouseClicked(MouseEvent e)
                        {
                            int row = table.rowAtPoint(e.getPoint());
                            if (row >= 0)
                            {
                                TableModel model = table.getModel();
                                String numeroProduitStr = model.getValueAt(row, 0).toString();
                                String libelleProduit = model.getValueAt(row, 1).toString();
                                String lieuProduit = model.getValueAt(row, 2).toString();

                                int numeroProduit = Integer.parseInt(numeroProduitStr);
                                int id = 0;
                                try
                                {
                                    id = getIdRow(numeroProduit, libelleProduit, lieuProduit);
                                } catch (Exception e1)
                                {
                                    e1.printStackTrace();
                                }

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
                catch(Exception e2) {}
            }
        };

        comboBox.addActionListener(changeTab);
        changeTab.actionPerformed(null);
    }

    public int getIdRow(int numeroProduit, String libelleProduit, String lieuProduit) throws SQLException
    {
        String query = "SELECT id FROM produit WHERE numero = " + numeroProduit + " AND libelle = '" + libelleProduit
                + "' AND lieu = '" + lieuProduit + "'";

        QueryResult queryResult = null;
        int id = 0;

        try
        {
            queryResult = Requete.executeQuery(query);
            ResultSet resultSet = queryResult.getResultSet();

            if (resultSet.next())
            {
                id = resultSet.getInt("id");
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        if (queryResult != null)
            queryResult.close();

        return id;
    }
}