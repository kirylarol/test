package by.kirylarol.spendsculptor.controllers;

import by.kirylarol.spendsculptor.entities.User;
import by.kirylarol.spendsculptor.service.ComparisonService;
import by.kirylarol.spendsculptor.utils.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/comparison")
public class ComparisonController {
    
    private final ComparisonService comparisonService;
    private final Util util;
    
    @Autowired
    public ComparisonController(ComparisonService comparisonService, Util util) {
        this.comparisonService = comparisonService;
        this.util = util;
    }
    
    @GetMapping("/expenses")
    public ResponseEntity<?> compareExpenses(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate period1Start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate period1End,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate period2Start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate period2End) {
        try {
            User user = util.getUser();
            Map<String, Object> result = comparisonService.compareExpenses(
                user.getId(), period1Start, period1End, period2Start, period2End);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    
    @GetMapping("/income")
    public ResponseEntity<?> compareIncome(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate period1Start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate period1End,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate period2Start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate period2End) {
        try {
            User user = util.getUser();
            Map<String, Object> result = comparisonService.compareIncome(
                user.getId(), period1Start, period1End, period2Start, period2End);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    
    @GetMapping("/net-income")
    public ResponseEntity<?> compareNetIncome(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate period1Start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate period1End,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate period2Start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate period2End) {
        try {
            User user = util.getUser();
            Map<String, Object> result = comparisonService.compareNetIncome(
                user.getId(), period1Start, period1End, period2Start, period2End);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    
    @GetMapping("/expenses-by-category")
    public ResponseEntity<?> compareExpensesByCategory(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate period1Start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate period1End,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate period2Start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate period2End) {
        try {
            User user = util.getUser();
            Map<String, Object> result = comparisonService.compareExpensesByCategory(
                user.getId(), period1Start, period1End, period2Start, period2End);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    
    @GetMapping("/income-by-category")
    public ResponseEntity<?> compareIncomeByCategory(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate period1Start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate period1End,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate period2Start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate period2End) {
        try {
            User user = util.getUser();
            Map<String, Object> result = comparisonService.compareIncomeByCategory(
                user.getId(), period1Start, period1End, period2Start, period2End);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    
    @GetMapping("/predefined-periods")
    public ResponseEntity<?> getPredefinedPeriods() {
        try {
            util.getUser(); // Verify user is authenticated
            Map<String, Map<String, LocalDate>> predefinedPeriods = comparisonService.getPredefinedPeriods();
            return ResponseEntity.ok(predefinedPeriods);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
