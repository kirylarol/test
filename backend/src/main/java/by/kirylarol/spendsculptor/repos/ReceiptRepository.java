package by.kirylarol.spendsculptor.repos;

import by.kirylarol.spendsculptor.entities.Account;
import by.kirylarol.spendsculptor.entities.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;


@Repository
public interface ReceiptRepository extends JpaRepository<Receipt, Integer> {
    List<Receipt> findAllByAccountId(int id);

    List<Receipt> findAllByDateBetween(LocalDate date1, LocalDate date2);


    @Query ("SELECT SUM (e.total) FROM Receipt e WHERE e.account.account = :account_id AND e.date BETWEEN :start AND :end")
    BigDecimal getTotalByAccount (@Param("account_id") Account account_id, @Param("start") LocalDate start, @Param("end") LocalDate end);


    @Query("SELECT rec FROM Receipt rec INNER JOIN rec.account au WHERE au.user.id = :user_id")
    List<Receipt> getReceiptsByUserId(@Param("user_id") int id);
}
