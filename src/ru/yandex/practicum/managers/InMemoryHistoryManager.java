package ru.yandex.practicum.managers;

import ru.yandex.practicum.interfaces.HistoryManager;
import ru.yandex.practicum.models.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {

    private final ArrayList<Task> history = new ArrayList<>();

    @Override
    public void add(Task task) {
        if (task != null) {
            history.remove(task);
            history.add(task);
        }
    }

    @Override
    public ArrayList<Task> getHistory() {
        return new ArrayList<>(history);
    }


}