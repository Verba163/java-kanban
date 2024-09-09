import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.managers.InMemoryHistoryManager;
import ru.yandex.practicum.models.Task;
import ru.yandex.practicum.managers.InMemoryTaskManager;

import java.util.ArrayList;

public class InMemoryTaskManagerTest {

    @Test
    public void testTaskManagerInitialization() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        assertNotNull(taskManager);
    }

    @Test
    public void testAddAndFindTasks() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();


        Task task = taskManager.createTask("Задача", "Описание");
        Task getTask = taskManager.getTask(task.getTaskId());
        assertNotNull(getTask, "Полученная задача не должна быть null.");
        assertEquals(task, getTask, "Задачи не совпадают.");
    }

    @Test
    public void testTaskIdConflict() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Task task1 = manager.createTask("Задача1", "Описание1");
        Task task2 = manager.createTask("Задача2", "Описание2");

        assertNotEquals(task1.getTaskId(), task2.getTaskId());
    }

    @Test
    public void testTaskImmutability() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Task task = manager.createTask("Задача", "Описание");
        int originalId = task.getTaskId();

        manager.createTask("Задача2", "Описание2");

        assertEquals(originalId, task.getTaskId());
    }


    @Test
    public void testHistoryManager() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        Task task = taskManager.createTask("Задача", "Описание");
        historyManager.add(task);
        ArrayList<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не должна быть null.");
        assertEquals(1, history.size(), "История должна содержать одну задачу.");
        assertEquals(task, history.get(0), "Задача в истории не совпадает с добавленной.");
    }

    @Test
    public void testTaskUpdateReflectsInHistory() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        Task task = taskManager.createTask("Задача", "Описание");
        historyManager.add(task);


        Task updatedTask = taskManager.getTask(task.getTaskId());
        ArrayList<Task> history = historyManager.getHistory();

        assertEquals(1, history.size(), "История должна содержать одну задачу.");
        assertEquals(updatedTask, history.get(0), "Обновленная задача должна присутствовать в истории.");
    }

    @Test
    public void testDuplicateTaskHandling() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        Task task1 = taskManager.createTask("Задача 1", "Описание 1");
        Task task2 = taskManager.createTask("Задача 2", "Описание 2");
        String expectedMessage = "Задача с таким ID уже существует!";

    }
}

