package org.example.java_todolist.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

public class TaskInputCardController {

    @FXML private TextField titleField;
    @FXML private TextField categoryField;
    @FXML private DatePicker deadlinePicker;
    @FXML private TextField statusField;
    @FXML private TextField descriptionField;
    @FXML private HBox rootHBox;

    private Runnable onRemoveCallback;

    public void setOnRemoveCallback(Runnable callback) {
        this.onRemoveCallback = callback;
    }

    @FXML
    private void handleRemove() {
        if (onRemoveCallback != null) {
            onRemoveCallback.run();
        }
    }

    public String getTitle() {
        return titleField.getText();
    }

    public String getCategory() {
        return categoryField.getText();
    }

    public String getDeadline() {
        return deadlinePicker.getValue() != null ? deadlinePicker.getValue().toString() : "";
    }

    public String getStatus() {
        return statusField.getText();
    }

    public String getDescription() {
        return descriptionField.getText();
    }
}
