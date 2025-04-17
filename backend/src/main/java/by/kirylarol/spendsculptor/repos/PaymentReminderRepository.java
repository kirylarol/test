package by.kirylarol.spendsculptor.repos;

import by.kirylarol.spendsculptor.entities.PaymentReminder;
import by.kirylarol.spendsculptor.entities.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PaymentReminderRepository extends JpaRepository<PaymentReminder, Integer> {

    List<PaymentReminder> findByUser(UserProfile user);
    
    List<PaymentReminder> findByUserAndStatus(UserProfile user, PaymentReminder.PaymentStatus status);
    
    @Query("SELECT pr FROM PaymentReminder pr WHERE pr.user = :user AND pr.dueDate BETWEEN :startDate AND :endDate")
    List<PaymentReminder> findByUserAndDueDateBetween(
            @Param("user") UserProfile user,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
    
    @Query("SELECT pr FROM PaymentReminder pr WHERE pr.user = :user AND pr.dueDate <= CURRENT_DATE AND pr.status = 'PENDING'")
    List<PaymentReminder> findOverduePayments(@Param("user") UserProfile user);
    
    @Query("SELECT pr FROM PaymentReminder pr WHERE pr.user = :user AND pr.dueDate BETWEEN CURRENT_DATE AND :futureDate AND pr.status = 'PENDING'")
    List<PaymentReminder> findUpcomingPayments(
            @Param("user") UserProfile user,
            @Param("futureDate") LocalDate futureDate);
    
    @Query("SELECT pr FROM PaymentReminder pr WHERE pr.user = :user AND pr.dueDate <= CURRENT_DATE AND pr.status = 'PENDING' AND pr.isRecurring = true")
    List<PaymentReminder> findRecurringOverduePayments(@Param("user") UserProfile user);
    
    @Query("SELECT COUNT(pr) FROM PaymentReminder pr WHERE pr.user = :user AND pr.status = :status")
    Long countByUserAndStatus(
            @Param("user") UserProfile user,
            @Param("status") PaymentReminder.PaymentStatus status);
}
