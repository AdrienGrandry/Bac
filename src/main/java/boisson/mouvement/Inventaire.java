package boisson.mouvement;

import options.ColorXml;
import ressources.Boisson;
import ressources.Message;
import ressources.Style;
import ressources.dataBase.QueryResult;
import ressources.dataBase.Requete;

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
	final JComboBox<Boisson> comboBox = new JComboBox<>();
	final DefaultTableModel model = new DefaultTableModel()
	{
		private static final long serialVersionUID = 1L;

		@Override
		public boolean isCellEditable(int row, int column)
		{
			return column == 2; // Seule la colonne "Nombre" est éditable
		}
	};
	final JLabel instructionStock = new JLabel("Quantité : ");
	final JTextField stock = new JTextField();
	String formattedDate = ZonedDateTime.now(ZoneId.of("Europe/Brussels")).format(DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.FRENCH));
	final JTextField commentaire = new JTextField("Inventaire du " + formattedDate);

	public Inventaire(JFrame parent) throws SQLException
	{
		super(parent, "Ajouter un mouvement", true);
		afficheOnglet(parent);
	}

	private void afficheOnglet (JFrame parent) throws SQLException
	{
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

		//texte comme dans les exemples
		commentaire.setBounds(25, 50, 550, 30);
		styleTextField(commentaire);
		panneauPrincipal.add(commentaire);

		JLabel instructionBoisson = new JLabel("Boisson : ");
		styleLabel(instructionBoisson);
		instructionBoisson.setBounds(25, 115, 100, 30);
		panneauPrincipal.add(instructionBoisson);

		loadProduct(comboBox);
		comboBox.setBounds(125, 115, 150, 30);
		Style.applyBoxStyleBoisson(comboBox);
		panneauPrincipal.add(comboBox);

		styleLabel(instructionStock);
		instructionStock.setBounds(300, 115, 100, 30);
		panneauPrincipal.add(instructionStock);

		styleTextField(stock);
		stock.setBounds(400, 115, 50, 30);
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

		// Ajouter les colonnes "ID", "Boisson" et "Nombre"
		model.addColumn("ID"); // Colonne cachée
		model.addColumn("Boisson");
		model.addColumn("Nombre");

		JTable table = new JTable(model)
		{
			private static final long serialVersionUID = 1L;

			@Override
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column)
			{
				Component component = super.prepareRenderer(renderer, row, column);
				if (row % 2 == 0)
				{
					component.setBackground(Color.decode(color.xmlReader("background_tab_pair")));
					component.setForeground(Color.decode(color.xmlReader("foreground")));
				} else
				{
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
		for (int i = 1; i < table.getColumnCount(); i++)
		{
			table.getColumnModel().getColumn(i).setResizable(false);
		}

		// Style de l'en-tête du tableau
		table.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer()
		{
			private static final long serialVersionUID = 1L;

			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
														   boolean hasFocus, int row, int column)
			{
				JLabel headerLabel = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
						row, column);
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
		scrollPane.setBounds(25, 180, 550, 345);
		panneauPrincipal.add(scrollPane);

		JButton boutonAjouter = new JButton("Ajouter");
		boutonAjouter.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				ajouterBoissons();
			}
		});
		boutonAjouter.setBounds(500, 115, 75, 30);
		Style.applyButtonStyle(boutonAjouter);
		boutonAjouter.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		panneauPrincipal.add(boutonAjouter);

		JButton boutonValider = new JButton("Valider");
		boutonValider.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				validerInventaire();
			}
		});

		boutonValider.setBounds(475, 590, 100, 40);
		Style.applyButtonStyle(boutonValider);
		boutonValider.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		panneauPrincipal.add(boutonValider);

		add(panneauPrincipal, BorderLayout.CENTER);
		setLocationRelativeTo(parent);
	}

	private void ajouterBoissons ()
	{
		boolean valide = true;
		Boisson boisson = (Boisson) comboBox.getSelectedItem(); // Récupérer l'objet Boisson sélectionné

		for (int i = 0; i < model.getRowCount(); i++)
		{
			int id = (int) model.getValueAt(i, 0); // Récupérer l'ID (colonne 0)

			if (id == boisson.getId() && valide)
			{
				valide = false;
				JOptionPane.showMessageDialog(Inventaire.this, "Ce produit est déjà ajouté à la liste.",
						"Erreur Ajout", JOptionPane.ERROR_MESSAGE);
			}
		}
		if (valide)
		{
			model.addRow(new Object[]
					{ boisson.getId(), boisson, Integer.parseInt(stock.getText()) });
		}
	}

	private void validerInventaire()
	{
		int idProduit;
		int quantite;
		int mouvementStock;
		boolean isCreate = false;

		for(int i=0; i < model.getRowCount(); i++)
		{
			idProduit = (int)model.getValueAt(i, 0);
			quantite = Integer.parseInt(model.getValueAt(i,2).toString());
			mouvementStock = 0;

			QueryResult queryResult = null;
			try {
				queryResult = Requete.executeQuery("Select stock from produit where id = " + idProduit);
				ResultSet resultSet = queryResult.getResultSet();
				if (resultSet.next()) {
					mouvementStock = resultSet.getInt("stock");
				}
				mouvementStock = quantite - mouvementStock;
			} catch (SQLException e1) {
				Message.showErrorMessage("Erreur Inventaire", e1.getMessage());
			} finally {
				if (queryResult != null)
				{
					queryResult.close();
				}
			}
			queryResult = null;
			if (mouvementStock != 0) {
				if (!isCreate) {
					try {
						queryResult = Requete.executeQuery("INSERT INTO mouvement (type, description, valide) VALUES ('Inventaire', '"
										+ commentaire.getText() + "', true);");
					} finally {
						if (queryResult != null)
						{
							queryResult.close();
						}
						isCreate = true;
					}
				}

				int idMouvement = 0;
				QueryResult queryResult11 = null;
				try {
					queryResult11 =
							Requete.executeQuery("Select MAX(id) from mouvement");
					ResultSet resultSet1 =
							queryResult11.getResultSet();
					if (resultSet1.next()) {
						idMouvement =
								resultSet1.getInt("MAX(id)");
					}
				} catch (SQLException e1) {
					Message.showErrorMessage("Erreur Inventaire", "Erreur lors de l'association du mouvement : " + e1.getMessage());
				} finally {
					if (queryResult11 != null)
						queryResult11.close();
				}

				try {
					Requete.executeQuery("INSERT INTO inclure (id_mouvement, id_produit, quantite) VALUES ("
									+ idMouvement + ", " + idProduit + ", " + mouvementStock + ")");
				} finally {
					if (queryResult11 != null)
						queryResult11.close();
				}

				try {
					Requete.executeQuery("UPDATE produit set stock = stock+" + mouvementStock +
							" where id = " + idProduit);

				} finally {
					if (queryResult11 != null)
						queryResult11.close();
				}
			}
		}
		dispose();
	}

	private void loadProduct(JComboBox<Boisson> comboBox) throws SQLException
	{
		QueryResult queryResult = null;

		try
		{
			queryResult = Requete.executeQuery("SELECT id, libelle FROM Produit;");
			ResultSet resultSet = queryResult.getResultSet();

			while (resultSet.next())
			{
				int idProduit = resultSet.getInt("id");
				String nom = resultSet.getString("libelle");

				Boisson boisson = new Boisson(idProduit, nom);
				listeBoissons.add(boisson);

				comboBox.addItem(boisson); // Ajouter directement l'objet Boisson
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			if (queryResult != null)
				queryResult.close();
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