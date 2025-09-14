package location.manageLocation;

import location.Location;
import ressources.dataBase.JPanelLocationRequete;
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

import static ressources.DateParser.parseDateToIsoString;

public class showLocation extends JPanel
{
    private static final long serialVersionUID = 1L;
    private ActionListener changeTab;

    public showLocation(final JFrame parentFrame)
    {
        final ColorXml color = new ColorXml();

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
                { "Tous", "Futures", "Cloturées" };

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
                        sql = "SELECT Nom, Prenom, strftime('%d/%m/%Y', Date) AS Date, CASE WHEN location.IdSalle IS NOT NULL AND location.idCafeteria IS NULL THEN 'Salle' WHEN location.IdSalle IS NULL AND location.idCafeteria IS NOT NULL THEN 'Cafétéria' WHEN location.IdSalle IS NOT NULL AND location.idCafeteria IS NOT NULL THEN 'Salle + Cafétéria' ELSE 'Rien' END AS Lieu, option AS Options, CafeteriaSeule AS 'Cafétéria seule', Cuisine AS 'Cuisine', Reunion AS 'Réunion', Cafeteria.BarAsbl AS 'Boissons fournies par l’ASBL (Cafétéria)', Cafeteria.BarVide AS 'Boissons non fournies par l’ASBL (Cafétéria)', Projecteur AS 'Projecteur', SalleSeul AS 'Salle seule', Cuisine160 AS 'Cuisine pour 160 personnes', Cuisine240 AS 'Cuisine pour 240 personnes', Salle.BarVide AS 'Boissons non fournies par l’ASBL (Salle)', Salle.BarAsbl AS 'Boissons fournies par l’ASBL (Salle)', PompeVide AS 'Pompes à bière sans les fûts' FROM location LEFT JOIN Salle ON location.IdSalle = salle.idSalle LEFT JOIN Cafeteria ON location.IdCafeteria = Cafeteria.idCafeteria ORDER BY option DESC, Cloturee ASC, Date;";
                        break;
                    case "Futures":
                        sql = "SELECT Nom, Prenom, strftime('%d/%m/%Y', Date) AS Date, CASE WHEN location.IdSalle IS NOT NULL AND location.idCafeteria IS NULL THEN 'Salle' WHEN location.IdSalle IS NULL AND location.idCafeteria IS NOT NULL THEN 'Cafétéria' WHEN location.IdSalle IS NOT NULL AND location.idCafeteria IS NOT NULL THEN 'Salle + Cafétéria' ELSE 'Rien' END AS Lieu, option AS Options, CafeteriaSeule AS 'Cafétéria seule', Cuisine AS 'Cuisine', Reunion AS 'Réunion', Cafeteria.BarAsbl AS 'Boissons fournies par l’ASBL (Cafétéria)', Cafeteria.BarVide AS 'Boissons non fournies par l’ASBL (Cafétéria)', Projecteur AS 'Projecteur', SalleSeul AS 'Salle seule', Cuisine160 AS 'Cuisine pour 160 personnes', Cuisine240 AS 'Cuisine pour 240 personnes', Salle.BarVide AS 'Boissons non fournies par l’ASBL (Salle)', Salle.BarAsbl AS 'Boissons fournies par l’ASBL (Salle)', PompeVide AS 'Pompes à bière sans les fûts' FROM location LEFT JOIN Salle ON location.IdSalle = salle.idSalle LEFT JOIN Cafeteria ON location.IdCafeteria = Cafeteria.idCafeteria WHERE Cloturee=0 ORDER BY option DESC, Date;";

                        break;
                    case "Cloturées":
                        sql = "SELECT Nom, Prenom, strftime('%d/%m/%Y', Date) AS Date, CASE WHEN location.IdSalle IS NOT NULL AND location.idCafeteria IS NULL THEN 'Salle' WHEN location.IdSalle IS NULL AND location.idCafeteria IS NOT NULL THEN 'Cafétéria' WHEN location.IdSalle IS NOT NULL AND location.idCafeteria IS NOT NULL THEN 'Salle + Cafétéria' ELSE 'Rien' END AS Lieu, option AS Options, CafeteriaSeule AS 'Cafétéria seule', Cuisine AS 'Cuisine', Reunion AS 'Réunion', Cafeteria.BarAsbl AS 'Boissons fournies par l’ASBL (Cafétéria)', Cafeteria.BarVide AS 'Boissons non fournies par l’ASBL (Cafétéria)', Projecteur AS 'Projecteur', SalleSeul AS 'Salle seule', Cuisine160 AS 'Cuisine pour 160 personnes', Cuisine240 AS 'Cuisine pour 240 personnes', Salle.BarVide AS 'Boissons non fournies par l’ASBL (Salle)', Salle.BarAsbl AS 'Boissons fournies par l’ASBL (Salle)', PompeVide AS 'Pompes à bière sans les fûts' FROM location LEFT JOIN Salle ON location.IdSalle = salle.idSalle LEFT JOIN Cafeteria ON location.IdCafeteria = Cafeteria.idCafeteria WHERE Cloturee=1 ORDER BY option DESC, Date;";
                        break;
                }

                try
                {
                    tableauPanel.removeAll();
                    JPanel tab = null;
                    tab = JPanelLocationRequete.executeQueryAndReturnPanel(sql, tableauPanel.getHeight(), tableauPanel.getWidth());
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
                                String NomLocation = model.getValueAt(row, 0).toString();
                                String PrenomLocation = model.getValueAt(row, 1).toString();
                                String DateLocation = model.getValueAt(row, 2).toString();

                                int id = 0;
                                try
                                {
                                    id = getIdRow(PrenomLocation, NomLocation, DateLocation);
                                } catch (Exception e1)
                                {
                                    e1.printStackTrace();
                                }

                                detailLocation panel = new detailLocation(parentFrame, id);

                                Location.panel.removeAll();
                                Location.panel.add(panel, BorderLayout.CENTER);
                                parentFrame.revalidate();
                                parentFrame.repaint();
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

    public int getIdRow(String Prenom, String Nom, String Date) throws SQLException
    {
        String query = "SELECT idlocation FROM location WHERE prenom = '" + Prenom + "' AND nom = '" + Nom
                + "' AND date = '" + parseDateToIsoString(Date) + "'";

        QueryResult queryResult = null;
        int id = 0;

        try
        {
            queryResult = Requete.executeQuery(query);
            ResultSet resultSet = queryResult.getResultSet();

            if (resultSet.next())
            {
                id = resultSet.getInt("idLocation");
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