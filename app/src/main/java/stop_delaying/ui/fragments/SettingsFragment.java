package stop_delaying.ui.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.procrastination.R;

import stop_delaying.ui.activities.MainApp;
import stop_delaying.ui.activities.OpeningScreen;
import stop_delaying.utils.Utils;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsFragment extends Fragment {
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

        profileImage = view.findViewById(R.id.profileImage);
        username = view.findViewById(R.id.username);
        email = view.findViewById(R.id.email);
        cardToggleNotifications = view.findViewById(R.id.cardToggleNotifications);
        cardEditProfilePopup = view.findViewById(R.id.cardEditProfilePopup);
        cardChangePasswordPopup = view.findViewById(R.id.cardChangePasswordPopup);
        switchAllowNotifications = view.findViewById(R.id.switchAllowNotifications);
        btnLogout = view.findViewById(R.id.btnLogout);
        btnDeleteUserPopup = view.findViewById(R.id.btnDeleteUserPopup);

        setListeners();
    }

    private void setListeners() {
        cardEditProfilePopup.setOnClickListener(v -> Utils.showPopup(requireView(), requireContext(), R.layout.cv_edit_profile));

        cardChangePasswordPopup.setOnClickListener(v -> Utils.showPopup(requireView(), requireContext(), R.layout.cv_update_user_password));

        btnDeleteUserPopup.setOnClickListener(v -> {
            View popupView = Utils.showPopup(requireView(), requireContext(), R.layout.cv_confirm_delete_user);
            View btnDeleteAccount = popupView.findViewById(R.id.bDeleteAccount);
            btnDeleteAccount.setOnClickListener(v1 -> {
                FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
                if (fbUser == null) {
                    return;
                }

                fbUser.delete().addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        // Now we need to delete related data about the user from the real-time database
                        DatabaseReference userNode = FirebaseDatabase.getInstance().getReference("Users");
                        userNode.child(fbUser.getUid()).removeValue();

                        Toast.makeText(getContext(), "User deleted.", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getContext(), OpeningScreen.class));
                    } else {
                        Toast.makeText(getContext(), "User cannot be deleted.", Toast.LENGTH_SHORT).show();
                    }
                });

            });
        });



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
