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

/**
 * RecyclerView adapter for displaying leaderboard entries. Maps user rank, name, 
 * and streak data to individual list items.
 */
@SuppressLint("NotifyDataSetChanged")
public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.LeaderboardEntryViewHolder> {
    /** The list of leaderboard entries to be displayed. */
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

    /**
     * ViewHolder class for leaderboard items, holding references to the UI components 
     * for rank, username, and streak values.
     */
    public static class LeaderboardEntryViewHolder extends RecyclerView.ViewHolder {

        final TextView tvRank;
        final TextView tvUserName;
        final TextView tvDayStreak;
        final TextView tvTaskStreak;

        LeaderboardEntryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRank = itemView.findViewById(R.id.tv_rank);
            tvUserName = itemView.findViewById(R.id.tv_rank_name);
            tvDayStreak = itemView.findViewById(R.id.tv_rank_day_streak);
            tvTaskStreak = itemView.findViewById(R.id.tv_rank_task_streak);
        }
    }

    /**
     * Updates the current list of leaderboard entries and refreshes the UI.
     * @param newEntries The new list of entries to display.
     */
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
