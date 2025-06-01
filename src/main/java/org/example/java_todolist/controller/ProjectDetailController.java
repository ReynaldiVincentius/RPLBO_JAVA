package org.example.java_todolist.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.Node;
import org.example.java_todolist.database.Database;
import org.example.java_todolist.model.Task;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;

public class ProjectDetailController {
    @FXML private Label projectTitle;
    @FXML private VBox taskListContainer;
    @FXML private TextField taskNameInput;
    @FXML private TextField taskCategoryInput;
    @FXML private DatePicker taskDeadlinePicker;

    private int projectId;

    public void setProjectData(int projectId, String projectName) {
        this.projectId = projectId;
        projectTitle.setText("📁 " + projectName);
        loadTasks();
    }

    private void loadTasks() {
        taskListContainer.getChildren().clear();

        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT * FROM tasks WHERE project_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, projectId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Task task = new Task(
                        rs.getString("id"),
                        rs.getString("project_id"),
                        rs.getString("name"),
                        rs.getString("status"),
                        rs.getString("category"),
                        rs.getString("deadline")
                );

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/java_todolist/view/task_card.fxml"));
                Node cardNode = loader.load();

                // Set data ke taskCardController
                TaskCardController controller = loader.getController();
                controller.setTask(task);

                taskListContainer.getChildren().add(cardNode);
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddTask() {
        String name = taskNameInput.getText();
        String category = taskCategoryInput.getText();
        LocalDate deadline = taskDeadlinePicker.getValue();

        if (name.isEmpty() || deadline == null) return;

        try (Connection conn = Database.getConnection()) {
            String sql = "INSERT INTO tasks (project_id, name, status, category, deadline) VALUES (?, ?, 'PROGRESS', ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, projectId);
            stmt.setString(2, name);
            stmt.setString(3, category);
            stmt.setString(4, deadline.toString());
            stmt.executeUpdate();

            // Reload
            loadTasks();

            taskNameInput.clear();
            taskCategoryInput.clear();
            taskDeadlinePicker.setValue(null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
