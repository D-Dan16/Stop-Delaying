package stop_delaying.adapters;

import static java.text.MessageFormat.format;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.procrastination.R;
import stop_delaying.models.Task;
import stop_delaying.ui.fragments.tasks.TasksFragment;
import stop_delaying.utils.AlarmScheduler;
import stop_delaying.utils.NotificationCreator;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView adapter for task cards with built-in multi-selection support.
 * <p>
 * Selection UX:
 * - Long-press a card to enter selection mode and select it.
 * - While any item is selected, tapping a card toggles its selection state.
 */
@SuppressLint("NotifyDataSetChanged")
public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.TaskViewHolder> {
    private List<Task> hiddenTasks;
    private List<Task> visibleTasks;
    private OnSelectionChangeListener selectionChangeListener;
    private OnStartSelectionListener startSelectionListener;

    public TaskListAdapter(List<Task> taskList) {
        this.hiddenTasks = new ArrayList<>();
        this.visibleTasks = new ArrayList<>(taskList);
    }

    //<editor-fold desc="Internal Interfaces">
    /**
     * Notifies listeners whenever the number of selected items changes.
     */
    public interface OnSelectionChangeListener {
        void onSelectionChanged(int selectedCount);
    }

    /**
     * Fired on the very first long-press that begins selection mode.
     */
    public interface OnStartSelectionListener {
        void onStartSelection();
    }

    public void setOnSelectionChangeListener(OnSelectionChangeListener listener) {
        this.selectionChangeListener = listener;
    }

    public void setOnStartSelectionListener(OnStartSelectionListener listener) {
        this.startSelectionListener = listener;
    }
    //</editor-fold>

    @NonNull
    @Override
    /// Called when a new card task is being made.
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_card_item, parent, false);
        TaskViewHolder holder = new TaskViewHolder(view);

        //Add listeners for the card upon creation
        addCardListeners(view, holder);

        return holder;
    }

    /**
     * Adds all of the listeners for the card.
     * This includes:
     * - Long-press to select and start CAB
     * - Tap to toggle selection off (or on if selection is active)
     * - Click on the notification button to toggle if the task should notify you or not.
     */
    private void addCardListeners(View view, TaskViewHolder holder) {
        holdCardForStartSelectionProcess(view, holder);

        toggleCardSelection(view, holder);

        toggleNotificationOfTask(view, holder);
    }


    ///Long press to select and start CAB
    private void holdCardForStartSelectionProcess(View view, TaskViewHolder holder) {
        view.setOnLongClickListener(v -> {
            int position = holder.getBindingAdapterPosition();
            if (position == RecyclerView.NO_POSITION)
                return false;

            Task task = visibleTasks.get(position);
            if (task.isTaskSelected())
                return false;

            ((CardView) v).setCardBackgroundColor(v.getResources().getColor(R.color.bg_task_card_selected, null));
            task.setTaskSelected(true);

            if (startSelectionListener != null)
                startSelectionListener.onStartSelection();
            if (selectionChangeListener != null)
                selectionChangeListener.onSelectionChanged(getSelectedCount());

            return true;
        });
    }

    /// Tap to toggle selection off (or on if selection is active)
    private void toggleCardSelection(View view, TaskViewHolder holder) {
        view.setOnClickListener(v -> {
            int position = holder.getBindingAdapterPosition();
            if (position == RecyclerView.NO_POSITION)
                return;

            Task task = visibleTasks.get(position);
            int currentSelected = getSelectedCount();
            if (currentSelected <= 0)
                return;

            // Toggle selection state
            boolean nowSelected = !task.isTaskSelected();
            task.setTaskSelected(nowSelected);
            ((CardView) v).setCardBackgroundColor(v.getResources().getColor(nowSelected ? R.color.bg_task_card_selected : R.color.bg_task_card, null));

            if (selectionChangeListener != null)
                selectionChangeListener.onSelectionChanged(getSelectedCount());
        });
    }

    /// Make the notification button toggle if the task should notify you or not.
    private void toggleNotificationOfTask(View view, TaskViewHolder holder) {
        view.findViewById(R.id.iv_task_notification).setOnClickListener(bellNotifButton -> {
            int position = holder.getBindingAdapterPosition();
            if (position == RecyclerView.NO_POSITION)
                return;

            Task task = visibleTasks.get(position);

            // If there isn't permission to use the notification manager, ask for it, then return.
            if (ActivityCompat.checkSelfPermission(bellNotifButton.getContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                if (bellNotifButton.getContext() instanceof android.app.Activity) {
                    NotificationCreator.requestNotificationPermission((android.app.Activity) bellNotifButton.getContext());
                } else {
                    // Fallback if the context isn't an activity for some reason
                    Toast.makeText(bellNotifButton.getContext(), "Please enable notifications in settings", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // If there isn't permission to use the alarm manager, ask for it, then return.
            if (!AlarmScheduler.canSchedule(bellNotifButton.getContext())) {
                Toast.makeText(bellNotifButton.getContext(), "Alarm manager not available", Toast.LENGTH_SHORT).show();
                return;
            }

            //<editor-fold desc="Notifcation creation logic">
            if (!task.isTaskNotifying()) {
                AlarmManager alarmMgr = (AlarmManager) bellNotifButton.getContext().getSystemService(Context.ALARM_SERVICE);
                if (alarmMgr == null) {
                    Log.e("TaskListAdapter", "AlarmManager is null");
                    return;
                }

                var hasSucceededScheduling = AlarmScheduler.scheduleNotificationAlarm(
                        bellNotifButton.getContext(),
                        CalculateTimeUntilThereIs1DayLeft(),
                        task.hashCode(),
                        TasksFragment.NOTIFICATION_CHANNEL_TASKS_CHANNEL_ID,
                        TasksFragment.NOTIFICATION_CHANNEL_TASKS_CHANNEL_NAME,
                        TasksFragment.NOTIFICATION_CHANNEL_TASKS_CHANNEL_DESCRIPTION,
                        task.getTitle(),
                        task.getDescription(),
                        R.drawable.ic_assignment,
                        NotificationManager.IMPORTANCE_DEFAULT,
                        new Intent(bellNotifButton.getContext(), TasksFragment.class)
                );

                if (!hasSucceededScheduling) {
                    Toast.makeText(bellNotifButton.getContext(), "Failed to schedule notification alarm", Toast.LENGTH_SHORT).show();
                } else {
                    task.setTaskNotifying(true);
                    Toast.makeText(bellNotifButton.getContext(), "Notification alarm scheduled", Toast.LENGTH_SHORT).show();
                    ((ImageView) bellNotifButton).setImageResource(R.drawable.ic_turn_notifs_on);
                }
            } else {
                AlarmScheduler.cancelNotificationAlarm(bellNotifButton.getContext(), task.hashCode());
                task.setTaskNotifying(false);
                ((ImageView) bellNotifButton).setImageResource(R.drawable.ic_turn_notifs_off);

                Toast.makeText(bellNotifButton.getContext(), "Notification alarm cancelled", Toast.LENGTH_SHORT).show();
            }
            //</editor-fold>
        });
    }

    //TODO: Implement the CalculateTimeUntilThereIs1DayLeft method
    private Long CalculateTimeUntilThereIs1DayLeft() {
        return System.currentTimeMillis() + 2000L;
    }

    @Override
    /// called to display the data at the specified position.
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = visibleTasks.get(position);

        holder.tvTaskTitle.setText(task.getTitle());
        holder.tvTaskDescription.setText(task.getDescription());

        var date = task.getDueDate();
        var timeOfDay = task.getDueTimeOfDay();
        holder.tvTaskDueDate.setText(format("{0}/{1}/{2}", date.day(), date.month(), date.year()));
        holder.tvTaskDueTime.setText(format("{0}:{1}", timeOfDay.hour(), timeOfDay.minute()));

        holder.ivTaskStatus.setImageResource(switch (task.getStatus()) {
            case TODO -> R.drawable.ic_assignment;
            case COMPLETED -> R.drawable.ic_done;
            case CANCELED -> R.drawable.ic_canceled_task;
        });

        // Set background based on task's selected state
        int colorRes = task.isTaskSelected() ?
                R.color.bg_task_card_selected :
                R.color.bg_task_card;

        ((CardView) holder.itemView).setCardBackgroundColor(holder.itemView.getContext().getResources().getColor(colorRes, null));
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView tvTaskTitle;
        TextView tvTaskDescription;
        TextView tvTaskDueDate;
        TextView tvTaskDueTime;
        ImageView ivTaskStatus;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTaskTitle = itemView.findViewById(R.id.tv_task_title);
            tvTaskDescription = itemView.findViewById(R.id.tv_task_description);
            tvTaskDueDate = itemView.findViewById(R.id.tv_task_due_date);
            tvTaskDueTime = itemView.findViewById(R.id.tv_task_due_time);
            ivTaskStatus = itemView.findViewById(R.id.iv_task_status);
        }
    }

    public List<Task> getVisibleTasks() {
        return visibleTasks;
    }

    public List<Task> getHiddenTasks() {
        return hiddenTasks;
    }

    public void setTasks(List<Task> newTasks) {
        this.hiddenTasks = new ArrayList<>();
        this.visibleTasks = new ArrayList<>(newTasks);
        notifyDataSetChanged();
    }

    @Override public int getItemCount() {
        return visibleTasks.size();
    }

    /**
     * @return number of items currently marked as selected.
     */
    public int getSelectedCount() {
        int count = 0;
        for (Task t : visibleTasks) {
            if (t.isTaskSelected()) count++;
        }
        return count;
    }

    /**
     * @return a new list containing all currently selected tasks (snapshot).
     */
    public List<Task> getSelectedTasks() {
        List<Task> selected = new ArrayList<>();
        for (Task t : visibleTasks) {
            if (t.isTaskSelected()) selected.add(t);
        }
        return selected;
    }

    /**
     * Clears the selection flag on all tasks and refreshes the list UI.
     */
    public void clearSelection() {
        visibleTasks.forEach(t -> t.setTaskSelected(false));
        notifyDataSetChanged();
    }

    /**
     * Removes all currently selected tasks from the adapter data and refreshes the list UI.
     */
    public void removeSelectedTasks() {
        List<Integer> selected = new ArrayList<>();
        for (int i = 0; i < visibleTasks.size(); i++) {
            if (visibleTasks.get(i).isTaskSelected()) {
                selected.add(i);
            }
        }

        for (int selectedTask : selected) {
            notifyItemRemoved(selectedTask);
        }

        visibleTasks.removeIf(Task::isTaskSelected);
    }

    public void filterTasks(String query) {
        if (query == null || query.isEmpty()) return;

        hiddenTasks.clear();

        for (Task task : visibleTasks) {
            if (
                !task.getTitle().toLowerCase().contains(query.toLowerCase().trim()) &&
                !task.getDescription().toLowerCase().contains(query.toLowerCase().trim())
            ) {
               hiddenTasks.add(task);
            }
        }

        visibleTasks.removeAll(hiddenTasks);
        notifyDataSetChanged();
    }

    public void unfilterTasks() {
        visibleTasks.addAll(hiddenTasks);
        hiddenTasks.clear();

        notifyDataSetChanged();
    }
}