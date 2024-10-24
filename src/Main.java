
import ru.yandex.practicum.managers.InMemoryTaskManager;

import ru.yandex.practicum.models.Task;

import java.time.Duration;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();

        taskManager.createTask("Task 1", "Description for task 1", Duration.ofHours(1), LocalDateTime.of(2024, 10, 20, 10, 0));
        taskManager.createTask("Task 2", "Description for task 2", Duration.ofHours(1), LocalDateTime.of(2023, 10, 20, 10, 0));
        taskManager.createTask("Task 3", "Description for task 3", Duration.ofHours(1), LocalDateTime.of(2025, 10, 21, 10, 0));

        System.out.println("Все задачи (отсортированные по времени начала):");
        for (Task task : taskManager.getAllTasks()) {
            System.out.println(task.toCSV());
        }
    }
}