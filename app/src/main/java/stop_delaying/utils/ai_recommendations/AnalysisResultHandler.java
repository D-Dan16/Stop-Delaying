package stop_delaying.utils.ai_recommendations;

public class AnalysisResultHandler {
    /**
     * Generates a human-readable summary of the analysis.
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
