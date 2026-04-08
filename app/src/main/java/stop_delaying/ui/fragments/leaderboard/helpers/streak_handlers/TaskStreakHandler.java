package stop_delaying.ui.fragments.leaderboard.helpers.streak_handlers;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import lombok.experimental.UtilityClass;
import stop_delaying.models.Task;
import stop_delaying.ui.fragments.leaderboard.helpers.leaderboard_handlers.UsersRepository;

@UtilityClass
public class TaskStreakHandler {
    public void inspectTasks(List<Task> selected) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null)
            return;

        String uid = currentUser.getUid();

        // Determine aggregate effect: if ANY task missed deadline -> reset streak to 0.
        // Otherwise, increment by the number of on-time tasks in a single atomic op.
        boolean anyLate = false;
        int onTimeCount = 0;
        for (Task task : selected)
            if (task.hasReachedDeadline())
                anyLate = true;
            else
                onTimeCount++;

        if (anyLate)
            UsersRepository.updateUserTaskStreak(uid, 0);
        else if (onTimeCount > 0)
            UsersRepository.incrementUserTaskStreakBy(uid, onTimeCount);
    }
}