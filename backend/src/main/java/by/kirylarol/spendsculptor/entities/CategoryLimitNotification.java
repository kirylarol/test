package by.kirylarol.spendsculptor.entities;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "category_limit_notification")
@Data
public class CategoryLimitNotification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "category_limit_notification_seq")
    @SequenceGenerator(name = "category_limit_notification_seq", sequenceName = "category_limit_notification_seq", allocationSize = 1)
    @Column(name = "notification_id")
    private Integer notificationId;
    
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "message")
    private String message;
    
    @Column(name = "is_read")
    private Boolean isRead;
    
    // Default constructor
    public CategoryLimitNotification() {
        this.createdAt = LocalDateTime.now();
        this.isRead = false;
    }
    
    // Constructor with parameters
    public CategoryLimitNotification(Category category, User user, String message) {
        this.category = category;
        this.user = user;
        this.message = message;
        this.createdAt = LocalDateTime.now();
        this.isRead = false;
    }
}
