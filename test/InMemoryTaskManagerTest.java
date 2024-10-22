import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.managers.InMemoryHistoryManager;
import ru.yandex.practicum.models.Task;
import ru.yandex.practicum.managers.InMemoryTaskManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest {

    @Test
    public void testTaskManagerInitialization() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        assertNotNull(taskManager, "TaskManager должен быть инициализирован.");
    }

    @Test
    public void testTaskIdConflict() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Task task1 = manager.createTask("Задача 1", "Описание 1",Duration.ofHours(1), LocalDateTime.now());
        Task task2 = manager.createTask("Задача 2", "Описание 2", Duration.ofHours(1), LocalDateTime.now().plusDays(1));

        assertNotEquals(task1.getTaskId(), task2.getTaskId(), "ID задач должны быть уникальными.");
    }

    @Test
    public void testTaskImmutability() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Task task = manager.createTask("Задача", "Описание", Duration.ofHours(1), LocalDateTime.now());
        int originalId = task.getTaskId();

        manager.createTask("Задача 2", "Описание 2", Duration.ofHours(1), LocalDateTime.now().plusDays(1));

        assertEquals(originalId, task.getTaskId(), "ID задачи не должен изменяться.");
    }

    @Test
    public void testHistoryManager() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        Task task = taskManager.createTask("Задача", "Описание",Duration.ofHours(1), LocalDateTime.now());
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

        Task task = taskManager.createTask("Задача", "Описание", Duration.ofHours(1), LocalDateTime.now());
        historyManager.add(task);

        Task updatedTask = new Task("Задача", "Обновленное описание", task.getTaskId(), task.getDuration(), task.getStartTime());
        taskManager.updateTask(updatedTask);

        ArrayList<Task> history = historyManager.getHistory();

        assertEquals(1, history.size(), "История должна содержать одну задачу.");
        assertEquals(updatedTask, history.get(0), "Обновленная задача должна присутствовать в истории.");
    }

}