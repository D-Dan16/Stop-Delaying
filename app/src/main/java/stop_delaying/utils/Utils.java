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
        // Get the root view of the fragment
        ViewGroup rootView = (ViewGroup) view;
        if (rootView == null) return;

        // Inflate the popup layout using LayoutInflater
        View popupView = LayoutInflater.from(context).inflate(layoutResId, rootView, false);

        // Find the close button within the newly inflated view
        ImageView closeButton = popupView.findViewById(R.id.iv_exit_popup);

        try {
            closeButton.setOnClickListener(v -> {
                rootView.removeView(popupView);
                setFragmentInteractable(view, true);
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Add the inflated view to the root layout
        rootView.addView(popupView);
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

            child.setClickable(interactable);
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