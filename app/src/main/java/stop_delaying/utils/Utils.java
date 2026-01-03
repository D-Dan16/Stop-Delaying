package stop_delaying.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.viewpager2.widget.ViewPager2;

import com.example.procrastination.R;

public final class Utils {
    public static void showPopup(View view, Context context, int layoutResId) {
        // make the rest of the fragment blurred out, and non-interactable
        setFragmentInteractable(view, false);

        addPopup(view, context, layoutResId);
    }

    private static void addPopup(View view, Context context, int layoutResId) {
        // Get the top-level root view of the entire window, which is a FrameLayout
        ViewGroup windowRootView = (ViewGroup) ((android.app.Activity) context).getWindow().getDecorView().getRootView();
        if (windowRootView == null) return;

        // Inflate the popup layout. The parent is the window's root, but we don't attach yet
        View popupView = LayoutInflater.from(context).inflate(layoutResId, windowRootView, false);

        ImageView closeButton = popupView.findViewById(R.id.iv_exit_popup);

        closeButton.setOnClickListener(v -> {
            windowRootView.removeView(popupView);
            setFragmentInteractable(view, true);
        });

        // Add the popup view to the window's root layout
        windowRootView.addView(popupView);
    }

    private static void setFragmentInteractable(View view, boolean interactable) {
        if (view instanceof ViewGroup viewGroup) {
            // Apply a dimming effect to the entire fragment background.
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                viewGroup.getChildAt(i).setAlpha(interactable ? 1.0f : 0.3f);
            }

            // Recursively enable or disable all views within the ViewGroup
            setChildrenInteractable(viewGroup, interactable);
        }
    }

    private static void setChildrenInteractable(ViewGroup viewGroup, boolean interactable) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);

            child.setFocusable(interactable);
            child.setEnabled(interactable);

            // Special handling for ViewPager2 to disable swipes
            if (child instanceof ViewPager2 viewPager2) {
                viewPager2.setUserInputEnabled(interactable);
            }

            // If the child is also a ViewGroup, recurse into it
            if (child instanceof ViewGroup viewGroupChild) {
                setChildrenInteractable(viewGroupChild, interactable);
            }
        }
    }
}