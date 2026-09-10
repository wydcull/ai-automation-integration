package org.example.aisalesops.service;

import org.example.aisalesops.entity.Territory;
import org.example.aisalesops.repository.TerritoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TerritoryService {

    private final TerritoryRepository territoryRepository;

    public TerritoryService(TerritoryRepository territoryRepository) {
        this.territoryRepository = territoryRepository;
    }


    // Create Territory
    public Territory createTerritory(Territory territory) {
        return territoryRepository.save(territory);
    }


    // Get All Territories
    public List<Territory> getAllTerritories() {
        return territoryRepository.findAll();
    }


    // Get Territory By ID
    public Optional<Territory> getTerritoryById(Long id) {
        return territoryRepository.findById(id);
    }


    /// Update Territory
    public Territory updateTerritory(Long id, Territory updatedTerritory) {

        Territory existingTerritory = territoryRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Territory not found with id: " + id
                        )
                );

        // Update name only if provided
        if (updatedTerritory.getName() != null) {
            existingTerritory.setName(updatedTerritory.getName());
        }

        // Update regionCode only if provided
        if (updatedTerritory.getRegionCode() != null) {
            existingTerritory.setRegionCode(updatedTerritory.getRegionCode());
        }

        // Update active only if provided
        if (updatedTerritory.getActive() != null) {
            existingTerritory.setActive(updatedTerritory.getActive());
        }

        return territoryRepository.save(existingTerritory);
    }


    // Delete Territory
    public void deleteTerritory(Long id) {

        Territory existingTerritory = territoryRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Territory not found with id: " + id
                        )
                );

        territoryRepository.delete(existingTerritory);
    }
}