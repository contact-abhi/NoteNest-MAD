package com.example.notenest;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.List;

/**
 * Sections Fragment for organizing notes by categories
 */
public class SectionsFragment extends Fragment implements SectionsAdapter.OnSectionClickListener {

    private RecyclerView recyclerView;
    private FloatingActionButton fabCreateSection;
    private SectionsAdapter sectionsAdapter;
    private FirebaseFirestore firestore;
    private FirebaseAuth mAuth;

    public SectionsFragment() {
        // Required empty public constructor
    }

    public static SectionsFragment newInstance() {
        return new SectionsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sections, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Firebase
        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Initialize UI
        recyclerView = view.findViewById(R.id.sectionsRecyclerView);
        fabCreateSection = view.findViewById(R.id.fabCreateSection);

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        sectionsAdapter = new SectionsAdapter(this);
        recyclerView.setAdapter(sectionsAdapter);

        // Set up FAB
        fabCreateSection.setOnClickListener(v -> openCreateSectionScreen());

        // Load sections
        loadSections();
    }

    private void loadSections() {
        String userId = mAuth.getCurrentUser().getUid();

        firestore.collection("sections")
                .whereEqualTo("userId", userId)
                .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .addSnapshotListener((querySnapshot, error) -> {
                    if (error != null) {
                        Toast.makeText(getContext(), "Error loading sections", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (querySnapshot != null) {
                        List<Section> sections = querySnapshot.toObjects(Section.class);
                        sectionsAdapter.setSections(sections);
                    }
                });
    }

    private void openCreateSectionScreen() {
        Intent intent = new Intent(getContext(), CreateSectionActivity.class);
        startActivity(intent);
    }

    @Override
    public void onSectionClick(Section section) {
        // TODO: Navigate to section details and show notes in this section
        Toast.makeText(getContext(), "Clicked: " + section.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSectionDelete(Section section) {
        if (section.getId() != null) {
            firestore.collection("sections").document(section.getId())
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Section deleted", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Error deleting section: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }
}
