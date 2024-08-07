
import ru.yandex.practicum.enums.TaskStatus;
import ru.yandex.practicum.managers.InMemoryHistoryManager;
import ru.yandex.practicum.models.Epic;
import ru.yandex.practicum.models.Subtask;
import ru.yandex.practicum.models.Task;
import ru.yandex.practicum.managers.InMemoryTaskManager;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        Scanner scanner = new Scanner(System.in);

        demoTasks(taskManager);


        while (true) {
            printMenu();
            int choice = scanner.nextInt();
            userChoice(choice, taskManager, historyManager, scanner);
        }
    }

    private static void demoTasks(InMemoryTaskManager taskManager) {

        Task task = taskManager.createTask("Название0", "Описание0");
        Task task1 = taskManager.createTask("Название1", "Описание1");
        Task task2 = taskManager.createTask("Название2", "Описание2");
        Task task3 = taskManager.createTask("Название3", "Описание3");


        Epic epic = taskManager.createEpic("НазваниеЭпика0", "Описание");
        Epic epic1 = taskManager.createEpic("НазваниеЭпика1", "Описание");
        Epic epic2 = taskManager.createEpic("НазваниеЭпика2", "Описание");
        Epic epic3 = taskManager.createEpic("НазваниеЭпика3", "Описание");
        Epic epic4 = taskManager.createEpic("НазваниеЭпика4", "Описание");


        Subtask subtask1 = taskManager.createSubtask("Название1", "Описание1", epic1.getTaskId());
        Subtask subtask2 = taskManager.createSubtask("Название2", "Описание2", epic.getTaskId());
        Subtask subtask3 = taskManager.createSubtask("Название1", "Описание1", epic3.getTaskId());
        Subtask subtask4 = taskManager.createSubtask("Название2", "Описание2", epic4.getTaskId());
        Subtask subtask5 = taskManager.createSubtask("Название1", "Описание1", epic2.getTaskId());
        Subtask subtask6 = taskManager.createSubtask("Название2", "Описание2", epic4.getTaskId());
    }

    private static void userChoice(int choice, InMemoryTaskManager taskManager, InMemoryHistoryManager historyManager, Scanner scanner) {
        switch (choice) {
            case 1:
                printAllTasks(taskManager, historyManager);
                break;
            case 2:
                updateTaskStatus(taskManager, scanner);
                break;
            case 3:
                System.exit(0);
                break;
            default:
                System.out.println("Неверный выбор. Пожалуйста, попробуйте снова.");
        }
    }

    private static void updateTaskStatus(InMemoryTaskManager taskManager, Scanner scanner) {

        System.out.print("Введите ID задачи для обновления статуса: ");
        int taskId = scanner.nextInt();
        System.out.print("Введите новый статус (NEW, IN_PROGRESS, DONE): ");
        String statusInput = scanner.next();

        TaskStatus status = TaskStatus.valueOf(statusInput.toUpperCase());
        taskManager.updateTaskStatus(taskId, status); // Метод обновления статуса задачи
        System.out.println("Статус задачи обновлён.");

    }

    private static void printAllTasks(InMemoryTaskManager taskManager, InMemoryHistoryManager historyManager) {
        System.out.println("Задачи:");
        for (Task task : taskManager.getAllTasks()) {
            System.out.println(task);
        }

        System.out.println("История:");
        for (Task task : historyManager.getHistory()) {
            System.out.println(task);
        }
    }

    public static void printMenu() {
        System.out.println("Выберите действие:");
        System.out.println("1 - Показать все задачи");
        System.out.println("2 - Обновить статус задач");
        System.out.println("3 - Выход");
    }
}
