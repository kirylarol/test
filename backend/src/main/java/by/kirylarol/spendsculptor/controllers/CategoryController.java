package by.kirylarol.spendsculptor.controllers;

import by.kirylarol.spendsculptor.dto.CategoryWithPositionsDTO;
import by.kirylarol.spendsculptor.entities.Category;
import by.kirylarol.spendsculptor.entities.CategoryLimitNotification;
import by.kirylarol.spendsculptor.entities.User;
import by.kirylarol.spendsculptor.service.AccountUserService;
import by.kirylarol.spendsculptor.service.CategoryService;
import by.kirylarol.spendsculptor.utils.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class CategoryController extends BaseController {

    private final CategoryService categoryService;
    private final AccountUserService accountUserService;

    @Autowired
    public CategoryController(Util util, CategoryService categoryService, AccountUserService accountUserService) {
        this.util = util;
        this.categoryService = categoryService;
        this.accountUserService = accountUserService;
    }

    @GetMapping("categories/all")
    public List<Category> getAll() throws Exception {
        User user = getAuthenticatedUser();
        List<Category> categoryList = categoryService.findAllByUser(user.getId());
        if (categoryList.isEmpty()){
            categoryList.add(new Category("Без категории"));
        }
        return categoryList;
    }

    @GetMapping("account/{accountid}/receipts/categories")
    public List<CategoryWithPositionsDTO> getSpendsByCategories(@PathVariable int accountid) throws Exception {
        User user = getAuthenticatedUser();

        if (accountUserService.getByUserAndAccount(accountid, user.getId()) == null){
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
                category -> categoryWithPositionsDTOList.add(new CategoryWithPositionsDTO(category))
        );
        
        return categoryWithPositionsDTOList;
    }
    
    // New endpoints for spending limits feature
    
    @PostMapping("categories/{categoryId}/limit")
    public ResponseEntity<?> setSpendingLimit(
            @PathVariable int categoryId,
            @RequestParam Double limit,
            @RequestParam(required = false) Integer threshold) {
        
        return executeAuthenticatedOperation(() -> {
            Category category = categoryService.getById(categoryId);
            if (category == null) {
                return errorResponse(HttpStatus.NOT_FOUND, "Category not found");
            }
            
            Category updatedCategory = categoryService.setSpendingLimit(categoryId, limit, threshold);
            return updatedCategory;
        });
    }
    
    @GetMapping("categories/{categoryId}/spending")
    public ResponseEntity<?> getCategorySpending(@PathVariable int categoryId) {
        return executeAuthenticatedOperation(() -> {
            Category category = categoryService.getById(categoryId);
            if (category == null) {
                return errorResponse(HttpStatus.NOT_FOUND, "Category not found");
            }
            
            Double currentSpending = categoryService.getCurrentMonthSpending(categoryId);
            Double percentage = categoryService.getSpendingPercentage(categoryId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("category", category);
            response.put("currentMonthSpending", currentSpending);
            response.put("limit", category.getSpendingLimit());
            response.put("percentage", percentage);
            
            return response;
        });
    }
    
    @GetMapping("categories/check-limits")
    public ResponseEntity<?> checkCategoryLimits() {
        return executeAuthenticatedOperation(() -> {
            User user = getAuthenticatedUser();
            categoryService.checkCategoryLimits(user.getId());
            return "Limits checked successfully";
        });
    }
    
    @GetMapping("notifications")
    public ResponseEntity<?> getUserNotifications() {
        return executeAuthenticatedOperation(() -> {
            User user = getAuthenticatedUser();
            return categoryService.getUserNotifications(user.getId());
        });
    }
    
    @GetMapping("notifications/unread")
    public ResponseEntity<?> getUnreadUserNotifications() {
        return executeAuthenticatedOperation(() -> {
            User user = getAuthenticatedUser();
            return categoryService.getUnreadUserNotifications(user.getId());
        });
    }
    
    @PostMapping("notifications/{notificationId}/read")
    public ResponseEntity<?> markNotificationAsRead(@PathVariable int notificationId) {
        return executeAuthenticatedOperation(() -> {
            categoryService.markNotificationAsRead(notificationId);
            return "Notification marked as read";
        });
    }
    
    @PostMapping("notifications/read-all")
    public ResponseEntity<?> markAllNotificationsAsRead() {
        return executeAuthenticatedOperation(() -> {
            User user = getAuthenticatedUser();
            categoryService.markAllNotificationsAsRead(user.getId());
            return "All notifications marked as read";
        });
    }
}
