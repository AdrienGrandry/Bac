package options;

import ressources.ColorXml;
import ressources.ColorInfo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
    }

    private String colorToHex(Color color)
    {
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }
}
