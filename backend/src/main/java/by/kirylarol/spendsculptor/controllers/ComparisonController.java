package by.kirylarol.spendsculptor.controllers;

import by.kirylarol.spendsculptor.entities.User;
import by.kirylarol.spendsculptor.service.ComparisonService;
import by.kirylarol.spendsculptor.utils.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/comparison")
public class ComparisonController extends BaseController {
    
    private final ComparisonService comparisonService;
    
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
        return executeAuthenticatedOperation(() -> {
            User user = getAuthenticatedUser();
            return comparisonService.compareExpenses(
                user.getId(), period1Start, period1End, period2Start, period2End);
        });
    }
    
    @GetMapping("/income")
    public ResponseEntity<?> compareIncome(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate period1Start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate period1End,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate period2Start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate period2End) {
        return executeAuthenticatedOperation(() -> {
            User user = getAuthenticatedUser();
            return comparisonService.compareIncome(
                user.getId(), period1Start, period1End, period2Start, period2End);
        });
    }
    
    @GetMapping("/net-income")
    public ResponseEntity<?> compareNetIncome(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate period1Start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate period1End,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate period2Start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate period2End) {
        return executeAuthenticatedOperation(() -> {
            User user = getAuthenticatedUser();
            return comparisonService.compareNetIncome(
                user.getId(), period1Start, period1End, period2Start, period2End);
        });
    }
    
    @GetMapping("/expenses-by-category")
    public ResponseEntity<?> compareExpensesByCategory(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate period1Start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate period1End,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate period2Start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate period2End) {
        return executeAuthenticatedOperation(() -> {
            User user = getAuthenticatedUser();
            return comparisonService.compareExpensesByCategory(
                user.getId(), period1Start, period1End, period2Start, period2End);
        });
    }
    
    @GetMapping("/income-by-category")
    public ResponseEntity<?> compareIncomeByCategory(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate period1Start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate period1End,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate period2Start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate period2End) {
        return executeAuthenticatedOperation(() -> {
            User user = getAuthenticatedUser();
            return comparisonService.compareIncomeByCategory(
                user.getId(), period1Start, period1End, period2Start, period2End);
        });
    }
    
    @GetMapping("/predefined-periods")
    public ResponseEntity<?> getPredefinedPeriods() {
        return executeAuthenticatedOperation(() -> {
            // Just verify user is authenticated, no need to use the user object
            getAuthenticatedUser();
            return comparisonService.getPredefinedPeriods();
        });
    }
}
