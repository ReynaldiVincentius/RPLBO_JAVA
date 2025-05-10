package org.example.java_todolist.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.Parent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import org.example.java_todolist.database.Database;
import org.example.java_todolist.model.Task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;

public class HomeController implements Initializable {

    @FXML private Label usernameLabel;
    @FXML private Label todayLabel;

    // Tugas Belum Selesai
    @FXML private TableView<Task> incompleteTaskTable;
    @FXML private TableColumn<Task, String> incompleteTitleColumn;
    @FXML private TableColumn<Task, String> incompleteDeadlineColumn;
    @FXML private TableColumn<Task, String> incompleteCategoryColumn;
    @FXML private TableColumn<Task, String> incompleteDescriptionColumn;
    @FXML private TableColumn<Task, Void> incompleteActionColumn;

    // Tugas Selesai
    @FXML private TableView<Task> completedTaskTable;
    @FXML private TableColumn<Task, String> completedTitleColumn;
    @FXML private TableColumn<Task, String> completedDeadlineColumn;
    @FXML private TableColumn<Task, String> completedCategoryColumn;
    @FXML private TableColumn<Task, String> completedDescriptionColumn;

    @FXML private ComboBox<String> categoryComboBox;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private TextField searchField;

    private String currentUsername;
    private final ObservableList<Task> incompleteTasks = FXCollections.observableArrayList();
    private final ObservableList<Task> completedTasks = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Kolom untuk tugas belum selesai
        incompleteTitleColumn.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
        incompleteDeadlineColumn.setCellValueFactory(cellData -> cellData.getValue().deadlineProperty());
        incompleteCategoryColumn.setCellValueFactory(cellData -> cellData.getValue().categoryProperty());
        incompleteDescriptionColumn.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());

        incompleteActionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Hapus");
            private final Button doneButton = new Button("Selesai");

            {
                editButton.setOnAction(event -> handleEditTask(event));
                deleteButton.setOnAction(event -> handleDeleteTask(event));
                doneButton.setOnAction(event -> {
                    Task task = getTableView().getItems().get(getIndex());
                    markTaskAsDone(task);
                });

                editButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
                deleteButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
                doneButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");
            }

            @Override
            public void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox hBox = new HBox(8, editButton, deleteButton, doneButton);
                    setGraphic(hBox);
                }
            }
        });

        // Kolom untuk tugas selesai
        completedTitleColumn.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
        completedDeadlineColumn.setCellValueFactory(cellData -> cellData.getValue().deadlineProperty());
        completedCategoryColumn.setCellValueFactory(cellData -> cellData.getValue().categoryProperty());
        completedDescriptionColumn.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());

        incompleteTaskTable.setItems(incompleteTasks);
        completedTaskTable.setItems(completedTasks);

        // Filter
        categoryComboBox.setItems(FXCollections.observableArrayList("Semua", "Pekerjaan", "Pribadi", "Olahraga", "Belanja", "Lainnya"));
        categoryComboBox.getSelectionModel().selectFirst();
        searchField.textProperty().addListener((observable, oldValue, newValue) -> loadTasks());
        categoryComboBox.valueProperty().addListener((observable, oldValue, newValue) -> loadTasks());
    }

    public void setCurrentUsername(String username) {
        this.currentUsername = username;
        usernameLabel.setText("Halo, " + username + "!");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
        String today = LocalDate.now().format(formatter);
        todayLabel.setText("Todolist Kamu hari ini - " + today);

        loadTasks();
    }

    private void loadTasks() {
        incompleteTasks.clear();
        completedTasks.clear();

        String categoryFilter = categoryComboBox.getValue();
        String searchText = searchField.getText().trim().toLowerCase();

        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT * FROM tasks WHERE username = ?";

            if (categoryFilter != null && !categoryFilter.equals("Semua")) {
                sql += " AND category = ?";
            }
            if (!searchText.isEmpty()) {
                sql += " AND LOWER(title) LIKE ?";
            }

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, currentUsername);
            int paramIndex = 2;
            if (categoryFilter != null && !categoryFilter.equals("Semua")) {
                stmt.setString(paramIndex++, categoryFilter);
            }
            if (!searchText.isEmpty()) {
                stmt.setString(paramIndex++, "%" + searchText + "%");
            }

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

                if (task.getIsDone()) completedTasks.add(task);
                else incompleteTasks.add(task);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void markTaskAsDone(Task task) {
        try (Connection conn = Database.getConnection()) {
            String sql = "UPDATE tasks SET is_done = 1 WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, task.getId());
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }

        loadTasks();
    }

    @FXML
    private void handleOpenHistory() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/java_todolist/history.fxml"));
            Parent root = loader.load();

            HistoryController controller = loader.getController();
            controller.setUsername(currentUsername); // <-- ini perbaikan penting

            Stage stage = new Stage();
            stage.setTitle("Riwayat Tugas");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddTask(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/java_todolist/add_task.fxml"));
            AnchorPane addTaskPane = loader.load();

            AddTaskController controller = loader.getController();
            controller.setCurrentUsername(currentUsername);
            controller.setOnTaskAdded(this::loadTasks);

            Stage dialog = new Stage();
            dialog.setTitle("Tambah Tugas");
            dialog.setScene(new Scene(addTaskPane));
            dialog.initOwner(((Node) event.getSource()).getScene().getWindow());
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleEditTask(ActionEvent event) {
        Task selectedTask = incompleteTaskTable.getSelectionModel().getSelectedItem();
        if (selectedTask == null) return;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/java_todolist/edit_task.fxml"));
            Parent root = loader.load();

            EditTaskController controller = loader.getController();
            controller.setTask(selectedTask);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Edit Tugas");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            loadTasks();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDeleteTask(ActionEvent event) {
        Task selectedTask = incompleteTaskTable.getSelectionModel().getSelectedItem();
        if (selectedTask == null) return;

        try (Connection conn = Database.getConnection()) {
            String sql = "DELETE FROM tasks WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, selectedTask.getId());
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }

        loadTasks();
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/java_todolist/login.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Login");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
