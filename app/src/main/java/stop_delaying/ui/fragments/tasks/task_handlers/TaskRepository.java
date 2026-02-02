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
 * Acts as the single source of truth for task data, providing abstraction over database calls.
 */
class TaskRepository {
    public interface TaskFetchCallback {
        void onTasksFetched(Map<Task.TaskStatus, List<Task>> categorizedTasks);
        void onFetchFailed(String error);
    }

    // --- Fetching from Firebase ---
    public static void fetchUserTasks(@NonNull TaskFetchCallback callback) {
        FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
        if (fbUser == null) {
            callback.onFetchFailed("User not logged in.");
            return;
        }

        DatabaseReference tasksRef =
                FirebaseDatabase.getInstance().getReference(FBBranches.TASKS + "/" + fbUser.getUid());

        tasksRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<Task.TaskStatus, List<Task>> categorizedTasks = new HashMap<>();
                for (Task.TaskStatus status : Task.TaskStatus.values())
                    categorizedTasks.put(status, new ArrayList<>());

                for (DataSnapshot taskSnapshot : snapshot.getChildren()) {
                    Task task = taskSnapshot.getValue(Task.class);
                    if (task == null) continue;

                    Objects.requireNonNull(categorizedTasks.get(task.getStatus())).add(task);
                }

                callback.onTasksFetched(categorizedTasks);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("TaskRepository", "Failed to fetch tasks", error.toException());
                callback.onFetchFailed(error.getMessage());
            }
        });
    }

    public static void addTaskToFirebase(Task task) {
        FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
        if (fbUser == null) return;

        DatabaseReference tasksRef = FirebaseDatabase.getInstance().getReference(FBBranches.TASKS + "/" + fbUser.getUid());
        tasksRef.child(task.getTaskId()).setValue(task);
    }

    public static void removeTaskFromFirebase(Task task) {
        FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
        if (fbUser == null) return;

        DatabaseReference tasksRef = FirebaseDatabase.getInstance().getReference(FBBranches.TASKS + "/" + fbUser.getUid());
        tasksRef.child(task.getTaskId()).removeValue();
    }

    public static void updateTaskInFirebase(Task task) {
        FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
        if (fbUser == null) return;

        DatabaseReference tasksRef = FirebaseDatabase.getInstance().getReference(FBBranches.TASKS + "/" + fbUser.getUid());
        tasksRef.child(task.getTaskId()).setValue(task);
    }
}
