# NoteNest Authentication System

## Overview

NoteNest now includes a complete Firebase Authentication system with login and signup screens. Users can create accounts, sign in, and manage their sessions.

## Architecture

### Activities

1. **LoginActivity** - First screen users see
   - Email and password login
   - Link to signup screen
   - Error handling and validation
   - Automatic redirect to MainActivity if already logged in

2. **SignupActivity** - Account creation screen
   - Email and password signup
   - Password confirmation
   - Input validation
   - Error handling
   - Back button to login

3. **MainActivity** - Main app screen (protected)
   - Only accessible to authenticated users
   - Display current user's email
   - Logout functionality
   - Firebase services initialization

## User Flow

```
Launch App
    ↓
LoginActivity (Launcher Activity)
    ├─ Already logged in? → MainActivity
    ├─ Invalid credentials → Show error
    ├─ New user? → SignupActivity
    └─ Valid login → MainActivity
         ↓
    SignupActivity
         ├─ Create account → Firebase Auth
         ├─ Validation errors → Show error
         ├─ Back button → LoginActivity
         └─ Success → MainActivity
              ↓
         MainActivity (Protected)
              ├─ Display logged-in user
              └─ Logout → LoginActivity
```

## File Structure

### Java Classes

```
app/src/main/java/com/example/notenest/
├── LoginActivity.java        - Login screen logic
├── SignupActivity.java       - Signup screen logic
├── MainActivity.java         - Main app (with auth check)
├── FirebaseUtils.java        - Firebase helpers
└── ...
```

### XML Layouts

```
app/src/main/res/layout/
├── activity_login.xml        - Login UI
├── activity_signup.xml       - Signup UI
├── activity_main.xml         - Main app UI (with logout)
└── ...
```

### XML Drawables

```
app/src/main/res/drawable/
├── edit_text_background.xml  - EditText border style
├── button_background.xml     - Primary button style (purple)
└── button_outline_background.xml - Secondary button style
```

### XML Resources

```
app/src/main/res/values/
├── strings.xml               - All text strings (updated)
├── themes.xml                - Colors and theme (updated)
└── ...

app/src/main/res/menu/
└── menu_main.xml             - Options menu with logout
```

## Features

### LoginActivity Features

- ✅ Email validation (pattern check)
- ✅ Password validation (minimum 6 characters)
- ✅ Firebase authentication
- ✅ Error message display
- ✅ Loading indicator (progress bar)
- ✅ Auto-redirect if already logged in
- ✅ Link to signup screen
- ✅ Toast messages for feedback

### SignupActivity Features

- ✅ Email validation
- ✅ Password validation (minimum 6 characters)
- ✅ Confirm password validation
- ✅ Password match verification
- ✅ Firebase user creation
- ✅ Error message display
- ✅ Loading indicator
- ✅ Back to login button
- ✅ Toast messages for feedback

### MainActivity Features

- ✅ Authentication state check on startup
- ✅ Redirect to login if not authenticated
- ✅ Display logged-in user's email
- ✅ Logout button at bottom of screen
- ✅ Logout option in app menu
- ✅ Firebase services initialization
- ✅ Post-logout redirect to login screen

## Validation Rules

### Email
- Must not be empty
- Must match email pattern (RFC 5322)
- Example: `user@example.com`

### Password
- Must be at least 6 characters long
- Required for both login and signup
- Must match confirm password field (signup only)

### Error Handling

All errors are caught and displayed in red error text:
- Invalid email format
- Password too short
- Passwords don't match
- User not found (login)
- Email already in use (signup)
- Network errors
- Firebase exceptions

## UI Components

### LoginActivity Layout

```
┌─────────────────────────────┐
│   Welcome Back (Title)      │
│   Sign in to your account   │
├─────────────────────────────┤
│ Email [_____________________] │
│ Password [_________________] │
├─────────────────────────────┤
│ [     LOGIN BUTTON     ]    │
├─────────────────────────────┤
│ Don't have account? Sign Up │
└─────────────────────────────┘
```

### SignupActivity Layout

```
┌─────────────────────────────┐
│   Create Account (Title)    │
│   Join NoteNest today       │
├─────────────────────────────┤
│ Email [_____________________] │
│ Password [_________________] │
│ Confirm [_________________] │
├─────────────────────────────┤
│ [   CREATE ACCOUNT   ]      │
│ [    BACK TO LOGIN   ]      │
└─────────────────────────────┘
```

### MainActivity Layout

```
┌─────────────────────────────┐
│       NoteNest              │
│  Welcome to NoteNest        │
├─────────────────────────────┤
│ Logged in as: user@ex.com   │
│ Firebase initialized ✓      │
├─────────────────────────────┤
│ [Content Area]              │
│                             │
├─────────────────────────────┤
│ [      LOGOUT BUTTON      ] │
└─────────────────────────────┘
```

## Colors & Styling

### Color Scheme

- **Primary**: `#6200EE` (Purple 500)
- **Primary Dark**: `#3700B3` (Purple 700)
- **Secondary**: `#03DAC6` (Teal 200)
- **Error**: `#F44336` (Red 500)
- **Error Background**: `#FFEBEE` (Light Red)
- **Text**: `#000000` (Black)
- **Dividers**: `#CCCCCC` (Light Gray)

### Button Styling

- **Primary Button** (Login/Signup)
  - Background: Purple 700
  - Text: White
  - Corners: 4dp radius

- **Secondary Button** (Back)
  - Background: White with purple border
  - Text: Purple 700
  - Border: 2dp

### EditText Styling

- Background: White
- Border: 1dp light gray
- Corners: 4dp radius
- Padding: 16dp horizontal

## Navigation Flow

### Intent Flags Used

```java
// Used when navigating to protected screens
Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK

// This clears the backstack, preventing users from going back to login
```

## String Resources

### Login Strings

```xml
<string name="login_title">Welcome Back</string>
<string name="login_subtitle">Sign in to your account</string>
<string name="email">Email Address</string>
<string name="password">Password</string>
<string name="login_button">Login</string>
<string name="no_account">Don\'t have an account?</string>
<string name="signup_link">Sign Up</string>
```

### Signup Strings

```xml
<string name="signup_title">Create Account</string>
<string name="signup_subtitle">Join NoteNest today</string>
<string name="confirm_password">Confirm Password</string>
<string name="signup_button">Create Account</string>
<string name="back_button">Back to Login</string>
```

## Code Examples

### Check if User is Logged In

```java
FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
if (currentUser != null) {
    // User is logged in
    String email = currentUser.getEmail();
}
```

### Login with Email and Password

```java
mAuth.signInWithEmailAndPassword(email, password)
    .addOnCompleteListener(task -> {
        if (task.isSuccessful()) {
            // Login successful
            navigateToMainActivity();
        } else {
            // Login failed
            showError(task.getException().getMessage());
        }
    });
```

### Create New User Account

```java
mAuth.createUserWithEmailAndPassword(email, password)
    .addOnCompleteListener(task -> {
        if (task.isSuccessful()) {
            // Account created
            navigateToMainActivity();
        } else {
            // Account creation failed
            showError(task.getException().getMessage());
        }
    });
```

### Logout

```java
FirebaseAuth.getInstance().signOut();
// Redirect to LoginActivity
```

## Firebase Configuration

### Required Firebase Services

- ✅ Firebase Authentication (Email/Password)
- ✅ Cloud Firestore (for future features)
- ✅ Cloud Storage (for future features)

### AndroidManifest.xml Updates

```xml
<!-- Activities registered -->
<activity android:name=".LoginActivity" android:exported="true">
    <!-- Launcher activity -->
</activity>
<activity android:name=".SignupActivity" android:exported="false" />
<activity android:name=".MainActivity" android:exported="false" />

<!-- Permissions added -->
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

## Testing the Authentication

### Test Login Flow

1. Launch app → LoginActivity shown
2. Enter invalid email → Error "Please enter a valid email address"
3. Enter password < 6 chars → Error "Password must be at least 6 characters"
4. Enter correct credentials → Navigate to MainActivity
5. User email displayed → "Logged in as: user@example.com"

### Test Signup Flow

1. Click "Sign Up" link → SignupActivity shown
2. Enter mismatched passwords → Error "Passwords do not match"
3. Email already exists → Error from Firebase
4. All fields valid → Account created, navigate to MainActivity

### Test Logout

1. On MainActivity, click logout button
2. SignOut complete → Toast shown
3. Redirect to LoginActivity → Can log in again

### Test Session Persistence

1. Login successfully
2. Kill the app (don't logout)
3. Relaunch app → MainActivity shown directly (no login screen)

## Development Notes

### Common Issues & Solutions

**Issue**: App shows login screen even after successful login
- **Solution**: Check that `Intent.FLAG_ACTIVITY_CLEAR_TASK` is used when navigating

**Issue**: EditText doesn't show border
- **Solution**: Ensure `drawable/edit_text_background.xml` exists and is referenced

**Issue**: Buttons have no color
- **Solution**: Check that button drawable XMLs exist in `drawable/` folder

**Issue**: Navigation stack issues
- **Solution**: Use `FLAG_ACTIVITY_CLEAR_TASK` to prevent back stack problems

## Future Enhancements

- [ ] Google Sign-In integration
- [ ] Phone authentication
- [ ] Password reset functionality
- [ ] Email verification
- [ ] Social login (Facebook, Twitter)
- [ ] Biometric authentication
- [ ] Account settings screen
- [ ] Profile management

## Security Considerations

✅ **Implemented**
- Password minimum length (6 characters)
- Email format validation
- Secure transmission via HTTPS
- Firebase Auth handles password encryption

⚠️ **Best Practices**
- Never hardcode credentials
- Always use HTTPS
- Keep `google-services.json` in `.gitignore`
- Use strong password requirements
- Implement rate limiting (server-side)
- Add CAPTCHA for bot prevention (future)

## Related Files

- [FIREBASE_SETUP.md](FIREBASE_SETUP.md) - Firebase configuration
- [README.md](README.md) - Project overview
- [QUICK_START.md](QUICK_START.md) - Quick start guide
