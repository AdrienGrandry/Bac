package database;
import ressources.ColorXml;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import java.awt.*;
import java.sql.*;

public class Requete
{
	ColorXml color = new ColorXml();
	
    public JPanel executeQueryAndReturnPanel(String query, int height, int width, String styleCase)
    {
        JPanel resultPanel = new JPanel();
        resultPanel.setLayout(new BorderLayout());

        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:Database.db");
             Statement statement = connection.createStatement())
        {
            if (query.toLowerCase().startsWith("select"))
            {
                ResultSet resultSet = statement.executeQuery(query);
                JTable table = buildTable(resultSet, styleCase);
                table.getTableHeader().setBackground(Color.decode(color.xmlReader("background")));
                JScrollPane scrollPane = new JScrollPane(table);
                scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

                scrollPane.setPreferredSize(new Dimension(width-4, height-4));
                scrollPane.getViewport().setBackground(Color.decode(color.xmlReader("background")));
                resultPanel.add(scrollPane, BorderLayout.CENTER);
            }
            else
            {
                int affectedRows = statement.executeUpdate(query);
                JLabel label = new JLabel(affectedRows + " ligne(s) affectée(s).", SwingConstants.CENTER);
                label.setFont(new Font("Arial", Font.BOLD, 20));
                label.setBackground(Color.decode(color.xmlReader("background")));
                resultPanel.add(label, BorderLayout.CENTER);
            }
        }
        catch (SQLException e)
        {
            JLabel errorLabel = new JLabel("Erreur : " + e.getMessage(), SwingConstants.CENTER);
            errorLabel.setFont(new Font("Arial", Font.BOLD, 20));
            errorLabel.setForeground(Color.RED);
            errorLabel.setBackground(Color.decode(color.xmlReader("background")));
            resultPanel.add(errorLabel, BorderLayout.CENTER);
        }

        return resultPanel;
    }

    private JTable buildTable(ResultSet resultSet, final String styleCase) throws SQLException
    {
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();
        String[] columnNames = new String[columnCount];
        for (int i = 1; i <= columnCount; i++)
        {
            columnNames[i - 1] = metaData.getColumnName(i);
        }

        DefaultTableModel model = new DefaultTableModel()
        {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column)
            {
                return false;
            }
        };
        model.setColumnIdentifiers(columnNames);
        while (resultSet.next())
		{
		    Object[] rowData = new Object[columnCount];
		    for (int col = 0; col < columnCount; col++)
		    {
		        rowData[col] = resultSet.getObject(col + 1);
		    }
		    model.addRow(rowData);
		}

        JTable table = new JTable(model)
        {
            private static final long serialVersionUID = 1L;

            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column)
            {
                Component component = super.prepareRenderer(renderer, row, column);
                if (isRowSelected(row))
                {
                    component.setBackground(getSelectionBackground().darker());
                    component.setForeground(Color.white);
                }
                else
                {
                	
	                switch (styleCase)
	                {
	                    case "pair_impair":
	                        component.setBackground((row % 2 == 0) ? Color.decode(color.xmlReader("background_tab_impair")) : Color.decode(color.xmlReader("background_tab_pair")));
	                        component.setForeground(Color.BLACK);
	                        break;
	
	                    case "type_colonne":
	                        if (isSpecificColumnExists(this, "Type"))
	                        {
	                            String typeValue = getColumnValue(this, row, "Type");
	                            if ("Entrée".equalsIgnoreCase(typeValue))
	                            {
	                                component.setBackground(Color.decode("#DFF0D8"));
	                                component.setForeground(Color.GREEN);
	                            }
	                            else if ("Sortie".equalsIgnoreCase(typeValue))
	                            {
	                                component.setBackground(Color.decode("#F2DEDE"));
	                                component.setForeground(Color.RED);
	                            }
	                            else
	                            {
	                                component.setBackground(Color.WHITE);
	                                component.setForeground(Color.BLACK);
	                            }
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
        
        for (int i = 0; i < columnCount; i++) {
            TableColumn column = table.getColumnModel().getColumn(i);
            int maxWidth = 0;

            // Calcul de la largeur maximale de la colonne
            TableCellRenderer headerRenderer = column.getHeaderRenderer();
            if (headerRenderer == null) {
                headerRenderer = table.getTableHeader().getDefaultRenderer();
            }
            Component headerComp = headerRenderer.getTableCellRendererComponent(table, column.getHeaderValue(), false, false, 0, 0);
            maxWidth = headerComp.getPreferredSize().width;

            // Parcourir les lignes pour ajuster la largeur
            for (int row = 0; row < table.getRowCount(); row++) {
                TableCellRenderer cellRenderer = table.getCellRenderer(row, i);
                Component cellComp = cellRenderer.getTableCellRendererComponent(table, table.getValueAt(row, i), false, false, row, i);
                maxWidth = Math.max(cellComp.getPreferredSize().width, maxWidth);
            }

            column.setPreferredWidth(maxWidth + 20);  // Ajoute une petite marge de 10 pixels
        }
        
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setResizable(false);
        }

        table.getTableHeader().setReorderingAllowed(false);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setRowHeight(50);
        
        table.getTableHeader().setPreferredSize(new Dimension(table.getTableHeader().getPreferredSize().width, 30));


        table.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer()
        {
            private static final long serialVersionUID = 1L;

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
            {
                Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                comp.setBackground(Color.decode(color.xmlReader("background_tab")));
                comp.setForeground(Color.decode(color.xmlReader("background_tab_texte")));
                setBorder(new EmptyBorder(0, 10, 0, 0));
                return comp;
            }
        });

        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer()
        {
            private static final long serialVersionUID = 1L;

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
            {
                Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBorder(new EmptyBorder(0, 10, 0, 0));
                return comp;
            }
        };
        table.setDefaultRenderer(Object.class, cellRenderer);

        return table;
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
}