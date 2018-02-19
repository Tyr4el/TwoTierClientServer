import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

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

    public ResultSetTableModel(Connection connection, String query) throws SQLException, ClassNotFoundException {
        Properties properties = new Properties();
        FileInputStream fileIn = null;
        MysqlDataSource dataSource = null;

        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            connectedToDatabase = true;

            setQuery(query);

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

        resultSet = statement.executeQuery(query);
        metaData = resultSet.getMetaData();

        resultSet.last();
        numberOfRows = resultSet.getRow();

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


