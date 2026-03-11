package stop_delaying.ui.fragments.leaderboard.leaderboard_handlers;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import stop_delaying.models.User;
import stop_delaying.utils.FBBranches;

public class UsersRepository {
    private static ValueEventListener activeListener = null;
    private static DatabaseReference activeListenerRef = null;
    private static UsersFetchCallback currentUsersFetchCallback; //field to hold the current callback

    public static void fetchUsers(@NonNull UsersFetchCallback callback) {
        currentUsersFetchCallback = callback; // Always update to the latest callback

        if (activeListener == null) {
            activeListenerRef = FirebaseDatabase.getInstance().getReference(FBBranches.USERS);

            activeListener = new ValueEventListener() {
                @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                    List<User> entries = extractUsersFromSnapshot(snapshot);
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
                    List<User> entries = extractUsersFromSnapshot(snapshot);
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

    public static void removeUsersListener() {
        if (activeListenerRef != null && activeListener != null) {
            activeListenerRef.removeEventListener(activeListener);
            activeListener = null;
            activeListenerRef = null;
            currentUsersFetchCallback = null; // Clear the callback when the listener is removed
            Log.d("UsersRepository", "Removed real-time users listener");
        }
    }

    private static List<User> extractUsersFromSnapshot(@NonNull DataSnapshot snapshot) {
        List<User> entries = new ArrayList<>();
        for (DataSnapshot entrySnapshot : snapshot.getChildren()) {
            String name = entrySnapshot.child(FBBranches.Users.USER_NAME).getValue().toString();
            int dayStreak = ((Long) entrySnapshot.child(FBBranches.Users.DAY_STREAK).getValue()).intValue();
            int taskStreak = ((Long) entrySnapshot.child(FBBranches.Users.TASK_STREAK).getValue()).intValue();

            entries.add(new User(name, dayStreak, taskStreak));
        }
        return entries;
    }
}
