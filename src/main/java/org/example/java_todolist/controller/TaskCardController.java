package org.example.java_todolist.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import org.example.java_todolist.model.Task;
import org.example.java_todolist.database.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

public class TaskCardController {

    @FXML private Label dateLabel;
    @FXML private Label titleLabel;
    @FXML private Label timeLabel;
    @FXML private Label assigneeLabel;
    @FXML private Label notesLabel;
    @FXML private Label categoryLabel;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public void setTask(Task task) {
        if (task == null) return;

        // Judul
        titleLabel.setText(task.getTitle());

        // Kategori
        String category = task.getCategory();
        categoryLabel.setText(category != null && !category.isEmpty() ? "🏷️ " + category : "🏷️ Tidak ada kategori");

        // Deadline
        String deadline = task.getDeadline();
        if (deadline != null && !deadline.isEmpty()) {
            dateLabel.setText("📅 " + deadline);
        } else {
            dateLabel.setText("📅 Tidak ada deadline");
        }

        // Status dan Waktu
        setStatusAndTime(task.getStatus(), deadline);

        // Assignee (asynchronous)
        fetchAndSetAssignee(task.getId());

        // Deskripsi singkat
        String desc = task.getDescription();
        if (desc != null && !desc.isEmpty()) {
            String shortDesc = desc.length() > 50 ? desc.substring(0, 50) + "..." : desc;
            notesLabel.setText("📝 " + shortDesc);
            notesLabel.setTooltip(new Tooltip(desc)); // tooltip untuk lihat versi full
        } else {
            notesLabel.setText("📌 Tidak ada catatan");
        }
    }

    private void setStatusAndTime(String status, String deadlineString) {
        if (status == null) {
            timeLabel.setText("STATUS : UNKNOWN");
            return;
        }

        switch (status.toUpperCase()) {
            case "DONE":
                timeLabel.setText("STATUS : DONE" + getDelayInfo(deadlineString));
                break;
            case "PROGRESS":
                timeLabel.setText(generateDeadlineCountdown(deadlineString));
                break;
            default:
                timeLabel.setText("STATUS : " + status.toUpperCase());
                break;
        }
    }

    private String getDelayInfo(String deadlineString) {
        if (deadlineString == null || deadlineString.isEmpty()) return "";

        try {
            LocalDate dl = LocalDate.parse(deadlineString, dateFormatter);
            long daysLate = ChronoUnit.DAYS.between(dl, LocalDate.now());
            return daysLate > 0 ? " (Sudah lewat deadline " + daysLate + " hari)" : "";
        } catch (DateTimeParseException e) {
            return " (Deadline tidak valid)";
        }
    }

    private String generateDeadlineCountdown(String deadlineString) {
        if (deadlineString == null || deadlineString.isEmpty()) return "❓ Deadline tidak tersedia";

        try {
            LocalDate deadline = LocalDate.parse(deadlineString, dateFormatter);
            long days = ChronoUnit.DAYS.between(LocalDate.now(), deadline);

            if (days > 0) return "⏳ " + days + " hari lagi";
            if (days == 0) return "⏰ Hari ini";
            return "✅ Lewat " + Math.abs(days) + " hari";

        } catch (DateTimeParseException e) {
            return "❓ Deadline tidak valid";
        }
    }

    private void fetchAndSetAssignee(String taskId) {
        new Thread(() -> {
            String assigneeText = "👷 Unknown";
            try (Connection conn = Database.getConnection()) {
                String query = "SELECT p.owner_username FROM projects p JOIN tasks t ON p.id = t.project_id WHERE t.id = ?";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setInt(1, Integer.parseInt(taskId));
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    String username = rs.getString("owner_username");
                    if (username != null && !username.isEmpty()) {
                        assigneeText = "👷 " + capitalizeUsername(username);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace(); // logging error dev
            }

            final String finalAssigneeText = assigneeText;
            Platform.runLater(() -> assigneeLabel.setText(finalAssigneeText));
        }).start();
    }

    private String capitalizeUsername(String username) {
        if (username == null || username.isEmpty()) return "Unknown";
        return username.substring(0, 1).toUpperCase() + username.substring(1);
    }
}
