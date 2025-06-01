package org.example.java_todolist.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.example.java_todolist.model.Task;

public class TaskCardController {
    @FXML private Label dateLabel;
    @FXML private Label titleLabel;
    @FXML private Label timeLabel;
    @FXML private Label assigneeLabel;
    @FXML private Label notesLabel;

    public void setTask(Task task) {
        titleLabel.setText(task.getTitle());
        dateLabel.setText("📅 " + task.getDeadline());
        timeLabel.setText("🕗 8:00AM for 4 hours");
        assigneeLabel.setText("👷 Faruk Ahmad");
        notesLabel.setText("📝 NOTES (2)");
    }
}
