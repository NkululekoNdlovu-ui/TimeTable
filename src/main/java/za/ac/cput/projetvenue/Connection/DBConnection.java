/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package za.ac.cput.projetvenue.Connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author DELL
 */
public class DBConnection {
     public static Connection derbyConnection() throws SQLException{
      String database_url = "jdbc:derby://localhost:1527/Project";
      String username = "user1";
      String password = "password";
      
      Connection conn = DriverManager.getConnection(database_url, username, password);
    
        return conn;
    
}
}
