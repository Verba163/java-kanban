package ru.yandex.practicum.tests;


import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.models.Task;
import ru.yandex.practicum.models.Epic;
import ru.yandex.practicum.models.Subtask;
import ru.yandex.practicum.managers.InMemoryTaskManager;
import ru.yandex.practicum.enums.TaskStatus;

public class TaskTest {

    @Test
    public void testTasksEquality() {
        Task task1 = new Task("Название1", "Описание1", 1);
        Task task2 = new Task("Название2", "Описание2", 1);
        assertEquals(task1, task2);
    }

    @Test
    public void testEpicAndSubtaskEquality() {
        Epic epic1 = new Epic("Эпик1", "Описание1",1);
        Epic epic2 = new Epic("Эпик2", "Описание2",1);

        Task subtask1 = new Subtask("Подзадача1", "Описание Подзадачи1", epic1.getTaskId());
        Task subtask2 = new Subtask("Подзадача2", "Описание Подзадачи2", epic2.getTaskId());

        assertEquals(epic1, epic2);
        assertEquals(subtask1, subtask2);
    }

    @Test
    public void testEpicCannotBeSubtask() {
        Epic epic = new Epic("Эпик", "Описание Эпика",1);
        Subtask subtask = new Subtask("Подзадача", "Описание подзадачи", epic.getTaskId());
        assertNotNull(subtask);

    }

    @Test
    public void testSubtaskCannotBeEpic() {
        Subtask subtask = new Subtask("Подзадача", "Описание подзадачи", 1);
        Epic epic = new Epic("Эпик", "Описание Эпика",2);
        assertNotEquals(epic.getTaskId(), subtask.getTaskId());

    }
}


