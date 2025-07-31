package ressources;

import javax.swing.*;
import java.awt.*;

public class LoadingDialog extends JDialog {

    private final JLabel messageLabel;

    public LoadingDialog(Frame owner, String message) {
        super(owner, true);
        setUndecorated(true);

        JPanel panel = new JPanel();
        panel.setBackground(new Color(0, 0, 0, 150));
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));

        messageLabel = new JLabel(message, SwingConstants.CENTER);
        messageLabel.setForeground(Color.WHITE);
        messageLabel.setFont(messageLabel.getFont().deriveFont(Font.BOLD, 16f));

        panel.add(messageLabel, BorderLayout.CENTER);

        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        panel.add(progressBar, BorderLayout.SOUTH);

        setContentPane(panel);

        pack();

        int fixedHeight = 100;
        setSize(getWidth(), fixedHeight);

        setLocationRelativeTo(owner);
    }
}