import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import javax.sql.DataSource;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.xml.transform.Result;
import java.sql.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static javax.swing.JOptionPane.INFORMATION_MESSAGE;
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
    private JButton disconnectButton;
    private JButton btnClear;

    // Get username string
    private String username = txtUsername.getText();
    // Get password string
    private String password = txtPassword.getText();
    // Get database string
    private String database = (String)dropDatabase.getSelectedItem();

    private String query;

    private Connection connection;
    private ResultSet rs;
    private Statement statement;
    private boolean connectedToDatabase = false;

    private ResultSetTableModel tableModel;

    private MysqlDataSource dataSource = new MysqlDataSource();


    public App() {
        // Create Listener for Clear SQL button
        btnClearSQLCommand.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtASQLStatement.setText("");
            }
        });

        // Create Listener for Connect button
        // Load JDBC Driver
        // Establish Connection
        btnConnect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get username string
                username = txtUsername.getText();
                // Get password string
                password = txtPassword.getText();
                // Get database string
                database = (String)dropDatabase.getSelectedItem();

                // Set username, password, database
                dataSource.setUser(username);
                dataSource.setPassword(password);
                dataSource.setURL(database);

                try {
                    // Disable Connect button, drop-downs and text fields upon successful connection
                    // Otherwise, throw an exception
                    connection = dataSource.getConnection();
                    // Disable buttons once connected
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
                query = txtASQLStatement.getText();
                // Split the query statement to get the first word
                String[] querySplit = query.split(" ", 2);
                String firstWord = querySplit[0];

                try {
                    // Try getting a ResultSetTableModel Object
                    tableModel = new ResultSetTableModel(dataSource, connection, query);
                    if (firstWord.equals("select")) {
                        // Set the query if the first word in the query is "select"
                        tableModel.setQuery((query));
                        // Set the model
                        tblOutput.setModel(tableModel);
                    } else {
                        // Send an Update
                        tableModel.setUpdate(query);
                        // Set the model
                        tblOutput.setModel(tableModel);
                    }
                } catch (SQLException | ClassNotFoundException exception) {
                    exception.printStackTrace();
                }
            }
        });

        disconnectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!connectedToDatabase) {
                    return;
                }
                try {
                    connection.close();
                } catch (SQLException sql) {
                    sql.printStackTrace();
                }

                btnConnect.setEnabled(true);
                dropDatabase.setEnabled(true);
                dropDriver.setEnabled(true);
                txtUsername.setEditable(true);
                txtPassword.setEditable(true);
                connectedToDatabase = false;

                txtAConnectionStatus.setText("Not connected");
                JOptionPane.showMessageDialog(null, "Disconnected from database", "Success",
                        INFORMATION_MESSAGE);
            }
        });

        btnClear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultTableModel emptyTable = new DefaultTableModel();
                emptyTable.setNumRows(0);
                tblOutput.setModel(emptyTable);
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("SQL Client GUI");
        frame.setContentPane(new App().pnlMain);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1005, 600);
        frame.setResizable(false);
        //frame.pack();
        frame.setVisible(true);
    }

}
