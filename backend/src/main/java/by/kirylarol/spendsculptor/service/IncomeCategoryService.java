package by.kirylarol.spendsculptor.service;

import by.kirylarol.spendsculptor.entities.IncomeCategory;
import by.kirylarol.spendsculptor.repos.IncomeCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class IncomeCategoryService {
    
    private final IncomeCategoryRepository incomeCategoryRepository;
    
    @Autowired
    public IncomeCategoryService(IncomeCategoryRepository incomeCategoryRepository) {
        this.incomeCategoryRepository = incomeCategoryRepository;
    }
    
    public List<IncomeCategory> getAllCategories() {
        return incomeCategoryRepository.findAllByOrderByNameAsc();
    }
    
    public Optional<IncomeCategory> getCategoryById(Integer id) {
        return incomeCategoryRepository.findById(id);
    }
    
    public Optional<IncomeCategory> getCategoryByName(String name) {
        return incomeCategoryRepository.findByName(name);
    }
    
    @Transactional
    public IncomeCategory createCategory(IncomeCategory category) {
        // Check if category with the same name already exists
        if (incomeCategoryRepository.existsByName(category.getName())) {
            throw new IllegalArgumentException("Income category with name '" + category.getName() + "' already exists");
        }
        return incomeCategoryRepository.save(category);
    }
    
    @Transactional
    public IncomeCategory updateCategory(Integer id, IncomeCategory categoryDetails) {
        IncomeCategory category = incomeCategoryRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Income category not found with id: " + id));
        
        // Check if new name conflicts with existing category
        if (!category.getName().equals(categoryDetails.getName()) && 
            incomeCategoryRepository.existsByName(categoryDetails.getName())) {
            throw new IllegalArgumentException("Income category with name '" + categoryDetails.getName() + "' already exists");
        }
        
        category.setName(categoryDetails.getName());
        category.setDescription(categoryDetails.getDescription());
        
        return incomeCategoryRepository.save(category);
    }
    
    @Transactional
    public void deleteCategory(Integer id) {
        IncomeCategory category = incomeCategoryRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Income category not found with id: " + id));
        
        // Check if category has associated incomes
        if (!category.getIncomes().isEmpty()) {
            throw new IllegalStateException("Cannot delete category that has associated income entries");
        }
        
        incomeCategoryRepository.delete(category);
    }
    
    @Transactional
    public void initializeDefaultCategories() {
        // Create default income categories if they don't exist
        if (incomeCategoryRepository.count() == 0) {
            createCategoryIfNotExists("Salary", "Regular employment income");
            createCategoryIfNotExists("Freelance", "Income from freelance work");
            createCategoryIfNotExists("Investments", "Income from investments");
            createCategoryIfNotExists("Gifts", "Money received as gifts");
            createCategoryIfNotExists("Other", "Miscellaneous income sources");
        }
    }
    
    private void createCategoryIfNotExists(String name, String description) {
        if (!incomeCategoryRepository.existsByName(name)) {
            IncomeCategory category = new IncomeCategory(name, description);
            incomeCategoryRepository.save(category);
        }
    }
}
