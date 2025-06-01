package org.example.java_todolist.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label; // ✅ Tambahkan ini
import javafx.scene.Parent;

public class MainController {

    @FXML private BorderPane rootPane;
    @FXML private StackPane contentPane;
    @FXML private Label usernameLabel; // ✅ Tambahkan ini sesuai fx:id di Main.fxml

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
}
