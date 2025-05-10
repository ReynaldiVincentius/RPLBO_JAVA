package org.example.java_todolist.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TaskHistory {
    private final StringProperty id;
    private final StringProperty username;
    private final StringProperty originalTaskId;
    private final StringProperty title;
    private final StringProperty deadline;
    private final StringProperty category;
    private final StringProperty isDone;
    private final StringProperty description;
    private final StringProperty snapshotDate;

    public TaskHistory(String id, String username, String originalTaskId, String title,
                       String deadline, String category, String isDone, String description, String snapshotDate) {
        this.id = new SimpleStringProperty(id);
        this.username = new SimpleStringProperty(username);
        this.originalTaskId = new SimpleStringProperty(originalTaskId);
        this.title = new SimpleStringProperty(title);
        this.deadline = new SimpleStringProperty(deadline);
        this.category = new SimpleStringProperty(category);
        this.isDone = new SimpleStringProperty(isDone);
        this.description = new SimpleStringProperty(description);
        this.snapshotDate = new SimpleStringProperty(snapshotDate);
    }

    // Getter Property untuk TableView binding
    public StringProperty idProperty() { return id; }
    public StringProperty usernameProperty() { return username; }
    public StringProperty originalTaskIdProperty() { return originalTaskId; }
    public StringProperty titleProperty() { return title; }
    public StringProperty deadlineProperty() { return deadline; }
    public StringProperty categoryProperty() { return category; }
    public StringProperty isDoneProperty() { return isDone; }
    public StringProperty descriptionProperty() { return description; }
    public StringProperty snapshotDateProperty() { return snapshotDate; }

    // Optional: getter value (kalau dibutuhkan untuk logika lain)
    public String getTitle() { return title.get(); }
    public String getDeadline() { return deadline.get(); }
    public String getCategory() { return category.get(); }
    public String getDescription() { return description.get(); }
    public String getSnapshotDate() { return snapshotDate.get(); }
    public String getIsDone() { return isDone.get(); }

    // Menambahkan metode fetchHistoryForUser untuk mengambil data histori dari database
    public static List<TaskHistory> fetchHistoryForUser(String username) {
        List<TaskHistory> historyList = new ArrayList<>();

        // Koneksi ke database SQLite
        String url = "jdbc:sqlite:rplbo_java.db";  // Ganti dengan path yang sesuai
        String sql = "SELECT * FROM task_history WHERE username = ? ORDER BY snapshot_date DESC";  // Query untuk mengambil histori berdasarkan username

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);  // Set username parameter
            ResultSet rs = stmt.executeQuery();

            // Proses hasil query dan masukkan ke dalam list
            while (rs.next()) {
                String id = rs.getString("id");
                String originalTaskId = rs.getString("originalTaskId");
                String title = rs.getString("title");
                String deadline = rs.getString("deadline");
                String category = rs.getString("category");
                String isDone = rs.getString("isDone");
                String description = rs.getString("description");
                String snapshotDate = rs.getString("snapshotDate");

                TaskHistory history = new TaskHistory(id, username, originalTaskId, title,
                        deadline, category, isDone, description, snapshotDate);
                historyList.add(history);  // Tambahkan ke list histori
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return historyList;  // Kembalikan list histori
    }
}
