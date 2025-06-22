package options;

import ressources.Message;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.io.File;

public class FolderChooserWin11Style {
    public static String chooseFolder() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            Message.showErrorMessage("Chargement Look And Feel", e.getMessage());
        }

        JFileChooser chooser = new JFileChooser(FileSystemView.getFileSystemView());
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setDialogTitle("Sélectionner un dossier");

        int returnValue = chooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFolder = chooser.getSelectedFile();
            return selectedFolder.getAbsolutePath();
        }
        return null;
    }
}