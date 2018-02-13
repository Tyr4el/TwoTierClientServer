import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import javax.sql.DataSource;
import javax.swing.*;
import java.sql.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static javax.swing.JOptionPane.WARNING_MESSAGE;

public class App {
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

    public Connection connection;

    public App() {

        // Set clear for SQL Command Text Area
        btnClearSQLCommand.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtASQLStatement.setText("");
            }
        });

        // Set clear command for scroll pane
        btnClearTable.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scrlExecution.removeAll();
            }
        });

        // Set execute button
        // Load JDBC Driver
        // Establish Connection
        btnConnect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = txtUsername.getText();
                String password = txtPassword.getText();
                String database = (String)dropDatabase.getSelectedItem();

                MysqlDataSource dataSource = new MysqlDataSource();
                dataSource.setUser(username);
                dataSource.setPassword(password);
                dataSource.setURL(database);

                try {
                    connection = dataSource.getConnection();
                    btnConnect.setEnabled(false);
                    dropDatabase.setEnabled(false);
                    dropDriver.setEnabled(false);
                    txtUsername.setEditable(false);
                    txtPassword.setEditable(false);
                    JOptionPane.showMessageDialog(null, "Database connected!");
                    txtAConnectionStatus.setText(String.format("Connected to %s", database));
                } catch (SQLException sql) {
                    JOptionPane.showMessageDialog(null,"Error establishing connection",
                            "Error", WARNING_MESSAGE);
                    System.err.println(sql);
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
