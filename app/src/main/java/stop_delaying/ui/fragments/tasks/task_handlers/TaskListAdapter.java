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


/// A new card will be created from {@link #setTasks}, which is being called from TasksFragment.setupTaskObservers() - this method triggers the task observer setup inside the fragment.
@SuppressLint("NotifyDataSetChanged")
public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.TaskViewHolder> {
    private final Tasks tasks;
    private SelectionActionHandler.OnSelectionChangeListener selectionChangeListener;
    private SelectionActionHandler.OnStartSelectionListener startSelectionListener;
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

    public void notifySelectionChanged() {
        if (selectionChangeListener != null)
            selectionChangeListener.onSelectionChanged(getSelectedCount());
    }

    public void notifyStartSelection() {
        if (startSelectionListener != null)
            startSelectionListener.onStartSelection();
    }


    /// Called when a new card task is being made.
    /// A new card will be created from {@link #setTasks}, which is being called from TasksFragment.setupTaskObservers() - this method triggers the task observer setup inside the fragment.
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
    /// called to display the data at the specified position.
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

    /// Being called from the TasksFragment, in setupTaskObservers()
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
     * @return number of items currently marked as selected.
     */
    public int getSelectedCount() {
        int count = 0;
        for (Task t : tasks.visibleTasks())
            if (t.isTaskSelected())
                count++;
        return count;
    }

    public List<Task> getVisibleTasks() {
        return tasks.visibleTasks();
    }

    /**
     * @return a new list containing all currently selected tasks (snapshot).
     */
    public List<Task> getSelectedTasks() {
        List<Task> selected = new ArrayList<>();
        for (Task t : tasks.visibleTasks())
            if (t.isTaskSelected())
                selected.add(t);

        return selected;
    }



    /**
     * Clears the selection flag on all tasks and refreshes the list UI.
     */
    public void clearSelection() {
        tasks.clearSelection();
        notifyDataSetChanged();
    }

    /**
     * Removes all currently selected tasks from the adapter data and refreshes the list UI.
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

    public void filterTasks(String query) {
        this.currentFilter = query;
        tasks.filterTasks(query);
        notifyDataSetChanged();
    }

    public void unfilterTasks() {
        this.currentFilter = null;
        tasks.unfilterTasks();
        notifyDataSetChanged();
    }

    public void addTask(Task task) {
        tasks.add(task);
        if (currentFilter != null)
            tasks.filterTasks(currentFilter);
        notifyDataSetChanged();
    }
}
