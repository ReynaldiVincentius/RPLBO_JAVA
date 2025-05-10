package org.example.java_todolist.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.java_todolist.database.Database;
import org.example.java_todolist.model.TaskHistory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class HistoryDetailController {

    @FXML
    private Label dateLabel;

    @FXML
    private TableView<TaskHistory> incompleteTable;

    @FXML
    private TableColumn<TaskHistory, String> incompleteTitleColumn;

    @FXML
    private TableColumn<TaskHistory, String> incompleteDeadlineColumn;

    @FXML
    private TableColumn<TaskHistory, String> incompleteCategoryColumn;

    @FXML
    private TableColumn<TaskHistory, String> incompleteDescriptionColumn;

    @FXML
    private TableView<TaskHistory> completeTable;

    @FXML
    private TableColumn<TaskHistory, String> completeTitleColumn;

    @FXML
    private TableColumn<TaskHistory, String> completeDeadlineColumn;

    @FXML
    private TableColumn<TaskHistory, String> completeCategoryColumn;

    @FXML
    private TableColumn<TaskHistory, String> completeDescriptionColumn;

    private String currentUsername;
    private String snapshotDate;

    public void setData(String username, String date) {
        this.currentUsername = username;
        this.snapshotDate = date;
        dateLabel.setText("Histori: " + snapshotDate);
        loadTaskHistory();
    }

    private void loadTaskHistory() {
        ObservableList<TaskHistory> completeTasks = FXCollections.observableArrayList();
        ObservableList<TaskHistory> incompleteTasks = FXCollections.observableArrayList();

        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT * FROM task_history WHERE username = ? AND snapshot_date = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, currentUsername);
            stmt.setString(2, snapshotDate);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                TaskHistory task = new TaskHistory(
                        rs.getString("id"),
                        rs.getString("username"),
                        rs.getString("original_task_id"),
                        rs.getString("title"),
                        rs.getString("deadline"),
                        rs.getString("category"),
                        rs.getString("is_done"),
                        rs.getString("description"),
                        rs.getString("snapshot_date")
                );
                if (rs.getInt("is_done") == 1) {
                    completeTasks.add(task);
                } else {
                    incompleteTasks.add(task);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Setup tabel incomplete
        incompleteTitleColumn.setCellValueFactory(data -> data.getValue().titleProperty());
        incompleteDeadlineColumn.setCellValueFactory(data -> data.getValue().deadlineProperty());
        incompleteCategoryColumn.setCellValueFactory(data -> data.getValue().categoryProperty());
        incompleteDescriptionColumn.setCellValueFactory(data -> data.getValue().descriptionProperty());
        incompleteTable.setItems(incompleteTasks);

        // Setup tabel complete
        completeTitleColumn.setCellValueFactory(data -> data.getValue().titleProperty());
        completeDeadlineColumn.setCellValueFactory(data -> data.getValue().deadlineProperty());
        completeCategoryColumn.setCellValueFactory(data -> data.getValue().categoryProperty());
        completeDescriptionColumn.setCellValueFactory(data -> data.getValue().descriptionProperty());
        completeTable.setItems(completeTasks);
    }

    @FXML
    private void handleDeleteHistory() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Konfirmasi Hapus");
        confirm.setHeaderText("Hapus histori tanggal " + snapshotDate + "?");
        confirm.setContentText("Semua data histori untuk tanggal ini akan dihapus.");
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try (Connection conn = Database.getConnection()) {
                    String deleteSql = "DELETE FROM task_history WHERE username = ? AND snapshot_date = ?";
                    PreparedStatement stmt = conn.prepareStatement(deleteSql);
                    stmt.setString(1, currentUsername);
                    stmt.setString(2, snapshotDate);
                    stmt.executeUpdate();

                    Alert info = new Alert(Alert.AlertType.INFORMATION);
                    info.setTitle("Berhasil");
                    info.setHeaderText("Histori berhasil dihapus.");
                    info.showAndWait();

                    // Tutup window detail histori
                    dateLabel.getScene().getWindow().hide();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
