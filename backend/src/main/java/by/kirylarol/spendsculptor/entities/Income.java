package by.kirylarol.spendsculptor.entities;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "income")
@Data
public class Income {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "income_seq")
    @SequenceGenerator(name = "income_seq", sequenceName = "income_seq", allocationSize = 1)
    @Column(name = "income_id")
    private Integer incomeId;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;
    
    @ManyToOne
    @JoinColumn(name = "income_category_id")
    private IncomeCategory incomeCategory;
    
    @Column(name = "amount")
    private BigDecimal amount;
    
    @Column(name = "description")
    private String description;
    
    @Column(name = "date")
    private LocalDate date;
    
    @Column(name = "is_recurring")
    private Boolean isRecurring;
    
    @Column(name = "recurrence_period")
    private String recurrencePeriod;
    
    // Default constructor
    public Income() {
        this.date = LocalDate.now();
        this.isRecurring = false;
    }
    
    // Constructor with essential fields
    public Income(User user, Account account, IncomeCategory incomeCategory, BigDecimal amount) {
        this.user = user;
        this.account = account;
        this.incomeCategory = incomeCategory;
        this.amount = amount;
        this.date = LocalDate.now();
        this.isRecurring = false;
    }
    
    // Full constructor
    public Income(User user, Account account, IncomeCategory incomeCategory, 
                 BigDecimal amount, String description, LocalDate date, 
                 Boolean isRecurring, String recurrencePeriod) {
        this.user = user;
        this.account = account;
        this.incomeCategory = incomeCategory;
        this.amount = amount;
        this.description = description;
        this.date = date;
        this.isRecurring = isRecurring;
        this.recurrencePeriod = recurrencePeriod;
    }
}
