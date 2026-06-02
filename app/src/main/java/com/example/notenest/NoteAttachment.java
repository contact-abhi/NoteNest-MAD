package com.example.notenest;

import java.io.Serializable;

/**
 * Model class for Note Attachments (Images, PDFs, etc.)
 */
public class NoteAttachment implements Serializable {
    private String id;
    private String fileName;
    private String fileType; // image/jpeg, image/png, application/pdf
    private String fileUrl; // Firebase Storage URL
    private long uploadedAt;
    private long fileSize;

    // Default constructor for Firebase
    public NoteAttachment() {
    }

    // Constructor with parameters
    public NoteAttachment(String id, String fileName, String fileType, String fileUrl, long uploadedAt, long fileSize) {
        this.id = id;
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileUrl = fileUrl;
        this.uploadedAt = uploadedAt;
        this.fileSize = fileSize;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public long getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(long uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    // Helper method to check if attachment is image
    public boolean isImage() {
        return fileType != null && fileType.startsWith("image/");
    }

    // Helper method to check if attachment is PDF
    public boolean isPdf() {
        return fileType != null && fileType.equals("application/pdf");
    }
}
