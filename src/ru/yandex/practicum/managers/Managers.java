package ru.yandex.practicum.managers;

import ru.yandex.practicum.interfaces.TaskManagers;

public class Managers {

    public static TaskManagers getDefault() {
        return new InMemoryTaskManager();
    }
}

