package by.kirylarol.spendsculptor.service;

import by.kirylarol.spendsculptor.entities.PaymentReminder;
import by.kirylarol.spendsculptor.entities.UserProfile;
import by.kirylarol.spendsculptor.repos.PaymentReminderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class PaymentReminderServiceTest {

    @Mock
    private PaymentReminderRepository paymentReminderRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private PaymentReminderService paymentReminderService;

    private UserProfile testUser;
    private PaymentReminder testPaymentReminder;

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
        testPaymentReminder.setAmount(new BigDecimal("100.00"));
        testPaymentReminder.setDueDate(LocalDate.now().plusDays(7));
        testPaymentReminder.setStatus(PaymentReminder.PaymentStatus.PENDING);
        testPaymentReminder.setIsRecurring(false);
        testPaymentReminder.setNotificationDays(3);

        // Mock userService to return test user
        when(userService.getCurrentUser()).thenReturn(testUser);
    }

    @Test
    public void testGetAllPaymentReminders() {
        // Arrange
        List<PaymentReminder> expectedReminders = Arrays.asList(testPaymentReminder);
        when(paymentReminderRepository.findByUser(testUser)).thenReturn(expectedReminders);

        // Act
        List<PaymentReminder> actualReminders = paymentReminderService.getAllPaymentReminders();

        // Assert
        assertEquals(expectedReminders, actualReminders);
        verify(paymentReminderRepository).findByUser(testUser);
    }

    @Test
    public void testGetPaymentRemindersByStatus() {
        // Arrange
        List<PaymentReminder> expectedReminders = Arrays.asList(testPaymentReminder);
        when(paymentReminderRepository.findByUserAndStatus(testUser, PaymentReminder.PaymentStatus.PENDING))
                .thenReturn(expectedReminders);

        // Act
        List<PaymentReminder> actualReminders = paymentReminderService.getPaymentRemindersByStatus(PaymentReminder.PaymentStatus.PENDING);

        // Assert
        assertEquals(expectedReminders, actualReminders);
        verify(paymentReminderRepository).findByUserAndStatus(testUser, PaymentReminder.PaymentStatus.PENDING);
    }

    @Test
    public void testGetPaymentReminderById_WhenExists() {
        // Arrange
        when(paymentReminderRepository.findById(1)).thenReturn(Optional.of(testPaymentReminder));

        // Act
        Optional<PaymentReminder> result = paymentReminderService.getPaymentReminderById(1);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testPaymentReminder, result.get());
    }

    @Test
    public void testGetPaymentReminderById_WhenNotExists() {
        // Arrange
        when(paymentReminderRepository.findById(999)).thenReturn(Optional.empty());

        // Act
        Optional<PaymentReminder> result = paymentReminderService.getPaymentReminderById(999);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    public void testGetPaymentReminderById_WhenBelongsToAnotherUser() {
        // Arrange
        UserProfile anotherUser = new UserProfile();
        anotherUser.setId(2);
        
        PaymentReminder anotherUserReminder = new PaymentReminder();
        anotherUserReminder.setId(2);
        anotherUserReminder.setUser(anotherUser);
        
        when(paymentReminderRepository.findById(2)).thenReturn(Optional.of(anotherUserReminder));

        // Act
        Optional<PaymentReminder> result = paymentReminderService.getPaymentReminderById(2);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    public void testCreatePaymentReminder() {
        // Arrange
        PaymentReminder newReminder = new PaymentReminder();
        newReminder.setTitle("New Payment");
        newReminder.setAmount(new BigDecimal("200.00"));
        newReminder.setDueDate(LocalDate.now().plusDays(14));
        
        when(paymentReminderRepository.save(any(PaymentReminder.class))).thenAnswer(invocation -> {
            PaymentReminder savedReminder = invocation.getArgument(0);
            savedReminder.setId(2);
            return savedReminder;
        });

        // Act
        PaymentReminder result = paymentReminderService.createPaymentReminder(newReminder);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getId());
        assertEquals(testUser, result.getUser());
        assertEquals("New Payment", result.getTitle());
        assertEquals(PaymentReminder.PaymentStatus.PENDING, result.getStatus());
        verify(paymentReminderRepository).save(newReminder);
    }

    @Test
    public void testUpdatePaymentStatus() {
        // Arrange
        when(paymentReminderRepository.findById(1)).thenReturn(Optional.of(testPaymentReminder));
        when(paymentReminderRepository.save(any(PaymentReminder.class))).thenReturn(testPaymentReminder);

        // Act
        Optional<PaymentReminder> result = paymentReminderService.updatePaymentStatus(1, PaymentReminder.PaymentStatus.PAID);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(PaymentReminder.PaymentStatus.PAID, result.get().getStatus());
        verify(paymentReminderRepository).save(testPaymentReminder);
    }

    @Test
    public void testDeletePaymentReminder_WhenExists() {
        // Arrange
        when(paymentReminderRepository.findById(1)).thenReturn(Optional.of(testPaymentReminder));

        // Act
        boolean result = paymentReminderService.deletePaymentReminder(1);

        // Assert
        assertTrue(result);
        verify(paymentReminderRepository).deleteById(1);
    }

    @Test
    public void testDeletePaymentReminder_WhenNotExists() {
        // Arrange
        when(paymentReminderRepository.findById(999)).thenReturn(Optional.empty());

        // Act
        boolean result = paymentReminderService.deletePaymentReminder(999);

        // Assert
        assertFalse(result);
        verify(paymentReminderRepository, never()).deleteById(anyInt());
    }

    @Test
    public void testGetPaymentSummary() {
        // Arrange
        when(paymentReminderRepository.countByUserAndStatus(eq(testUser), eq(PaymentReminder.PaymentStatus.PENDING)))
                .thenReturn(5L);
        when(paymentReminderRepository.countByUserAndStatus(eq(testUser), eq(PaymentReminder.PaymentStatus.PAID)))
                .thenReturn(10L);
        when(paymentReminderRepository.countByUserAndStatus(eq(testUser), eq(PaymentReminder.PaymentStatus.OVERDUE)))
                .thenReturn(2L);

        // Act
        Map<String, Long> summary = paymentReminderService.getPaymentSummary();

        // Assert
        assertEquals(3, summary.size());
        assertEquals(5L, summary.get("pending"));
        assertEquals(10L, summary.get("paid"));
        assertEquals(2L, summary.get("overdue"));
    }
}
