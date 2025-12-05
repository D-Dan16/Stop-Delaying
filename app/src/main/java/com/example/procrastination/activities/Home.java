package com.example.procrastination.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.appcompat.app.AppCompatActivity;

import com.example.procrastination.R;

public class Home extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        TextView tvImportantTask = findViewById(R.id.tvImportantTask);
        Button btnWhatIsProcrastination = findViewById(R.id.btnWhatIsProcrastination);
        Button btnTipsAndTricks = findViewById(R.id.btnTipsAndTricks);
        ViewFlipper vfImageScroller = findViewById(R.id.vfImageScroller);

        // TODO: Get the most important task and set it to the TextView

        vfImageScroller.setFlipInterval(3000);
        vfImageScroller.startFlipping();

        btnWhatIsProcrastination.setOnClickListener(v -> {
            // TODO: Start the WhatIsProcrastinationActivity
        });

        btnTipsAndTricks.setOnClickListener(v -> {
            // TODO: Start the TipsAndTricksActivity
        });
    }
}