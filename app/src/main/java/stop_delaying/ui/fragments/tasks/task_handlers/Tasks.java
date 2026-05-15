package stop_delaying.ui.fragments.tasks.task_handlers;

import java.util.ArrayList;
import java.util.List;

import stop_delaying.models.Task;

/**
 * Manages a collection of tasks, separating them into visible and hidden lists 
 * to support filtering and UI presentation within task tabs.
 */
@SuppressWarnings("ClassCanBeRecord")
public final class Tasks {
    /** Tasks currently visible in the UI list. */
    private final ArrayList<Task> visibleTasks;
    /** Tasks temporarily hidden from view due to active filtering. */
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

    /**
     * Resets the entire task collection with a new list of tasks.
     */
    public void setAllTasks(List<Task> tasks) {
        visibleTasks.clear();
        visibleTasks.addAll(tasks);
        hiddenTasks.clear();
    }

    /**
     * Calculates the total number of visible tasks that are currently selected.
     */
    public int getSelectedCount() {
        int count = 0;
        for (Task t : visibleTasks)
            if (t.isTaskSelected())
                count++;
        return count;
    }

    /**
     * Returns a list of all visible tasks that are currently selected.
     */
    public List<Task> getSelectedTasks() {
        List<Task> selected = new ArrayList<>();
        for (Task t : visibleTasks)
            if (t.isTaskSelected())
                selected.add(t);
        return selected;
    }

    /**
     * Resets the selection status for all visible tasks.
     */
    public void clearSelection() {
        for (Task t : visibleTasks)
            t.setTaskSelected(false);
    }

    /**
     * Removes all selected tasks from both visible and hidden collections.
     */
    public void removeSelectedTasks() {
        visibleTasks.removeIf(Task::isTaskSelected);
        hiddenTasks.removeIf(Task::isTaskSelected);
    }

    /**
     * Filters visible tasks based on a text query, moving non-matching tasks to the hidden list.
     * @param query The search string used for filtering.
     */
    public void filterTasks(String query) {
        unfilterTasks(); // Reset visibility before applying a new filter

        if (query == null || query.isEmpty())
            return;

        // Sanitize the query: remove non-English alphanumeric characters and spaces that could hinder the search
        String q = query.replaceAll("[^a-zA-Z0-9\\s]", "").toLowerCase().trim();
        if (q.isEmpty()) return;

        for (Task t : visibleTasks) {
            // Also, sanitize title and description to ensure symbols or accents don't hinder the match
            String title = (t.getTitle() != null) ? t.getTitle().replaceAll("[^a-zA-Z0-9\\s]", "").toLowerCase() : "";
            String description = (t.getDescription() != null) ? t.getDescription().replaceAll("[^a-zA-Z0-9\\s]", "").toLowerCase() : "";

            if (!title.contains(q) && !description.contains(q))
                hiddenTasks.add(t);
        }

        visibleTasks.removeAll(hiddenTasks);
    }

    /**
     * Restores all hidden tasks back to the visible collection.
     */
    public void unfilterTasks() {
        visibleTasks.addAll(hiddenTasks);
        hiddenTasks.clear();
    }

    /** Adds a single task to the visible collection. */
    public void add(Task task) {
        visibleTasks.add(task);
    }

    /**
     * Updates the status of specific tasks within the visible collection.
     */
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
