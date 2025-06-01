package org.example.java_todolist.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.java_todolist.database.Database;
import org.example.java_todolist.model.Task;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class TaskDetailModalController {
    @FXML private TextField titleField;
    @FXML private DatePicker deadlinePicker;
    @FXML private ComboBox<String> categoryComboBox;
    @FXML private Label labelStatus;
    @FXML private TextArea descriptionField;

    @FXML private Button onSaveClickedBtn;
    @FXML private Button onCancelClickedBtn;
    private Task task;

    @FXML
    public void initialize() {
        System.out.println("TaskDetailModalController initialized");
        System.out.println("onSaveClickedBtn = " + onSaveClickedBtn); // harusnya tidak null
    }


    public void setTask(Task task) {
        this.task = task;

        titleField.setText(task.getTitle());
        deadlinePicker.setValue(java.time.LocalDate.parse(task.getDeadline()));
        categoryComboBox.getItems().addAll("Pekerjaan", "Pribadi", "Olahraga", "Belanja", "Lainnya");
        categoryComboBox.setValue(task.getCategory());
        labelStatus.setText("Status: " + task.getStatus());
        descriptionField.setText(task.getDescription());

        setEditMode(false);
    }

    private void setEditMode(boolean isEdit) {
        System.out.println("Edit mode: " + isEdit);
        titleField.setEditable(isEdit);
        deadlinePicker.setDisable(!isEdit);
        categoryComboBox.setDisable(!isEdit);

        descriptionField.setEditable(isEdit);
        descriptionField.setDisable(!isEdit); // penting
        System.out.println("descriptionField editable = " + descriptionField.isEditable());
        System.out.println("descriptionField disabled = " + descriptionField.isDisable());

        onSaveClickedBtn.setVisible(isEdit);
        onCancelClickedBtn.setVisible(isEdit);
    }

    @FXML
    private void onEditClicked() {
        setEditMode(true);
    }

    @FXML
    private void onCancelClicked() {
        setTask(task);  // reset data
        setEditMode(false);
    }

    @FXML
    private void onSaveClicked() {
        try (Connection conn = Database.getConnection()) {
            String sql = "UPDATE tasks SET title=?, deadline=?, category=?, description=? WHERE id=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, titleField.getText());
            stmt.setString(2, deadlinePicker.getValue().toString());
            stmt.setString(3, categoryComboBox.getValue());
            stmt.setString(4, descriptionField.getText());
            stmt.setInt(5, Integer.parseInt(task.getId()));
            stmt.executeUpdate();

            // Update objek task lokal
            task.setTitle(titleField.getText());
            task.setDeadline(deadlinePicker.getValue().toString());
            task.setCategory(categoryComboBox.getValue());
            task.setDescription(descriptionField.getText());

            setEditMode(false);
            showInfo("Tugas berhasil diperbarui.");

        } catch (Exception e) {
            showInfo("Gagal menyimpan perubahan: " + e.getMessage());
        }
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
        Stage stage = (Stage) titleField.getScene().getWindow();
        stage.close();
    }
}
