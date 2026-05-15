package stop_delaying.ui.fragments.tasks.task_handlers;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Objects;

import stop_delaying.models.Task;
import stop_delaying.utils.FBBranches;


/**
 * Data repository responsible for handling all task-related operations with Firebase. 
 * Provides abstraction over database calls for the ViewModel.
 */
public class TaskRepository {
    private static ValueEventListener activeListener = null;
    private static DatabaseReference activeListenerRef = null;

    /** Callback interface for receiving categorized task data. */
    public interface TaskFetchCallback {

        void onTasksFetched(Map<Task.TaskStatus, List<Task>> categorizedTasks);
        void onFetchFailed(String error);
    }

    /** Callback interface for generic task write operations. */
    public interface TaskOperationCallback {
        default void onSuccess() {}
        void onFailure(String error);
    }

    // --- Real-time Fetching from Firebase ---
    /**
     * Sets up a real-time listener for the current user's tasks in Firebase.
     * Categorizes tasks by status upon retrieval.
     */
    public static void observeUserTasks(@NonNull TaskFetchCallback callback) {
        FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
        if (fbUser == null) {
            callback.onFetchFailed("User not logged in.");
            return;
        }
        Log.w("TaskRepository", "Setting up real-time listener for user: " + fbUser.getUid());

        // Remove previous listener if exists
        removeTasksListener();

        activeListenerRef = FirebaseDatabase.getInstance()
                .getReference(FBBranches.TASKS)
                .child(fbUser.getUid());

        activeListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("TaskRepository", "Firebase onDataChange triggered - snapshot exists: " + snapshot.exists());
                Map<Task.TaskStatus, List<Task>> categorizedTasks = new HashMap<>();
                for (Task.TaskStatus status : Task.TaskStatus.values())
                    categorizedTasks.put(status, new ArrayList<>());

                for (DataSnapshot taskSnapshot : snapshot.getChildren()) {
                    Task task = taskSnapshot.getValue(Task.class);
                    if (task == null) continue;

                    // Set taskId from the Firebase key (since it's @Excluded from serialization)
                    task.setTaskId(taskSnapshot.getKey());

                    Objects.requireNonNull(categorizedTasks.get(task.getStatus())).add(task);
                }

                Log.d("TaskRepository", "Calling callback with " + categorizedTasks.values().stream().mapToInt(List::size).sum() + " total tasks");
                callback.onTasksFetched(categorizedTasks);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("TaskRepository", "Failed to fetch tasks", error.toException());
                callback.onFetchFailed(error.getMessage());
            }
        };

        activeListenerRef.addValueEventListener(activeListener);
    }

    /**
     * Removes any active real-time listeners for task data to prevent leaks.
     */
    public static void removeTasksListener() {
        if (activeListenerRef != null && activeListener != null) {
            activeListenerRef.removeEventListener(activeListener);
            activeListener = null;
            activeListenerRef = null;
            Log.d("TaskRepository", "Removed real-time task listener");
        }
    }

    /**
     * Saves a new task to the user's task list in Firebase.
     */
    public static void addTaskToFirebase(Task task, @NonNull TaskOperationCallback callback) {
        try {
            FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
            if (fbUser == null) {
                callback.onFailure("User not logged in.");
                return;
            }

            DatabaseReference tasksRef = FirebaseDatabase.getInstance().getReference(FBBranches.TASKS);

            tasksRef.child(fbUser.getUid())
                    .child(task.getTaskId())
                    .setValue(task)
                    .addOnSuccessListener(aVoid -> callback.onSuccess())
                    .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
        } catch (Exception e) {
            Log.e("TaskRepository", "Failed to add task", e);
        }
    }

    /**
     * Removes a specific task from the user's task list in Firebase.
     */
    public static void removeTaskFromFirebase(Task task, @NonNull TaskOperationCallback callback) {
        try {
            FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
            if (fbUser == null) {
                callback.onFailure("User not logged in.");
                return;
            }

            DatabaseReference tasksRef = FirebaseDatabase.getInstance().getReference(FBBranches.TASKS + "/" + fbUser.getUid());
            tasksRef.child(task.getTaskId()).removeValue()
                    .addOnSuccessListener(aVoid -> callback.onSuccess())
                    .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
        } catch (Exception e) {
            Log.e("TaskRepository", "Failed to remove task", e);
        }
    }

    /**
     * Updates an existing task's data in the Firebase database.
     */
    public static void updateTaskInFirebase(Task task, @NonNull TaskOperationCallback callback) {
        try {
            FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
            if (fbUser == null) {
                callback.onFailure("User not logged in.");
                return;
            }

            if (task.getTaskId() == null || task.getTaskId().isEmpty()) {
                callback.onFailure("Task ID is null or empty.");
                Log.e("TaskRepository", "Attempted to update task with null/empty taskId");
                return;
            }

            Log.d("TaskRepository", "Updating task: " + task.getTaskId() + " with status: " + task.getStatus());

            DatabaseReference tasksRef = FirebaseDatabase.getInstance().getReference(FBBranches.TASKS + "/" + fbUser.getUid());
            tasksRef.child(task.getTaskId()).setValue(task)
                    .addOnSuccessListener(aVoid -> {
                        Log.d("TaskRepository", "Task updated successfully: " + task.getTaskId());
                        callback.onSuccess();
                    })
                    .addOnFailureListener(e -> {
                        Log.e("TaskRepository", "Failed to update task in Firebase: " + e.getMessage());
                        callback.onFailure(e.getMessage());
                    });
        } catch (Exception e) {
            Log.e("TaskRepository", "Failed to update task", e);
            callback.onFailure(e.getMessage());
        }
    }

    /**
     * Deletes all tasks associated with a specific user ID from Firebase.
     */
    public static void removeUserTasksFromFirebase(String userUID, TaskOperationCallback taskOperationCallback) {
        DatabaseReference tasksRef = FirebaseDatabase.getInstance().getReference(FBBranches.TASKS + "/" + userUID);
        tasksRef.removeValue()
                .addOnSuccessListener(aVoid -> taskOperationCallback.onSuccess())
                .addOnFailureListener(e -> taskOperationCallback.onFailure(e.getMessage()));
    }
}
