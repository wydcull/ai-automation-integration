package org.example.aisalesops.controller;

import org.example.aisalesops.entity.Task;
import org.example.aisalesops.service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;


    // =========================================
    // CONSTRUCTOR
    // =========================================

    public TaskController(
            TaskService taskService
    ) {

        this.taskService = taskService;
    }


    // =========================================
    // CREATE TASK - POST
    // =========================================

    @PostMapping
    public Task createTask(
            @RequestBody Task task
    ) {

        return taskService.createTask(
                task
        );
    }


    // =========================================
    // GET ALL TASKS
    // =========================================

    @GetMapping
    public List<Task> getAllTasks() {

        return taskService.getAllTasks();
    }


    // =========================================
    // GET TASK BY ID
    // =========================================

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(
            @PathVariable Long id
    ) {

        return taskService
                .getTaskById(id)
                .map(ResponseEntity::ok)
                .orElse(
                        ResponseEntity.notFound().build()
                );
    }


    // =========================================
    // FULL UPDATE - PUT
    // =========================================

    @PutMapping("/{id}")
    public Task updateTask(
            @PathVariable Long id,
            @RequestBody Task task
    ) {

        return taskService.updateTask(
                id,
                task
        );
    }


    // =========================================
    // PARTIAL UPDATE - PATCH
    // =========================================

    @PatchMapping("/{id}")
    public Task partialUpdateTask(
            @PathVariable Long id,
            @RequestBody Task task
    ) {

        return taskService.partialUpdateTask(
                id,
                task
        );
    }


    // =========================================
    // DELETE TASK
    // =========================================

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTask(
            @PathVariable Long id
    ) {

        taskService.deleteTask(id);

        return ResponseEntity.ok(
                "Task deleted successfully"
        );
    }
}