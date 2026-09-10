package org.example.aisalesops.service;

import org.example.aisalesops.entity.Role;
import org.example.aisalesops.repository.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }


    // Create Role
    public Role createRole(Role role) {
        return roleRepository.save(role);
    }


    // Get All Roles
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }


    // Get Role By ID
    public Optional<Role> getRoleById(Long id) {
        return roleRepository.findById(id);
    }


    // Update Role
    public Role updateRole(Long id, Role updatedRole) {

        Role existingRole = roleRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Role not found with id: " + id)
                );

        existingRole.setCode(updatedRole.getCode());
        existingRole.setName(updatedRole.getName());

        return roleRepository.save(existingRole);
    }


    // Delete Role
    public void deleteRole(Long id) {

        Role existingRole = roleRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Role not found with id: " + id)
                );

        roleRepository.delete(existingRole);
    }
}