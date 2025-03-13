package database;

import ressources.ColorXml;
import ressources.Message;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

public class Requete
{
	public ColorXml color = new ColorXml();

	public JPanel executeQueryAndReturnPanel(final String query, final int height, final int width,
	        final String styleCase)
	{
		final JPanel resultPanel = new JPanel();
		resultPanel.setLayout(new BorderLayout());

		try (Connection connection = DriverManager.getConnection("jdbc:sqlite:ressources/Database.db");
		        Statement statement = connection.createStatement();
		        ResultSet resultSet = statement.executeQuery(query))
		{
			final String lowerCaseString = query.toLowerCase(Locale.ROOT);
			if (lowerCaseString.startsWith("select"))
			{
				final JTable table = buildTable(resultSet, styleCase);
				table.getTableHeader().setBackground(Color.decode(color.xmlReader("background")));
				final JScrollPane scrollPane = new JScrollPane(table);
				scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
				scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

				scrollPane.setPreferredSize(new Dimension(width - 4, height - 4));
				scrollPane.getViewport().setBackground(Color.decode(color.xmlReader("background")));
				resultPanel.add(scrollPane, BorderLayout.CENTER);
			} else
			{
				final int affectedRows = statement.executeUpdate(query);
				final JLabel label = new JLabel(affectedRows + " ligne(s) affectée(s).", SwingConstants.CENTER);
				label.setFont(new Font("Arial", Font.BOLD, 20));
				label.setBackground(Color.decode(color.xmlReader("background")));
				resultPanel.add(label, BorderLayout.CENTER);
			}
		} catch (SQLException e)
		{
			Message.showErrorMessage("Erreur de la Base de Donnée", "Impossible d'afficher l'élément à cause\nd'une erreur de la base de données !");
		}
		return resultPanel;
	}

	private JTable buildTable(final ResultSet resultSet, final String styleCase) throws SQLException
	{
		final ResultSetMetaData metaData = resultSet.getMetaData();
		final int columnCount = metaData.getColumnCount();
		final String[] columnNames = getColumnNames(resultSet);

		final DefaultTableModel model = buildTableModel(resultSet, columnNames);

		model.setColumnIdentifiers(columnNames);
		final Object[] rowData = new Object[columnCount];
		while (resultSet.next())
		{
			for (int col = 0; col < columnCount; col++)
			{
				rowData[col] = resultSet.getObject(col + 1);
			}
			model.addRow(rowData);
		}

		final JTable table = new JTable(model)
		{
			private static final long serialVersionUID = 1L;

			@Override
			public Component prepareRenderer(final TableCellRenderer renderer, final int row, final int column)
			{
				final Component component = super.prepareRenderer(renderer, row, column);
				if (isRowSelected(row))
				{
					component.setBackground(getSelectionBackground().darker());
					component.setForeground(Color.white);
				} else
				{
					switch (styleCase)
					{
					case "pair_impair":
						component.setBackground((row % 2 == 0) ? Color.decode(color.xmlReader("background_tab_impair"))
						        : Color.decode(color.xmlReader("background_tab_pair")));
						component.setForeground(Color.BLACK);
						break;

					case "type_colonne":
						System.out.print("test");
						if (isSpecificColumnExists(this, "Type"))
						{
							final String typeValue = getColumnValue(this, row, "Type");
							System.out.print(typeValue);
							if ("Entrée".equalsIgnoreCase(typeValue) || "Commande".equalsIgnoreCase(typeValue))
							{
								component.setBackground(Color.decode("#98FB98"));
							} else if ("Sortie".equalsIgnoreCase(typeValue) || "Location".equalsIgnoreCase(typeValue))
							{
								component.setBackground(Color.decode("#f14949"));
							} else
							{
								component.setBackground(Color.decode("#72b4e6"));
							}
							component.setForeground(Color.BLACK);
						}
						break;

					default:
						component.setBackground(Color.WHITE);
						component.setForeground(Color.BLACK);
						break;
					}
				}
				return component;
			}
		};

		for (int i = 0; i < columnCount; i++)
		{
			final TableColumn column = table.getColumnModel().getColumn(i);

			TableCellRenderer headerRenderer = column.getHeaderRenderer();
			if (headerRenderer == null)
			{
				headerRenderer = table.getTableHeader().getDefaultRenderer();
			}
			final Component headerComp = headerRenderer.getTableCellRendererComponent(table, column.getHeaderValue(),
			        false, false, 0, 0);
			int maxWidth = headerComp.getPreferredSize().width;

			for (int row = 0; row < table.getRowCount(); row++)
			{
				final TableCellRenderer cellRenderer = table.getCellRenderer(row, i);
				final Component cellComp = cellRenderer.getTableCellRendererComponent(table, table.getValueAt(row, i),
				        false, false, row, i);
				maxWidth = Math.max(cellComp.getPreferredSize().width, maxWidth);
			}

			column.setPreferredWidth(maxWidth + 20);
		}

		for (int i = 0; i < table.getColumnCount(); i++)
		{
			table.getColumnModel().getColumn(i).setResizable(false);
		}

		table.getTableHeader().setReorderingAllowed(false);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setRowHeight(20);

		table.getTableHeader().setPreferredSize(new Dimension(table.getTableHeader().getPreferredSize().width, 30));

		table.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer()
		{
			private static final long serialVersionUID = 1L;

			@Override
			public Component getTableCellRendererComponent(final JTable table, final Object value,
			        final boolean isSelected, final boolean hasFocus, final int row, final int column)
			{
				final Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
				        column);
				comp.setBackground(Color.decode(color.xmlReader("background_tab")));
				comp.setForeground(Color.decode(color.xmlReader("background_tab_texte")));
				setBorder(new EmptyBorder(0, 10, 0, 0));
				return comp;
			}
		});

		final DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer()
		{
			private static final long serialVersionUID = 1L;

			@Override
			public Component getTableCellRendererComponent(final JTable table, final Object value,
			        final boolean isSelected, final boolean hasFocus, final int row, final int column)
			{
				final Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
				        column);
				setBorder(new EmptyBorder(0, 10, 0, 0));
				return comp;
			}
		};
		table.setDefaultRenderer(Object.class, cellRenderer);

		return table;
	}

	private String[] getColumnNames(final ResultSet resultSet) throws SQLException
	{
		final ResultSetMetaData metaData = resultSet.getMetaData();
		final int columnCount = metaData.getColumnCount();
		final String[] columnNames = new String[columnCount];
		for (int i = 1; i <= columnCount; i++)
		{
			columnNames[i - 1] = metaData.getColumnName(i);
		}
		return columnNames;
	}

	private DefaultTableModel buildTableModel(final ResultSet resultSet, final String... columnNames)
	        throws SQLException
	{
		final DefaultTableModel model = new DefaultTableModel()
		{
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column)
			{
				return false;
			}
		};
		model.setColumnIdentifiers(columnNames);

		final Object[] rowData = new Object[columnNames.length];
		while (resultSet.next())
		{
			for (int col = 0; col < columnNames.length; col++)
			{
				rowData[col] = resultSet.getObject(col + 1);
			}
			model.addRow(rowData);
		}
		return model;
	}

	private boolean isSpecificColumnExists(JTable table, String columnName)
    {
        for (int i = 0; i < table.getColumnCount(); i++)
        {
            if (columnName.equalsIgnoreCase(table.getColumnName(i)))
            {
                return true;
            }
        }
        return false;
    }

    private String getColumnValue(JTable table, int row, String columnName)
    {
        for (int i = 0; i < table.getColumnCount(); i++)
        {
            if (columnName.equalsIgnoreCase(table.getColumnName(i)))
            {
                Object value = table.getValueAt(row, i);
                return value != null ? value.toString() : "";
            }
        }
        return "";
    }

	public static QueryResult executeQuery(final String query) throws SQLException
	{
		final Connection connection = DriverManager.getConnection("jdbc:sqlite:ressources/Database.db");
		final Statement statement = connection.createStatement();
		final ResultSet resultSet = statement.executeQuery(query);

		return new QueryResult(connection, resultSet);
	}
}