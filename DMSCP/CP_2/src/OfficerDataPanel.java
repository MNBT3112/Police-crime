import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class OfficerDataPanel extends JPanel {
    private JTextField searchField;
    private JTable table;
    private DefaultTableModel tableModel;

    public OfficerDataPanel() {
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

        // Table with enhanced scrolling
        tableModel = new DefaultTableModel(new String[]{
                "Case ID", "Case Type", "Officer ID", "Officer Name",
                "Station ID", "Station Name", "Location", "FIR ID",
                "Filing Date", "Citizen ID", "Citizen Name", "Address",
                "Record ID", "Crime Description", "Evidence ID",
                "Evidence Description", "Evidence Type", "Arrest ID",
                "Arrest Date", "Incident ID", "Incident Description",
                "Incident Date"
        }, 0);
        table = new JTable(tableModel);

        // Configure table for horizontal scrolling
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); // Crucial for horizontal scrolling
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        // Set preferred size to ensure scrollbars appear when needed
        scrollPane.setPreferredSize(new Dimension(1200, 400));

        add(scrollPane, BorderLayout.CENTER);

        // Buttons Panel
        JPanel buttonPanel = new JPanel();
        JButton refreshBtn = new JButton("Refresh Data");
        buttonPanel.add(refreshBtn);
        add(buttonPanel, BorderLayout.SOUTH);

        // Load all data initially
        loadAllOfficerData();

        // Add Actions
        searchBtn.addActionListener(e -> searchOfficerData());
        clearBtn.addActionListener(e -> loadAllOfficerData());
        refreshBtn.addActionListener(e -> loadAllOfficerData());
    }

    private void loadAllOfficerData() {
        tableModel.setRowCount(0);
        try (Connection conn = DatabaseConnector.getConnection()) {
            String query = "SELECT * FROM OfficerData";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("case_id"),
                        rs.getString("case_type"),
                        rs.getInt("officer_id"),
                        rs.getString("officer_name"),
                        rs.getInt("station_id"),
                        rs.getString("station_name"),
                        rs.getString("location"),
                        rs.getInt("fir_id"),
                        rs.getDate("filing_date"),
                        rs.getInt("citizen_id"),
                        rs.getString("citizen_name"),
                        rs.getString("address"),
                        rs.getInt("record_id"),
                        rs.getString("crime_description"),
                        rs.getInt("evidence_id"),
                        rs.getString("evidence_description"),
                        rs.getString("evidence_type"),
                        rs.getInt("arrest_id"),
                        rs.getDate("arrest_date"),
                        rs.getInt("incident_id"),
                        rs.getString("incident_description"),
                        rs.getDate("incident_date")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void searchOfficerData() {
        String searchText = searchField.getText();
        tableModel.setRowCount(0);
        try (Connection conn = DatabaseConnector.getConnection()) {
            String query = "SELECT * FROM OfficerData WHERE officer_name LIKE ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, "%" + searchText + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("case_id"),
                        rs.getString("case_type"),
                        rs.getInt("officer_id"),
                        rs.getString("officer_name"),
                        rs.getInt("station_id"),
                        rs.getString("station_name"),
                        rs.getString("location"),
                        rs.getInt("fir_id"),
                        rs.getDate("filing_date"),
                        rs.getInt("citizen_id"),
                        rs.getString("citizen_name"),
                        rs.getString("address"),
                        rs.getInt("record_id"),
                        rs.getString("crime_description"),
                        rs.getInt("evidence_id"),
                        rs.getString("evidence_description"),
                        rs.getString("evidence_type"),
                        rs.getInt("arrest_id"),
                        rs.getDate("arrest_date"),
                        rs.getInt("incident_id"),
                        rs.getString("incident_description"),
                        rs.getDate("incident_date")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}