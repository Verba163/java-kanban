import java.util.ArrayList;

class Epic extends Task {
    private final ArrayList<Subtask> subtasks;

    public Epic(String title, String description, int taskId) {
        super(title, description, taskId);
        this.subtasks = new ArrayList<>();
    }


    public void addSubtask(Subtask subtask) {
        subtasks.add(subtask);
        subtask.setEpic(this);
    }


    public ArrayList<Subtask> getSubtasks() {
        return subtasks;
    }


    public boolean checkCompletion() {
        for (Subtask subtask : subtasks) {
            if (subtask.getStatus() != TaskStatus.DONE) {
                return false;
            }
        }
        return true;
    }


    private boolean areAllSubtasksNew() {
        if (subtasks.isEmpty()) {
            return true;
        }

        for (Subtask subtask : subtasks) {
            if (subtask.getStatus() != TaskStatus.NEW) {
                return false;
            }
        }

        return true;
    }


    public void updateStatus() {
        if (areAllSubtasksNew()) {
            setStatus(TaskStatus.NEW);
        } else if (checkCompletion()) {
            setStatus(TaskStatus.DONE);
        } else {
            setStatus(TaskStatus.IN_PROGRESS);
        }
    }


    @Override
    public String toString() {
        return "Epic{" +
                "title='" + getTitle() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", taskId=" + getTaskId() +
                ", status=" + getStatus() +
                ", subtasks=" + subtasks +
                '}';
    }
}