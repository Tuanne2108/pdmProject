import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class editStudent extends JFrame{
    private JTextField id;
    private JTextField name;
    private JTextField dob;
    private JTextField tuition;
    private JTextField department;
    JPanel Main;
    private JButton cancelButton;
    private JButton updateButton;
    private Lecturer lecturer;

    private Connection con;
    private String tableName;
    private Object recordId;
    public editStudent(Lecturer lecturer, String tableName, Object recordId) {
        this.lecturer = lecturer;
        this.tableName = tableName;
        this.recordId = recordId;
        setTitle("Update Student");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        connect();
        loadRecord();

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateRecord();
            }
        });
    }

    private void connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost/pdmproject", "root", "21082002");
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadRecord() {
        try {
            String query = "SELECT * FROM " + tableName + " WHERE student_id = ?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setObject(1, recordId);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                id.setText(rs.getString("student_id"));
                name.setText(rs.getString("studentName"));
                dob.setText(rs.getString("doB"));
                tuition.setText(rs.getString("tuitionFee"));
                department.setText(rs.getString("studentDepartment"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateRecord() {
        try {
            String query = "UPDATE " + tableName + " SET student_id = ?, studentName = ?, doB = ?, tuitionFee = ?, studentDepartment = ? WHERE student_id = ?";
            PreparedStatement pst = con.prepareStatement(query);

            pst.setString(1, id.getText());
            pst.setString(2, name.getText());
            pst.setString(3, dob.getText());
            pst.setString(4, tuition.getText());
            pst.setString(5, department.getText());
            pst.setObject(6, recordId); // Set the recordId for the WHERE clause

            int rowsUpdated = pst.executeUpdate();
            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(null, "Record updated successfully");
                lecturer.loadSelectedTable("student");
                dispose();
            } else {
                JOptionPane.showMessageDialog(null, "Failed to update record");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
