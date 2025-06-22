package boisson.mouvement;

import options.ColorXml;
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
import java.util.ArrayList;
import java.util.logging.Logger;

public class AddMouvement extends JDialog {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(AddMouvement.class.getName());
	private final ColorXml color = new ColorXml();
	private final ArrayList<Boisson> listeBoissons = new ArrayList<>();
	private final DefaultTableModel tableModel = createTableModel();

	public AddMouvement(JFrame parent) throws SQLException {
		super(parent, "Ajouter un mouvement", true);
		setSize(615, 700);
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setResizable(false);

		JPanel panneauPrincipal = createMainPanel();
		add(panneauPrincipal, BorderLayout.CENTER);
		setLocationRelativeTo(parent);
	}

	private JPanel createMainPanel() throws SQLException {
		JPanel panel = new JPanel();
		panel.setBackground(Color.decode(color.xmlReader("background")));
		panel.setLayout(null);

		// Section: Commentaire
		JLabel instructionCommentaire = createLabel("Commentaire :", 25, 25, 400, 30);
		panel.add(instructionCommentaire);

		JTextField commentaireField = createTextField(25, 50, 550, 30);
		panel.add(commentaireField);

		// Section: Type de mouvement
		JLabel instructionType = createLabel("Type :", 25, 115, 100, 30);
		panel.add(instructionType);

		JComboBox<String> comboBoxType = createComboBox(new String[]{"Entrée", "Sortie", "Commande"}, 125, 115, 200, 30);
		panel.add(comboBoxType);

		// Section: Produit
		JLabel instructionBoisson = createLabel("Boisson :", 25, 180, 100, 30);
		panel.add(instructionBoisson);

		JComboBox<Boisson> comboBoxBoisson = new JComboBox<>();
		loadProducts(comboBoxBoisson);
		comboBoxBoisson.setBounds(125, 180, 150, 30);
		Style.applyBoxStyleBoisson(comboBoxBoisson);
		panel.add(comboBoxBoisson);

		JLabel instructionStock = createLabel("Quantité :", 300, 180, 100, 30);
		panel.add(instructionStock);

		JTextField stockField = createTextField(400, 180, 50, 30);
		panel.add(stockField);

		JButton boutonAjouter = createButton("Ajouter", 500, 180, 75, 30, e -> addProduct(comboBoxBoisson, stockField));
		panel.add(boutonAjouter);

		// Section: Tableau des produits
		JTable table = createTable();
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setBounds(25, 250, 550, 275);
		panel.add(scrollPane);

		// Section: Boutons principaux
		JButton boutonFermer = createButton("Fermer", 25, 590, 100, 40, e -> dispose());
		panel.add(boutonFermer);

		JButton boutonValider = createButton("Valider", 475, 590, 100, 40, e -> validateData(commentaireField, comboBoxType));
		panel.add(boutonValider);

		return panel;
	}

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

	private JComboBox<String> createComboBox(String[] items, int x, int y, int width, int height) {
		JComboBox<String> comboBox = new JComboBox<>(items);
		comboBox.setBounds(x, y, width, height);
		Style.applyBoxStyle(comboBox);
		return comboBox;
	}

	private JButton createButton(String text, int x, int y, int width, int height, ActionListener action) {
		JButton button = new JButton(text);
		button.setBounds(x, y, width, height);
		button.addActionListener(action);
		Style.applyButtonStyle(button);
		button.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		return button;
	}

	private JTable createTable() {
		JTable table = new JTable(tableModel) {
			@Override
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
				Component component = super.prepareRenderer(renderer, row, column);
				if (row % 2 == 0) {
					component.setBackground(Color.decode(color.xmlReader("background_tab_pair")));
				} else {
					component.setBackground(Color.decode(color.xmlReader("background_tab_impair")));
				}
				component.setForeground(Color.decode(color.xmlReader("foreground")));
				return component;
			}
		};

		table.getTableHeader().setDefaultRenderer(createHeaderRenderer());
		table.getTableHeader().setReorderingAllowed(false);
		table.getColumnModel().getColumn(0).setMinWidth(0); // ID cachée
		table.getColumnModel().getColumn(0).setMaxWidth(0);
		table.getColumnModel().getColumn(0).setPreferredWidth(0);
		return table;
	}

	private DefaultTableCellRenderer createHeaderRenderer() {
		return new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				JLabel headerLabel = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				headerLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
				headerLabel.setForeground(Color.decode(color.xmlReader("foreground")));
				headerLabel.setBackground(Color.decode(color.xmlReader("background_tab")));
				headerLabel.setHorizontalAlignment(CENTER);
				return headerLabel;
			}
		};
	}

	private DefaultTableModel createTableModel() {
		String[] columnNames = {"ID", "Boisson", "Quantité"};
		return new DefaultTableModel(columnNames, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return column == 2; // Seule la colonne "Quantité" est éditable
			}
		};
	}

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
		int quantity = Integer.parseInt(stockField.getText());
		tableModel.addRow(new Object[]{selectedBoisson.getId(), selectedBoisson, quantity});
	}

	private void validateData(JTextField commentaireField, JComboBox<String> comboBoxType) {
		// Logic for validation and database insertion
		dispose();
	}
}