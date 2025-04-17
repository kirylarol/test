package by.kirylarol.spendsculptor.controllers;

import by.kirylarol.spendsculptor.entities.Account;
import by.kirylarol.spendsculptor.entities.Income;
import by.kirylarol.spendsculptor.entities.IncomeCategory;
import by.kirylarol.spendsculptor.entities.User;
import by.kirylarol.spendsculptor.service.AccountService;
import by.kirylarol.spendsculptor.service.IncomeCategoryService;
import by.kirylarol.spendsculptor.service.IncomeService;
import by.kirylarol.spendsculptor.utils.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/incomes")
public class IncomeController extends BaseController {
    
    private final IncomeService incomeService;
    private final IncomeCategoryService incomeCategoryService;
    private final AccountService accountService;
    
    @Autowired
    public IncomeController(IncomeService incomeService, 
                           IncomeCategoryService incomeCategoryService,
                           AccountService accountService,
                           Util util) {
        this.incomeService = incomeService;
        this.incomeCategoryService = incomeCategoryService;
        this.accountService = accountService;
        this.util = util;
    }
    
    @GetMapping
    public ResponseEntity<?> getAllIncomes() {
        return executeAuthenticatedOperation(() -> {
            User user = getAuthenticatedUser();
            return incomeService.getAllIncomesByUser(user.getId());
        });
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getIncomeById(@PathVariable Integer id) {
        return executeAuthenticatedOperation(() -> {
            User user = getAuthenticatedUser();
            return incomeService.getIncomeById(id)
                .map(income -> {
                    // Verify the income belongs to the current user
                    if (income.getUser().getId().equals(user.getId())) {
                        return income;
                    } else {
                        return errorResponse(HttpStatus.FORBIDDEN, "Access denied");
                    }
                })
                .orElse(errorResponse(HttpStatus.NOT_FOUND, "Income not found"));
        });
    }
    
    @GetMapping("/account/{accountId}")
    public ResponseEntity<?> getIncomesByAccount(@PathVariable Integer accountId) {
        return executeAuthenticatedOperation(() -> {
            // Verify user has access to the account
            Account account = accountService.getById(accountId);
            if (account == null) {
                return errorResponse(HttpStatus.NOT_FOUND, "Account not found");
            }
            
            return incomeService.getAllIncomesByAccount(accountId);
        });
    }
    
    @GetMapping("/date-range")
    public ResponseEntity<?> getIncomesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return executeAuthenticatedOperation(() -> {
            User user = getAuthenticatedUser();
            return incomeService.getIncomesByUserAndDateRange(user.getId(), startDate, endDate);
        });
    }
    
    @PostMapping
    public ResponseEntity<?> createIncome(@RequestBody Income incomeRequest) {
        return executeAuthenticatedOperation(() -> {
            User user = getAuthenticatedUser();
            
            // Set the user for the income
            incomeRequest.setUser(user);
            
            // Validate and get the account
            Account account = accountService.getById(incomeRequest.getAccount().getId());
            if (account == null) {
                return errorResponse(HttpStatus.BAD_REQUEST, "Invalid account ID");
            }
            incomeRequest.setAccount(account);
            
            // Validate and get the category
            IncomeCategory category = incomeCategoryService.getCategoryById(incomeRequest.getIncomeCategory().getIncomeCategoryId())
                .orElse(null);
            if (category == null) {
                return errorResponse(HttpStatus.BAD_REQUEST, "Invalid income category ID");
            }
            incomeRequest.setIncomeCategory(category);
            
            return incomeService.createIncome(incomeRequest);
        });
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateIncome(@PathVariable Integer id, @RequestBody Income incomeRequest) {
        return executeAuthenticatedOperation(() -> {
            User user = getAuthenticatedUser();
            
            // Check if income exists and belongs to the user
            Income existingIncome = incomeService.getIncomeById(id).orElse(null);
            if (existingIncome == null) {
                return errorResponse(HttpStatus.NOT_FOUND, "Income not found");
            }
            
            if (!existingIncome.getUser().getId().equals(user.getId())) {
                return errorResponse(HttpStatus.FORBIDDEN, "Access denied");
            }
            
            // Keep the original user
            incomeRequest.setUser(existingIncome.getUser());
            
            // Validate and get the account
            Account account = accountService.getById(incomeRequest.getAccount().getId());
            if (account == null) {
                return errorResponse(HttpStatus.BAD_REQUEST, "Invalid account ID");
            }
            incomeRequest.setAccount(account);
            
            // Validate and get the category
            IncomeCategory category = incomeCategoryService.getCategoryById(incomeRequest.getIncomeCategory().getIncomeCategoryId())
                .orElse(null);
            if (category == null) {
                return errorResponse(HttpStatus.BAD_REQUEST, "Invalid income category ID");
            }
            incomeRequest.setIncomeCategory(category);
            
            return incomeService.updateIncome(id, incomeRequest);
        });
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteIncome(@PathVariable Integer id) {
        return executeAuthenticatedOperation(() -> {
            User user = getAuthenticatedUser();
            
            // Check if income exists and belongs to the user
            Income existingIncome = incomeService.getIncomeById(id).orElse(null);
            if (existingIncome == null) {
                return errorResponse(HttpStatus.NOT_FOUND, "Income not found");
            }
            
            if (!existingIncome.getUser().getId().equals(user.getId())) {
                return errorResponse(HttpStatus.FORBIDDEN, "Access denied");
            }
            
            incomeService.deleteIncome(id);
            return "Income deleted successfully";
        });
    }
    
    @GetMapping("/analysis/total")
    public ResponseEntity<?> getTotalIncome(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return executeAuthenticatedOperation(() -> {
            User user = getAuthenticatedUser();
            
            // If dates not provided, use current month
            if (startDate == null || endDate == null) {
                LocalDate now = LocalDate.now();
                startDate = now.withDayOfMonth(1);
                endDate = now.withDayOfMonth(now.lengthOfMonth());
            }
            
            BigDecimal totalIncome = incomeService.getTotalIncomeByUserAndDateRange(user.getId(), startDate, endDate);
            
            Map<String, Object> response = new HashMap<>();
            response.put("startDate", startDate);
            response.put("endDate", endDate);
            response.put("totalIncome", totalIncome);
            
            return response;
        });
    }
    
    @GetMapping("/analysis/by-category")
    public ResponseEntity<?> getIncomeByCategory(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return executeAuthenticatedOperation(() -> {
            User user = getAuthenticatedUser();
            
            // If dates not provided, use current month
            if (startDate == null || endDate == null) {
                LocalDate now = LocalDate.now();
                startDate = now.withDayOfMonth(1);
                endDate = now.withDayOfMonth(now.lengthOfMonth());
            }
            
            Map<String, BigDecimal> categoryTotals = incomeService.getIncomeByCategoryForUserAndDateRange(
                user.getId(), startDate, endDate);
            
            Map<String, Object> response = new HashMap<>();
            response.put("startDate", startDate);
            response.put("endDate", endDate);
            response.put("categoryTotals", categoryTotals);
            
            return response;
        });
    }
    
    @GetMapping("/analysis/monthly")
    public ResponseEntity<?> getMonthlyIncome() {
        return executeAuthenticatedOperation(() -> {
            User user = getAuthenticatedUser();
            
            Map<YearMonth, BigDecimal> monthlyTotals = incomeService.getMonthlyIncomeForUser(user.getId());
            
            // Convert YearMonth keys to strings for JSON serialization
            Map<String, BigDecimal> response = new HashMap<>();
            monthlyTotals.forEach((yearMonth, amount) -> 
                response.put(yearMonth.toString(), amount));
            
            return response;
        });
    }
    
    @GetMapping("/analysis/current-month")
    public ResponseEntity<?> getCurrentMonthIncome() {
        return executeAuthenticatedOperation(() -> {
            User user = getAuthenticatedUser();
            
            BigDecimal currentMonthIncome = incomeService.getCurrentMonthIncomeForUser(user.getId());
            
            Map<String, Object> response = new HashMap<>();
            response.put("month", YearMonth.now().toString());
            response.put("totalIncome", currentMonthIncome);
            
            return response;
        });
    }
}
