# Home Dashboard Implementation

## Overview

The Home Dashboard is the main screen users see after logging in. It features a RecyclerView of notes, a Floating Action Button (FAB) for adding notes, and bottom navigation for switching between Home, Sections, and Profile tabs.

---

## Architecture

### Activities

1. **MainActivity** - Splash/Redirect Activity
   - Checks authentication state
   - Redirects to LoginActivity if not authenticated
   - Redirects to HomeActivity if authenticated

2. **HomeActivity** - Main Dashboard Container
   - Hosts fragments for Home, Sections, and Profile
   - Manages bottom navigation
   - Controls fragment switching

### Fragments

1. **HomeFragment** - Notes Display
   - RecyclerView with notes from Firestore
   - Floating Action Button to add notes
   - Real-time data synchronization
   - Delete note functionality

2. **SectionsFragment** - Note Categories (Placeholder)
   - Coming soon feature
   - Ready for categories/sections implementation

3. **ProfileFragment** - User Account
   - Display user email
   - Logout button
   - Account management

---

## Data Model

### Note Class

```java
public class Note {
    private String id;              // Document ID
    private String title;           // Note title
    private String content;         // Note content preview
    private long timestamp;         // Creation/modification time
    private String userId;          // Owner's user ID
}
```

**Firestore Collection**: `notes`
**Structure**:
```
notes/
├── [noteId1]/
│   ├── id: "noteId1"
│   ├── title: "Note Title"
│   ├── content: "Note content..."
│   ├── timestamp: 1716734400000
│   └── userId: "user123"
├── [noteId2]/
└── ...
```

---

## Components

### RecyclerView & Adapter

**NotesAdapter**
- Displays list of notes from Firestore
- ViewHolder pattern for efficient rendering
- Click listener for editing notes
- Delete button for removing notes

**Features**:
- Note title display
- Content preview (2 lines max)
- Timestamp formatting (1m ago, 2h ago, etc.)
- Delete button per note
- Real-time updates via `addSnapshotListener()`

### Floating Action Button (FAB)

- **Position**: Bottom-right corner
- **Icon**: Plus icon (+)
- **Color**: Purple 700
- **Action**: Opens AddNoteActivity (placeholder)
- **Behavior**: Always visible above notes

### Bottom Navigation

**Navigation Tabs**:
1. **Home** (House icon) - Notes list
2. **Sections** (Grid icon) - Note categories
3. **Profile** (Person icon) - User account

**Features**:
- Icon labels showing
- Smooth fragment transitions
- Persistent across rotations
- State preservation

---

## Firestore Integration

### Query for Notes

```java
firestore.collection("notes")
    .whereEqualTo("userId", userId)
    .orderBy("timestamp", Query.Direction.DESCENDING)
    .addSnapshotListener((querySnapshot, error) -> {
        if (querySnapshot != null) {
            List<Note> notes = querySnapshot.toObjects(Note.class);
            notesAdapter.setNotes(notes);
        }
    });
```

**Features**:
- User-specific notes (filtered by userId)
- Real-time synchronization
- Sorted by newest first
- Automatic updates when data changes

### Delete Note

```java
firestore.collection("notes")
    .document(note.getId())
    .delete();
```

---

## File Structure

### Java Classes

```
app/src/main/java/com/example/notenest/
├── MainActivity.java              - Splash/redirect logic
├── HomeActivity.java              - Dashboard container
├── HomeFragment.java              - Notes list screen
├── SectionsFragment.java          - Categories (placeholder)
├── ProfileFragment.java           - User account screen
├── Note.java                      - Data model
├── NotesAdapter.java              - RecyclerView adapter
├── LoginActivity.java
├── SignupActivity.java
└── FirebaseUtils.java
```

### XML Layouts

```
app/src/main/res/layout/
├── activity_main.xml              - Splash screen
├── activity_home.xml              - Dashboard with nav
├── fragment_home.xml              - Home notes tab
├── fragment_sections.xml          - Sections tab
├── fragment_profile.xml           - Profile tab
└── item_note.xml                  - Note card item
```

### XML Drawables

```
app/src/main/res/drawable/
├── ic_home.xml                   - Home icon
├── ic_sections.xml               - Sections icon
├── ic_profile.xml                - Profile icon
├── ic_add.xml                    - Plus icon (FAB)
├── note_card_background.xml      - Card styling
├── button_background.xml         - Button styling
└── ...
```

### XML Menus

```
app/src/main/res/menu/
├── bottom_navigation.xml         - Bottom nav items
└── menu_main.xml                 - Options menu (legacy)
```

### XML Resources

```
app/src/main/res/values/
├── strings.xml                   - Updated with dashboard strings
├── themes.xml                    - Colors and theme
└── ...
```

---

## UI/UX Details

### Colors

- **Primary**: Purple 700 (#3700B3)
- **Header**: Purple 500 (#6200EE)
- **Text**: Black (#000000)
- **Secondary Text**: Gray (#666666)
- **Dividers**: Light Gray (#E0E0E0)
- **Card Background**: White with light border

### Spacing & Sizing

- **Header Height**: Wrap content with 16dp padding
- **RecyclerView**: 8dp padding
- **Note Cards**: 8dp margin, 12dp padding
- **FAB**: 16dp margin from bottom-right
- **Bottom Nav Height**: 56dp (standard)

### Typography

- **Header Title**: 24sp bold, purple
- **Note Title**: 16sp bold, black
- **Note Content**: 14sp, gray
- **Timestamp**: 12sp, light gray
- **Button Text**: 16sp bold

---

## Navigation Flow

```
App Launch
    ↓
MainActivity (Splash)
    ├─ Not logged in → LoginActivity
    └─ Logged in → HomeActivity
                   ├─ Home Fragment (default)
                   ├─ Sections Fragment
                   └─ Profile Fragment → Logout → LoginActivity
```

---

## View Binding

View Binding is enabled in `build.gradle`:

```gradle
buildFeatures {
    viewBinding true
}
```

**Usage in Activity**:
```java
private ActivityHomeBinding binding;

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = ActivityHomeBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());
}
```

---

## Fragment Lifecycle

### HomeFragment

1. `onCreateView()` - Inflate layout
2. `onViewCreated()` - Initialize UI and Firestore listener
3. `loadNotes()` - Add Firestore snapshot listener
4. `onNoteClick()` - Handle note click (placeholder)
5. `onNoteDelete()` - Delete note from Firestore

### PersistenceFragment

- Loads on demand
- Maintains separate state
- Fragment is recreated each time (no caching)

---

## Real-Time Features

### Firestore Snapshot Listener

```java
firestore.collection("notes")
    .whereEqualTo("userId", userId)
    .orderBy("timestamp", Query.Direction.DESCENDING)
    .addSnapshotListener((querySnapshot, error) -> {
        // Called when:
        // 1. Initial data loaded
        // 2. Data changes in real-time
        // 3. Network connection changes
    });
```

**Benefits**:
- Automatic UI updates
- Offline support
- Real-time sync across devices

---

## Error Handling

### Firestore Errors

```java
.addSnapshotListener((querySnapshot, error) -> {
    if (error != null) {
        // Handle error silently
        // User won't see loading data
        Log.w(TAG, "Error loading notes", error);
        return;
    }
    // Update UI with data
});
```

### Delete Errors

```java
.delete()
    .addOnSuccessListener(aVoid -> {
        // Note deleted - UI updated by snapshot listener
    })
    .addOnFailureListener(e -> {
        // Handle deletion error
    });
```

---

## Performance Optimizations

1. **Efficient RecyclerView**
   - ViewHolder pattern
   - Item animations on add/remove
   - Proper layout management

2. **Firestore Query**
   - Filtered by userId (faster queries)
   - Indexed on timestamp
   - Sorted descending (newest first)

3. **Image Optimization**
   - Vector drawables for icons
   - Lazy loading ready for thumbnails

4. **Memory Management**
   - Fragment lifecycle aware
   - Listener cleanup on destroy
   - No memory leaks

---

## Testing Checklist

- [ ] Login redirects to HomeActivity
- [ ] HomeFragment shows notes from Firestore
- [ ] Real-time updates work when adding notes
- [ ] Delete button removes notes
- [ ] Bottom navigation switches fragments
- [ ] Profile shows correct user email
- [ ] Logout button works
- [ ] Timestamp formatting is correct
- [ ] Note cards display properly
- [ ] FAB click opens add screen (when implemented)

---

## Future Enhancements

- [ ] AddNoteActivity - Create new notes
- [ ] EditNoteActivity - Modify existing notes
- [ ] Search functionality
- [ ] Note categories/sections
- [ ] Note pinning
- [ ] Note colors
- [ ] Rich text editing
- [ ] Note sharing
- [ ] Offline sync
- [ ] Image attachments

---

## Strings Added

```xml
<string name="my_notes">My Notes</string>
<string name="add_note">Add Note</string>
<string name="home">Home</string>
<string name="sections">Sections</string>
<string name="profile">Profile</string>
<string name="delete">Delete</string>
<string name="sections_coming_soon">Sections feature coming soon</string>
```

---

## Related Files

- [AUTHENTICATION.md](AUTHENTICATION.md) - Login/signup system
- [FIREBASE_SETUP.md](FIREBASE_SETUP.md) - Firebase configuration
- [README.md](README.md) - Project overview
