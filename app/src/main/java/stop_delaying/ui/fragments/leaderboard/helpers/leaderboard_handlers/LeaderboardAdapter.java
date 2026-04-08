package stop_delaying.ui.fragments.leaderboard.helpers.leaderboard_handlers;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.procrastination.R;

import java.util.List;

import stop_delaying.models.LeaderboardEntry;

@SuppressLint("NotifyDataSetChanged")
public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.LeaderboardEntryViewHolder> {
    private final List<LeaderboardEntry> leaderboardEntries;

    public LeaderboardAdapter(List<LeaderboardEntry> leaderboardEntries) {
        this.leaderboardEntries = leaderboardEntries;
    }

    @NonNull @Override public LeaderboardEntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.leaderboard_item, parent, false);
        return new LeaderboardEntryViewHolder(view);
    }

    @Override public void onBindViewHolder(@NonNull LeaderboardEntryViewHolder holder, int position) {
        LeaderboardEntry entry = leaderboardEntries.get(position);

        holder.tvRank.setText(position + 1 + ".");
        holder.tvUserName.setText(entry.getUserName());
        holder.tvDayStreak.setText(String.valueOf(entry.getDayStreak()));
        holder.tvTaskStreak.setText(String.valueOf(entry.getTaskStreak()));
    }

    public static class LeaderboardEntryViewHolder extends RecyclerView.ViewHolder {

        TextView tvRank;
        TextView tvUserName;
        TextView tvDayStreak;
        TextView tvTaskStreak;

        public LeaderboardEntryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRank = itemView.findViewById(R.id.tv_rank);
            tvUserName = itemView.findViewById(R.id.tv_rank_name);
            tvDayStreak = itemView.findViewById(R.id.tv_rank_day_streak);
            tvTaskStreak = itemView.findViewById(R.id.tv_rank_task_streak);
        }
    }

    public void setLeaderboardEntries(@Nullable List<LeaderboardEntry> newEntries) {
        if (newEntries == null) return;

        this.leaderboardEntries.clear();
        this.leaderboardEntries.addAll(newEntries);
        notifyDataSetChanged();
    }

    @Override public int getItemCount() {
        return leaderboardEntries.size();
    }
}
