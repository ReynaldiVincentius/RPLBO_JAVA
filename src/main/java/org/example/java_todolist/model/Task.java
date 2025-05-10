package org.example.java_todolist.model;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.BooleanProperty;

public class Task {
    private final StringProperty title;
    private final StringProperty deadline;
    private final StringProperty category;
    private final BooleanProperty isDone;
    private final StringProperty description;
    private final StringProperty id;

    // Constructor
    public Task(String title, String deadline, String category, boolean isDone, String description, String id) {
        this.title = new SimpleStringProperty(title);
        this.deadline = new SimpleStringProperty(deadline);
        this.category = new SimpleStringProperty(category);
        this.isDone = new SimpleBooleanProperty(isDone);
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

    public boolean getIsDone() {
        return isDone.get();
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

    public void setIsDone(boolean isDone) {
        this.isDone.set(isDone);
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

    public BooleanProperty isDoneProperty() {
        return isDone;
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public StringProperty idProperty() {
        return id;
    }
}
