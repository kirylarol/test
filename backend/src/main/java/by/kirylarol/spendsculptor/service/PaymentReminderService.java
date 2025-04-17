package by.kirylarol.spendsculptor.service;

import by.kirylarol.spendsculptor.entities.PaymentReminder;
import by.kirylarol.spendsculptor.entities.UserProfile;
import by.kirylarol.spendsculptor.repos.PaymentReminderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentReminderService {

    private final PaymentReminderRepository paymentReminderRepository;
    private final UserService userService;

    @Transactional(readOnly = true)
    public List<PaymentReminder> getAllPaymentReminders() {
        UserProfile currentUser = userService.getCurrentUser();
        return paymentReminderRepository.findByUser(currentUser);
    }

    @Transactional(readOnly = true)
    public List<PaymentReminder> getPaymentRemindersByStatus(PaymentReminder.PaymentStatus status) {
        UserProfile currentUser = userService.getCurrentUser();
        return paymentReminderRepository.findByUserAndStatus(currentUser, status);
    }

    @Transactional(readOnly = true)
    public List<PaymentReminder> getPaymentRemindersByDateRange(LocalDate startDate, LocalDate endDate) {
        UserProfile currentUser = userService.getCurrentUser();
        return paymentReminderRepository.findByUserAndDueDateBetween(currentUser, startDate, endDate);
    }

    @Transactional(readOnly = true)
    public List<PaymentReminder> getOverduePayments() {
        UserProfile currentUser = userService.getCurrentUser();
        return paymentReminderRepository.findOverduePayments(currentUser);
    }

    @Transactional(readOnly = true)
    public List<PaymentReminder> getUpcomingPayments(int days) {
        UserProfile currentUser = userService.getCurrentUser();
        LocalDate futureDate = LocalDate.now().plusDays(days);
        return paymentReminderRepository.findUpcomingPayments(currentUser, futureDate);
    }

    @Transactional(readOnly = true)
    public Optional<PaymentReminder> getPaymentReminderById(Integer id) {
        UserProfile currentUser = userService.getCurrentUser();
        Optional<PaymentReminder> paymentReminder = paymentReminderRepository.findById(id);
        
        // Ensure the payment reminder belongs to the current user
        if (paymentReminder.isPresent() && paymentReminder.get().getUser().getId().equals(currentUser.getId())) {
            return paymentReminder;
        }
        
        return Optional.empty();
    }

    @Transactional
    public PaymentReminder createPaymentReminder(PaymentReminder paymentReminder) {
        UserProfile currentUser = userService.getCurrentUser();
        paymentReminder.setUser(currentUser);
        
        // Set default values if not provided
        if (paymentReminder.getStatus() == null) {
            paymentReminder.setStatus(PaymentReminder.PaymentStatus.PENDING);
        }
        
        return paymentReminderRepository.save(paymentReminder);
    }

    @Transactional
    public Optional<PaymentReminder> updatePaymentReminder(Integer id, PaymentReminder updatedPaymentReminder) {
        UserProfile currentUser = userService.getCurrentUser();
        Optional<PaymentReminder> existingPaymentReminder = paymentReminderRepository.findById(id);
        
        if (existingPaymentReminder.isPresent() && existingPaymentReminder.get().getUser().getId().equals(currentUser.getId())) {
            PaymentReminder paymentReminder = existingPaymentReminder.get();
            
            // Update fields
            paymentReminder.setTitle(updatedPaymentReminder.getTitle());
            paymentReminder.setAmount(updatedPaymentReminder.getAmount());
            paymentReminder.setDueDate(updatedPaymentReminder.getDueDate());
            paymentReminder.setDescription(updatedPaymentReminder.getDescription());
            paymentReminder.setCategory(updatedPaymentReminder.getCategory());
            paymentReminder.setIsRecurring(updatedPaymentReminder.getIsRecurring());
            paymentReminder.setRecurrencePattern(updatedPaymentReminder.getRecurrencePattern());
            paymentReminder.setNotificationDays(updatedPaymentReminder.getNotificationDays());
            
            return Optional.of(paymentReminderRepository.save(paymentReminder));
        }
        
        return Optional.empty();
    }

    @Transactional
    public Optional<PaymentReminder> updatePaymentStatus(Integer id, PaymentReminder.PaymentStatus status) {
        UserProfile currentUser = userService.getCurrentUser();
        Optional<PaymentReminder> existingPaymentReminder = paymentReminderRepository.findById(id);
        
        if (existingPaymentReminder.isPresent() && existingPaymentReminder.get().getUser().getId().equals(currentUser.getId())) {
            PaymentReminder paymentReminder = existingPaymentReminder.get();
            paymentReminder.setStatus(status);
            
            return Optional.of(paymentReminderRepository.save(paymentReminder));
        }
        
        return Optional.empty();
    }

    @Transactional
    public boolean deletePaymentReminder(Integer id) {
        UserProfile currentUser = userService.getCurrentUser();
        Optional<PaymentReminder> existingPaymentReminder = paymentReminderRepository.findById(id);
        
        if (existingPaymentReminder.isPresent() && existingPaymentReminder.get().getUser().getId().equals(currentUser.getId())) {
            paymentReminderRepository.deleteById(id);
            return true;
        }
        
        return false;
    }

    @Transactional(readOnly = true)
    public Map<String, Long> getPaymentSummary() {
        UserProfile currentUser = userService.getCurrentUser();
        Map<String, Long> summary = new HashMap<>();
        
        summary.put("pending", paymentReminderRepository.countByUserAndStatus(currentUser, PaymentReminder.PaymentStatus.PENDING));
        summary.put("paid", paymentReminderRepository.countByUserAndStatus(currentUser, PaymentReminder.PaymentStatus.PAID));
        summary.put("overdue", paymentReminderRepository.countByUserAndStatus(currentUser, PaymentReminder.PaymentStatus.OVERDUE));
        
        return summary;
    }

    @Transactional
    public void updatePaymentStatuses() {
        // Find all users with pending payments
        List<UserProfile> users = userService.getAllUsers();
        
        for (UserProfile user : users) {
            // Get all pending payments that are past due date
            List<PaymentReminder> overduePayments = paymentReminderRepository.findOverduePayments(user);
            
            // Update status to OVERDUE
            for (PaymentReminder payment : overduePayments) {
                payment.setStatus(PaymentReminder.PaymentStatus.OVERDUE);
                paymentReminderRepository.save(payment);
            }
        }
    }

    @Transactional
    public List<PaymentReminder> createRecurringPayments() {
        // Find all users with recurring payments
        List<UserProfile> users = userService.getAllUsers();
        
        for (UserProfile user : users) {
            // Get all recurring payments that are overdue
            List<PaymentReminder> recurringOverduePayments = paymentReminderRepository.findRecurringOverduePayments(user);
            
            // Create new instances for the next period
            for (PaymentReminder payment : recurringOverduePayments) {
                // Only create new instance if the payment is marked as paid
                if (payment.getStatus() == PaymentReminder.PaymentStatus.PAID) {
                    PaymentReminder newPayment = new PaymentReminder();
                    newPayment.setUser(user);
                    newPayment.setTitle(payment.getTitle());
                    newPayment.setAmount(payment.getAmount());
                    newPayment.setDescription(payment.getDescription());
                    newPayment.setCategory(payment.getCategory());
                    newPayment.setIsRecurring(true);
                    newPayment.setRecurrencePattern(payment.getRecurrencePattern());
                    newPayment.setNotificationDays(payment.getNotificationDays());
                    newPayment.setStatus(PaymentReminder.PaymentStatus.PENDING);
                    
                    // Calculate next due date based on recurrence pattern
                    LocalDate nextDueDate = calculateNextDueDate(payment.getDueDate(), payment.getRecurrencePattern());
                    newPayment.setDueDate(nextDueDate);
                    
                    paymentReminderRepository.save(newPayment);
                }
            }
        }
        
        return getAllPaymentReminders();
    }

    private LocalDate calculateNextDueDate(LocalDate currentDueDate, String recurrencePattern) {
        switch (recurrencePattern) {
            case "DAILY":
                return currentDueDate.plus(1, ChronoUnit.DAYS);
            case "WEEKLY":
                return currentDueDate.plus(1, ChronoUnit.WEEKS);
            case "BIWEEKLY":
                return currentDueDate.plus(2, ChronoUnit.WEEKS);
            case "MONTHLY":
                return currentDueDate.plus(1, ChronoUnit.MONTHS);
            case "QUARTERLY":
                return currentDueDate.plus(3, ChronoUnit.MONTHS);
            case "SEMIANNUALLY":
                return currentDueDate.plus(6, ChronoUnit.MONTHS);
            case "ANNUALLY":
                return currentDueDate.plus(1, ChronoUnit.YEARS);
            default:
                return currentDueDate.plus(1, ChronoUnit.MONTHS); // Default to monthly
        }
    }
}
