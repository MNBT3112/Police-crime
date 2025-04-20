import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class PoliceStationOfficersPanel extends JPanel {
    private JTextField searchField;
    private JTable table;
    private DefaultTableModel tableModel;

    public PoliceStationOfficersPanel() {
        setLayout(new BorderLayout());

        // Search Panel
        JPanel searchPanel = new JPanel();
        searchPanel.add(new JLabel("Search by Officer Name: "));
        searchField = new JTextField(15);
        JButton searchBtn = new JButton("Search");
        JButton clearBtn = new JButton("Clear");
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        searchPanel.add(clearBtn);
        add(searchPanel, BorderLayout.NORTH);

        // Table
        tableModel = new DefaultTableModel(new String[]{"Officer ID", "Name", "Rank", "Station Name"}, 0);
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Buttons Panel
        JPanel buttonPanel = new JPanel();
        JButton addBtn = new JButton("Add Officer");
        JButton deleteBtn = new JButton("Delete Officer");
        buttonPanel.add(addBtn);
        buttonPanel.add(deleteBtn);
        add(buttonPanel, BorderLayout.SOUTH);

        // Load all officers initially
        loadAllOfficers();

        // Add/Delete/Search Actions
        searchBtn.addActionListener(e -> searchOfficers());
        clearBtn.addActionListener(e -> loadAllOfficers());
        addBtn.addActionListener(e -> addOfficer());
        deleteBtn.addActionListener(e -> deleteOfficer());
    }

    private void loadAllOfficers() {
        tableModel.setRowCount(0);
        try (Connection conn = DatabaseConnector.getConnection()) {
            String query = "SELECT o.officer_id, o.name, o.rank, p.station_name " +
                    "FROM officers o " +
                    "JOIN police_stations p ON o.station_id = p.station_id";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                tableModel.addRow(new Object[]{rs.getInt("officer_id"), rs.getString("name"),
                        rs.getString("rank"), rs.getString("station_name")});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void searchOfficers() {
        String searchText = searchField.getText();
        tableModel.setRowCount(0);
        try (Connection conn = DatabaseConnector.getConnection()) {
            String query = "SELECT o.officer_id, o.name, o.rank, p.station_name " +
                    "FROM officers o " +
                    "JOIN police_stations p ON o.station_id = p.station_id " +
                    "WHERE o.name LIKE ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, "%" + searchText + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                tableModel.addRow(new Object[]{rs.getInt("officer_id"), rs.getString("name"),
                        rs.getString("rank"), rs.getString("station_name")});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addOfficer() {
        String officerName = JOptionPane.showInputDialog("Enter Officer Name:");
        String rank = JOptionPane.showInputDialog("Enter Officer Rank:");
        String stationName = JOptionPane.showInputDialog("Enter Station Name:");

        if (officerName != null && rank != null && stationName != null) {
            try (Connection conn = DatabaseConnector.getConnection()) {
                // Find station ID by station name
                String stationQuery = "SELECT station_id FROM police_stations WHERE station_name = ?";
                PreparedStatement stationStmt = conn.prepareStatement(stationQuery);
                stationStmt.setString(1, stationName);
                ResultSet stationRs = stationStmt.executeQuery();

                if (stationRs.next()) {
                    int stationId = stationRs.getInt("station_id");

                    // Insert new officer record
                    String query = "INSERT INTO officers (name, rank, station_id) VALUES (?, ?, ?)";
                    PreparedStatement stmt = conn.prepareStatement(query);
                    stmt.setString(1, officerName);
                    stmt.setString(2, rank);
                    stmt.setInt(3, stationId);
                    stmt.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Officer Added!");
                    loadAllOfficers();
                } else {
                    JOptionPane.showMessageDialog(this, "Station not found!");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void deleteOfficer() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select an officer to delete.");
            return;
        }

        int officerId = (int) tableModel.getValueAt(selectedRow, 0);
        try (Connection conn = DatabaseConnector.getConnection()) {
            String query = "DELETE FROM officers WHERE officer_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, officerId);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Officer Deleted!");
            loadAllOfficers();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
