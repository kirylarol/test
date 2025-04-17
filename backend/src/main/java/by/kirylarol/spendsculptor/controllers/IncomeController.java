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
public class IncomeController {
    
    private final IncomeService incomeService;
    private final IncomeCategoryService incomeCategoryService;
    private final AccountService accountService;
    private final Util util;
    
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
        try {
            User user = util.getUser();
            List<Income> incomes = incomeService.getAllIncomesByUser(user.getId());
            return ResponseEntity.ok(incomes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getIncomeById(@PathVariable Integer id) {
        try {
            User user = util.getUser();
            return incomeService.getIncomeById(id)
                .map(income -> {
                    // Verify the income belongs to the current user
                    if (income.getUser().getId().equals(user.getId())) {
                        return ResponseEntity.ok(income);
                    } else {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                    }
                })
                .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    
    @GetMapping("/account/{accountId}")
    public ResponseEntity<?> getIncomesByAccount(@PathVariable Integer accountId) {
        try {
            User user = util.getUser();
            // Verify user has access to the account
            Account account = accountService.getById(accountId);
            if (account == null) {
                return ResponseEntity.notFound().build();
            }
            
            List<Income> incomes = incomeService.getAllIncomesByAccount(accountId);
            return ResponseEntity.ok(incomes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    
    @GetMapping("/date-range")
    public ResponseEntity<?> getIncomesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            User user = util.getUser();
            List<Income> incomes = incomeService.getIncomesByUserAndDateRange(user.getId(), startDate, endDate);
            return ResponseEntity.ok(incomes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    
    @PostMapping
    public ResponseEntity<?> createIncome(@RequestBody Income incomeRequest) {
        try {
            User user = util.getUser();
            
            // Set the user for the income
            incomeRequest.setUser(user);
            
            // Validate and get the account
            Account account = accountService.getById(incomeRequest.getAccount().getId());
            if (account == null) {
                return ResponseEntity.badRequest().body("Invalid account ID");
            }
            incomeRequest.setAccount(account);
            
            // Validate and get the category
            IncomeCategory category = incomeCategoryService.getCategoryById(incomeRequest.getIncomeCategory().getIncomeCategoryId())
                .orElse(null);
            if (category == null) {
                return ResponseEntity.badRequest().body("Invalid income category ID");
            }
            incomeRequest.setIncomeCategory(category);
            
            Income createdIncome = incomeService.createIncome(incomeRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdIncome);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateIncome(@PathVariable Integer id, @RequestBody Income incomeRequest) {
        try {
            User user = util.getUser();
            
            // Check if income exists and belongs to the user
            Income existingIncome = incomeService.getIncomeById(id).orElse(null);
            if (existingIncome == null) {
                return ResponseEntity.notFound().build();
            }
            
            if (!existingIncome.getUser().getId().equals(user.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            // Keep the original user
            incomeRequest.setUser(existingIncome.getUser());
            
            // Validate and get the account
            Account account = accountService.getById(incomeRequest.getAccount().getId());
            if (account == null) {
                return ResponseEntity.badRequest().body("Invalid account ID");
            }
            incomeRequest.setAccount(account);
            
            // Validate and get the category
            IncomeCategory category = incomeCategoryService.getCategoryById(incomeRequest.getIncomeCategory().getIncomeCategoryId())
                .orElse(null);
            if (category == null) {
                return ResponseEntity.badRequest().body("Invalid income category ID");
            }
            incomeRequest.setIncomeCategory(category);
            
            Income updatedIncome = incomeService.updateIncome(id, incomeRequest);
            return ResponseEntity.ok(updatedIncome);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteIncome(@PathVariable Integer id) {
        try {
            User user = util.getUser();
            
            // Check if income exists and belongs to the user
            Income existingIncome = incomeService.getIncomeById(id).orElse(null);
            if (existingIncome == null) {
                return ResponseEntity.notFound().build();
            }
            
            if (!existingIncome.getUser().getId().equals(user.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            incomeService.deleteIncome(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    
    @GetMapping("/analysis/total")
    public ResponseEntity<?> getTotalIncome(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            User user = util.getUser();
            
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
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    
    @GetMapping("/analysis/by-category")
    public ResponseEntity<?> getIncomeByCategory(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            User user = util.getUser();
            
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
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    
    @GetMapping("/analysis/monthly")
    public ResponseEntity<?> getMonthlyIncome() {
        try {
            User user = util.getUser();
            
            Map<YearMonth, BigDecimal> monthlyTotals = incomeService.getMonthlyIncomeForUser(user.getId());
            
            // Convert YearMonth keys to strings for JSON serialization
            Map<String, BigDecimal> response = new HashMap<>();
            monthlyTotals.forEach((yearMonth, amount) -> 
                response.put(yearMonth.toString(), amount));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    
    @GetMapping("/analysis/current-month")
    public ResponseEntity<?> getCurrentMonthIncome() {
        try {
            User user = util.getUser();
            
            BigDecimal currentMonthIncome = incomeService.getCurrentMonthIncomeForUser(user.getId());
            
            Map<String, Object> response = new HashMap<>();
            response.put("month", YearMonth.now().toString());
            response.put("totalIncome", currentMonthIncome);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
