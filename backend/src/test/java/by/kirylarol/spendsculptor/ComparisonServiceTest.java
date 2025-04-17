package by.kirylarol.spendsculptor;

import by.kirylarol.spendsculptor.service.ComparisonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

public class ComparisonServiceTest {

    @Mock
    private by.kirylarol.spendsculptor.service.ReceiptService receiptService;

    @Mock
    private by.kirylarol.spendsculptor.service.IncomeService incomeService;

    @Mock
    private by.kirylarol.spendsculptor.service.CategoryService categoryService;

    @Mock
    private by.kirylarol.spendsculptor.service.IncomeCategoryService incomeCategoryService;

    @InjectMocks
    private ComparisonService comparisonService;

    private LocalDate period1Start;
    private LocalDate period1End;
    private LocalDate period2Start;
    private LocalDate period2End;
    private Integer userId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        userId = 1;
        period1Start = LocalDate.of(2025, 1, 1);
        period1End = LocalDate.of(2025, 1, 31);
        period2Start = LocalDate.of(2025, 2, 1);
        period2End = LocalDate.of(2025, 2, 28);

        // Mock receipt service
        when(receiptService.getTotalExpensesByUserAndDateRange(anyInt(), any(LocalDate.class), any(LocalDate.class)))
            .thenAnswer(invocation -> {
                LocalDate start = invocation.getArgument(1);
                if (start.equals(period1Start)) {
                    return new BigDecimal("1000.00");
                } else {
                    return new BigDecimal("1200.00");
                }
            });

        // Mock income service
        when(incomeService.getTotalIncomeByUserAndDateRange(anyInt(), any(LocalDate.class), any(LocalDate.class)))
            .thenAnswer(invocation -> {
                LocalDate start = invocation.getArgument(1);
                if (start.equals(period1Start)) {
                    return new BigDecimal("1500.00");
                } else {
                    return new BigDecimal("1800.00");
                }
            });

        // Mock category expense data
        Map<String, BigDecimal> period1ExpensesByCategory = new HashMap<>();
        period1ExpensesByCategory.put("Food", new BigDecimal("400.00"));
        period1ExpensesByCategory.put("Transportation", new BigDecimal("300.00"));
        period1ExpensesByCategory.put("Entertainment", new BigDecimal("200.00"));

        Map<String, BigDecimal> period2ExpensesByCategory = new HashMap<>();
        period2ExpensesByCategory.put("Food", new BigDecimal("450.00"));
        period2ExpensesByCategory.put("Transportation", new BigDecimal("350.00"));
        period2ExpensesByCategory.put("Entertainment", new BigDecimal("250.00"));

        when(receiptService.getExpensesByCategoryForUserAndDateRange(anyInt(), any(LocalDate.class), any(LocalDate.class)))
            .thenAnswer(invocation -> {
                LocalDate start = invocation.getArgument(1);
                if (start.equals(period1Start)) {
                    return period1ExpensesByCategory;
                } else {
                    return period2ExpensesByCategory;
                }
            });

        // Mock category income data
        Map<String, BigDecimal> period1IncomeByCategory = new HashMap<>();
        period1IncomeByCategory.put("Salary", new BigDecimal("1200.00"));
        period1IncomeByCategory.put("Freelance", new BigDecimal("300.00"));

        Map<String, BigDecimal> period2IncomeByCategory = new HashMap<>();
        period2IncomeByCategory.put("Salary", new BigDecimal("1400.00"));
        period2IncomeByCategory.put("Freelance", new BigDecimal("400.00"));

        when(incomeService.getIncomeByCategoryForUserAndDateRange(anyInt(), any(LocalDate.class), any(LocalDate.class)))
            .thenAnswer(invocation -> {
                LocalDate start = invocation.getArgument(1);
                if (start.equals(period1Start)) {
                    return period1IncomeByCategory;
                } else {
                    return period2IncomeByCategory;
                }
            });
    }

    @Test
    void testCompareExpenses() {
        // Act
        Map<String, Object> result = comparisonService.compareExpenses(
            userId, period1Start, period1End, period2Start, period2End);

        // Assert
        assertNotNull(result);
        assertEquals(new BigDecimal("1000.00"), ((Map<String, Object>) result.get("period1")).get("amount"));
        assertEquals(new BigDecimal("1200.00"), ((Map<String, Object>) result.get("period2")).get("amount"));
        assertEquals(new BigDecimal("200.00"), result.get("difference"));
        assertEquals(new BigDecimal("20.00"), result.get("percentageChange"));
        assertEquals(true, result.get("increased"));
    }

    @Test
    void testCompareIncome() {
        // Act
        Map<String, Object> result = comparisonService.compareIncome(
            userId, period1Start, period1End, period2Start, period2End);

        // Assert
        assertNotNull(result);
        assertEquals(new BigDecimal("1500.00"), ((Map<String, Object>) result.get("period1")).get("amount"));
        assertEquals(new BigDecimal("1800.00"), ((Map<String, Object>) result.get("period2")).get("amount"));
        assertEquals(new BigDecimal("300.00"), result.get("difference"));
        assertEquals(new BigDecimal("20.00"), result.get("percentageChange"));
        assertEquals(true, result.get("increased"));
    }

    @Test
    void testCompareNetIncome() {
        // Act
        Map<String, Object> result = comparisonService.compareNetIncome(
            userId, period1Start, period1End, period2Start, period2End);

        // Assert
        assertNotNull(result);
        
        Map<String, Object> period1 = (Map<String, Object>) result.get("period1");
        Map<String, Object> period2 = (Map<String, Object>) result.get("period2");
        
        assertEquals(new BigDecimal("1500.00"), period1.get("income"));
        assertEquals(new BigDecimal("1000.00"), period1.get("expenses"));
        assertEquals(new BigDecimal("500.00"), period1.get("amount"));
        
        assertEquals(new BigDecimal("1800.00"), period2.get("income"));
        assertEquals(new BigDecimal("1200.00"), period2.get("expenses"));
        assertEquals(new BigDecimal("600.00"), period2.get("amount"));
        
        assertEquals(new BigDecimal("100.00"), result.get("difference"));
        assertEquals(new BigDecimal("20.00"), result.get("percentageChange"));
        assertEquals(true, result.get("improved"));
    }

    @Test
    void testCompareExpensesByCategory() {
        // Act
        Map<String, Object> result = comparisonService.compareExpensesByCategory(
            userId, period1Start, period1End, period2Start, period2End);

        // Assert
        assertNotNull(result);
        
        Map<String, Map<String, Object>> categoryComparisons = 
            (Map<String, Map<String, Object>>) result.get("categoryComparisons");
        
        assertNotNull(categoryComparisons);
        assertTrue(categoryComparisons.containsKey("Food"));
        assertTrue(categoryComparisons.containsKey("Transportation"));
        assertTrue(categoryComparisons.containsKey("Entertainment"));
        
        Map<String, Object> foodComparison = categoryComparisons.get("Food");
        assertEquals(new BigDecimal("400.00"), foodComparison.get("period1Amount"));
        assertEquals(new BigDecimal("450.00"), foodComparison.get("period2Amount"));
        assertEquals(new BigDecimal("50.00"), foodComparison.get("difference"));
        assertEquals(new BigDecimal("12.50"), foodComparison.get("percentageChange"));
        assertEquals(true, foodComparison.get("increased"));
    }

    @Test
    void testCompareIncomeByCategory() {
        // Act
        Map<String, Object> result = comparisonService.compareIncomeByCategory(
            userId, period1Start, period1End, period2Start, period2End);

        // Assert
        assertNotNull(result);
        
        Map<String, Map<String, Object>> categoryComparisons = 
            (Map<String, Map<String, Object>>) result.get("categoryComparisons");
        
        assertNotNull(categoryComparisons);
        assertTrue(categoryComparisons.containsKey("Salary"));
        assertTrue(categoryComparisons.containsKey("Freelance"));
        
        Map<String, Object> salaryComparison = categoryComparisons.get("Salary");
        assertEquals(new BigDecimal("1200.00"), salaryComparison.get("period1Amount"));
        assertEquals(new BigDecimal("1400.00"), salaryComparison.get("period2Amount"));
        assertEquals(new BigDecimal("200.00"), salaryComparison.get("difference"));
        assertEquals(new BigDecimal("16.67"), salaryComparison.get("percentageChange"));
        assertEquals(true, salaryComparison.get("increased"));
    }

    @Test
    void testGetPredefinedPeriods() {
        // Act
        Map<String, Map<String, LocalDate>> predefinedPeriods = comparisonService.getPredefinedPeriods();

        // Assert
        assertNotNull(predefinedPeriods);
        assertTrue(predefinedPeriods.containsKey("currentVsPreviousMonth"));
        assertTrue(predefinedPeriods.containsKey("currentVsSameMonthLastYear"));
        assertTrue(predefinedPeriods.containsKey("currentVsPreviousQuarter"));
        assertTrue(predefinedPeriods.containsKey("currentVsPreviousYear"));
        
        Map<String, LocalDate> monthComparison = predefinedPeriods.get("currentVsPreviousMonth");
        assertNotNull(monthComparison.get("period1Start"));
        assertNotNull(monthComparison.get("period1End"));
        assertNotNull(monthComparison.get("period2Start"));
        assertNotNull(monthComparison.get("period2End"));
    }
}
