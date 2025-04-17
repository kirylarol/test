package by.kirylarol.spendsculptor.controllers;

import by.kirylarol.spendsculptor.service.PaymentReminderService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final PaymentReminderService paymentReminderService;

    @GetMapping("/payment-summary")
    public ResponseEntity<Map<String, Long>> getPaymentSummary() {
        Map<String, Long> summary = paymentReminderService.getPaymentSummary();
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/calendar")
    public ResponseEntity<Map<String, Object>> getCalendarData(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year) {
        
        // Default to current month and year if not provided
        LocalDate today = LocalDate.now();
        int currentMonth = month != null ? month : today.getMonthValue();
        int currentYear = year != null ? year : today.getYear();
        
        // Get start and end dates for the month
        YearMonth yearMonth = YearMonth.of(currentYear, currentMonth);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();
        
        // Get payments for the month
        List<Map<String, Object>> calendarEvents = new ArrayList<>();
        paymentReminderService.getPaymentRemindersByDateRange(startDate, endDate).forEach(payment -> {
            Map<String, Object> event = new HashMap<>();
            event.put("id", payment.getId());
            event.put("title", payment.getTitle());
            event.put("date", payment.getDueDate());
            event.put("amount", payment.getAmount());
            event.put("status", payment.getStatus());
            event.put("category", payment.getCategory() != null ? payment.getCategory().getName() : null);
            calendarEvents.add(event);
        });
        
        // Create response
        Map<String, Object> response = new HashMap<>();
        response.put("month", currentMonth);
        response.put("year", currentYear);
        response.put("startDate", startDate);
        response.put("endDate", endDate);
        response.put("events", calendarEvents);
        
        return ResponseEntity.ok(response);
    }
}
