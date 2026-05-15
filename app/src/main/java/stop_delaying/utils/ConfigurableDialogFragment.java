package stop_delaying.utils;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.example.procrastination.R;

import java.util.function.Consumer;

/**
 * A highly flexible DialogFragment that can be instantiated with any layout resource. 
 * Supports custom view initialization and dismissal callbacks for UI dimming effects.
 */
public class ConfigurableDialogFragment extends DialogFragment {
    private final int layoutResId;
    private final Runnable onDismissCallback;
    private final Consumer<View> onViewCreatedListener;

    /**
     * Private constructor to enforce the use of static showDialog factory methods.
     */
    private ConfigurableDialogFragment(
            int layoutResId,
            Consumer<View> onViewCreatedListener,
            Runnable onDismissCallback
    ) {
        this.layoutResId = layoutResId;
        this.onDismissCallback = onDismissCallback;
        this.onViewCreatedListener = onViewCreatedListener;
    }

    /**
     * Displays a configurable dialog with specialized handling for dimming the container 
     * view while the dialog is visible.
     */
    private static void showDialog(
            View containerView,
            FragmentManager fragmentManager,
            int popupLayout,
            Consumer<View> viewInitializer,
            Runnable onDismiss
    ) {
        ConfigurableDialogFragment dialog = new ConfigurableDialogFragment(
                popupLayout,
                (v) -> {
                    Utils.applyDimmingEffect(containerView, true);
                    viewInitializer.accept(v);
                },
                () -> {
                    Utils.applyDimmingEffect(containerView, false);
                    onDismiss.run();
                }
        );

        dialog.show(fragmentManager, "custom_popup");
    }

    /**
     * Displays a dialog with a custom view initializer. Useful for popups requiring 
     * data binding or dynamic UI updates.
     */
    public static void showDialog(View containerView, FragmentManager fragmentManager, int popupLayout, Consumer<View> viewInitializer) {
        showDialog(containerView, fragmentManager, popupLayout, viewInitializer, () -> {});
    }

    /**
     * Displays a basic dialog using only a layout resource ID.
     */
    public static void showDialog(View containerView, FragmentManager fragmentManager, int popupLayout) {
        showDialog(containerView, fragmentManager, popupLayout, v -> {}, () -> {});
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
