package options;

import ressources.ColorXml;
import ressources.ColorInfo;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;

public class Options extends JPanel
{
    private static final long serialVersionUID = 1L;

    public Options(JFrame parentFrame)
    {
        final ColorXml colorXml = new ColorXml();
        List<ColorInfo> colors = colorXml.getAllColors();

        setBackground(Color.decode(colorXml.xmlReader("background")));
        setLayout(new BorderLayout());

        JLabel titre = new JLabel("Options", JLabel.CENTER);
        titre.setFont(new Font("Arial", Font.BOLD, 40));
        titre.setForeground(Color.decode(colorXml.xmlReader("foreground")));
        add(titre, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new GridBagLayout());
        contentPanel.setBackground(Color.decode(colorXml.xmlReader("background")));
        
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);

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
            JLabel titleLabel = new JLabel("<html><body>" + labelText + "</body></html>");
            titleLabel.setFont(new Font("Arial", Font.PLAIN, 25));
            titleLabel.setForeground(Color.decode(colorXml.xmlReader("foreground")));
            titleLabel.setPreferredSize(new Dimension(0, 23));
            gbc.gridx = 0;
            gbc.weightx = 0.2;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            contentPanel.add(titleLabel, gbc);

            final JPanel exemplaire = new JPanel();
            exemplaire.setBackground(Color.decode(colorXml.xmlReader(color.getName())));
            exemplaire.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            exemplaire.setPreferredSize(new Dimension(0, 23));
            gbc.gridx = 1;
            gbc.weightx = 0.075;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            contentPanel.add(exemplaire, gbc);

            exemplaire.addMouseListener(new MouseAdapter()
            {
                @Override
                public void mouseClicked(MouseEvent e)
                {
                	Color selectedColor = JColorChooser.showDialog(null, "Changer la couleur (" + color.getDescription() + ")", getBackground());
                    if (selectedColor != null)
                    {
                        String newColorValue = colorToHex(selectedColor);
                        colorXml.updateColorValue(color.getName(), newColorValue);
                        exemplaire.setBackground(selectedColor);
                    }
                }
            });

            gbc.gridy++;
        }
        
        String labelText = "Enregistrement fichier :";
        JLabel titleLabel = new JLabel("<html><body>" + labelText + "</body></html>");
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
            	JFileChooser chooser = new JFileChooser();
                chooser.setDialogTitle("Sélectionnez un répertoire");
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                chooser.setAcceptAllFileFilterUsed(false);

                int returnValue = chooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedDirectory = chooser.getSelectedFile();
                    colorXml.updateLocated(selectedDirectory.getAbsolutePath());
                }
            }
        });
        
        gbc.gridx = 1;
        gbc.weightx = 0.075;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        contentPanel.add(panelLocated, gbc);
    }

    private String colorToHex(Color color)
    {
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }
}