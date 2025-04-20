import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class CitizenCriminalRecordsPanel extends JPanel {
    private JTextField searchField;
    private JTable table;
    private DefaultTableModel tableModel;

    public CitizenCriminalRecordsPanel() {
        setLayout(new BorderLayout());

        // Search Panel
        JPanel searchPanel = new JPanel();
        searchPanel.add(new JLabel("Search: "));
        searchField = new JTextField(15);
        JButton searchBtn = new JButton("Search");
        JButton clearBtn = new JButton("Clear");
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        searchPanel.add(clearBtn);
        add(searchPanel, BorderLayout.NORTH);

        // Table
        tableModel = new DefaultTableModel(new String[]{"Citizen ID", "Crime Description", "Date of Crime"}, 0);
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Buttons Panel
        JPanel buttonPanel = new JPanel();
        JButton addBtn = new JButton("Add Record");
        JButton deleteBtn = new JButton("Delete Record");
        buttonPanel.add(addBtn);
        buttonPanel.add(deleteBtn);
        add(buttonPanel, BorderLayout.SOUTH);

        // Load all records initially
        loadAllRecords();

        // Add/Delete/Search Actions
        searchBtn.addActionListener(e -> searchRecords());
        clearBtn.addActionListener(e -> loadAllRecords());
        addBtn.addActionListener(e -> addRecord());
        deleteBtn.addActionListener(e -> deleteRecord());
    }

    private void loadAllRecords() {
        tableModel.setRowCount(0);
        try (Connection conn = DatabaseConnector.getConnection()) {
            String query = "SELECT * FROM criminal_records";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("citizen_id"),
                        rs.getString("crime_description"),
                        rs.getDate("date_of_crime")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void searchRecords() {
        String searchText = searchField.getText();
        tableModel.setRowCount(0);
        try (Connection conn = DatabaseConnector.getConnection()) {
            String query = "SELECT * FROM criminal_records WHERE citizen_id LIKE ? OR crime_description LIKE ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, "%" + searchText + "%");
            stmt.setString(2, "%" + searchText + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("citizen_id"),
                        rs.getString("crime_description"),
                        rs.getDate("date_of_crime")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addRecord() {
        String citizenIdText = JOptionPane.showInputDialog("Enter Citizen ID:");

        if (citizenIdText == null || citizenIdText.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Citizen ID is required.");
            return;
        }

        try (Connection conn = DatabaseConnector.getConnection()) {
            int citizenId = Integer.parseInt(citizenIdText);

            // Check if the citizen ID exists in the citizens table
            String checkQuery = "SELECT COUNT(*) FROM citizens WHERE citizen_id = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setInt(1, citizenId);
            ResultSet rs = checkStmt.executeQuery();

            boolean citizenExists = false;
            if (rs.next() && rs.getInt(1) > 0) {
                citizenExists = true;
            }

            String citizenName = null;
            if (!citizenExists) {
                citizenName = JOptionPane.showInputDialog("Citizen does not exist. Enter Citizen Name:");
                if (citizenName == null || citizenName.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Citizen Name is required for new citizens.");
                    return;
                }

                // Insert the new citizen into the citizens table
                String insertCitizenQuery = "INSERT INTO citizens (citizen_id, name) VALUES (?, ?)";
                PreparedStatement insertCitizenStmt = conn.prepareStatement(insertCitizenQuery);
                insertCitizenStmt.setInt(1, citizenId);
                insertCitizenStmt.setString(2, citizenName);
                insertCitizenStmt.executeUpdate();
            }

            // Now, ask for crime details
            String crimeDescription = JOptionPane.showInputDialog("Enter Crime Description:");
            String dateOfCrimeText = JOptionPane.showInputDialog("Enter Date of Crime (YYYY-MM-DD):");

            if (crimeDescription == null || crimeDescription.trim().isEmpty() || dateOfCrimeText == null || dateOfCrimeText.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Crime details are required.");
                return;
            }

            // Insert the crime record
            String insertCrimeQuery = "INSERT INTO criminal_records (citizen_id, crime_description, date_of_crime) VALUES (?, ?, ?)";
            PreparedStatement insertCrimeStmt = conn.prepareStatement(insertCrimeQuery);
            insertCrimeStmt.setInt(1, citizenId);
            insertCrimeStmt.setString(2, crimeDescription);
            insertCrimeStmt.setDate(3, Date.valueOf(dateOfCrimeText)); // Convert string to Date
            insertCrimeStmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Record Added!");
            loadAllRecords();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding record", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid Citizen ID or Date format", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }




    private void deleteRecord() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a record to delete.");
            return;
        }

        int citizenId = (int) tableModel.getValueAt(selectedRow, 0);
        try (Connection conn = DatabaseConnector.getConnection()) {
            String query = "DELETE FROM criminal_records WHERE citizen_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, citizenId);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Record Deleted!");
            loadAllRecords();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
