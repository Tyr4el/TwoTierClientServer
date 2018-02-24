import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.io.FileInputStream;
import java.sql.*;
import java.util.Properties;

public class ResultSetTableModel extends AbstractTableModel {
    private Statement statement;
    private ResultSet resultSet;
    private ResultSetMetaData metaData;
    private int numberOfRows;

    private boolean connectedToDatabase = false;

    public ResultSetTableModel(MysqlDataSource dataSource, Connection connection, String query) throws SQLException,
            ClassNotFoundException {
        String[] querySplit = query.split(" ", 2);
        String firstWord = querySplit[0];

        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            connectedToDatabase = true;

            if (dataSource.getUser().equals("root")) {
                setQuery(query);
            }
            if (dataSource.getUser().equals("client") && !firstWord.equals("select")) {
                JOptionPane.showMessageDialog(null, "Cannot execute update commands.");
            }

        } catch (SQLException sql) {
            sql.printStackTrace();
            System.exit(1);
        }
    }

    public Class getColumnClass(int column) throws IllegalStateException {
        if (!connectedToDatabase) {
            throw new IllegalStateException("Not connected to database.  Please try again.");
        }

        try {
            String className = metaData.getColumnClassName(column + 1);
            return Class.forName(className);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Object.class;
    }

    public int getColumnCount() throws IllegalStateException {
        if (!connectedToDatabase) {
            throw new IllegalStateException("Not connected to database.  Please try again");
        }

        try {
            return metaData.getColumnCount();
        } catch (SQLException sql) {
            sql.printStackTrace();
        }

        return 0;
    }

    public String getColumnName(int column) throws IllegalStateException {
        if (!connectedToDatabase) {
            throw new IllegalStateException("Not connected to database.  Please try again");
        }

        try {
            return metaData.getColumnName(column + 1);
        } catch (SQLException sql) {
            sql.printStackTrace();
        }

        return "";
    }

    public int getRowCount() throws IllegalStateException {
        if (!connectedToDatabase) {
            throw new IllegalStateException("Not connected to database.  Please try again");
        }

        return numberOfRows;
    }

    public Object getValueAt(int row, int column) throws IllegalStateException {
        if (!connectedToDatabase) {
            throw new IllegalStateException("Not connected to database.  Please try again");
        }

        try {
            resultSet.next();
            resultSet.absolute(row + 1);
            return resultSet.getObject(column + 1);
        } catch (SQLException sql) {
            sql.printStackTrace();
        }

        return "";
    }

    public void setQuery(String query) throws SQLException, IllegalStateException {
        if (!connectedToDatabase) {
            throw new IllegalStateException("Not connected to database.  Please try again");
        }

        if (query.equals("")) {
            JOptionPane.showMessageDialog(null, "Please enter a query", "Error",
                    JOptionPane.WARNING_MESSAGE);
            throw new IllegalStateException("SQL statement is empty.  Please enter a statement");
        } else {
            try {
                resultSet = statement.executeQuery(query);
                metaData = resultSet.getMetaData();

                resultSet.last();
                numberOfRows = resultSet.getRow();
            } catch (SQLException sql) {
                sql.printStackTrace();
                JOptionPane.showMessageDialog(null, sql.getMessage(), "SQL Error",
                        JOptionPane.WARNING_MESSAGE);
            }

        }

        fireTableStructureChanged();
    }

    public void setUpdate(String query) throws SQLException, IllegalStateException {
        int res;

        if (!connectedToDatabase) {
            throw new IllegalStateException("Not connected to database.  Please try again");
        }

        res = statement.executeUpdate(query);

        fireTableStructureChanged();
    }
}


