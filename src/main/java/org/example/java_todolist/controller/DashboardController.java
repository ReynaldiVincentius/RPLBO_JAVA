package org.example.java_todolist.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.example.java_todolist.model.Task;
import org.example.java_todolist.database.Database;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.Parent;

public class DashboardController {

    @FXML private VBox cardContainer;
    @FXML private StackPane rootPane;  // Tetap StackPane seperti sekarang
    @FXML private Button addProjectBtn;

    private String currentUsername;

    public void setCurrentUsername(String username) {
        this.currentUsername = username;
    }

    public StackPane getRootPane() {
        return rootPane;
    }

    public VBox getCardContainer() {
        return cardContainer;
    }

    @FXML
    public void initialize() {
        addProjectBtn.setOnAction(event -> openAddProjectForm());
    }

    private void openAddProjectForm() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/java_todolist/view/add_project.fxml"));
            VBox addProjectPane = loader.load();

            AddProjectController controller = loader.getController();
            controller.setDashboardController(this);

            rootPane.getChildren().setAll(addProjectPane);

        } catch (IOException e) {
            e.printStackTrace();
        }
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

            // PENTING: pastikan method setRootPane di ProjectDetailController menerima StackPane
            controller.setRootPane(rootPane);

            controller.setCurrentUsername(currentUsername);

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

    public void addProject(String name, String description, List<Task> tasks) {
        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);

            String insertProjectSql = "INSERT INTO projects (name, description, owner_username) VALUES (?, ?, ?)";
            PreparedStatement projectStmt = conn.prepareStatement(insertProjectSql, PreparedStatement.RETURN_GENERATED_KEYS);
            projectStmt.setString(1, name);
            projectStmt.setString(2, description);
            projectStmt.setString(3, currentUsername);
            int affectedRows = projectStmt.executeUpdate();

            if (affectedRows == 0) {
                conn.rollback();
                throw new SQLException("Creating project failed, no rows affected.");
            }

            ResultSet generatedKeys = projectStmt.getGeneratedKeys();
            int projectId;
            if (generatedKeys.next()) {
                projectId = generatedKeys.getInt(1);
            } else {
                conn.rollback();
                throw new SQLException("Creating project failed, no ID obtained.");
            }

            String insertTaskSql = "INSERT INTO tasks (title, deadline, category, status, description, project_id) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement taskStmt = conn.prepareStatement(insertTaskSql);

            for (Task task : tasks) {
                taskStmt.setString(1, task.getTitle());
                taskStmt.setString(2, task.getDeadline());
                taskStmt.setString(3, task.getCategory());
                taskStmt.setString(4, task.getStatus());
                taskStmt.setString(5, task.getDescription());
                taskStmt.setInt(6, projectId);
                taskStmt.addBatch();
            }

            taskStmt.executeBatch();
            conn.commit();

            loadProjectCards();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void returnToDashboardView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/java_todolist/view/dashboard.fxml"));
            Parent dashboardRoot = loader.load();

            DashboardController controller = loader.getController();
            controller.setCurrentUsername(currentUsername);
            controller.loadProjectCards();

            rootPane.getChildren().setAll(dashboardRoot);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
