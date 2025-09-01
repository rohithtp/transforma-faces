package org.apache.myfaces.transforma.dao;

import org.apache.myfaces.transforma.beans.TaskBean;
import org.apache.myfaces.transforma.utils.DatabaseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Task entities, handling all database operations.
 * 
 * @author Transforma-Faces Team
 * @version 1.0.0
 */
public class TaskDAO implements Serializable {
    
    private static final Logger logger = LoggerFactory.getLogger(TaskDAO.class);
    
    /**
     * Retrieve all tasks from the database.
     * 
     * @return list of all tasks
     */
    public List<TaskBean.Task> getAllTasks() {
        List<TaskBean.Task> tasks = new ArrayList<>();
        String sql = "SELECT id, title, description, priority, status, created_date, updated_date FROM tasks ORDER BY id";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                TaskBean.Task task = new TaskBean.Task();
                task.setId(rs.getLong("id"));
                task.setTitle(rs.getString("title"));
                task.setDescription(rs.getString("description"));
                task.setPriority(rs.getString("priority"));
                task.setStatus(rs.getString("status"));
                
                // Parse date strings
                String createdDateStr = rs.getString("created_date");
                if (createdDateStr != null) {
                    task.setCreatedDate(parseSQLiteDateTime(createdDateStr));
                }
                
                tasks.add(task);
            }
            
            logger.debug("Retrieved {} tasks from database", tasks.size());
            
        } catch (SQLException e) {
            logger.error("Failed to retrieve tasks", e);
            throw new RuntimeException("Database operation failed", e);
        }
        
        return tasks;
    }
    
    /**
     * Insert a new task into the database.
     * 
     * @param task the task to insert
     * @return the generated ID of the new task
     */
    public Long insertTask(TaskBean.Task task) {
        String sql = "INSERT INTO tasks (title, description, priority, status, created_date) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, task.getTitle());
            stmt.setString(2, task.getDescription());
            stmt.setString(3, task.getPriority());
            stmt.setString(4, task.getStatus());
            stmt.setString(5, formatSQLiteDateTime(task.getCreatedDate()));
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating task failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    Long id = generatedKeys.getLong(1);
                    logger.info("Created new task with ID: {}", id);
                    return id;
                } else {
                    throw new SQLException("Creating task failed, no ID obtained.");
                }
            }
            
        } catch (SQLException e) {
            logger.error("Failed to insert task", e);
            throw new RuntimeException("Database operation failed", e);
        }
    }
    
    /**
     * Update an existing task in the database.
     * 
     * @param task the task to update
     * @return true if update was successful
     */
    public boolean updateTask(TaskBean.Task task) {
        String sql = "UPDATE tasks SET title = ?, description = ?, priority = ?, status = ?, updated_date = ? WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, task.getTitle());
            stmt.setString(2, task.getDescription());
            stmt.setString(3, task.getPriority());
            stmt.setString(4, task.getStatus());
            stmt.setString(5, formatSQLiteDateTime(new java.util.Date()));
            stmt.setLong(6, task.getId());
            
            int affectedRows = stmt.executeUpdate();
            boolean success = affectedRows > 0;
            
            if (success) {
                logger.info("Updated task with ID: {}", task.getId());
            } else {
                logger.warn("No task found with ID: {}", task.getId());
            }
            
            return success;
            
        } catch (SQLException e) {
            logger.error("Failed to update task", e);
            throw new RuntimeException("Database operation failed", e);
        }
    }
    
    /**
     * Delete a task from the database.
     * 
     * @param taskId the ID of the task to delete
     * @return true if deletion was successful
     */
    public boolean deleteTask(Long taskId) {
        String sql = "DELETE FROM tasks WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, taskId);
            
            int affectedRows = stmt.executeUpdate();
            boolean success = affectedRows > 0;
            
            if (success) {
                logger.info("Deleted task with ID: {}", taskId);
            } else {
                logger.warn("No task found with ID: {}", taskId);
            }
            
            return success;
            
        } catch (SQLException e) {
            logger.error("Failed to delete task", e);
            throw new RuntimeException("Database operation failed", e);
        }
    }
    
    /**
     * Get a task by its ID.
     * 
     * @param taskId the task ID
     * @return the task or null if not found
     */
    public TaskBean.Task getTaskById(Long taskId) {
        String sql = "SELECT id, title, description, priority, status, created_date, updated_date FROM tasks WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, taskId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    TaskBean.Task task = new TaskBean.Task();
                    task.setId(rs.getLong("id"));
                    task.setTitle(rs.getString("title"));
                    task.setDescription(rs.getString("description"));
                    task.setPriority(rs.getString("priority"));
                    task.setStatus(rs.getString("status"));
                    
                    String createdDateStr = rs.getString("created_date");
                    if (createdDateStr != null) {
                        task.setCreatedDate(parseSQLiteDateTime(createdDateStr));
                    }
                    
                    return task;
                }
            }
            
        } catch (SQLException e) {
            logger.error("Failed to retrieve task by ID: {}", taskId, e);
            throw new RuntimeException("Database operation failed", e);
        }
        
        return null;
    }
    
    /**
     * Format a Java Date to SQLite datetime string.
     * 
     * @param date the Java Date
     * @return SQLite datetime string
     */
    private String formatSQLiteDateTime(java.util.Date date) {
        if (date == null) {
            return null;
        }
        
        // Use SQLite's datetime function for current time
        if (date.getTime() == System.currentTimeMillis()) {
            return "datetime('now')";
        }
        
        // Format as ISO string for other dates
        return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }
    
    /**
     * Parse a SQLite datetime string to Java Date.
     * 
     * @param dateTimeStr the SQLite datetime string
     * @return Java Date object
     */
    private java.util.Date parseSQLiteDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.trim().isEmpty()) {
            return null;
        }
        
        try {
            // Try to parse ISO format first
            return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateTimeStr);
        } catch (java.text.ParseException e) {
            try {
                // Try to parse just the date
                return new java.text.SimpleDateFormat("yyyy-MM-dd").parse(dateTimeStr);
            } catch (java.text.ParseException e2) {
                logger.warn("Could not parse date: {}", dateTimeStr);
                return new java.util.Date(); // Return current date as fallback
            }
        }
    }
} 