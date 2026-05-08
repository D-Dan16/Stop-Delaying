package stop_delaying.ui.fragments.tasks.task_handlers;

import java.util.ArrayList;
import java.util.List;

import stop_delaying.models.Task;

/**
 * Represents a collection of tasks, divided into visible and hidden tasks.
 * Each instance of this class holds a separate list, and the separation is because of the status of the tasks (i.e., To Do, Completed, Canceled).
 */
@SuppressWarnings("ClassCanBeRecord")
public final class Tasks {
    private final ArrayList<Task> visibleTasks;
    private final ArrayList<Task> hiddenTasks;

    public Tasks(ArrayList<Task> visibleTasks, ArrayList<Task> hiddenTasks) {
        this.visibleTasks = visibleTasks;
        this.hiddenTasks = hiddenTasks;
    }

    public ArrayList<Task> visibleTasks() {
        return visibleTasks;
    }

    public ArrayList<Task> hiddenTasks() {
        return hiddenTasks;
    }

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

        // Sanitize the query: remove non-English alphanumeric characters and spaces that could hinder the search
        String q = query.replaceAll("[^a-zA-Z0-9\\s]", "").toLowerCase().trim();
        if (q.isEmpty()) return;

        hiddenTasks.clear();

        for (Task t : visibleTasks) {
            // Also, sanitize title and description to ensure symbols or accents don't hinder the match
            String title = (t.getTitle() != null) ? t.getTitle().replaceAll("[^a-zA-Z0-9\\s]", "").toLowerCase() : "";
            String description = (t.getDescription() != null) ? t.getDescription().replaceAll("[^a-zA-Z0-9\\s]", "").toLowerCase() : "";

            if (!title.contains(q) && !description.contains(q))
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
