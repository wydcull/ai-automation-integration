package org.example.aisalesops.service;

import org.example.aisalesops.entity.Task;
import org.example.aisalesops.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    private final TaskRepository taskRepository;


    // =========================================
    // CONSTRUCTOR
    // =========================================

    public TaskService(
            TaskRepository taskRepository
    ) {

        this.taskRepository = taskRepository;
    }


    // =========================================
    // CREATE TASK - POST
    // =========================================

    public Task createTask(
            Task task
    ) {

        return taskRepository.save(task);
    }


    // =========================================
    // GET ALL TASKS - GET
    // =========================================

    public List<Task> getAllTasks() {

        return taskRepository.findAll();
    }


    // =========================================
    // GET TASK BY ID - GET
    // =========================================

    public Optional<Task> getTaskById(
            Long id
    ) {

        return taskRepository.findById(id);
    }


    // =========================================
    // FULL UPDATE - PUT
    // =========================================

    @Transactional
    public Task updateTask(
            Long id,
            Task updatedTask
    ) {

        Task existingTask =
                taskRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Task not found with id: " + id
                                )
                        );


        // Update Lead
        existingTask.setLead(
                updatedTask.getLead()
        );


        // Update Title
        existingTask.setTitle(
                updatedTask.getTitle()
        );


        // Update Priority
        existingTask.setPriority(
                updatedTask.getPriority()
        );


        // Update Status
        existingTask.setStatus(
                updatedTask.getStatus()
        );


        // Update Due Date
        existingTask.setDueAt(
                updatedTask.getDueAt()
        );


        // Update Assigned User
        existingTask.setAssignedUser(
                updatedTask.getAssignedUser()
        );


        // Update Reason
        existingTask.setReason(
                updatedTask.getReason()
        );


        // Update Completed At
        existingTask.setCompletedAt(
                updatedTask.getCompletedAt()
        );


        // createdAt is intentionally not updated

        return taskRepository.save(
                existingTask
        );
    }


    // =========================================
    // PARTIAL UPDATE - PATCH
    // =========================================

    @Transactional
    public Task partialUpdateTask(
            Long id,
            Task updatedTask
    ) {

        Task existingTask =
                taskRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Task not found with id: " + id
                                )
                        );


        // Update Lead
        if (updatedTask.getLead() != null) {

            existingTask.setLead(
                    updatedTask.getLead()
            );
        }


        // Update Title
        if (updatedTask.getTitle() != null) {

            existingTask.setTitle(
                    updatedTask.getTitle()
            );
        }


        // Update Priority
        if (updatedTask.getPriority() != null) {

            existingTask.setPriority(
                    updatedTask.getPriority()
            );
        }


        // Update Status
        if (updatedTask.getStatus() != null) {

            existingTask.setStatus(
                    updatedTask.getStatus()
            );
        }


        // Update Due Date
        if (updatedTask.getDueAt() != null) {

            existingTask.setDueAt(
                    updatedTask.getDueAt()
            );
        }


        // Update Assigned User
        if (updatedTask.getAssignedUser() != null) {

            existingTask.setAssignedUser(
                    updatedTask.getAssignedUser()
            );
        }


        // Update Reason
        if (updatedTask.getReason() != null) {

            existingTask.setReason(
                    updatedTask.getReason()
            );
        }


        // Update Completed At
        if (updatedTask.getCompletedAt() != null) {

            existingTask.setCompletedAt(
                    updatedTask.getCompletedAt()
            );
        }


        // createdAt is not updated

        return taskRepository.save(
                existingTask
        );
    }


    // =========================================
    // DELETE TASK - DELETE
    // =========================================

    public void deleteTask(
            Long id
    ) {

        Task existingTask =
                taskRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Task not found with id: " + id
                                )
                        );


        taskRepository.delete(
                existingTask
        );
    }
}