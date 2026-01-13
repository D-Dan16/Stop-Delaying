package stop_delaying.ui.fragments;

import static com.google.android.gms.common.api.internal.LifecycleCallback.getFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.procrastination.R;

import stop_delaying.models.Date;
import stop_delaying.models.Task;
import stop_delaying.models.TimeOfDay;
import stop_delaying.ui.fragments.tabs.TasksCanceledFragment;
import stop_delaying.ui.fragments.tabs.TasksCompletedFragment;
import stop_delaying.ui.fragments.tabs.TasksToDoFragment;
import stop_delaying.utils.DatePickerFragment;
import stop_delaying.utils.TimePickerFragment;
import stop_delaying.utils.Utils;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.android.material.textfield.TextInputLayout;

public class TasksFragment extends Fragment {
    TabLayout tabLayout;
    ViewPager2 viewPager;
    FloatingActionButton fabMainToggle;
    FloatingActionButton fabAddTask;
    FloatingActionButton fabSearchTask;
    FloatingActionButton fabAiAnalyze;
    FloatingActionButton fabOrderBy;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tasks, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tabLayout = view.findViewById(R.id.tasks_tab_layout);
        viewPager = view.findViewById(R.id.tasks_view_pager);
        fabMainToggle = view.findViewById(R.id.fab_main_toggle);
        fabAddTask = view.findViewById(R.id.fab_add_task);
        fabSearchTask = view.findViewById(R.id.fab_search_task);
        fabAiAnalyze = view.findViewById(R.id.fab_ai_analyze);
        fabOrderBy = view.findViewById(R.id.fab_order_by);

        createTabLayoutLogic();

        registerActionButtons();
    }

    private void createTabLayoutLogic() {
        viewPager.setAdapter(new FragmentStateAdapter(this) {
            @Override
            public int getItemCount() {
                return 3;
            }

            @NonNull
            @Override
            public Fragment createFragment(int position) {
                return switch (position) {
                    case 0 -> new TasksToDoFragment();
                    case 1 -> new TasksCompletedFragment();
                    case 2 -> new TasksCanceledFragment();
                    default -> throw new IllegalStateException("Unexpected value: " + position);
                };
            }
        });

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0 -> tab.setText("To Do");
                case 1 -> tab.setText("Completed");
                case 2 -> tab.setText("Canceled");
            }
        }).attach();
    }

    private void registerActionButtons() {
        toggleActionButtonsVisibility();

        orderTasks();

        aiAnalyzeTasks();

        searchForTask();

        addNewTask();
    }

    private void toggleActionButtonsVisibility() {
        fabMainToggle.setOnClickListener(v -> {
            if (!fabAddTask.isShown()) {
                fabAddTask.show();
                fabSearchTask.show();
                fabAiAnalyze.show();
                fabOrderBy.show();
            } else {
                fabAddTask.hide();
                fabSearchTask.hide();
                fabAiAnalyze.hide();
                fabOrderBy.hide();
            }
        });
    }

    private void orderTasks() {
        fabOrderBy.setOnClickListener(v -> Utils.showDialog(requireView(), getParentFragmentManager(), R.layout.cv_order_tasks_popup));
    }

    private void aiAnalyzeTasks() {
        fabAiAnalyze.setOnClickListener(v -> Utils.showDialog(requireView(), getParentFragmentManager(), R.layout.cv_search_ai_analyze));
    }

    private void searchForTask() {
        fabSearchTask.setOnClickListener(v -> Utils.showDialog(requireView(), getParentFragmentManager(), R.layout.cv_search_task_popup));
    }

    private void addNewTask() {
        fabAddTask.setOnClickListener(v -> Utils.showDialog(requireView(), getParentFragmentManager(), R.layout.cv_add_task_popup, dialog -> {
            //<editor-fold desc="Get Components">
            EditText etTitle = dialog.findViewById(R.id.et_new_task_title);
            EditText etDescription = dialog.findViewById(R.id.et_edit_user_email);
            TextInputLayout tilTaskTitle = dialog.findViewById(R.id.til_task_title);
            TextInputLayout tilTaskDescription = dialog.findViewById(R.id.til_task_description);

            Button bDate = dialog.findViewById(R.id.bSetTime);
            Button bTimeOfDay = dialog.findViewById(R.id.bSetDate);
            //</editor-fold>

            TimePickerFragment timePickerFragment = new TimePickerFragment(dialog.findViewById(R.id.tvSelectedTime));
            DatePickerFragment datePickerFragment = new DatePickerFragment(dialog.findViewById(R.id.tvSelectedDate));

            bDate.setOnClickListener(v1 -> {
                timePickerFragment.show(getParentFragmentManager(), "dueTimeOfDayPicker");
            });

            bTimeOfDay.setOnClickListener(v1 -> {
                datePickerFragment.show(getParentFragmentManager(), "dueDayOfDayPicker");
            });

            //<editor-fold desc="Confirm add task logic">
            dialog.findViewById(R.id.bConfirmAddTask).setOnClickListener(v1 -> {
                tilTaskTitle.setError(null);
                tilTaskDescription.setError(null);

                //<editor-fold desc="Guards">
                if (etTitle.getText().toString().isEmpty()) {
                    tilTaskTitle.setError("Title is required.");
                    return;
                }
                if (etDescription.getText().toString().isEmpty()) {
                    tilTaskDescription.setError("Description is required.");
                    return;
                }
                if (dialog.<TextView>findViewById(R.id.tvSelectedDate).getText().equals("No Date Set")
                        || dialog.<TextView>findViewById(R.id.tvSelectedTime).getText().equals("No Time Set")) {
                    Toast.makeText(requireContext(), "Please select a date and time", Toast.LENGTH_SHORT).show();
                    return;
                }
                //</editor-fold>

                String title = etTitle.getText().toString();
                String description = etDescription.getText().toString();
                Date dueDate = new Date(datePickerFragment.getDayChosen(), datePickerFragment.getMonthChosen(), datePickerFragment.getYearChosen());
                TimeOfDay dueTime = new TimeOfDay(timePickerFragment.getHourChosen(), timePickerFragment.getMinuteChosen());

                //<editor-fold desc="Handle the task creation logic here">
                Toast.makeText(requireContext(), "Task added: " + title, Toast.LENGTH_LONG).show();

                TasksToDoFragment.addTask(new Task(title, description, dueDate, dueTime, Task.TaskStatus.TODO));

                // Dismiss the dialog
                DialogFragment addTaskDialog = (DialogFragment) getParentFragmentManager().findFragmentByTag("custom_popup");
                if (addTaskDialog != null)
                    addTaskDialog.dismiss();
                //</editor-fold>
            });
            //</editor-fold>

        }));
    }

}
