package kafdrop.model;

import java.time.temporal.ChronoUnit;
import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "auth_tokens")
public class AuthTokenVO implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  @Column(nullable = false)
  private String token;

  @Column(name = "expires_at", nullable = false)
  private LocalDateTime expiresAt;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private UserVO user;

  // Getters and setters omitted for brevity

  public AuthTokenVO() {
    this.createdAt = LocalDateTime.now();
    this.updatedAt = this.createdAt;
    // Set the default expiration to 24 hours from creation time
    this.expiresAt = this.createdAt.plus(24, ChronoUnit.HOURS);
  }

  public void setToken(String token) {
    this.token = token;
  }

  public String getToken() {
    return token;
  }

  public LocalDateTime getExpiresAt() {
    return expiresAt;
  }

  public void setExpiresAt(LocalDateTime expiresAt) {
    this.expiresAt = expiresAt;
  }
  // Other getters and setters should be implemented as needed
}