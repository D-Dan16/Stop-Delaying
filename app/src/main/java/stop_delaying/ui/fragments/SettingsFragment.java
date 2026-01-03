package stop_delaying.ui.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.procrastination.R;
import stop_delaying.utils.Utils;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.button.MaterialButton;

public class SettingsFragment extends Fragment {
    MaterialCardView cardEditProfile;
    MaterialCardView cardChangePassword;
    MaterialButton btnDeleteUser;

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

        cardEditProfile = view.findViewById(R.id.cardEditProfile);
        cardChangePassword = view.findViewById(R.id.cardChangePassword);
        btnDeleteUser = view.findViewById(R.id.btnDeleteUser);

        cardEditProfile.setOnClickListener(v -> Utils.showPopup(requireView(), requireContext(), R.layout.cv_edit_profile));

        cardChangePassword.setOnClickListener(v -> Utils.showPopup(requireView(), requireContext(), R.layout.cv_update_user_password));

        btnDeleteUser.setOnClickListener(v -> Utils.showPopup(requireView(), requireContext(), R.layout.cv_confirm_delete_user));
    }
}
