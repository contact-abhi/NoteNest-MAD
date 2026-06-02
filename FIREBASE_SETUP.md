# Firebase Integration Setup Guide

## Project Configuration

This NoteNest project has been configured with Firebase support including:
- **Firebase Authentication** - User sign-in and management
- **Cloud Firestore** - NoSQL database
- **Cloud Storage** - File storage

## Dependencies Added

```gradle
// Firebase BOM (Bill of Materials) v33.0.0
implementation platform('com.google.firebase:firebase-bom:33.0.0')
implementation 'com.google.firebase:firebase-auth'
implementation 'com.google.firebase:firebase-firestore'
implementation 'com.google.firebase:firebase-storage'
```

## Setup Steps

### 1. Get google-services.json

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Create a new project or select existing one
3. Add Android app with package name: `com.example.notenest`
4. Download `google-services.json`
5. Place the file in: `app/google-services.json`

**Note:** A template `google-services.json` is currently in the app directory. Replace it with the actual file from Firebase Console.

### 2. Verify Dependencies

The project includes:
- Root `build.gradle` - Firebase Google Services plugin added
- `app/build.gradle` - Firebase dependencies and google-services plugin configured
- `AndroidManifest.xml` - Internet and network permissions added

### 3. Security Rules

#### Firestore Rules (`firestore.rules`)
```
- Requires user authentication for all read/write operations
- Deploy via Firebase Console or `firebase deploy --only firestore:rules`
```

#### Storage Rules (`storage.rules`)
```
- Each user can only access their own files in /users/{userId}/
- Deploy via Firebase Console or `firebase deploy --only storage`
```

## Usage in Code

### Using FirebaseUtils Helper Class

```java
// Initialize Firebase
FirebaseUtils.initialize();

// Get current user
FirebaseUser user = FirebaseUtils.getCurrentUser();

// Get Firestore instance
FirebaseFirestore db = FirebaseUtils.getFirestore();

// Get Storage instance
FirebaseStorage storage = FirebaseUtils.getStorage();

// Check if user is logged in
if (FirebaseUtils.isUserLoggedIn()) {
    String userId = FirebaseUtils.getCurrentUserUID();
}
```

### In MainActivity

The app initializes Firebase on startup:
```java
- FirebaseAuth instance created
- Firestore initialized
- Storage initialized
- Status text updates to show successful initialization
```

## Gradle Build Commands

```bash
# Build the project
./gradlew build

# Build and run on device/emulator
./gradlew installDebug

# Run tests
./gradlew test

# Clean build
./gradlew clean build
```

## Common Issues & Solutions

### Issue: `google-services.json` not found
**Solution:** Download the actual file from Firebase Console and place it in the `app/` directory.

### Issue: `com.google.gms:google-services` plugin not found
**Solution:** Ensure you're using a recent version of Android Studio and Gradle (8.1.3+).

### Issue: Build fails with dependency conflicts
**Solution:** The BOM (Bill of Materials) approach handles version compatibility automatically. Rebuild with:
```bash
./gradlew clean build
```

### Issue: Firebase not initializing
**Solution:** Check that:
- `google-services.json` is in the correct location
- App package name matches Firebase Console configuration
- Internet permission is enabled in AndroidManifest.xml

## Next Steps

1. Download actual `google-services.json` from Firebase Console
2. Replace the template file in `app/google-services.json`
3. Sync Gradle files in Android Studio
4. Build and run the project
5. Implement authentication in MainActivity or create new activity for login
6. Add Firestore queries as needed for notes feature
7. Configure Storage for attachments

## Project Structure

```
NoteNest/
├── app/
│   ├── google-services.json (⚠️ Replace with actual file)
│   ├── src/main/
│   │   ├── java/com/example/notenest/
│   │   │   ├── MainActivity.java (Firebase initialization)
│   │   │   └── FirebaseUtils.java (Helper class)
│   │   ├── res/
│   │   │   ├── layout/activity_main.xml
│   │   │   └── values/strings.xml
│   │   └── AndroidManifest.xml (with permissions)
│   └── build.gradle (with Firebase dependencies)
├── build.gradle (with Google Services plugin)
├── firestore.rules (Firestore security rules)
├── storage.rules (Storage security rules)
└── README.md
```

## Security Best Practices

1. **Use Firestore Security Rules** - Protect your database
2. **Enable Authentication** - Restrict access to authenticated users
3. **Configure Storage Rules** - Users can only access their own files
4. **Never commit real credentials** - Keep google-services.json in .gitignore
5. **Use Firebase Authentication** - Don't implement custom auth in client app

## Resources

- [Firebase Console](https://console.firebase.google.com/)
- [Firebase Android Documentation](https://firebase.google.com/docs/android/setup)
- [Cloud Firestore Guide](https://firebase.google.com/docs/firestore)
- [Firebase Storage Guide](https://firebase.google.com/docs/storage)
- [Firebase Security Rules](https://firebase.google.com/docs/firestore/security/get-started)
