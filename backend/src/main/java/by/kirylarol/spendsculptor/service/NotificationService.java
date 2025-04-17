package by.kirylarol.spendsculptor.service;

import by.kirylarol.spendsculptor.entities.PaymentNotification;
import by.kirylarol.spendsculptor.entities.PaymentReminder;
import by.kirylarol.spendsculptor.entities.UserProfile;
import by.kirylarol.spendsculptor.repos.PaymentNotificationRepository;
import by.kirylarol.spendsculptor.repos.PaymentReminderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final PaymentNotificationRepository notificationRepository;
    private final PaymentReminderRepository reminderRepository;
    private final UserService userService;

    @Transactional(readOnly = true)
    public List<PaymentNotification> getAllNotifications() {
        UserProfile currentUser = userService.getCurrentUser();
        return notificationRepository.findByUser(currentUser);
    }

    @Transactional(readOnly = true)
    public List<PaymentNotification> getUnreadNotifications() {
        UserProfile currentUser = userService.getCurrentUser();
        return notificationRepository.findUnreadNotifications(currentUser);
    }

    @Transactional(readOnly = true)
    public List<PaymentNotification> getNotificationsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        UserProfile currentUser = userService.getCurrentUser();
        return notificationRepository.findByUserAndNotificationDateBetween(currentUser, startDate, endDate);
    }

    @Transactional(readOnly = true)
    public Optional<PaymentNotification> getNotificationById(Integer id) {
        UserProfile currentUser = userService.getCurrentUser();
        Optional<PaymentNotification> notification = notificationRepository.findById(id);
        
        // Ensure the notification belongs to the current user
        if (notification.isPresent() && notification.get().getPaymentReminder().getUser().getId().equals(currentUser.getId())) {
            return notification;
        }
        
        return Optional.empty();
    }

    @Transactional
    public Optional<PaymentNotification> markNotificationAsRead(Integer id) {
        UserProfile currentUser = userService.getCurrentUser();
        Optional<PaymentNotification> notification = notificationRepository.findById(id);
        
        if (notification.isPresent() && notification.get().getPaymentReminder().getUser().getId().equals(currentUser.getId())) {
            PaymentNotification paymentNotification = notification.get();
            paymentNotification.setIsRead(true);
            return Optional.of(notificationRepository.save(paymentNotification));
        }
        
        return Optional.empty();
    }

    @Transactional
    public void markAllNotificationsAsRead() {
        UserProfile currentUser = userService.getCurrentUser();
        List<PaymentNotification> unreadNotifications = notificationRepository.findUnreadNotifications(currentUser);
        
        for (PaymentNotification notification : unreadNotifications) {
            notification.setIsRead(true);
            notificationRepository.save(notification);
        }
    }

    @Transactional
    public PaymentNotification createNotification(PaymentReminder paymentReminder, PaymentNotification.NotificationType type) {
        PaymentNotification notification = new PaymentNotification();
        notification.setPaymentReminder(paymentReminder);
        notification.setNotificationDate(LocalDateTime.now());
        notification.setNotificationType(type);
        notification.setIsRead(false);
        
        return notificationRepository.save(notification);
    }

    @Transactional
    public void generateDueNotifications() {
        List<UserProfile> users = userService.getAllUsers();
        
        for (UserProfile user : users) {
            // Get all pending payments
            List<PaymentReminder> pendingPayments = reminderRepository.findByUserAndStatus(user, PaymentReminder.PaymentStatus.PENDING);
            
            for (PaymentReminder payment : pendingPayments) {
                LocalDate today = LocalDate.now();
                LocalDate notificationDate = payment.getDueDate().minusDays(payment.getNotificationDays());
                
                // If today is the notification date, create a notification
                if (today.equals(notificationDate)) {
                    createNotification(payment, PaymentNotification.NotificationType.DUE_DATE);
                }
                
                // If today is the due date, create a notification
                if (today.equals(payment.getDueDate())) {
                    createNotification(payment, PaymentNotification.NotificationType.DUE_DATE);
                }
                
                // If today is past the due date, create an overdue notification
                if (today.isAfter(payment.getDueDate())) {
                    createNotification(payment, PaymentNotification.NotificationType.OVERDUE);
                    
                    // Also update the payment status to OVERDUE
                    payment.setStatus(PaymentReminder.PaymentStatus.OVERDUE);
                    reminderRepository.save(payment);
                }
            }
        }
    }

    @Transactional
    public PaymentNotification createTestNotification(Integer paymentReminderId) {
        UserProfile currentUser = userService.getCurrentUser();
        
        // If a payment reminder ID is provided, use that
        if (paymentReminderId != null) {
            Optional<PaymentReminder> paymentReminder = reminderRepository.findById(paymentReminderId);
            
            if (paymentReminder.isPresent() && paymentReminder.get().getUser().getId().equals(currentUser.getId())) {
                return createNotification(paymentReminder.get(), PaymentNotification.NotificationType.REMINDER);
            }
        }
        
        // Otherwise, find the first pending payment
        List<PaymentReminder> pendingPayments = reminderRepository.findByUserAndStatus(currentUser, PaymentReminder.PaymentStatus.PENDING);
        
        if (!pendingPayments.isEmpty()) {
            return createNotification(pendingPayments.get(0), PaymentNotification.NotificationType.REMINDER);
        }
        
        // If no pending payments, create a dummy payment reminder
        PaymentReminder dummyReminder = new PaymentReminder();
        dummyReminder.setUser(currentUser);
        dummyReminder.setTitle("Test Payment");
        dummyReminder.setDueDate(LocalDate.now().plusDays(7));
        dummyReminder.setStatus(PaymentReminder.PaymentStatus.PENDING);
        dummyReminder = reminderRepository.save(dummyReminder);
        
        return createNotification(dummyReminder, PaymentNotification.NotificationType.REMINDER);
    }

    @Transactional(readOnly = true)
    public Long getUnreadNotificationCount() {
        UserProfile currentUser = userService.getCurrentUser();
        return notificationRepository.countUnreadNotifications(currentUser);
    }
}
