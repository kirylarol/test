package by.kirylarol.spendsculptor.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_reminder")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentReminder {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "payment_reminder_seq")
    @SequenceGenerator(name = "payment_reminder_seq", sequenceName = "payment_reminder_seq", allocationSize = 1)
    @Column(name = "payment_reminder_id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserProfile user;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "is_recurring")
    private Boolean isRecurring;

    @Column(name = "recurrence_pattern")
    private String recurrencePattern;

    @Column(name = "notification_days")
    private Integer notificationDays;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "last_modified_date")
    private LocalDateTime lastModifiedDate;

    @PrePersist
    protected void onCreate() {
        createdDate = LocalDateTime.now();
        lastModifiedDate = LocalDateTime.now();
        if (status == null) {
            status = PaymentStatus.PENDING;
        }
        if (notificationDays == null) {
            notificationDays = 3;
        }
        if (isRecurring == null) {
            isRecurring = false;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        lastModifiedDate = LocalDateTime.now();
    }

    public enum PaymentStatus {
        PENDING,
        PAID,
        OVERDUE
    }
}
