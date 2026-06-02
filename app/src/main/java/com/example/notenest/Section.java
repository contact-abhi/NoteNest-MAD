package com.example.notenest;

import java.io.Serializable;

/**
 * Model class for Section (Note Category/Folder)
 */
public class Section implements Serializable {
    private String id;
    private String name;
    private String userId;
    private long createdAt;
    private int color; // For future color coding

    // Default constructor for Firebase
    public Section() {
    }

    // Constructor with parameters
    public Section(String id, String name, String userId, long createdAt) {
        this.id = id;
        this.name = name;
        this.userId = userId;
        this.createdAt = createdAt;
        this.color = 0xFF8B5DFF; // Default purple
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
