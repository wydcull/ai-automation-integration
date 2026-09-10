package org.example.aisalesops.dto;

import lombok.Data;

@Data
public class UserSummaryResponse {

    private long activeUsers;
    private long managers;
    private long representatives;
    private long admins;

    public UserSummaryResponse(long activeUsers,
                               long managers,
                               long representatives,
                               long admins) {

        this.activeUsers = activeUsers;
        this.managers = managers;
        this.representatives = representatives;
        this.admins = admins;
    }

    public long getActiveUsers() {
        return activeUsers;
    }

    public void setActiveUsers(long activeUsers) {
        this.activeUsers = activeUsers;
    }

    public long getManagers() {
        return managers;
    }

    public void setManagers(long managers) {
        this.managers = managers;
    }

    public long getRepresentatives() {
        return representatives;
    }

    public void setRepresentatives(long representatives) {
        this.representatives = representatives;
    }

    public long getAdmins() {
        return admins;
    }

    public void setAdmins(long admins) {
        this.admins = admins;
    }
}