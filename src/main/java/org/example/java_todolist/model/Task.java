package org.example.java_todolist.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Task {
    private final StringProperty title;
    private final StringProperty deadline;
    private final StringProperty category;
    private final StringProperty status;
    private final StringProperty description;
    private final StringProperty id;

    // Constructor
    public Task(String title, String deadline, String category, String status, String description, String id) {
        this.title = new SimpleStringProperty(title);
        this.deadline = new SimpleStringProperty(deadline);
        this.category = new SimpleStringProperty(category);
        this.status = new SimpleStringProperty(status);
        this.description = new SimpleStringProperty(description);
        this.id = new SimpleStringProperty(id);
    }

    // Getters
    public String getTitle() {
        return title.get();
    }

    public String getDeadline() {
        return deadline.get();
    }

    public String getCategory() {
        return category.get();
    }

    public String getStatus() {
        return status.get();
    }

    public String getDescription() {
        return description.get();
    }

    public String getId() {
        return id.get();
    }

    // Setters
    public void setTitle(String title) {
        this.title.set(title);
    }

    public void setDeadline(String deadline) {
        this.deadline.set(deadline);
    }

    public void setCategory(String category) {
        this.category.set(category);
    }

    public void setStatus(String status) {
        this.status.set(status);
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public void setId(String id) {
        this.id.set(id);
    }

    // Property getters (needed for TableView bindings)
    public StringProperty titleProperty() {
        return title;
    }

    public StringProperty deadlineProperty() {
        return deadline;
    }

    public StringProperty categoryProperty() {
        return category;
    }

    public StringProperty statusProperty() {
        return status;
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public StringProperty idProperty() {
        return id;
    }
}
