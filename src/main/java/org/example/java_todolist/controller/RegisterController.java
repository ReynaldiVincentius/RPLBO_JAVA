package org.example.java_todolist.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.example.java_todolist.database.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.io.IOException;

public class RegisterController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private void handleRegister() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // Validasi input
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showAlert("Error", "All fields are required.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showAlert("Error", "Passwords do not match.");
            return;
        }

        // Enkripsi password sebelum disimpan ke database
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encryptedPassword = encoder.encode(password);

        // Menyimpan data ke database
        try (Connection conn = Database.getConnection()) {
            String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, username);
                stmt.setString(2, encryptedPassword);

                int rowsInserted = stmt.executeUpdate();
                if (rowsInserted > 0) {
                    showAlert("Success", "User registered successfully.");

                    // Memuat halaman login setelah registrasi berhasil
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/java_todolist/login.fxml"));
                        AnchorPane loginPane = loader.load();

                        // Mendapatkan stage atau scene yang aktif
                        Stage currentStage = (Stage) usernameField.getScene().getWindow();

                        // Mengatur scene dengan halaman login
                        Scene loginScene = new Scene(loginPane);
                        currentStage.setScene(loginScene);
                        currentStage.show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        showAlert("Error", "Failed to load login screen.");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Error", "Failed to register user.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Database connection error.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
