package id.ac.ui.cs.advprog.yomu.backend.auth.api.dto;

import id.ac.ui.cs.advprog.yomu.backend.auth.domain.Role;
import java.util.UUID;

public class MeResponse {
    private final UUID id;
    private final String username;
    private final String displayName; 
    private final String email;
    private final String phoneNumber; 
    private final Role role;

    public MeResponse(UUID id, String username, String displayName, String email, String phoneNumber, Role role) {
        this.id = id;
        this.username = username;
        this.displayName = displayName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.role = role;
    }

    public UUID getId() { return id; }
    public String getUsername() { return username; }
    public String getDisplayName() { return displayName; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }
    public Role getRole() { return role; }
}