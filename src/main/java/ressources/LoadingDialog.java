package ressources;

import javax.swing.*;
import java.awt.*;

public class LoadingDialog extends JDialog {

    private final JLabel messageLabel;

    public LoadingDialog(Frame owner, String message) {
        super(owner, true);  // modal

        setUndecorated(true);  // sans bordure ni barre titre
        setSize(200, 100);
        setLocationRelativeTo(owner);

        JPanel panel = new JPanel();
        panel.setBackground(new Color(0, 0, 0, 150));  // fond semi-transparent
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));

        messageLabel = new JLabel(message, SwingConstants.CENTER);
        messageLabel.setForeground(Color.WHITE);
        messageLabel.setFont(messageLabel.getFont().deriveFont(Font.BOLD, 16f));

        panel.add(messageLabel, BorderLayout.CENTER);

        // Optionnel : ajouter un JProgressBar indéterminé
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        panel.add(progressBar, BorderLayout.SOUTH);

        setContentPane(panel);
    }

    // Mise à jour du message si besoin
    public void setMessage(String message) {
        messageLabel.setText(message);
    }
}