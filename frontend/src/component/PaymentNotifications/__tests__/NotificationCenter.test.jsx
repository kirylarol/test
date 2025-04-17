import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import axios from 'axios';
import MockAdapter from 'axios-mock-adapter';
import NotificationCenter from '../NotificationCenter';

// Mock axios
const mockAxios = new MockAdapter(axios);

// Mock the react-router-dom's useNavigate
jest.mock('react-router-dom', () => ({
  ...jest.requireActual('react-router-dom'),
  useNavigate: () => jest.fn()
}));

describe('NotificationCenter Component', () => {
  beforeEach(() => {
    mockAxios.reset();
    
    // Mock notifications endpoint
    mockAxios.onGet('/notifications').reply(200, [
      {
        id: 1,
        paymentReminder: {
          id: 101,
          title: 'Rent Payment',
          amount: 1000.00,
          dueDate: '2025-05-01',
          status: 'PENDING'
        },
        notificationDate: '2025-04-28T10:00:00',
        notificationType: 'DUE_DATE',
        isRead: false
      },
      {
        id: 2,
        paymentReminder: {
          id: 102,
          title: 'Electricity Bill',
          amount: 150.00,
          dueDate: '2025-04-20',
          status: 'OVERDUE'
        },
        notificationDate: '2025-04-20T10:00:00',
        notificationType: 'OVERDUE',
        isRead: true
      }
    ]);
    
    // Mock unread count endpoint
    mockAxios.onGet('/notifications/count').reply(200, { count: 1 });
    
    // Mock mark as read endpoint
    mockAxios.onPatch('/notifications/1/read').reply(200, {
      id: 1,
      isRead: true
    });
    
    // Mock mark all as read endpoint
    mockAxios.onPatch('/notifications/read-all').reply(200);
    
    // Mock test notification endpoint
    mockAxios.onPost('/notifications/test').reply(201, {
      id: 3,
      paymentReminder: {
        id: 103,
        title: 'Test Payment',
        amount: 50.00,
        dueDate: '2025-05-10',
        status: 'PENDING'
      },
      notificationDate: '2025-04-17T10:00:00',
      notificationType: 'REMINDER',
      isRead: false
    });
  });

  test('renders notification center with notifications', async () => {
    render(
      <BrowserRouter>
        <NotificationCenter />
      </BrowserRouter>
    );
    
    // Wait for notifications to load
    await waitFor(() => {
      expect(screen.getByText('Notification Center')).toBeInTheDocument();
    });
    
    // Check notification count is displayed
    expect(screen.getByText(/You have 1 unread notification/i)).toBeInTheDocument();
    
    // Check notifications are displayed
    expect(screen.getByText(/Payment due soon: Rent Payment/i)).toBeInTheDocument();
    expect(screen.getByText(/Overdue payment: Electricity Bill/i)).toBeInTheDocument();
    
    // Check buttons are displayed
    expect(screen.getByText('Mark All as Read')).toBeInTheDocument();
    expect(screen.getByText('Test Notification')).toBeInTheDocument();
  });

  test('marks notification as read', async () => {
    render(
      <BrowserRouter>
        <NotificationCenter />
      </BrowserRouter>
    );
    
    // Wait for notifications to load
    await waitFor(() => {
      expect(screen.getByText(/Payment due soon: Rent Payment/i)).toBeInTheDocument();
    });
    
    // Find and click the "Mark as Read" button for the first notification
    const markAsReadButton = screen.getAllByText('Mark as Read')[0];
    fireEvent.click(markAsReadButton);
    
    // Check that the API was called
    await waitFor(() => {
      expect(mockAxios.history.patch.length).toBe(1);
      expect(mockAxios.history.patch[0].url).toBe('/notifications/1/read');
    });
  });

  test('marks all notifications as read', async () => {
    render(
      <BrowserRouter>
        <NotificationCenter />
      </BrowserRouter>
    );
    
    // Wait for notifications to load
    await waitFor(() => {
      expect(screen.getByText(/Payment due soon: Rent Payment/i)).toBeInTheDocument();
    });
    
    // Find and click the "Mark All as Read" button
    const markAllAsReadButton = screen.getByText('Mark All as Read');
    fireEvent.click(markAllAsReadButton);
    
    // Check that the API was called
    await waitFor(() => {
      expect(mockAxios.history.patch.length).toBe(1);
      expect(mockAxios.history.patch[0].url).toBe('/notifications/read-all');
    });
  });

  test('creates test notification', async () => {
    render(
      <BrowserRouter>
        <NotificationCenter />
      </BrowserRouter>
    );
    
    // Wait for notifications to load
    await waitFor(() => {
      expect(screen.getByText(/Payment due soon: Rent Payment/i)).toBeInTheDocument();
    });
    
    // Find and click the "Test Notification" button
    const testNotificationButton = screen.getByText('Test Notification');
    fireEvent.click(testNotificationButton);
    
    // Check that the API was called
    await waitFor(() => {
      expect(mockAxios.history.post.length).toBe(1);
      expect(mockAxios.history.post[0].url).toBe('/notifications/test');
    });
  });

  test('displays empty state when no notifications', async () => {
    // Override the notifications endpoint to return empty array
    mockAxios.onGet('/notifications').reply(200, []);
    
    render(
      <BrowserRouter>
        <NotificationCenter />
      </BrowserRouter>
    );
    
    // Wait for notifications to load
    await waitFor(() => {
      expect(screen.getByText('Notification Center')).toBeInTheDocument();
    });
    
    // Check empty state message is displayed
    expect(screen.getByText(/No notifications found/i)).toBeInTheDocument();
  });
});
