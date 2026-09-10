package org.example.aisalesops.controller;

import org.example.aisalesops.entity.SystemConfig;
import org.example.aisalesops.service.SystemConfigService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/system-config")
public class SystemConfigController {

    private final SystemConfigService systemConfigService;


    public SystemConfigController(
            SystemConfigService systemConfigService
    ) {
        this.systemConfigService = systemConfigService;
    }


    // Create System Config
    @PostMapping
    public SystemConfig createSystemConfig(
            @RequestBody SystemConfig systemConfig
    ) {

        return systemConfigService
                .createSystemConfig(systemConfig);
    }


    // Get All System Configs
    @GetMapping
    public List<SystemConfig> getAllSystemConfigs() {

        return systemConfigService
                .getAllSystemConfigs();
    }


    // Get System Config By ID
    @GetMapping("/{id}")
    public ResponseEntity<SystemConfig> getSystemConfigById(
            @PathVariable Long id
    ) {

        return systemConfigService
                .getSystemConfigById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    // Full Update - PUT
    @PutMapping("/{id}")
    public SystemConfig updateSystemConfig(
            @PathVariable Long id,
            @RequestBody SystemConfig systemConfig
    ) {

        return systemConfigService
                .updateSystemConfig(id, systemConfig);
    }


    // Partial Update - PATCH
    @PatchMapping("/{id}")
    public SystemConfig partialUpdateSystemConfig(
            @PathVariable Long id,
            @RequestBody SystemConfig systemConfig
    ) {

        return systemConfigService
                .partialUpdateSystemConfig(
                        id,
                        systemConfig
                );
    }


    // Delete System Config
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSystemConfig(
            @PathVariable Long id
    ) {

        systemConfigService.deleteSystemConfig(id);

        return ResponseEntity.ok(
                "System Config deleted successfully"
        );
    }
}