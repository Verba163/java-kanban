package ru.yandex.practicum.models;

import java.util.Objects;

public class Subtask extends Task {
    private Epic epic;

    public Subtask(String title, String description, int taskId) {
        super(title, description, taskId);
    }

    public void setEpic(Epic epic) {
        this.epic = epic;
    }
    public Epic getEpic() {
        return epic;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "epic=" + epic +
                '}';
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        if (!super.equals(obj)) return false;
        Subtask subtask = (Subtask) obj;
        return Objects.equals(epic, subtask.epic);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epic);
    }
}


