package org.example.aisalesops.controller;

import org.example.aisalesops.entity.Territory;
import org.example.aisalesops.service.TerritoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/territories")
public class TerritoryController {

    private final TerritoryService territoryService;

    public TerritoryController(TerritoryService territoryService) {
        this.territoryService = territoryService;
    }


    // Create Territory
    @PostMapping
    public Territory createTerritory(@RequestBody Territory territory) {
        return territoryService.createTerritory(territory);
    }


    // Get All Territories
    @GetMapping
    public List<Territory> getAllTerritories() {
        return territoryService.getAllTerritories();
    }


    // Get Territory By ID
    @GetMapping("/{id}")
    public ResponseEntity<Territory> getTerritoryById(
            @PathVariable Long id
    ) {

        return territoryService.getTerritoryById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    // Update Territory
    @PutMapping("/{id}")
    public Territory updateTerritory(
            @PathVariable Long id,
            @RequestBody Territory territory
    ) {

        return territoryService.updateTerritory(id, territory);
    }

    @PatchMapping("/{id}")
    public Territory updateTerritoryPartially(
            @PathVariable Long id,
            @RequestBody Territory territory
    ) {
        return territoryService.updateTerritory(id, territory);
    }


    // Delete Territory
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTerritory(
            @PathVariable Long id
    ) {

        territoryService.deleteTerritory(id);

        return ResponseEntity.ok(
                "Territory deleted successfully"
        );
    }
}