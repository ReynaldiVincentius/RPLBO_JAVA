package org.example.java_todolist.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.example.java_todolist.database.Database;
import org.example.java_todolist.model.Task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

public class ProjectCardController {

    @FXML private Label projectNameLabel;
    @FXML private Label projectDescriptionLabel;
    @FXML private VBox taskListContainer;

    @FXML private Button totalTaskBtn;
    @FXML private Button doneBtn;
    @FXML private Button progressBtn;
    @FXML private Button cancelledBtn;
    @FXML private Button categoryCountBtn;

    public void setProject(int projectId, String name, String description) {
        projectNameLabel.setText(name);
        projectDescriptionLabel.setText(description != null ? description : "");

        loadRecentTasks(projectId);
        loadSummaryInfo(projectId);
    }

    private void loadRecentTasks(int projectId) {
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT * FROM tasks WHERE project_id = ? ORDER BY id DESC LIMIT 3";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, projectId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Task task = new Task(
                        rs.getString("title"),
                        rs.getString("deadline"),
                        rs.getString("category"),
                        rs.getString("status"), // langsung pakai status
                        rs.getString("description"),
                        String.valueOf(rs.getInt("id"))
                );

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/java_todolist/view/task_card.fxml"));
                Node taskCard = loader.load();
                TaskCardController controller = loader.getController();
                controller.setTask(task);

                taskListContainer.getChildren().add(taskCard);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadSummaryInfo(int projectId) {
        try (Connection conn = Database.getConnection()) {
            // Total task
            String totalSql = "SELECT COUNT(*) as total FROM tasks WHERE project_id = ?";
            PreparedStatement totalStmt = conn.prepareStatement(totalSql);
            totalStmt.setInt(1, projectId);
            ResultSet totalRs = totalStmt.executeQuery();
            int total = totalRs.next() ? totalRs.getInt("total") : 0;

            // Hitung jumlah status (status sekarang sudah dalam bentuk string langsung)
            Map<String, Integer> statusMap = new HashMap<>();
            String statusSql = "SELECT status, COUNT(*) as count FROM tasks WHERE project_id = ? GROUP BY status";
            PreparedStatement statusStmt = conn.prepareStatement(statusSql);
            statusStmt.setInt(1, projectId);
            ResultSet statusRs = statusStmt.executeQuery();
            while (statusRs.next()) {
                String status = statusRs.getString("status");
                int count = statusRs.getInt("count");
                statusMap.put(status.toUpperCase(), count); // jaga-jaga kalau lowercase
            }

            // Unique category count
            String catSql = "SELECT COUNT(DISTINCT category) as cat_count FROM tasks WHERE project_id = ? AND category IS NOT NULL";
            PreparedStatement catStmt = conn.prepareStatement(catSql);
            catStmt.setInt(1, projectId);
            ResultSet catRs = catStmt.executeQuery();
            int categoryCount = catRs.next() ? catRs.getInt("cat_count") : 0;

            // Set ke button
            totalTaskBtn.setText("Total: " + total);
            doneBtn.setText("DONE: " + statusMap.getOrDefault("DONE", 0));
            progressBtn.setText("PROGRESS: " + statusMap.getOrDefault("PROGRESS", 0));
            cancelledBtn.setText("CANCELLED: " + statusMap.getOrDefault("CANCELLED", 0));
            categoryCountBtn.setText("Kategori: " + categoryCount);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
