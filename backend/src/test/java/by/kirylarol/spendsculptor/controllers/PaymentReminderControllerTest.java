package by.kirylarol.spendsculptor.controllers;

import by.kirylarol.spendsculptor.entities.PaymentReminder;
import by.kirylarol.spendsculptor.service.PaymentReminderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class PaymentReminderControllerTest {

    @Mock
    private PaymentReminderService paymentReminderService;

    @InjectMocks
    private PaymentReminderController paymentReminderController;

    private PaymentReminder testPaymentReminder;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        // Create test payment reminder
        testPaymentReminder = new PaymentReminder();
        testPaymentReminder.setId(1);
        testPaymentReminder.setTitle("Test Payment");
        testPaymentReminder.setAmount(new BigDecimal("100.00"));
        testPaymentReminder.setDueDate(LocalDate.now().plusDays(7));
        testPaymentReminder.setStatus(PaymentReminder.PaymentStatus.PENDING);
    }

    @Test
    public void testGetAllPaymentReminders() {
        // Arrange
        List<PaymentReminder> expectedReminders = Arrays.asList(testPaymentReminder);
        when(paymentReminderService.getAllPaymentReminders()).thenReturn(expectedReminders);

        // Act
        ResponseEntity<List<PaymentReminder>> response = paymentReminderController.getAllPaymentReminders(null, null, null);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedReminders, response.getBody());
        verify(paymentReminderService).getAllPaymentReminders();
    }

    @Test
    public void testGetPaymentRemindersByStatus() {
        // Arrange
        List<PaymentReminder> expectedReminders = Arrays.asList(testPaymentReminder);
        when(paymentReminderService.getPaymentRemindersByStatus(PaymentReminder.PaymentStatus.PENDING))
                .thenReturn(expectedReminders);

        // Act
        ResponseEntity<List<PaymentReminder>> response = paymentReminderController.getAllPaymentReminders(
                PaymentReminder.PaymentStatus.PENDING, null, null);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedReminders, response.getBody());
        verify(paymentReminderService).getPaymentRemindersByStatus(PaymentReminder.PaymentStatus.PENDING);
    }

    @Test
    public void testGetPaymentRemindersByDateRange() {
        // Arrange
        List<PaymentReminder> expectedReminders = Arrays.asList(testPaymentReminder);
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(30);
        
        when(paymentReminderService.getPaymentRemindersByDateRange(startDate, endDate))
                .thenReturn(expectedReminders);

        // Act
        ResponseEntity<List<PaymentReminder>> response = paymentReminderController.getAllPaymentReminders(
                null, startDate, endDate);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedReminders, response.getBody());
        verify(paymentReminderService).getPaymentRemindersByDateRange(startDate, endDate);
    }

    @Test
    public void testGetPaymentReminderById_WhenExists() {
        // Arrange
        when(paymentReminderService.getPaymentReminderById(1)).thenReturn(Optional.of(testPaymentReminder));

        // Act
        ResponseEntity<PaymentReminder> response = paymentReminderController.getPaymentReminderById(1);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testPaymentReminder, response.getBody());
    }

    @Test
    public void testGetPaymentReminderById_WhenNotExists() {
        // Arrange
        when(paymentReminderService.getPaymentReminderById(999)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<PaymentReminder> response = paymentReminderController.getPaymentReminderById(999);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    public void testCreatePaymentReminder() {
        // Arrange
        when(paymentReminderService.createPaymentReminder(any(PaymentReminder.class))).thenReturn(testPaymentReminder);

        // Act
        ResponseEntity<PaymentReminder> response = paymentReminderController.createPaymentReminder(new PaymentReminder());

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(testPaymentReminder, response.getBody());
    }

    @Test
    public void testUpdatePaymentReminder_WhenExists() {
        // Arrange
        when(paymentReminderService.updatePaymentReminder(eq(1), any(PaymentReminder.class)))
                .thenReturn(Optional.of(testPaymentReminder));

        // Act
        ResponseEntity<PaymentReminder> response = paymentReminderController.updatePaymentReminder(1, new PaymentReminder());

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testPaymentReminder, response.getBody());
    }

    @Test
    public void testUpdatePaymentReminder_WhenNotExists() {
        // Arrange
        when(paymentReminderService.updatePaymentReminder(eq(999), any(PaymentReminder.class)))
                .thenReturn(Optional.empty());

        // Act
        ResponseEntity<PaymentReminder> response = paymentReminderController.updatePaymentReminder(999, new PaymentReminder());

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    public void testUpdatePaymentStatus_WhenExists() {
        // Arrange
        Map<String, String> statusUpdate = new HashMap<>();
        statusUpdate.put("status", "PAID");
        
        when(paymentReminderService.updatePaymentStatus(eq(1), eq(PaymentReminder.PaymentStatus.PAID)))
                .thenReturn(Optional.of(testPaymentReminder));

        // Act
        ResponseEntity<PaymentReminder> response = paymentReminderController.updatePaymentStatus(1, statusUpdate);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testPaymentReminder, response.getBody());
    }

    @Test
    public void testUpdatePaymentStatus_WithInvalidStatus() {
        // Arrange
        Map<String, String> statusUpdate = new HashMap<>();
        statusUpdate.put("status", "INVALID_STATUS");

        // Act
        ResponseEntity<PaymentReminder> response = paymentReminderController.updatePaymentStatus(1, statusUpdate);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    public void testUpdatePaymentStatus_WithMissingStatus() {
        // Arrange
        Map<String, String> statusUpdate = new HashMap<>();

        // Act
        ResponseEntity<PaymentReminder> response = paymentReminderController.updatePaymentStatus(1, statusUpdate);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    public void testDeletePaymentReminder_WhenExists() {
        // Arrange
        when(paymentReminderService.deletePaymentReminder(1)).thenReturn(true);

        // Act
        ResponseEntity<Void> response = paymentReminderController.deletePaymentReminder(1);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    public void testDeletePaymentReminder_WhenNotExists() {
        // Arrange
        when(paymentReminderService.deletePaymentReminder(999)).thenReturn(false);

        // Act
        ResponseEntity<Void> response = paymentReminderController.deletePaymentReminder(999);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testGetUpcomingPayments() {
        // Arrange
        List<PaymentReminder> expectedReminders = Arrays.asList(testPaymentReminder);
        when(paymentReminderService.getUpcomingPayments(7)).thenReturn(expectedReminders);

        // Act
        ResponseEntity<List<PaymentReminder>> response = paymentReminderController.getUpcomingPayments(7);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedReminders, response.getBody());
        verify(paymentReminderService).getUpcomingPayments(7);
    }

    @Test
    public void testGetOverduePayments() {
        // Arrange
        List<PaymentReminder> expectedReminders = Arrays.asList(testPaymentReminder);
        when(paymentReminderService.getOverduePayments()).thenReturn(expectedReminders);

        // Act
        ResponseEntity<List<PaymentReminder>> response = paymentReminderController.getOverduePayments();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedReminders, response.getBody());
        verify(paymentReminderService).getOverduePayments();
    }
}
