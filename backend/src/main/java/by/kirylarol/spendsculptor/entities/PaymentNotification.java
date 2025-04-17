package by.kirylarol.spendsculptor.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_notification")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "payment_notification_seq")
    @SequenceGenerator(name = "payment_notification_seq", sequenceName = "payment_notification_seq", allocationSize = 1)
    @Column(name = "payment_notification_id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "payment_reminder_id", nullable = false)
    private PaymentReminder paymentReminder;

    @Column(name = "notification_date", nullable = false)
    private LocalDateTime notificationDate;

    @Column(name = "notification_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;

    @Column(name = "is_read")
    private Boolean isRead;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @PrePersist
    protected void onCreate() {
        createdDate = LocalDateTime.now();
        if (isRead == null) {
            isRead = false;
        }
    }

    public enum NotificationType {
        DUE_DATE,
        OVERDUE,
        REMINDER,
        PAYMENT_CREATED,
        PAYMENT_UPDATED,
        PAYMENT_DELETED
    }
}
