package by.kirylarol.spendsculptor.repos;

import by.kirylarol.spendsculptor.entities.PaymentNotification;
import by.kirylarol.spendsculptor.entities.PaymentReminder;
import by.kirylarol.spendsculptor.entities.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PaymentNotificationRepository extends JpaRepository<PaymentNotification, Integer> {

    List<PaymentNotification> findByPaymentReminder(PaymentReminder paymentReminder);
    
    List<PaymentNotification> findByPaymentReminderAndIsRead(PaymentReminder paymentReminder, Boolean isRead);
    
    @Query("SELECT pn FROM PaymentNotification pn WHERE pn.paymentReminder.user = :user ORDER BY pn.notificationDate DESC")
    List<PaymentNotification> findByUser(@Param("user") UserProfile user);
    
    @Query("SELECT pn FROM PaymentNotification pn WHERE pn.paymentReminder.user = :user AND pn.isRead = :isRead ORDER BY pn.notificationDate DESC")
    List<PaymentNotification> findByUserAndIsRead(
            @Param("user") UserProfile user,
            @Param("isRead") Boolean isRead);
    
    @Query("SELECT pn FROM PaymentNotification pn WHERE pn.paymentReminder.user = :user AND pn.notificationDate BETWEEN :startDate AND :endDate ORDER BY pn.notificationDate DESC")
    List<PaymentNotification> findByUserAndNotificationDateBetween(
            @Param("user") UserProfile user,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(pn) FROM PaymentNotification pn WHERE pn.paymentReminder.user = :user AND pn.isRead = false")
    Long countUnreadNotifications(@Param("user") UserProfile user);
    
    @Query("SELECT pn FROM PaymentNotification pn WHERE pn.paymentReminder.user = :user AND pn.isRead = false ORDER BY pn.notificationDate DESC")
    List<PaymentNotification> findUnreadNotifications(@Param("user") UserProfile user);
}
