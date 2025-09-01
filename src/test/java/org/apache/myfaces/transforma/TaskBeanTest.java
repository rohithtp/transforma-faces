package org.apache.myfaces.transforma;

import org.apache.myfaces.transforma.beans.TaskBean;
import org.apache.myfaces.transforma.dao.TaskDAO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test class for TaskBean CRUD operations.
 * 
 * @author Transforma-Faces Team
 * @version 1.0.0
 */
@RunWith(MockitoJUnitRunner.class)
public class TaskBeanTest {
    
    @Mock
    private TaskDAO mockTaskDAO;
    
    private TaskBean taskBean;
    
    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        taskBean = new TaskBean();
        
        // Use reflection to inject the mock DAO
        try {
            java.lang.reflect.Field daoField = TaskBean.class.getDeclaredField("taskDAO");
            daoField.setAccessible(true);
            daoField.set(taskBean, mockTaskDAO);
            
            // Also set tasks to null to force initialization
            java.lang.reflect.Field tasksField = TaskBean.class.getDeclaredField("tasks");
            tasksField.setAccessible(true);
            tasksField.set(taskBean, null);
        } catch (Exception e) {
            fail("Failed to inject mock DAO: " + e.getMessage());
        }
    }
    
    @Test
    public void testTaskBeanCreation() {
        assertNotNull("TaskBean should not be null", taskBean);
        assertNotNull("New task should not be null", taskBean.getNewTask());
        assertEquals("New task priority should be Medium", "Medium", taskBean.getNewTask().getPriority());
        assertEquals("New task status should be To Do", "To Do", taskBean.getNewTask().getStatus());
    }
    
    @Test
    public void testGetTasksWhenEmpty() {
        // Mock empty task list
        when(mockTaskDAO.getAllTasks()).thenReturn(new ArrayList<>());
        
        List<TaskBean.Task> tasks = taskBean.getTasks();
        
        assertNotNull("Tasks list should not be null", tasks);
        assertTrue("Tasks list should be empty", tasks.isEmpty());
        verify(mockTaskDAO, times(1)).getAllTasks();
    }
    
    @Test
    public void testGetTasksWithData() {
        // Create test tasks
        List<TaskBean.Task> testTasks = createTestTasks();
        when(mockTaskDAO.getAllTasks()).thenReturn(testTasks);
        
        List<TaskBean.Task> tasks = taskBean.getTasks();
        
        assertNotNull("Tasks list should not be null", tasks);
        assertEquals("Should return 3 tasks", 3, tasks.size());
        assertEquals("First task title should match", "Test Task 1", tasks.get(0).getTitle());
        verify(mockTaskDAO, times(1)).getAllTasks();
    }
    
    @Test
    public void testAddTaskSuccess() {
        // Setup
        TaskBean.Task newTask = new TaskBean.Task();
        newTask.setTitle("New Test Task");
        newTask.setDescription("Test Description");
        newTask.setPriority("High");
        newTask.setStatus("To Do");
        
        when(mockTaskDAO.insertTask(any(TaskBean.Task.class))).thenReturn(1L);
        when(mockTaskDAO.getAllTasks()).thenReturn(createTestTasks());
        
        // Set the new task
        taskBean.setNewTask(newTask);
        
        // Execute
        taskBean.addTask();
        
        // Verify
        verify(mockTaskDAO, times(1)).insertTask(any(TaskBean.Task.class));
        verify(mockTaskDAO, times(1)).getAllTasks();
        
        // Verify new task is reset
        assertNotNull("New task should be reset", taskBean.getNewTask());
        assertNull("New task title should be null after reset", taskBean.getNewTask().getTitle());
    }
    
    @Test
    public void testAddTaskWithEmptyTitle() {
        // Setup
        TaskBean.Task newTask = new TaskBean.Task();
        newTask.setTitle(""); // Empty title
        newTask.setDescription("Test Description");
        
        taskBean.setNewTask(newTask);
        
        // Execute
        taskBean.addTask();
        
        // Verify - should not call DAO methods
        verify(mockTaskDAO, never()).insertTask(any(TaskBean.Task.class));
        verify(mockTaskDAO, never()).getAllTasks();
    }
    
    @Test
    public void testAddTaskWithNullTitle() {
        // Setup
        TaskBean.Task newTask = new TaskBean.Task();
        newTask.setTitle(null); // Null title
        newTask.setDescription("Test Description");
        
        taskBean.setNewTask(newTask);
        
        // Execute
        taskBean.addTask();
        
        // Verify - should not call DAO methods
        verify(mockTaskDAO, never()).insertTask(any(TaskBean.Task.class));
        verify(mockTaskDAO, never()).getAllTasks();
    }
    
    @Test
    public void testDeleteTaskSuccess() {
        // Setup
        TaskBean.Task taskToDelete = new TaskBean.Task();
        taskToDelete.setId(1L);
        taskToDelete.setTitle("Task to Delete");
        
        when(mockTaskDAO.deleteTask(1L)).thenReturn(true);
        when(mockTaskDAO.getAllTasks()).thenReturn(createTestTasks());
        
        // Execute
        taskBean.deleteTask(taskToDelete);
        
        // Verify
        verify(mockTaskDAO, times(1)).deleteTask(1L);
        verify(mockTaskDAO, times(1)).getAllTasks();
    }
    
    @Test
    public void testDeleteTaskFailure() {
        // Setup
        TaskBean.Task taskToDelete = new TaskBean.Task();
        taskToDelete.setId(1L);
        taskToDelete.setTitle("Task to Delete");
        
        when(mockTaskDAO.deleteTask(1L)).thenReturn(false);
        
        // Execute
        taskBean.deleteTask(taskToDelete);
        
        // Verify
        verify(mockTaskDAO, times(1)).deleteTask(1L);
        verify(mockTaskDAO, never()).getAllTasks(); // Should not reload on failure
    }
    
    @Test
    public void testDeleteTaskWithNullId() {
        // Setup
        TaskBean.Task taskToDelete = new TaskBean.Task();
        taskToDelete.setId(null);
        taskToDelete.setTitle("Task to Delete");
        
        // Execute
        taskBean.deleteTask(taskToDelete);
        
        // Verify - should not call DAO methods
        verify(mockTaskDAO, never()).deleteTask(any(Long.class));
        verify(mockTaskDAO, never()).getAllTasks();
    }
    
    @Test
    public void testUpdateTaskSuccess() {
        // Setup
        TaskBean.Task taskToUpdate = new TaskBean.Task();
        taskToUpdate.setId(1L);
        taskToUpdate.setTitle("Updated Task");
        taskToUpdate.setDescription("Updated Description");
        taskToUpdate.setPriority("High");
        taskToUpdate.setStatus("In Progress");
        
        taskBean.setSelectedTask(taskToUpdate);
        
        when(mockTaskDAO.updateTask(any(TaskBean.Task.class))).thenReturn(true);
        when(mockTaskDAO.getAllTasks()).thenReturn(createTestTasks());
        
        // Execute
        taskBean.updateTask();
        
        // Verify
        verify(mockTaskDAO, times(1)).updateTask(taskToUpdate);
        verify(mockTaskDAO, times(1)).getAllTasks();
    }
    
    @Test
    public void testUpdateTaskFailure() {
        // Setup
        TaskBean.Task taskToUpdate = new TaskBean.Task();
        taskToUpdate.setId(1L);
        taskToUpdate.setTitle("Updated Task");
        
        taskBean.setSelectedTask(taskToUpdate);
        
        when(mockTaskDAO.updateTask(any(TaskBean.Task.class))).thenReturn(false);
        
        // Execute
        taskBean.updateTask();
        
        // Verify
        verify(mockTaskDAO, times(1)).updateTask(taskToUpdate);
        verify(mockTaskDAO, never()).getAllTasks(); // Should not reload on failure
    }
    
    @Test
    public void testUpdateTaskWithNullSelectedTask() {
        // Setup
        taskBean.setSelectedTask(null);
        
        // Execute
        taskBean.updateTask();
        
        // Verify - should not call DAO methods
        verify(mockTaskDAO, never()).updateTask(any(TaskBean.Task.class));
        verify(mockTaskDAO, never()).getAllTasks();
    }
    
    @Test
    public void testRefreshTasks() {
        // Setup
        when(mockTaskDAO.getAllTasks()).thenReturn(createTestTasks());
        
        // Execute
        taskBean.refreshTasks();
        
        // Verify
        verify(mockTaskDAO, times(1)).getAllTasks();
    }
    
    @Test
    public void testTaskInnerClass() {
        // Test Task inner class creation and properties
        TaskBean.Task task = new TaskBean.Task();
        
        // Test default values
        assertEquals("Default priority should be Medium", "Medium", task.getPriority());
        assertEquals("Default status should be To Do", "To Do", task.getStatus());
        
        // Test setters and getters
        task.setId(1L);
        task.setTitle("Test Task");
        task.setDescription("Test Description");
        task.setPriority("High");
        task.setStatus("In Progress");
        Date testDate = new Date();
        task.setCreatedDate(testDate);
        
        assertEquals("ID should be set", Long.valueOf(1L), task.getId());
        assertEquals("Title should be set", "Test Task", task.getTitle());
        assertEquals("Description should be set", "Test Description", task.getDescription());
        assertEquals("Priority should be set", "High", task.getPriority());
        assertEquals("Status should be set", "In Progress", task.getStatus());
        assertEquals("Created date should be set", testDate, task.getCreatedDate());
    }
    
    @Test
    public void testTaskConstructorWithParameters() {
        // Test Task constructor with all parameters
        Date testDate = new Date();
        TaskBean.Task task = new TaskBean.Task(1L, "Test Task", "Test Description", "High", "In Progress", testDate);
        
        assertEquals("ID should be set", Long.valueOf(1L), task.getId());
        assertEquals("Title should be set", "Test Task", task.getTitle());
        assertEquals("Description should be set", "Test Description", task.getDescription());
        assertEquals("Priority should be set", "High", task.getPriority());
        assertEquals("Status should be set", "In Progress", task.getStatus());
        assertEquals("Created date should be set", testDate, task.getCreatedDate());
    }
    
    @Test
    public void testGetTasksWithNullInitialization() {
        // Test the ensureTasksInitialized method when tasks is null
        when(mockTaskDAO.getAllTasks()).thenReturn(createTestTasks());
        
        // Force tasks to be null by calling getTasks directly
        List<TaskBean.Task> tasks = taskBean.getTasks();
        
        assertNotNull("Tasks should not be null after initialization", tasks);
        assertEquals("Should return 3 tasks", 3, tasks.size());
        verify(mockTaskDAO, times(1)).getAllTasks();
    }
    
    /**
     * Helper method to create test tasks
     */
    private List<TaskBean.Task> createTestTasks() {
        List<TaskBean.Task> tasks = new ArrayList<>();
        
        TaskBean.Task task1 = new TaskBean.Task(1L, "Test Task 1", "Description 1", "High", "To Do", new Date());
        TaskBean.Task task2 = new TaskBean.Task(2L, "Test Task 2", "Description 2", "Medium", "In Progress", new Date());
        TaskBean.Task task3 = new TaskBean.Task(3L, "Test Task 3", "Description 3", "Low", "Done", new Date());
        
        tasks.add(task1);
        tasks.add(task2);
        tasks.add(task3);
        
        return tasks;
    }
}
