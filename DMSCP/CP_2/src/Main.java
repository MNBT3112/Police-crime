import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Police Database Management System");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1000, 600);

            JTabbedPane tabbedPane = new JTabbedPane();
            tabbedPane.addTab("Police Stations", new PoliceStationPanel());
            tabbedPane.addTab("Crime Statistics", new CrimeStatisticsPanel());
            tabbedPane.addTab("Citizen Criminal Record", new CitizenCriminalRecordsPanel());
            tabbedPane.addTab("Police Station Officer", new PoliceStationOfficersPanel());
            tabbedPane.addTab("Citizen Filed Cases", new CitizenFiledCasesPanel());
            tabbedPane.addTab("Public Data Panel", new PublicDataPanel());
            tabbedPane.addTab("Officer Data Panel", new OfficerDataPanel());
            frame.add(tabbedPane);
            frame.setVisible(true);
        });
    }
}
