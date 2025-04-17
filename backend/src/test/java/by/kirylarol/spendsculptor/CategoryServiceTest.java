package by.kirylarol.spendsculptor;

import by.kirylarol.spendsculptor.entities.Category;
import by.kirylarol.spendsculptor.entities.CategoryLimitNotification;
import by.kirylarol.spendsculptor.entities.User;
import by.kirylarol.spendsculptor.repos.CategoryLimitNotificationRepository;
import by.kirylarol.spendsculptor.repos.CategoryRepository;
import by.kirylarol.spendsculptor.repos.UserRepository;
import by.kirylarol.spendsculptor.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryLimitNotificationRepository notificationRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CategoryService categoryService;

    private Category testCategory;
    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testCategory = new Category("Test Category");
        testCategory.setCategoryId(1);
        testCategory.setSpendingLimit(100.0);
        testCategory.setNotificationThreshold(80);

        testUser = new User();
        testUser.setId(1);
    }

    @Test
    void testSetSpendingLimit() {
        when(categoryRepository.findById(1)).thenReturn(Optional.of(testCategory));
        when(categoryRepository.save(any(Category.class))).thenReturn(testCategory);

        Category result = categoryService.setSpendingLimit(1, 200.0, 70);

        assertNotNull(result);
        assertEquals(200.0, result.getSpendingLimit());
        assertEquals(70, result.getNotificationThreshold());
        verify(categoryRepository).save(testCategory);
    }

    @Test
    void testGetUserNotifications() {
        List<CategoryLimitNotification> notifications = new ArrayList<>();
        notifications.add(new CategoryLimitNotification(testCategory, testUser, "Test notification"));

        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(notificationRepository.findByUserOrderByCreatedAtDesc(testUser)).thenReturn(notifications);

        List<CategoryLimitNotification> result = categoryService.getUserNotifications(1);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test notification", result.get(0).getMessage());
    }

    @Test
    void testMarkNotificationAsRead() {
        CategoryLimitNotification notification = new CategoryLimitNotification(testCategory, testUser, "Test notification");
        notification.setNotificationId(1);
        notification.setIsRead(false);

        when(notificationRepository.findById(1)).thenReturn(Optional.of(notification));

        categoryService.markNotificationAsRead(1);

        assertTrue(notification.getIsRead());
        verify(notificationRepository).save(notification);
    }

    @Test
    void testMarkAllNotificationsAsRead() {
        List<CategoryLimitNotification> unreadNotifications = new ArrayList<>();
        CategoryLimitNotification notification1 = new CategoryLimitNotification(testCategory, testUser, "Test notification 1");
        CategoryLimitNotification notification2 = new CategoryLimitNotification(testCategory, testUser, "Test notification 2");
        unreadNotifications.add(notification1);
        unreadNotifications.add(notification2);

        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(notificationRepository.findByUserAndIsReadOrderByCreatedAtDesc(testUser, false)).thenReturn(unreadNotifications);

        categoryService.markAllNotificationsAsRead(1);

        assertTrue(notification1.getIsRead());
        assertTrue(notification2.getIsRead());
        verify(notificationRepository, times(2)).save(any(CategoryLimitNotification.class));
    }
}
