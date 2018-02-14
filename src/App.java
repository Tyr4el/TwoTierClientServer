import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import javax.sql.DataSource;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.xml.transform.Result;
import java.sql.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import static javax.swing.JOptionPane.WARNING_MESSAGE;

public class App extends JFrame {
    private JPanel pnlMain;
    private JComboBox dropDriver;
    private JComboBox dropDatabase;
    private JTextField txtUsername;
    private JTextField txtPassword;
    private JTextArea txtASQLStatement;
    private JLabel lblDriver;
    private JLabel lblDatabase;
    private JLabel lblUsername;
    private JLabel lblPassword;
    private JLabel lblDatabaseInfo;
    private JLabel lblSQLCommand;
    private JTextArea txtAConnectionStatus;
    private JButton btnConnect;
    private JButton btnExecute;
    private JButton btnClearSQLCommand;
    private JButton btnClearTable;
    private JScrollPane scrlExecution;
    private JTable tblOutput;

    public Connection connection;
    public ResultSet rs;
    public boolean connectedToDatabase = false;

    public static DefaultTableModel buildTableModel(ResultSet rs) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();

        // names of columns
        Vector<String> columnNames = new Vector<String>();
        int columnCount = metaData.getColumnCount();
        for (int column = 1; column <= columnCount; column++) {
            columnNames.add(metaData.getColumnName(column));
        }

        // data of the table
        Vector<Vector<Object>> data = new Vector<Vector<Object>>();
        while (rs.next()) {
            Vector<Object> vector = new Vector<Object>();
            for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                vector.add(rs.getObject(columnIndex));
            }
            data.add(vector);
        }

        return new DefaultTableModel(data, columnNames);
    }

    public App() {
        // Create Listener for Clear SQL button
        btnClearSQLCommand.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtASQLStatement.setText("");
            }
        });

        // Create Listener for Clear Table button
        btnClearTable.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scrlExecution.removeAll();
            }
        });

        // Create Listener for Connect button
        // Load JDBC Driver
        // Establish Connection
        btnConnect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get username string
                String username = txtUsername.getText();
                // Get password string
                String password = txtPassword.getText();
                // Get database string
                String database = (String)dropDatabase.getSelectedItem();

                // Set username, password, database
                MysqlDataSource dataSource = new MysqlDataSource();
                dataSource.setUser(username);
                dataSource.setPassword(password);
                dataSource.setURL(database);

                try {
                    // Disable Connect button, drop-downs and text fields upon successful connection
                    // Otherwise, throw an exception
                    connection = dataSource.getConnection();
                    btnConnect.setEnabled(false);
                    dropDatabase.setEnabled(false);
                    dropDriver.setEnabled(false);
                    txtUsername.setEditable(false);
                    txtPassword.setEditable(false);
                    connectedToDatabase = true;
                    JOptionPane.showMessageDialog(null, "Database connected!");
                    txtAConnectionStatus.setText(String.format("Connected to %s", database));
                } catch (SQLException sql) {
                    JOptionPane.showMessageDialog(null,"Error establishing connection",
                            "Error", WARNING_MESSAGE);
                    sql.printStackTrace();
                }

            }
        });

        // Create Listener for Execute button
        btnExecute.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) throws IllegalStateException {
                // Check if connected to the database
                if (!connectedToDatabase) {
                    JOptionPane.showMessageDialog(null, "Not connected to database.  Please " +
                            "connect and try again", "Error", WARNING_MESSAGE);
                    throw new IllegalStateException("Not connected to database");
                }

                // Set the query to the text in the text area
                String query = txtASQLStatement.getText();

                try {
                    // Create the statement
                    Statement statement = connection.createStatement();
                    // Execute the query
                    rs = statement.executeQuery(query);
                    JTable table = new JTable(buildTableModel(rs));
                    JOptionPane.showMessageDialog(null, new JScrollPane(table));
                } catch (SQLException sql) {
                    JOptionPane.showMessageDialog(null, "Error with query", "Error",
                            WARNING_MESSAGE);
                    sql.printStackTrace();
                }
            }
        });
    }

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        JFrame frame = new JFrame("SQL Client GUI");
        frame.setContentPane(new App().pnlMain);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(928, 600);
        frame.setResizable(false);
        //frame.pack();
        frame.setVisible(true);
    }

}
