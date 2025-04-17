package by.kirylarol.spendsculptor;

import by.kirylarol.spendsculptor.entities.Account;
import by.kirylarol.spendsculptor.entities.Income;
import by.kirylarol.spendsculptor.entities.IncomeCategory;
import by.kirylarol.spendsculptor.entities.User;
import by.kirylarol.spendsculptor.repos.AccountRepository;
import by.kirylarol.spendsculptor.repos.IncomeCategoryRepository;
import by.kirylarol.spendsculptor.repos.IncomeRepository;
import by.kirylarol.spendsculptor.repos.UserRepository;
import by.kirylarol.spendsculptor.service.IncomeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class IncomeServiceTest {

    @Mock
    private IncomeRepository incomeRepository;

    @Mock
    private IncomeCategoryRepository incomeCategoryRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private IncomeService incomeService;

    private User testUser;
    private Account testAccount;
    private IncomeCategory testCategory;
    private Income testIncome;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testUser = new User();
        testUser.setId(1);

        testAccount = new Account();
        testAccount.setId(1);

        testCategory = new IncomeCategory("Salary");
        testCategory.setIncomeCategoryId(1);

        testIncome = new Income(testUser, testAccount, testCategory, new BigDecimal("1000.00"));
        testIncome.setIncomeId(1);
        testIncome.setDate(LocalDate.now());
        testIncome.setDescription("Monthly salary");
        testIncome.setIsRecurring(true);
        testIncome.setRecurrencePeriod("MONTHLY");
    }

    @Test
    void testGetAllIncomesByUser() {
        // Arrange
        List<Income> expectedIncomes = Arrays.asList(testIncome);
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(incomeRepository.findByUser(testUser)).thenReturn(expectedIncomes);

        // Act
        List<Income> actualIncomes = incomeService.getAllIncomesByUser(1);

        // Assert
        assertEquals(expectedIncomes.size(), actualIncomes.size());
        assertEquals(expectedIncomes, actualIncomes);
        verify(userRepository).findById(1);
        verify(incomeRepository).findByUser(testUser);
    }

    @Test
    void testGetAllIncomesByUserWithInvalidUserId() {
        // Arrange
        when(userRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            incomeService.getAllIncomesByUser(999);
        });
        
        assertTrue(exception.getMessage().contains("User not found"));
        verify(userRepository).findById(999);
        verify(incomeRepository, never()).findByUser(any(User.class));
    }

    @Test
    void testGetAllIncomesByAccount() {
        // Arrange
        List<Income> expectedIncomes = Arrays.asList(testIncome);
        when(accountRepository.findById(1)).thenReturn(Optional.of(testAccount));
        when(incomeRepository.findByAccount(testAccount)).thenReturn(expectedIncomes);

        // Act
        List<Income> actualIncomes = incomeService.getAllIncomesByAccount(1);

        // Assert
        assertEquals(expectedIncomes.size(), actualIncomes.size());
        assertEquals(expectedIncomes, actualIncomes);
        verify(accountRepository).findById(1);
        verify(incomeRepository).findByAccount(testAccount);
    }

    @Test
    void testGetIncomeById() {
        // Arrange
        when(incomeRepository.findById(1)).thenReturn(Optional.of(testIncome));

        // Act
        Optional<Income> result = incomeService.getIncomeById(1);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testIncome, result.get());
        verify(incomeRepository).findById(1);
    }

    @Test
    void testGetIncomesByUserAndDateRange() {
        // Arrange
        LocalDate startDate = LocalDate.now().minusMonths(1);
        LocalDate endDate = LocalDate.now();
        List<Income> expectedIncomes = Arrays.asList(testIncome);
        
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(incomeRepository.findByUserAndDateBetween(testUser, startDate, endDate)).thenReturn(expectedIncomes);

        // Act
        List<Income> actualIncomes = incomeService.getIncomesByUserAndDateRange(1, startDate, endDate);

        // Assert
        assertEquals(expectedIncomes.size(), actualIncomes.size());
        assertEquals(expectedIncomes, actualIncomes);
        verify(userRepository).findById(1);
        verify(incomeRepository).findByUserAndDateBetween(testUser, startDate, endDate);
    }

    @Test
    void testCreateIncome() {
        // Arrange
        when(incomeRepository.save(any(Income.class))).thenReturn(testIncome);

        // Act
        Income createdIncome = incomeService.createIncome(testIncome);

        // Assert
        assertEquals(testIncome, createdIncome);
        verify(incomeRepository).save(testIncome);
    }

    @Test
    void testCreateIncomeWithInvalidData() {
        // Arrange
        Income invalidIncome = new Income();
        invalidIncome.setAmount(new BigDecimal("-100.00")); // Negative amount

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            incomeService.createIncome(invalidIncome);
        });
        
        assertTrue(exception.getMessage().contains("must be positive"));
        verify(incomeRepository, never()).save(any(Income.class));
    }

    @Test
    void testUpdateIncome() {
        // Arrange
        Income updatedIncome = new Income(testUser, testAccount, testCategory, new BigDecimal("1500.00"));
        updatedIncome.setDescription("Updated salary");
        
        when(incomeRepository.findById(1)).thenReturn(Optional.of(testIncome));
        when(incomeRepository.save(any(Income.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Income result = incomeService.updateIncome(1, updatedIncome);

        // Assert
        assertEquals(new BigDecimal("1500.00"), result.getAmount());
        assertEquals("Updated salary", result.getDescription());
        verify(incomeRepository).findById(1);
        verify(incomeRepository).save(any(Income.class));
    }

    @Test
    void testDeleteIncome() {
        // Arrange
        when(incomeRepository.findById(1)).thenReturn(Optional.of(testIncome));

        // Act
        incomeService.deleteIncome(1);

        // Assert
        verify(incomeRepository).findById(1);
        verify(incomeRepository).delete(testIncome);
    }

    @Test
    void testGetTotalIncomeByUserAndDateRange() {
        // Arrange
        LocalDate startDate = LocalDate.now().minusMonths(1);
        LocalDate endDate = LocalDate.now();
        BigDecimal expectedTotal = new BigDecimal("2500.00");
        
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(incomeRepository.sumIncomeByUserAndDateBetween(testUser, startDate, endDate)).thenReturn(expectedTotal);

        // Act
        BigDecimal actualTotal = incomeService.getTotalIncomeByUserAndDateRange(1, startDate, endDate);

        // Assert
        assertEquals(expectedTotal, actualTotal);
        verify(userRepository).findById(1);
        verify(incomeRepository).sumIncomeByUserAndDateBetween(testUser, startDate, endDate);
    }

    @Test
    void testGetTotalIncomeByUserAndDateRangeWithNoData() {
        // Arrange
        LocalDate startDate = LocalDate.now().minusMonths(1);
        LocalDate endDate = LocalDate.now();
        
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(incomeRepository.sumIncomeByUserAndDateBetween(testUser, startDate, endDate)).thenReturn(null);

        // Act
        BigDecimal actualTotal = incomeService.getTotalIncomeByUserAndDateRange(1, startDate, endDate);

        // Assert
        assertEquals(BigDecimal.ZERO, actualTotal);
        verify(userRepository).findById(1);
        verify(incomeRepository).sumIncomeByUserAndDateBetween(testUser, startDate, endDate);
    }

    @Test
    void testGetIncomeByCategoryForUserAndDateRange() {
        // Arrange
        LocalDate startDate = LocalDate.now().minusMonths(1);
        LocalDate endDate = LocalDate.now();
        
        List<Object[]> mockResults = new ArrayList<>();
        mockResults.add(new Object[] { testCategory, new BigDecimal("1000.00") });
        
        IncomeCategory secondCategory = new IncomeCategory("Freelance");
        secondCategory.setIncomeCategoryId(2);
        mockResults.add(new Object[] { secondCategory, new BigDecimal("500.00") });
        
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(incomeRepository.sumIncomeByUserAndCategoryAndDateBetween(testUser, startDate, endDate)).thenReturn(mockResults);

        // Act
        Map<String, BigDecimal> categoryTotals = incomeService.getIncomeByCategoryForUserAndDateRange(1, startDate, endDate);

        // Assert
        assertEquals(2, categoryTotals.size());
        assertEquals(new BigDecimal("1000.00"), categoryTotals.get("Salary"));
        assertEquals(new BigDecimal("500.00"), categoryTotals.get("Freelance"));
        verify(userRepository).findById(1);
        verify(incomeRepository).sumIncomeByUserAndCategoryAndDateBetween(testUser, startDate, endDate);
    }

    @Test
    void testGetMonthlyIncomeForUser() {
        // Arrange
        List<Object[]> mockResults = new ArrayList<>();
        mockResults.add(new Object[] { 2025, 3, new BigDecimal("1000.00") }); // March 2025
        mockResults.add(new Object[] { 2025, 4, new BigDecimal("1200.00") }); // April 2025
        
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(incomeRepository.sumIncomeByUserGroupByMonth(testUser)).thenReturn(mockResults);

        // Act
        Map<YearMonth, BigDecimal> monthlyTotals = incomeService.getMonthlyIncomeForUser(1);

        // Assert
        assertEquals(2, monthlyTotals.size());
        assertEquals(new BigDecimal("1000.00"), monthlyTotals.get(YearMonth.of(2025, 3)));
        assertEquals(new BigDecimal("1200.00"), monthlyTotals.get(YearMonth.of(2025, 4)));
        verify(userRepository).findById(1);
        verify(incomeRepository).sumIncomeByUserGroupByMonth(testUser);
    }

    @Test
    void testGetCurrentMonthIncomeForUser() {
        // Arrange
        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = now.withDayOfMonth(1);
        LocalDate endOfMonth = now.withDayOfMonth(now.lengthOfMonth());
        BigDecimal expectedTotal = new BigDecimal("1500.00");
        
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(incomeRepository.sumIncomeByUserAndDateBetween(testUser, startOfMonth, endOfMonth)).thenReturn(expectedTotal);

        // Act
        BigDecimal actualTotal = incomeService.getCurrentMonthIncomeForUser(1);

        // Assert
        assertEquals(expectedTotal, actualTotal);
        verify(userRepository).findById(1);
        verify(incomeRepository).sumIncomeByUserAndDateBetween(testUser, startOfMonth, endOfMonth);
    }
}
