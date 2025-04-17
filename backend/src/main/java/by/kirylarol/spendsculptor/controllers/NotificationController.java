package by.kirylarol.spendsculptor.controllers;

import by.kirylarol.spendsculptor.entities.PaymentNotification;
import by.kirylarol.spendsculptor.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<PaymentNotification>> getAllNotifications(
            @RequestParam(required = false) Boolean isRead,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        if (isRead != null && isRead == false) {
            return ResponseEntity.ok(notificationService.getUnreadNotifications());
        } else if (startDate != null && endDate != null) {
            return ResponseEntity.ok(notificationService.getNotificationsByDateRange(startDate, endDate));
        } else {
            return ResponseEntity.ok(notificationService.getAllNotifications());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentNotification> getNotificationById(@PathVariable Integer id) {
        Optional<PaymentNotification> notification = notificationService.getNotificationById(id);
        return notification.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<PaymentNotification> markNotificationAsRead(@PathVariable Integer id) {
        Optional<PaymentNotification> updatedNotification = notificationService.markNotificationAsRead(id);
        return updatedNotification.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/read-all")
    public ResponseEntity<Void> markAllNotificationsAsRead() {
        notificationService.markAllNotificationsAsRead();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> getUnreadNotificationCount() {
        Long count = notificationService.getUnreadNotificationCount();
        return ResponseEntity.ok(Map.of("count", count));
    }

    @PostMapping("/test")
    public ResponseEntity<PaymentNotification> createTestNotification(
            @RequestBody(required = false) Map<String, Integer> requestBody) {
        
        Integer paymentReminderId = null;
        if (requestBody != null) {
            paymentReminderId = requestBody.get("paymentReminderId");
        }
        
        PaymentNotification notification = notificationService.createTestNotification(paymentReminderId);
        return ResponseEntity.status(HttpStatus.CREATED).body(notification);
    }
}
