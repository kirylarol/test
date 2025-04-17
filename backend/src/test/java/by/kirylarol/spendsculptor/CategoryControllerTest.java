package by.kirylarol.spendsculptor;

import by.kirylarol.spendsculptor.controllers.CategoryController;
import by.kirylarol.spendsculptor.entities.Category;
import by.kirylarol.spendsculptor.entities.CategoryLimitNotification;
import by.kirylarol.spendsculptor.entities.User;
import by.kirylarol.spendsculptor.service.AccountUserService;
import by.kirylarol.spendsculptor.service.CategoryService;
import by.kirylarol.spendsculptor.utils.Util;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

public class CategoryControllerTest {

    @Mock
    private CategoryService categoryService;

    @Mock
    private AccountUserService accountUserService;

    @Mock
    private Util util;

    @InjectMocks
    private CategoryController categoryController;

    private User testUser;
    private Category testCategory;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testUser = new User();
        testUser.setId(1);

        testCategory = new Category("Test Category");
        testCategory.setCategoryId(1);
        testCategory.setSpendingLimit(100.0);
        testCategory.setNotificationThreshold(80);
    }

    @Test
    void testSetSpendingLimit() throws Exception {
        when(util.getUser()).thenReturn(testUser);
        when(categoryService.getById(1)).thenReturn(testCategory);
        when(categoryService.setSpendingLimit(1, 200.0, 70)).thenReturn(testCategory);

        ResponseEntity<?> response = categoryController.setSpendingLimit(1, 200.0, 70);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(categoryService).setSpendingLimit(1, 200.0, 70);
    }

    @Test
    void testGetCategorySpending() throws Exception {
        when(util.getUser()).thenReturn(testUser);
        when(categoryService.getById(1)).thenReturn(testCategory);
        when(categoryService.getCurrentMonthSpending(1)).thenReturn(50.0);
        when(categoryService.getSpendingPercentage(1)).thenReturn(50.0);

        ResponseEntity<?> response = categoryController.getCategorySpending(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        
        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals(testCategory, responseBody.get("category"));
        assertEquals(50.0, responseBody.get("currentMonthSpending"));
        assertEquals(100.0, responseBody.get("limit"));
        assertEquals(50.0, responseBody.get("percentage"));
    }

    @Test
    void testGetUserNotifications() throws Exception {
        List<CategoryLimitNotification> notifications = new ArrayList<>();
        notifications.add(new CategoryLimitNotification(testCategory, testUser, "Test notification"));

        when(util.getUser()).thenReturn(testUser);
        when(categoryService.getUserNotifications(testUser.getId())).thenReturn(notifications);

        ResponseEntity<?> response = categoryController.getUserNotifications();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof List);
        
        @SuppressWarnings("unchecked")
        List<CategoryLimitNotification> responseBody = (List<CategoryLimitNotification>) response.getBody();
        assertEquals(1, responseBody.size());
        assertEquals("Test notification", responseBody.get(0).getMessage());
    }

    @Test
    void testMarkNotificationAsRead() throws Exception {
        when(util.getUser()).thenReturn(testUser);

        ResponseEntity<?> response = categoryController.markNotificationAsRead(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(categoryService).markNotificationAsRead(1);
    }

    @Test
    void testMarkAllNotificationsAsRead() throws Exception {
        when(util.getUser()).thenReturn(testUser);

        ResponseEntity<?> response = categoryController.markAllNotificationsAsRead();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(categoryService).markAllNotificationsAsRead(testUser.getId());
    }
}
