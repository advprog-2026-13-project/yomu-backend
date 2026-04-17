package id.ac.ui.cs.advprog.yomu.backend.auth.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(
    name = "users",
    uniqueConstraints = {
      @UniqueConstraint(name = "uk_users_username", columnNames = "username"),
      @UniqueConstraint(name = "uk_users_email", columnNames = "email"),
      @UniqueConstraint(name = "uk_users_phone", columnNames = "phoneNumber"),
      @UniqueConstraint(name = "uk_users_google_sub", columnNames = "googleSub")
    })
public class User {
  @Id @GeneratedValue private UUID id;

  @Column(nullable = false, length = 40)
  private String username;

  @Column(nullable = false, length = 100)
  private String displayName;

  @Column(length = 120)
  private String email;

  @Column(length = 20)
  private String phoneNumber;

  @Column(length = 200)
  private String passwordHash;

  @Column(length = 255, unique = true)
  private String googleSub;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 10)
  private Role role;

  @Column(nullable = false)
  private Instant createdAt = Instant.now();

  protected User() {}

  public User(
      String username,
      String displayName,
      String email,
      String phoneNumber,
      String passwordHash,
      Role role) {
    this.username = username;
    this.displayName = displayName;
    this.email = email;
    this.phoneNumber = phoneNumber;
    this.passwordHash = passwordHash;
    this.role = role;
  }

  public UUID getId() {
    return id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public String getPasswordHash() {
    return passwordHash;
  }

  public void setPasswordHash(String passwordHash) {
    this.passwordHash = passwordHash;
  }

  public String getGoogleSub() {
    return googleSub;
  }

  public void setGoogleSub(String googleSub) {
    this.googleSub = googleSub;
  }

  public Role getRole() {
    return role;
  }

  public void setRole(Role role) {
    this.role = role;
  }
}
