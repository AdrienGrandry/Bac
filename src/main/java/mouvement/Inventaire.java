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
    	super(parent, "Ajouter un mouvement", true);

        setSize(615, 700);
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        setResizable(false);

        final JPanel panneauPrincipal = new JPanel();
        panneauPrincipal.setBackground(Color.decode(color.xmlReader("background")));
        panneauPrincipal.setLayout(null);

        JLabel instructionCommentaire = new JLabel("Commentaire :");
        styleLabel(instructionCommentaire);
        instructionCommentaire.setBounds(25, 25, 400, 30);
        panneauPrincipal.add(instructionCommentaire);

        final JTextField commentaire = new JTextField("MODIFIER POUR METTRE LE TEXTE PREFAIT -  CHOIX");
        commentaire.setBounds(25, 50, 550, 30);
        styleTextField(commentaire);
        panneauPrincipal.add(commentaire);
        
        JLabel instructionType = new JLabel("Type :");
        styleLabel(instructionType);
        instructionType.setBounds(25, 115, 100, 30);
        panneauPrincipal.add(instructionType);

        String[] type = {"Entrée", "Sortie", "Commande"};
        final JComboBox<String> comboBoxType = new JComboBox<>(type);
        comboBoxType.setBounds(125, 115, 200, 30);
        Style.applyBoxStyle(comboBoxType);
        panneauPrincipal.add(comboBoxType);

        JLabel instructionBoisson = new JLabel("Boisson : ");
        styleLabel(instructionBoisson);
        instructionBoisson.setBounds(25, 180, 100, 30);
        panneauPrincipal.add(instructionBoisson);

        final JComboBox<Boisson> comboBox = new JComboBox<>();
        loadProduct(comboBox);
        comboBox.setBounds(125, 180, 150, 30);
        Style.applyBoxStyleBoisson(comboBox);
        panneauPrincipal.add(comboBox);

        JLabel instructionStock = new JLabel("Quantité : ");
        styleLabel(instructionStock);
        instructionStock.setBounds(300, 180, 100, 30);
        panneauPrincipal.add(instructionStock);

        final JTextField stock = new JTextField();
        styleTextField(stock);
        stock.setBounds(400, 180, 50, 30);
        panneauPrincipal.add(stock);

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
        scrollPane.setBounds(25, 250, 550, 275);
        panneauPrincipal.add(scrollPane);

        JButton boutonAjouter = new JButton("Ajouter");
        boutonAjouter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean valide = true;
            	Boisson boisson = (Boisson) comboBox.getSelectedItem(); // Récupérer l'objet Boisson sélectionné
                
                for (int i = 0; i < model.getRowCount(); i++) {
                    int id = (int) model.getValueAt(i, 0); // Récupérer l'ID (colonne 0)
                    
                    if(id == boisson.getId() && valide)
                    {
                    	valide = false;
                    	JOptionPane.showMessageDialog(
                                Inventaire.this, 
                                "Ce produit est déjà ajouté à la liste.", 
                                "Erreur Ajout", 
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
                if(valide)
                {
                    model.addRow(new Object[]{boisson.getId(), boisson, Integer.parseInt(stock.getText())});
                }
            }
        });
        boutonAjouter.setBounds(500, 180, 75, 30);
        Style.applyButtonStyle(boutonAjouter);
        boutonAjouter.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        panneauPrincipal.add(boutonAjouter);
        
        JButton boutonValider = new JButton("Valider");
        boutonValider.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
            	QueryResult queryResult = null;
            	int idMouvement = 0;
            	
        		try {
        			queryResult = Requete.executeQuery("INSERT INTO mouvement (type, description, valide) VALUES ('"
					        + comboBoxType.getSelectedItem() + "', '"
					        + commentaire.getText() + "', true);");
				} catch (SQLException e1) { }
        		finally {
		            if (queryResult != null) queryResult.close();
		        }
        		
                try {
					queryResult = Requete.executeQuery("SELECT MAX(id) as id FROM mouvement");
					ResultSet resultSet = queryResult.getResultSet();

					if(resultSet.next())
					{
						idMouvement = resultSet.getInt("id");
					}
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} finally {
		            if (queryResult != null) queryResult.close();
		        }
        		
            	for(int i=0; i < model.getRowCount(); i++)
            	{
            		int idProduit = (int) model.getValueAt(i, 0);
            		int quantite = (int) model.getValueAt(i, 2);
            		
            		try {
						Requete.executeQuery("INSERT INTO inclure (id_mouvement, id_produit, quantite) VALUES ("
						        + idMouvement + ", "
						        + idProduit + ", " + quantite + ")");
					} catch (SQLException e1) {
					} finally {
			            if (queryResult != null) queryResult.close();
			        }
            		
            		try {
            			if(comboBoxType.getSelectedItem().equals("Entrée"))
            			{
            				Requete.executeQuery("UPDATE produit set stock = stock+" + quantite + " where id = " + idProduit);
            			}
            			else if(comboBoxType.getSelectedItem().equals("Sortie"))
            			{
            				Requete.executeQuery("UPDATE produit set stock = stock-" + quantite + " where id = " + idProduit);
            			}
						
					} catch (SQLException e1) {
					} finally {
			            if (queryResult != null) queryResult.close();
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
    	
    	//ancien code
        /*super(parent, "Inventaire", true);

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
                return column == 3; // Seule la colonne "Compté" est éditable
            }
        };

        // Ajouter les colonnes "ID", "Boisson" et "Nombre"
        model.addColumn("ID");      // Colonne cachée
        model.addColumn("Boisson"); 
        model.addColumn("stock");
        model.addColumn("Compté");
        
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
        setLocationRelativeTo(parent);*/
    }

    private void loadProduct(JComboBox<Boisson> comboBox) throws SQLException {
        QueryResult queryResult = null;

        try {
            queryResult = Requete.executeQuery("SELECT id, libelle FROM Produit;");
            ResultSet resultSet = queryResult.getResultSet();

            while (resultSet.next()) {
                int idProduit = resultSet.getInt("id");
                String nom = resultSet.getString("libelle");

                Boisson boisson = new Boisson(idProduit, nom);
                listeBoissons.add(boisson);

                comboBox.addItem(boisson); // Ajouter directement l'objet Boisson
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (queryResult != null) queryResult.close();
        }
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