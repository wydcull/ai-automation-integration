package org.example.aisalesops.repository;

import org.example.aisalesops.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository
        extends JpaRepository<Task, Long> {

}