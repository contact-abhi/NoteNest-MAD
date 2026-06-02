package com.example.notenest;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Activity for creating new sections
 */
public class CreateSectionActivity extends AppCompatActivity {

    private EditText sectionNameInput;
    private Button createButton;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_section);

        // Set up toolbar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Create Section");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Initialize views
        sectionNameInput = findViewById(R.id.sectionNameInput);
        createButton = findViewById(R.id.createButton);

        // Create button listener
        createButton.setOnClickListener(v -> createSection());
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void createSection() {
        String sectionName = sectionNameInput.getText().toString().trim();

        // Validation
        if (sectionName.isEmpty()) {
            sectionNameInput.setError("Section name is required");
            return;
        }

        if (sectionName.length() < 2) {
            sectionNameInput.setError("Section name must be at least 2 characters");
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();
        long createdAt = System.currentTimeMillis();

        // Create Section object
        Section section = new Section();
        section.setName(sectionName);
        section.setUserId(userId);
        section.setCreatedAt(createdAt);

        // Save to Firestore
        firestore.collection("sections").add(section)
                .addOnSuccessListener(documentReference -> {
                    // Update section with generated ID
                    section.setId(documentReference.getId());
                    documentReference.set(section);

                    Toast.makeText(CreateSectionActivity.this, "Section created successfully", Toast.LENGTH_SHORT).show();

                    // Return to sections screen
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(CreateSectionActivity.this, "Error creating section: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
