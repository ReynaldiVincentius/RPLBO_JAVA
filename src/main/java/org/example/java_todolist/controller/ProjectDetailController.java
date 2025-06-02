package org.example.java_todolist.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.java_todolist.database.Database;
import org.example.java_todolist.model.Task;

import java.io.IOException;
import java.sql.*;
import java.util.List;
import java.util.stream.Collectors;

public class ProjectDetailController {
    @FXML private Label projectTitle;
    @FXML private VBox taskListContainer;
    @FXML private Button backButton;

    @FXML private TextField searchField;
    @FXML private ComboBox<String> categoryFilterComboBox;

    private String currentUsername;
    private int projectId;

    private javafx.collections.ObservableList<Task> taskObservableList = javafx.collections.FXCollections.observableArrayList();

    // Ganti dari VBox ke StackPane
    private StackPane rootPane;

    public void setCurrentUsername(String username) {
        this.currentUsername = username;
    }

    public void setProjectData(int projectId, String projectName) {
        this.projectId = projectId;
        projectTitle.setText("📁 " + projectName);
        loadTasks();

        searchField.textProperty().addListener((obs, oldVal, newVal) -> filterAndDisplayTasks());
        categoryFilterComboBox.valueProperty().addListener((obs, oldVal, newVal) -> filterAndDisplayTasks());
    }

    public void setRootPane(StackPane rootPane) {
        this.rootPane = rootPane;
    }

    private void loadTasks() {
        taskObservableList.clear();

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
                        rs.getString("id")
                );
                taskObservableList.add(task);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        updateCategoryFilterOptions();
        filterAndDisplayTasks();
    }

    private void updateCategoryFilterOptions() {
        List<String> categories = getUniqueCategories();
        categories.add(0, "All Categories");
        categoryFilterComboBox.setItems(javafx.collections.FXCollections.observableArrayList(categories));
        categoryFilterComboBox.getSelectionModel().selectFirst();
    }

    private List<String> getUniqueCategories() {
        return taskObservableList.stream()
                .map(Task::getCategory)
                .filter(cat -> cat != null && !cat.isEmpty())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    private void filterAndDisplayTasks() {
        taskListContainer.getChildren().clear();

        String searchText = searchField.getText() != null ? searchField.getText().toLowerCase() : "";
        String selectedCategory = categoryFilterComboBox.getValue();

        List<Task> filteredTasks = taskObservableList.stream()
                .filter(task -> {
                    boolean matchesSearch = task.getTitle().toLowerCase().contains(searchText);
                    boolean matchesCategory = (selectedCategory == null || selectedCategory.equals("All Categories"))
                            || task.getCategory().equalsIgnoreCase(selectedCategory);
                    return matchesSearch && matchesCategory;
                })
                .collect(Collectors.toList());

        for (Task task : filteredTasks) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/java_todolist/view/task_card.fxml"));
                Node cardNode = loader.load();

                TaskCardController controller = loader.getController();
                controller.setTask(task);

                cardNode.setOnMouseClicked(event -> openTaskDetailModal(task));

                taskListContainer.getChildren().add(cardNode);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleClearFilter() {
        searchField.clear();
        categoryFilterComboBox.getSelectionModel().selectFirst();
    }

    @FXML
    private void openTaskDetailModal(Task task) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/java_todolist/view/task_detail_modal.fxml"));
            Parent root = loader.load();

            TaskDetailModalController controller = loader.getController();
            controller.setTask(task);
            controller.setOnTaskUpdated(this::loadTasks);

            Stage stage = new Stage();
            stage.setTitle("Detail Tugas");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddTask() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/java_todolist/view/add_task_modal.fxml"));
            Parent root = loader.load();

            AddTaskModalController controller = loader.getController();
            controller.setExistingCategories(getUniqueCategories());

            Stage stage = new Stage();
            stage.setTitle("Tambah Tugas");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            Task newTask = controller.getNewTask();
            if (newTask != null) {
                try (Connection conn = Database.getConnection()) {
                    String sql = "INSERT INTO tasks (project_id, title, status, category, deadline, description) VALUES (?, ?, ?, ?, ?, ?)";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setInt(1, projectId);
                    stmt.setString(2, newTask.getTitle());
                    stmt.setString(3, newTask.getStatus());
                    stmt.setString(4, newTask.getCategory());
                    stmt.setString(5, newTask.getDeadline());
                    stmt.setString(6, newTask.getDescription());
                    stmt.executeUpdate();

                    loadTasks();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/java_todolist/view/dashboard.fxml"));
            Node dashboardView = loader.load();

            DashboardController controller = loader.getController();
            controller.setCurrentUsername(currentUsername);
            controller.loadProjectCards();

            if (rootPane != null) {
                rootPane.getChildren().setAll(dashboardView);
            } else {
                System.out.println("Root pane is null!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
