package stop_delaying.ui.fragments.tasks.task_handlers;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

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
    // LiveData to hold the categorized tasks for the UI
    private final MutableLiveData<Map<Task.TaskStatus, List<Task>>> _categorizedTasks = new MutableLiveData<>();
    // LiveData for holding filtered tasks (if filtering is applied)
    private final MutableLiveData<Map<Task.TaskStatus, TaskLists>> _uiTaskLists = new MutableLiveData<>();

    // Being called by the ViewModelProvider
    public TasksViewModel() {
        loadTasks();
    }

    public void loadTasks() {
        TaskRepository.fetchUserTasks(new TaskRepository.TaskFetchCallback() {
            @Override public void onTasksFetched(Map<Task.TaskStatus, List<Task>> fetchedCategorizedTasks) {
                _categorizedTasks.setValue(fetchedCategorizedTasks);

                // Initialize UI TaskLists based on fetched data
                Map<Task.TaskStatus, TaskLists> newUiTaskLists = new HashMap<>();
                for (Map.Entry<Task.TaskStatus, List<Task>> entry : fetchedCategorizedTasks.entrySet())
                    newUiTaskLists.put(entry.getKey(), new TaskLists(new ArrayList<>(entry.getValue()), new ArrayList<>()));

                _uiTaskLists.setValue(newUiTaskLists);
            }

            @Override public void onFetchFailed(String error) {
                // Handle error, maybe update a LiveData for error messages
                // For now, just log it
                System.err.println("Failed to load tasks: " + error);
            }
        });
    }

    public void addTask(Task task) {
        TaskRepository.addTaskToFirebase(task, new TaskRepository.TaskOperationCallback() {
            @Override
            public void onSuccess() {
                loadTasks();
            }

            @Override
            public void onFailure(String error) {
                System.err.println("Failed to add task: " + error);
            }
        });
    }

    public void addAllTasks(List<Task> task) {
        for (Task t : task)
            TaskRepository.addTaskToFirebase(t, new TaskRepository.TaskOperationCallback() {
                @Override
                public void onSuccess() {
                    // Success for individual task
                }

                @Override
                public void onFailure(String error) {
                    System.err.println("Failed to add task: " + error);
                }
            });

        loadTasks();
    }


    public void removeTask(Task task) {
        TaskRepository.removeTaskFromFirebase(task, new TaskRepository.TaskOperationCallback() {
            @Override
            public void onSuccess() {
                loadTasks();
            }

            @Override
            public void onFailure(String error) {
                System.err.println("Failed to remove task: " + error);
            }
        });
    }

    public void updateTask(Task task) {
        TaskRepository.updateTaskInFirebase(task, new TaskRepository.TaskOperationCallback() {
            @Override
            public void onSuccess() {
                loadTasks();
            }

            @Override
            public void onFailure(String error) {
                System.err.println("Failed to update task: " + error);
            }
        });
    }


    /**
     * Represents a collection of tasks, divided into visible and hidden tasks.
     * Each instance of this class holds a separate list, and the separation is because of the status of the tasks (ie. To Do, Completed, Canceled).
     *
     * @param visibleTasks The list of tasks currently visible.
     * @param hiddenTasks The list of tasks currently hidden.
     */
    public record TaskLists(ArrayList<Task> visibleTasks, ArrayList<Task> hiddenTasks) {
        public List<Task> getAllVisibleTasks() {
                return visibleTasks;
        }

        public List<Task> getAllHiddenTasks() {
            return hiddenTasks;
        }

        public void setAllTasks(List<Task> tasks) {
            visibleTasks.clear();
            visibleTasks.addAll(tasks);
            hiddenTasks.clear();
        }

        public int getSelectedCount() {
            int count = 0;
            for (Task t : visibleTasks)
                if (t.isTaskSelected())
                    count++;
            return count;
        }

        public List<Task> getSelectedTasks() {
            List<Task> selected = new ArrayList<>();
            for (Task t : visibleTasks)
                if (t.isTaskSelected())
                    selected.add(t);
            return selected;
        }

        public void clearSelection() {
            for (Task t : visibleTasks)
                t.setTaskSelected(false);
        }

        public void removeSelectedTasks() {
            visibleTasks.removeIf(Task::isTaskSelected);
            hiddenTasks.removeIf(Task::isTaskSelected);
        }

        public void filterTasks(String query) {
            if (query == null || query.isEmpty())
                return;

            hiddenTasks.clear();

            for (Task t : visibleTasks) {
                String q = query.toLowerCase().trim();
                if (!t.getTitle().toLowerCase().contains(q) && !t.getDescription().toLowerCase().contains(q))
                    hiddenTasks.add(t);
            }

            visibleTasks.removeAll(hiddenTasks);
        }

        public void unfilterTasks() {
            visibleTasks.addAll(hiddenTasks);
            hiddenTasks.clear();
        }

        public void add(Task task) {
            visibleTasks.add(task);
        }
    }
}
