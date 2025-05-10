package org.example.java_todolist.controller;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.example.java_todolist.database.Database;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javafx.scene.Node;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import java.io.IOException;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @FXML
    public void handleRegisterRedirect(ActionEvent event) {
        try {
            // Muat file FXML Register
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/java_todolist/register.fxml"));
            AnchorPane registerPane = loader.load();

            // Buat scene baru dengan Register.fxml
            Scene registerScene = new Scene(registerPane);

            // Mendapatkan Stage dari event yang diteruskan
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(registerScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT * FROM users WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String hashedPassword = rs.getString("password");
                // Memverifikasi password menggunakan BCryptPasswordEncoder
                if (passwordEncoder.matches(password, hashedPassword)) {
                    System.out.println("Login berhasil!");

                    // Pindah ke halaman Home
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/java_todolist/home.fxml"));
                    AnchorPane homePane = loader.load();

                    // Kirim username ke HomeController
                    HomeController controller = loader.getController();
                    controller.setCurrentUsername(username);

                    // Ambil stage dari event
                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    Scene scene = new Scene(homePane);
                    stage.setScene(scene);
                    stage.show();
                } else {
                    System.out.println("Password salah!");
                }
            } else {
                System.out.println("Username tidak ditemukan!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
