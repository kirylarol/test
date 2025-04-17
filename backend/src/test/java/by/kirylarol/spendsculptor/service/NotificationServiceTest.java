package by.kirylarol.spendsculptor.service;

import by.kirylarol.spendsculptor.entities.PaymentNotification;
import by.kirylarol.spendsculptor.entities.PaymentReminder;
import by.kirylarol.spendsculptor.entities.UserProfile;
import by.kirylarol.spendsculptor.repos.PaymentNotificationRepository;
import by.kirylarol.spendsculptor.repos.PaymentReminderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class NotificationServiceTest {

    @Mock
    private PaymentNotificationRepository notificationRepository;

    @Mock
    private PaymentReminderRepository reminderRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private NotificationService notificationService;

    private UserProfile testUser;
    private PaymentReminder testPaymentReminder;
    private PaymentNotification testNotification;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        // Create test user
        testUser = new UserProfile();
        testUser.setId(1);
        testUser.setUsername("testuser");

        // Create test payment reminder
        testPaymentReminder = new PaymentReminder();
        testPaymentReminder.setId(1);
        testPaymentReminder.setUser(testUser);
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

        // Mock userService to return test user
        when(userService.getCurrentUser()).thenReturn(testUser);
    }

    @Test
    public void testGetAllNotifications() {
        // Arrange
        List<PaymentNotification> expectedNotifications = Arrays.asList(testNotification);
        when(notificationRepository.findByUser(testUser)).thenReturn(expectedNotifications);

        // Act
        List<PaymentNotification> actualNotifications = notificationService.getAllNotifications();

        // Assert
        assertEquals(expectedNotifications, actualNotifications);
        verify(notificationRepository).findByUser(testUser);
    }

    @Test
    public void testGetUnreadNotifications() {
        // Arrange
        List<PaymentNotification> expectedNotifications = Arrays.asList(testNotification);
        when(notificationRepository.findUnreadNotifications(testUser)).thenReturn(expectedNotifications);

        // Act
        List<PaymentNotification> actualNotifications = notificationService.getUnreadNotifications();

        // Assert
        assertEquals(expectedNotifications, actualNotifications);
        verify(notificationRepository).findUnreadNotifications(testUser);
    }

    @Test
    public void testGetNotificationById_WhenExists() {
        // Arrange
        when(notificationRepository.findById(1)).thenReturn(Optional.of(testNotification));

        // Act
        Optional<PaymentNotification> result = notificationService.getNotificationById(1);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testNotification, result.get());
    }

    @Test
    public void testGetNotificationById_WhenNotExists() {
        // Arrange
        when(notificationRepository.findById(999)).thenReturn(Optional.empty());

        // Act
        Optional<PaymentNotification> result = notificationService.getNotificationById(999);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    public void testGetNotificationById_WhenBelongsToAnotherUser() {
        // Arrange
        UserProfile anotherUser = new UserProfile();
        anotherUser.setId(2);
        
        PaymentReminder anotherUserReminder = new PaymentReminder();
        anotherUserReminder.setId(2);
        anotherUserReminder.setUser(anotherUser);
        
        PaymentNotification anotherUserNotification = new PaymentNotification();
        anotherUserNotification.setId(2);
        anotherUserNotification.setPaymentReminder(anotherUserReminder);
        
        when(notificationRepository.findById(2)).thenReturn(Optional.of(anotherUserNotification));

        // Act
        Optional<PaymentNotification> result = notificationService.getNotificationById(2);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    public void testMarkNotificationAsRead() {
        // Arrange
        when(notificationRepository.findById(1)).thenReturn(Optional.of(testNotification));
        when(notificationRepository.save(any(PaymentNotification.class))).thenReturn(testNotification);

        // Act
        Optional<PaymentNotification> result = notificationService.markNotificationAsRead(1);

        // Assert
        assertTrue(result.isPresent());
        assertTrue(result.get().getIsRead());
        verify(notificationRepository).save(testNotification);
    }

    @Test
    public void testMarkAllNotificationsAsRead() {
        // Arrange
        List<PaymentNotification> unreadNotifications = Arrays.asList(testNotification);
        when(notificationRepository.findUnreadNotifications(testUser)).thenReturn(unreadNotifications);

        // Act
        notificationService.markAllNotificationsAsRead();

        // Assert
        assertTrue(testNotification.getIsRead());
        verify(notificationRepository).save(testNotification);
    }

    @Test
    public void testCreateNotification() {
        // Arrange
        when(notificationRepository.save(any(PaymentNotification.class))).thenAnswer(invocation -> {
            PaymentNotification savedNotification = invocation.getArgument(0);
            savedNotification.setId(2);
            return savedNotification;
        });

        // Act
        PaymentNotification result = notificationService.createNotification(
                testPaymentReminder, 
                PaymentNotification.NotificationType.DUE_DATE
        );

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getId());
        assertEquals(testPaymentReminder, result.getPaymentReminder());
        assertEquals(PaymentNotification.NotificationType.DUE_DATE, result.getNotificationType());
        assertFalse(result.getIsRead());
        verify(notificationRepository).save(any(PaymentNotification.class));
    }

    @Test
    public void testGetUnreadNotificationCount() {
        // Arrange
        when(notificationRepository.countUnreadNotifications(testUser)).thenReturn(5L);

        // Act
        Long count = notificationService.getUnreadNotificationCount();

        // Assert
        assertEquals(5L, count);
        verify(notificationRepository).countUnreadNotifications(testUser);
    }

    @Test
    public void testGenerateDueNotifications() {
        // Arrange
        List<UserProfile> users = Arrays.asList(testUser);
        when(userService.getAllUsers()).thenReturn(users);
        
        // Create a payment due today
        PaymentReminder dueTodayPayment = new PaymentReminder();
        dueTodayPayment.setId(2);
        dueTodayPayment.setUser(testUser);
        dueTodayPayment.setTitle("Due Today");
        dueTodayPayment.setDueDate(LocalDate.now());
        dueTodayPayment.setStatus(PaymentReminder.PaymentStatus.PENDING);
        dueTodayPayment.setNotificationDays(0);
        
        // Create a payment that should trigger a notification (due in X days where X = notificationDays)
        PaymentReminder notificationDuePayment = new PaymentReminder();
        notificationDuePayment.setId(3);
        notificationDuePayment.setUser(testUser);
        notificationDuePayment.setTitle("Notification Due");
        notificationDuePayment.setDueDate(LocalDate.now().plusDays(3));
        notificationDuePayment.setStatus(PaymentReminder.PaymentStatus.PENDING);
        notificationDuePayment.setNotificationDays(3);
        
        List<PaymentReminder> pendingPayments = Arrays.asList(dueTodayPayment, notificationDuePayment);
        when(reminderRepository.findByUserAndStatus(testUser, PaymentReminder.PaymentStatus.PENDING))
                .thenReturn(pendingPayments);
        
        // Mock save for notifications
        when(notificationRepository.save(any(PaymentNotification.class))).thenAnswer(invocation -> {
            PaymentNotification savedNotification = invocation.getArgument(0);
            savedNotification.setId(savedNotification.getPaymentReminder().getId());
            return savedNotification;
        });

        // Act
        notificationService.generateDueNotifications();

        // Assert
        // Verify notifications were created for the due today payment
        verify(notificationRepository).save(argThat(notification -> 
            notification.getPaymentReminder().getId() == 2 && 
            notification.getNotificationType() == PaymentNotification.NotificationType.DUE_DATE
        ));
        
        // Verify notifications were created for the notification due payment
        verify(notificationRepository).save(argThat(notification -> 
            notification.getPaymentReminder().getId() == 3 && 
            notification.getNotificationType() == PaymentNotification.NotificationType.DUE_DATE
        ));
    }
}
