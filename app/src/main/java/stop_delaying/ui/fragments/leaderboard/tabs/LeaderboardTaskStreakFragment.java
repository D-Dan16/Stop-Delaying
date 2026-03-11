package stop_delaying.ui.fragments.leaderboard.tabs;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.procrastination.R;

import java.util.ArrayList;

import stop_delaying.ui.fragments.leaderboard.leaderboard_handlers.LeaderboardAdapter;

public class LeaderboardTaskStreakFragment extends Fragment {
    private static final LeaderboardAdapter adapter = new LeaderboardAdapter(new ArrayList<>());

    public static LeaderboardAdapter getAdapter() {
        return adapter;
    }
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_leaderboard_task_streak, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.rv_task_streak);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recyclerView.setAdapter(adapter);
    }

}