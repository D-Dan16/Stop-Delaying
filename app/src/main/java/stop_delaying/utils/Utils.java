package stop_delaying.utils;

import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.FragmentManager;

import java.util.function.Consumer;

public final class Utils {
    public static void showPopup(View rootView, FragmentManager fragmentManager, int layoutResId) {
        applyDimmingEffect(rootView, true);

        CustomDialogFragment dialog = new CustomDialogFragment(
                layoutResId,
                () -> applyDimmingEffect(rootView, false)
        );

        dialog.show(fragmentManager, "custom_popup");
    }

    public static void showPopup(View rootView, FragmentManager fragmentManager, int layoutResId, Consumer<View> logicInit) {
        applyDimmingEffect(rootView, true);

        CustomDialogFragment dialog = new CustomDialogFragment(
                layoutResId,
                logicInit,
                () -> applyDimmingEffect(rootView, false)
        );

        dialog.show(fragmentManager, "custom_popup");
    }

    private static void applyDimmingEffect(View rootViewToDimFrom, boolean shouldBeDimmed) {
        if (rootViewToDimFrom instanceof ViewGroup viewGroup) {
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                viewGroup.getChildAt(i).setAlpha(shouldBeDimmed ? 0.3f : 1.0f);
            }
        }
    }
}
