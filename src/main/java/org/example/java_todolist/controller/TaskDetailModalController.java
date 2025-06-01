package org.example.java_todolist.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.example.java_todolist.database.Database;
import org.example.java_todolist.model.Task;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class TaskDetailModalController {
    @FXML private Label labelTitle;
    @FXML private Label labelDeadline;
    @FXML private Label labelCategory;
    @FXML private Label labelStatus;
    @FXML private TextArea labelDescription;

    private Task task;

    public void setTask(Task task) {
        this.task = task;
        labelTitle.setText(task.getTitle());
        labelDeadline.setText("Deadline: " + task.getDeadline());
        labelCategory.setText("Kategori: " + task.getCategory());
        labelStatus.setText("Status: " + task.getStatus());
        labelDescription.setText(task.getDescription());
    }

    @FXML
    private void onEditClicked() {
        // TODO: Buka form edit (modal atau scene baru)
        showInfo("Fitur edit belum diimplementasi.");
    }

    @FXML
    private void onMarkDoneClicked() {
        if (!task.getStatus().equalsIgnoreCase("Selesai")) {
            try (Connection conn = Database.getConnection()) {
                String sql = "UPDATE tasks SET status = ? WHERE id = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, "Selesai");
                stmt.setInt(2, Integer.parseInt(task.getId()));
                stmt.executeUpdate();

                task.setStatus("Selesai");
                labelStatus.setText("Status: Selesai");
                showInfo("Tugas ditandai sebagai selesai.");
            } catch (Exception e) {
                showInfo("Gagal update status: " + e.getMessage());
            }
        } else {
            showInfo("Tugas sudah selesai.");
        }
    }

    @FXML
    private void onDeleteClicked() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Konfirmasi Hapus");
        alert.setHeaderText("Yakin ingin menghapus tugas ini?");
        alert.setContentText(task.getTitle());

        alert.showAndWait().ifPresent(result -> {
            if (result.getText().equals("OK")) {
                try (Connection conn = Database.getConnection()) {
                    String sql = "DELETE FROM tasks WHERE id = ?";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setInt(1, Integer.parseInt(task.getId()));
                    stmt.executeUpdate();

                    showInfo("Tugas berhasil dihapus.");
                    closeWindow();
                } catch (Exception e) {
                    showInfo("Gagal menghapus tugas: " + e.getMessage());
                }
            }
        });
    }

    private void showInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informasi");
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void closeWindow() {
        Stage stage = (Stage) labelTitle.getScene().getWindow();
        stage.close();
    }
}
