package ressources;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

public class Message
{

	// Méthode pour afficher un message d'erreur avec un nom de panel inexistant
	public static void showErrorMessage(final String errorName, final String description)
	{
		ImageIcon icon = new ImageIcon(XmlConfig.getPath("error"));
		JOptionPane.showMessageDialog(null, description, errorName, JOptionPane.INFORMATION_MESSAGE, icon);
	}

	// Méthode pour afficher un message de validation avec un nom de panel
	// inexistant
	public static void showValidMessage(String errorName, String description)
	{
		ImageIcon icon = new ImageIcon(XmlConfig.getPath("valid"));
		JOptionPane.showMessageDialog(null, description, errorName, JOptionPane.INFORMATION_MESSAGE, icon);
	}
}
