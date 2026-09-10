package org.example.aisalesops.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "assignment_rules")
public class AssignmentRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false, length = 120)
    private String name;


    @Column(name = "match_type", nullable = false, length = 32)
    private String matchType;


    @Column(name = "match_value", nullable = false, length = 120)
    private String matchValue;


    @ManyToOne
    @JoinColumn(name = "assignee_user_id")
    private User assigneeUser;


    @ManyToOne
    @JoinColumn(name = "territory_id")
    private Territory territory;


    @Column(nullable = false)
    private Integer priority = 100;


    @Column(nullable = false)
    private Boolean active = true;


    public AssignmentRule() {
    }


    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getMatchType() {
        return matchType;
    }


    public void setMatchType(String matchType) {
        this.matchType = matchType;
    }


    public String getMatchValue() {
        return matchValue;
    }


    public void setMatchValue(String matchValue) {
        this.matchValue = matchValue;
    }


    public User getAssigneeUser() {
        return assigneeUser;
    }


    public void setAssigneeUser(User assigneeUser) {
        this.assigneeUser = assigneeUser;
    }


    public Territory getTerritory() {
        return territory;
    }


    public void setTerritory(Territory territory) {
        this.territory = territory;
    }


    public Integer getPriority() {
        return priority;
    }


    public void setPriority(Integer priority) {
        this.priority = priority;
    }


    public Boolean getActive() {
        return active;
    }


    public void setActive(Boolean active) {
        this.active = active;
    }
}