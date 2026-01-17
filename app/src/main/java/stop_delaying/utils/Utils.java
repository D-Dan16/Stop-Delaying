package stop_delaying.utils;

import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.FragmentManager;

import java.util.function.Consumer;

public final class Utils {
    public static void showDialog(View containerView, FragmentManager fragmentManager, int popupLayout, Consumer<View> viewInitializer) {
        applyDimmingEffect(containerView, true);

        ConfigurableDialogFragment dialog = new ConfigurableDialogFragment(
                popupLayout,
                viewInitializer,
                () -> applyDimmingEffect(containerView, false)
        );

        dialog.show(fragmentManager, "custom_popup");
    }

    public static void showDialog(View containerView, FragmentManager fragmentManager, int popupLayout) {
        showDialog(containerView, fragmentManager, popupLayout, v -> {});
    }

    public static void applyDimmingEffect(View rootViewToDimFrom, boolean shouldBeDimmed) {
        if (rootViewToDimFrom instanceof ViewGroup viewGroup) {
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                viewGroup.getChildAt(i).setAlpha(shouldBeDimmed ? 0.3f : 1.0f);
            }
        }
    }

    public static boolean isPasswordNotValid(String password) {
        return !password.matches(".*[A-Za-z].*") || !password.matches(".*\\d.*") || password.length() < 8;
    }
}
