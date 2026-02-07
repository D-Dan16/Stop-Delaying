package stop_delaying.ui.fragments.tasks.task_handlers;

import android.graphics.Color;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.procrastination.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import stop_delaying.models.Task;
/**
 * ViewModel responsible for managing task-related UI state and business logic.
 * It holds task lists, handles filtering, selection, and coordinates data flow between the UI and TaskRepository.
 */
public class TasksViewModel extends ViewModel {
    /// The data structure holding tasks categorized by their status for UI display
    private final MutableLiveData<Map<Task.TaskStatus, Tasks>> _uiTaskLists = new MutableLiveData<>();
    private final String TAG = "TasksViewModel";

    /// Being called by the ViewModelProvider
    public TasksViewModel() {
        TaskRepository.observeUserTasks(new TaskRepository.TaskFetchCallback() {
            @Override public void onTasksFetched(Map<Task.TaskStatus, List<Task>> fetchedCategorizedTasks) {
                for (Map.Entry<Task.TaskStatus, List<Task>> entry : fetchedCategorizedTasks.entrySet())
                    Log.d(TAG, "  " + entry.getKey() + ": " + entry.getValue().size() + " tasks");

                // Initialize UI TaskLists based on fetched data
                Map<Task.TaskStatus, Tasks> newUiTaskLists = new HashMap<>();
                for (Map.Entry<Task.TaskStatus, List<Task>> entry : fetchedCategorizedTasks.entrySet())
                    newUiTaskLists.put(entry.getKey(), new Tasks(new ArrayList<>(entry.getValue()), new ArrayList<>()));

                _uiTaskLists.setValue(newUiTaskLists);
                Log.d(TAG, "UI TaskLists updated");
            }

            @Override public void onFetchFailed(String error) {
                Log.e(TAG, "Failed to fetch tasks: " + error);
            }
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        TaskRepository.removeTasksListener();
    }

    /**
     * Adds a task optimistically to the UI and saves it to Firebase.
     * The real-time listener will handle the confirmation from Firebase.
     */
    public void addTask(Task task) {
        // Optimistic update - add to UI immediately
        Map<Task.TaskStatus, Tasks> currentUiTaskLists = _uiTaskLists.getValue();

        if (currentUiTaskLists != null) {
            currentUiTaskLists.get(task.getStatus()).add(task);
            _uiTaskLists.setValue(currentUiTaskLists);
        }

        // Save to Firebase - real-time listener will sync any conflicts
        TaskRepository.addTaskToFirebase(task, new TaskRepository.TaskOperationCallback() {
            @Override public void onSuccess() {
                // No action needed - listener handles updates
            }
            @Override public void onFailure(String error) {
                System.err.println("Failed to add task: " + error);
                // Could rollback optimistic update here if needed
            }
        });
    }

    /**
     * Adds multiple tasks optimistically to the UI and saves them to Firebase.
     * The real-time listener will handle the confirmation from Firebase.
     */
    public void addAllTasks(List<Task> tasks) {
        // Optimistic update - add to UI immediately
        Map<Task.TaskStatus, Tasks> currentUiTaskLists = _uiTaskLists.getValue();

        if (currentUiTaskLists != null) {
            for (Task task : tasks) {
                Task.TaskStatus status = task.getStatus();
                currentUiTaskLists.get(status).add(task);
            }
            _uiTaskLists.setValue(currentUiTaskLists);
        }

        // Save to Firebase - real-time listener will sync any conflicts
        for (Task t : tasks)
            TaskRepository.addTaskToFirebase(t, new TaskRepository.TaskOperationCallback() {
                @Override
                public void onSuccess() {
                    // No action needed - listener handles updates
                }

                @Override
                public void onFailure(String error) {
                    System.err.println("Failed to add task: " + error);
                }
            });
    }


    /**
     * Removes a task optimistically from the UI and deletes it from Firebase.
     * The real-time listener will handle the confirmation from Firebase.
     */
    public void removeTask(Task task) {
        // Optimistic update - remove from UI immediately
        Map<Task.TaskStatus, Tasks> currentUiTaskLists = _uiTaskLists.getValue();

        if (currentUiTaskLists != null) {
            currentUiTaskLists.get(task.getStatus()).visibleTasks().remove(task);
            _uiTaskLists.setValue(currentUiTaskLists);
        }

        // Delete from Firebase - real-time listener will sync any conflicts
        TaskRepository.removeTaskFromFirebase(task, new TaskRepository.TaskOperationCallback() {
            @Override public void onSuccess() {
                // No action needed - listener handles updates
            }
            @Override public void onFailure(String error) {
                System.err.println("Failed to remove task: " + error);
                // Could rollback optimistic update here if needed
            }
        });
    }

    /**
     * Updates a task's status and syncs to Firebase. Used when moving tasks between lists.
     * The real-time listener will handle updating the UI in all lists.
     */
    public void updateTask(Task task) {
        if (task.getTaskId() == null) {
            Log.e(TAG, "Cannot update task with null taskId");
            return;
        }

        // Update in Firebase - real-time listener will handle UI updates
        TaskRepository.updateTaskInFirebase(task, new TaskRepository.TaskOperationCallback() {
            @Override public void onSuccess() {
                Log.d(TAG, "Task updated successfully: " + task.getTaskId());
            }
            @Override public void onFailure(String error) {
                Log.e(TAG, "Failed to update task: " + error);
            }
        });
    }

    /**
     * Returns the LiveData for all tasks.
     */
    public MutableLiveData<Map<Task.TaskStatus, Tasks>> getUiTaskLists() {
        return this._uiTaskLists;
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
