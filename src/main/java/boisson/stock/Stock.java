package boisson.stock;

import ressources.dataBase.QueryResult;
import ressources.dataBase.Requete;
import ressources.Style;
import options.ColorXml;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Stock extends JPanel {
	private static final long serialVersionUID = 1L;

	private final Color backgroundColor;
	private final Color foregroundColor;
	private final Requete requete = new Requete();
	private final JPanel tableauPanel = new JPanel();
	private final JLabel totalLabel = new JLabel();

	public Stock(JFrame parentFrame) {
		ColorXml color = new ColorXml();
		backgroundColor = Color.decode(color.xmlReader("background"));
		foregroundColor = Color.decode(color.xmlReader("foreground"));

		setBackground(backgroundColor);
		setLayout(new BorderLayout(10, 10));

		initHeader();
		initTableauPanel(parentFrame);

		// Déclencher l'affichage initial
		updateStockTable("Tous");
	}

	private void initHeader() {
		JPanel entete = new JPanel(new BorderLayout(10, 10));
		entete.setBackground(backgroundColor);

		JLabel titre = new JLabel("Stock", SwingConstants.CENTER);
		titre.setFont(new Font("Arial", Font.BOLD, 40));
		titre.setForeground(foregroundColor);
		titre.setBorder(new EmptyBorder(10, 10, 10, 10));
		entete.add(titre, BorderLayout.NORTH);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setBackground(backgroundColor);
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

		String[] periods = {"Tous", "Salle", "Cafétéria", "Salle + Cafétéria"};
		JComboBox<String> comboBox = new JComboBox<>(periods);
		Style.applyBoxStyle(comboBox);
		comboBox.setPreferredSize(new Dimension(250, 40));
		comboBox.setMaximumSize(new Dimension(250, 40));

		JPanel totalPanel = new JPanel();
		totalPanel.setPreferredSize(new Dimension(300, 40));
		totalPanel.setMaximumSize(new Dimension(300, 40));
		Style.applyPanelStyle(totalPanel);

		totalLabel.setFont(new Font("Arial", Font.PLAIN, 20));
		totalLabel.setForeground(foregroundColor);
		totalPanel.add(totalLabel);

		buttonPanel.add(Box.createHorizontalGlue());
		buttonPanel.add(comboBox);
		buttonPanel.add(Box.createRigidArea(new Dimension(50, 0)));
		buttonPanel.add(totalPanel);
		buttonPanel.add(Box.createHorizontalGlue());

		entete.add(buttonPanel, BorderLayout.CENTER);
		add(entete, BorderLayout.NORTH);

		// Listener
		comboBox.addActionListener(e -> {
			String selectedPeriod = (String) comboBox.getSelectedItem();
			updateStockTable(selectedPeriod);
		});
	}

	private void initTableauPanel(JFrame parentFrame) {
		tableauPanel.setBackground(backgroundColor);
		tableauPanel.setLayout(new BorderLayout());
		tableauPanel.setPreferredSize(new Dimension(parentFrame.getWidth(), parentFrame.getHeight()));
		add(tableauPanel, BorderLayout.CENTER);
	}

	private void updateStockTable(String period) {
		String sql = buildStockQuery(period);
		String totalSql = buildTotalQuery(period);

		tableauPanel.removeAll();
		JPanel tab = requete.executeQueryAndReturnPanel(
				sql,
				tableauPanel.getHeight(),
				tableauPanel.getWidth(),
				"pair_impair"
		);
		tableauPanel.add(tab);
		revalidate();
		repaint();

		updateTotalStockLabel(totalSql);
	}

	private void updateTotalStockLabel(String totalQuery) {
		try (QueryResult result = Requete.executeQuery(totalQuery)) {
			ResultSet rs = result.getResultSet();
			if (rs.next()) {
				String value = rs.getString("valeur_totale_stock");
				totalLabel.setText("Stock total HTVA : " + value + " €");
			} else {
				totalLabel.setText("Stock total : 0 €");
			}
		} catch (SQLException ex) {
			ex.printStackTrace(); // En production, remplace par un logger ou une boîte de dialogue
			totalLabel.setText("Erreur de calcul du stock");
		}
	}

	private String buildStockQuery(String period) {
		String base = "SELECT numero AS 'Numéro', libelle AS 'Libelle', CASE WHEN lieu = 'deux' THEN 'Salle + Cafétéria' ELSE lieu END AS 'Lieu', stock AS 'Stock', " +
				"ROUND(prix/nbCasier, 3) AS 'Prix Unité HTVA', ROUND(((prix/nbCasier) * stock), 3) AS 'Prix total HTVA' " +
				"FROM produit WHERE visible = 1";

		if("Salle + Cafétéria".equals(period)) {
			base += " AND lieu = 'deux'";
		}
		else if (!"Tous".equalsIgnoreCase(period)) {
			base += " AND lieu = '" + period.toLowerCase().replace("é", "e") + "'";
		}

		return base + " ORDER BY numero";
	}

	private String buildTotalQuery(String period) {
		String base = "SELECT SUM(ROUND(((prix/nbCasier) * stock), 3)) AS valeur_totale_stock FROM produit WHERE visible = 1";

		if("Salle + Cafétéria".equals(period)) {
			base += " AND lieu = 'deux'";
		}
		else if (!"Tous".equalsIgnoreCase(period)) {
			base += " AND lieu = '" + period.toLowerCase().replace("é", "e") + "'";
		}

		return base;
	}
}