package stop_delaying.utils.ai_recommendations;

import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.firebase.ai.FirebaseAI;
import com.google.firebase.ai.java.ChatFutures;
import com.google.firebase.ai.java.GenerativeModelFutures;
import com.google.firebase.ai.type.Content;
import com.google.firebase.ai.type.GenerateContentResponse;
import com.google.firebase.ai.type.GenerationConfig;
import com.google.firebase.ai.type.GenerativeBackend;


import java.util.List;
import java.util.Collections;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import stop_delaying.models.Task;

public class TaskAnalyzer {
    private TaskAnalyzer() {}
    private static final Executor executor = Executors.newSingleThreadExecutor();

    private static final GenerativeModelFutures model = GenerativeModelFutures.from(FirebaseAI.getInstance(GenerativeBackend.googleAI()).generativeModel(
            "gemini-2.5-flash",
            new GenerationConfig.Builder()
                    .setTemperature(0.7f)
                    .build()
    ));

    private static final Content systemInstruction = new Content.Builder()
            .addText("""
                    You are a helpful assistant that helps a user with their tasks. The user has a list of tasks in their To Do list in an app for helping with their procrastination problems.\s
                    Provide the top 4 suggested tasks to do based on how important and urgent they are.\s
                    Take into account the action of the task itself and how much demanding it can be.\s
                    Consider the deadline of the task and how close it is.\s
                    Your answer MUST be in the following format:\s
                    1. [Task 1] - [5 to 10 words why you chose this task in particular]\s
                    2. [Task 2] - [5 to 10 words why you chose this task in particular]\s
                    3. [Task 3] - [5 to 10 words why you chose this task in particular]\s
                    4. [Task 4] - [5 to 10 words why you chose this task in particular]\s
                   \s
                    DO NOT include any other text or instructions.
                    IF the amount of the total tasks is lesser than 4, order them without filling the entire 4 slots.
                   \s
                    Answer based on the language of the tasks, and ONLY by that language.
               \s""")
            .build();


    public static void analyzeTasks(List<Task> tasks, AnalysisCallback callback) {
        if (tasks == null || tasks.isEmpty()) {
            callback.onError("No tasks to analyze");
            return;
        }
        if (tasks.size() == 1) {
            callback.onError("Not enough tasks to analyze");
            return;
        }


        Content userContent = new Content.Builder()
                .addText(buildPrompt(tasks))
                .build();

        // Start a chat session with the system instruction as the initial history.
        // A new chat is started each time to ensure the system instruction is always fresh for each analysis
        ChatFutures chat = model.startChat(Collections.singletonList(systemInstruction));

        Futures.addCallback(chat.sendMessage(userContent), new FutureCallback<>() {
            @Override public void onSuccess(GenerateContentResponse result) {
                String aiText = result.getText();

                int overdueCount = (int) tasks.stream().filter(Task::hasReachedDeadline).count();
                AnalysisResult analysisResult = new AnalysisResult(
                        aiText,
                        tasks.size(),
                        (int) tasks.stream().filter(Task::isDeadlineNear).count() - overdueCount,
                        overdueCount
                );

                new Handler(Looper.getMainLooper()).post(() ->
                        callback.onSuccess(analysisResult));
            }

            @Override public void onFailure(@NonNull Throwable t) {
                new Handler(Looper.getMainLooper()).post(() ->
                        callback.onError(t.getMessage()));
            }
        }, executor);
    }

    private static String buildPrompt(List<Task> tasks) {
        StringBuilder sb = new StringBuilder();
        for (Task task : tasks)
            sb.append("--> ")
                .append(task.getTitle())
                .append(", Due: ")
                .append(task.getDueDate())
                .append(task.getDueTimeOfDay())
                .append(", Status: ")
                .append(task.getStatus())
                .append("\n\n");

        return sb.toString();
    }

    public interface AnalysisCallback {
        void onSuccess(AnalysisResult result);
        void onError(String errorMessage);
    }
}