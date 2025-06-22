package options;

import ressources.Message;

import javax.swing.*;
import java.awt.*;

public class ColorChooserWin11Style {
    public static Color chooseColor(Component parent, String title, Color initialColor) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            Message.showErrorMessage("Chargement Look And Feel", e.getMessage());
        }

        return JColorChooser.showDialog(parent, title, initialColor);
    }
}
