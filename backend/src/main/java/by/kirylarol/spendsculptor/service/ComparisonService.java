package by.kirylarol.spendsculptor.service;

import by.kirylarol.spendsculptor.entities.Category;
import by.kirylarol.spendsculptor.entities.Income;
import by.kirylarol.spendsculptor.entities.IncomeCategory;
import by.kirylarol.spendsculptor.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ComparisonService {
    
    private final ReceiptService receiptService;
    private final IncomeService incomeService;
    private final CategoryService categoryService;
    private final IncomeCategoryService incomeCategoryService;
    
    @Autowired
    public ComparisonService(ReceiptService receiptService, 
                            IncomeService incomeService,
                            CategoryService categoryService,
                            IncomeCategoryService incomeCategoryService) {
        this.receiptService = receiptService;
        this.incomeService = incomeService;
        this.categoryService = categoryService;
        this.incomeCategoryService = incomeCategoryService;
    }
    
    /**
     * Compare expenses between two periods
     */
    public Map<String, Object> compareExpenses(Integer userId, 
                                              LocalDate period1Start, LocalDate period1End,
                                              LocalDate period2Start, LocalDate period2End) {
        // Get total expenses for both periods
        BigDecimal period1Total = receiptService.getTotalExpensesByUserAndDateRange(userId, period1Start, period1End);
        BigDecimal period2Total = receiptService.getTotalExpensesByUserAndDateRange(userId, period2Start, period2End);
        
        // Calculate difference and percentage change
        BigDecimal difference = period2Total.subtract(period1Total);
        BigDecimal percentageChange = calculatePercentageChange(period1Total, period2Total);
        
        // Prepare result
        Map<String, Object> result = new HashMap<>();
        result.put("period1", createPeriodInfo(period1Start, period1End, period1Total));
        result.put("period2", createPeriodInfo(period2Start, period2End, period2Total));
        result.put("difference", difference);
        result.put("percentageChange", percentageChange);
        result.put("increased", difference.compareTo(BigDecimal.ZERO) > 0);
        
        return result;
    }
    
    /**
     * Compare income between two periods
     */
    public Map<String, Object> compareIncome(Integer userId, 
                                            LocalDate period1Start, LocalDate period1End,
                                            LocalDate period2Start, LocalDate period2End) {
        // Get total income for both periods
        BigDecimal period1Total = incomeService.getTotalIncomeByUserAndDateRange(userId, period1Start, period1End);
        BigDecimal period2Total = incomeService.getTotalIncomeByUserAndDateRange(userId, period2Start, period2End);
        
        // Calculate difference and percentage change
        BigDecimal difference = period2Total.subtract(period1Total);
        BigDecimal percentageChange = calculatePercentageChange(period1Total, period2Total);
        
        // Prepare result
        Map<String, Object> result = new HashMap<>();
        result.put("period1", createPeriodInfo(period1Start, period1End, period1Total));
        result.put("period2", createPeriodInfo(period2Start, period2End, period2Total));
        result.put("difference", difference);
        result.put("percentageChange", percentageChange);
        result.put("increased", difference.compareTo(BigDecimal.ZERO) > 0);
        
        return result;
    }
    
    /**
     * Compare net income (income - expenses) between two periods
     */
    public Map<String, Object> compareNetIncome(Integer userId, 
                                               LocalDate period1Start, LocalDate period1End,
                                               LocalDate period2Start, LocalDate period2End) {
        // Get total income and expenses for both periods
        BigDecimal period1Income = incomeService.getTotalIncomeByUserAndDateRange(userId, period1Start, period1End);
        BigDecimal period1Expenses = receiptService.getTotalExpensesByUserAndDateRange(userId, period1Start, period1End);
        BigDecimal period2Income = incomeService.getTotalIncomeByUserAndDateRange(userId, period2Start, period2End);
        BigDecimal period2Expenses = receiptService.getTotalExpensesByUserAndDateRange(userId, period2Start, period2End);
        
        // Calculate net income for both periods
        BigDecimal period1NetIncome = period1Income.subtract(period1Expenses);
        BigDecimal period2NetIncome = period2Income.subtract(period2Expenses);
        
        // Calculate difference and percentage change
        BigDecimal difference = period2NetIncome.subtract(period1NetIncome);
        BigDecimal percentageChange = calculatePercentageChange(period1NetIncome, period2NetIncome);
        
        // Prepare result
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> period1 = createPeriodInfo(period1Start, period1End, period1NetIncome);
        period1.put("income", period1Income);
        period1.put("expenses", period1Expenses);
        
        Map<String, Object> period2 = createPeriodInfo(period2Start, period2End, period2NetIncome);
        period2.put("income", period2Income);
        period2.put("expenses", period2Expenses);
        
        result.put("period1", period1);
        result.put("period2", period2);
        result.put("difference", difference);
        result.put("percentageChange", percentageChange);
        result.put("improved", period2NetIncome.compareTo(period1NetIncome) > 0);
        
        return result;
    }
    
    /**
     * Compare expenses by category between two periods
     */
    public Map<String, Object> compareExpensesByCategory(Integer userId, 
                                                        LocalDate period1Start, LocalDate period1End,
                                                        LocalDate period2Start, LocalDate period2End) {
        // Get expenses by category for both periods
        Map<String, BigDecimal> period1ByCategory = receiptService.getExpensesByCategoryForUserAndDateRange(userId, period1Start, period1End);
        Map<String, BigDecimal> period2ByCategory = receiptService.getExpensesByCategoryForUserAndDateRange(userId, period2Start, period2End);
        
        // Get all categories
        List<Category> allCategories = categoryService.getAllCategories();
        
        // Prepare comparison data
        Map<String, Map<String, Object>> categoryComparisons = new HashMap<>();
        
        for (Category category : allCategories) {
            String categoryName = category.getCategoryName();
            BigDecimal period1Amount = period1ByCategory.getOrDefault(categoryName, BigDecimal.ZERO);
            BigDecimal period2Amount = period2ByCategory.getOrDefault(categoryName, BigDecimal.ZERO);
            
            if (period1Amount.compareTo(BigDecimal.ZERO) > 0 || period2Amount.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal difference = period2Amount.subtract(period1Amount);
                BigDecimal percentageChange = calculatePercentageChange(period1Amount, period2Amount);
                
                Map<String, Object> comparison = new HashMap<>();
                comparison.put("period1Amount", period1Amount);
                comparison.put("period2Amount", period2Amount);
                comparison.put("difference", difference);
                comparison.put("percentageChange", percentageChange);
                comparison.put("increased", difference.compareTo(BigDecimal.ZERO) > 0);
                
                categoryComparisons.put(categoryName, comparison);
            }
        }
        
        // Prepare result
        Map<String, Object> result = new HashMap<>();
        result.put("period1", createPeriodInfo(period1Start, period1End, null));
        result.put("period2", createPeriodInfo(period2Start, period2End, null));
        result.put("categoryComparisons", categoryComparisons);
        
        return result;
    }
    
    /**
     * Compare income by category between two periods
     */
    public Map<String, Object> compareIncomeByCategory(Integer userId, 
                                                     LocalDate period1Start, LocalDate period1End,
                                                     LocalDate period2Start, LocalDate period2End) {
        // Get income by category for both periods
        Map<String, BigDecimal> period1ByCategory = incomeService.getIncomeByCategoryForUserAndDateRange(userId, period1Start, period1End);
        Map<String, BigDecimal> period2ByCategory = incomeService.getIncomeByCategoryForUserAndDateRange(userId, period2Start, period2End);
        
        // Get all income categories
        List<IncomeCategory> allCategories = incomeCategoryService.getAllCategories();
        
        // Prepare comparison data
        Map<String, Map<String, Object>> categoryComparisons = new HashMap<>();
        
        for (IncomeCategory category : allCategories) {
            String categoryName = category.getName();
            BigDecimal period1Amount = period1ByCategory.getOrDefault(categoryName, BigDecimal.ZERO);
            BigDecimal period2Amount = period2ByCategory.getOrDefault(categoryName, BigDecimal.ZERO);
            
            if (period1Amount.compareTo(BigDecimal.ZERO) > 0 || period2Amount.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal difference = period2Amount.subtract(period1Amount);
                BigDecimal percentageChange = calculatePercentageChange(period1Amount, period2Amount);
                
                Map<String, Object> comparison = new HashMap<>();
                comparison.put("period1Amount", period1Amount);
                comparison.put("period2Amount", period2Amount);
                comparison.put("difference", difference);
                comparison.put("percentageChange", percentageChange);
                comparison.put("increased", difference.compareTo(BigDecimal.ZERO) > 0);
                
                categoryComparisons.put(categoryName, comparison);
            }
        }
        
        // Prepare result
        Map<String, Object> result = new HashMap<>();
        result.put("period1", createPeriodInfo(period1Start, period1End, null));
        result.put("period2", createPeriodInfo(period2Start, period2End, null));
        result.put("categoryComparisons", categoryComparisons);
        
        return result;
    }
    
    /**
     * Get predefined periods for comparison
     */
    public Map<String, Map<String, LocalDate>> getPredefinedPeriods() {
        Map<String, Map<String, LocalDate>> predefinedPeriods = new HashMap<>();
        LocalDate today = LocalDate.now();
        
        // Current month vs previous month
        YearMonth currentMonth = YearMonth.from(today);
        YearMonth previousMonth = currentMonth.minusMonths(1);
        
        predefinedPeriods.put("currentVsPreviousMonth", Map.of(
            "period1Start", previousMonth.atDay(1),
            "period1End", previousMonth.atEndOfMonth(),
            "period2Start", currentMonth.atDay(1),
            "period2End", currentMonth.atEndOfMonth()
        ));
        
        // Current month vs same month last year
        YearMonth sameMonthLastYear = currentMonth.minusYears(1);
        
        predefinedPeriods.put("currentVsSameMonthLastYear", Map.of(
            "period1Start", sameMonthLastYear.atDay(1),
            "period1End", sameMonthLastYear.atEndOfMonth(),
            "period2Start", currentMonth.atDay(1),
            "period2End", currentMonth.atEndOfMonth()
        ));
        
        // Current quarter vs previous quarter
        int currentQuarter = (today.getMonthValue() - 1) / 3 + 1;
        LocalDate currentQuarterStart = LocalDate.of(today.getYear(), (currentQuarter - 1) * 3 + 1, 1);
        LocalDate currentQuarterEnd = LocalDate.of(today.getYear(), currentQuarter * 3, 1)
            .plusMonths(1).minusDays(1);
        
        LocalDate previousQuarterStart = currentQuarterStart.minusMonths(3);
        LocalDate previousQuarterEnd = currentQuarterStart.minusDays(1);
        
        predefinedPeriods.put("currentVsPreviousQuarter", Map.of(
            "period1Start", previousQuarterStart,
            "period1End", previousQuarterEnd,
            "period2Start", currentQuarterStart,
            "period2End", currentQuarterEnd
        ));
        
        // Current year vs previous year
        int currentYear = today.getYear();
        
        predefinedPeriods.put("currentVsPreviousYear", Map.of(
            "period1Start", LocalDate.of(currentYear - 1, 1, 1),
            "period1End", LocalDate.of(currentYear - 1, 12, 31),
            "period2Start", LocalDate.of(currentYear, 1, 1),
            "period2End", LocalDate.of(currentYear, 12, 31)
        ));
        
        return predefinedPeriods;
    }
    
    /**
     * Helper method to calculate percentage change
     */
    private BigDecimal calculatePercentageChange(BigDecimal oldValue, BigDecimal newValue) {
        if (oldValue.compareTo(BigDecimal.ZERO) == 0) {
            // If old value is zero, and new value is not zero, it's a 100% increase
            return newValue.compareTo(BigDecimal.ZERO) == 0 ? 
                BigDecimal.ZERO : new BigDecimal("100");
        }
        
        return newValue.subtract(oldValue)
            .divide(oldValue.abs(), 4, RoundingMode.HALF_UP)
            .multiply(new BigDecimal("100"))
            .setScale(2, RoundingMode.HALF_UP);
    }
    
    /**
     * Helper method to create period info
     */
    private Map<String, Object> createPeriodInfo(LocalDate startDate, LocalDate endDate, BigDecimal amount) {
        Map<String, Object> periodInfo = new HashMap<>();
        periodInfo.put("startDate", startDate);
        periodInfo.put("endDate", endDate);
        periodInfo.put("durationDays", ChronoUnit.DAYS.between(startDate, endDate) + 1);
        
        if (amount != null) {
            periodInfo.put("amount", amount);
        }
        
        return periodInfo;
    }
}
