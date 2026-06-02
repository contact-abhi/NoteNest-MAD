package com.example.notenest;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout;
import android.widget.ImageView;
import android.view.View;
import android.content.Intent;
import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.bumptech.glide.Glide;

/**
 * Activity for displaying and editing note details
 */
public class NoteDetailActivity extends AppCompatActivity {

    private TextView titleText, contentText, sectionText, timestampText;
    private EditText titleEdit, contentEdit, sectionEdit;
    private Button deleteButton, editButton, saveButton, cancelButton;
    private View viewContainer, editContainer, viewButtonsContainer, editButtonsContainer;
    private LinearLayout attachmentsContainer;
    private Note currentNote;
    private FirebaseFirestore firestore;
    private FirebaseAuth mAuth;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_detail);

        // Set up toolbar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Note Details");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize Firebase
        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Initialize view mode views
        titleText = findViewById(R.id.titleText);
        contentText = findViewById(R.id.contentText);
        sectionText = findViewById(R.id.sectionText);
        timestampText = findViewById(R.id.timestampText);
        viewContainer = findViewById(R.id.viewContainer);

        // Initialize edit mode views
        titleEdit = findViewById(R.id.titleEdit);
        contentEdit = findViewById(R.id.contentEdit);
        sectionEdit = findViewById(R.id.sectionEdit);
        editContainer = findViewById(R.id.editContainer);

        // Initialize buttons
        deleteButton = findViewById(R.id.deleteButton);
        editButton = findViewById(R.id.editButton);
        saveButton = findViewById(R.id.saveButton);
        cancelButton = findViewById(R.id.cancelButton);
        
        // Initialize button containers
        viewButtonsContainer = findViewById(R.id.viewButtonsContainer);
        editButtonsContainer = findViewById(R.id.editButtonsContainer);
        
        // Initialize attachments container
        attachmentsContainer = findViewById(R.id.attachmentsViewContainer);

        // Get note data from intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            currentNote = (Note) extras.getSerializable("note");
            displayNoteDetails();
        }

        // Button listeners
        deleteButton.setOnClickListener(v -> deleteNote());
        editButton.setOnClickListener(v -> enterEditMode());
        saveButton.setOnClickListener(v -> saveNoteChanges());
        cancelButton.setOnClickListener(v -> exitEditMode());
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (isEditMode) {
            exitEditMode();
            return true;
        }
        finish();
        return true;
    }

    private void displayNoteDetails() {
        if (currentNote != null) {
            // View mode
            titleText.setText(currentNote.getTitle());
            contentText.setText(currentNote.getContent());
            
            // Display section if available
            if (currentNote.getSection() != null && !currentNote.getSection().isEmpty()) {
                sectionText.setText("Section: " + currentNote.getSection());
            } else {
                sectionText.setText("Section: Uncategorized");
            }

            // Format and display timestamp
            timestampText.setText("Created: " + formatTimestamp(currentNote.getTimestamp()));
            
            // Display attachments if available
            if (currentNote.getAttachments() != null && !currentNote.getAttachments().isEmpty()) {
                displayAttachments(currentNote.getAttachments());
            }
        }
    }

    private void displayAttachments(java.util.List<NoteAttachment> attachments) {
        attachmentsContainer.removeAllViews();
        
        for (NoteAttachment attachment : attachments) {
            LinearLayout itemLayout = new LinearLayout(this);
            itemLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            itemLayout.setOrientation(LinearLayout.HORIZONTAL);
            itemLayout.setPadding(8, 8, 8, 8);
            
            // File icon/preview
            if (attachment.isImage()) {
                ImageView imagePreview = new ImageView(this);
                imagePreview.setLayoutParams(new LinearLayout.LayoutParams(100, 100));
                imagePreview.setScaleType(ImageView.ScaleType.CENTER_CROP);
                Glide.with(this)
                        .load(attachment.getFileUrl())
                        .into(imagePreview);
                itemLayout.addView(imagePreview);
            } else if (attachment.isPdf()) {
                TextView pdfIcon = new TextView(this);
                pdfIcon.setText("📄");
                pdfIcon.setTextSize(40);
                pdfIcon.setLayoutParams(new LinearLayout.LayoutParams(100, 100));
                itemLayout.addView(pdfIcon);
            }
            
            // File info
            LinearLayout infoLayout = new LinearLayout(this);
            infoLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
            infoLayout.setOrientation(LinearLayout.VERTICAL);
            infoLayout.setPadding(12, 0, 0, 0);
            
            TextView fileName = new TextView(this);
            fileName.setText(attachment.getFileName());
            fileName.setTextSize(14);
            fileName.setTypeface(null, android.graphics.Typeface.BOLD);
            infoLayout.addView(fileName);
            
            TextView fileSize = new TextView(this);
            fileSize.setText(formatFileSize(attachment.getFileSize()));
            fileSize.setTextSize(12);
            fileSize.setTextColor(0xFF999999);
            infoLayout.addView(fileSize);
            
            itemLayout.addView(infoLayout);
            
            // Download button
            Button downloadBtn = new Button(this);
            downloadBtn.setText("View");
            downloadBtn.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            downloadBtn.setOnClickListener(v -> openFile(attachment.getFileUrl()));
            itemLayout.addView(downloadBtn);
            
            attachmentsContainer.addView(itemLayout);
        }
    }

    private void openFile(String fileUrl) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(fileUrl));
        startActivity(intent);
    }

    private String formatFileSize(long bytes) {
        if (bytes <= 0) return "0 B";
        final String[] units = new String[]{"B", "KB", "MB", "GB"};
        int digitGroups = (int) (Math.log10(bytes) / Math.log10(1024));
        return String.format("%.1f %s", bytes / Math.pow(1024, digitGroups), units[digitGroups]);
    }

    private void enterEditMode() {
        isEditMode = true;
        
        // Populate edit fields with current data
        titleEdit.setText(currentNote.getTitle());
        contentEdit.setText(currentNote.getContent());
        sectionEdit.setText(currentNote.getSection() != null ? currentNote.getSection() : "");
        
        // Show edit container, hide view container
        viewContainer.setVisibility(View.GONE);
        editContainer.setVisibility(View.VISIBLE);
        
        // Toggle button containers
        viewButtonsContainer.setVisibility(View.GONE);
        editButtonsContainer.setVisibility(View.VISIBLE);
        
        // Update toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Edit Note");
        }
    }

    private void exitEditMode() {
        isEditMode = false;
        
        // Hide edit container, show view container
        editContainer.setVisibility(View.GONE);
        viewContainer.setVisibility(View.VISIBLE);
        
        // Toggle button containers
        editButtonsContainer.setVisibility(View.GONE);
        viewButtonsContainer.setVisibility(View.VISIBLE);
        
        // Update toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Note Details");
        }
    }

    private void saveNoteChanges() {
        String newTitle = titleEdit.getText().toString().trim();
        String newContent = contentEdit.getText().toString().trim();
        String newSection = sectionEdit.getText().toString().trim();

        // Validation
        if (newTitle.isEmpty()) {
            titleEdit.setError("Title is required");
            return;
        }

        if (newContent.isEmpty()) {
            contentEdit.setError("Content is required");
            return;
        }

        if (currentNote != null && currentNote.getId() != null) {
            // Update note in Firestore
            currentNote.setTitle(newTitle);
            currentNote.setContent(newContent);
            currentNote.setSection(newSection);
            // Note: sectionId would be set when user selects from existing sections

            firestore.collection("notes").document(currentNote.getId())
                    .set(currentNote)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(NoteDetailActivity.this, "Note updated successfully", Toast.LENGTH_SHORT).show();
                        
                        // Update display and exit edit mode
                        displayNoteDetails();
                        exitEditMode();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(NoteDetailActivity.this, "Error updating note: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void deleteNote() {
        if (currentNote != null && currentNote.getId() != null) {
            firestore.collection("notes").document(currentNote.getId())
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(NoteDetailActivity.this, "Note deleted", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(NoteDetailActivity.this, "Error deleting note: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private String formatTimestamp(long timestamp) {
        if (timestamp == 0) return "Just now";

        // Format as full date/time
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MMM dd, yyyy 'at' HH:mm", java.util.Locale.getDefault());
        return sdf.format(new java.util.Date(timestamp));
    }
}
