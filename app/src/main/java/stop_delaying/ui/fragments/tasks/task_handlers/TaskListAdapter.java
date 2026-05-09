package stop_delaying.ui.fragments.tasks.task_handlers;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.procrastination.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import stop_delaying.models.Task;
import stop_delaying.ui.fragments.settings.SettingsFragment;


/**
 * RecyclerView adapter for displaying task items. Manages task rendering, selection 
 * state, filtering, and event callbacks for bulk selection and UI updates.
 */
@SuppressLint("NotifyDataSetChanged")
public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.TaskViewHolder> {
    /** The collection of tasks managed by this adapter, including filtering logic. */
    private final Tasks tasks;
    /** Callback for notifying when the total number of selected tasks changes. */
    private SelectionActionHandler.OnSelectionChangeListener selectionChangeListener;
    /** Callback for notifying when a bulk selection session is initiated. */
    private SelectionActionHandler.OnStartSelectionListener startSelectionListener;
    /** The current search query used to filter tasks, or null if unfiltered. */
    private String currentFilter;

    public TaskListAdapter(Tasks taskLists) {
        this.tasks = taskLists;
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
        // When you ask for the position of visible tasks, u don't need to worry about the existence of hidden tasks.
        Task task = tasks.visibleTasks().get(position);

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
     * Updates the underlying task collection and applies any active filters.
     */
    public void setTasks(@Nullable List<Task> newTasks) {
        if (newTasks == null) return;

        tasks.setAllTasks(newTasks);
        if (currentFilter != null)
            tasks.filterTasks(currentFilter);

        notifyDataSetChanged();
    }

    @Override public int getItemCount() {
        return tasks.visibleTasks().size();
    }

    /**
     * @return the total number of tasks currently marked as selected.
     */
    public int getSelectedCount() {
        int count = 0;
        for (Task t : tasks.visibleTasks())
            if (t.isTaskSelected())
                count++;
        return count;
    }

    /**
     * @return the list of tasks currently visible in the RecyclerView.
     */
    public List<Task> getVisibleTasks() {
        return tasks.visibleTasks();
    }

    /**
     * @return a list containing all tasks currently marked as selected.
     */
    public List<Task> getSelectedTasks() {
        List<Task> selected = new ArrayList<>();
        for (Task t : tasks.visibleTasks())
            if (t.isTaskSelected())
                selected.add(t);

        return selected;
    }



    /**
     * Resets the selection flag on all tasks and refreshes the display.
     */
    public void clearSelection() {
        tasks.clearSelection();
        notifyDataSetChanged();
    }

    /**
     * Removes all selected tasks from the collection and updates the RecyclerView.
     */
    public void removeSelectedTasks() {
        List<Integer> selectedIndices = new ArrayList<>();
        for (int i = 0; i < tasks.visibleTasks().size(); i++)
            if (tasks.visibleTasks().get(i).isTaskSelected())
                selectedIndices.add(i);

        tasks.removeSelectedTasks(); // This modifies the underlying list

        // Notify items removed in reverse order to avoid index shifting issues
        for (int i = selectedIndices.size() - 1; i >= 0; i--)
            notifyItemRemoved(selectedIndices.get(i));
    }

    /**
     * Filters the task list based on a provided query string.
     */
    public void filterTasks(String query) {
        this.currentFilter = query;
        tasks.filterTasks(query);
        notifyDataSetChanged();
    }

    /**
     * Removes the active filter and displays all tasks in the collection.
     */
    public void unfilterTasks() {
        this.currentFilter = null;
        tasks.unfilterTasks();
        notifyDataSetChanged();
    }

    /**
     * Adds a single task to the collection and refreshes the display.
     */
    public void addTask(Task task) {
        tasks.add(task);
        if (currentFilter != null)
            tasks.filterTasks(currentFilter);
        notifyDataSetChanged();
    }
}
