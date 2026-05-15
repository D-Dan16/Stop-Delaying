package stop_delaying.ui.fragments.settings;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.procrastination.R;

import stop_delaying.ui.fragments.tasks.task_handlers.TaskRepository;
import stop_delaying.utils.ConfigurableDialogFragment;
import stop_delaying.utils.FBBranches;
import stop_delaying.ui.activities.OpeningScreen;
import stop_delaying.utils.Utils;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Fragment for managing user preferences and account settings. Allows users 
 * to update profiles, toggle notifications, and manage their account status.
 */
public class SettingsFragment extends Fragment {
    private TextView username;
    private TextView email;
    private MaterialCardView cardEditProfilePopup;
    private MaterialCardView cardChangePasswordPopup;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private
    Switch switchAllowNotifications;
    private MaterialButton btnLogout;
    private MaterialButton btnDeleteUserPopup;

    /** Static flag indicating if notifications are globally enabled within the app. */
    private static boolean isNotificationsEnabled = true;

    @Override
    public View onCreateView(
        LayoutInflater inflater,
        ViewGroup container,
        Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize UI elements
        // UI elements
        username = view.findViewById(R.id.username);
        email = view.findViewById(R.id.email);
        MaterialCardView cardToggleNotifications = view.findViewById(R.id.cardToggleNotifications);
        cardEditProfilePopup = view.findViewById(R.id.cardEditProfilePopup);
        cardChangePasswordPopup = view.findViewById(R.id.cardChangePasswordPopup);
        switchAllowNotifications = view.findViewById(R.id.switchAllowNotifications);
        btnLogout = view.findViewById(R.id.btnLogout);
        btnDeleteUserPopup = view.findViewById(R.id.btnDeleteUserPopup);

        // Update the UI with user information
        updateUserInfoAtTop();

        // Set up listeners for various actions
        setupToggleNotificationsListener();
        setupEditProfileListener();
        setupChangePasswordListener();
        setupDeleteUserListener();
        loggingOutListener();
    }


    /**
     * Checks if notifications are currently disabled based on the user's toggle state.
     */
    public static boolean isNotificationsDisabled() {
        return !isNotificationsEnabled;
    }

    /** Configures the listener for the notification toggle switch. */
    private void setupToggleNotificationsListener() {
        switchAllowNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isNotificationsEnabled = isChecked;

            Toast.makeText(
                    getContext(),
                    isChecked ? "Notifications enabled" : "Notifications disabled",
                    Toast.LENGTH_SHORT
            ).show();

            //! Updating all cards' Notif button's color based on the new state will be done in the appropriate Tasks fragment..
        });
    }

    /** Retrieves and displays the current user's username and email from Firebase. */
    private void updateUserInfoAtTop() {
        FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
        if (fbUser != null) {
            FirebaseDatabase.getInstance()
                .getReference(FBBranches.USERS)
                .child(fbUser.getUid())
                .child(FBBranches.Users.USER_NAME)
                .get().addOnSuccessListener(snapshot -> {
                    if (snapshot.exists() && snapshot.getValue() != null)
                        username.setText(snapshot.getValue().toString());
                });

            email.setText(fbUser.getEmail());
        }
    }

    /**
     * Sets up the listener for the 'Edit Profile' action, opening a dialog to update 
     * user-specific information in the database.
     */
    private void setupEditProfileListener() {
        cardEditProfilePopup.setOnClickListener(v -> ConfigurableDialogFragment.showDialog(requireView(), getParentFragmentManager(), R.layout.cv_edit_profile, dialogFragment -> {
            FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
            if (fbUser == null)
                return;


            // Get references to EditTexts for username and email
            EditText etUsername = dialogFragment.findViewById(R.id.et_edit_user_username);
            EditText etEmail = dialogFragment.findViewById(R.id.et_edit_user_email);

            // Get database reference for the user's username
            DatabaseReference usernameRef = FirebaseDatabase.getInstance()
                    .getReference(FBBranches.USERS)
                    .child(fbUser.getUid())
                    .child(FBBranches.Users.USER_NAME);

            // Set the current username and email in the EditText fields
            usernameRef.get().addOnSuccessListener(snapshot -> {
                if (snapshot.exists() && snapshot.getValue() != null)
                    etUsername.setText(snapshot.getValue().toString());
            });
            etEmail.setText(fbUser.getEmail());

            //region send info
            dialogFragment.findViewById(R.id.bUpdateUserProperties).setOnClickListener(v1 -> {
                TextInputLayout emailTIL = dialogFragment.findViewById(R.id.tilEditEmail);
                TextInputLayout usernameTIL = dialogFragment.findViewById(R.id.tilEditUsername);

                // Clear any previous error messages
                emailTIL.setError(null);
                usernameTIL.setError(null);

                // Get the new username and email from the EditText fields
                String newUsername = etUsername.getText().toString().trim();
                String newEmail = etEmail.getText().toString().trim();

                // Validate new username
                if (newUsername.isEmpty()) {
                    usernameTIL.setError("Username is required.");
                    return;
                }

                // Validate new email
                if (newEmail.isEmpty()) {
                    emailTIL.setError("Email is required.");
                    return;
                } else if (!Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
                    emailTIL.setError("Invalid email format.");
                    return;
                }

                // Update the user's email and username in Firebase
                usernameRef.setValue(newUsername);
                fbUser.verifyBeforeUpdateEmail(newEmail).addOnCompleteListener(task -> {
                    if (task.isSuccessful())
                        Toast.makeText(getContext(), "Verification email sent to " + newEmail, Toast.LENGTH_LONG)
                             .show();
                    else
                        Toast.makeText(getContext(), "Failed to send verification. Is the email real?", Toast.LENGTH_LONG)
                             .show();
                });
            });
            //endregion
        }));
    }

    /**
     * Configures the listener for changing the user's password, managing validation 
     * and Firebase Authentication updates.
     */
    private void setupChangePasswordListener() {
        cardChangePasswordPopup.setOnClickListener(v -> ConfigurableDialogFragment.showDialog(requireView(), getParentFragmentManager(), R.layout.cv_update_user_password, dialogFragment -> {
            @SuppressLint("CutPasteId") View btnUpdatePassword = dialogFragment.findViewById(R.id.bUpdateUserProperties);

            btnUpdatePassword.setOnClickListener(v1 -> {
                FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
                if (fbUser == null)
                    return;

                // Get references to TextInputLayouts for new password and confirm new password
                TextInputLayout newPasswordTIL = dialogFragment.findViewById(R.id.tilNewPassword);
                TextInputLayout newPasswordConfirmTIL = dialogFragment.findViewById(R.id.tilConfirmNewPassword);

                // Clear any previous error messages
                newPasswordTIL.setError(null);
                newPasswordConfirmTIL.setError(null);

                // Get new password and confirmation from EditTexts
                String newPassword = ((EditText) dialogFragment.findViewById(R.id.et_edit_user_password)).getText().toString();
                String newPasswordConfirm = ((EditText) dialogFragment.findViewById(R.id.et_edit_confirm_user_password)).getText().toString();

                // Validate new password strength
                if (Utils.isPasswordNotValid(newPassword)) {
                    newPasswordTIL.setError("Password must be at least 8 characters long and include a letter and a number.");
                    return;
                }

                // Check if the new password and confirmation match
                if (!newPassword.equals(newPasswordConfirm)) {
                    newPasswordConfirmTIL.setError("Passwords do not match.");
                    return;
                }

                // Update user's password in Firebase Authentication
                fbUser.updatePassword(newPassword).addOnCompleteListener(task -> {
                    if (task.isSuccessful())
                        Toast.makeText(getContext(), "Password successfully updated.", Toast.LENGTH_SHORT)
                             .show();
                    else
                        Toast.makeText(getContext(), "Failed to update password.", Toast.LENGTH_LONG)
                             .show();
                });
            });

        }));
    }

    /**
     * Configures the account deletion listener, ensuring cascading deletion 
     * of authentication credentials and associated data.
     */
    private void setupDeleteUserListener() {
        btnDeleteUserPopup.setOnClickListener(v -> ConfigurableDialogFragment.showDialog(requireView(), getParentFragmentManager(), R.layout.cv_confirm_delete_user, dialogFragment -> {
            View btnDeleteAccount = dialogFragment.findViewById(R.id.bDeleteAccount);
            btnDeleteAccount.setOnClickListener(v1 -> {
                FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
                if (fbUser == null)
                    return;

                // Delete the user from Firebase Authentication
                fbUser.delete().addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        // Now, delete related data from Firebase Realtime Database
                        DatabaseReference userNode = FirebaseDatabase.getInstance().getReference(FBBranches.USERS);
                        userNode.child(fbUser.getUid()).removeValue();

                        Toast.makeText(getContext(), "User deleted.", Toast.LENGTH_SHORT).show();
                        // Navigate to the opening screen after account deletion
                        startActivity(new Intent(getContext(), OpeningScreen.class));
                    } else
                        Toast.makeText(getContext(), "User cannot be deleted.", Toast.LENGTH_SHORT).show();
                });

                // We need to delete the user's tasks stored in the Firebase as well
                TaskRepository.removeUserTasksFromFirebase(fbUser.getUid(),new TaskRepository.TaskOperationCallback() {
                    @Override public void onSuccess() {
                        Log.d("TaskRepository", "User tasks deleted successfully.");
                    }
                    @Override public void onFailure(String error) {
                        Log.e("TaskRepository", "Failed to delete user tasks: " + error);
                    }
                });

            });
        }));
    }

    /** Configures the logout listener to terminate the current session and return to the start screen. */
    private void loggingOutListener() {
        btnLogout.setOnClickListener(v -> {
            FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
            if (fbUser == null) {
                Toast.makeText(getContext(), "No user logged in.", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseAuth.getInstance().signOut();
            Toast.makeText(getContext(), "Logging out user.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getContext(), OpeningScreen.class));
        });
    }
}
