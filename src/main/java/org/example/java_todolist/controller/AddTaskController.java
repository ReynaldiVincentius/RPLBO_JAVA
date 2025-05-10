package org.example.java_todolist.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import org.example.java_todolist.database.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Arrays;

public class AddTaskController {

    @FXML private TextField titleField;
    @FXML private DatePicker deadlinePicker;
    @FXML private ComboBox<String> categoryComboBox; // Menggunakan ComboBox untuk kategori
    @FXML private TextArea descriptionField;

    private String currentUsername;
    private Runnable onTaskAdded; // callback setelah tambah

    public void setCurrentUsername(String username) {
        this.currentUsername = username;
    }

    public void setOnTaskAdded(Runnable callback) {
        this.onTaskAdded = callback;
    }

    @FXML
    private void initialize() {
        // Menambahkan pilihan kategori pada ComboBox
        categoryComboBox.getItems().addAll("Pekerjaan", "Pribadi", "Olahraga", "Belanja", "Lainnya");
    }

    @FXML
    private void handleSave(ActionEvent event) {
        String title = titleField.getText().trim();
        String deadline = (deadlinePicker.getValue() != null) ? deadlinePicker.getValue().toString() : "";
        String category = categoryComboBox.getValue(); // Mendapatkan nilai dari ComboBox
        String description = descriptionField.getText().trim();

        if (title.isEmpty() || deadline.isEmpty() || category == null || description.isEmpty()) {
            System.out.println("Semua field harus diisi.");
            return;
        }

        try (Connection conn = Database.getConnection()) {
            String sql = "INSERT INTO tasks (username, title, deadline, category, description, is_done) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, currentUsername);
            stmt.setString(2, title);
            stmt.setString(3, deadline);
            stmt.setString(4, category);
            stmt.setString(5, description);
            stmt.setBoolean(6, false);
            stmt.executeUpdate();

            if (onTaskAdded != null) onTaskAdded.run();

            ((Stage) titleField.getScene().getWindow()).close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        ((Stage) titleField.getScene().getWindow()).close();
    }
}
