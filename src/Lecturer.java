import net.proteanit.sql.DbUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Map;
import java.util.HashMap;

public class Lecturer {
    private JButton signOutButton;
    private JTable result;
    private JTextField textField1;
    private JButton executeButton;
    private JPanel Main;
    private JComboBox<String> selectField;
    private static Lecturer instance;

    static final String connectionUrl =
        "jdbc:sqlserver://localhost:1433;databaseName=pdmProject;user=sa;password=21082002;encrypt=true;trustServerCertificate=true;";
    private final Map<String, String> tableMap = new HashMap<>();

    private Lecturer() {
        JFrame frame = new JFrame("Lecturer");
        frame.setContentPane(Main);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        String[] displayFields = {"Student", "Student Grade", "IT Supports", "Question", "Examination", "View Examination"};
        String[] tableNames = {"student", "grade_student", "it_support", "question", "examination", "view_examination"};

        for (int i = 0; i < displayFields.length; i++) {
            tableMap.put(displayFields[i], tableNames[i]);
        }
        selectField.setModel(new JComboBox<>(displayFields).getModel());

        executeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                executeButton.setEnabled(false);
                String query = textField1.getText();
                if (query.isEmpty()) {
                    JOptionPane.showMessageDialog(
                        null, "Search field cannot be empty.", "Warning", JOptionPane.WARNING_MESSAGE);
                    executeButton.setEnabled(true);
                    return;
                }
                SwingWorker<Void, Void> worker = new SwingWorker<>() {
                    @Override
                    protected Void doInBackground() {
                        try {
                            executeQuery(query, result);
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(
                                null, "Error executing query: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        } finally {
                            executeButton.setEnabled(true);
                        }
                        return null;
                    }
                };
                worker.execute();
            }
        });

        selectField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String displayValue = (String) selectField.getSelectedItem();
                String tableName = tableMap.get(displayValue);
                loadSelectedTable(tableName);
            }
        });
    }

    private void loadSelectedTable(String tableName) {
        try (Connection con = getConnection();
            PreparedStatement pst = con.prepareStatement("SELECT * FROM " + tableName);
            ResultSet rs = pst.executeQuery()) {

            result.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(connectionUrl);
    }

    public static synchronized Lecturer getInstance() {
        if (instance == null) {
            instance = new Lecturer();
        }
        return instance;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Lecturer::getInstance);
    }

    public static void executeQuery(String query, JTable resultTable) throws SQLException {
        try (Connection con = DriverManager.getConnection(connectionUrl);
            PreparedStatement stmt = con.prepareStatement(query)) {

            int rowsAffected = stmt.executeUpdate();
            JOptionPane.showMessageDialog(
                null, "Query executed successfully. Rows affected: " + rowsAffected, "Information", JOptionPane.INFORMATION_MESSAGE);
            resultTable.setModel(new DefaultTableModel());

        } catch (SQLException ex) {
            System.out.println("Error executing query: " + ex.getMessage());
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                null, "Error executing query: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

