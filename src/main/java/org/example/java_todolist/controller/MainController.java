package org.example.java_todolist.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.event.ActionEvent;
import java.io.IOException;

public class MainController {

    @FXML
    private StackPane contentPane; // ini harus sama dengan fx:id di Main.fxml

    @FXML
    private Button dashboardButton;

    @FXML
    private Button todoButton;

    @FXML
    private Button completedButton;

    @FXML
    private void initialize() {
        // Load default view (misalnya dashboard)
        loadView("dashboard");
    }

    @FXML
    private void handleSidebarAction(ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        String label = clickedButton.getText().toLowerCase();

        // Load berdasarkan nama tombol
        switch (label) {
            case "dashboard":
                loadView("dashboard");
                break;
            case "todo":
                loadView("todo");
                break;
            case "completed":
                loadView("completed");
                break;
            default:
                System.out.println("Tidak dikenali: " + label);
        }
    }

    private void loadView(String viewName) {
        try {
            Node view = FXMLLoader.load(getClass().getResource(
                    "/org/example/java_todolist/View/" + viewName + ".fxml"
            ));
            contentPane.getChildren().setAll(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
