package org.example.aisalesops.repository;

import org.example.aisalesops.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}