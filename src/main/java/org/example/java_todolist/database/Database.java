package org.example.java_todolist.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Database {
    private static final String DB_URL = "jdbc:sqlite:rplbo_java.db";

    static {
        try {
            // Memuat driver SQLite secara eksplisit
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("Database path: " + new java.io.File("rplbo_java.db").getAbsolutePath());
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    // Method untuk update task
    public static void updateTask(int id, String title, String description, String deadline, boolean isDone, String category) {
        try (Connection conn = getConnection()) {
            String sql = "UPDATE tasks SET title = ?, description = ?, deadline = ?, is_done = ?, category = ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, title);
            stmt.setString(2, description);
            stmt.setString(3, deadline);
            stmt.setBoolean(4, isDone);
            stmt.setString(5, category);
            stmt.setInt(6, id);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
