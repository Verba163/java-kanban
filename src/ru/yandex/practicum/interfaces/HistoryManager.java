package ru.yandex.practicum.interfaces;

import ru.yandex.practicum.models.Task;

import java.util.ArrayList;

public interface HistoryManager {

    void add(Task task);

    ArrayList<Task> getHistory();
}

