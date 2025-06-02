package org.example.java_todolist.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.example.java_todolist.model.Task;
import org.example.java_todolist.database.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class TaskCardController {
    @FXML private Label dateLabel;
    @FXML private Label titleLabel;
    @FXML private Label timeLabel;
    @FXML private Label assigneeLabel;
    @FXML private Label notesLabel;

    public void setTask(Task task) {
        titleLabel.setText(task.getTitle());
        dateLabel.setText("📅 " + task.getDeadline());

        String status = task.getStatus();
        String deadline = task.getDeadline();

        if ("DONE".equalsIgnoreCase(status)) {
            // Hitung hari lewat deadline
            String extraInfo = "";
            try {
                LocalDate dl = LocalDate.parse(deadline, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                LocalDate today = LocalDate.now();
                long daysLate = ChronoUnit.DAYS.between(dl, today);
                if (daysLate > 0) {
                    extraInfo = " (Sudah lewat deadline " + daysLate + " hari)";
                }
            } catch (Exception e) {
                // ignore parsing error, ga perlu info tambahan
            }
            timeLabel.setText("STATUS : DONE" + extraInfo);
        } else if ("PROGRESS".equalsIgnoreCase(status)) {
            // Tampilkan countdown deadline
            String deadlineText = generateDeadlineCountdown(deadline);
            timeLabel.setText(deadlineText);
        } else {
            timeLabel.setText("STATUS : " + (status != null ? status : "UNKNOWN"));
        }

        // Ambil assignee dari DB
        fetchAndSetAssignee(task.getId());

        // Notes (deskripsi singkat)
        String description = task.getDescription();
        if (description != null && !description.isEmpty()) {
            String shortDesc = description.split("\\.")[0];
            if (shortDesc.length() > 50) {
                shortDesc = shortDesc.substring(0, 50) + "...";
            }
            notesLabel.setText("📝 " + shortDesc);
        } else {
            notesLabel.setText("📌 Tidak ada catatan");
        }
    }

    private String generateDeadlineCountdown(String deadlineString) {
        try {
            LocalDate deadline = LocalDate.parse(deadlineString, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            LocalDate today = LocalDate.now();

            long days = ChronoUnit.DAYS.between(today, deadline);

            if (days > 0) {
                return "⏳ " + days + " hari lagi";
            } else if (days == 0) {
                return "⏰ Hari ini";
            } else {
                return "✅ Lewat " + Math.abs(days) + " hari";
            }

        } catch (Exception e) {
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
                        String capitalized = username.substring(0, 1).toUpperCase() + username.substring(1);
                        assigneeText = "👷 " + capitalized;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            final String finalAssigneeText = assigneeText;
            Platform.runLater(() -> assigneeLabel.setText(finalAssigneeText));
        }).start();
    }
}
