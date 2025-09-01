package org.apache.myfaces.transforma.beans;

import org.apache.myfaces.transforma.dao.TaskDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Managed bean for handling task management in the Transforma-Faces application.
 * Now uses SQLite database for persistence.
 * 
 * @author Transforma-Faces Team
 * @version 1.0.0
 */
public class TaskBean implements Serializable {
    
    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(TaskBean.class);
    
    private List<Task> tasks;
    private Task newTask;
    private Task selectedTask;
    private TaskDAO taskDAO;
    
    /**
     * Default constructor to initialize TaskDAO and load tasks.
     */
    public TaskBean() {
        this.taskDAO = new TaskDAO();
        this.newTask = new Task();
        loadTasksFromDatabase();
    }
    
    /**
     * Load tasks from the database.
     */
    private void loadTasksFromDatabase() {
        try {
            this.tasks = taskDAO.getAllTasks();
            logger.info("Loaded {} tasks from database", tasks.size());
        } catch (Exception e) {
            logger.error("Failed to load tasks from database", e);
            this.tasks = new ArrayList<>();
            // Fallback to empty list if database fails
        }
    }
    
    /**
     * Ensure tasks list is initialized.
     */
    private void ensureTasksInitialized() {
        if (this.tasks == null) {
            loadTasksFromDatabase();
        }
    }
    
    /**
     * Add a new task to the database.
     */
    public void addTask() {
        if (newTask != null && newTask.getTitle() != null && !newTask.getTitle().trim().isEmpty()) {
            try {
                newTask.setCreatedDate(new Date());
                Long newId = taskDAO.insertTask(newTask);
                newTask.setId(newId);
                
                // Reload tasks from database to get the latest data
                loadTasksFromDatabase();
                
                // Reset the form
                newTask = new Task();
                
                logger.info("Successfully added new task with ID: {}", newId);
                
            } catch (Exception e) {
                logger.error("Failed to add task", e);
                // You could add a faces message here to show the error to the user
            }
        }
    }
    
    /**
     * Delete a task from the database.
     */
    public void deleteTask(Task task) {
        if (task != null && task.getId() != null) {
            try {
                boolean deleted = taskDAO.deleteTask(task.getId());
                if (deleted) {
                    // Reload tasks from database
                    loadTasksFromDatabase();
                    logger.info("Successfully deleted task with ID: {}", task.getId());
                } else {
                    logger.warn("Failed to delete task with ID: {}", task.getId());
                }
            } catch (Exception e) {
                logger.error("Failed to delete task", e);
            }
        }
    }
    
    /**
     * Update an existing task in the database.
     */
    public void updateTask() {
        if (selectedTask != null && selectedTask.getId() != null) {
            try {
                boolean updated = taskDAO.updateTask(selectedTask);
                if (updated) {
                    // Reload tasks from database
                    loadTasksFromDatabase();
                    logger.info("Successfully updated task with ID: {}", selectedTask.getId());
                } else {
                    logger.warn("Failed to update task with ID: {}", selectedTask.getId());
                }
            } catch (Exception e) {
                logger.error("Failed to update task", e);
            }
        }
    }
    
    /**
     * Refresh the tasks list from the database.
     */
    public void refreshTasks() {
        loadTasksFromDatabase();
    }
    
    // Getters and Setters
    public List<Task> getTasks() {
        ensureTasksInitialized();
        return tasks;
    }
    
    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }
    
    public Task getNewTask() {
        return newTask;
    }
    
    public void setNewTask(Task newTask) {
        this.newTask = newTask;
    }
    
    public Task getSelectedTask() {
        return selectedTask;
    }
    
    public void setSelectedTask(Task selectedTask) {
        this.selectedTask = selectedTask;
    }
    
    /**
     * Inner class representing a Task entity.
     */
    public static class Task implements Serializable {
        private static final long serialVersionUID = 1L;
        
        private Long id;
        private String title;
        private String description;
        private String priority;
        private String status;
        private Date createdDate;
        
        public Task() {
            this.priority = "Medium";
            this.status = "To Do";
        }
        
        public Task(Long id, String title, String description, String priority, String status, Date createdDate) {
            this.id = id;
            this.title = title;
            this.description = description;
            this.priority = priority;
            this.status = status;
            this.createdDate = createdDate;
        }
        
        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public String getPriority() { return priority; }
        public void setPriority(String priority) { this.priority = priority; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public Date getCreatedDate() { return createdDate; }
        public void setCreatedDate(Date createdDate) { this.createdDate = createdDate; }
    }
} 