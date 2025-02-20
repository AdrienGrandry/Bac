package mouvement;

import ressources.ColorXml;
import style.Style;
import database.QueryResult;
import database.Requete;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

public class Inventaire extends JDialog
{
    private static final long serialVersionUID = 1L;
    ColorXml color = new ColorXml();
    ArrayList<Boisson> listeBoissons = new ArrayList<>();

    public Inventaire(JFrame parent) throws SQLException
    {
        super(parent, "Inventaire", true);

        setSize(615, 700);
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        setResizable(false);

        final JPanel panneauPrincipal = new JPanel();
        panneauPrincipal.setBackground(Color.decode(color.xmlReader("background")));
        panneauPrincipal.setLayout(null);

        JButton boutonFermer = new JButton("Fermer");
        boutonFermer.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                dispose();
            }
        });
        boutonFermer.setBounds(25, 590, 100, 40);
        Style.applyButtonStyle(boutonFermer);
        boutonFermer.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        panneauPrincipal.add(boutonFermer);
        
        final DefaultTableModel model = new DefaultTableModel()
        {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2; // Seule la colonne "Nombre" est éditable
            }
        };

        // Ajouter les colonnes "ID", "Boisson" et "Nombre"
        model.addColumn("ID");      // Colonne cachée
        model.addColumn("Boisson"); 
        model.addColumn("Nombre");
        
        QueryResult queryResult = null;
        try {
			queryResult = Requete.executeQuery("SELECT id, libelle, stock FROM produit ORDER BY numero");
			ResultSet resultSet = queryResult.getResultSet();
			
			while(resultSet.next())
			{
		        model.addRow(new Object[]{resultSet.getInt("id"), resultSet.getString("libelle"), resultSet.getInt("stock")});
			}
			
		} catch (SQLException e1) { }
		finally {
            if (queryResult != null) queryResult.close();
        }
        
        JTable table = new JTable(model)
        {
            private static final long serialVersionUID = 1L;
            
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component component = super.prepareRenderer(renderer, row, column);
                if (row % 2 == 0) {
                    component.setBackground(Color.decode(color.xmlReader("background_tab_pair")));
                    component.setForeground(Color.decode(color.xmlReader("foreground")));
                } else {
                    component.setBackground(Color.decode(color.xmlReader("background_tab_impair")));
                    component.setForeground(Color.decode(color.xmlReader("foreground")));
                }
                return component;
            }
        };

        // Masquer la colonne "ID" (colonne 0)
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setPreferredWidth(0);

        // Désactiver le redimensionnement des autres colonnes
        for (int i = 1; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setResizable(false);
        }

        // Style de l'en-tête du tableau
        table.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel headerLabel = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                headerLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                headerLabel.setForeground(Color.decode(color.xmlReader("foreground")));
                headerLabel.setBackground(Color.decode(color.xmlReader("background_tab")));
                headerLabel.setHorizontalAlignment(CENTER);
                return headerLabel;
            }
        });

        // Empêcher le réarrangement des colonnes
        table.getTableHeader().setReorderingAllowed(false);

        // Ajouter le tableau à un JScrollPane
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(25, 50, 550, 475);
        panneauPrincipal.add(scrollPane);
        
        JButton boutonValider = new JButton("Valider");
        boutonValider.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
            	QueryResult queryResult = null;
            	boolean isCreate = false;
            	
            	for(int i=0; i < model.getRowCount(); i++)
            	{
            		int idProduit = (int) model.getValueAt(i, 0);
            		int quantite = Integer.parseInt(model.getValueAt(i, 2).toString());
            		int mouvementStock = 0;
            		
            		QueryResult queryResult1 = null;
            		try {
						queryResult1 = Requete.executeQuery("Select stock from produit where id = " + idProduit);
	            		ResultSet resultSet = queryResult1.getResultSet();
	            		if(resultSet.next())
	            		{
	            			mouvementStock = resultSet.getInt("stock");
	            		}
	            		mouvementStock = quantite - mouvementStock;
            		} catch (SQLException e1) {

            		} finally {
			            if (queryResult1 != null) queryResult1.close();
			        }
            		if(mouvementStock != 0)
            		{
            			if(!isCreate)
                		{
                			try
                			{
                                String formattedDate = ZonedDateTime.now(ZoneId.of("Europe/Brussels")).format(DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.FRENCH));
                                
                    			queryResult = Requete.executeQuery("INSERT INTO mouvement (type, description, valide) VALUES ('Inventaire'," + "'Inventaire du " + formattedDate + "', true);");
            				} catch (SQLException e1) {
            				}
                    		finally {
            		            if (queryResult != null) queryResult.close();
            		        }
                			isCreate = true;
                		}
            			
            			int idMouvement = 0;
	            		QueryResult queryResult11 = null;
	            		try {
							queryResult11 = Requete.executeQuery("Select MAX(id) from mouvement");
		            		ResultSet resultSet1 = queryResult11.getResultSet();
		            		if(resultSet1.next())
		            		{
		            			idMouvement = resultSet1.getInt("MAX(id)");
		            		}
	            		} catch (SQLException e1) {
							e1.printStackTrace();
	            		} finally {
				            if (queryResult11 != null) queryResult11.close();
				        }
	            		
	            		try {
							Requete.executeQuery("INSERT INTO inclure (id_mouvement, id_produit, quantite) VALUES ("
							        + idMouvement + ", "
							        + idProduit + ", " + mouvementStock + ")");
						} catch (SQLException e1) {
						} finally {
				            if (queryResult11 != null) queryResult11.close();
				        }
	            		
	            		try
	            		{
	            			System.out.println("UPDATE produit set stock = stock+" + mouvementStock + " where id = " + idProduit);
	        				Requete.executeQuery("UPDATE produit set stock = stock+" + mouvementStock + " where id = " + idProduit);
							
						} catch (SQLException e1) {
						} finally {
				            if (queryResult11 != null) queryResult11.close();
				        }
            		}
                }
            	dispose();
            }
        });
        boutonValider.setBounds(475, 590, 100, 40);
        Style.applyButtonStyle(boutonValider);
        boutonValider.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        panneauPrincipal.add(boutonValider);

        add(panneauPrincipal, BorderLayout.CENTER);
        setLocationRelativeTo(parent);
    }

	public void styleLabel(JLabel label)
    {
        label.setFont(new Font("Arial", Font.BOLD, 18));
        label.setForeground(Color.decode(color.xmlReader("foreground")));
    }

    public void styleTextField(JTextField textField)
    {
        textField.setFont(new Font("Arial", Font.PLAIN, 16));
        textField.setBorder(BorderFactory.createLineBorder(Color.BLACK));
    }
}