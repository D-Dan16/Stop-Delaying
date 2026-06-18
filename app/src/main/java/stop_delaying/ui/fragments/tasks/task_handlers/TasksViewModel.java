package stop_delaying.ui.fragments.tasks.task_handlers;

import android.app.Application;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.procrastination.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import stop_delaying.models.Task;
import stop_delaying.utils.notifications_and_scheduling.TaskScheduler;

/**
 * ViewModel responsible for managing task-related UI state and business logic. 
 * Coordinates between the UI fragments and the Firebase repository.
 */
// This file was made with the aid of AI
public class TasksViewModel extends AndroidViewModel {
    /** LiveData holding tasks categorized by status for UI consumption. */
    private final MutableLiveData<Map<Task.TaskStatus, Tasks>> _uiTaskLists = new MutableLiveData<>();
    private final String TAG = "TasksViewModel";
    private String currentFilter = null;

    /** Handler for managing the "Smart Fallback" logic. */
    private final Handler fallbackHandler = new Handler(Looper.getMainLooper());
    private Runnable pendingFallback = null;

    /** LiveData tracking the state of background AI task analysis. */
    @Getter
    private final MutableLiveData<Boolean> aiAnalysisInProgress = new MutableLiveData<>(false);

    public TasksViewModel(@NonNull Application application) {
        super(application);

        // --- FALLBACK: Initialize empty state immediately ---
        initializeEmptyState();

        TaskRepository.observeUserTasks(new TaskRepository.TaskFetchCallback() {
            @Override 
            public void onTasksFetched(Map<Task.TaskStatus, List<Task>> fetchedCategorizedTasks) {
                Log.d(TAG, "onTasksFetched called - Authority received from Firebase");
                
                // --- CANCEL FALLBACK: The real data arrived, so we don't need the local update ---
                cancelPendingFallback();

                Map<Task.TaskStatus, Tasks> oldUiTaskLists = _uiTaskLists.getValue();
                Map<Task.TaskStatus, Tasks> newUiTaskLists = new HashMap<>();

                for (Task.TaskStatus status : Task.TaskStatus.values()) {
                    List<Task> newTasksList = fetchedCategorizedTasks.getOrDefault(status, new ArrayList<>());
                    
                    // Preserve selection state from the old list
                    if (oldUiTaskLists != null) {
                        for (Task newTask : newTasksList) {
                            for (Tasks oldTasks : oldUiTaskLists.values()) {
                                Task oldTask = findTaskById(oldTasks, newTask.getTaskId());
                                if (oldTask != null) {
                                    newTask.setTaskSelected(oldTask.isTaskSelected());
                                    break;
                                }
                            }
                        }
                    }

                    Tasks tasksObj = new Tasks(new ArrayList<>(newTasksList), new ArrayList<>());
                    if (currentFilter != null)
                        tasksObj.filterTasks(currentFilter);
                    newUiTaskLists.put(status, tasksObj);
                }

                _uiTaskLists.setValue(newUiTaskLists);
            }

            @Override 
            public void onFetchFailed(String error) {
                Log.e(TAG, "Failed to fetch tasks: " + error);
            }
        });
    }

    /** Initializes the UI task list map with empty collections. */
    private void initializeEmptyState() {
        Map<Task.TaskStatus, Tasks> initialState = new HashMap<>();
        for (Task.TaskStatus status : Task.TaskStatus.values()) {
            initialState.put(status, new Tasks(new ArrayList<>(), new ArrayList<>()));
        }
        _uiTaskLists.setValue(initialState);
    }

    /** Schedules a local "Optimistic" update only if Firebase is slow. */
    private void scheduleFallback(Runnable optimisticAction) {
        cancelPendingFallback();
        pendingFallback = () -> {
            Log.w(TAG, "Firebase slow - Triggering 'Plan B' (Optimistic Local Update)");
            optimisticAction.run();
            _uiTaskLists.setValue(_uiTaskLists.getValue());
            pendingFallback = null;
        };
        // 200ms threshold: Fast enough for the user, enough time for a local Firebase listener.
        fallbackHandler.postDelayed(pendingFallback, 200);
    }

    private void cancelPendingFallback() {
        if (pendingFallback != null) {
            fallbackHandler.removeCallbacks(pendingFallback);
            pendingFallback = null;
        }
    }

    private Task findTaskById(Tasks tasks, String taskId) {
        for (Task t : tasks.visibleTasks())
            if (t.getTaskId().equals(taskId)) return t;
        for (Task t : tasks.hiddenTasks())
            if (t.getTaskId().equals(taskId)) return t;
        return null;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        cancelPendingFallback();
        TaskRepository.removeTasksListener();
    }

    /**
     * Adds a task. Only performs a local update if Firebase is slow.
     */
    public void addTask(Task task) {
        TaskRepository.addTaskToFirebase(task);

        scheduleFallback(() -> {
            Map<Task.TaskStatus, Tasks> current = _uiTaskLists.getValue();
            if (current != null && current.containsKey(task.getStatus())) {
                current.get(task.getStatus()).add(task);
                if (currentFilter != null) current.get(task.getStatus()).filterTasks(currentFilter);
            }
        });
    }

    /**
     * Removes a task. Only performs a local update if Firebase is slow.
     */
    public void removeTask(Task task) {
        TaskScheduler.cancelNotificationAlarm(getApplication(), task.getTaskId().hashCode());
        TaskRepository.removeTaskFromFirebase(task);

        scheduleFallback(() -> {
            Map<Task.TaskStatus, Tasks> current = _uiTaskLists.getValue();
            if (current != null && current.containsKey(task.getStatus())) {
                current.get(task.getStatus()).remove(task);
            }
        });
    }

    /**
     * Moves tasks to a new status. Only performs a local update if Firebase is slow.
     */
    public void moveTasks(List<Task> tasks, Task.TaskStatus newStatus) {
        for (Task t : tasks) {
            // We don't mutate the objects in the list yet to avoid "double update" redundancy.
            // We create a modified version for Firebase.
            Task update = t; 
            update.setStatus(newStatus);
            update.setTaskSelected(false);
            TaskRepository.updateTaskInFirebase(update);
        }

        scheduleFallback(() -> {
            Map<Task.TaskStatus, Tasks> current = _uiTaskLists.getValue();
            if (current == null) return;
            for (Task t : tasks) {
                // Find and move locally in the map
                for (Tasks list : current.values()) list.remove(t);
                if (current.containsKey(newStatus)) {
                    current.get(newStatus).add(t);
                    if (currentFilter != null) current.get(newStatus).filterTasks(currentFilter);
                }
            }
        });
    }

    public void removeTasks(List<Task> tasks) {
        for (Task task : tasks) removeTask(task);
    }

    public void updateTask(Task task) {
        if (task.getTaskId() == null) return;
        TaskRepository.updateTaskInFirebase(task);
        // Note: For simple edits, we usually wait for the listener or handle it locally if needed.
    }

    public MutableLiveData<Map<Task.TaskStatus, Tasks>> getLiveData() {
        return this._uiTaskLists;
    }

    public void filterTasks(String query) {
        this.currentFilter = query;
        Map<Task.TaskStatus, Tasks> current = _uiTaskLists.getValue();
        if (current != null) {
            for (Tasks t : current.values()) t.filterTasks(query);
            _uiTaskLists.setValue(current);
        }
    }

    public void unfilterTasks() {
        this.currentFilter = null;
        Map<Task.TaskStatus, Tasks> current = _uiTaskLists.getValue();
        if (current != null) {
            for (Tasks t : current.values()) t.unfilterTasks();
            _uiTaskLists.setValue(current);
        }
    }

    public Map<Task.TaskStatus, Tasks> getTasks() {
        return this._uiTaskLists.getValue();
    }

    public static void updateTaskCardBackgroundColor(@NonNull TaskListAdapter.TaskViewHolder holder, Task task) {
        int colorRes = task.hasReachedDeadline() ? R.color.bg_task_card_post_deadline :
                task.isDeadlineNear() ? R.color.bg_task_card_near_deadline :
                        R.color.bg_task_card;

        int baseColor = ContextCompat.getColor(holder.itemView.getContext(), colorRes);
        ((CardView) holder.itemView).setCardBackgroundColor(
                task.isTaskSelected() ? ColorUtils.blendARGB(baseColor, Color.LTGRAY, 0.3f) : baseColor
        );
    }
}
