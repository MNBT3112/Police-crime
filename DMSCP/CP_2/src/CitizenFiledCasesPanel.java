import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class CitizenFiledCasesPanel extends JPanel {
    private JTextField searchField;
    private JTable table;
    private DefaultTableModel tableModel;

    public CitizenFiledCasesPanel() {
        setLayout(new BorderLayout());

        // Search Panel
        JPanel searchPanel = new JPanel();
        searchPanel.add(new JLabel("Search by FIR ID: "));
        searchField = new JTextField(15);
        JButton searchBtn = new JButton("Search");
        JButton clearBtn = new JButton("Clear");
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        searchPanel.add(clearBtn);
        add(searchPanel, BorderLayout.NORTH);

        // Table
        tableModel = new DefaultTableModel(new String[]{"Case ID", "FIR ID", "Officer Name", "Case Type", "Status"}, 0);
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Buttons Panel
        JPanel buttonPanel = new JPanel();
        JButton addBtn = new JButton("Add Case");
        JButton deleteBtn = new JButton("Delete Case");
        buttonPanel.add(addBtn);
        buttonPanel.add(deleteBtn);
        add(buttonPanel, BorderLayout.SOUTH);

        // Load all cases initially
        loadAllCases();

        // Add/Delete/Search Actions
        searchBtn.addActionListener(e -> searchCases());
        clearBtn.addActionListener(e -> loadAllCases());
        addBtn.addActionListener(e -> addCase());
        deleteBtn.addActionListener(e -> deleteCase());
    }

    private void loadAllCases() {
        tableModel.setRowCount(0);
        try (Connection conn = DatabaseConnector.getConnection()) {
            String query = "SELECT c.case_id, c.fir_id, o.name AS officer_name, c.case_type, c.status " +
                    "FROM cases c " +
                    "JOIN officers o ON c.officer_id = o.officer_id";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                tableModel.addRow(new Object[]{rs.getInt("case_id"), rs.getInt("fir_id"),
                        rs.getString("officer_name"), rs.getString("case_type"), rs.getString("status")});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void searchCases() {
        String searchText = searchField.getText();
        tableModel.setRowCount(0);
        try (Connection conn = DatabaseConnector.getConnection()) {
            String query = "SELECT c.case_id, c.fir_id, o.name AS officer_name, c.case_type, c.status " +
                    "FROM cases c " +
                    "JOIN officers o ON c.officer_id = o.officer_id " +
                    "WHERE c.fir_id LIKE ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, "%" + searchText + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                tableModel.addRow(new Object[]{rs.getInt("case_id"), rs.getInt("fir_id"),
                        rs.getString("officer_name"), rs.getString("case_type"), rs.getString("status")});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addCase() {
        String firIdStr = JOptionPane.showInputDialog("Enter FIR ID:");
        String caseType = JOptionPane.showInputDialog("Enter Case Type:");
        String status = JOptionPane.showInputDialog("Enter Case Status (Pending/Under Investigation/Closed):");
        String officerName = JOptionPane.showInputDialog("Enter Officer Name:");

        if (firIdStr != null && caseType != null && status != null && officerName != null) {
            try (Connection conn = DatabaseConnector.getConnection()) {
                // Find officer ID by officer name
                String officerQuery = "SELECT officer_id FROM officers WHERE name = ?";
                PreparedStatement officerStmt = conn.prepareStatement(officerQuery);
                officerStmt.setString(1, officerName);
                ResultSet officerRs = officerStmt.executeQuery();

                if (officerRs.next()) {
                    int officerId = officerRs.getInt("officer_id");
                    int firId = Integer.parseInt(firIdStr);

                    // Insert new case record
                    String query = "INSERT INTO cases (fir_id, officer_id, case_type, status) VALUES (?, ?, ?, ?)";
                    PreparedStatement stmt = conn.prepareStatement(query);
                    stmt.setInt(1, firId);
                    stmt.setInt(2, officerId);
                    stmt.setString(3, caseType);
                    stmt.setString(4, status);
                    stmt.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Case Added!");
                    loadAllCases();
                } else {
                    JOptionPane.showMessageDialog(this, "Officer not found!");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void deleteCase() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a case to delete.");
            return;
        }

        int caseId = (int) tableModel.getValueAt(selectedRow, 0);
        try (Connection conn = DatabaseConnector.getConnection()) {
            String query = "DELETE FROM cases WHERE case_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, caseId);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Case Deleted!");
            loadAllCases();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

