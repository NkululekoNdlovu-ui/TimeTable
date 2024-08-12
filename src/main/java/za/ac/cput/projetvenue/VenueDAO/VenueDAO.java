package za.ac.cput.projetvenue.VenueDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import za.ac.cput.projetvenue.Connection.DBConnection;
import za.ac.cput.projetvenue.WokerClass.WorkerClass;

/**
 * Data Access Object for Venue operations.
 */
public class VenueDAO {
    private Connection conn; // Connection object to manage database connection
    private PreparedStatement pstmt; // PreparedStatement object to execute precompiled SQL queries

    // Constructor to establish database connection
    public VenueDAO() {
        try {
            this.conn = DBConnection.derbyConnection();  // Get the database connection
            createTable();  // Ensure the table is created
            JOptionPane.showMessageDialog(null, "Connection established and table ensured.");
        } catch (SQLException ex) {
            Logger.getLogger(VenueDAO.class.getName()).log(Level.SEVERE, null, ex); // Log any SQL exceptions
        }
    }

    // Method to create the VENUE table using PreparedStatement
    public void createTable() {
        String createTableSQL = "CREATE TABLE VENUE (" +
                                "Venue_Id DOUBLE PRIMARY KEY, " +
                                "Description VARCHAR(255), " +
                                "Capacity INT, " +
                                "Create_Date DATE)";  // Added Create_Date column
        
        try {
            pstmt = conn.prepareStatement(createTableSQL);  // Prepare the SQL statement
            pstmt.execute();  // Execute the SQL command
            JOptionPane.showMessageDialog(null, "Table 'VENUE' created successfully.");
        } catch (SQLException ex) {
            // Handle table already exists scenario
            if (ex.getSQLState().equals("X0Y32")) {
                JOptionPane.showMessageDialog(null, "Table 'VENUE' already exists.");
            } else {
                JOptionPane.showMessageDialog(null, "Error creating table: " + ex.getMessage());
            }
        } finally {
            if (pstmt != null) {
                try {
                    pstmt.close();  // Close the PreparedStatement
                } catch (SQLException ex) {
                    Logger.getLogger(VenueDAO.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

// Method to save a Venue object to the database
public boolean save(WorkerClass venue) {
    String checkSql = "SELECT COUNT(*) FROM VENUE WHERE Venue_Id = ?";
    String insertSql = "INSERT INTO VENUE (Venue_Id, Description, Capacity, Create_Date) VALUES (?, ?, ?, ?)";

    try {
        // Check if the Venue_Id already exists
        pstmt = conn.prepareStatement(checkSql);
        pstmt.setDouble(1, venue.getVenue_id());
        ResultSet rs = pstmt.executeQuery();
        rs.next();
        int count = rs.getInt(1);

        if (count > 0) {
            // If the record exists, show an error message and do not proceed with insertion
            JOptionPane.showMessageDialog(null, "Error: Venue_Id already exists.");
            return false; // Indicate that the save operation was not successful
        } else {
            // If the record does not exist, insert it
            pstmt.close(); // Close previous PreparedStatement
            pstmt = conn.prepareStatement(insertSql);
            pstmt.setDouble(1, venue.getVenue_id());
            pstmt.setString(2, venue.getDescription());
            pstmt.setInt(3, venue.getCapacity());
            pstmt.setDate(4, new java.sql.Date(System.currentTimeMillis())); // Set the current date
            int ok = pstmt.executeUpdate();
            if (ok > 0) {
                JOptionPane.showMessageDialog(null, "Venue inserted successfully.");
                return true; // Indicate that the save operation was successful
            } else {
                JOptionPane.showMessageDialog(null, "Error inserting venue.");
            }
        }
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
        Logger.getLogger(VenueDAO.class.getName()).log(Level.SEVERE, null, ex); // Log SQL exceptions
    } finally {
        if (pstmt != null) {
            try {
                pstmt.close(); // Close the PreparedStatement
            } catch (SQLException ex) {
                Logger.getLogger(VenueDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    return false;
}
public boolean doesVenueIdExist(double venueId) {
    String checkSql = "SELECT COUNT(*) FROM VENUE WHERE Venue_Id = ?";
    try {
        pstmt = conn.prepareStatement(checkSql);
        pstmt.setDouble(1, venueId);
        ResultSet rs = pstmt.executeQuery();
        rs.next();
        return rs.getInt(1) > 0;
    } catch (SQLException ex) {
        Logger.getLogger(VenueDAO.class.getName()).log(Level.SEVERE, null, ex);
        return false;
    } finally {
        if (pstmt != null) {
            try {
                pstmt.close();
            } catch (SQLException ex) {
                Logger.getLogger(VenueDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}

    // Method to delete a venue by its ID
    public void delete(double venueId) {
        String query = "DELETE FROM VENUE WHERE Venue_Id = ?";
        try {
            pstmt = this.conn.prepareStatement(query);
            pstmt.setDouble(1, venueId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (pstmt != null) {
                try {
                    pstmt.close(); // Close the PreparedStatement
                } catch (SQLException ex) {
                    Logger.getLogger(VenueDAO.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    // Method to fetch all venues from the database
    public List<WorkerClass> getAllVenues() {
        List<WorkerClass> venues = new ArrayList<>();
        String query = "SELECT * FROM VENUE";

        try {
            pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                double venueId = rs.getDouble("Venue_Id");
                String description = rs.getString("Description");
                int capacity = rs.getInt("Capacity");

                WorkerClass venue = new WorkerClass(venueId, description, capacity);
                venues.add(venue);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error fetching venues: " + ex.getMessage());
        } finally {
            if (pstmt != null) {
                try {
                    pstmt.close(); // Close the PreparedStatement
                } catch (SQLException ex) {
                    Logger.getLogger(VenueDAO.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        return venues;
    }

    // Method to update a Venue object in the database
    public void update(WorkerClass venue) {
        String updateSQL = "UPDATE VENUE SET Description = ?, Capacity = ?, Create_Date = ? WHERE Venue_Id = ?";

        try {
            pstmt = conn.prepareStatement(updateSQL);
            pstmt.setString(1, venue.getDescription());
            pstmt.setInt(2, venue.getCapacity());
            pstmt.setDate(3, new java.sql.Date(System.currentTimeMillis())); // Update the current date
            pstmt.setDouble(4, venue.getVenue_id());

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Venue updated successfully.");
            } else {
                JOptionPane.showMessageDialog(null, "No venue found with the provided Venue_Id.");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error updating venue: " + ex.getMessage());
        } finally {
            if (pstmt != null) {
                try {
                    pstmt.close(); // Close the PreparedStatement
                } catch (SQLException ex) {
                    Logger.getLogger(VenueDAO.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}


