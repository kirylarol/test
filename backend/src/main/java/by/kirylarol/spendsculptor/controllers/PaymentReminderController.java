package by.kirylarol.spendsculptor.controllers;

import by.kirylarol.spendsculptor.entities.PaymentReminder;
import by.kirylarol.spendsculptor.service.PaymentReminderService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/payment-reminders")
@RequiredArgsConstructor
public class PaymentReminderController {

    private final PaymentReminderService paymentReminderService;

    @GetMapping
    public ResponseEntity<List<PaymentReminder>> getAllPaymentReminders(
            @RequestParam(required = false) PaymentReminder.PaymentStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        if (status != null) {
            return ResponseEntity.ok(paymentReminderService.getPaymentRemindersByStatus(status));
        } else if (startDate != null && endDate != null) {
            return ResponseEntity.ok(paymentReminderService.getPaymentRemindersByDateRange(startDate, endDate));
        } else {
            return ResponseEntity.ok(paymentReminderService.getAllPaymentReminders());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentReminder> getPaymentReminderById(@PathVariable Integer id) {
        Optional<PaymentReminder> paymentReminder = paymentReminderService.getPaymentReminderById(id);
        return paymentReminder.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<PaymentReminder> createPaymentReminder(@RequestBody PaymentReminder paymentReminder) {
        PaymentReminder createdPaymentReminder = paymentReminderService.createPaymentReminder(paymentReminder);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPaymentReminder);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PaymentReminder> updatePaymentReminder(
            @PathVariable Integer id,
            @RequestBody PaymentReminder paymentReminder) {
        
        Optional<PaymentReminder> updatedPaymentReminder = paymentReminderService.updatePaymentReminder(id, paymentReminder);
        return updatedPaymentReminder.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<PaymentReminder> updatePaymentStatus(
            @PathVariable Integer id,
            @RequestBody Map<String, String> statusUpdate) {
        
        String statusStr = statusUpdate.get("status");
        if (statusStr == null) {
            return ResponseEntity.badRequest().build();
        }
        
        try {
            PaymentReminder.PaymentStatus status = PaymentReminder.PaymentStatus.valueOf(statusStr);
            Optional<PaymentReminder> updatedPaymentReminder = paymentReminderService.updatePaymentStatus(id, status);
            return updatedPaymentReminder.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePaymentReminder(@PathVariable Integer id) {
        boolean deleted = paymentReminderService.deletePaymentReminder(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @GetMapping("/upcoming")
    public ResponseEntity<List<PaymentReminder>> getUpcomingPayments(
            @RequestParam(defaultValue = "7") int days) {
        
        List<PaymentReminder> upcomingPayments = paymentReminderService.getUpcomingPayments(days);
        return ResponseEntity.ok(upcomingPayments);
    }

    @GetMapping("/overdue")
    public ResponseEntity<List<PaymentReminder>> getOverduePayments() {
        List<PaymentReminder> overduePayments = paymentReminderService.getOverduePayments();
        return ResponseEntity.ok(overduePayments);
    }
}
