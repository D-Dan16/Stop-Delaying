package stop_delaying.utils.ai_recommendations;

/**
 * Data model representing the results of task analysis.
 * Contains insights and statistics about the analyzed tasks.
 */
public class AnalysisResult {
    private final String suggestedTasksToDo;
    private final int totalTasks;
    private final int todoCount;
    private final int nearDeadlineCount;
    private final int overdueCount;

    private AnalysisResult(String suggestedTasksToDo, Builder builder) {
        this.suggestedTasksToDo = suggestedTasksToDo;
        this.totalTasks = builder.totalTasks;
        this.todoCount = builder.todoCount;
        this.nearDeadlineCount = builder.nearDeadlineCount;
        this.overdueCount = builder.overdueCount;
    }

    public int getTotalTasks() {
        return totalTasks;
    }

    public int getTodoCount() {
        return todoCount;
    }

    public int getNearDeadlineCount() {
        return nearDeadlineCount;
    }

    public int getOverdueCount() {
        return overdueCount;
    }

    /**
     * Generates a human-readable summary of the analysis.
     */
    public String getSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("Total TO DO Tasks: ").append(totalTasks).append("\n\n");

        if (overdueCount > 0)
            summary.append("⚠️ Overdue: ").append(overdueCount).append("\n");
        if (nearDeadlineCount > 0)
            summary.append("⏰ Near Deadline (24h): ").append(nearDeadlineCount).append("\n");

        // Add insights
        summary.append("\n💡 Insights:\n");

        return summary.toString();
    }

    /**
     * Builder pattern for creating AnalysisResult instances.
     */
    public static class Builder {
        private int totalTasks;
        private int todoCount;
        private int nearDeadlineCount;
        private int overdueCount;

        public Builder setTotalTasks(int totalTasks) {
            this.totalTasks = totalTasks;
            return this;
        }

        public Builder setTodoCount(int todoCount) {
            this.todoCount = todoCount;
            return this;
        }

        public Builder setNearDeadlineCount(int nearDeadlineCount) {
            this.nearDeadlineCount = nearDeadlineCount;
            return this;
        }

        public Builder setOverdueCount(int overdueCount) {
            this.overdueCount = overdueCount;
            return this;
        }

        public AnalysisResult build() {
            return new AnalysisResult("", this);
        }
    }
}
