package org.example.aisalesops.service;

import org.example.aisalesops.entity.AssignmentRule;
import org.example.aisalesops.entity.Territory;
import org.example.aisalesops.entity.User;
import org.example.aisalesops.repository.AssignmentRuleRepository;
import org.example.aisalesops.repository.TerritoryRepository;
import org.example.aisalesops.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AssignmentRuleService {

    private final AssignmentRuleRepository assignmentRuleRepository;
    private final UserRepository userRepository;
    private final TerritoryRepository territoryRepository;


    public AssignmentRuleService(
            AssignmentRuleRepository assignmentRuleRepository,
            UserRepository userRepository,
            TerritoryRepository territoryRepository
    ) {
        this.assignmentRuleRepository = assignmentRuleRepository;
        this.userRepository = userRepository;
        this.territoryRepository = territoryRepository;
    }


    // Create Assignment Rule
    public AssignmentRule createAssignmentRule(
            AssignmentRule assignmentRule
    ) {

        // Check and assign User if provided
        if (assignmentRule.getAssigneeUser() != null &&
                assignmentRule.getAssigneeUser().getId() != null) {

            User user = userRepository
                    .findById(assignmentRule.getAssigneeUser().getId())
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "User not found with id: " +
                                            assignmentRule
                                                    .getAssigneeUser()
                                                    .getId()
                            )
                    );

            assignmentRule.setAssigneeUser(user);
        }


        // Check and assign Territory if provided
        if (assignmentRule.getTerritory() != null &&
                assignmentRule.getTerritory().getId() != null) {

            Territory territory = territoryRepository
                    .findById(assignmentRule.getTerritory().getId())
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Territory not found with id: " +
                                            assignmentRule
                                                    .getTerritory()
                                                    .getId()
                            )
                    );

            assignmentRule.setTerritory(territory);
        }


        return assignmentRuleRepository.save(assignmentRule);
    }


    // Get All Assignment Rules
    public List<AssignmentRule> getAllAssignmentRules() {
        return assignmentRuleRepository.findAll();
    }


    // Get Assignment Rule By ID
    public Optional<AssignmentRule> getAssignmentRuleById(
            Long id
    ) {
        return assignmentRuleRepository.findById(id);
    }


    // Full Update - PUT
    public AssignmentRule updateAssignmentRule(
            Long id,
            AssignmentRule updatedRule
    ) {

        AssignmentRule existingRule =
                assignmentRuleRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Assignment Rule not found with id: "
                                                + id
                                )
                        );


        existingRule.setName(updatedRule.getName());
        existingRule.setMatchType(updatedRule.getMatchType());
        existingRule.setMatchValue(updatedRule.getMatchValue());
        existingRule.setPriority(updatedRule.getPriority());
        existingRule.setActive(updatedRule.getActive());


        // Update User
        if (updatedRule.getAssigneeUser() != null &&
                updatedRule.getAssigneeUser().getId() != null) {

            User user = userRepository
                    .findById(updatedRule
                            .getAssigneeUser()
                            .getId())
                    .orElseThrow(() ->
                            new RuntimeException("User not found")
                    );

            existingRule.setAssigneeUser(user);

        } else {
            existingRule.setAssigneeUser(null);
        }


        // Update Territory
        if (updatedRule.getTerritory() != null &&
                updatedRule.getTerritory().getId() != null) {

            Territory territory = territoryRepository
                    .findById(updatedRule
                            .getTerritory()
                            .getId())
                    .orElseThrow(() ->
                            new RuntimeException("Territory not found")
                    );

            existingRule.setTerritory(territory);

        } else {
            existingRule.setTerritory(null);
        }


        return assignmentRuleRepository.save(existingRule);
    }


    // Partial Update - PATCH
    public AssignmentRule partialUpdateAssignmentRule(
            Long id,
            AssignmentRule updatedRule
    ) {

        AssignmentRule existingRule =
                assignmentRuleRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Assignment Rule not found with id: "
                                                + id
                                )
                        );


        if (updatedRule.getName() != null) {
            existingRule.setName(updatedRule.getName());
        }


        if (updatedRule.getMatchType() != null) {
            existingRule.setMatchType(updatedRule.getMatchType());
        }


        if (updatedRule.getMatchValue() != null) {
            existingRule.setMatchValue(updatedRule.getMatchValue());
        }


        if (updatedRule.getPriority() != null) {
            existingRule.setPriority(updatedRule.getPriority());
        }


        if (updatedRule.getActive() != null) {
            existingRule.setActive(updatedRule.getActive());
        }


        // Update User only if provided
        if (updatedRule.getAssigneeUser() != null &&
                updatedRule.getAssigneeUser().getId() != null) {

            User user = userRepository
                    .findById(updatedRule
                            .getAssigneeUser()
                            .getId())
                    .orElseThrow(() ->
                            new RuntimeException("User not found")
                    );

            existingRule.setAssigneeUser(user);
        }


        // Update Territory only if provided
        if (updatedRule.getTerritory() != null &&
                updatedRule.getTerritory().getId() != null) {

            Territory territory = territoryRepository
                    .findById(updatedRule
                            .getTerritory()
                            .getId())
                    .orElseThrow(() ->
                            new RuntimeException("Territory not found")
                    );

            existingRule.setTerritory(territory);
        }


        return assignmentRuleRepository.save(existingRule);
    }


    // Delete Assignment Rule
    public void deleteAssignmentRule(Long id) {

        AssignmentRule existingRule =
                assignmentRuleRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Assignment Rule not found with id: "
                                                + id
                                )
                        );

        assignmentRuleRepository.delete(existingRule);
    }
}