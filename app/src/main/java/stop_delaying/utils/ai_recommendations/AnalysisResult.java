package stop_delaying.utils.ai_recommendations;

import androidx.annotation.NonNull;

import java.util.Objects;

/**
 * Data model representing the results of task analysis.
 * Contains insights and statistics about the analyzed tasks.
 */
@SuppressWarnings("ClassCanBeRecord")
public final class AnalysisResult {
    private final String suggestedTasksToDo;
    private final int totalTasks;
    private final int nearDeadlineCount;
    private final int overdueCount;

    public AnalysisResult(
            String suggestedTasksToDo,
            int totalTasks,
            int nearDeadlineCount,
            int overdueCount
    ) {
        this.suggestedTasksToDo = suggestedTasksToDo;
        this.totalTasks = totalTasks;
        this.nearDeadlineCount = nearDeadlineCount;
        this.overdueCount = overdueCount;
    }

    public String suggestedTasksToDo() {
        return suggestedTasksToDo;
    }

    public int totalTasks() {
        return totalTasks;
    }

    public int nearDeadlineCount() {
        return nearDeadlineCount;
    }

    public int overdueCount() {
        return overdueCount;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (AnalysisResult) obj;
        return Objects.equals(this.suggestedTasksToDo, that.suggestedTasksToDo) &&
                this.totalTasks == that.totalTasks &&
                this.nearDeadlineCount == that.nearDeadlineCount &&
                this.overdueCount == that.overdueCount;
    }

    @Override
    public int hashCode() {
        return Objects.hash(suggestedTasksToDo, totalTasks, nearDeadlineCount, overdueCount);
    }

    @NonNull @Override
    public String toString() {
        return "AnalysisResult[" +
                "suggestedTasksToDo=" + suggestedTasksToDo + ", " +
                "totalTasks=" + totalTasks + ", " +
                "nearDeadlineCount=" + nearDeadlineCount + ", " +
                "overdueCount=" + overdueCount + ']';
    }
}
