package produit;

import ressources.ColorXml;
import style.Style;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AddProduit extends JDialog
{
	private static final long serialVersionUID = 1L;
	ColorXml color = new ColorXml();

	public AddProduit(JFrame parent, final int id)
	{
		super(parent, "Ajouter un produit", true);

		setSize(500, 700);
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setResizable(false);

		JPanel panneauPrincipal = new JPanel();
		panneauPrincipal.setBackground(Color.decode(color.xmlReader("background")));
		panneauPrincipal.setLayout(null);

		JLabel instructionNum = new JLabel("Numéro");
		styleLabel(instructionNum);
		instructionNum.setBounds(50, 50, 400, 30);
		panneauPrincipal.add(instructionNum);

		final JTextField num = new JTextField();
		styleTextField(num);
		num.setBounds(50, 90, 400, 30);
		panneauPrincipal.add(num);

		JLabel instructionNom = new JLabel("Nom");
		styleLabel(instructionNom);
		instructionNom.setBounds(50, 150, 400, 30);
		panneauPrincipal.add(instructionNom);

		final JTextField nom = new JTextField();
		styleTextField(nom);
		nom.setBounds(50, 190, 400, 30);
		panneauPrincipal.add(nom);

		JLabel instructionLieu = new JLabel("Lieu");
		styleLabel(instructionLieu);
		instructionLieu.setBounds(50, 250, 400, 30);
		panneauPrincipal.add(instructionLieu);

		String[] periods =
		{ "Salle", "Cafétéria" };
		final JComboBox<String> comboBox = new JComboBox<>(periods);
		comboBox.setBounds(50, 290, 400, 30);
		Style.applyBoxStyle(comboBox);
		panneauPrincipal.add(comboBox);

		JLabel instructionStock = new JLabel("Stock");
		styleLabel(instructionStock);
		instructionStock.setBounds(50, 350, 400, 30);
		panneauPrincipal.add(instructionStock);

		final JTextField stock = new JTextField();
		styleTextField(stock);
		stock.setBounds(50, 390, 400, 30);
		panneauPrincipal.add(stock);

		JLabel instructionPrix = new JLabel("Prix");
		styleLabel(instructionPrix);
		instructionPrix.setBounds(50, 450, 400, 30);
		panneauPrincipal.add(instructionPrix);

		final JTextField prix = new JTextField();
		styleTextField(prix);
		prix.setBounds(50, 490, 400, 30);
		panneauPrincipal.add(prix);

		final JCheckBox checkBoxVisibilite = new JCheckBox("Visible");
		checkBoxVisibilite.setFont(new Font("Arial", Font.BOLD, 18));
		checkBoxVisibilite.setBounds(50, 550, 100, 30);
		checkBoxVisibilite.setBackground(Color.decode(color.xmlReader("background")));
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
				String nomProduit = nom.getText();
				String stockText = stock.getText();
				String numText = num.getText();
				String prixText = prix.getText();
				String selectedPeriod = (String) comboBox.getSelectedItem();

				if (nomProduit.isEmpty() || stockText.isEmpty() || numText.isEmpty() || prixText.isEmpty()
				        || selectedPeriod == null)
				{
					JOptionPane.showMessageDialog(null, "Veuillez remplir tous les champs.", "Erreur de validation",
					        JOptionPane.ERROR_MESSAGE);
					return;
				}

				try
				{
					int stockProduit = Integer.parseInt(stockText);
					int numProduit = Integer.parseInt(numText);
					float prixProduit = Float.parseFloat(prixText.replace(',', '.'));
					boolean visible = checkBoxVisibilite.isSelected();

					if (stockProduit < 0 || numProduit < 0 || prixProduit < 0)
					{
						JOptionPane.showMessageDialog(null, "Les valeurs numériques doivent être positives.",
						        "Erreur de validation", JOptionPane.ERROR_MESSAGE);
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

					try (Connection connection = DriverManager.getConnection("jdbc:sqlite:Database.db");
					        Statement statement = connection.createStatement())
					{

						if (id == 0)
						{
							statement.executeUpdate(
							        "INSERT INTO produit (libelle, lieu, stock, numero, prix, visible) VALUES ('"
							                + nomProduit + "', '" + lieu + "', " + stockProduit + ", " + numProduit
							                + ", " + prixProduit + ", " + visible + ");");
						} else
						{
							statement.executeUpdate("UPDATE produit SET libelle='" + nomProduit + "', lieu='" + lieu
							        + "', stock=" + stockProduit + ", numero=" + numProduit + ", prix=" + prixProduit
							        + ", visible=" + visible + " WHERE id=" + id + ";");
						}
					} catch (SQLException ex)
					{
						JOptionPane.showMessageDialog(null,
						        "Erreur lors de la mise à jour de la base de données : " + ex.getMessage(),
						        "Erreur SQL", JOptionPane.ERROR_MESSAGE);
					}
					dispose();
				} catch (NumberFormatException ex)
				{
					JOptionPane.showMessageDialog(null,
					        "Le stock, le numéro du produit et le prix doivent être des nombres valides.",
					        "Erreur de validation", JOptionPane.ERROR_MESSAGE);
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
				try (Connection connection = DriverManager.getConnection("jdbc:sqlite:Database.db");
				        Statement statement = connection.createStatement())
				{

					statement.executeUpdate("DELETE FROM produit WHERE id = " + id);
				} catch (SQLException e1)
				{
					JOptionPane.showMessageDialog(null, "Impossible de lancer la demande !", "ERREUR...",
					        JOptionPane.ERROR_MESSAGE);
				}

				dispose();
			}
		});

		if (id != 0)
		{
			panneauPrincipal.add(boutonSupprimer);

			try (Connection connection = DriverManager.getConnection("jdbc:sqlite:Database.db");
			        Statement statement = connection.createStatement())
			{

				ResultSet resultSet = statement.executeQuery("SELECT * FROM produit WHERE id=" + id);

				String numeroSql = resultSet.getString("numero");
				String nomSql = resultSet.getString("libelle");
				String lieuSql = resultSet.getString("lieu");
				String stockSql = resultSet.getString("stock");
				String prixSql = resultSet.getString("prix").replace(".", ",");
				String visibleSql = resultSet.getString("visible");

				nom.setText(nomSql);
				switch (lieuSql)
				{
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

				if (visibleSql.equals("1"))
				{
					checkBoxVisibilite.setSelected(true);
				} else
				{
					checkBoxVisibilite.setSelected(false);
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