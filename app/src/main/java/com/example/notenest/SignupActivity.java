package com.example.notenest;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "SignupActivity";

    private TextInputEditText emailEditText;
    private TextInputEditText passwordEditText;
    private TextInputEditText confirmPasswordEditText;
    private Button signupButton;
    private Button backButton;
    private ProgressBar progressBar;
    private TextView errorTextView;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize UI elements
        initializeUI();
    }

    private void initializeUI() {
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        signupButton = findViewById(R.id.signupButton);
        backButton = findViewById(R.id.backButton);
        progressBar = findViewById(R.id.progressBar);
        errorTextView = findViewById(R.id.errorTextView);

        // Set click listeners
        signupButton.setOnClickListener(v -> performSignup());
        backButton.setOnClickListener(v -> finish());
    }

    private void performSignup() {
        // Clear previous error
        errorTextView.setText("");

        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        // Validate inputs
        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showError("Please fill in all fields");
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showError("Please enter a valid email address");
            return;
        }

        if (password.length() < 6) {
            showError("Password must be at least 6 characters");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showError("Passwords do not match");
            return;
        }

        // Show progress bar
        progressBar.setVisibility(android.view.View.VISIBLE);
        signupButton.setEnabled(false);

        // Create user with Firebase
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    progressBar.setVisibility(android.view.View.GONE);
                    signupButton.setEnabled(true);

                    if (task.isSuccessful()) {
                        Log.d(TAG, "createUserWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            Toast.makeText(SignupActivity.this, "Account created successfully!",
                                    Toast.LENGTH_SHORT).show();
                            navigateToMainActivity();
                        }
                    } else {
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        String errorMessage = "Authentication failed";
                        if (task.getException() != null) {
                            if (task.getException() instanceof FirebaseAuthException) {
                                String code = ((FirebaseAuthException) task.getException()).getErrorCode();
                                if (code.equals("ERROR_CONFIGURATION_NOT_FOUND")) {
                                    errorMessage = "Email/Password sign-in is not enabled in Firebase Console.";
                                } else {
                                    errorMessage = task.getException().getMessage();
                                }
                            } else {
                                errorMessage = task.getException().getMessage();
                            }
                        }
                        showError(errorMessage);
                    }
                });
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(SignupActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void showError(String message) {
        errorTextView.setText(message);
        errorTextView.setVisibility(android.view.View.VISIBLE);
    }
}
