package org.example.java_todolist.controller;

import javafx.scene.layout.BorderPane;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.example.java_todolist.database.Database;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javafx.scene.Node;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.Parent;


public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @FXML
    public void handleRegisterRedirect(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/java_todolist/register.fxml"));
            BorderPane registerPane = loader.load();  // Asumsi root di register.fxml BorderPane, sesuaikan kalau bukan

            Scene registerScene = new Scene(registerPane);

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
                if (passwordEncoder.matches(password, hashedPassword)) {
                    System.out.println("Login berhasil!");

                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/java_todolist/view/Main.fxml"));
                    Parent mainPane = loader.load(); // ✅ atau pakai BorderPane kalau kamu yakin

                    MainController mainController = loader.getController();
                    mainController.setCurrentUsername(username);

                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    Scene scene = new Scene(mainPane);
                    stage.setScene(scene);
                    stage.setMaximized(true);
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

