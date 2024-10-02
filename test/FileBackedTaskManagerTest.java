import org.junit.jupiter.api.*;
import ru.yandex.practicum.streams.FileBackedTaskManager;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {
    private static final String FILE_PATH = "C:/Users/4Java/IdeaProjects/java-kanban/test/test_data.cvs";
    private static final String FILE_PATH1 = "C:/Users/4Java/IdeaProjects/java-kanban/test/test_data1.cvs";
    private static final String FILE_PATH2 = "C:/Users/4Java/IdeaProjects/java-kanban/test/test_data2.cvs";
    private FileBackedTaskManager manager;
    private FileBackedTaskManager manager1;
    private FileBackedTaskManager manager2;

    @BeforeEach
    void setUp() {
        File file = new File(FILE_PATH);
        File file1 = new File(FILE_PATH1);
        File file2 = new File(FILE_PATH2);

        manager = new FileBackedTaskManager(file);
        manager1 = new FileBackedTaskManager(file1);
        manager2 = new FileBackedTaskManager(file2);
    }


    @Test
    void testSaveMultipleTasks() {

        manager.createTask("Task 1", "Description 1");
        manager.createTask("Task 2", "Description 2");
        manager.createEpic("new", "New");
        manager.createSubtask("1", "1", 3);
        manager.save();
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(new File(FILE_PATH));


        assertEquals(4, loadedManager.getTasks().size());
        assertTrue(loadedManager.getTasks().containsKey(1));
        assertTrue(loadedManager.getTasks().containsKey(2));
    }

    @Test
    void testLoadMultipleTasks() {
        manager2.createTask("Task 3", "Description 1");
        manager2.createTask("Task 4", "Description 2");
        manager2.save();

        manager2 = new FileBackedTaskManager(new File(FILE_PATH2));

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(new File(FILE_PATH2));

        assertEquals(2, loadedManager.getTasks().size());
        assertEquals("Task 3", loadedManager.getTasks().get(1).getTitle());
        assertEquals("Task 4", loadedManager.getTasks().get(2).getTitle());
    }

    @Test
    void testSaveAndLoadEmptyFile() {
        manager1.save();
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(new File(FILE_PATH1));
        assertEquals(0, loadedManager.getTasks().size());
    }
}