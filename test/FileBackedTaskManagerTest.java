import org.junit.jupiter.api.*;
import ru.yandex.practicum.streams.FileBackedTaskManager;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {
    private static final String FILE_PATH = "C:/Users/4Java/IdeaProjects/java-kanban/test/test_data.cvs";
    private FileBackedTaskManager manager;

    @BeforeEach
    void setUp() {
        File file = new File(FILE_PATH);
        manager = new FileBackedTaskManager(file);
    }

    @AfterEach
    void tearDown() {
        File file = new File(FILE_PATH);
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    void testSaveAndLoadEmptyFile() {
        manager.save();

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(new File(FILE_PATH));

        assertEquals(0, loadedManager.getTasks().size());
        assertEquals(0, loadedManager.epics.size());
        assertEquals(0, loadedManager.subtasksByEpic.size());
    }

    @Test
    void testSaveMultipleTasks() {

        manager.createTask("Task 1", "Description 1");
        manager.createTask("Task 2", "Description 2");


        manager.save();


        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(new File(FILE_PATH));


        assertEquals(2, loadedManager.getTasks().size());
        assertTrue(loadedManager.getTasks().containsKey(1));
        assertTrue(loadedManager.getTasks().containsKey(2));
    }

    @Test
    void testLoadMultipleTasks() {
        manager.createTask("Task 1", "Description 1");
        manager.createTask("Task 2", "Description 2");
        manager.save();

        manager = new FileBackedTaskManager(new File(FILE_PATH));

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(new File(FILE_PATH));

        assertEquals(2, loadedManager.getTasks().size());
        assertEquals("Task 1", loadedManager.getTasks().get(1).getTitle());
        assertEquals("Task 2", loadedManager.getTasks().get(2).getTitle());
    }
}