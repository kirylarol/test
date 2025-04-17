package by.kirylarol.spendsculptor.service;

import by.kirylarol.spendsculptor.entities.Category;
import by.kirylarol.spendsculptor.entities.CategoryLimitNotification;
import by.kirylarol.spendsculptor.entities.Position;
import by.kirylarol.spendsculptor.entities.User;
import by.kirylarol.spendsculptor.repos.CategoryLimitNotificationRepository;
import by.kirylarol.spendsculptor.repos.CategoryRepository;
import by.kirylarol.spendsculptor.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryLimitNotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository, 
                          CategoryLimitNotificationRepository notificationRepository,
                          UserRepository userRepository) {
        this.categoryRepository = categoryRepository;
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    public List<Category> getAll() {
        return categoryRepository.findAll();
    }

    public Category getById(int id) {
        return categoryRepository.findById(id).orElse(null);
    }

    public Category getByName(String name) {
        return categoryRepository.findDistinctFirstByCategoryName(name);
    }

    @Transactional
    public Category createCategory(Category category) {
        if (category != null) {
            Category dbCategory = categoryRepository.findByCategoryName(category.categoryName());
            if (dbCategory != null) return dbCategory;
            category.setPositions(new ArrayList<>());
            return categoryRepository.save(category);
        }
        return null;
    }

    public void predictCategory(List<Position> positionList, int userId) {
        for (var elem : positionList) {
            if (elem.getCategory() == null) {
                List<Category> res = categoryRepository.findTopCategoryByNameAndUserId(elem.getName(), userId);
                if (!res.isEmpty()) {
                    elem.setCategory(res.get(0));
                }
            }
        }
    }

    @Transactional
    public void deleteCategory(String name) {
        Category category = categoryRepository.findDistinctFirstByCategoryName(name);
        if (category != null) deleteCategory(category);
    }

    @Transactional
    public void deleteCategory(Category category) {
        categoryRepository.delete(category);
    }

    @Transactional
    public Category updateCategory(String newName, Category category) {
        if (this.getByName(newName) != null) {
            categoryRepository.deleteById(category.categoryId());
            return this.getByName(newName);
        }
        category.setCategoryName(newName);
        return categoryRepository.save(category);
    }

    @Transactional
    public Category createCategory(String name) {
        Category category1 = categoryRepository.findByCategoryName(name);
        if (category1 != null) return category1;
        Category category = new Category(name);
        return createCategory(category);
    }

    public List<Category> findAllByUser(int id) {
        return categoryRepository.findAllByUser(id);
    }

    public List<Category> findAllByAccountUser(int id){
        return categoryRepository.findAllByAccountUser(id);
    }
    
    // New methods for spending limits feature
    
    @Transactional
    public Category setSpendingLimit(int categoryId, Double limit, Integer threshold) {
        Category category = getById(categoryId);
        if (category != null) {
            category.setSpendingLimit(limit);
            if (threshold != null) {
                category.setNotificationThreshold(threshold);
            }
            return categoryRepository.save(category);
        }
        return null;
    }
    
    public Double getCurrentMonthSpending(int categoryId) {
        Category category = getById(categoryId);
        if (category == null || category.getPositions() == null || category.getPositions().isEmpty()) {
            return 0.0;
        }
        
        LocalDate now = LocalDate.now();
        YearMonth currentMonth = YearMonth.of(now.getYear(), now.getMonth());
        LocalDate firstDayOfMonth = currentMonth.atDay(1);
        LocalDate lastDayOfMonth = currentMonth.atEndOfMonth();
        
        return category.getPositions().stream()
            .filter(position -> {
                LocalDate positionDate = position.getReceipt().getDate();
                return !positionDate.isBefore(firstDayOfMonth) && !positionDate.isAfter(lastDayOfMonth);
            })
            .mapToDouble(position -> position.getPrice().doubleValue())
            .sum();
    }
    
    public Double getSpendingPercentage(int categoryId) {
        Category category = getById(categoryId);
        if (category == null || category.getSpendingLimit() == null || category.getSpendingLimit() <= 0) {
            return null;
        }
        
        Double currentSpending = getCurrentMonthSpending(categoryId);
        return (currentSpending / category.getSpendingLimit()) * 100;
    }
    
    @Transactional
    public void checkCategoryLimits(int userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return;
        }
        
        List<Category> categories = findAllByUser(userId);
        for (Category category : categories) {
            if (category.getSpendingLimit() != null && category.getSpendingLimit() > 0) {
                Double percentage = getSpendingPercentage(category.categoryId());
                if (percentage != null && percentage >= category.getNotificationThreshold()) {
                    // Check if we already sent a notification for this threshold
                    boolean notificationExists = notificationRepository.findByUserOrderByCreatedAtDesc(user).stream()
                        .anyMatch(notification -> 
                            notification.getCategory().categoryId() == category.categoryId() && 
                            notification.getMessage().contains(String.format("%.0f%%", percentage)));
                    
                    if (!notificationExists) {
                        String message = String.format("You've reached %.0f%% of your spending limit for %s", 
                            percentage, category.categoryName());
                        CategoryLimitNotification notification = new CategoryLimitNotification(category, user, message);
                        notificationRepository.save(notification);
                    }
                }
            }
        }
    }
    
    public List<CategoryLimitNotification> getUserNotifications(int userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return new ArrayList<>();
        }
        return notificationRepository.findByUserOrderByCreatedAtDesc(user);
    }
    
    public List<CategoryLimitNotification> getUnreadUserNotifications(int userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return new ArrayList<>();
        }
        return notificationRepository.findByUserAndIsReadOrderByCreatedAtDesc(user, false);
    }
    
    @Transactional
    public void markNotificationAsRead(int notificationId) {
        Optional<CategoryLimitNotification> notification = notificationRepository.findById(notificationId);
        notification.ifPresent(n -> {
            n.setIsRead(true);
            notificationRepository.save(n);
        });
    }
    
    @Transactional
    public void markAllNotificationsAsRead(int userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return;
        }
        
        List<CategoryLimitNotification> unreadNotifications = 
            notificationRepository.findByUserAndIsReadOrderByCreatedAtDesc(user, false);
        
        for (CategoryLimitNotification notification : unreadNotifications) {
            notification.setIsRead(true);
            notificationRepository.save(notification);
        }
    }
}
