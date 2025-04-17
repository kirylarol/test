package by.kirylarol.spendsculptor.entities;

import jakarta.persistence.*;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "income_category")
@Data
public class IncomeCategory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "income_category_seq")
    @SequenceGenerator(name = "income_category_seq", sequenceName = "income_category_seq", allocationSize = 1)
    @Column(name = "income_category_id")
    private Integer incomeCategoryId;
    
    @Column(name = "name", unique = true)
    private String name;
    
    @Column(name = "description")
    private String description;
    
    @OneToMany(mappedBy = "incomeCategory", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Income> incomes = new ArrayList<>();
    
    // Default constructor
    public IncomeCategory() {
    }
    
    // Constructor with name
    public IncomeCategory(String name) {
        this.name = name;
    }
    
    // Constructor with name and description
    public IncomeCategory(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
