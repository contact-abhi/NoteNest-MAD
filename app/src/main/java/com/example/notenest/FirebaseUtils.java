package com.example.notenest;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Utility class for Firebase services.
 * Provides convenient access to Firebase Authentication, Firestore, and Storage.
 */
public class FirebaseUtils {

    private static FirebaseAuth firebaseAuth;
    private static FirebaseFirestore firestore;
    private static FirebaseStorage storage;

    /**
     * Initialize Firebase services
     */
    public static void initialize() {
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
    }

    /**
     * Get Firebase Authentication instance
     */
    public static FirebaseAuth getAuth() {
        if (firebaseAuth == null) {
            firebaseAuth = FirebaseAuth.getInstance();
        }
        return firebaseAuth;
    }

    /**
     * Get Firestore instance
     */
    public static FirebaseFirestore getFirestore() {
        if (firestore == null) {
            firestore = FirebaseFirestore.getInstance();
        }
        return firestore;
    }

    /**
     * Get Storage instance
     */
    public static FirebaseStorage getStorage() {
        if (storage == null) {
            storage = FirebaseStorage.getInstance();
        }
        return storage;
    }

    /**
     * Get current user
     */
    public static FirebaseUser getCurrentUser() {
        return getAuth().getCurrentUser();
    }

    /**
     * Check if user is logged in
     */
    public static boolean isUserLoggedIn() {
        return getCurrentUser() != null;
    }

    /**
     * Get storage reference for a specific path
     */
    public static StorageReference getStorageReference(String path) {
        return getStorage().getReference(path);
    }

    /**
     * Get current user UID
     */
    public static String getCurrentUserUID() {
        FirebaseUser user = getCurrentUser();
        return user != null ? user.getUid() : null;
    }
}
