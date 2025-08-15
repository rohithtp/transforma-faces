package org.apache.myfaces.transforma.beans;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Managed bean for handling task management in the Transforma-Faces application.
 * 
 * @author Transforma-Faces Team
 * @version 1.0.0
 */
@ManagedBean
@SessionScoped
public class TaskBean implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private List<Task> tasks;
    private Task newTask;
    private Task selectedTask;
    
    /**
     * Default constructor to initialize tasks list.
     */
    public TaskBean() {
        this.tasks = new ArrayList<>();
        this.newTask = new Task();
        initializeSampleTasks();
    }
    
    /**
     * Initialize with some sample tasks for demonstration.
     */
    private void initializeSampleTasks() {
        tasks.add(new Task(1L, "Complete project setup", "Set up the development environment and configure dependencies", "High", "In Progress", new Date()));
        tasks.add(new Task(2L, "Design user interface", "Create wireframes and mockups for the main application", "Medium", "To Do", new Date()));
        tasks.add(new Task(3L, "Implement core functionality", "Develop the main business logic and data models", "High", "To Do", new Date()));
        tasks.add(new Task(4L, "Write unit tests", "Create comprehensive test coverage for all components", "Medium", "To Do", new Date()));
        tasks.add(new Task(5L, "Documentation", "Write user and developer documentation", "Low", "To Do", new Date()));
    }
    
    /**
     * Add a new task to the list.
     */
    public void addTask() {
        if (newTask != null && newTask.getTitle() != null && !newTask.getTitle().trim().isEmpty()) {
            newTask.setId(generateNextId());
            newTask.setCreatedDate(new Date());
            tasks.add(newTask);
            newTask = new Task();
        }
    }
    
    /**
     * Delete a task from the list.
     */
    public void deleteTask(Task task) {
        if (task != null) {
            tasks.remove(task);
        }
    }
    
    /**
     * Update an existing task.
     */
    public void updateTask() {
        // The task is already updated in the list since it's bound to the UI
        // This method can be used for additional validation or business logic
    }
    
    /**
     * Generate the next available ID for new tasks.
     */
    private Long generateNextId() {
        return tasks.stream()
                   .mapToLong(Task::getId)
                   .max()
                   .orElse(0) + 1;
    }
    
    // Getters and Setters
    public List<Task> getTasks() {
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