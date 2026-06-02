package com.example.notenest;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.notenest.databinding.ItemSectionBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView adapter for displaying sections
 */
public class SectionsAdapter extends RecyclerView.Adapter<SectionsAdapter.SectionViewHolder> {

    private List<Section> sections;
    private OnSectionClickListener listener;
    private FirebaseFirestore firestore;

    public interface OnSectionClickListener {
        void onSectionClick(Section section);
        void onSectionDelete(Section section);
    }

    public SectionsAdapter(OnSectionClickListener listener) {
        this.sections = new ArrayList<>();
        this.listener = listener;
        this.firestore = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public SectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSectionBinding binding = ItemSectionBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new SectionViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SectionViewHolder holder, int position) {
        Section section = sections.get(position);
        holder.bind(section, listener, firestore);
    }

    @Override
    public int getItemCount() {
        return sections.size();
    }

    public void setSections(List<Section> sections) {
        this.sections = sections;
        notifyDataSetChanged();
    }

    public void addSection(Section section) {
        sections.add(0, section);
        notifyItemInserted(0);
    }

    public void removeSection(int position) {
        sections.remove(position);
        notifyItemRemoved(position);
    }

    /**
     * ViewHolder class for sections
     */
    static class SectionViewHolder extends RecyclerView.ViewHolder {
        private final ItemSectionBinding binding;

        public SectionViewHolder(ItemSectionBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Section section, OnSectionClickListener listener, FirebaseFirestore firestore) {
            binding.sectionNameText.setText(section.getName());

            // Get note count for this section
            firestore.collection("notes")
                    .whereEqualTo("sectionId", section.getId())
                    .addSnapshotListener((querySnapshot, error) -> {
                        if (error == null && querySnapshot != null) {
                            int noteCount = querySnapshot.size();
                            String countText = noteCount + " note" + (noteCount != 1 ? "s" : "");
                            binding.noteCountText.setText(countText);
                        }
                    });

            // Click listener for section
            binding.getRoot().setOnClickListener(v -> listener.onSectionClick(section));

            // Delete button listener
            binding.deleteButton.setOnClickListener(v -> listener.onSectionDelete(section));
        }
    }
}
