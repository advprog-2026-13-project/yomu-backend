package id.ac.ui.cs.advprog.yomu.backend.auth.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
    name = "users",
    uniqueConstraints = {
      @UniqueConstraint(name = "uk_users_username", columnNames = "username"),
      @UniqueConstraint(name = "uk_users_email", columnNames = "email")
    })
public class User {
  @Id @GeneratedValue private UUID id;

  @Column(nullable = false, length = 40)
  private String username;

  @Column(nullable = false, length = 120)
  private String email;

  @Column(nullable = false, length = 200)
  private String passwordHash;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 10)
  private Role role;

  @Column(nullable = false)
  private Instant createdAt = Instant.now();

  protected User() {}

  public User(String username, String email, String passwordHash, Role role) {
    this.username = username;
    this.email = email;
    this.passwordHash = passwordHash;
    this.role = role;
  }

  public UUID getId() {
    return id;
  }

  public String getUsername() {
    return username;
  }

  public String getEmail() {
    return email;
  }

  public String getPasswordHash() {
    return passwordHash;
  }

  public Role getRole() {
    return role;
  }
}
