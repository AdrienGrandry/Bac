package boisson.produit;

import options.ColorXml;
import ressources.Message;
import ressources.Style;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serial;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
		nom.setBounds(50, 145, 400, 30);
		panneauPrincipal.add(nom);

		JLabel instructionLieu = new JLabel("Lieu");
		styleLabel(instructionLieu);
		instructionLieu.setBounds(50, 195, 400, 30);
		panneauPrincipal.add(instructionLieu);

		String[] periods =
		{ "Salle", "Cafétéria" };
		final JComboBox<String> comboBox = new JComboBox<>(periods);
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

		JLabel instructionPrix = new JLabel("Prix d'achat par Casier");
		styleLabel(instructionPrix);
		instructionPrix.setBounds(50, 365, 250, 30);
		panneauPrincipal.add(instructionPrix);

		final JTextField prix = new JTextField();
		styleTextField(prix);
		prix.setBounds(270, 365, 180, 30);
		panneauPrincipal.add(prix);

		JLabel instructionQuantite = new JLabel("Nombre/Casier");
		styleLabel(instructionQuantite);
		instructionQuantite.setBounds(50, 400, 250, 30);
		panneauPrincipal.add(instructionQuantite);

		final JTextField quantite = new JTextField();
		styleTextField(quantite);
		quantite.setBounds(185, 400, 25, 30);
		panneauPrincipal.add(quantite);

		JLabel instructionPrixVente = new JLabel("Prix de vente : ");
		styleLabel(instructionPrixVente);
		instructionPrixVente.setBounds(270, 400, 180, 30);
		panneauPrincipal.add(instructionPrixVente);

		javax.swing.event.DocumentListener calculListener = new javax.swing.event.DocumentListener() {
			private void update() {
				try {
					double prixCasier = Double.parseDouble(prix.getText().replace(",", "."));
					int qte = Integer.parseInt(quantite.getText());

					if (qte > 0) {
						double prixUnitaire = prixCasier / qte;
						//CALCULER LA FORMULE 10% ET TVA

						instructionPrixVente.setText("Prix de vente : " + String.format("%.3f", prixUnitaire));
					} else {
						instructionPrixVente.setText("Prix de vente : -");
					}
				} catch (NumberFormatException e) {
					instructionPrixVente.setText("Prix de vente : -");
				}
			}

			@Override
			public void insertUpdate(javax.swing.event.DocumentEvent e) {
				update();
			}

			@Override
			public void removeUpdate(javax.swing.event.DocumentEvent e) {
				update();
			}

			@Override
			public void changedUpdate(javax.swing.event.DocumentEvent e) {
				update();
			}
		};
		prix.getDocument().addDocumentListener(calculListener);
		quantite.getDocument().addDocumentListener(calculListener);

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

					try (Connection connection = DriverManager.getConnection("jdbc:sqlite:ressources/Database.db");
					        Statement statement = connection.createStatement())
					{

						if (id == 0)
						{
							statement.executeUpdate(
							        "INSERT INTO produit (libelle, lieu, stock, numero, prix, visible, nbCasier, PrixReunion, PrixPensionnes, prixParoisse) VALUES ('"
							                + nomProduit + "', '" + lieu + "', " + stockProduit + ", " + numProduit
							                + ", " + prixProduit + ", " + visible + ", " + quantiteCasier + ", " + prixReunionProduit + ", " + prixPensionnesProduit + ", " + prixParoisseProduit + ");");
						} else
						{
							statement.executeUpdate("UPDATE produit SET libelle='" + nomProduit + "', lieu='" + lieu
							        + "', stock=" + stockProduit + ", numero=" + numProduit + ", prix=" + prixProduit
							        + ", visible=" + visible + ", nbCasier=" + quantiteCasier + ", PrixReunion=" + prixReunionProduit
									+ ", PrixPensionnes=" + prixPensionnesProduit + ", PrixParoisse=" + prixParoisseProduit + " WHERE id=" + id + ";");
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