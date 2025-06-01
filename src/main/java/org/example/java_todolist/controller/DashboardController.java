package org.example.java_todolist.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.example.java_todolist.model.Task;
import org.example.java_todolist.database.Database;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class DashboardController {

    @FXML private VBox cardContainer;
    @FXML private StackPane rootPane;  // Ini adalah root StackPane dari dashboard.fxml

    private String currentUsername;

    public void setCurrentUsername(String username) {
        this.currentUsername = username;
    }

    @FXML
    public void initialize() {
        // Tidak memuat project langsung, tunggu hingga currentUsername di-set
    }

    public void loadProjectCards() {
        if (currentUsername == null) {
            System.out.println("Username belum diset. Tidak bisa load project.");
            return;
        }

        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT * FROM projects WHERE owner_username = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, currentUsername);
            ResultSet rs = stmt.executeQuery();

            cardContainer.getChildren().clear();

            while (rs.next()) {
                int projectId = rs.getInt("id");
                String name = rs.getString("name");
                String description = rs.getString("description");

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/java_todolist/view/project_card.fxml"));
                Node projectCard = loader.load();
                ProjectCardController controller = loader.getController();

                controller.setProject(projectId, name, description);

                // Kirim info project + username ke detail saat diklik
                projectCard.setOnMouseClicked(event -> openProjectDetail(projectId, name));

                cardContainer.getChildren().add(projectCard);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openProjectDetail(int projectId, String projectName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/java_todolist/view/project_detail.fxml"));
            AnchorPane detailPane = loader.load();

            ProjectDetailController controller = loader.getController();
            controller.setProjectData(projectId, projectName);
            controller.setRootPane(rootPane);
            controller.setCurrentUsername(currentUsername); // ✅ Kirim username ke detail

            rootPane.getChildren().setAll(detailPane);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<Task> getTasksForProject(int projectId) {
        List<Task> tasks = new ArrayList<>();
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT * FROM tasks WHERE project_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, projectId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Task task = new Task(
                        rs.getString("title"),
                        rs.getString("deadline"),
                        rs.getString("category"),
                        rs.getString("status"),
                        rs.getString("description"),
                        String.valueOf(rs.getInt("id"))
                );
                tasks.add(task);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tasks;
    }
}
