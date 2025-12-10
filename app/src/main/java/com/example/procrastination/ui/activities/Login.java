package com.example.procrastination.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.procrastination.R;

public class Login extends AppCompatActivity {

    Button bToSignIn;
    TextView tvToRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        bToSignIn = findViewById(R.id.bToSignIn);
        tvToRegister = findViewById(R.id.tvToRegister);

        bToSignIn.setOnClickListener(v -> {
            startActivity(new Intent(this, MainApp.class));
        });

        tvToRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, Register.class));
        });
    }
}
