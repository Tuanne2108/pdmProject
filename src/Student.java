import net.proteanit.sql.DbUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class Student {
    private JPanel Main;
    private JTable table1;
    private JButton signOutButton;
    private JComboBox<String> comboBox1;
    private JButton searchButton;
    private JTextField textField1;

    Connection con;
    PreparedStatement pst;

    private final Map<String, String> tableMap = new HashMap<>();

    public Student() {
        connect();

        String[] displayFields = {"Examination", "Student Grade", "IT Supports"};
        String[] tableNames = {"examination", "grade_student", "it_support"};

        for (int i = 0; i < displayFields.length; i++) {
            tableMap.put(displayFields[i], tableNames[i]);
        }

        comboBox1.setModel(new JComboBox<>(displayFields).getModel());

        comboBox1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String displayValue = (String) comboBox1.getSelectedItem();
                String tableName = tableMap.get(displayValue);
                loadSelectedTable(tableName);
            }
        });

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String searchTerm = textField1.getText();
                String displayValue = (String) comboBox1.getSelectedItem();
                String tableName = tableMap.get(displayValue);
                searchInTable(tableName, searchTerm);
            }
        });
    }

    public JPanel getMainPanel() {
        return Main;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Student");
        frame.setContentPane(new Student().getMainPanel());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public void connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost/pdmproject", "root", "21082002");
            System.out.println("Connected Successfully");
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    void loadSelectedTable(String tableName) {
        try {
            pst = con.prepareStatement("SELECT * FROM " + tableName);
            ResultSet rs = pst.executeQuery();
            table1.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    void searchInTable(String tableName, String searchTerm) {
        try {
            DatabaseMetaData metaData = con.getMetaData();
            ResultSet columns = metaData.getColumns(null, null, tableName, null);

            StringBuilder queryBuilder = new StringBuilder("SELECT * FROM ").append(tableName).append(" WHERE ");
            boolean first = true;
            while (columns.next()) {
                if (!first) {
                    queryBuilder.append(" OR ");
                }
                String columnName = columns.getString("COLUMN_NAME");
                queryBuilder.append(columnName).append(" LIKE ?");
                first = false;
            }

            pst = con.prepareStatement(queryBuilder.toString());

            columns.beforeFirst();
            int index = 1;
            while (columns.next()) {
                pst.setString(index++, STR."%\{searchTerm}%");
            }

            ResultSet rs = pst.executeQuery();
            table1.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
