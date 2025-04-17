package by.kirylarol.spendsculptor.controllers;

import by.kirylarol.spendsculptor.entities.PaymentNotification;
import by.kirylarol.spendsculptor.entities.PaymentReminder;
import by.kirylarol.spendsculptor.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class NotificationControllerTest {

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private NotificationController notificationController;

    private PaymentReminder testPaymentReminder;
    private PaymentNotification testNotification;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        // Create test payment reminder
        testPaymentReminder = new PaymentReminder();
        testPaymentReminder.setId(1);
        testPaymentReminder.setTitle("Test Payment");
        testPaymentReminder.setDueDate(LocalDate.now().plusDays(7));
        testPaymentReminder.setStatus(PaymentReminder.PaymentStatus.PENDING);

        // Create test notification
        testNotification = new PaymentNotification();
        testNotification.setId(1);
        testNotification.setPaymentReminder(testPaymentReminder);
        testNotification.setNotificationDate(LocalDateTime.now());
        testNotification.setNotificationType(PaymentNotification.NotificationType.REMINDER);
        testNotification.setIsRead(false);
    }

    @Test
    public void testGetAllNotifications() {
        // Arrange
        List<PaymentNotification> expectedNotifications = Arrays.asList(testNotification);
        when(notificationService.getAllNotifications()).thenReturn(expectedNotifications);

        // Act
        ResponseEntity<List<PaymentNotification>> response = notificationController.getAllNotifications(null, null, null);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedNotifications, response.getBody());
        verify(notificationService).getAllNotifications();
    }

    @Test
    public void testGetUnreadNotifications() {
        // Arrange
        List<PaymentNotification> expectedNotifications = Arrays.asList(testNotification);
        when(notificationService.getUnreadNotifications()).thenReturn(expectedNotifications);

        // Act
        ResponseEntity<List<PaymentNotification>> response = notificationController.getAllNotifications(false, null, null);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedNotifications, response.getBody());
        verify(notificationService).getUnreadNotifications();
    }

    @Test
    public void testGetNotificationsByDateRange() {
        // Arrange
        List<PaymentNotification> expectedNotifications = Arrays.asList(testNotification);
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();
        
        when(notificationService.getNotificationsByDateRange(startDate, endDate))
                .thenReturn(expectedNotifications);

        // Act
        ResponseEntity<List<PaymentNotification>> response = notificationController.getAllNotifications(
                null, startDate, endDate);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedNotifications, response.getBody());
        verify(notificationService).getNotificationsByDateRange(startDate, endDate);
    }

    @Test
    public void testGetNotificationById_WhenExists() {
        // Arrange
        when(notificationService.getNotificationById(1)).thenReturn(Optional.of(testNotification));

        // Act
        ResponseEntity<PaymentNotification> response = notificationController.getNotificationById(1);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testNotification, response.getBody());
    }

    @Test
    public void testGetNotificationById_WhenNotExists() {
        // Arrange
        when(notificationService.getNotificationById(999)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<PaymentNotification> response = notificationController.getNotificationById(999);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    public void testMarkNotificationAsRead_WhenExists() {
        // Arrange
        testNotification.setIsRead(true);
        when(notificationService.markNotificationAsRead(1)).thenReturn(Optional.of(testNotification));

        // Act
        ResponseEntity<PaymentNotification> response = notificationController.markNotificationAsRead(1);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testNotification, response.getBody());
        assertTrue(response.getBody().getIsRead());
    }

    @Test
    public void testMarkNotificationAsRead_WhenNotExists() {
        // Arrange
        when(notificationService.markNotificationAsRead(999)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<PaymentNotification> response = notificationController.markNotificationAsRead(999);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    public void testMarkAllNotificationsAsRead() {
        // Act
        ResponseEntity<Void> response = notificationController.markAllNotificationsAsRead();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(notificationService).markAllNotificationsAsRead();
    }

    @Test
    public void testGetUnreadNotificationCount() {
        // Arrange
        when(notificationService.getUnreadNotificationCount()).thenReturn(5L);

        // Act
        ResponseEntity<Map<String, Long>> response = notificationController.getUnreadNotificationCount();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(5L, response.getBody().get("count"));
    }

    @Test
    public void testCreateTestNotification() {
        // Arrange
        when(notificationService.createTestNotification(any())).thenReturn(testNotification);

        // Act
        ResponseEntity<PaymentNotification> response = notificationController.createTestNotification(null);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(testNotification, response.getBody());
    }

    @Test
    public void testCreateTestNotificationWithPaymentReminderId() {
        // Arrange
        Map<String, Integer> requestBody = new HashMap<>();
        requestBody.put("paymentReminderId", 1);
        
        when(notificationService.createTestNotification(1)).thenReturn(testNotification);

        // Act
        ResponseEntity<PaymentNotification> response = notificationController.createTestNotification(requestBody);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(testNotification, response.getBody());
    }
}
