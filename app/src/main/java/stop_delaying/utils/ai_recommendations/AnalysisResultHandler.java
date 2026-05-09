package stop_delaying.utils.ai_recommendations;

/**
 * Provides formatting logic for AnalysisResult objects. Converts raw analysis data into 
 * user-friendly summaries for display in the UI.
 */
public class AnalysisResultHandler {
    /**
     * Generates a formatted human-readable summary of the task analysis results, 
     * including counts for overdue and near-deadline tasks.
     * @param result The analysis data to summarize.
     * @return A string containing the formatted summary.
     */
    public static String getSummary(AnalysisResult result) {
        StringBuilder summary = new StringBuilder();
        summary.append("📜 To Do: ").append(result.totalTasks()).append("\n");
        if (result.overdueCount() > 0)
            summary.append("⚠️ Overdue: ").append(result.overdueCount()).append("\n");
        if (result.nearDeadlineCount() > 0)
            summary.append("⏰ Near Deadline (24h): ").append(result.nearDeadlineCount()).append("\n\n");

        summary.append("💡 Priorities:\n").append(result.suggestedTasksToDo());

        return summary.toString();
    }
}
