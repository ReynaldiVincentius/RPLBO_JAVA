package org.example.java_todolist.model;

public class Project {
    private int id;
    private String name;
    private String description;
    private String ownerUsername;

    public Project(int id, String name, String description, String ownerUsername) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.ownerUsername = ownerUsername;
    }

    public int getId() { return id; }

    public String getName() { return name; }

    public String getDescription() { return description; }

    public String getOwnerUsername() { return ownerUsername; }
}
