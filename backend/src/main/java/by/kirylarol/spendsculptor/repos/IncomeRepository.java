package by.kirylarol.spendsculptor.repos;

import by.kirylarol.spendsculptor.entities.Account;
import by.kirylarol.spendsculptor.entities.Income;
import by.kirylarol.spendsculptor.entities.IncomeCategory;
import by.kirylarol.spendsculptor.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface IncomeRepository extends JpaRepository<Income, Integer> {
    
    List<Income> findByUser(User user);
    
    List<Income> findByAccount(Account account);
    
    List<Income> findByIncomeCategory(IncomeCategory category);
    
    List<Income> findByUserAndDateBetween(User user, LocalDate startDate, LocalDate endDate);
    
    List<Income> findByAccountAndDateBetween(Account account, LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT SUM(i.amount) FROM Income i WHERE i.user = :user AND i.date BETWEEN :startDate AND :endDate")
    BigDecimal sumIncomeByUserAndDateBetween(@Param("user") User user, 
                                           @Param("startDate") LocalDate startDate, 
                                           @Param("endDate") LocalDate endDate);
    
    @Query("SELECT SUM(i.amount) FROM Income i WHERE i.account = :account AND i.date BETWEEN :startDate AND :endDate")
    BigDecimal sumIncomeByAccountAndDateBetween(@Param("account") Account account, 
                                              @Param("startDate") LocalDate startDate, 
                                              @Param("endDate") LocalDate endDate);
    
    @Query("SELECT i.incomeCategory, SUM(i.amount) FROM Income i WHERE i.user = :user AND i.date BETWEEN :startDate AND :endDate GROUP BY i.incomeCategory")
    List<Object[]> sumIncomeByUserAndCategoryAndDateBetween(@Param("user") User user, 
                                                          @Param("startDate") LocalDate startDate, 
                                                          @Param("endDate") LocalDate endDate);
    
    @Query("SELECT FUNCTION('YEAR', i.date) as year, FUNCTION('MONTH', i.date) as month, SUM(i.amount) FROM Income i WHERE i.user = :user GROUP BY FUNCTION('YEAR', i.date), FUNCTION('MONTH', i.date) ORDER BY year, month")
    List<Object[]> sumIncomeByUserGroupByMonth(@Param("user") User user);
}
