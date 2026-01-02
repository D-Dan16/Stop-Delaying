package procrastination.ui.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.procrastination.R;

import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddTaskActivity extends AppCompatActivity {

    private TextInputEditText etTaskTitle;
    private TextInputEditText etTaskDescription;
    private Button btnSelectDueDate;
    private TextView tvSelectedDueDate;
    private Button btnSaveTask;

    private Date dueDate;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yy", Locale.getDefault());

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        etTaskTitle = findViewById(R.id.et_task_title);
        etTaskDescription = findViewById(R.id.et_task_description);
        btnSelectDueDate = findViewById(R.id.btn_select_due_date);
        tvSelectedDueDate = findViewById(R.id.tv_selected_due_date);
        btnSaveTask = findViewById(R.id.btn_save_task);

        btnSelectDueDate.setOnClickListener(v -> showDatePickerDialog());

        btnSaveTask.setOnClickListener(v -> saveTask());
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    calendar.set(year1, monthOfYear, dayOfMonth);
                    dueDate = calendar.getTime();
                    tvSelectedDueDate.setText(dateFormat.format(dueDate));
                }, year, month, day);
        datePickerDialog.show();
    }

    private void saveTask() {
        String title = etTaskTitle.getText().toString().trim();
        String description = etTaskDescription.getText().toString().trim();

        if (title.isEmpty()) {
            Toast.makeText(this, "Title cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (dueDate == null) {
            Toast.makeText(this, "Please select a due date", Toast.LENGTH_SHORT).show();
            return;
        }

        // For now, we'll just finish the activity.
        // You'll need to implement logic to add the task to your list.
        Toast.makeText(this, "Task saved!", Toast.LENGTH_SHORT).show();
        finish();
    }
}
