package org.example.aisalesops.controller;

import org.example.aisalesops.entity.AssignmentRule;
import org.example.aisalesops.service.AssignmentRuleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assignment-rules")
public class AssignmentRuleController {

    private final AssignmentRuleService assignmentRuleService;


    public AssignmentRuleController(
            AssignmentRuleService assignmentRuleService
    ) {
        this.assignmentRuleService = assignmentRuleService;
    }


    // Create Assignment Rule
    @PostMapping
    public AssignmentRule createAssignmentRule(
            @RequestBody AssignmentRule assignmentRule
    ) {

        return assignmentRuleService
                .createAssignmentRule(assignmentRule);
    }


    // Get All Assignment Rules
    @GetMapping
    public List<AssignmentRule> getAllAssignmentRules() {

        return assignmentRuleService
                .getAllAssignmentRules();
    }


    // Get Assignment Rule By ID
    @GetMapping("/{id}")
    public ResponseEntity<AssignmentRule>
    getAssignmentRuleById(@PathVariable Long id) {

        return assignmentRuleService
                .getAssignmentRuleById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    // Full Update
    @PutMapping("/{id}")
    public AssignmentRule updateAssignmentRule(
            @PathVariable Long id,
            @RequestBody AssignmentRule assignmentRule
    ) {

        return assignmentRuleService
                .updateAssignmentRule(id, assignmentRule);
    }


    // Partial Update
    @PatchMapping("/{id}")
    public AssignmentRule partialUpdateAssignmentRule(
            @PathVariable Long id,
            @RequestBody AssignmentRule assignmentRule
    ) {

        return assignmentRuleService
                .partialUpdateAssignmentRule(
                        id,
                        assignmentRule
                );
    }


    // Delete Assignment Rule
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAssignmentRule(
            @PathVariable Long id
    ) {

        assignmentRuleService.deleteAssignmentRule(id);

        return ResponseEntity.ok(
                "Assignment Rule deleted successfully"
        );
    }
}