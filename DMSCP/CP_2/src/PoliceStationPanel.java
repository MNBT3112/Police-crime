import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class PoliceStationPanel extends JPanel {
    private JTextField searchField;
    private JTable table;
    private DefaultTableModel tableModel;

    public PoliceStationPanel() {
        setLayout(new BorderLayout());

        // Search Panel
        JPanel searchPanel = new JPanel();
        searchPanel.add(new JLabel("Search: "));
        searchField = new JTextField(15);
        JButton searchBtn = new JButton("Search");
        JButton clearBtn = new JButton("Clear"); // Clear button added
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        searchPanel.add(clearBtn);
        add(searchPanel, BorderLayout.NORTH);

        // Table
        tableModel = new DefaultTableModel(new String[]{"Station ID", "Name", "Location"}, 0);
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel();
        JButton addBtn = new JButton("Add Station");
        JButton deleteBtn = new JButton("Delete Station");
        buttonPanel.add(addBtn);
        buttonPanel.add(deleteBtn);
        add(buttonPanel, BorderLayout.SOUTH);

        // Load all records initially
        loadAllStations();

        // Search and Clear Actions
        searchBtn.addActionListener(e -> searchPoliceStations());
        clearBtn.addActionListener(e -> loadAllStations());

        // Add/Delete Actions
        addBtn.addActionListener(e -> addPoliceStation());
        deleteBtn.addActionListener(e -> deletePoliceStation());
    }

    private void loadAllStations() {
        tableModel.setRowCount(0);
        try (Connection conn = DatabaseConnector.getConnection()) {
            String query = "SELECT * FROM police_stations";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                tableModel.addRow(new Object[]{rs.getInt("station_id"), rs.getString("station_name"), rs.getString("location")});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void searchPoliceStations() {
        String searchText = searchField.getText();
        tableModel.setRowCount(0);
        try (Connection conn = DatabaseConnector.getConnection()) {
            String query = "SELECT * FROM police_stations WHERE station_name LIKE ? OR station_id LIKE ? OR location LIKE ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, "%" + searchText + "%");
            stmt.setString(2, "%" + searchText + "%");
            stmt.setString(3, "%" + searchText + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                tableModel.addRow(new Object[]{rs.getInt("station_id"), rs.getString("station_name"), rs.getString("location")});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addPoliceStation() {
        String idText = JOptionPane.showInputDialog("Enter Station ID:");
        String name = JOptionPane.showInputDialog("Enter Station Name:");
        String location = JOptionPane.showInputDialog("Enter Location:");

        if (idText != null && name != null && location != null) {
            try (Connection conn = DatabaseConnector.getConnection()) {
                String query = "INSERT INTO police_stations (station_id, station_name, location) VALUES (?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setInt(1, Integer.parseInt(idText));
                stmt.setString(2, name);
                stmt.setString(3, location);
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Station Added!");
                loadAllStations();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void deletePoliceStation() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a station to delete.");
            return;
        }

        int stationID = (int) tableModel.getValueAt(selectedRow, 0);
        try (Connection conn = DatabaseConnector.getConnection()) {
            String query = "DELETE FROM police_stations WHERE station_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, stationID);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Station Deleted!");
            loadAllStations();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

//class CitizenCriminalRecordsPanel extends JPanel {}
//class CaseEvidencePanel extends JPanel {}
//class CitizenFiledCasesPanel extends JPanel {}
//class OfficerSolvedCasesPanel extends JPanel {}

