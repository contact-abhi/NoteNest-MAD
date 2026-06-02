package com.example.notenest;

import java.io.Serializable;

/**
 * Model class for Note
 */
public class Note implements Serializable {
    private String id;
    private String title;
    private String content;
    private String section;
    private String sectionId;
    private long timestamp;
    private String userId;
    private java.util.List<NoteAttachment> attachments;

    // Default constructor for Firebase
    public Note() {
    }

    // Constructor with parameters
    public Note(String id, String title, String content, String section, long timestamp, String userId) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.section = section;
        this.timestamp = timestamp;
        this.userId = userId;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getSectionId() {
        return sectionId;
    }

    public void setSectionId(String sectionId) {
        this.sectionId = sectionId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public java.util.List<NoteAttachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(java.util.List<NoteAttachment> attachments) {
        this.attachments = attachments;
    }
}
