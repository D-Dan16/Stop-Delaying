package stop_delaying.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.procrastination.R;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import stop_delaying.utils.FBBranches;
import stop_delaying.models.User;

public class Login extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference usersDBRef;

    private EditText etEmailLogin;
    private TextInputEditText etPasswordLogin;
    private TextInputLayout tilPasswordLogin;
    private TextInputLayout tilEmailLogin;
    private Button bToSignIn;
    private TextView tvToRegister;

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

        mAuth = FirebaseAuth.getInstance();
        usersDBRef = FirebaseDatabase.getInstance().getReference(FBBranches.USERS);

        etEmailLogin = findViewById(R.id.etEmailLogin);
        etPasswordLogin = findViewById(R.id.etPasswordLogin);
        tilPasswordLogin = findViewById(R.id.tilPasswordLogin);
        tilEmailLogin = findViewById(R.id.tilEmailLogin);
        bToSignIn = findViewById(R.id.bToSignIn);
        tvToRegister = findViewById(R.id.tvToRegister);

        signInButtonLogic();

        tvToRegister.setOnClickListener(v -> startActivity(new Intent(this, Register.class)));
    }

    private void signInButtonLogic() {
        bToSignIn.setOnClickListener(v -> {
            String email = etEmailLogin.getText().toString().trim();
            String password = Objects.requireNonNull(etPasswordLogin.getText()).toString();

            tilEmailLogin.setError(null);
            tilPasswordLogin.setError(null);

            if (email.isEmpty()) {
                tilEmailLogin.setError("Email is required.");
                return;
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                tilEmailLogin.setError("Invalid email format.");
                return;
            }
            if (password.isEmpty()) {
                tilPasswordLogin.setError("Password is required.");
                return;
            }

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            executeSignIn();
                        } else {
                            logReasonForUnsuccessfulSignIn(task);
                        }
                    });
        });
    }

    private void executeSignIn() {
        FirebaseUser fbUser = mAuth.getCurrentUser();
        if (fbUser == null)
            return;

        String uid = fbUser.getUid();
        usersDBRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User currentUser = dataSnapshot.getValue(User.class);
                if (currentUser != null) {
                    // You can add a singleton or another way to manage the user session here
                    Toast.makeText(Login.this, "Sign in successful.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Login.this, MainApp.class));
                    finish();
                } else {
                    Toast.makeText(Login.this, "Failed to retrieve user information.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Login.this, "Failed to retrieve user information.", Toast.LENGTH_SHORT).show();
                Log.e("Login", "Database error: " + databaseError.getMessage());
            }
        });
    }

    private void logReasonForUnsuccessfulSignIn(Task<AuthResult> task) {
        Exception exception = task.getException();

        if (exception instanceof FirebaseAuthInvalidUserException || exception instanceof FirebaseAuthInvalidCredentialsException) {
            tilEmailLogin.setError("Invalid email or password");
            tilPasswordLogin.setError("Invalid email or password");
        } else {
            Toast.makeText(Login.this, "Authentication failed.", Toast.LENGTH_LONG).show();
        }
    }
}
