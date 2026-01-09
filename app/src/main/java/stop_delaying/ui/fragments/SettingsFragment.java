package stop_delaying.ui.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.procrastination.R;

import stop_delaying.FBBranches;
import stop_delaying.ui.activities.OpeningScreen;
import stop_delaying.utils.Utils;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Fragment for displaying and managing user settings.
 * Allows users to update their profile, change password, delete account, and log out.
 */
public class SettingsFragment extends Fragment {
    // UI elements
    CircleImageView profileImage;
    TextView username;
    TextView email;
    MaterialCardView cardToggleNotifications;
    MaterialCardView cardEditProfilePopup;
    MaterialCardView cardChangePasswordPopup;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch switchAllowNotifications;
    MaterialButton btnLogout;
    MaterialButton btnDeleteUserPopup;

    /**
     * Called to have the fragment instantiate its user interface view.
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return The View for the fragment's UI, or null.
     */
    @Override
    public View onCreateView(
        LayoutInflater inflater,
        ViewGroup container,
        Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    /**
     * Called immediately after {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)} has returned, but before any saved state has been restored in to the view.
     * @param view The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize UI elements
        profileImage = view.findViewById(R.id.profileImage);
        username = view.findViewById(R.id.username);
        email = view.findViewById(R.id.email);
        cardToggleNotifications = view.findViewById(R.id.cardToggleNotifications);
        cardEditProfilePopup = view.findViewById(R.id.cardEditProfilePopup);
        cardChangePasswordPopup = view.findViewById(R.id.cardChangePasswordPopup);
        switchAllowNotifications = view.findViewById(R.id.switchAllowNotifications);
        btnLogout = view.findViewById(R.id.btnLogout);
        btnDeleteUserPopup = view.findViewById(R.id.btnDeleteUserPopup);

        // Set up listeners for various actions
        setupEditProfileListener();
        setupChangePasswordListener();
        setupDeleteUserListener();
        loggingOutListener();
    }

    /**
     * Sets up a click listener for the 'Edit Profile' card, which displays a dialog
     * for updating the user's email and username in Firebase.
     */
    private void setupEditProfileListener() {
        cardEditProfilePopup.setOnClickListener(v -> Utils.showDialog(requireView(), getParentFragmentManager(), R.layout.cv_edit_profile, dialogFragment -> {
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
                    .child(FBBranches.USER_NAME);

            // Set the current username and email in the EditText fields
            usernameRef.get().addOnSuccessListener(snapshot -> {
                if (snapshot.exists() && snapshot.getValue() != null) {
                    etUsername.setText(snapshot.getValue().toString());
                }
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
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Verification email sent to " + newEmail, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getContext(), "Failed to send verification. Is the email real?",Toast.LENGTH_LONG).show();
                    }
                });
            });
            //endregion
        }));
    }

    /**
     * Sets up a click listener for the 'Change Password' card, which displays a dialog
     * for updating the user's password in Firebase.
     */
    private void setupChangePasswordListener() {
        cardChangePasswordPopup.setOnClickListener(v -> Utils.showDialog(requireView(), getParentFragmentManager(), R.layout.cv_update_user_password, dialogFragment -> {
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

                // Check if new password and confirmation match
                if (!newPassword.equals(newPasswordConfirm)) {
                    newPasswordConfirmTIL.setError("Passwords do not match.");
                    return;
                }

                // Update user's password in Firebase Authentication
                fbUser.updatePassword(newPassword).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Password successfully updated.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Failed to update password.",Toast.LENGTH_LONG).show();
                    }
                });
            });

        }));
    }

    /**
     * Sets up a click listener for the 'Delete User' button, which displays a confirmation dialog
     * and, upon confirmation, deletes the user's account and associated data from Firebase.
     */
    private void setupDeleteUserListener() {
        btnDeleteUserPopup.setOnClickListener(v -> Utils.showDialog(requireView(), getParentFragmentManager(), R.layout.cv_confirm_delete_user, dialogFragment -> {
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
                    } else {
                        Toast.makeText(getContext(), "User cannot be deleted.", Toast.LENGTH_SHORT).show();
                    }
                });

            });
        }));
    }

    /**
     * Sets up a click listener for the 'Log Out' button, which signs out the current Firebase user
     * and navigates to the {@link OpeningScreen}.
     */
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