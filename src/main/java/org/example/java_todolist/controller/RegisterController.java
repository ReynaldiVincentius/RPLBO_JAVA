package org.example.java_todolist.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.example.java_todolist.database.Database;

import javafx.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

        if (isUsernameTaken(username)) {
            showAlert("Error", "Username already exists.");
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

                    // Arahkan ke halaman login
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/java_todolist/login.fxml"));
                        AnchorPane loginPane = loader.load();
                        Stage currentStage = (Stage) usernameField.getScene().getWindow();
                        currentStage.setScene(new Scene(loginPane));
                        currentStage.show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        showAlert("Error", "Failed to load login screen.");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to register user.");
        }
    }

    // Tambahan: cek apakah username sudah ada
    private boolean isUsernameTaken(String username) {
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return true; // jika error, lebih aman diasumsikan sudah dipakai
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void handleLoginRedirect(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/java_todolist/login.fxml"));
            Parent loginPane = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(loginPane));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
