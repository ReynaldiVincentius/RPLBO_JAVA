package org.example.java_todolist.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.example.java_todolist.model.Task;

import java.util.ArrayList;
import java.util.List;

public class AddProjectController {

    @FXML private TextField projectNameField;          // Sesuai fx:id di FXML
    @FXML private TextArea projectDescField;            // Sesuai fx:id di FXML
    @FXML private VBox tasksContainer;                   // Container untuk list task input

    private DashboardController dashboardController;

    public void setDashboardController(DashboardController controller) {
        this.dashboardController = controller;
    }

    // Ini untuk event handler dari FXML onAction
    @FXML
    private void handleSaveProject() {
        String name = projectNameField.getText().trim();
        String description = projectDescField.getText().trim();

        if (name.isEmpty()) {
            System.out.println("Project name cannot be empty");
            return;
        }

        List<Task> tasks = new ArrayList<>();

        for (var node : tasksContainer.getChildren()) {
            if (node instanceof VBox) {
                VBox taskBox = (VBox) node;

                TextField titleField = (TextField) taskBox.lookup("#taskTitleField");
                TextField deadlineField = (TextField) taskBox.lookup("#taskDeadlineField");
                TextField categoryField = (TextField) taskBox.lookup("#taskCategoryField");
                TextField statusField = (TextField) taskBox.lookup("#taskStatusField");
                TextArea descriptionField = (TextArea) taskBox.lookup("#taskDescriptionArea");

                if (titleField != null && !titleField.getText().trim().isEmpty()) {
                    Task task = new Task(
                            titleField.getText().trim(),
                            deadlineField != null ? deadlineField.getText().trim() : "",
                            categoryField != null ? categoryField.getText().trim() : "",
                            statusField != null ? statusField.getText().trim() : "",
                            descriptionField != null ? descriptionField.getText().trim() : "",
                            null
                    );
                    tasks.add(task);
                }
            }
        }

        dashboardController.addProject(name, description, tasks);
        dashboardController.returnToDashboardView();
    }


    @FXML
    private void handleCancel() {
        dashboardController.loadProjectCards();

        dashboardController.getRootPane().getChildren().clear();
        dashboardController.getRootPane().getChildren().add(dashboardController.getCardContainer().getParent());
    }

    @FXML
    private void handleAddTask() {
        VBox newTaskBox = createTaskInputBox();
        tasksContainer.getChildren().add(newTaskBox);
    }

    // Helper method buat generate input form task baru
    private VBox createTaskInputBox() {
        VBox box = new VBox(5);
        box.setStyle("-fx-border-color: #ccc; -fx-padding: 10; -fx-border-radius: 5;");

        TextField titleField = new TextField();
        titleField.setPromptText("Judul Task");
        titleField.setId("taskTitleField");

        TextField deadlineField = new TextField();
        deadlineField.setPromptText("Deadline (yyyy-mm-dd)");
        deadlineField.setId("taskDeadlineField");

        TextField categoryField = new TextField();
        categoryField.setPromptText("Kategori");
        categoryField.setId("taskCategoryField");

        TextField statusField = new TextField();
        statusField.setPromptText("Status");
        statusField.setId("taskStatusField");

        TextArea descriptionArea = new TextArea();
        descriptionArea.setPromptText("Deskripsi Task");
        descriptionArea.setPrefRowCount(2);
        descriptionArea.setId("taskDescriptionArea");

        box.getChildren().addAll(
                new Label("Judul Task:"), titleField,
                new Label("Deadline:"), deadlineField,
                new Label("Kategori:"), categoryField,
                new Label("Status:"), statusField,
                new Label("Deskripsi:"), descriptionArea
        );

        return box;
    }
}
