package start;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ressources.ColorXml;

public class Start extends JPanel
{
	private static final long serialVersionUID = 1L;
	static ColorXml color = new ColorXml();

	public Start(JFrame parentFrame)
	{
		setLayout(new GridBagLayout());
		setBackground(Color.decode(color.xmlReader("background")));

		try
		{
			BufferedImage img = ImageIO.read(new File("ressources/img/logo.png"));
			Image scaledImage = img.getScaledInstance(515, 309, Image.SCALE_SMOOTH);

			JLabel textLabel = new JLabel("Gesti'Bac");
			textLabel.setFont(new Font("Arial", Font.PLAIN, 50));
			textLabel.setForeground(Color.decode(color.xmlReader("bonton_texte")));

			JLabel picLabel = new JLabel(new ImageIcon(scaledImage));

			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.anchor = GridBagConstraints.CENTER;
			add(textLabel, gbc);

			gbc.gridy = 1;
			add(picLabel, gbc);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
