import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class PublicDataPanel extends JPanel {
    private JTextField searchField;
    private JTable table;
    private DefaultTableModel tableModel;

    public PublicDataPanel() {
        setLayout(new BorderLayout());

        // Search Panel
        JPanel searchPanel = new JPanel();
        searchPanel.add(new JLabel("Search by Station Name: "));
        searchField = new JTextField(15);
        JButton searchBtn = new JButton("Search");
        JButton clearBtn = new JButton("Clear");
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        searchPanel.add(clearBtn);
        add(searchPanel, BorderLayout.NORTH);

        // Table
        tableModel = new DefaultTableModel(new String[]{"Station Name", "Location", "Case ID", "Case Type"}, 0);
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Buttons Panel
        JPanel buttonPanel = new JPanel();
        JButton refreshBtn = new JButton("Refresh Data");
        buttonPanel.add(refreshBtn);
        add(buttonPanel, BorderLayout.SOUTH);

        // Load all data initially
        loadAllPublicData();

        // Add Actions
        searchBtn.addActionListener(e -> searchPublicData());
        clearBtn.addActionListener(e -> loadAllPublicData());
        refreshBtn.addActionListener(e -> loadAllPublicData());
    }

    private void loadAllPublicData() {
        tableModel.setRowCount(0);
        try (Connection conn = DatabaseConnector.getConnection()) {
            String query = "SELECT * FROM PublicData";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getString("station_name"),
                        rs.getString("location"),
                        rs.getInt("case_id"),
                        rs.getString("case_type")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void searchPublicData() {
        String searchText = searchField.getText();
        tableModel.setRowCount(0);
        try (Connection conn = DatabaseConnector.getConnection()) {
            String query = "SELECT * FROM PublicData WHERE station_name LIKE ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, "%" + searchText + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getString("station_name"),
                        rs.getString("location"),
                        rs.getInt("case_id"),
                        rs.getString("case_type")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}