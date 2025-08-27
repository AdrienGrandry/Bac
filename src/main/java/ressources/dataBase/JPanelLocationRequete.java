package ressources.dataBase;

import ressources.Message;
import options.ColorXml;
import ressources.Style;
import ressources.XmlConfig;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

/**
 * @author Grandry Adrien
 */
public class JPanelLocationRequete extends JPanel
{
    public static ColorXml color = new ColorXml();

    public JPanelLocationRequete()
    {}

    public static JPanel executeQueryAndReturnPanel(final String query, final int height, final int width)
    {
        final JPanel resultPanel = new JPanel();
        resultPanel.setLayout(new BorderLayout());

        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + XmlConfig.getPath("database"));
             Statement statement = connection.createStatement())
        {
            ResultSet resultSet = statement.executeQuery(query);
            final JTable table = buildTable(resultSet);
            table.getTableHeader().setBackground(Color.decode(color.xmlReader("background")));
            final JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

            scrollPane.setPreferredSize(new Dimension(width - 4, height - 4));
            scrollPane.getViewport().setBackground(Color.decode(color.xmlReader("background")));
            resultPanel.add(scrollPane, BorderLayout.CENTER);

        } catch (SQLException e)
        {
            Message.showErrorMessage("Erreur de la Base de Donnée",
                    "Impossible d'afficher l'élément à cause d'une erreur de la base de données !\n" + e.getMessage());
        }
        return resultPanel;
    }

    private static JTable buildTable(final ResultSet resultSet) throws SQLException {
        final ResultSetMetaData metaData = resultSet.getMetaData();
        final int columnCount = metaData.getColumnCount();
        final String[] columnNames = getColumnNames(resultSet);

        final DefaultTableModel model = buildTableModel(resultSet, columnNames);

        final JTable table = new JTable(model) {
            private static final long serialVersionUID = 1L;

            @Override
            public Component prepareRenderer(final TableCellRenderer renderer, final int row, final int column) {
                final Component component = super.prepareRenderer(renderer, row, column);

                if (isRowSelected(row)) {
                    component.setBackground(getSelectionBackground().darker());
                    component.setForeground(Color.WHITE);
                } else {
                    Color bgColor = getBackgroundColorForLieu(this, row);
                    component.setBackground(bgColor);
                    component.setForeground(Color.BLACK);
                }
                return component;
            }
        };

        // Transformer les titres en 1, 2 ou 3 lignes selon le nombre de mots
        for (int i = 0; i < table.getColumnCount(); i++) {
            String header = table.getColumnName(i).trim();
            String[] words = header.split(" ");

            StringBuilder html = new StringBuilder("<html>");
            if (words.length <= 3) {
                html.append(header); // 1 ligne
            } else if (words.length <= 5) {
                int mid = words.length / 2; // 2 lignes
                String line1 = String.join(" ", java.util.Arrays.copyOfRange(words, 0, mid));
                String line2 = String.join(" ", java.util.Arrays.copyOfRange(words, mid, words.length));
                html.append(line1).append("<br>").append(line2);
            } else {
                // 3 lignes
                int part1 = words.length / 3;
                int part2 = 2 * words.length / 3;
                String line1 = String.join(" ", java.util.Arrays.copyOfRange(words, 0, part1));
                String line2 = String.join(" ", java.util.Arrays.copyOfRange(words, part1, part2));
                String line3 = String.join(" ", java.util.Arrays.copyOfRange(words, part2, words.length));
                html.append(line1).append("<br>").append(line2).append("<br>").append(line3);
            }
            html.append("</html>");
            table.getColumnModel().getColumn(i).setHeaderValue(html.toString());
        }

        // Renderer pour l'en-tête avec couleurs personnalisées
        table.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                final Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                comp.setBackground(Color.decode(color.xmlReader("background_tab")));
                comp.setForeground(Color.decode(color.xmlReader("background_tab_texte")));
                setHorizontalAlignment(CENTER);
                setVerticalAlignment(BOTTOM);
                return comp;
            }
        });

        // Ajuster largeur colonnes selon contenu
        for (int i = 0; i < columnCount; i++) {
            final TableColumn column = table.getColumnModel().getColumn(i);
            TableCellRenderer headerRenderer = column.getHeaderRenderer();
            if (headerRenderer == null) {
                headerRenderer = table.getTableHeader().getDefaultRenderer();
            }
            final Component headerComp = headerRenderer.getTableCellRendererComponent(table, column.getHeaderValue(),
                    false, false, 0, 0);
            int maxWidth = headerComp.getPreferredSize().width;

            for (int row = 0; row < table.getRowCount(); row++) {
                final Component cellComp = table.getCellRenderer(row, i)
                        .getTableCellRendererComponent(table, table.getValueAt(row, i), false, false, row, i);
                maxWidth = Math.max(maxWidth, cellComp.getPreferredSize().width);
            }
            column.setPreferredWidth(maxWidth + 20);
        }

        // Bloquer le redimensionnement
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setResizable(false);
        }

        table.getTableHeader().setReorderingAllowed(false);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setRowHeight(20);
        table.getTableHeader().setPreferredSize(new Dimension(table.getTableHeader().getPreferredSize().width, 50));

        // Renderer cellules
        final DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;

            @Override
            public Component getTableCellRendererComponent(final JTable table, final Object value,
                                                           final boolean isSelected, final boolean hasFocus,
                                                           final int row, final int column) {
                final Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBorder(new EmptyBorder(0, 10, 0, 0));
                return comp;
            }
        };
        table.setDefaultRenderer(Object.class, cellRenderer);

        // Colonnes 5 à 16 en checkbox
        for (int i = 4; i <= 16; i++) {
            if (i < table.getColumnCount()) {
                table.getColumnModel().getColumn(i).setCellRenderer(new CheckBoxRenderer());
            }
        }

        return table;
    }

    private static String[] getColumnNames(final ResultSet resultSet) throws SQLException
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

    private static DefaultTableModel buildTableModel(final ResultSet resultSet, final String... columnNames)
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

            @Override
            public Class<?> getColumnClass(int columnIndex)
            {
                if (columnIndex >= 4 && columnIndex <= 16)
                {
                    return Boolean.class;
                }
                return Object.class;
            }
        };

        model.setColumnIdentifiers(columnNames);

        final Object[] rowData = new Object[columnNames.length];
        while (resultSet.next())
        {
            for (int col = 0; col < columnNames.length; col++)
            {
                Object value = resultSet.getObject(col + 1);

                if (col >= 4 && col <= 16)
                {
                    if (value instanceof Number)
                    {
                        rowData[col] = ((Number) value).intValue() != 0;
                    }
                    else if (value instanceof String)
                    {
                        String str = ((String) value).trim().toLowerCase();
                        rowData[col] = str.equals("true") || str.equals("1");
                    }
                    else
                    {
                        rowData[col] = false;
                    }
                }
                else
                {
                    rowData[col] = value;
                }
            }
            model.addRow(rowData);
        }
        return model;
    }

    static boolean isSpecificColumnExists(final JTable table, final String columnName)
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

    static String getColumnValue(final JTable table, final int row, final String columnName)
    {
        for (int i = 0; i < table.getColumnCount(); i++)
        {
            if (columnName.equalsIgnoreCase(table.getColumnName(i)))
            {
                final Object value = table.getValueAt(row, i);
                return (value != null) ? value.toString() : "";
            }
        }
        return "";
    }

    static public QueryResult executeQuery(final String query)
    {
        try
        {
            Connection connection = DriverManager.getConnection("jdbc:sqlite:" + XmlConfig.getPath("database"));
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            return new QueryResult(connection, resultSet);
        }
        catch (SQLException e)
        {
            if(e.getErrorCode() != 101)
            {
                Message.showErrorMessage("Erreur", e.getMessage());
            }
            return null;
        }
    }

    // Renderer checkbox centré avec style
    private static class CheckBoxRenderer extends JCheckBox implements TableCellRenderer
    {
        public CheckBoxRenderer()
        {
            setOpaque(false);
            setHorizontalAlignment(SwingConstants.CENTER);
            Style.applyCheckBoxStyle(this);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column)
        {
            if (value instanceof Boolean)
            {
                setSelected((Boolean) value);
            }

            if (isSelected)
            {
                setOpaque(true);  // Important pour que la couleur de fond apparaisse
                setBackground(table.getSelectionBackground());
                setForeground(table.getSelectionForeground());
            }
            else
            {
                Color bgColor = getBackgroundColorForLieu(table, row);
                setOpaque(true);  // Nécessaire ici aussi
                setBackground(bgColor);
                setForeground(Color.BLACK);
            }

            return this;
        }
    }

    // Méthode utilitaire pour récupérer la couleur de fond selon la valeur "Lieu"
    private static Color getBackgroundColorForLieu(JTable table, int row) {
        if (isSpecificColumnExists(table, "Lieu")) {
            final String typeValue = getColumnValue(table, row, "Lieu");
            if ("Salle".equalsIgnoreCase(typeValue)) {
                return Color.decode("#EEA5EA");
            } else if ("Cafétéria".equalsIgnoreCase(typeValue)) {
                return Color.decode("#EEC884");
            } else if ("Salle + Cafétéria".equalsIgnoreCase(typeValue)) {
                return Color.decode("#72b4e6");
            }
        }
        return table.getBackground();
    }

}