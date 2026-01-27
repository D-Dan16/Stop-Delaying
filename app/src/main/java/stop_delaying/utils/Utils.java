package stop_delaying.utils;


import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;

import android.graphics.Color;

import com.example.procrastination.R;

import stop_delaying.models.Task;
import stop_delaying.ui.fragments.tasks.task_handlers.TaskListAdapter;

public final class Utils {
    public static void applyDimmingEffect(View rootViewToDimFrom, boolean shouldBeDimmed) {
        if (rootViewToDimFrom instanceof ViewGroup viewGroup)
            for (int i = 0; i < viewGroup.getChildCount(); i++)
                viewGroup.getChildAt(i).setAlpha(shouldBeDimmed ? 0.3f : 1.0f);
    }

    public static boolean isPasswordNotValid(String password) {
        return !password.matches(".*[A-Za-z].*") || !password.matches(".*\\d.*") || password.length() < 8;
    }

    public static void updateTaskCardBackgroundColor(@NonNull TaskListAdapter.TaskViewHolder holder, Task task) {
        int colorRes = task.hasReachedDeadline() ? R.color.bg_task_card_post_deadline :
                        task.isDeadlineNear() ? R.color.bg_task_card_near_deadline :
                        R.color.bg_task_card;

        int baseColor = ContextCompat.getColor(holder.itemView.getContext(), colorRes);

        ((CardView) holder.itemView).setCardBackgroundColor(
                // Lighten the color if the task is selected
                task.isTaskSelected()
                    ? ColorUtils.blendARGB(baseColor, Color.LTGRAY, 0.3f)
                    : baseColor
        );
    }
}
