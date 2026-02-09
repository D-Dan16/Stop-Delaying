package stop_delaying.utils.ai_recommendations;

import java.util.List;

import stop_delaying.models.Task;

/**
 * Analyzes tasks using AI to provide insights and recommendations.
 * Encapsulates all AI-related logic for task analysis.
 */
public class TaskAnalyzer {
    private static final AnalysisResult.Builder builder = new AnalysisResult.Builder();

    /**
     * Analyzes a list of tasks and provides insights.
     *
     * @param tasks    List of tasks to analyze
     * @param callback Callback to handle the analysis result
     */
    public void analyzeTasks(List<Task> tasks, AnalysisCallback callback) {
        if (tasks == null || tasks.isEmpty()) {
            callback.onError("No tasks to analyze");
            return;
        }

        // TODO: Implement AI analysis logic
        // For now, provide a basic analysis structure
        AnalysisResult result = performBasicAnalysis(tasks);
        callback.onSuccess(result);
    }

    /**
     * Performs basic statistical analysis of tasks.
     * This is a placeholder that can be expanded with actual AI logic.
     */
    private AnalysisResult performBasicAnalysis(List<Task> tasks) {
        int todoCount = 0;
        int nearDeadlineCount = 0;
        int overdueCount = 0;

        for (Task task : tasks) {
            todoCount++;
            if (task.isDeadlineNear()) nearDeadlineCount++;
            if (task.hasReachedDeadline()) overdueCount++;
        }

        return builder
                .setTotalTasks(tasks.size())
                .setTodoCount(todoCount)
                .setNearDeadlineCount(nearDeadlineCount)
                .setOverdueCount(overdueCount)
                .build();
    }

    /**
     * Callback interface for handling analysis results.
     */
    public interface AnalysisCallback {
        void onSuccess(AnalysisResult result);
        void onError(String errorMessage);
    }
}
