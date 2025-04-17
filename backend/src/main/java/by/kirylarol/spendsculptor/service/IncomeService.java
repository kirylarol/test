package by.kirylarol.spendsculptor.service;

import by.kirylarol.spendsculptor.entities.Account;
import by.kirylarol.spendsculptor.entities.Income;
import by.kirylarol.spendsculptor.entities.IncomeCategory;
import by.kirylarol.spendsculptor.entities.User;
import by.kirylarol.spendsculptor.repos.AccountRepository;
import by.kirylarol.spendsculptor.repos.IncomeCategoryRepository;
import by.kirylarol.spendsculptor.repos.IncomeRepository;
import by.kirylarol.spendsculptor.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class IncomeService {
    
    private final IncomeRepository incomeRepository;
    private final IncomeCategoryRepository incomeCategoryRepository;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    
    @Autowired
    public IncomeService(IncomeRepository incomeRepository, 
                        IncomeCategoryRepository incomeCategoryRepository,
                        UserRepository userRepository,
                        AccountRepository accountRepository) {
        this.incomeRepository = incomeRepository;
        this.incomeCategoryRepository = incomeCategoryRepository;
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
    }
    
    public List<Income> getAllIncomesByUser(Integer userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        return incomeRepository.findByUser(user);
    }
    
    public List<Income> getAllIncomesByAccount(Integer accountId) {
        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new IllegalArgumentException("Account not found with id: " + accountId));
        return incomeRepository.findByAccount(account);
    }
    
    public Optional<Income> getIncomeById(Integer id) {
        return incomeRepository.findById(id);
    }
    
    public List<Income> getIncomesByUserAndDateRange(Integer userId, LocalDate startDate, LocalDate endDate) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        return incomeRepository.findByUserAndDateBetween(user, startDate, endDate);
    }
    
    public List<Income> getIncomesByAccountAndDateRange(Integer accountId, LocalDate startDate, LocalDate endDate) {
        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new IllegalArgumentException("Account not found with id: " + accountId));
        return incomeRepository.findByAccountAndDateBetween(account, startDate, endDate);
    }
    
    @Transactional
    public Income createIncome(Income income) {
        validateIncome(income);
        return incomeRepository.save(income);
    }
    
    @Transactional
    public Income updateIncome(Integer id, Income incomeDetails) {
        Income income = incomeRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Income not found with id: " + id));
        
        income.setAccount(incomeDetails.getAccount());
        income.setIncomeCategory(incomeDetails.getIncomeCategory());
        income.setAmount(incomeDetails.getAmount());
        income.setDescription(incomeDetails.getDescription());
        income.setDate(incomeDetails.getDate());
        income.setIsRecurring(incomeDetails.getIsRecurring());
        income.setRecurrencePeriod(incomeDetails.getRecurrencePeriod());
        
        validateIncome(income);
        return incomeRepository.save(income);
    }
    
    @Transactional
    public void deleteIncome(Integer id) {
        Income income = incomeRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Income not found with id: " + id));
        incomeRepository.delete(income);
    }
    
    public BigDecimal getTotalIncomeByUserAndDateRange(Integer userId, LocalDate startDate, LocalDate endDate) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        
        BigDecimal total = incomeRepository.sumIncomeByUserAndDateBetween(user, startDate, endDate);
        return total != null ? total : BigDecimal.ZERO;
    }
    
    public BigDecimal getTotalIncomeByAccountAndDateRange(Integer accountId, LocalDate startDate, LocalDate endDate) {
        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new IllegalArgumentException("Account not found with id: " + accountId));
        
        BigDecimal total = incomeRepository.sumIncomeByAccountAndDateBetween(account, startDate, endDate);
        return total != null ? total : BigDecimal.ZERO;
    }
    
    public Map<String, BigDecimal> getIncomeByCategoryForUserAndDateRange(Integer userId, LocalDate startDate, LocalDate endDate) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        
        List<Object[]> results = incomeRepository.sumIncomeByUserAndCategoryAndDateBetween(user, startDate, endDate);
        
        Map<String, BigDecimal> categoryTotals = new HashMap<>();
        for (Object[] result : results) {
            IncomeCategory category = (IncomeCategory) result[0];
            BigDecimal amount = (BigDecimal) result[1];
            categoryTotals.put(category.getName(), amount);
        }
        
        return categoryTotals;
    }
    
    public Map<YearMonth, BigDecimal> getMonthlyIncomeForUser(Integer userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        
        List<Object[]> results = incomeRepository.sumIncomeByUserGroupByMonth(user);
        
        Map<YearMonth, BigDecimal> monthlyTotals = new HashMap<>();
        for (Object[] result : results) {
            Integer year = (Integer) result[0];
            Integer month = (Integer) result[1];
            BigDecimal amount = (BigDecimal) result[2];
            
            YearMonth yearMonth = YearMonth.of(year, month);
            monthlyTotals.put(yearMonth, amount);
        }
        
        return monthlyTotals;
    }
    
    public BigDecimal getCurrentMonthIncomeForUser(Integer userId) {
        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = now.withDayOfMonth(1);
        LocalDate endOfMonth = now.withDayOfMonth(now.lengthOfMonth());
        
        return getTotalIncomeByUserAndDateRange(userId, startOfMonth, endOfMonth);
    }
    
    private void validateIncome(Income income) {
        if (income.getUser() == null) {
            throw new IllegalArgumentException("Income must be associated with a user");
        }
        
        if (income.getAccount() == null) {
            throw new IllegalArgumentException("Income must be associated with an account");
        }
        
        if (income.getIncomeCategory() == null) {
            throw new IllegalArgumentException("Income must have a category");
        }
        
        if (income.getAmount() == null || income.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Income amount must be positive");
        }
        
        if (income.getDate() == null) {
            income.setDate(LocalDate.now());
        }
        
        if (income.getIsRecurring() == null) {
            income.setIsRecurring(false);
        }
        
        // If recurring, ensure recurrence period is set
        if (Boolean.TRUE.equals(income.getIsRecurring()) && 
            (income.getRecurrencePeriod() == null || income.getRecurrencePeriod().isEmpty())) {
            throw new IllegalArgumentException("Recurring income must have a recurrence period");
        }
    }
}
