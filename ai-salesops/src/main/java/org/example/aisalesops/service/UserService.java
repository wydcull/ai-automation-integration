package org.example.aisalesops.service;

import org.example.aisalesops.entity.Role;
import org.example.aisalesops.entity.Territory;
import org.example.aisalesops.entity.User;
import org.example.aisalesops.repository.RoleRepository;
import org.example.aisalesops.repository.TerritoryRepository;
import org.example.aisalesops.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final TerritoryRepository territoryRepository;


    public UserService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            TerritoryRepository territoryRepository
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.territoryRepository = territoryRepository;
    }


    // Create User
    public User createUser(User user) {

        Role role = roleRepository.findById(user.getRole().getId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Role not found with id: "
                                        + user.getRole().getId()
                        )
                );

        user.setRole(role);


        // Territory is optional
        if (user.getTerritory() != null &&
                user.getTerritory().getId() != null) {

            Territory territory = territoryRepository
                    .findById(user.getTerritory().getId())
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Territory not found with id: "
                                            + user.getTerritory().getId()
                            )
                    );

            user.setTerritory(territory);
        }

        return userRepository.save(user);
    }


    // Get All Users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }


    // Get User By ID
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }


    // Full Update - PUT
    public User updateUser(Long id, User updatedUser) {

        User existingUser = userRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found with id: " + id
                        )
                );


        existingUser.setFullName(updatedUser.getFullName());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setPasswordHash(updatedUser.getPasswordHash());
        existingUser.setActive(updatedUser.getActive());


        Role role = roleRepository
                .findById(updatedUser.getRole().getId())
                .orElseThrow(() ->
                        new RuntimeException("Role not found")
                );

        existingUser.setRole(role);


        if (updatedUser.getTerritory() != null &&
                updatedUser.getTerritory().getId() != null) {

            Territory territory = territoryRepository
                    .findById(updatedUser.getTerritory().getId())
                    .orElseThrow(() ->
                            new RuntimeException("Territory not found")
                    );

            existingUser.setTerritory(territory);

        } else {
            existingUser.setTerritory(null);
        }


        return userRepository.save(existingUser);
    }


    // Partial Update - PATCH
    public User partialUpdateUser(Long id, User updatedUser) {

        User existingUser = userRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found with id: " + id
                        )
                );


        if (updatedUser.getFullName() != null) {
            existingUser.setFullName(updatedUser.getFullName());
        }


        if (updatedUser.getEmail() != null) {
            existingUser.setEmail(updatedUser.getEmail());
        }


        if (updatedUser.getPasswordHash() != null) {
            existingUser.setPasswordHash(updatedUser.getPasswordHash());
        }


        if (updatedUser.getActive() != null) {
            existingUser.setActive(updatedUser.getActive());
        }


        // Update Role only if provided
        if (updatedUser.getRole() != null &&
                updatedUser.getRole().getId() != null) {

            Role role = roleRepository
                    .findById(updatedUser.getRole().getId())
                    .orElseThrow(() ->
                            new RuntimeException("Role not found")
                    );

            existingUser.setRole(role);
        }


        // Update Territory only if provided
        if (updatedUser.getTerritory() != null &&
                updatedUser.getTerritory().getId() != null) {

            Territory territory = territoryRepository
                    .findById(updatedUser.getTerritory().getId())
                    .orElseThrow(() ->
                            new RuntimeException("Territory not found")
                    );

            existingUser.setTerritory(territory);
        }


        return userRepository.save(existingUser);
    }


    // Delete User
    public void deleteUser(Long id) {

        User existingUser = userRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found with id: " + id
                        )
                );

        userRepository.delete(existingUser);
    }
}