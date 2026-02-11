package stop_delaying.utils.ai_recommendations;

import android.os.Handler;
import android.os.Looper;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.ai.FirebaseAI;
import com.google.firebase.ai.GenerativeModel;
import com.google.firebase.ai.java.GenerativeModelFutures;
import com.google.firebase.ai.type.Content;
import com.google.firebase.ai.type.GenerateContentResponse;
import com.google.firebase.ai.type.GenerationConfig;
import com.google.firebase.ai.type.GenerativeBackend;


import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import stop_delaying.models.Task;

public class TaskAnalyzer {

    private final Executor executor = Executors.newSingleThreadExecutor();

    public void analyzeTasks(List<Task> tasks, AnalysisCallback callback) {

        if (tasks == null || tasks.isEmpty()) {
            callback.onError("No tasks to analyze");
            return;
        }


        GenerationConfig config = new GenerationConfig.Builder()
                .setTemperature(0.7f)
                .build();

        FirebaseAI ai = FirebaseAI.getInstance(GenerativeBackend.googleAI());
        GenerativeModel gm = ai.generativeModel(
                "gemini-1.5-flash",
                config
        );

        GenerativeModelFutures model = GenerativeModelFutures.from(gm);


        String prompt = buildPrompt(tasks);
        Content content = new Content.Builder()
                .addText(prompt)
                .build();

        ListenableFuture<GenerateContentResponse> response =
                model.generateContent(content);

        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {

                String aiText = result.getText();

                AnalysisResult analysisResult = new AnalysisResult.Builder()
                        .setTotalTasks(tasks.size())
                        .build();

                new Handler(Looper.getMainLooper()).post(() ->
                        callback.onSuccess(analysisResult));
            }

            @Override
            public void onFailure(Throwable t) {
                new Handler(Looper.getMainLooper()).post(() ->
                        callback.onError(t.getMessage()));
            }
        }, executor);
    }

    private String buildPrompt(List<Task> tasks) {

        StringBuilder sb = new StringBuilder();
        sb.append("Analyze the following tasks and provide productivity insights:\n\n");

        for (Task task : tasks) {
            sb.append("- ")
                    .append(task.getTitle())
                    .append(", Due: ")
                    .append(task.getDueDate())
                    .append(", Status: ")
                    .append(task.getStatus())
                    .append("\n");
        }

        sb.append("\nProvide:\n");
        sb.append("1. Productivity analysis\n");
        sb.append("2. Risk assessment\n");
        sb.append("3. Action recommendations\n");

        return sb.toString();
    }

    public interface AnalysisCallback {
        void onSuccess(AnalysisResult result);
        void onError(String errorMessage);
    }
}
