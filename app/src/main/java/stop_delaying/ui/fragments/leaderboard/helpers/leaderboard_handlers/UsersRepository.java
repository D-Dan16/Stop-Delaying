package stop_delaying.ui.fragments.leaderboard.helpers.leaderboard_handlers;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import stop_delaying.models.User;
import stop_delaying.utils.FBBranches;

public class UsersRepository {
    private static ValueEventListener activeListener = null;
    private static DatabaseReference activeListenerRef = null;
    private static UsersFetchCallback currentUsersFetchCallback; //field to hold the current callback

    public interface UserFetchCallback {

        void onUserFetched(User user);

        void onFetchFailed(String errorMessage);
    }
    
    public interface UsersFetchCallback {
        void onUsersFetched(List<UserWithId> users);
        void onFetchFailed(String errorMessage);
    }
    
    // Helper class to hold both user data and their ID
    public static class UserWithId {
        public String userId;
        public User user;
        
        public UserWithId(String userId, User user) {
            this.userId = userId;
            this.user = user;
        }
    }
    public static void fetchUserById(String userId, @NonNull UserFetchCallback callback) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference(FBBranches.USERS).child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child(FBBranches.Users.USER_NAME).getValue(String.class);
                    int dayStreak = snapshot.child(FBBranches.Users.DAY_STREAK).getValue(Integer.class);
                    int taskStreak = snapshot.child(FBBranches.Users.TASK_STREAK).getValue(Integer.class);
                    boolean hasCompletedTodayATask = snapshot.child(FBBranches.Users.HAS_COMPLETED_TODAY_A_TASK).getValue(Boolean.class);
                    callback.onUserFetched(new User(name, dayStreak, taskStreak, hasCompletedTodayATask));
                } else
                    callback.onFetchFailed("User not found");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("UsersRepository", "Failed to fetch user by ID", error.toException());
                callback.onFetchFailed(error.getMessage());
            }
        });
    }
    public static void fetchUsers(@NonNull UsersFetchCallback callback) {
        currentUsersFetchCallback = callback; // Always update to the latest callback

        if (activeListener == null) {
            activeListenerRef = FirebaseDatabase.getInstance().getReference(FBBranches.USERS);

            activeListener = new ValueEventListener() {
                @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                    List<UserWithId> entries = extractUsersFromSnapshot(snapshot);
                    // Check if callback is still active
                    if (currentUsersFetchCallback != null)
                        currentUsersFetchCallback.onUsersFetched(entries);
                }

                @Override public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("UsersRepository", "Failed to fetch users", error.toException());
                    // Check if callback is still active
                    if (currentUsersFetchCallback != null)
                        currentUsersFetchCallback.onFetchFailed(error.getMessage());
                }
            };

            activeListenerRef.addValueEventListener(activeListener);
        } else
            activeListenerRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                    List<UserWithId> entries = extractUsersFromSnapshot(snapshot);
                    if (currentUsersFetchCallback != null)
                        currentUsersFetchCallback.onUsersFetched(entries);
                }

                @Override public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("UsersRepository", "Failed to fetch users (single event)", error.toException());
                    if (currentUsersFetchCallback != null)
                        currentUsersFetchCallback.onFetchFailed(error.getMessage());
                }
            });
    }

    public static void updateUserDayStreak(String userId, int newDayStreak) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference(FBBranches.USERS).child(userId);
        Map<String, Object> updates = new HashMap<>();
        updates.put(FBBranches.Users.DAY_STREAK, newDayStreak);
        userRef.updateChildren(updates)
                .addOnSuccessListener(aVoid -> Log.d("UsersRepository", "Day streak updated successfully for user " + userId))
                .addOnFailureListener(e -> Log.e("UsersRepository", "Failed to update day streak for user " + userId, e));
    }

    public static void updateUserTaskStreak(String uid, int newTaskStreak) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference(FBBranches.USERS).child(uid);
        Map<String, Object> updates = new HashMap<>();
        updates.put(FBBranches.Users.TASK_STREAK, newTaskStreak);
        userRef.updateChildren(updates)
                .addOnSuccessListener(aVoid -> Log.d("UsersRepository", "Task streak updated successfully for user " + newTaskStreak))
                .addOnFailureListener(e -> Log.e("UsersRepository", "Failed to update task streak for user " + newTaskStreak, e));
    }

    public static void incrementUserTaskStreak(String userId) {
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference(FBBranches.USERS)
                .child(userId)
                .child(FBBranches.Users.TASK_STREAK);
        // Atomic increment on the server to avoid race conditions when called rapidly
        reference.setValue(ServerValue.increment(1));
    }

    public static void updateUserHasCompletedTodayATask(String userId, boolean hasCompletedTodayATask) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference(FBBranches.USERS).child(userId);
        Map<String, Object> updates = new HashMap<>();
        updates.put(FBBranches.Users.HAS_COMPLETED_TODAY_A_TASK, hasCompletedTodayATask);
        userRef.updateChildren(updates)
                .addOnSuccessListener(aVoid -> Log.d("UsersRepository", "hasCompletedTodayATask updated successfully for user " + userId))
                .addOnFailureListener(e -> Log.e("UsersRepository", "Failed to update hasCompletedTodayATask for user " + userId, e));
    }

    public static void incrementUserTaskStreakBy(String userId, int amount) {
        if (amount == 0) return;
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference(FBBranches.USERS)
                .child(userId)
                .child(FBBranches.Users.TASK_STREAK);
        reference.setValue(ServerValue.increment(amount));
    }

    public static void removeUsersListener() {
        if (activeListenerRef == null || activeListener == null)
            return;

        activeListenerRef.removeEventListener(activeListener);
        activeListener = null;
        activeListenerRef = null;
        currentUsersFetchCallback = null; // Clear the callback when the listener is removed
        Log.d("UsersRepository", "Removed real-time users listener");
    }

    private static List<UserWithId> extractUsersFromSnapshot(@NonNull DataSnapshot snapshot) {
        List<UserWithId> entries = new ArrayList<>();
        for (DataSnapshot entrySnapshot : snapshot.getChildren()) {
            String userId = entrySnapshot.getKey();
            String name = entrySnapshot.child(FBBranches.Users.USER_NAME).getValue().toString();
            int dayStreak = ((Long) entrySnapshot.child(FBBranches.Users.DAY_STREAK).getValue()).intValue();
            int taskStreak = ((Long) entrySnapshot.child(FBBranches.Users.TASK_STREAK).getValue()).intValue();
            boolean hasCompletedTodayATask = entrySnapshot.child(FBBranches.Users.HAS_COMPLETED_TODAY_A_TASK).getValue(Boolean.class);

            User user = new User(name, dayStreak, taskStreak, hasCompletedTodayATask);
            entries.add(new UserWithId(userId, user));
        }
        return entries;
    }
}