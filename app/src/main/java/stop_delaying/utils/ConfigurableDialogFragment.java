package stop_delaying.utils;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.procrastination.R;

import java.util.function.Consumer;

public class ConfigurableDialogFragment extends DialogFragment {
    private final int layoutResId;
    private final Runnable onDismissCallback;
    private final Consumer<View> onViewCreatedListener;

    public ConfigurableDialogFragment(int layoutResId, Consumer<View> onViewCreatedListener, Runnable onDismissCallback) {
        this.layoutResId = layoutResId;
        this.onDismissCallback = onDismissCallback;
        this.onViewCreatedListener = onViewCreatedListener;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(layoutResId, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setCancelable(false);

        View closeButton = view.findViewById(R.id.iv_exit_popup);
        if (closeButton != null)
            closeButton.setOnClickListener(v -> dismiss());


        // Create the logic for the dialog that has been provided
        if (onViewCreatedListener != null)
            onViewCreatedListener.accept(requireView());
    }

    @Override
    public void onStart() {
        super.onStart();
        // Set the width to 90% like your original PopupWindow
        if (getDialog() != null && getDialog().getWindow() != null) {
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.9);
            getDialog().getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
            // Optional: Remove default dialog background to use your layout's background
            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (onDismissCallback != null) onDismissCallback.run();
    }

}