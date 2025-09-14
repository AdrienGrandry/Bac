package ressources;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.*;

import options.ColorXml;

public class Style
{
	static ColorXml color = new ColorXml();

	public static void applyButtonStyle(JButton button)
	{
		button.setFont(new Font("Arial", Font.CENTER_BASELINE, 15));
		button.setBackground(Color.decode(color.xmlReader("bonton")));
		button.setForeground(Color.decode(color.xmlReader("bonton_texte")));
		applyHoverEffect(button);
	}

	public static void applyHoverEffect(final JButton button)
	{
		button.addMouseListener(new java.awt.event.MouseAdapter()
		{
			@Override
			public void mouseEntered(java.awt.event.MouseEvent e)
			{
				button.setBackground(Color.decode(color.xmlReader("bonton_selected")));
				button.setForeground(Color.decode(color.xmlReader("bonton_texte_selected")));
			}

			@Override
			public void mouseExited(java.awt.event.MouseEvent e)
			{
				button.setBackground(Color.decode(color.xmlReader("bonton")));
				button.setForeground(Color.decode(color.xmlReader("bonton_texte")));
			}
		});
	}

	public static void applyBoxStyle(JComboBox<String> comboBox)
	{
		comboBox.setSelectedIndex(0);
		comboBox.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		comboBox.setBackground(Color.decode(color.xmlReader("bonton")));
		comboBox.setForeground(Color.decode(color.xmlReader("bonton_texte")));
		comboBox.setBorder(BorderFactory.createLineBorder(Color.black));
		comboBox.setFocusable(false);
		comboBox.setFont(new Font("Arial", Font.BOLD, 18));

		comboBox.setRenderer(new DefaultListCellRenderer()
		{
			private static final long serialVersionUID = 1L;

			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
			        boolean cellHasFocus)
			{
				JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected,
				        cellHasFocus);
				label.setFont(new Font("Arial", Font.BOLD, 18));
				label.setForeground(Color.decode(color.xmlReader("foreground")));
				label.setHorizontalAlignment(SwingConstants.CENTER);
				if (isSelected)
				{
					label.setBackground(Color.decode(color.xmlReader("bonton_selected")));
					label.setForeground(Color.decode(color.xmlReader("bonton_texte_selected")));
				}
				return label;
			}
		});
	}

	public static void applyBoxStyleBoisson(JComboBox<Boisson> comboBox)
	{
		if (comboBox.getItemCount() > 0) {
			comboBox.setSelectedIndex(0);
		}
		comboBox.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		comboBox.setBackground(Color.decode(color.xmlReader("bonton")));
		comboBox.setForeground(Color.decode(color.xmlReader("bonton_texte")));
		comboBox.setBorder(BorderFactory.createLineBorder(Color.black));
		comboBox.setFocusable(false);

		comboBox.setRenderer(new DefaultListCellRenderer()
		{
			private static final long serialVersionUID = 1L;

			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
														  boolean cellHasFocus)
			{
				JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected,
						cellHasFocus);
				label.setFont(new Font("Arial", Font.BOLD, 14));
				label.setForeground(Color.decode(color.xmlReader("foreground")));
				label.setHorizontalAlignment(SwingConstants.CENTER);
				if (isSelected)
				{
					label.setBackground(Color.decode(color.xmlReader("bonton_selected")));
					label.setForeground(Color.decode(color.xmlReader("bonton_texte_selected")));
				}
				return label;
			}
		});
	}


	public static void applyPanelStyle(JPanel panel)
	{
		panel.setFont(new Font("Arial", Font.BOLD, 15));
		panel.setBackground(Color.decode(color.xmlReader("bonton")));
		panel.setForeground(Color.decode(color.xmlReader("bonton_texte")));
		panel.setBorder(BorderFactory.createLineBorder(Color.black));
	}

	public static void applyCheckBoxStyle(JCheckBox checkBox) {
		// Charge les icônes depuis les fichiers
		Icon uncheckedIcon = new ImageIcon(XmlConfig.getPath("nocheck"));
		Icon checkedIcon = new ImageIcon(XmlConfig.getPath("check"));

		checkBox.setIcon(uncheckedIcon);
		checkBox.setSelectedIcon(checkedIcon);

		// Optionnel : enlever la bordure autour de la checkbox
		checkBox.setBorderPainted(false);
		checkBox.setFocusPainted(false);
		checkBox.setContentAreaFilled(false);
	}

	public static void applyRadioStyle(JRadioButton radioButton) {
		// Charge les icônes depuis les fichiers
		Icon uncheckedIcon = new ImageIcon(XmlConfig.getPath("nocheck"));
		Icon checkedIcon = new ImageIcon(XmlConfig.getPath("check"));

		radioButton.setIcon(uncheckedIcon);
		radioButton.setSelectedIcon(checkedIcon);

		// Optionnel : enlever la bordure autour de la checkbox
		radioButton.setBorderPainted(false);
		radioButton.setFocusPainted(false);
		radioButton.setContentAreaFilled(false);
	}
}