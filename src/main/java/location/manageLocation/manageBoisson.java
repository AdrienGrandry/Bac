package location.manageLocation;

import ressources.Boisson;
import options.ColorXml;
import ressources.Message;
import ressources.Style;
import ressources.dataBase.QueryResult;
import ressources.dataBase.Requete;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.List;

public class manageBoisson extends JDialog {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(manageBoisson.class.getName());
	private final ColorXml color = new ColorXml();
	private final ArrayList<Boisson> listeBoissons = new ArrayList<>();
	private final DefaultTableModel tableModel;
	private JTable table = null;
	private int idSalle = 0, idCafet = 0;
	private ValidationListener validationListener;
	private List<Integer> idBoisson = new ArrayList<>();

	public manageBoisson(JFrame parent, int idSalleParam, int idCafetParma) throws SQLException {
		super(parent, "Ajouter un mouvement", true);
		setSize(615, 700);
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setResizable(false);

		idSalle = idSalleParam;
		idCafet = idCafetParma;
		tableModel = createTableModel(idSalle, idCafet);
		table = createTable();

		JPanel panneauPrincipal = createMainPanel();
		add(panneauPrincipal, BorderLayout.CENTER);
		setLocationRelativeTo(parent);
	}

	/* ===================== PANNEAU PRINCIPAL ===================== */

	private JPanel createMainPanel() throws SQLException {
		JPanel panel = new JPanel();
		panel.setBackground(Color.decode(color.xmlReader("background")));
		panel.setLayout(null);

		// Section: Produit
		JLabel instructionBoisson = createLabel("Boisson :", 25, 50, 100, 30);
		panel.add(instructionBoisson);

		JComboBox<Boisson> comboBoxBoisson = new JComboBox<>();
		loadProducts(comboBoxBoisson);
		comboBoxBoisson.setBounds(125, 50, 150, 30);
		Style.applyBoxStyleBoisson(comboBoxBoisson);
		panel.add(comboBoxBoisson);

		JLabel instructionStock = createLabel("Quantité :", 300, 50, 100, 30);
		panel.add(instructionStock);

		JTextField stockField = createTextField(400, 50, 50, 30);
		panel.add(stockField);

		JButton boutonAjouter = createButton("Ajouter", 500, 50, 75, 30,
				e -> addProduct(comboBoxBoisson, stockField));
		panel.add(boutonAjouter);

		// Section: Tableau des produits

		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setBounds(25, 150, 550, 375);
		panel.add(scrollPane);

		// Section: Boutons principaux
		JButton boutonDelete = createButton("Suprimer boisson", 25, 590, 200, 40, e -> deleteBoison());
		panel.add(boutonDelete);

		JButton boutonValider = createButton("Valider", 375, 590, 200, 40, e -> validateData());
		panel.add(boutonValider);

		return panel;
	}

	/* ===================== CRÉATION UI ===================== */

	private JLabel createLabel(String text, int x, int y, int width, int height) {
		JLabel label = new JLabel(text);
		label.setFont(new Font("Arial", Font.BOLD, 18));
		label.setForeground(Color.decode(color.xmlReader("foreground")));
		label.setBounds(x, y, width, height);
		return label;
	}

	private JTextField createTextField(int x, int y, int width, int height) {
		JTextField textField = new JTextField();
		textField.setFont(new Font("Arial", Font.PLAIN, 16));
		textField.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		textField.setBounds(x, y, width, height);
		return textField;
	}

	private JButton createButton(String text, int x, int y, int width, int height, ActionListener action) {
		JButton button = new JButton(text);
		button.setBounds(x, y, width, height);
		button.addActionListener(action);
		Style.applyButtonStyle(button);
		button.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		return button;
	}

	/* ===================== TABLEAU ===================== */

	private JTable createTable() {
		JTable table = new JTable(tableModel) {
			@Override
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
				Component component = super.prepareRenderer(renderer, row, column);

				if (isRowSelected(row))
				{
					component.setBackground(getSelectionBackground().darker());
					component.setForeground(Color.white);
				} else
				{
					if (row % 2 == 0) {
						component.setBackground(Color.decode(color.xmlReader("background_tab_pair")));
					} else {
						component.setBackground(Color.decode(color.xmlReader("background_tab_impair")));
					}
					component.setForeground(Color.decode(color.xmlReader("foreground")));
				}

				return component;
			}
		};

		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getTableHeader().setDefaultRenderer(createHeaderRenderer());
		table.getTableHeader().setReorderingAllowed(false);

		// Masquer la colonne ID
		table.getColumnModel().getColumn(0).setMinWidth(0);
		table.getColumnModel().getColumn(0).setMaxWidth(0);
		table.getColumnModel().getColumn(0).setPreferredWidth(0);

		setupTableListeners();
		return table;
	}

	private DefaultTableCellRenderer createHeaderRenderer() {
		return new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value,
														   boolean isSelected, boolean hasFocus,
														   int row, int column) {
				JLabel headerLabel = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				headerLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
				headerLabel.setForeground(Color.decode(color.xmlReader("foreground")));
				headerLabel.setBackground(Color.decode(color.xmlReader("background_tab")));
				headerLabel.setHorizontalAlignment(CENTER);
				return headerLabel;
			}
		};
	}

	private DefaultTableModel createTableModel(int idSalle, int idCafet) {
		String[] columnNames = {"ID", "Boisson", "Fourni", "Repris", "Consommé"};

		// Création du modèle avec colonnes
		DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return column == 2 || column == 3; // Seules "Fourni" et "Repris" sont éditables
			}
		};

		QueryResult queryResult = null;
		try {
			queryResult = Requete.executeQuery("SELECT IdBoisson, libelle, Fournie, Reprise FROM MouvementLocation " +
					"INNER JOIN produit ON produit.id = MouvementLocation.IdBoisson " +
					"WHERE IdSalle = " + idSalle + " AND IdCafeteria = " + idCafet);
			while (queryResult.getResultSet().next())
			{
				int id = queryResult.getResultSet().getInt(1);
				String nom = queryResult.getResultSet().getString(2);
				int fourni = queryResult.getResultSet().getInt(3);
				int repris = queryResult.getResultSet().getInt(4);

				if (repris == -1)
				{
					model.addRow(new Object[]{id, nom, fourni, "", 0});
				}
				else
				{
					model.addRow(new Object[]{id, nom, fourni, repris, fourni-repris});
				}
			}
		} catch (Exception ex) {
			Message.showErrorMessage("Erreur de la base de données", ex.getMessage());
		}
		finally {
			if (queryResult != null) {
				queryResult.close();
			}
		}

		return model;
	}

	/* ===================== LOGIQUE TABLEAU ===================== */

	private void setupTableListeners() {
		tableModel.addTableModelListener(e -> {
			int row = e.getFirstRow();
			int col = e.getColumn();

			if (col == 2 || col == 3) {
				calculateConsomme(row);
			}
		});
	}

	private void calculateConsomme(int row) {
		try
		{
			if(tableModel.getValueAt(row, 3).equals(""))
			{
				tableModel.setValueAt(0, row, 4);
			}
			else
			{
				int fourni = parseIntSafe(tableModel.getValueAt(row, 2));
				int repris = parseIntSafe(tableModel.getValueAt(row, 3));

				int consomme = fourni - repris;

				if (consomme < 0) {
					Message.showErrorMessage("Erreur", "La quantité consommée ne peut pas être négative !");
					tableModel.setValueAt(fourni, row, 3); // force Repris = Fourni
					consomme = 0;
				}

				tableModel.setValueAt(consomme, row, 4);
			}
		} catch (Exception ex) {
			logger.warning("Erreur lors du calcul consommé : " + ex.getMessage());
		}
	}

	private int parseIntSafe(Object value) {
		if (value == null) return 0;
		if (value instanceof Number) return ((Number) value).intValue();
		try {
			return Integer.parseInt(value.toString().trim());
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	private void deleteBoison() {
		int ligne = table.getSelectedRow();
		if(ligne != -1)
		{
			QueryResult queryResult = null;
			try
			{
				queryResult = Requete.executeQuery("SELECT count(*) from MouvementLocation where IdSalle = "
						+ idSalle + " AND IdCafeteria = " + idCafet + " AND IdBoisson = "
						+ tableModel.getValueAt(ligne, 0));

				if(queryResult.getResultSet().next())
				{
					if(queryResult.getResultSet().getInt(1) == 1) {
						idBoisson.add((int) tableModel.getValueAt(ligne, 0));
					}
				}
			}
			catch (Exception e)
			{
				Message.showErrorMessage("Erreur sql", "Erreur lors de la suppression de la boisson !");
			}
			finally {
				if(queryResult != null)
				{
					queryResult.close();
				}
			}

			tableModel.removeRow(ligne);
		}
	}

	/* ===================== BASE DE DONNÉES ===================== */

	private void loadProducts(JComboBox<Boisson> comboBox) {
		try (QueryResult queryResult = Requete.executeQuery("SELECT id, libelle FROM Produit;")) {
			ResultSet resultSet = queryResult.getResultSet();

			while (resultSet.next()) {
				Boisson boisson = new Boisson(resultSet.getInt("id"), resultSet.getString("libelle"));
				listeBoissons.add(boisson);
				comboBox.addItem(boisson);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void addProduct(JComboBox<Boisson> comboBox, JTextField stockField) {
		Boisson selectedBoisson = (Boisson) comboBox.getSelectedItem();
		if (selectedBoisson == null || stockField.getText().trim().isEmpty()) {
			return;
		}

		int quantity;
		try {
			quantity = Integer.parseInt(stockField.getText().trim());
		} catch (NumberFormatException e) {
			Message.showErrorMessage("Erreur", "La quantité doit être un nombre valide !");
			return;
		}

		// Vérifier si la boisson est déjà dans le tableau
		boolean alreadyExists = false;
		for (int i = 0; i < tableModel.getRowCount(); i++) {
			int idBoisson = (int) tableModel.getValueAt(i, 0);
			if (idBoisson == selectedBoisson.getId()) {
				int currentQuantity = parseIntSafe(tableModel.getValueAt(i, 2));
				tableModel.setValueAt(currentQuantity + quantity, i, 2);
				calculateConsomme(i);
				alreadyExists = true;
				break;
			}
		}

		// Sinon on ajoute une nouvelle ligne
		if (!alreadyExists) {
			tableModel.addRow(new Object[]{selectedBoisson.getId(), selectedBoisson, quantity, "", 0});
		}
	}

	/* ===================== VALIDATION ===================== */
	private void validateData() {
		if (table.isEditing()) {
			table.getCellEditor().stopCellEditing();
		}

		int rowCount = tableModel.getRowCount(); // Obtenir le nombre de lignes dans le modèle

		if (rowCount == 0) {
			dispose();
			return;
		}

		for (int i = 0; i < rowCount; i++) {
			try {
				// Récupérer "Fournie"
				Object fourniObj = tableModel.getValueAt(i, 2);
				int fourni = 0;
				if (fourniObj != null) {
					String str = fourniObj.toString().trim();
					if (!str.isEmpty()) {
						fourni = Integer.parseInt(str);
					}
				}

				// Récupérer "Reprise"
				Object repriseObj = tableModel.getValueAt(i, 3);
				int reprise = (repriseObj == null || repriseObj.toString().trim().isEmpty())
						? -1
						: Integer.parseInt(repriseObj.toString());

				int idBoisson = (int) tableModel.getValueAt(i, 0);

				// Vérifier si la ligne existe déjà
				String checkSql = "SELECT COUNT(*) FROM MouvementLocation " +
						"WHERE IdBoisson = " + idBoisson +
						" AND IdSalle = " + idSalle +
						" AND IdCafeteria = " + idCafet + ";";

				int count = 0;

				QueryResult queryResult = null;
				try {
					queryResult = Requete.executeQuery(checkSql);
					if(queryResult.getResultSet().next())
					{
						count = queryResult.getResultSet().getInt(1);
					}
				} catch (Exception ex) {
					Message.showErrorMessage("Erreur de la base de données", ex.getMessage());
				}
				finally {
					if (queryResult != null) {
						queryResult.close();
					}
				}

				String sql;
				if (count > 0) {
					// Mise à jour existante
					sql = "UPDATE MouvementLocation SET " +
							"Fournie = " + fourni + ", " +
							"Reprise = " + reprise + " " +
							"WHERE IdBoisson = " + idBoisson +
							" AND IdSalle = " + idSalle +
							" AND IdCafeteria = " + idCafet + ";";
				} else {
					// Nouvelle insertion
					sql = "INSERT INTO MouvementLocation (IdBoisson, IdSalle, IdCafeteria, Fournie, Reprise) " +
							"VALUES (" + idBoisson + ", " + idSalle + ", " + idCafet + ", " + fourni + ", " + reprise + ");";
				}

				Requete.executeUpdate(sql);

			} catch (Exception ex) {
				Message.showErrorMessage("Erreur lors de l'enregistrement des boissons dans la base de données", ex.getMessage());
			}
		}

		for (int valeur : idBoisson)
		{
			Requete.executeUpdate("DELETE FROM MouvementLocation where IdSalle = " + idSalle
					+ " AND IdCafeteria = " + idCafet + " AND IdBoisson = " + valeur);
		}

		if (validationListener != null) {
			validationListener.onValidate();
		}
		dispose();
	}

	public interface ValidationListener {
		void onValidate();
	}

	public void getValidationListener(ValidationListener listener) {
		this.validationListener = listener;
	}
}