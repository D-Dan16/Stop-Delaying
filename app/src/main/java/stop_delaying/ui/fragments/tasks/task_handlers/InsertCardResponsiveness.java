package stop_delaying.ui.fragments.tasks.task_handlers;

import static java.text.MessageFormat.format;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.example.procrastination.R;

import java.util.List;

import stop_delaying.models.Date;
import stop_delaying.models.Task;
import stop_delaying.models.TimeOfDay;
import stop_delaying.ui.fragments.tasks.TasksFragment;
import stop_delaying.utils.Utils;
import stop_delaying.utils.notifications_and_scheduling.TaskScheduler;
import stop_delaying.utils.notifications_and_scheduling.NotificationCreator;

/**
 * Helper utility for attaching interaction behavior to a task card (inserted card)
 * such as long-press selection, tap-to-toggle selection, and notification toggle.
 */
final class InsertCardResponsiveness {
    private static final String SET_CARD_COLOR_TO_POST_DEADLINE = "stop_delaying.action.UPDATE_CARD_COLOR";
    private static final String EXTRA_TASK_HASH_CODE = "task_hash_code";

    /**
     * Attaches all listeners to the provided task card view.
     */
    static void configureCardInteractions(View view, TaskListAdapter.TaskViewHolder holder, TaskListAdapter adapter) {
        holdCardForStartSelectionProcess(view, holder, adapter);
        toggleCardSelection(view, holder, adapter);
        toggleNotificationOfTask(view, holder, adapter);
    }

    /** Long press to select and start CAB. */
    private static void holdCardForStartSelectionProcess(View view, TaskListAdapter.TaskViewHolder holder, TaskListAdapter adapter) {
        view.setOnLongClickListener(v -> {
            int position = holder.getBindingAdapterPosition();
            if (position == androidx.recyclerview.widget.RecyclerView.NO_POSITION)
                return false;

            Task task = adapter.getVisibleTasks().get(position);
            if (task.isTaskSelected())
                return false;

            // Set background based on the task's state
            Utils.updateTaskCardBackgroundColor(holder, task);

            adapter.notifyStartSelection();
            adapter.notifySelectionChanged();

            return true;
        });
    }

    /** Tap to toggle selection off (or on if selection is active). */
    private static void toggleCardSelection(View view, TaskListAdapter.TaskViewHolder holder, TaskListAdapter adapter) {
        view.setOnClickListener(v -> {
            int position = holder.getBindingAdapterPosition();
            if (position == androidx.recyclerview.widget.RecyclerView.NO_POSITION)
                return;

            Task task = adapter.getVisibleTasks().get(position);
            int currentSelected = adapter.getSelectedCount();
            if (currentSelected <= 0)
                return;

            // Toggle selection state
            task.setTaskSelected(!task.isTaskSelected());

            // Set background based on the task's state
            Utils.updateTaskCardBackgroundColor(holder, task);

            adapter.notifySelectionChanged();
        });
    }

    /** Make the notification button toggle if the task should notify you or not. */
    private static void toggleNotificationOfTask(View view, TaskListAdapter.TaskViewHolder holder, TaskListAdapter adapter) {
        view.findViewById(R.id.iv_task_notification).setOnClickListener(bellNotifButton -> {
            int position = holder.getBindingAdapterPosition();
            if (position == androidx.recyclerview.widget.RecyclerView.NO_POSITION)
                return;

            Task task = adapter.getVisibleTasks().get(position);

            // If there isn't permission to use the notification manager, ask for it, then return.
            if (ActivityCompat.checkSelfPermission(bellNotifButton.getContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                // Fallback if the context isn't an activity for some reason
                if (bellNotifButton.getContext() instanceof android.app.Activity)
                    NotificationCreator.requestNotificationPermission((android.app.Activity) bellNotifButton.getContext());
                else
                    Toast.makeText(bellNotifButton.getContext(), "Please enable notifications in settings", Toast.LENGTH_SHORT).show();
                return;
            }

            // <editor-fold desc="Notification creation logic">
            if (!task.isTaskNotifying()) {
                var hasSucceededScheduling = TaskScheduler.scheduleNotificationAlarms(
                        bellNotifButton.getContext(),
                        new Intent(bellNotifButton.getContext(), TasksFragment.class),
                        List.of(
                                calculateScheduleDelay(24 * 3600, task.getDueTimeOfDay(), task.getDueDate()),
                                calculateScheduleDelay(5 * 3600, task.getDueTimeOfDay(), task.getDueDate()),
                                calculateScheduleDelay(1 * 3600, task.getDueTimeOfDay(), task.getDueDate())
                        ),
                        task.hashCode(),
                        List.of(
                                format("24 hours left for - {0}", task.getTitle()),
                                format("5 hours left for - {0}", task.getTitle()),
                                format("1 hour left for - {0}", task.getTitle())
                        ), task.getDescription(), R.drawable.ic_assignment, NotificationManager.IMPORTANCE_DEFAULT, TasksFragment.NOTIFICATION_CHANNEL_TASKS_CHANNEL_ID
                );

                if (!hasSucceededScheduling) return;

                task.setTaskNotifying(true);
                Toast.makeText(bellNotifButton.getContext(), "Notification alarm scheduled", Toast.LENGTH_SHORT).show();
                ((ImageView) bellNotifButton).setImageResource(R.drawable.ic_turn_notifs_on);
            } else {
                TaskScheduler.cancelNotificationAlarm(bellNotifButton.getContext(), task.hashCode());
                task.setTaskNotifying(false);
                ((ImageView) bellNotifButton).setImageResource(R.drawable.ic_turn_notifs_off);

                Toast.makeText(bellNotifButton.getContext(), "Notification alarm cancelled", Toast.LENGTH_SHORT).show();
            }
            //</editor-fold>
        });
    }

    private static long calculateScheduleDelay(long timeLeftUntilDeadline, TimeOfDay dueTimeOfDay, Date dueDate) {
        return dueTimeOfDay.calcTimeUntil() + dueDate.calcTimeUntil() - timeLeftUntilDeadline;
    }
}
