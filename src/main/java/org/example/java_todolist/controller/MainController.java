package org.example.java_todolist.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label; // ✅ Tambahkan ini
import javafx.scene.Parent;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {

    @FXML private BorderPane rootPane;
    @FXML private StackPane contentPane;
    @FXML private Label usernameLabel; // ✅ Tambahkan ini sesuai fx:id di Main.fxml
    @FXML private MenuButton profileMenu;


    private String currentUsername;

    public void setCurrentUsername(String username) {
        this.currentUsername = username;

        // ✅ Set teks label jika sudah tersedia
        if (usernameLabel != null) {
            usernameLabel.setText("Welcome, " + username + "!");
        }

        loadDashboard();
    }

    private void loadDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/java_todolist/view/dashboard.fxml"));
            Parent dashboardPane = loader.load();

            DashboardController dashboardController = loader.getController();
            dashboardController.setCurrentUsername(currentUsername);
            dashboardController.loadProjectCards();

            contentPane.getChildren().setAll(dashboardPane);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Logout Confirmation");
        confirm.setHeaderText(null);
        confirm.setContentText("Are you sure you want to logout?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/java_todolist/login.fxml"));
                    Parent loginPane = loader.load();
                    Stage stage = (Stage) profileMenu.getScene().getWindow(); // Ambil dari MenuButton
                    stage.setScene(new Scene(loginPane));
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
