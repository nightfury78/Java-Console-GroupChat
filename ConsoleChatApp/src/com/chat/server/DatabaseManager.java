package com.chat.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseManager {
    // Using the SQL Authentication credentials we created
	private static final String DB_URL = "jdbc:sqlserver://127.0.0.1\\SQLEXPRESS:1433;"
	        + "databaseName=ChatApp;"
	        + "user=SuperUser;"
	        + "password=GlobalPass123!;"
	        + "encrypt=true;"
	        + "trustServerCertificate=true;"; 

	public static void logUserJoin(String username) {
	    String query = "INSERT INTO UserLogs (Username, Action, LogTime) VALUES (?, 'JOIN', GETDATE())";
	    try (Connection conn = DriverManager.getConnection(DB_URL);
	         PreparedStatement pstmt = conn.prepareStatement(query)) {
	        pstmt.setString(1, username);
	        pstmt.executeUpdate();
	        System.out.println("DB: Logged join for " + username);
	    } catch (SQLException e) {
	        System.err.println("DB Error: " + e.getMessage());
	    }
	}

    // THIS IS THE MISSING METHOD CAUSING YOUR ERROR
    public static void logUserLeave(String username) {
        String sql = "INSERT INTO user_activity (username, action_type, event_time) VALUES (?, 'LEFT', GETDATE())";
        executeLog(username, sql);
    }

    // Helper method to keep code clean
    private static void executeLog(String username, String sql) {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            pstmt.executeUpdate();
            System.out.println("DB Log Success: " + username);
            
        } catch (SQLException e) {
            System.err.println("Database Logging Error: " + e.getMessage());
        }
    }
}