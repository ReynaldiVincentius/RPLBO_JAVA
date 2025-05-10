package org.example.java_todolist.service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.java_todolist.database.Database;
import org.example.java_todolist.model.Task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class TaskService {

    // Mendapatkan tugas berdasarkan username
    public ObservableList<Task> getTasksForUser(String username) {
        ObservableList<Task> tasks = FXCollections.observableArrayList();
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT * FROM tasks WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Task task = new Task(
                        rs.getString("title"),
                        rs.getString("deadline"),
                        rs.getString("category"),
                        rs.getBoolean("is_done"),
                        rs.getString("description"),
                        rs.getString("id")
                );
                tasks.add(task);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tasks;
    }

    // Menghapus tugas dari database
    public void deleteTask(String taskId) {
        try (Connection conn = Database.getConnection()) {
            String sql = "DELETE FROM tasks WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, taskId);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
