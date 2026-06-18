package stop_delaying.ui.fragments.tasks.task_handlers;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.procrastination.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import lombok.Getter;
import stop_delaying.models.Task;
import stop_delaying.ui.fragments.settings.SettingsFragment;


/**
 * RecyclerView adapter for displaying task items. Manages task rendering, selection 
 * state, and event callbacks for bulk selection and UI updates.
 */
// This file was made with the aid of AI
@SuppressLint("NotifyDataSetChanged")
public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.TaskViewHolder> {
    /**
     * The collection of tasks currently displayed by this adapter.
     */
    @Getter private final List<Task> visibleTasks = new ArrayList<>();
    /** Callback for notifying when the total number of selected tasks changes. */
    private SelectionActionHandler.OnSelectionChangeListener selectionChangeListener;
    /** Callback for notifying when a bulk selection session is initiated. */
    private SelectionActionHandler.OnStartSelectionListener startSelectionListener;

    public TaskListAdapter() {
    }

    public void setOnSelectionChangeListener(SelectionActionHandler.OnSelectionChangeListener listener) {
        this.selectionChangeListener = listener;
    }

    public void setOnStartSelectionListener(SelectionActionHandler.OnStartSelectionListener listener) {
        this.startSelectionListener = listener;
    }

    /** Triggers the selection change listener with the current count. */
    public void notifySelectionChanged() {
        if (selectionChangeListener != null)
            selectionChangeListener.onSelectionChanged(getSelectedCount());
    }

    /** Triggers the selection start listener. */
    public void notifyStartSelection() {
        if (startSelectionListener != null)
            startSelectionListener.onStartSelection();
    }


    @NonNull @Override public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                                  .inflate(R.layout.task_card_item, parent, false);
        TaskViewHolder holder = new TaskViewHolder(view);

        //Add listeners for the card upon creation
        InsertCardResponsiveness.configureCardInteractions(
                view, holder, this
        );

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = visibleTasks.get(position);

        holder.tvTaskTitle.setText(task.getTitle());
        holder.tvTaskDescription.setText(task.getDescription());

        var date = task.getDueDate();
        var timeOfDay = task.getDueTimeOfDay();
        holder.tvTaskDueDate.setText(String.format(Locale.getDefault(), "%02d-%02d-%02d", date.getDay(), date.getMonth(), date.getYear() % 100));
        holder.tvTaskDueTime.setText(String.format(Locale.getDefault(), "%02d:%02d", timeOfDay.getHour(), timeOfDay.getMinute()));

        holder.ivTaskStatus.setImageResource(switch (task.getStatus()) {
            case TODO -> R.drawable.ic_assignment;
            case COMPLETED -> R.drawable.ic_done;
            case CANCELED -> R.drawable.ic_canceled_task;
        });

        // Set the notif button color based on if the user has enabled (in-app) notifications or not, or if a task reached deadline.
        boolean isNotifEnabled = !SettingsFragment.isNotificationsDisabled() && !task.hasReachedDeadline();
        holder.ivTaskNotification.setColorFilter(
                isNotifEnabled
                        ? holder.itemView.getResources().getColor(R.color.task_card_icon, null)
                        : holder.itemView.getResources().getColor(R.color.task_card_icon_disabled, null)
        );


        // Set background based on the task's state
        TasksViewModel.updateTaskCardBackgroundColor(holder, task);
    }

    /**
     * ViewHolder for task items, containing references to all card UI components.
     */
    public static class TaskViewHolder extends RecyclerView.ViewHolder {

        final TextView tvTaskTitle;
        final TextView tvTaskDescription;
        final TextView tvTaskDueDate;
        final TextView tvTaskDueTime;
        final ImageView ivTaskStatus;
        final ImageView ivTaskNotification;

        TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTaskTitle = itemView.findViewById(R.id.tv_task_title);
            tvTaskDescription = itemView.findViewById(R.id.tv_task_description);
            tvTaskDueDate = itemView.findViewById(R.id.tv_task_due_date);
            tvTaskDueTime = itemView.findViewById(R.id.tv_task_due_time);
            ivTaskStatus = itemView.findViewById(R.id.iv_task_status);
            ivTaskNotification = itemView.findViewById(R.id.iv_task_notification);
        }

    }

    /**
     * Updates the underlying task collection using DiffUtil for efficient UI refreshes.
     */
    public void setTasks(@Nullable List<Task> newTasks) {
        List<Task> safeNewTasks = (newTasks == null) ? new ArrayList<>() : new ArrayList<>(newTasks);

        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override public int getOldListSize() {
                return visibleTasks.size();
            }

            @Override public int getNewListSize() {
                return safeNewTasks.size();
            }

            @Override public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return visibleTasks.get(oldItemPosition).getTaskId().equals(safeNewTasks.get(newItemPosition).getTaskId());
            }

            @Override public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                Task oldTask = visibleTasks.get(oldItemPosition);
                Task newTask = safeNewTasks.get(newItemPosition);
                return oldTask.getStatus() == newTask.getStatus() &&
                       oldTask.isTaskSelected() == newTask.isTaskSelected() &&
                       oldTask.isTaskNotifying() == newTask.isTaskNotifying() &&
                       oldTask.getTitle().equals(newTask.getTitle()) &&
                       oldTask.getDescription().equals(newTask.getDescription());
            }
        });

        this.visibleTasks.clear();
        this.visibleTasks.addAll(safeNewTasks);
        diffResult.dispatchUpdatesTo(this);
    }

    @Override public int getItemCount() {
        return visibleTasks.size();
    }

    /**
     * @return the total number of tasks currently marked as selected.
     */
    public int getSelectedCount() {
        int count = 0;
        for (Task t : visibleTasks)
            if (t.isTaskSelected())
                count++;
        return count;
    }

    /**
     * @return a list containing all tasks currently marked as selected.
     */
    public List<Task> getSelectedTasks() {
        List<Task> selected = new ArrayList<>();
        for (Task t : visibleTasks)
            if (t.isTaskSelected())
                selected.add(t);

        return selected;
    }

    /**
     * Resets the selection flag on all tasks and refreshes the display.
     */
    public void clearSelection() {
        for (Task t : visibleTasks)
            t.setTaskSelected(false);
        notifyDataSetChanged();
    }

    /**
     * Removes all selected tasks from the collection and updates the RecyclerView.
     */
    public void removeSelectedTasks() {
        List<Integer> selectedIndices = new ArrayList<>();
        for (int i = 0; i < visibleTasks.size(); i++)
            if (visibleTasks.get(i).isTaskSelected())
                selectedIndices.add(i);

        visibleTasks.removeIf(Task::isTaskSelected);

        // Notify items removed in reverse order to avoid index shifting issues
        for (int i = selectedIndices.size() - 1; i >= 0; i--)
            notifyItemRemoved(selectedIndices.get(i));
    }

    /**
     * Adds a single task to the collection and refreshes the display.
     */
    public void addTask(Task task) {
        visibleTasks.add(task);
        notifyItemInserted(visibleTasks.size() - 1);
    }
}
