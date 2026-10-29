package vn.edu.hcmute.storemanagement.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "customer_feedback")
public class CustomerFeedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String username;

    @Column(nullable = false, length = 150)
    private String displayName;

    @Column(length = 60)
    private String category = "Góp ý chung";

    @Column(nullable = false, length = 1200)
    private String message;

    @Column(nullable = false)
    private int rating;

    @Column(nullable = false, length = 20)
    private String status = "Mới";

    @Column(length = 1200)
    private String adminReply;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public void setUsername(String v) { username = v; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String v) { displayName = v; }
    public String getCategory() { return category; }
    public void setCategory(String v) { category = v; }
    public String getMessage() { return message; }
    public void setMessage(String v) { message = v; }
    public int getRating() { return rating; }
    public void setRating(int v) { rating = v; }
    public String getStatus() { return status; }
    public void setStatus(String v) { status = v; }
    public String getAdminReply() { return adminReply; }
    public void setAdminReply(String v) { adminReply = v; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
