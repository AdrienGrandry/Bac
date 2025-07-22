package options;

import principale.MainFrame;
import ressources.Style;
import ressources.XmlConfig;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class Options extends JFrame
{
	private static final long serialVersionUID = 1L;

	public Options()
	{
		super("Gesti'Bac - Options");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(600, 700);
		setLocationRelativeTo(null);
		setExtendedState(Frame.MAXIMIZED_BOTH);

		final ImageIcon icon = new ImageIcon(XmlConfig.getPath("logo"));
		setIconImage(icon.getImage());

		final ColorXml colorXml = new ColorXml();
		List<ColorInfo> colors = colorXml.getAllColors();

		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.setBackground(Color.decode(colorXml.xmlReader("background")));

		JPanel TitrePanel = new JPanel(new BorderLayout());
		TitrePanel.setBackground(Color.decode(colorXml.xmlReader("background")));

		// Panel bouton à gauche
		JPanel leftPanel = new JPanel(new GridBagLayout());
		leftPanel.setOpaque(false);
		JButton retour = new JButton("Retour");
		Style.applyButtonStyle(retour);
		retour.addActionListener(e -> {
			MainFrame mainFrame = new MainFrame();
			mainFrame.setVisible(true);
			dispose();
		});
		leftPanel.add(retour);
		TitrePanel.add(leftPanel, BorderLayout.WEST);

		// Panel texte centré
		JPanel centerPanel = new JPanel(new GridBagLayout());
		centerPanel.setOpaque(false);
		JLabel titre = new JLabel("Options", JLabel.CENTER);
		titre.setFont(new Font("Arial", Font.BOLD, 40));
		titre.setForeground(Color.decode(colorXml.xmlReader("foreground")));
		centerPanel.add(titre);
		TitrePanel.add(centerPanel, BorderLayout.CENTER);

		// Panel vide à droite pour équilibrer
		JPanel rightSpacer = new JPanel();
		rightSpacer.setOpaque(false);
		rightSpacer.setPreferredSize(leftPanel.getPreferredSize()); // même taille que leftPanel
		TitrePanel.add(rightSpacer, BorderLayout.EAST);


		mainPanel.add(TitrePanel, BorderLayout.NORTH);

		JPanel contentPanel = new JPanel(new GridBagLayout());
		contentPanel.setBackground(Color.decode(colorXml.xmlReader("background")));

		JScrollPane scrollPane = new JScrollPane(contentPanel);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBorder(null);
		mainPanel.add(scrollPane, BorderLayout.CENTER);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(10, 10, 10, 10);
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.gridx = 0;
		gbc.gridy = 0;

		for (final ColorInfo color : colors)
		{
			String labelText = color.getDescription() + " :";
			JLabel titleLabel = new JLabel(labelText);
			titleLabel.setFont(new Font("Arial", Font.PLAIN, 25));
			titleLabel.setForeground(Color.decode(colorXml.xmlReader("foreground")));
			titleLabel.setPreferredSize(new Dimension(0, 23));
			gbc.gridx = 0;
			gbc.weightx = 0.5;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			contentPanel.add(titleLabel, gbc);

			final JPanel exemplaire = new JPanel();
			exemplaire.setBackground(Color.decode(colorXml.xmlReader(color.getName())));
			exemplaire.setBorder(BorderFactory.createLineBorder(Color.BLACK));
			exemplaire.setPreferredSize(new Dimension(0, 23));
			gbc.gridx = 1;
			gbc.weightx = 0.5;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			contentPanel.add(exemplaire, gbc);

			exemplaire.addMouseListener(new MouseAdapter()
			{
				@Override
				public void mouseClicked(MouseEvent e)
				{
					Color selectedColor = ColorChooserWin11Style.chooseColor(
							null,
							"Changer la couleur (" + color.getDescription() + ")",
							getBackground()
					);

					if (selectedColor != null) {
						String newColorValue = colorToHex(selectedColor);
						colorXml.updateColorValue(color.getName(), newColorValue);
						exemplaire.setBackground(selectedColor);
					}
				}
			});

			gbc.gridy++;
		}

		String labelText = "Enregistrement fichier :";
		JLabel titleLabel = new JLabel(labelText);
		titleLabel.setFont(new Font("Arial", Font.PLAIN, 25));
		titleLabel.setForeground(Color.decode(colorXml.xmlReader("foreground")));
		titleLabel.setPreferredSize(new Dimension(0, 23));
		gbc.gridx = 0;
		gbc.weightx = 0.2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		contentPanel.add(titleLabel, gbc);

		JPanel panelLocated = new JPanel(new BorderLayout());
		panelLocated.setBackground(Color.decode(colorXml.xmlReader("background")));
		panelLocated.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		panelLocated.setPreferredSize(new Dimension(0, 23));
		JLabel located = new JLabel(colorXml.xmlLocated());
		located.setFont(new Font("Arial", Font.PLAIN, 15));
		panelLocated.add(located, BorderLayout.EAST);

		panelLocated.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				String returnValue = FolderChooserWin11Style.chooseFolder();
				if (returnValue != null)
				{
					colorXml.updateLocated(returnValue);
					located.setText(returnValue);
				}
			}
		});

		gbc.gridx = 1;
		gbc.weightx = 0.075;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		contentPanel.add(panelLocated, gbc);

		setContentPane(mainPanel);
		setVisible(true);
	}

	private String colorToHex(Color color)
	{
		return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
	}
}