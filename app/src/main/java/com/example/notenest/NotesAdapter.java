package com.example.notenest;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.example.notenest.databinding.ItemNoteBinding;
import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView adapter for displaying notes
 */
public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {

    private List<Note> notes;
    private OnNoteClickListener listener;

    public interface OnNoteClickListener {
        void onNoteClick(Note note);
        void onNoteDelete(Note note);
    }

    public NotesAdapter(OnNoteClickListener listener) {
        this.notes = new ArrayList<>();
        this.listener = listener;
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemNoteBinding binding = ItemNoteBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new NoteViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = notes.get(position);
        holder.bind(note, listener);
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public void setNotes(List<Note> newNotes) {
        // Use DiffUtil for efficient updates
        final List<Note> oldNotes = this.notes;
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return oldNotes.size();
            }

            @Override
            public int getNewListSize() {
                return newNotes.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                String oldId = oldNotes.get(oldItemPosition).getId();
                String newId = newNotes.get(newItemPosition).getId();
                return oldId != null && oldId.equals(newId);
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                Note o = oldNotes.get(oldItemPosition);
                Note n = newNotes.get(newItemPosition);
                return (o.getTitle() == null ? n.getTitle() == null : o.getTitle().equals(n.getTitle()))
                        && (o.getContent() == null ? n.getContent() == null : o.getContent().equals(n.getContent()))
                        && o.getTimestamp() == n.getTimestamp();
            }
        });

        this.notes = new ArrayList<>(newNotes);
        diffResult.dispatchUpdatesTo(this);
    }

    @Override
    public long getItemId(int position) {
        Note note = notes.get(position);
        if (note.getId() == null) return position;
        return note.getId().hashCode();
    }

    public void addNote(Note note) {
        notes.add(0, note);
        notifyItemInserted(0);
    }

    public void removeNote(int position) {
        notes.remove(position);
        notifyItemRemoved(position);
    }

    public void updateNote(int position, Note note) {
        notes.set(position, note);
        notifyItemChanged(position);
    }

    /**
     * ViewHolder class for notes
     */
    static class NoteViewHolder extends RecyclerView.ViewHolder {
        private final ItemNoteBinding binding;

        public NoteViewHolder(ItemNoteBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Note note, OnNoteClickListener listener) {
            binding.noteTitleText.setText(note.getTitle());
            binding.noteContentText.setText(note.getContent());
            
            // Display section if available
            if (note.getSection() != null && !note.getSection().isEmpty()) {
                binding.noteSectionText.setText(note.getSection());
            } else {
                binding.noteSectionText.setText("Uncategorized");
            }
            
            binding.noteTimeText.setText(formatTimestamp(note.getTimestamp()));

            binding.getRoot().setOnClickListener(v -> listener.onNoteClick(note));

            binding.deleteButton.setOnClickListener(v -> listener.onNoteDelete(note));
        }

        private static String formatTimestamp(long timestamp) {
            if (timestamp == 0) return "Just now";
            long diff = System.currentTimeMillis() - timestamp;
            long minutes = diff / (60 * 1000);
            long hours = diff / (60 * 60 * 1000);
            long days = diff / (24 * 60 * 60 * 1000);

            if (minutes < 1) return "Just now";
            if (minutes < 60) return minutes + "m ago";
            if (hours < 24) return hours + "h ago";
            return days + "d ago";
        }
    }
}
