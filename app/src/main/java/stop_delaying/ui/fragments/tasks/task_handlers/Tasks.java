package stop_delaying.ui.fragments.tasks.task_handlers;

import java.util.ArrayList;
import java.util.List;

import stop_delaying.models.Task;

/**
 * Represents a collection of tasks, divided into visible and hidden tasks.
 * Each instance of this class holds a separate list, and the separation is because of the status of the tasks (ie. To Do, Completed, Canceled).
 *
 * @param visibleTasks The list of tasks currently visible.
 * @param hiddenTasks  The list of tasks currently hidden.
 */
public record Tasks(ArrayList<Task> visibleTasks, ArrayList<Task> hiddenTasks) {
    public void setAllTasks(List<Task> tasks) {
        visibleTasks.clear();
        visibleTasks.addAll(tasks);
        hiddenTasks.clear();
    }

    public int getSelectedCount() {
        int count = 0;
        for (Task t : visibleTasks)
            if (t.isTaskSelected())
                count++;
        return count;
    }

    public List<Task> getSelectedTasks() {
        List<Task> selected = new ArrayList<>();
        for (Task t : visibleTasks)
            if (t.isTaskSelected())
                selected.add(t);
        return selected;
    }

    public void clearSelection() {
        for (Task t : visibleTasks)
            t.setTaskSelected(false);
    }

    public void removeSelectedTasks() {
        visibleTasks.removeIf(Task::isTaskSelected);
        hiddenTasks.removeIf(Task::isTaskSelected);
    }

    public void filterTasks(String query) {
        if (query == null || query.isEmpty())
            return;

        hiddenTasks.clear();

        for (Task t : visibleTasks) {
            String q = query.toLowerCase().trim();
            if (!t.getTitle().toLowerCase().contains(q) && !t.getDescription().toLowerCase().contains(q))
                hiddenTasks.add(t);
        }

        visibleTasks.removeAll(hiddenTasks);
    }

    public void unfilterTasks() {
        visibleTasks.addAll(hiddenTasks);
        hiddenTasks.clear();
    }

    public void add(Task task) {
        visibleTasks.add(task);
    }

    public ArrayList<Integer> updateTasks(List<Task> selected) {
        ArrayList<Integer> listOfIndices = new ArrayList<>();

        for (Task t : selected)
            if (visibleTasks.contains(t)) {
                int index = visibleTasks.indexOf(t);
                listOfIndices.add(index);
                visibleTasks.set(index, t);
            }

        return listOfIndices;
    }
}
