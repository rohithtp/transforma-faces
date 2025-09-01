package org.apache.myfaces.transforma;

import org.apache.myfaces.transforma.beans.TaskBean;
import org.apache.myfaces.transforma.dao.TaskDAO;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Integration test for complete task management flow.
 * Tests the interaction between TaskBean and TaskDAO with a real database.
 * 
 * @author Transforma-Faces Team
 * @version 1.0.0
 */
public class TaskManagementIntegrationTest {
    
    private TaskBean taskBean;
    private TaskDAO taskDAO;
    private Connection connection;
    private static final String TEST_DB_URL = "jdbc:h2:mem:integrationdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE";
    private static final String TEST_DB_USER = "sa";
    private static final String TEST_DB_PASSWORD = "";
    
    @Before
    public void setUp() throws Exception {
        // Create in-memory H2 database
        connection = DriverManager.getConnection(TEST_DB_URL, TEST_DB_USER, TEST_DB_PASSWORD);
        
        // Drop table if exists to ensure clean state
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS tasks");
        }
        
        // Create tasks table
        String createTableSQL = "CREATE TABLE tasks (" +
            "id INTEGER PRIMARY KEY AUTO_INCREMENT," +
            "title VARCHAR(255) NOT NULL," +
            "description TEXT," +
            "priority VARCHAR(50) NOT NULL DEFAULT 'Medium'," +
            "status VARCHAR(50) NOT NULL DEFAULT 'To Do'," +
            "created_date TIMESTAMP NOT NULL," +
            "updated_date TIMESTAMP" +
            ")";
        
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createTableSQL);
        }
        
        // Create TaskDAO
        taskDAO = new TaskDAO();
        
        // Create TaskBean
        taskBean = new TaskBean();
        
        // Inject the TaskDAO into TaskBean using reflection
        try {
            java.lang.reflect.Field daoField = TaskBean.class.getDeclaredField("taskDAO");
            daoField.setAccessible(true);
            daoField.set(taskBean, taskDAO);
            
            // Also set tasks to null to force initialization
            java.lang.reflect.Field tasksField = TaskBean.class.getDeclaredField("tasks");
            tasksField.setAccessible(true);
            tasksField.set(taskBean, null);
        } catch (Exception e) {
            fail("Failed to inject TaskDAO into TaskBean: " + e.getMessage());
        }
    }
    
    @After
    public void tearDown() throws Exception {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
    
    @Test
    public void testTaskBeanInitialization() {
        // Test TaskBean initialization and default values
        
        assertNotNull("TaskBean should not be null", taskBean);
        assertNotNull("New task should not be null", taskBean.getNewTask());
        assertEquals("New task priority should be Medium", "Medium", taskBean.getNewTask().getPriority());
        assertEquals("New task status should be To Do", "To Do", taskBean.getNewTask().getStatus());
        
        // Test that getTasks() initializes the list if needed
        List<TaskBean.Task> tasks = taskBean.getTasks();
        assertNotNull("Tasks list should not be null", tasks);
        assertTrue("Tasks list should be empty initially", tasks.isEmpty());
    }
    
    @Test
    public void testTaskValidation() {
        // Test task validation (empty/null titles should not be added)
        
        // Test with empty title
        TaskBean.Task emptyTitleTask = createTestTask("", "Description", "High", "To Do");
        taskBean.setNewTask(emptyTitleTask);
        
        List<TaskBean.Task> tasksBefore = taskBean.getTasks();
        taskBean.addTask();
        List<TaskBean.Task> tasksAfter = taskBean.getTasks();
        
        assertEquals("Tasks count should not change with empty title", 
            tasksBefore.size(), tasksAfter.size());
        
        // Test with null title
        TaskBean.Task nullTitleTask = createTestTask(null, "Description", "High", "To Do");
        taskBean.setNewTask(nullTitleTask);
        
        tasksBefore = taskBean.getTasks();
        taskBean.addTask();
        tasksAfter = taskBean.getTasks();
        
        assertEquals("Tasks count should not change with null title", 
            tasksBefore.size(), tasksAfter.size());
        
        // Test with valid title
        TaskBean.Task validTask = createTestTask("Valid Task", "Description", "High", "To Do");
        taskBean.setNewTask(validTask);
        
        tasksBefore = taskBean.getTasks();
        taskBean.addTask();
        tasksAfter = taskBean.getTasks();
        
        assertEquals("Tasks count should increase with valid title", 
            tasksBefore.size() + 1, tasksAfter.size());
    }
    
    @Test
    public void testTaskDataIntegrity() {
        // Test that task data integrity is maintained through all operations
        
        // Create a task with all fields set
        TaskBean.Task originalTask = new TaskBean.Task();
        originalTask.setTitle("Data Integrity Test");
        originalTask.setDescription("Testing data integrity through CRUD operations");
        originalTask.setPriority("High");
        originalTask.setStatus("To Do");
        Date originalDate = new Date();
        originalTask.setCreatedDate(originalDate);
        
        taskBean.setNewTask(originalTask);
        taskBean.addTask();
        
        // Retrieve the task
        List<TaskBean.Task> tasks = taskBean.getTasks();
        TaskBean.Task retrievedTask = tasks.get(0);
        
        // Verify all data is preserved
        assertEquals("Title should be preserved", "Data Integrity Test", retrievedTask.getTitle());
        assertEquals("Description should be preserved", "Testing data integrity through CRUD operations", retrievedTask.getDescription());
        assertEquals("Priority should be preserved", "High", retrievedTask.getPriority());
        assertEquals("Status should be preserved", "To Do", retrievedTask.getStatus());
        assertNotNull("Created date should be preserved", retrievedTask.getCreatedDate());
        
        // Update the task
        TaskBean.Task updatedTask = new TaskBean.Task();
        updatedTask.setId(retrievedTask.getId());
        updatedTask.setTitle("Updated Data Integrity Test");
        updatedTask.setDescription("Updated description");
        updatedTask.setPriority("Medium");
        updatedTask.setStatus("In Progress");
        updatedTask.setCreatedDate(originalDate); // Preserve original date
        
        taskBean.setSelectedTask(updatedTask);
        taskBean.updateTask();
        
        // Retrieve the updated task
        taskBean.refreshTasks();
        List<TaskBean.Task> updatedTasks = taskBean.getTasks();
        TaskBean.Task finalTask = updatedTasks.get(0);
        
        // Verify updated data is preserved
        assertEquals("Updated title should be preserved", "Updated Data Integrity Test", finalTask.getTitle());
        assertEquals("Updated description should be preserved", "Updated description", finalTask.getDescription());
        assertEquals("Updated priority should be preserved", "Medium", finalTask.getPriority());
        assertEquals("Updated status should be preserved", "In Progress", finalTask.getStatus());
        assertNotNull("Created date should still be preserved", finalTask.getCreatedDate());
    }
    
    @Test
    public void testTaskRefresh() {
        // Test refreshing tasks from database
        
        // Add a task
        TaskBean.Task task = createTestTask("Refresh Test Task", "Description", "High", "To Do");
        taskBean.setNewTask(task);
        taskBean.addTask();
        
        // Verify task was added
        List<TaskBean.Task> tasksBeforeRefresh = taskBean.getTasks();
        assertEquals("Should have 1 task before refresh", 1, tasksBeforeRefresh.size());
        
        // Refresh tasks
        taskBean.refreshTasks();
        
        // Verify tasks are still there after refresh
        List<TaskBean.Task> tasksAfterRefresh = taskBean.getTasks();
        assertEquals("Should still have 1 task after refresh", 1, tasksAfterRefresh.size());
        assertEquals("Task title should still match", "Refresh Test Task", tasksAfterRefresh.get(0).getTitle());
    }
    
    @Test
    public void testMultipleTasksManagement() {
        // Test managing multiple tasks
        
        // Add first task
        TaskBean.Task task1 = createTestTask("Task 1", "Description 1", "High", "To Do");
        taskBean.setNewTask(task1);
        taskBean.addTask();
        
        // Add second task
        TaskBean.Task task2 = createTestTask("Task 2", "Description 2", "Medium", "In Progress");
        taskBean.setNewTask(task2);
        taskBean.addTask();
        
        // Add third task
        TaskBean.Task task3 = createTestTask("Task 3", "Description 3", "Low", "Done");
        taskBean.setNewTask(task3);
        taskBean.addTask();
        
        // Verify all tasks were added
        List<TaskBean.Task> allTasks = taskBean.getTasks();
        assertEquals("Should have 3 tasks", 3, allTasks.size());
        
        // Verify task properties
        TaskBean.Task firstTask = allTasks.get(0);
        TaskBean.Task secondTask = allTasks.get(1);
        TaskBean.Task thirdTask = allTasks.get(2);
        
        assertEquals("First task title should match", "Task 1", firstTask.getTitle());
        assertEquals("Second task title should match", "Task 2", secondTask.getTitle());
        assertEquals("Third task title should match", "Task 3", thirdTask.getTitle());
        
        assertEquals("First task priority should match", "High", firstTask.getPriority());
        assertEquals("Second task priority should match", "Medium", secondTask.getPriority());
        assertEquals("Third task priority should match", "Low", thirdTask.getPriority());
        
        assertEquals("First task status should match", "To Do", firstTask.getStatus());
        assertEquals("Second task status should match", "In Progress", secondTask.getStatus());
        assertEquals("Third task status should match", "Done", thirdTask.getStatus());
    }
    
    @Test
    public void testCompleteTaskLifecycle() {
        // Test the complete lifecycle of a task from creation to deletion
        
        // 1. Verify initial state
        List<TaskBean.Task> initialTasks = taskBean.getTasks();
        assertNotNull("Initial tasks list should not be null", initialTasks);
        assertTrue("Initial tasks list should be empty", initialTasks.isEmpty());
        
        // 2. Create a new task
        TaskBean.Task newTask = new TaskBean.Task();
        newTask.setTitle("Integration Test Task");
        newTask.setDescription("This is a test task for integration testing");
        newTask.setPriority("High");
        newTask.setStatus("To Do");
        newTask.setCreatedDate(new Date());
        
        taskBean.setNewTask(newTask);
        
        // 3. Add the task
        taskBean.addTask();
        
        // 4. Verify task was added
        List<TaskBean.Task> tasksAfterAdd = taskBean.getTasks();
        assertEquals("Should have 1 task after adding", 1, tasksAfterAdd.size());
        
        TaskBean.Task addedTask = tasksAfterAdd.get(0);
        assertNotNull("Added task should not be null", addedTask);
        assertEquals("Task title should match", "Integration Test Task", addedTask.getTitle());
        assertEquals("Task description should match", "This is a test task for integration testing", addedTask.getDescription());
        assertEquals("Task priority should match", "High", addedTask.getPriority());
        assertEquals("Task status should match", "To Do", addedTask.getStatus());
        assertNotNull("Task ID should be generated", addedTask.getId());
        
        // 5. Update the task
        TaskBean.Task taskToUpdate = new TaskBean.Task();
        taskToUpdate.setId(addedTask.getId());
        taskToUpdate.setTitle("Updated Integration Test Task");
        taskToUpdate.setDescription("This is an updated test task");
        taskToUpdate.setPriority("Medium");
        taskToUpdate.setStatus("In Progress");
        
        taskBean.setSelectedTask(taskToUpdate);
        taskBean.updateTask();
        
        // 6. Verify task was updated
        List<TaskBean.Task> tasksAfterUpdate = taskBean.getTasks();
        assertEquals("Should still have 1 task after update", 1, tasksAfterUpdate.size());
        
        TaskBean.Task updatedTask = tasksAfterUpdate.get(0);
        assertEquals("Task title should be updated", "Updated Integration Test Task", updatedTask.getTitle());
        assertEquals("Task description should be updated", "This is an updated test task", updatedTask.getDescription());
        assertEquals("Task priority should be updated", "Medium", updatedTask.getPriority());
        assertEquals("Task status should be updated", "In Progress", updatedTask.getStatus());
        
        // 7. Delete the task
        taskBean.deleteTask(updatedTask);
        
        // 8. Verify task was deleted
        List<TaskBean.Task> tasksAfterDelete = taskBean.getTasks();
        assertTrue("Tasks list should be empty after deletion", tasksAfterDelete.isEmpty());
    }
    
    /**
     * Helper method to create a test task
     */
    private TaskBean.Task createTestTask(String title, String description, String priority, String status) {
        TaskBean.Task task = new TaskBean.Task();
        task.setTitle(title);
        task.setDescription(description);
        task.setPriority(priority);
        task.setStatus(status);
        task.setCreatedDate(new Date());
        return task;
    }
}
