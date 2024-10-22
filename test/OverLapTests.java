import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.managers.InMemoryTaskManager;
import ru.yandex.practicum.models.Epic;
import ru.yandex.practicum.models.Subtask;
import ru.yandex.practicum.models.Task;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class OverLapTests {

    private InMemoryTaskManager taskManager;

    @BeforeEach
    void setUp() {
        taskManager = new InMemoryTaskManager();
    }


    @Test
    void testTaskOverlap() {
        taskManager.createTask("Task 1", "Description 1", Duration.ofHours(2), LocalDateTime.of(2023, 10, 1, 10, 0));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            taskManager.createTask("Task 2", "Description 2", Duration.ofHours(2), LocalDateTime.of(2023, 10, 1, 11, 0));
        });
        assertEquals("Задача пересекается с существующей задачей.", exception.getMessage());
    }

    @Test
    void testSubtaskOverlap() {
        Epic epic = taskManager.createEpic("Epic 1", "Epic Description");
        taskManager.createSubtask("Subtask 1", "Description 1", Duration.ofHours(2), LocalDateTime.of(2023, 10, 1, 11, 0),epic.getTaskId());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            taskManager.createSubtask("Subtask 2", "Description 2", Duration.ofHours(2), LocalDateTime.of(2023, 10, 1, 11, 0), epic.getTaskId());
        });
        assertEquals("Подзадача пересекается с существующей задачей или подзадачей.", exception.getMessage());
    }

}