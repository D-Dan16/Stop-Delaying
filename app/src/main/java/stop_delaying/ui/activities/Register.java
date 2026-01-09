package stop_delaying.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.procrastination.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

import stop_delaying.FBBranches;
import stop_delaying.models.User;
import stop_delaying.utils.Utils;

public class Register extends AppCompatActivity {

    private FirebaseAuth mAuth;

    Button bRegisterSignUp;
    TextView tvToLogin;
    EditText etEmailRegister;
    EditText etUsernameRegister;
    TextInputEditText etPasswordRegister;
    TextInputEditText etConfirmPasswordRegister;
    TextInputLayout tilPasswordRegister;
    TextInputLayout tilConfirmPasswordRegister;
    TextInputLayout tilEmailRegister;
    TextInputLayout tilUsernameRegister;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();

        bRegisterSignUp = findViewById(R.id.bRegisterSignUp);
        tvToLogin = findViewById(R.id.tvToLogin);
        etEmailRegister = findViewById(R.id.etEmailRegister);
        etUsernameRegister = findViewById(R.id.etUsernameRegister);
        etPasswordRegister = findViewById(R.id.etPasswordRegister);
        etConfirmPasswordRegister = findViewById(R.id.etConfirmPasswordRegister);
        tilPasswordRegister = findViewById(R.id.tilPasswordRegister);
        tilEmailRegister = findViewById(R.id.tilEmailRegister);
        tilUsernameRegister = findViewById(R.id.tilUsernameRegister);
        tilConfirmPasswordRegister = findViewById(R.id.tilConfirmPasswordRegister);


        SignupButtonLogic();

        tvToLogin.setOnClickListener(v -> startActivity(new Intent(this, Login.class)));
    }

    private void SignupButtonLogic() {
        bRegisterSignUp.setOnClickListener(view -> {
            String email = Objects.requireNonNull(etEmailRegister.getText()).toString().trim();
            String password = Objects.requireNonNull(etPasswordRegister.getText()).toString();
            String confirmPassword = Objects.requireNonNull(etConfirmPasswordRegister.getText()).toString();
            String userName = Objects.requireNonNull(etUsernameRegister.getText()).toString().trim();

            if (!validateInput(email, password, confirmPassword, userName)) {
                return;
            }

            Toast.makeText(Register.this, "Signing Up", Toast.LENGTH_SHORT).show();

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser fbUser = mAuth.getCurrentUser();
                            createUserAndNextActivity(Objects.requireNonNull(fbUser).getUid(), userName);
                        } else {
                            Exception e = task.getException();
                            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                                tilEmailRegister.setError("Invalid email format.");
                            } else if (e instanceof FirebaseAuthUserCollisionException) {
                                tilEmailRegister.setError("This email is already in use.");
                            } else {
                                Toast.makeText(Register.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        });
    }

    private boolean validateInput(String email, String password, String confirmPassword, String userName) {
        boolean isValid = true;

        tilPasswordRegister.setError(null);
        tilConfirmPasswordRegister.setError(null);
        tilEmailRegister.setError(null);
        tilUsernameRegister.setError(null);

        if (email.isEmpty()) {
            tilEmailRegister.setError("Email is required.");
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmailRegister.setError("Invalid email format.");
            isValid = false;
        }

        if (password.isEmpty()) {
            tilPasswordRegister.setError("Password is required.");
            isValid = false;
        } else if (Utils.isPasswordNotValid(password)) {
            tilPasswordRegister.setError("Password must be at least 8 characters long and include a letter and a number.");
            isValid = false;
        }

        if (!password.equals(confirmPassword)) {
            tilConfirmPasswordRegister.setError("Passwords do not match.");
            isValid = false;
        }

        if (userName.isEmpty()) {
            tilUsernameRegister.setError("Username is required.");
            isValid = false;
        }

        return isValid;
    }

    private void createUserAndNextActivity(String uid, String userName) {
        User currentUser = new User(userName,0,0);
        DatabaseReference userNode = FirebaseDatabase.getInstance().getReference(FBBranches.USERS).child(uid);
        userNode.setValue(currentUser)
        .addOnCompleteListener(aVoid -> {
            Toast.makeText(Register.this, "User created successfully.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Register.this, MainApp.class));
            finish();
        })
        .addOnFailureListener(e -> Toast.makeText(Register.this, "Failed to create user in database.", Toast.LENGTH_SHORT).show());
    }
}
