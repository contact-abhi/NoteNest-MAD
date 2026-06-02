package com.example.notenest;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.LinearLayout;
import android.widget.ImageButton;
import android.content.Intent;
import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * hehehehehe Activity for creating and saving new notes with file attachments
 */
public class AddNoteActivity extends AppCompatActivity {

    private TextInputEditText titleInput, contentInput, sectionInput;
    private Button saveButton;
    private ImageButton attachFileButton;
    private LinearLayout attachmentsContainer;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private FirebaseStorage storage;
    private List<NoteAttachment> attachments;
    private ActivityResultLauncher<String[]> filePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        // Set up toolbar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Add Note");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        // Initialize views
        titleInput = findViewById(R.id.titleInput);
        contentInput = findViewById(R.id.contentInput);
        sectionInput = findViewById(R.id.sectionInput);
        saveButton = findViewById(R.id.saveButton);
        attachFileButton = findViewById(R.id.attachFileButton);
        attachmentsContainer = findViewById(R.id.attachmentsContainer);

        // Initialize attachments list
        attachments = new ArrayList<>();

        // File picker launcher for images and PDFs
        filePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.OpenDocument(),
                uri -> {
                    if (uri != null) {
                        uploadFile(uri);
                    }
                }
        );

        // Attach file button listener
        attachFileButton.setOnClickListener(v -> pickFile());

        // Save button click listener
        saveButton.setOnClickListener(v -> saveNote());
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void pickFile() {
        // Allow images and PDFs
        filePickerLauncher.launch(new String[]{"image/*", "application/pdf"});
    }

    private void uploadFile(Uri fileUri) {
        String fileName = getFileNameFromUri(fileUri);
        String fileType = getContentResolver().getType(fileUri);
        String fileId = UUID.randomUUID().toString();

        // Show upload progress
        Toast.makeText(this, "Uploading: " + fileName, Toast.LENGTH_SHORT).show();

        // Get file size
        long tempSize = 0;
        try {
            android.database.Cursor cursor = getContentResolver().query(fileUri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int sizeIndex = cursor.getColumnIndex(android.provider.OpenableColumns.SIZE);
                tempSize = cursor.getLong(sizeIndex);
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        final long fileSize = tempSize;

        // Upload to Firebase Storage
        StorageReference fileRef = storage.getReference()
                .child("note_attachments")
                .child(mAuth.getCurrentUser().getUid())
                .child(fileId);

        fileRef.putFile(fileUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Get download URL
                    fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        NoteAttachment attachment = new NoteAttachment();
                        attachment.setId(fileId);
                        attachment.setFileName(fileName);
                        attachment.setFileType(fileType);
                        attachment.setFileUrl(uri.toString());
                        attachment.setUploadedAt(System.currentTimeMillis());
                        attachment.setFileSize(fileSize);

                        attachments.add(attachment);

                        Toast.makeText(AddNoteActivity.this, "File uploaded successfully", Toast.LENGTH_SHORT).show();
                        addAttachmentUI(attachment);
                    });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AddNoteActivity.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void addAttachmentUI(NoteAttachment attachment) {
        // This would add a UI element showing the attachment
        // For now, just show a toast
    }

    private String getFileNameFromUri(Uri uri) {
        String fileName = "file";
        try {
            android.database.Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME);
                fileName = cursor.getString(nameIndex);
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileName;
    }

    private void saveNote() {
        String title = titleInput.getText().toString().trim();
        String content = contentInput.getText().toString().trim();
        String section = sectionInput.getText().toString().trim();

        // Validation
        if (title.isEmpty()) {
            titleInput.setError("Title is required");
            return;
        }

        if (content.isEmpty()) {
            contentInput.setError("Content is required");
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();
        long timestamp = System.currentTimeMillis();

        // Create Note object
        Note note = new Note();
        note.setTitle(title);
        note.setContent(content);
        note.setSection(section);
        note.setUserId(userId);
        note.setTimestamp(timestamp);
        note.setAttachments(attachments.isEmpty() ? null : attachments);

        // Save to Firestore
        firestore.collection("notes").add(note)
                .addOnSuccessListener(documentReference -> {
                    // Update the note with its generated document ID
                    note.setId(documentReference.getId());
                    documentReference.set(note);

                    Toast.makeText(AddNoteActivity.this, "Note saved successfully", Toast.LENGTH_SHORT).show();

                    // Return to home screen
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AddNoteActivity.this, "Error saving note: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
