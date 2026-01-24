package stop_delaying.ui.fragments.tasks.task_handlers;

import static java.text.MessageFormat.format;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.procrastination.R;

import java.util.ArrayList;
import java.util.List;

import stop_delaying.models.Task;

@SuppressLint("NotifyDataSetChanged")
public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.TaskViewHolder> {
    private List<Task> hiddenTasks;
    private List<Task> visibleTasks;
    private SelectionActionHandler.OnSelectionChangeListener selectionChangeListener;
    private SelectionActionHandler.OnStartSelectionListener startSelectionListener;

    public TaskListAdapter(List<Task> taskList) {
        this.hiddenTasks = new ArrayList<>();
        this.visibleTasks = new ArrayList<>(taskList);
    }

    public void setOnSelectionChangeListener(SelectionActionHandler.OnSelectionChangeListener listener) {
        this.selectionChangeListener = listener;
    }

    public void setOnStartSelectionListener(SelectionActionHandler.OnStartSelectionListener listener) {
        this.startSelectionListener = listener;
    }

    // Helper notifiers used by helper util to keep adapter encapsulation
    public void notifySelectionChanged() {
        if (selectionChangeListener != null) selectionChangeListener.onSelectionChanged(getSelectedCount());
    }

    public void notifyStartSelection() {
        if (startSelectionListener != null) startSelectionListener.onStartSelection();
    }

    @NonNull
    @Override
    /// Called when a new card task is being made.
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_card_item, parent, false);
        TaskViewHolder holder = new TaskViewHolder(view);

        //Add listeners for the card upon creation
        InsertCardResponsiveness.configureCardInteractions(view, holder, this);

        return holder;
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