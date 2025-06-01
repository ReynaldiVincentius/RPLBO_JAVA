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

    @FXML private StackPane rootPane;  // Tambahkan ini, sesuai root layout di fxml

    private String currentUsername;

    public void setCurrentUsername(String username) {
        this.currentUsername = username;
    }

    @FXML
    public void initialize() {
        // Jangan panggil loadTaskCards di sini karena currentUsername belum tentu sudah di-set
        // Panggil loadTaskCards setelah setCurrentUsername dipanggil dari luar
    }

    public void loadProjectCards() {
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

                // Set project data
                controller.setProject(projectId, name, description);

                cardContainer.getChildren().add(projectCard);
            }

        } catch (Exception e) {
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

    private void openProjectDetail(int projectId, String projectName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/java_todolist/view/project_detail.fxml"));
            AnchorPane detailPane = loader.load();

            ProjectDetailController controller = loader.getController();
            controller.setProjectData(projectId, projectName);

            // Ganti seluruh konten rootPane dengan detailPane
            rootPane.getChildren().setAll(detailPane);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
