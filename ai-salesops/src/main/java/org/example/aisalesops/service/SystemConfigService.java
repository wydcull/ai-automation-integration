package org.example.aisalesops.service;

import org.example.aisalesops.entity.SystemConfig;
import org.example.aisalesops.repository.SystemConfigRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SystemConfigService {

    private final SystemConfigRepository systemConfigRepository;


    public SystemConfigService(
            SystemConfigRepository systemConfigRepository
    ) {
        this.systemConfigRepository = systemConfigRepository;
    }


    // Create System Config
    public SystemConfig createSystemConfig(
            SystemConfig systemConfig
    ) {

        return systemConfigRepository.save(systemConfig);
    }


    // Get All System Configs
    public List<SystemConfig> getAllSystemConfigs() {

        return systemConfigRepository.findAll();
    }


    // Get System Config By ID
    public Optional<SystemConfig> getSystemConfigById(
            Long id
    ) {

        return systemConfigRepository.findById(id);
    }


    // Full Update - PUT
    public SystemConfig updateSystemConfig(
            Long id,
            SystemConfig updatedConfig
    ) {

        SystemConfig existingConfig =
                systemConfigRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "System Config not found with id: "
                                                + id
                                )
                        );


        existingConfig.setKey(updatedConfig.getKey());
        existingConfig.setValue(updatedConfig.getValue());


        return systemConfigRepository.save(existingConfig);
    }


    // Partial Update - PATCH
    public SystemConfig partialUpdateSystemConfig(
            Long id,
            SystemConfig updatedConfig
    ) {

        SystemConfig existingConfig =
                systemConfigRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "System Config not found with id: "
                                                + id
                                )
                        );


        // Update key only if provided
        if (updatedConfig.getKey() != null) {
            existingConfig.setKey(updatedConfig.getKey());
        }


        // Update value only if provided
        if (updatedConfig.getValue() != null) {
            existingConfig.setValue(updatedConfig.getValue());
        }


        return systemConfigRepository.save(existingConfig);
    }


    // Delete System Config
    public void deleteSystemConfig(Long id) {

        SystemConfig existingConfig =
                systemConfigRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "System Config not found with id: "
                                                + id
                                )
                        );


        systemConfigRepository.delete(existingConfig);
    }
}