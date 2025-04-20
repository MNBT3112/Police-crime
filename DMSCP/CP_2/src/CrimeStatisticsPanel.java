import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class CrimeStatisticsPanel extends JPanel {
    private JTextField locationField;
    private JTable table;
    private DefaultTableModel tableModel;

    public CrimeStatisticsPanel() {
        setLayout(new BorderLayout());

        // Search Panel
        JPanel searchPanel = new JPanel();
        searchPanel.add(new JLabel("Enter Location: "));
        locationField = new JTextField(15);
        JButton searchBtn = new JButton("Search");
        JButton clearBtn = new JButton("Clear"); // Clear button added
        searchPanel.add(locationField);
        searchPanel.add(searchBtn);
        searchPanel.add(clearBtn);
        add(searchPanel, BorderLayout.NORTH);

        // Table
        tableModel = new DefaultTableModel(new String[]{"Crime Type", "Count"}, 0);
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Load all crime statistics initially
        loadAllCrimeStatistics();

        // Search Functionality
        searchBtn.addActionListener(e -> searchCrimeStatistics());
        clearBtn.addActionListener(e -> loadAllCrimeStatistics());
    }

    private void loadAllCrimeStatistics() {
        tableModel.setRowCount(0);
        try (Connection conn = DatabaseConnector.getConnection()) {
            String query = "SELECT crime_type, COUNT(*) AS count FROM crimes GROUP BY crime_type";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                tableModel.addRow(new Object[]{rs.getString("crime_type"), rs.getInt("count")});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void searchCrimeStatistics() {
        String locationText = locationField.getText();
        tableModel.setRowCount(0);
        try (Connection conn = DatabaseConnector.getConnection()) {
            String query = "SELECT crime_type, COUNT(*) AS count FROM crimes WHERE location = ? GROUP BY crime_type";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, locationText);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                tableModel.addRow(new Object[]{rs.getString("crime_type"), rs.getInt("count")});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
