package ressources;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

public class Message {

	// Méthode pour afficher un message d'erreur
	public static void showErrorMessage(final String errorName, final String description) {
		ImageIcon icon = new ImageIcon(XmlConfig.getPath("error"));
		JOptionPane.showMessageDialog(null, description, errorName, JOptionPane.INFORMATION_MESSAGE, icon);
	}

	// Méthode pour afficher un message de validation
	public static void showValidMessage(String errorName, String description) {
		ImageIcon icon = new ImageIcon(XmlConfig.getPath("valid"));
		JOptionPane.showMessageDialog(null, description, errorName, JOptionPane.INFORMATION_MESSAGE, icon);
	}

	// Méthode pour poser une question Oui/Non et retourner true/false
	public static boolean askYesNoQuestion(String question, String title) {
		ImageIcon icon = new ImageIcon(XmlConfig.getPath("question")); // Utilisation de ton icône "valid"

		Object[] options = {"Oui", "Non"}; // Boutons personnalisés
		int response = JOptionPane.showOptionDialog(
				null,
				question,
				title,
				JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE,
				icon,
				options,
				options[0] // bouton par défaut
		);

		return response == 0; // 0 correspond au bouton "Oui"
	}
}
