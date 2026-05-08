package stop_delaying.utils;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Locale;

public final class Utils {
    private static TextToSpeech textToSpeech;
    public static void applyDimmingEffect(View rootViewToDimFrom, boolean shouldBeDimmed) {
        if (rootViewToDimFrom instanceof ViewGroup viewGroup)
            for (int i = 0; i < viewGroup.getChildCount(); i++)
                viewGroup.getChildAt(i).setAlpha(shouldBeDimmed ? 0.3f : 1.0f);
    }

    public static boolean isPasswordNotValid(String password) {
        return !password.matches(".*[A-Za-z].*") || !password.matches(".*\\d.*") || password.length() < 8;
    }

    /**
     * Utility method to speak a string.
     */
    public static void speak(android.content.Context context, String text) {
        if (textToSpeech == null)
            textToSpeech = new TextToSpeech(
                    context.getApplicationContext(), status -> {
                if (status == TextToSpeech.SUCCESS)
                    performSpeech(context, text);
                else
                    Toast.makeText(context, "TTS Initialization failed", Toast.LENGTH_SHORT).show();
            }
            );
        else
            performSpeech(context, text);
    }

    private static void performSpeech(android.content.Context context, String text) {
        if (textToSpeech == null || text == null)
            return;

        // Remove emojis and special symbols to prevent TTS from reading them
        String cleanedText = text.replaceAll("[\\p{So}\\p{Cn}]", "");

        boolean isHebrew = containsHebrew(cleanedText);

        Locale lang = isHebrew ? new Locale("he", "IL") : Locale.US;

        int result = textToSpeech.setLanguage(lang);

        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            Log.e("TTS", "Language is not supported or missing data: " + lang);
            Toast.makeText(context, "Hebrew voice data missing. Please install it in TTS settings.", Toast.LENGTH_LONG).show();

            // Optional: Prompt to install data
            Intent installIntent = new Intent();
            installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
            installIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            try {
                context.startActivity(installIntent);
            } catch (Exception e) {
                Log.e("TTS", "Could not open TTS install intent", e);
            }

            return;
        }

        textToSpeech.speak(cleanedText, TextToSpeech.QUEUE_FLUSH, null, "TaskTTS");
    }

    public static boolean containsHebrew(String text) {
        if (text == null) return false;
        for (char c : text.toCharArray())
            if (c >= '\u0590' && c <= '\u05FF')
                return true;
        return false;
    }

    public static void configSTTButton(Fragment fragment, View speechToTextComponent, EditText textInput) {
        speechToTextComponent.setOnClickListener(view -> {
            Context context = fragment.requireContext();
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                //noinspection deprecation
                fragment.requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 101);
                return;
            }

            if (!SpeechRecognizer.isRecognitionAvailable(context)) {
                Toast.makeText(context, "Speech recognition is not available", Toast.LENGTH_SHORT).show();
                return;
            }

            final SpeechRecognizer speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context);

            Log.d("STT_Utils", "Speech Recognition Started");

            // Capture original text to append new words correctly during partial updates
            final String originalText = textInput.getText().toString();

            // set up the listener for the listening device
            speechRecognizer.setRecognitionListener(new RecognitionListener() {
                @Override public void onReadyForSpeech(Bundle bundle) {}
                @Override public void onBeginningOfSpeech() {}
                @Override public void onRmsChanged(float v) {}
                @Override public void onBufferReceived(byte[] bytes) {}
                @Override public void onEndOfSpeech() {}
                @Override public void onEvent(int i, Bundle bundle) {}

                @Override public void onError(int i) {
                    speechRecognizer.destroy();
                }

                @SuppressLint("SetTextI18n") @Override public void onResults(Bundle bundle) {
                    ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                    if (data != null && !data.isEmpty())
                        textInput.setText(originalText + data.get(0));

                    textInput.append(". \n");

                    Log.d("STT_Utils", "The Speech to Text: " + textInput.getText().toString());
                    speechRecognizer.destroy();
                }

                @SuppressLint("SetTextI18n") @Override public void onPartialResults(Bundle bundle) {
                    ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                    if (data != null && !data.isEmpty()) {
                        textInput.setText(originalText + data.get(0));

                        Log.d("STT_Utils", "onPartialResults: " + data.get(0));
                    }
                }
            });

            var recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");
            recognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);

            // Wait longer after the user stops speaking (approx 2-3 seconds of silence)
            recognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 3000L);
            recognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 2000L);

            speechRecognizer.startListening(recognizerIntent);
        });
    }
}
