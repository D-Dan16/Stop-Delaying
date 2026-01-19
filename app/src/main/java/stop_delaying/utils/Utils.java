package stop_delaying.utils;

import android.view.View;
import android.view.ViewGroup;

public final class Utils {
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
