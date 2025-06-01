package org.example.java_todolist.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.java_todolist.model.Task;

import java.time.LocalDate;
import java.util.List;

public class AddTaskModalController {

    @FXML private TextField titleField;
    @FXML private ComboBox<String> categoryComboBox;
    @FXML private DatePicker deadlinePicker;
    @FXML private TextArea descriptionArea;

    private Task newTask;
    private ObservableList<String> categories = FXCollections.observableArrayList();

    // Setter untuk menerima kategori dari luar
    public void setExistingCategories(List<String> existingCategories) {
        categories.setAll(existingCategories);
        categoryComboBox.setItems(categories);
    }

    public Task getNewTask() {
        return newTask;
    }

    public void handleSave() {
        String title = titleField.getText().trim();
        String category = categoryComboBox.getEditor().getText().trim();
        LocalDate deadline = deadlinePicker.getValue();
        String description = descriptionArea.getText();

        if (title.isEmpty() || deadline == null || category.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Judul, Kategori, dan Deadline wajib diisi!", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        newTask = new Task(title, deadline.toString(), category, "PROGRESS", description, null);

        Stage stage = (Stage) titleField.getScene().getWindow();
        stage.close();
    }

    public void handleCancel() {
        newTask = null;
        Stage stage = (Stage) titleField.getScene().getWindow();
        stage.close();
    }
}
