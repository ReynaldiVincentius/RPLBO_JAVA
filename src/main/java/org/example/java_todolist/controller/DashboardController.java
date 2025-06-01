package org.example.java_todolist.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import org.example.java_todolist.model.Task;
import org.example.java_todolist.database.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class DashboardController {

    @FXML private VBox cardContainer;

    private String currentUsername;

    public void setCurrentUsername(String username) {
        this.currentUsername = username;
    }

    @FXML
    public void initialize() {
        // Jangan panggil loadTaskCards di sini karena currentUsername belum tentu sudah di-set
        // Panggil loadTaskCards setelah setCurrentUsername dipanggil dari luar
    }

    public void loadTaskCards() {
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT * FROM tasks WHERE is_done = 0 AND username = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, currentUsername);
            ResultSet rs = stmt.executeQuery();

            List<Task> tasks = new ArrayList<>();
            while (rs.next()) {
                Task task = new Task(
                        rs.getString("title"),
                        rs.getString("deadline"),
                        rs.getString("category"),
                        rs.getBoolean("is_done"),
                        rs.getString("description"),
                        rs.getString("id")
                );
                tasks.add(task);
            }

            cardContainer.getChildren().clear();
            for (Task task : tasks) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/java_todolist/view/task_card.fxml"));
                Node card = loader.load();
                TaskCardController controller = loader.getController();
                controller.setTask(task);
                cardContainer.getChildren().add(card);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
