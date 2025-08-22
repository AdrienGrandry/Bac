package boisson.produit;

import options.ColorXml;
import ressources.Message;
import ressources.Style;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.io.Serial;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
/////////////////////////////////////////////////////////////////////////////////////////
/// AFFICHER A COTE DU NOM CASE FUTS POUR CHANGER TVA=21 ET PRIX FUTS ET PAS NBCASIER ///
/////////////////////////////////////////////////////////////////////////////////////////

public class AddProduit extends JDialog
{
	@Serial
	private static final long serialVersionUID = 1L;
	ColorXml color = new ColorXml();

	public AddProduit(JFrame parent, final int id)
	{
		super(parent, "Ajouter un produit", true);

		setSize(500, 700);
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setResizable(false);

		JPanel panneauPrincipal = new JPanel();
		panneauPrincipal.setBackground(Color.decode(ColorXml.xmlReader("background")));
		panneauPrincipal.setLayout(null);

		JLabel instructionNum = new JLabel("Numéro");
		styleLabel(instructionNum);
		instructionNum.setBounds(50, 25, 400, 30);
		panneauPrincipal.add(instructionNum);

		final JTextField num = new JTextField();
		styleTextField(num);
		num.setBounds(50, 60, 400, 30);
		panneauPrincipal.add(num);

		JLabel instructionNom = new JLabel("Nom");
		styleLabel(instructionNom);
		instructionNom.setBounds(50, 110, 400, 30);
		panneauPrincipal.add(instructionNom);

		final JTextField nom = new JTextField();
		styleTextField(nom);
		nom.setBounds(50, 145, 300, 30);
		panneauPrincipal.add(nom);

		final JCheckBox checkBoxFut = new JCheckBox();
		Style.applyCheckBoxStyle(checkBoxFut);
		checkBoxFut.setBounds(370, 145, 30, 30);
		panneauPrincipal.add(checkBoxFut);

		JLabel instructionFuts = new JLabel("Fût");
		styleLabel(instructionFuts);
		instructionFuts.setBounds(400, 145, 50, 30);
		panneauPrincipal.add(instructionFuts);

		JLabel instructionLieu = new JLabel("Lieu");
		styleLabel(instructionLieu);
		instructionLieu.setBounds(50, 195, 400, 30);
		panneauPrincipal.add(instructionLieu);

		String[] lieu =
		{ "Salle", "Cafétéria" };
		final JComboBox<String> comboBox = new JComboBox<>(lieu);
		comboBox.setBounds(50, 230, 400, 30);
		Style.applyBoxStyle(comboBox);
		panneauPrincipal.add(comboBox);

		JLabel instructionStock = new JLabel("Stock");
		styleLabel(instructionStock);
		instructionStock.setBounds(50, 280, 400, 30);
		panneauPrincipal.add(instructionStock);

		final JTextField stock = new JTextField();
		styleTextField(stock);
		stock.setBounds(50, 315, 400, 30);
		panneauPrincipal.add(stock);

		JLabel instructionPrix = new JLabel("PA/Casier HTVA");
		styleLabel(instructionPrix);
		instructionPrix.setBounds(50, 365, 175, 30);
		panneauPrincipal.add(instructionPrix);

		final JTextField prix = new JTextField();
		styleTextField(prix);
		prix.setBounds(200, 365, 75, 30);
		panneauPrincipal.add(prix);

		JLabel euro = new JLabel("€");
		styleLabel(euro);
		euro.setBounds(275, 365, 25, 30);
		panneauPrincipal.add(euro);

		JLabel instructionTVA = new JLabel("TVA");
		styleLabel(instructionTVA);
		instructionTVA.setBounds(50, 400, 150, 30);
		panneauPrincipal.add(instructionTVA);

		String[] tva =
				{"6 %", "21 %"};
		final JComboBox<String> comboBoxTVA = new JComboBox<>(tva);
		comboBoxTVA.setBounds(90, 400, 75, 30);
		Style.applyBoxStyle(comboBoxTVA);
		panneauPrincipal.add(comboBoxTVA);

		JLabel instructionQuantite = new JLabel("Nbr/Casier");
		styleLabel(instructionQuantite);
		instructionQuantite.setBounds(300, 365, 150, 30);
		panneauPrincipal.add(instructionQuantite);

		final JTextField quantite = new JTextField();
		styleTextField(quantite);
		quantite.setBounds(400, 365, 50, 30);
		panneauPrincipal.add(quantite);

		JLabel instructionTaux = new JLabel("MB");
		styleLabel(instructionTaux);
		instructionTaux.setBounds(175, 400, 50, 30);
		panneauPrincipal.add(instructionTaux);

		String[] taux =
				{"0 %", "1 %", "2 %", "3 %", "4 %", "5 %", "6 %", "7 %", "8 %",
						"9 %", "10 %", "11 %", "12 %", "13 %", "14 %", "15 %" };
		final JComboBox<String> comboBoxTaux = new JComboBox<>(taux);
		comboBoxTaux.setBounds(225, 400, 75, 30);
		Style.applyBoxStyle(comboBoxTaux);
		panneauPrincipal.add(comboBoxTaux);

		JLabel instructionPrixVente = new JLabel("PVU : ");
		styleLabel(instructionPrixVente);
		instructionPrixVente.setBounds(325, 400, 125, 30);
		panneauPrincipal.add(instructionPrixVente);

		DocumentListener calculListener = new DocumentListener() {
			public void update() {
				try {
					double prixCasier = Double.parseDouble(prix.getText().replace(",", "."));
					int qte = Integer.parseInt(quantite.getText());

					double tva = 0;
					double taux = 0;

					switch ((String) comboBoxTVA.getSelectedItem())
					{
						case "6 %":
							tva = 1.06;
							break;
						case "21 %":
							tva = 1.21;
							break;
					}

					switch ((String) comboBoxTaux.getSelectedItem()) {
						case "0 %":  taux = 1.00;  break;
						case "1 %":  taux = 1.01; break;
						case "2 %":  taux = 1.02; break;
						case "3 %":  taux = 1.03; break;
						case "4 %":  taux = 1.04; break;
						case "5 %":  taux = 1.05; break;
						case "6 %":  taux = 1.06; break;
						case "7 %":  taux = 1.07; break;
						case "8 %":  taux = 1.08; break;
						case "9 %":  taux = 1.09; break;
						case "10 %": taux = 1.10; break;
						case "11 %": taux = 1.11; break;
						case "12 %": taux = 1.12; break;
						case "13 %": taux = 1.13; break;
						case "14 %": taux = 1.14; break;
						case "15 %": taux = 1.15; break;
					}

					if(checkBoxFut.isSelected())
					{
						qte = 1;
						tva = 1.21;
					}

					if (qte > 0) {


						double prixUnitaireHtva = prixCasier / qte;
						double prixUnitaireFinal = prixUnitaireHtva * tva * taux;
						//CALCULER LA FORMULE 10% ET TVA

						instructionPrixVente.setText("PVU : " + String.format("%.2f", prixUnitaireFinal) + " €");
					} else {
						instructionPrixVente.setText("PVU : -");
					}
				} catch (NumberFormatException e) {
					instructionPrixVente.setText("PVU : -");
				}
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				update();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				update();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				update();
			}
		};
		prix.getDocument().addDocumentListener(calculListener);
		quantite.getDocument().addDocumentListener(calculListener);
		comboBoxTVA.addActionListener(e -> calculListener.insertUpdate(null));
		comboBoxTaux.addActionListener(e -> calculListener.insertUpdate(null));
		checkBoxFut.addItemListener(e -> calculListener.insertUpdate(null));

		JLabel instructionPrixGlobal = new JLabel("Prix :");
		styleLabel(instructionPrixGlobal);
		instructionPrixGlobal.setBounds(50, 450, 180, 30);
		panneauPrincipal.add(instructionPrixGlobal);

		JLabel instructionReunion = new JLabel("Réunion");
		styleLabel(instructionReunion);
		instructionReunion.setHorizontalAlignment(SwingConstants.CENTER);
		instructionReunion.setBounds(50, 480, 90, 30);
		panneauPrincipal.add(instructionReunion);

		final JTextField prixReunion = new JTextField();
		styleTextField(prixReunion);
		prixReunion.setBounds(50, 510, 90, 30);
		panneauPrincipal.add(prixReunion);

		JLabel instructionPensionne = new JLabel("Pensionnés");
		styleLabel(instructionPensionne);
		instructionPensionne.setHorizontalAlignment(SwingConstants.CENTER);
		instructionPensionne.setBounds(190, 480, 120, 30);
		panneauPrincipal.add(instructionPensionne);

		final JTextField prixPensionnes = new JTextField();
		styleTextField(prixPensionnes);
		prixPensionnes.setBounds(190, 510, 120, 30);
		panneauPrincipal.add(prixPensionnes);

		JLabel instructionParoisse = new JLabel("Paroisse");
		styleLabel(instructionParoisse);
		instructionParoisse.setHorizontalAlignment(SwingConstants.CENTER);
		instructionParoisse.setBounds(360, 480, 90, 30);
		panneauPrincipal.add(instructionParoisse);

		final JTextField prixParoisse = new JTextField();
		styleTextField(prixParoisse);
		prixParoisse.setBounds(360, 510, 90, 30);
		panneauPrincipal.add(prixParoisse);

		final JCheckBox checkBoxVisibilite = new JCheckBox("Visible");
		Style.applyCheckBoxStyle(checkBoxVisibilite);
		checkBoxVisibilite.setFont(new Font("Arial", Font.BOLD, 18));
		checkBoxVisibilite.setBounds(50, 550, 100, 30);
		checkBoxVisibilite.setBackground(Color.decode(ColorXml.xmlReader("background")));
		checkBoxVisibilite.setSelected(true);
		panneauPrincipal.add(checkBoxVisibilite);

		checkBoxFut.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (checkBoxFut.isSelected()) {
					instructionPrix.setText("PA/Fût HTVA");
					instructionQuantite.setVisible(false);
					quantite.setVisible(false);
					instructionTVA.setText("TVA    21 %");
					comboBoxTVA.setVisible(false);
				} else {
					instructionPrix.setText("PA/Casier HTVA");
					instructionQuantite.setVisible(true);
					quantite.setVisible(true);
					instructionTVA.setText("TVA");
					comboBoxTVA.setVisible(true);
				}
			}
		});

		JButton boutonFermer = new JButton("Fermer");
		boutonFermer.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				dispose();
			}
		});
		boutonFermer.setBounds(50, 590, 100, 40);
		Style.applyButtonStyle(boutonFermer);
		boutonFermer.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		panneauPrincipal.add(boutonFermer);

		JButton boutonValider = new JButton("Valider");
		boutonValider.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				String numText = num.getText();
				String nomProduit = nom.getText();
				String selectedPeriod = (String) comboBox.getSelectedItem();
				String stockText = stock.getText();
				String prixText = prix.getText();
				String quantiteText = quantite.getText();
				String prixReunionText = prixReunion.getText();
				String prixPensionnesText = prixPensionnes.getText();
				String prixParoisseText = prixParoisse.getText();

				if (nomProduit.isEmpty() || stockText.isEmpty() || numText.isEmpty() || prixText.isEmpty()
				        || selectedPeriod == null || quantiteText.isEmpty() || prixReunionText.isEmpty() || prixPensionnesText.isEmpty())
				{
					Message.showErrorMessage("Erreur de validation", "Veuillez remplir tous les champs.");
					return;
				}

				try
				{
					int numProduit = Integer.parseInt(numText);
					int stockProduit = Integer.parseInt(stockText);
					float prixProduit = Float.parseFloat(prixText.replace(',', '.'));
					int quantiteCasier = Integer.parseInt(quantiteText);
					float prixReunionProduit = Float.parseFloat(prixReunionText.replace(',', '.'));
					float prixPensionnesProduit = Float.parseFloat(prixPensionnesText.replace(',', '.'));
					float prixParoisseProduit = Float.parseFloat(prixParoisseText.replace(',', '.'));
					boolean visible = checkBoxVisibilite.isSelected();

					if (stockProduit < 0 || numProduit < 0 || prixProduit < 0 || quantiteCasier < 0 || prixReunionProduit < 0 || prixPensionnesProduit < 0 || prixParoisseProduit < 0)
					{
						Message.showErrorMessage("Erreur de validation", "Les valeurs numériques doivent être positives.");
						return;
					}

					String lieu = null;
					switch (selectedPeriod)
					{
					case "Salle":
						lieu = "salle";
						break;
					case "Cafétéria":
						lieu = "cafeteria";
						break;
                    }

					boolean fut;
					if(checkBoxFut.isSelected())
					{
						fut = true;
					}
					else
					{
						fut = false;
					}

					int tvasql = 0;
					int tauxsql = 0;

					switch ((String) comboBoxTVA.getSelectedItem())
					{
						case "6 %":
							tvasql = 6;
							break;
						case "21 %":
							tvasql = 21;
							break;
					}

					switch ((String) comboBoxTaux.getSelectedItem()) {
						case "0 %":  tauxsql = 0;  break;
						case "1 %":  tauxsql = 1; break;
						case "2 %":  tauxsql = 2; break;
						case "3 %":  tauxsql = 3; break;
						case "4 %":  tauxsql = 4; break;
						case "5 %":  tauxsql = 5; break;
						case "6 %":  tauxsql = 6; break;
						case "7 %":  tauxsql = 7; break;
						case "8 %":  tauxsql = 8; break;
						case "9 %":  tauxsql = 9; break;
						case "10 %": tauxsql = 10; break;
						case "11 %": tauxsql = 11; break;
						case "12 %": tauxsql = 12; break;
						case "13 %": tauxsql = 13; break;
						case "14 %": tauxsql = 14; break;
						case "15 %": tauxsql = 15; break;
					}

					try (Connection connection = DriverManager.getConnection("jdbc:sqlite:ressources/Database.db");
					        Statement statement = connection.createStatement())
					{

						if (id == 0)
						{
							statement.executeUpdate(
							        "INSERT INTO produit (libelle, lieu, stock, numero, prix, visible, nbCasier," +
											"PrixReunion, PrixPensionnes, prixParoisse, Fut, TVA, Taux) VALUES ('"
							                + nomProduit + "', '" + lieu + "', " + stockProduit + ", " + numProduit
											+ ", " + prixProduit + ", " + visible + ", " + quantiteCasier + ", " + prixReunionProduit
											+ ", " + prixPensionnesProduit + ", " + prixParoisseProduit + ", " + fut
											+ ", " + tvasql + ", " + tauxsql + ");");
						} else
						{
							statement.executeUpdate("UPDATE produit SET libelle='" + nomProduit + "', lieu='" + lieu
							        + "', stock=" + stockProduit + ", numero=" + numProduit + ", prix=" + prixProduit
							        + ", visible=" + visible + ", nbCasier=" + quantiteCasier + ", PrixReunion=" + prixReunionProduit
									+ ", PrixPensionnes=" + prixPensionnesProduit + ", PrixParoisse=" + prixParoisseProduit
									+ ", Fut=" + fut + ", TVA=" + tvasql + ",Taux=" + tauxsql + " WHERE id=" + id + ";");
						}
					} catch (SQLException ex)
					{
						Message.showErrorMessage("Erreur SQL", "Erreur lors de la mise à jour de la base de données : " + ex.getMessage());
					}
					dispose();
				} catch (NumberFormatException ex)
				{
					Message.showErrorMessage("Erreur de validation",
					        "Le stock, le numéro du produit et le prix doivent être des nombres valides.");
				}
			}
		});
		boutonValider.setBounds(350, 590, 100, 40);
		Style.applyButtonStyle(boutonValider);
		boutonValider.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		panneauPrincipal.add(boutonValider);

		JButton boutonSupprimer = new JButton("Supprimer");
		boutonSupprimer.setBounds(200, 590, 100, 40);
		Style.applyButtonStyle(boutonSupprimer);
		boutonSupprimer.setBorder(BorderFactory.createLineBorder(Color.BLACK));

		boutonSupprimer.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				try (Connection connection = DriverManager.getConnection("jdbc:sqlite:ressources/Database.db");
				        Statement statement = connection.createStatement())
				{

					statement.executeUpdate("DELETE FROM produit WHERE id = " + id);
				} catch (SQLException e1)
				{
					Message.showErrorMessage("Erreur !", "Impossible de lancer la demande !");
				}

				dispose();
			}
		});

		if (id != 0)
		{
			panneauPrincipal.add(boutonSupprimer);

			try (Connection connection = DriverManager.getConnection("jdbc:sqlite:ressources/Database.db");
			        Statement statement = connection.createStatement())
			{

				ResultSet resultSet = statement.executeQuery("SELECT * FROM produit WHERE id=" + id);

				if (resultSet.next()) {
					String numeroSql = resultSet.getString("numero");
					String nomSql = resultSet.getString("libelle");
					String lieuSql = resultSet.getString("lieu");
					String stockSql = resultSet.getString("stock"); // correction ici aussi
					String prixSql = resultSet.getString("prix").replace(".", ",");
					String nbCasierSql = resultSet.getString("nbCasier");
					int tvaSql = resultSet.getInt("TVA");
					int tauxSql = resultSet.getInt("taux");
					boolean fut = resultSet.getBoolean("Fut");
					String prixReunionSql = resultSet.getString("PrixReunion").replace(".", ",");
					String prixPensionnesSql = resultSet.getString("PrixPensionnes").replace(".", ",");
					String prixParoisseSql = resultSet.getString("PrixParoisse").replace(".", ",");
					String visibleSql = resultSet.getString("visible");

					nom.setText(nomSql);
					switch (lieuSql) {
						case "salle":
							comboBox.setSelectedIndex(0);
							break;
						case "cafeteria":
							comboBox.setSelectedIndex(1);
							break;
					}
					stock.setText(stockSql);
					num.setText(numeroSql);
					prix.setText(prixSql);
					quantite.setText(nbCasierSql);
					prixReunion.setText(prixReunionSql);
					prixPensionnes.setText(prixPensionnesSql);
					prixParoisse.setText(prixParoisseSql);

					if(tvaSql == 6)
					{
						comboBoxTVA.setSelectedIndex(0);
					}
					else
					{
						comboBoxTVA.setSelectedIndex(1);
					}

					comboBoxTaux.setSelectedIndex(tauxSql);

					if(fut)
					{
						checkBoxFut.setSelected(fut);
						instructionPrix.setText("PA/Fût HTVA");
						instructionQuantite.setVisible(false);
						quantite.setVisible(false);
						instructionTVA.setText("TVA    21 %");
						comboBoxTVA.setVisible(false);
					}

					checkBoxVisibilite.setSelected(visibleSql.equals("1"));
				} else {
					JOptionPane.showMessageDialog(null, "Produit introuvable pour cet ID.", "Erreur",
							JOptionPane.ERROR_MESSAGE);
				}

			} catch (SQLException e1)
			{
				JOptionPane.showMessageDialog(null, "Impossible de lancer la demande car vide ou incorrect !",
				        "ERREUR...", JOptionPane.ERROR_MESSAGE);
			}
		}

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