package location.database;

import options.ColorXml;
import ressources.Style;
import ressources.dataBase.Requete;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
* @author Grandry Adrien
*/
public class Database extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Panel d'accès direct à la base de données.
	 * Permet d'effectuer des requêtes et mises à jour.
	 */
	public Database(JFrame unusedParentFrame)
	{
		super();

		final ColorXml color = new ColorXml();
		final Requete requete = new Requete();

		final JPanel entete = new JPanel();
		final JLabel titre = new JLabel("Base de données", SwingConstants.CENTER);
		final JTextArea textArea = new JTextArea();
		final JScrollPane scrollPane = new JScrollPane(textArea);
		final JPanel centerPanel = new JPanel(new BorderLayout());
		final JButton button = new JButton("VALIDER . . .");
		final JPanel tableauPanel = new JPanel();

		setLayout(new BorderLayout(10, 10));
		setBackground(Color.decode(color.xmlReader("background")));

		entete.setBackground(Color.decode(color.xmlReader("background")));
		entete.setLayout(new BorderLayout(10, 10));
		add(entete, BorderLayout.NORTH);

		titre.setFont(new Font("Arial", Font.BOLD, 40));
		titre.setForeground(Color.decode(color.xmlReader("foreground")));
		titre.setBorder(new EmptyBorder(10, 10, 10, 10));
		entete.add(titre, BorderLayout.NORTH);

		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setFont(new Font("Arial", Font.PLAIN, 15));
		scrollPane.setPreferredSize(new Dimension(400, 100));

		centerPanel.setBackground(Color.decode(color.xmlReader("background")));
		centerPanel.add(scrollPane, BorderLayout.CENTER);
		centerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
		entete.add(centerPanel, BorderLayout.CENTER);

		Style.applyButtonStyle(button);
		button.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		button.setPreferredSize(new Dimension(200, 50));
		entete.add(button, BorderLayout.SOUTH);

		tableauPanel.setBackground(Color.decode(color.xmlReader("background")));
		add(tableauPanel, BorderLayout.WEST);

		button.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent event)
			{
				tableauPanel.removeAll();
				final JPanel tab = requete.executeQueryAndReturnPanel(textArea.getText(), tableauPanel.getSize().height,
				        getSize().width, "pair_impair");

				tableauPanel.add(tab);

				revalidate();
				repaint();
			}
		});
	}
}