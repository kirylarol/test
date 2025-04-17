package by.kirylarol.spendsculptor.controllers;

import by.kirylarol.spendsculptor.dto.CategoryWithPositionsDTO;
import by.kirylarol.spendsculptor.entities.Category;
import by.kirylarol.spendsculptor.entities.CategoryLimitNotification;
import by.kirylarol.spendsculptor.entities.User;
import by.kirylarol.spendsculptor.service.AccountUserService;
import by.kirylarol.spendsculptor.service.CategoryService;
import by.kirylarol.spendsculptor.utils.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class CategoryController {

    Util util;
    CategoryService categoryService;
    AccountUserService accountUserService;

    @Autowired
    public CategoryController(Util util, CategoryService categoryService, AccountUserService accountUserService) {
        this.util = util;
        this.categoryService = categoryService;
        this.accountUserService = accountUserService;
    }

    @GetMapping("categories/all")
    List<Category> getAll() throws Exception {
        User user = util.getUser();
        List<Category> categoryList = categoryService.findAllByUser(user.getId());
        if (categoryList.isEmpty()){
            categoryList.add(new Category("Без категории"));
        }
        return categoryList;
    }

    @GetMapping("account/{accountid}/receipts/categories")
    List<CategoryWithPositionsDTO> getSpendsByCategories(@PathVariable int accountid) throws Exception{
        User user = util.getUser();

        if (user == null || accountUserService.getByUserAndAccount(accountid, user.getId()) == null){
            throw new Exception("Нет доступа к этому аккаунту");
        }
        int accountuserid = accountUserService.getByUserAndAccount(accountid, user.getId()).getId();
        List<Category> categoryList = categoryService.findAllByAccountUser(accountuserid);
        categoryList.forEach(
                category ->
                        category.setPositions(category.getPositions().stream().filter(
                                position -> position.getReceipt().getAccount().getId() == accountuserid
                        ).toList())
        );
        List<CategoryWithPositionsDTO> categoryWithPositionsDTOList = new ArrayList<>();
        categoryList.forEach(
                category -> {
                    categoryWithPositionsDTOList.add(new CategoryWithPositionsDTO(category));
                }
        );
        return categoryWithPositionsDTOList;
    }
    
    // New endpoints for spending limits feature
    
    @PostMapping("categories/{categoryId}/limit")
    public ResponseEntity<?> setSpendingLimit(
            @PathVariable int categoryId,
            @RequestParam Double limit,
            @RequestParam(required = false) Integer threshold) throws Exception {
        
        User user = util.getUser();
        if (user == null) {
            return ResponseEntity.status(401).body("User not authenticated");
        }
        
        Category category = categoryService.getById(categoryId);
        if (category == null) {
            return ResponseEntity.status(404).body("Category not found");
        }
        
        Category updatedCategory = categoryService.setSpendingLimit(categoryId, limit, threshold);
        return ResponseEntity.ok(updatedCategory);
    }
    
    @GetMapping("categories/{categoryId}/spending")
    public ResponseEntity<?> getCategorySpending(@PathVariable int categoryId) throws Exception {
        User user = util.getUser();
        if (user == null) {
            return ResponseEntity.status(401).body("User not authenticated");
        }
        
        Category category = categoryService.getById(categoryId);
        if (category == null) {
            return ResponseEntity.status(404).body("Category not found");
        }
        
        Double currentSpending = categoryService.getCurrentMonthSpending(categoryId);
        Double percentage = categoryService.getSpendingPercentage(categoryId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("category", category);
        response.put("currentMonthSpending", currentSpending);
        response.put("limit", category.getSpendingLimit());
        response.put("percentage", percentage);
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("categories/check-limits")
    public ResponseEntity<?> checkCategoryLimits() throws Exception {
        User user = util.getUser();
        if (user == null) {
            return ResponseEntity.status(401).body("User not authenticated");
        }
        
        categoryService.checkCategoryLimits(user.getId());
        return ResponseEntity.ok("Limits checked successfully");
    }
    
    @GetMapping("notifications")
    public ResponseEntity<?> getUserNotifications() throws Exception {
        User user = util.getUser();
        if (user == null) {
            return ResponseEntity.status(401).body("User not authenticated");
        }
        
        List<CategoryLimitNotification> notifications = categoryService.getUserNotifications(user.getId());
        return ResponseEntity.ok(notifications);
    }
    
    @GetMapping("notifications/unread")
    public ResponseEntity<?> getUnreadUserNotifications() throws Exception {
        User user = util.getUser();
        if (user == null) {
            return ResponseEntity.status(401).body("User not authenticated");
        }
        
        List<CategoryLimitNotification> notifications = categoryService.getUnreadUserNotifications(user.getId());
        return ResponseEntity.ok(notifications);
    }
    
    @PostMapping("notifications/{notificationId}/read")
    public ResponseEntity<?> markNotificationAsRead(@PathVariable int notificationId) throws Exception {
        User user = util.getUser();
        if (user == null) {
            return ResponseEntity.status(401).body("User not authenticated");
        }
        
        categoryService.markNotificationAsRead(notificationId);
        return ResponseEntity.ok("Notification marked as read");
    }
    
    @PostMapping("notifications/read-all")
    public ResponseEntity<?> markAllNotificationsAsRead() throws Exception {
        User user = util.getUser();
        if (user == null) {
            return ResponseEntity.status(401).body("User not authenticated");
        }
        
        categoryService.markAllNotificationsAsRead(user.getId());
        return ResponseEntity.ok("All notifications marked as read");
    }
}
