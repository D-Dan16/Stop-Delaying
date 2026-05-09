package stop_delaying.ui.activities;

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

/**
 * The initial landing screen of the application. Provides options for the user 
 * to either sign in or register a new account.
 */
public class OpeningScreen extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_opening_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button bToSignIn = findViewById(R.id.bToSignIn);
        TextView tvToRegister = findViewById(R.id.tvToRegister);

        bToSignIn.setOnClickListener(v -> {
            startActivity(new Intent(this, Login.class));
        });

        tvToRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, Register.class));
        });


    }
}
