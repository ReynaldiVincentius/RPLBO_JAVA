package org.example.java_todolist.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.example.java_todolist.model.TaskHistory;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class HistoryController {

    @FXML
    private VBox historyVBox;

    private String username;

    public void setUsername(String username) {
        this.username = username;
        loadHistoryCards();
    }

    public void loadHistoryCards() {
        historyVBox.getChildren().clear();
        List<TaskHistory> historyList = TaskHistory.fetchHistoryForUser(username);

        Map<String, Integer> groupedHistory = new HashMap<>();
        for (TaskHistory history : historyList) {
            String snapshotDate = history.getSnapshotDate();
            groupedHistory.put(snapshotDate, groupedHistory.getOrDefault(snapshotDate, 0) + 1);
        }

        for (Map.Entry<String, Integer> entry : groupedHistory.entrySet()) {
            String date = entry.getKey();
            int taskCount = entry.getValue();

            Label label = new Label(date + " - " + taskCount + " task(s)");
            label.setOnMouseClicked(event -> openHistoryDetail(date));
            historyVBox.getChildren().add(label);
        }
    }

    private void openHistoryDetail(String date) {
        System.out.println("Buka detail untuk tanggal: " + date);
    }
}
