package com.example.notenest;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.widget.Spinner;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.widget.EditText;
import android.text.TextWatcher;
import android.text.Editable;
import android.widget.ArrayAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.ListenerRegistration;
import java.util.List;
import java.util.ArrayList;

/**
 * Home Fragment displaying user's notes
 */
public class HomeFragment extends Fragment implements NotesAdapter.OnNoteClickListener {

    private RecyclerView recyclerView;
    private FloatingActionButton fabAddNote;
    private Spinner sectionFilterSpinner;
    private EditText searchEditText;
    private NotesAdapter notesAdapter;
    private FirebaseFirestore firestore;
    private FirebaseAuth mAuth;
    private String selectedSectionId = null; // null means show all notes
    private List<Note> allNotes = new ArrayList<>(); // Store all notes for filtering
    private List<Note> filteredNotes = new ArrayList<>(); // Store filtered notes
    private ProgressBar loadingIndicator;
    private LinearLayout emptyState;
    private TextView errorView;
    private ListenerRegistration notesListener;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Firebase
        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Initialize UI
        initializeUI(view);

        // Load notes from Firestore
        loadNotes();
    }

    private void initializeUI(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
        fabAddNote = view.findViewById(R.id.fabAddNote);
        sectionFilterSpinner = view.findViewById(R.id.sectionFilterSpinner);
        searchEditText = view.findViewById(R.id.searchNotes);
        loadingIndicator = view.findViewById(R.id.loadingIndicator);
        emptyState = view.findViewById(R.id.emptyState);
        errorView = view.findViewById(R.id.errorView);

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        notesAdapter = new NotesAdapter(this);
        recyclerView.setAdapter(notesAdapter);

        // Set up FAB
        fabAddNote.setOnClickListener(v -> openAddNoteScreen());

        // Set up Search functionality
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterNotes(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        
        // Load sections for spinner
        loadSectionsForFilter();
    }

    private void loadNotes() {
        String userId = mAuth.getCurrentUser().getUid();

        // Clean up previous listener to avoid duplicates
        if (notesListener != null) {
            notesListener.remove();
            notesListener = null;
        }

        // Show loading
        if (loadingIndicator != null) loadingIndicator.setVisibility(View.VISIBLE);
        if (errorView != null) errorView.setVisibility(View.GONE);

        com.google.firebase.firestore.Query query = firestore.collection("notes")
                .whereEqualTo("userId", userId)
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .limit(200);

        // Filter by section if selected
        if (selectedSectionId != null) {
            query = query.whereEqualTo("sectionId", selectedSectionId);
        }

        notesListener = query.addSnapshotListener((querySnapshot, error) -> {
            // Hide loading
            if (loadingIndicator != null) loadingIndicator.setVisibility(View.GONE);

            if (error != null) {
                // Show error message
                if (errorView != null) {
                    errorView.setText(getString(R.string.error_loading_notes) + "\n" + error.getMessage());
                    errorView.setVisibility(View.VISIBLE);
                }
                return;
            }

            if (querySnapshot != null) {
                allNotes = querySnapshot.toObjects(Note.class);
                // Apply current search filter
                String searchQuery = searchEditText.getText().toString();
                filterNotes(searchQuery);

                // Show empty state if nothing to display
                if (allNotes.isEmpty()) {
                    if (emptyState != null) emptyState.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    if (emptyState != null) emptyState.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (notesListener != null) {
            notesListener.remove();
            notesListener = null;
        }
    }

    private void filterNotes(String searchQuery) {
        filteredNotes.clear();
        
        if (searchQuery.isEmpty()) {
            // If search is empty, show all notes
            filteredNotes.addAll(allNotes);
        } else {
            // Filter notes by title and content
            String lowerCaseQuery = searchQuery.toLowerCase();
            for (Note note : allNotes) {
                if (note.getTitle().toLowerCase().contains(lowerCaseQuery) || 
                    note.getContent().toLowerCase().contains(lowerCaseQuery)) {
                    filteredNotes.add(note);
                }
            }
        }
        
        // Update adapter with filtered notes
        notesAdapter.setNotes(filteredNotes);
    }

    private void loadSectionsForFilter() {
        String userId = mAuth.getCurrentUser().getUid();

        firestore.collection("sections")
                .whereEqualTo("userId", userId)
                .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .addSnapshotListener((querySnapshot, error) -> {
                    if (error != null || querySnapshot == null) {
                        return;
                    }

                    List<Section> sections = querySnapshot.toObjects(Section.class);
                    
                    // Create spinner options with "All Notes" at the top
                    java.util.List<String> options = new java.util.ArrayList<>();
                    options.add("All Notes");
                    for (Section section : sections) {
                        options.add(section.getName());
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), 
                            android.R.layout.simple_spinner_item, options);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    sectionFilterSpinner.setAdapter(adapter);

                    // Handle spinner selection
                    sectionFilterSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                            if (position == 0) {
                                selectedSectionId = null; // Show all notes
                            } else {
                                selectedSectionId = sections.get(position - 1).getId();
                            }
                            // Clear search and reload notes with new filter
                            searchEditText.setText("");
                            loadNotes();
                        }

                        @Override
                        public void onNothingSelected(android.widget.AdapterView<?> parent) {
                        }
                    });
                });
    }

    private void openAddNoteScreen() {
        Intent intent = new Intent(getContext(), AddNoteActivity.class);
        startActivity(intent);
    }

    @Override
    public void onNoteClick(Note note) {
        Intent intent = new Intent(getContext(), NoteDetailActivity.class);
        intent.putExtra("note", note);
        startActivity(intent);
    }

    @Override
    public void onNoteDelete(Note note) {
        if (note.getId() != null) {
            firestore.collection("notes").document(note.getId())
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        // Note deleted successfully
                    })
                    .addOnFailureListener(e -> {
                        // Handle error
                    });
        }
    }
}
