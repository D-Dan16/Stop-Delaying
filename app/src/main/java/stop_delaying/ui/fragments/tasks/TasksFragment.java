package stop_delaying.ui.fragments.tasks;

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
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.procrastination.R;

import stop_delaying.models.Date;
import stop_delaying.models.Task;
import stop_delaying.models.TimeOfDay;
import stop_delaying.ui.fragments.tasks.task_handlers.SelectionActionHandler;
import stop_delaying.ui.fragments.tasks.tabs.TaskTabIndices;
import stop_delaying.ui.fragments.tasks.tabs.TasksCanceledFragment;
import stop_delaying.ui.fragments.tasks.tabs.TasksCompletedFragment;
import stop_delaying.ui.fragments.tasks.tabs.TasksToDoFragment;
import stop_delaying.utils.ConfigurableDialogFragment;
import stop_delaying.utils.notifications_and_scheduling.NotificationCreator;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.android.material.textfield.TextInputLayout;

/**
 * Parent controller fragment for the Tasks screen.
 * <p>
 * Responsibilities:
 * - Hosts the tabs (To Do / Completed / Canceled)
 * - Exposes an inline selection toolbar (inline CAB) shown under the tabs when tasks are selected
 * - Routes selection actions (move/delete) to the currently active tab via `SelectionActionHandler`
 */
public class TasksFragment extends Fragment {
    public static String NOTIFICATION_CHANNEL_TASKS_CHANNEL_ID = "tasks";
    public static String NOTIFICATION_CHANNEL_TASKS_CHANNEL_NAME = "Tasks";
    public static String NOTIFICATION_CHANNEL_TASKS_CHANNEL_DESCRIPTION = "Notifications for tasks";
    TabLayout tabLayout;
    ViewPager2 viewPager;
    com.google.android.material.appbar.MaterialToolbar cardSelectionToolbar;
    FloatingActionButton fabMainToggle;
    FloatingActionButton fabAddTask;
    FloatingActionButton fabSearchTask;
    FloatingActionButton fabAiAnalyze;

    FloatingActionButton fabOrderBy;

    private SelectionActionHandler curCardSelectionHandler;

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
        cardSelectionToolbar = view.findViewById(R.id.selection_toolbar);
        fabMainToggle = view.findViewById(R.id.fab_main_toggle);
        fabAddTask = view.findViewById(R.id.fab_add_task);
        fabSearchTask = view.findViewById(R.id.fab_search_task);
        fabAiAnalyze = view.findViewById(R.id.fab_ai_analyze);
        fabOrderBy = view.findViewById(R.id.fab_order_by);

        selectionToolBarLogic();

        createTabLayoutLogic();

        registerActionButtons();

        NotificationCreator.createNotificationChannel(
                requireContext(),
                NOTIFICATION_CHANNEL_TASKS_CHANNEL_ID,
                NOTIFICATION_CHANNEL_TASKS_CHANNEL_NAME,
                NotificationManagerCompat.IMPORTANCE_DEFAULT,
                NOTIFICATION_CHANNEL_TASKS_CHANNEL_DESCRIPTION
        );
    }

    /**
     * Wires the inline selection toolbar (inline CAB) that appears under the tabs when the user
     * long-presses a task card. The toolbar shows icon-only actions and an X navigation icon to
     * cancel selection.
     */
    private void selectionToolBarLogic() {
        if (cardSelectionToolbar != null) {
            // Inflate icon-only menu for move/delete actions
            cardSelectionToolbar.inflateMenu(R.menu.menu_tasks_selection_bar);

            // Left navigation icon (X) cancels selection: clear selection in the active tab (via handler)
            // and then hide the selection bar.
            cardSelectionToolbar.setNavigationIcon(R.drawable.ic_canceled);
            cardSelectionToolbar.setNavigationOnClickListener(v -> {
                if (curCardSelectionHandler != null) {
                    curCardSelectionHandler.onEscape();
                }
                hideSelectionBar();
            });

            // Route action clicks to the child fragment-provided handler.
            cardSelectionToolbar.setOnMenuItemClickListener(item -> {
                if (curCardSelectionHandler == null) return false;
                int id = item.getItemId();
                if (id == R.id.action_move_todo) {
                    curCardSelectionHandler.onMoveTo(Task.TaskStatus.TODO);
                    return true;
                } else if (id == R.id.action_move_completed) {
                    curCardSelectionHandler.onMoveTo(Task.TaskStatus.COMPLETED);
                    return true;
                } else if (id == R.id.action_move_canceled) {
                    curCardSelectionHandler.onMoveTo(Task.TaskStatus.CANCELED);
                    return true;
                } else if (id == R.id.action_delete) {
                    curCardSelectionHandler.onDelete();
                    return true;
                }
                return false;
            });
        }
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
                    case TaskTabIndices.TO_DO -> new TasksToDoFragment();
                    case TaskTabIndices.COMPLETED -> new TasksCompletedFragment();
                    case TaskTabIndices.CANCELED -> new TasksCanceledFragment();
                    default -> throw new IllegalStateException("Unexpected value: " + position);
                };
            }
        });

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case TaskTabIndices.TO_DO -> tab.setText("To Do");
                case TaskTabIndices.COMPLETED -> tab.setText("Completed");
                case TaskTabIndices.CANCELED -> tab.setText("Canceled");
            }
        }).attach();

        // Clear any active selections when switching tabs (both via swipe and tab tap)
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                // Ask the active tab to clear its selection, then hide the inline selection bar
                if (curCardSelectionHandler != null) {
                    curCardSelectionHandler.onEscape();
                }
                hideSelectionBar();
            }
        });
    }

    private void registerActionButtons() {
        toggleActionButtonsVisibility();

        orderTasks();

        aiAnalyzeTasks();

        searchForTask();

        addNewTask();
    }

    // Selection toolbar controls for child tabs

    /**
     * Shows the inline selection toolbar (inline CAB) and sets its title/subtitle.
     * Called by child fragments when the first item gets selected.
     *
     * @param selectedCount current number of selected tasks
     * @param handler       action handler provided by the active tab to execute move/delete
     */
    public void showSelectionBar(int selectedCount, SelectionActionHandler handler) {
        this.curCardSelectionHandler = handler;
        if (cardSelectionToolbar != null) {
            cardSelectionToolbar.setVisibility(View.VISIBLE);
            cardSelectionToolbar.setTitle("Select tasks");
            cardSelectionToolbar.setSubtitle(selectedCount + " selected");
        }
    }

    /**
     * Updates the toolbar subtitle with the current selected count.
     * Auto-hides the selection bar when the count reaches zero.
     */
    public void updateSelectionCount(int selectedCount) {
        if (cardSelectionToolbar != null && cardSelectionToolbar.getVisibility() == View.VISIBLE) {
            if (selectedCount <= 0) {
                hideSelectionBar();
            } else {
                cardSelectionToolbar.setSubtitle(selectedCount + " selected");
            }
        }
    }

    /**
     * Hides the inline selection toolbar and clears the current handler reference.
     * Child fragments are responsible for clearing their adapter selection state.
     */
    public void hideSelectionBar() {
        if (cardSelectionToolbar != null) {
            cardSelectionToolbar.setVisibility(View.GONE);
        }
        curCardSelectionHandler = null;
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
        fabOrderBy.setOnClickListener(v -> ConfigurableDialogFragment.showDialog(requireView(), getParentFragmentManager(), R.layout.cv_order_tasks_popup));
    }

    private void aiAnalyzeTasks() {
        fabAiAnalyze.setOnClickListener(v -> ConfigurableDialogFragment.showDialog(requireView(), getParentFragmentManager(), R.layout.cv_search_ai_analyze));
    }

    private void searchForTask() {
        fabSearchTask.setOnClickListener(v -> ConfigurableDialogFragment.showDialog(requireView(), getParentFragmentManager(), R.layout.cv_search_task_popup,
                (dialogView) -> {
                    // If there are selected tasks, unselect them so there won't be menu over-dumping and to keep logic simpler, without wierd edge-cases.
                    switch (viewPager.getCurrentItem()) {
                        case TaskTabIndices.TO_DO ->
                                TasksToDoFragment.getAdapter().clearSelection();
                        case TaskTabIndices.COMPLETED ->
                                TasksCompletedFragment.getAdapter().clearSelection();
                        case TaskTabIndices.CANCELED ->
                                TasksCanceledFragment.getAdapter().clearSelection();
                        default ->
                                throw new IllegalStateException("Unexpected value: " + viewPager.getCurrentItem());
                    }


                    EditText etSearch = dialogView.findViewById(R.id.et_search_task_by_name_search);
                    TextInputLayout tilSearch = dialogView.findViewById(R.id.til_task_search_name_search);
                    Button bSearch = dialogView.findViewById(R.id.b_confirm_search_task);

                    //<editor-fold desc="On Filter Tasks logic ">
                    bSearch.setOnClickListener(v1 -> {
                        // reset prev search of this dialog if there was a previous search
                        TasksToDoFragment.getAdapter().unfilterTasks();
                        TasksCompletedFragment.getAdapter().unfilterTasks();
                        TasksCanceledFragment.getAdapter().unfilterTasks();
                        //----------------------------------------------------------------

                        tilSearch.setError(null);

                        String taskName = etSearch.getText().toString();

                        if (taskName.isEmpty()) {
                            tilSearch.setError("Task name is required.");
                            return;
                        }

                        // Obtain all task lists and filter based on search query
                        TasksToDoFragment.getAdapter().filterTasks(taskName);
                        TasksCompletedFragment.getAdapter().filterTasks(taskName);
                        TasksCanceledFragment.getAdapter().filterTasks(taskName);

                        //<editor-fold desc="Unfilter button logic">
                        View mcvFilterClearBar = requireView().findViewById(R.id.mcv_filter_clear_bar);
                        mcvFilterClearBar.setVisibility(View.VISIBLE);
                        mcvFilterClearBar.setOnClickListener(v2 -> {
                            TasksToDoFragment.getAdapter().unfilterTasks();
                            TasksCompletedFragment.getAdapter().unfilterTasks();
                            TasksCanceledFragment.getAdapter().unfilterTasks();

                            mcvFilterClearBar.setVisibility(View.GONE);
                        });
                        //</editor-fold>
                    });
                    //</editor-fold>
                }
        ));
    }

    private void addNewTask() {
        fabAddTask.setOnClickListener(v -> ConfigurableDialogFragment.showDialog(requireView(), getParentFragmentManager(), R.layout.cv_add_task_popup, dialog -> {
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
                if (dialog.<TextView>findViewById(R.id.tvSelectedDate)
                        .getText()
                        .equals("No Date Set")
                        || dialog.<TextView>findViewById(R.id.tvSelectedTime)
                        .getText()
                        .equals("No Time Set")) {
                    Toast.makeText(requireContext(), "Please select a date and time", Toast.LENGTH_SHORT)
                            .show();
                    return;
                }
                //</editor-fold>

                String title = etTitle.getText().toString();
                String description = etDescription.getText().toString();
                Date dueDate = new Date(datePickerFragment.getDayChosen(), datePickerFragment.getMonthChosen(), datePickerFragment.getYearChosen());
                TimeOfDay dueTime = new TimeOfDay(timePickerFragment.getHourChosen(), timePickerFragment.getMinuteChosen());

                //<editor-fold desc="Handle the task creation logic here">
                Toast.makeText(requireContext(), "Task added: " + title, Toast.LENGTH_LONG).show();

                TasksToDoFragment.addTaskFromUser(new Task(title, description, dueDate, dueTime, Task.TaskStatus.TODO));

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
