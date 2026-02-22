package stop_delaying.utils.ai_recommendations;

/**
 * Data model representing the results of task analysis.
 * Contains insights and statistics about the analyzed tasks.
 */
public record AnalysisResult(
        String suggestedTasksToDo,
        int totalTasks,
        int nearDeadlineCount,
        int overdueCount
) {
}