package za.ac.cput.projetvenue.VenueGui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import za.ac.cput.projetvenue.VenueDAO.VenueDAO;
import za.ac.cput.projetvenue.WokerClass.WorkerClass;

public class VenueGui {

    public static void main(String[] args) {
        JFrame frame = new JFrame("VENUES");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400); // Adjusted size to fit the table better

        // Instantiate the DAO
        VenueDAO venueDAO = new VenueDAO();

        // Main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        // Table
        String[] columnNames = {"Venue Id", "Description", "Capacity"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        JTable table = new JTable(model);

        // Set table preferred size to show 5 rows
        table.setPreferredScrollableViewportSize(new Dimension(500, table.getRowHeight() * 5));
        table.setFillsViewportHeight(true);

        JScrollPane scrollPane = new JScrollPane(table);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 4));  // Adjusted for "Load Data" button
        JButton addNewButton = new JButton("Add New");
        JButton changeButton = new JButton("Change");
        JButton deleteButton = new JButton("Delete");
        JButton loadDataButton = new JButton("Load Data");  // New button
        buttonPanel.add(addNewButton);
        buttonPanel.add(changeButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(loadDataButton);  // Add new button to panel

        // Create a container panel for the table and button panel
        JPanel containerPanel = new JPanel();
        containerPanel.setLayout(new BorderLayout());
        containerPanel.add(scrollPane, BorderLayout.CENTER);
        containerPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add container panel to frame
        frame.getContentPane().add(containerPanel, BorderLayout.CENTER);

        // Load initial data into the table
        loadTableData(model, venueDAO);

        // Add New Button Action Listener
     addNewButton.addActionListener(new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        JPanel inputPanel = new JPanel(new GridLayout(3, 2));
        JTextField venueIdField = new JTextField();
        JTextField descriptionField = new JTextField();
        JTextField capacityField = new JTextField();

        inputPanel.add(new JLabel("Venue Id:"));
        inputPanel.add(venueIdField);
        inputPanel.add(new JLabel("Description:"));
        inputPanel.add(descriptionField);
        inputPanel.add(new JLabel("Capacity:"));
        inputPanel.add(capacityField);

        int result = JOptionPane.showConfirmDialog(frame, inputPanel,
                "Enter new venue details", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                // Convert input values
                double venueId = Double.parseDouble(venueIdField.getText());
                String description = descriptionField.getText();
                int capacity = Integer.parseInt(capacityField.getText());

                // Create WorkerClass object
                WorkerClass newVenue = new WorkerClass(venueId, description, capacity);

                // Check if the Venue_Id already exists before saving
                boolean exists = false;
                for (int i = 0; i < model.getRowCount(); i++) {
                    if ((double) model.getValueAt(i, 0) == venueId) {
                        exists = true;
                        break;
                    }
                }

                if (exists) {
                    JOptionPane.showMessageDialog(frame, "Error: Venue_Id already exists in the table.", "Duplicate ID", JOptionPane.ERROR_MESSAGE);
                } else {
                    // Save to database using VenueDAO
                    boolean saved = venueDAO.save(newVenue);

                    if (saved) {
                        // Save to table if successfully inserted into the database
                        model.addRow(new Object[]{venueId, description, capacity});
                    } else {
                        JOptionPane.showMessageDialog(frame, "Failed to insert venue. Venue ID might already exist in the database.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Please enter valid numerical values for Venue Id and Capacity.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
});

        // Delete Button Action Listener
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    // Get the Venue Id from the selected row
                    double venueId = (double) model.getValueAt(selectedRow, 0);

                    // Delete from database using VenueDAO
                    try {
                        venueDAO.delete(venueId);

                        // If successful, remove the row from the table
                        model.removeRow(selectedRow);
                        JOptionPane.showMessageDialog(frame, "Venue deleted successfully.");
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(frame, "Error deleting venue: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Please select a row to delete.", "No Row Selected", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        // Change Button Action Listener
changeButton.addActionListener(new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            // Current values from the selected row
            double currentVenueId = (double) model.getValueAt(selectedRow, 0);
            String currentDescription = (String) model.getValueAt(selectedRow, 1);
            int currentCapacity = (int) model.getValueAt(selectedRow, 2);

            // Input panel to change Venue details
            JPanel inputPanel = new JPanel(new GridLayout(3, 2));
            JTextField venueIdField = new JTextField(String.valueOf(currentVenueId));
            JTextField descriptionField = new JTextField(currentDescription);
            JTextField capacityField = new JTextField(String.valueOf(currentCapacity));

            inputPanel.add(new JLabel("Venue Id:"));
            inputPanel.add(venueIdField);
            inputPanel.add(new JLabel("Description:"));
            inputPanel.add(descriptionField);
            inputPanel.add(new JLabel("Capacity:"));
            inputPanel.add(capacityField);

            int result = JOptionPane.showConfirmDialog(frame, inputPanel,
                    "Change venue details", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                try {
                    // Get new values from the input fields
                    double newVenueId = Double.parseDouble(venueIdField.getText());
                    String newDescription = descriptionField.getText();
                    int newCapacity = Integer.parseInt(capacityField.getText());

                    // Create WorkerClass object with updated values
                    WorkerClass updatedVenue = new WorkerClass(newVenueId, newDescription, newCapacity);

                    // Check for duplicate IDs in the table
                    boolean idExistsInTable = false;
                    for (int i = 0; i < model.getRowCount(); i++) {
                        if ((double) model.getValueAt(i, 0) == newVenueId && i != selectedRow) {
                            idExistsInTable = true;
                            break;
                        }
                    }

                    // If ID exists in table, show error message and abort update
                    if (idExistsInTable) {
                        JOptionPane.showMessageDialog(frame, "Error: Venue_Id already exists in the table.", "Duplicate ID", JOptionPane.ERROR_MESSAGE);
                    } else {
                        // Check for duplicate IDs in the database
                        boolean idExistsInDatabase = venueDAO.doesVenueIdExist(newVenueId);

                        if (idExistsInDatabase && newVenueId != currentVenueId) {
                            JOptionPane.showMessageDialog(frame, "Error: Venue_Id already exists in the database.", "Duplicate ID", JOptionPane.ERROR_MESSAGE);
                        } else {
                            // Update the database using VenueDAO
                            venueDAO.update(updatedVenue);

                            // Update the table with new values
                            model.setValueAt(newVenueId, selectedRow, 0);
                            model.setValueAt(newDescription, selectedRow, 1);
                            model.setValueAt(newCapacity, selectedRow, 2);

                            JOptionPane.showMessageDialog(frame, "Venue updated successfully.");
                        }
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Please enter valid numerical values for Venue Id and Capacity.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(frame, "Please select a row to change.", "No Row Selected", JOptionPane.WARNING_MESSAGE);
        }
    }
});


        // Load Data Button Action Listener
        loadDataButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadTableData(model, venueDAO);
            }
        });

        frame.setVisible(true);
    }

    private static void loadTableData(DefaultTableModel model, VenueDAO venueDAO) {
        // Clear existing rows
        model.setRowCount(0);

        try {
            // Fetch data from database
            List<WorkerClass> venues = venueDAO.getAllVenues(); // Get all venues from DAO

            // Add rows to the table
            for (WorkerClass venue : venues) {
                model.addRow(new Object[]{venue.getVenue_id(), venue.getDescription(), venue.getCapacity()});
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error loading data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}