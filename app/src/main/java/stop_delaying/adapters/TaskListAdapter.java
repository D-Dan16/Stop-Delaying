package stop_delaying.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.procrastination.R;
import stop_delaying.models.Task;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.TaskViewHolder> {

    private List<Task> taskList;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yy", Locale.getDefault());

    public TaskListAdapter(List<Task> taskList) {
        this.taskList = taskList;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_card_item, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.tvTaskTitle.setText(task.getTitle());
        holder.tvTaskDescription.setText(task.getDescription());
        holder.tvTaskDueDate.setText(dateFormat.format(task.getDueDate()));

        holder.ivTaskStatus.setImageResource( switch (task.getStatus()) {
            case TODO -> R.drawable.ic_tasks;
            case COMPLETED -> R.drawable.ic_home;
            case CANCELED -> R.drawable.ic_leaderboard;
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView tvTaskTitle;
        TextView tvTaskDescription;
        TextView tvTaskDueDate;
        ImageView ivTaskStatus;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTaskTitle = itemView.findViewById(R.id.tv_task_title);
            tvTaskDescription = itemView.findViewById(R.id.tv_task_description);
            tvTaskDueDate = itemView.findViewById(R.id.tv_task_due_date);
            ivTaskStatus = itemView.findViewById(R.id.iv_task_status);
        }
    }

    public void setTasks(List<Task> newTasks) {
        this.taskList = newTasks;
        notifyDataSetChanged();
    }
}
