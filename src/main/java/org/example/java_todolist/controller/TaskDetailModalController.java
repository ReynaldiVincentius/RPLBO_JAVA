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
    private Runnable onTaskUpdated;

    public void setOnTaskUpdated(Runnable callback) {
        this.onTaskUpdated = callback;
    }

    @FXML
    public void initialize() {
        System.out.println("TaskDetailModalController initialized");
    }

    public void setTask(Task task) {
        this.task = task;

        titleField.setText(task.getTitle());
        deadlinePicker.setValue(java.time.LocalDate.parse(task.getDeadline()));
        loadCategories();
        categoryComboBox.setValue(task.getCategory());
        labelStatus.setText("Status: " + task.getStatus());
        descriptionField.setText(task.getDescription());

        setEditMode(false);
    }

    private void loadCategories() {
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT DISTINCT category FROM tasks WHERE category IS NOT NULL";
            PreparedStatement stmt = conn.prepareStatement(sql);
            var rs = stmt.executeQuery();

            categoryComboBox.getItems().clear();

            while (rs.next()) {
                String category = rs.getString("category");
                if (category != null && !category.isEmpty()) {
                    categoryComboBox.getItems().add(category);
                }
            }

            // Tambah opsi buat kategori baru
            categoryComboBox.getItems().add("➕ Tambah Kategori Baru...");

        } catch (Exception e) {
            showInfo("Gagal memuat kategori: " + e.getMessage());
        }
    }

    private void setEditMode(boolean isEdit) {
        titleField.setEditable(isEdit);
        deadlinePicker.setDisable(!isEdit);
        categoryComboBox.setDisable(!isEdit);
        descriptionField.setEditable(isEdit);
        descriptionField.setDisable(!isEdit);
        onSaveClickedBtn.setVisible(isEdit);
        onCancelClickedBtn.setVisible(isEdit);

        if (isEdit) {
            loadCategories(); // reload data kategori saat mode edit
            categoryComboBox.setOnAction(event -> {
                if ("➕ Tambah Kategori Baru...".equals(categoryComboBox.getValue())) {
                    TextInputDialog dialog = new TextInputDialog();
                    dialog.setTitle("Kategori Baru");
                    dialog.setHeaderText("Masukkan nama kategori baru:");
                    dialog.setContentText("Kategori:");

                    dialog.showAndWait().ifPresent(newCategory -> {
                        if (newCategory != null && !newCategory.isBlank()) {
                            categoryComboBox.getItems().add(categoryComboBox.getItems().size() - 1, newCategory);
                            categoryComboBox.setValue(newCategory);
                        }
                    });
                }
            });
        }
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

            // Update lokal
            task.setTitle(titleField.getText());
            task.setDeadline(deadlinePicker.getValue().toString());
            task.setCategory(categoryComboBox.getValue());
            task.setDescription(descriptionField.getText());

            setEditMode(false);
            showInfo("Tugas berhasil diperbarui.");

            if (onTaskUpdated != null) onTaskUpdated.run();

        } catch (Exception e) {
            showInfo("Gagal menyimpan perubahan: " + e.getMessage());
        }
    }

    @FXML
    private void onMarkDoneClicked() {
        if (!task.getStatus().equalsIgnoreCase("DONE")) {
            try (Connection conn = Database.getConnection()) {
                String sql = "UPDATE tasks SET status = ? WHERE id = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, "DONE");
                stmt.setInt(2, Integer.parseInt(task.getId()));
                stmt.executeUpdate();

                task.setStatus("DONE");
                labelStatus.setText("Status: DONE");
                showInfo("Tugas ditandai sebagai DONE.");
            } catch (Exception e) {
                showInfo("Gagal update status: " + e.getMessage());
            }
        } else {
            showInfo("Tugas sudah DONE.");
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

                    if (onTaskUpdated != null) onTaskUpdated.run();

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
