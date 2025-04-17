import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { SpendingLimitManager } from '../SpendingLimitManager';
import { SpendingLimitDashboard } from '../SpendingLimitDashboard';
import { NotificationCenter } from '../NotificationCenter';
import { instance } from '../../../../axios/axiosConfig';

// Mock axios instance
jest.mock('../../../../axios/axiosConfig', () => ({
  instance: {
    get: jest.fn(),
    post: jest.fn()
  }
}));

describe('SpendingLimitManager Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    instance.get.mockResolvedValue({
      data: [
        { categoryId: 1, categoryName: 'Food', spendingLimit: 100, notificationThreshold: 80 },
        { categoryId: 2, categoryName: 'Entertainment', spendingLimit: null, notificationThreshold: 80 }
      ]
    });
    instance.post.mockResolvedValue({ data: 'success' });
  });

  test('renders the component with categories', async () => {
    render(<SpendingLimitManager />);
    
    // Wait for categories to load
    await waitFor(() => {
      expect(screen.getByText('Spending Limits Management')).toBeInTheDocument();
      expect(screen.getByText('Choose a category...')).toBeInTheDocument();
    });
  });

  test('allows setting a spending limit', async () => {
    render(<SpendingLimitManager />);
    
    // Wait for categories to load
    await waitFor(() => {
      expect(screen.getByText('Choose a category...')).toBeInTheDocument();
    });
    
    // Select a category
    fireEvent.change(screen.getByRole('combobox'), { target: { value: '2' } });
    
    // Enter spending limit
    fireEvent.change(screen.getByLabelText('Spending Limit'), { target: { value: '200' } });
    
    // Enter notification threshold
    fireEvent.change(screen.getByLabelText('Notification Threshold (%)'), { target: { value: '70' } });
    
    // Submit the form
    fireEvent.click(screen.getByText('Save Limit'));
    
    // Verify API call
    await waitFor(() => {
      expect(instance.post).toHaveBeenCalledWith('/categories/2/limit?limit=200&threshold=70');
    });
  });
});

describe('SpendingLimitDashboard Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    instance.get.mockImplementation((url) => {
      if (url === '/categories/all') {
        return Promise.resolve({
          data: [
            { categoryId: 1, categoryName: 'Food', spendingLimit: 100, notificationThreshold: 80 },
            { categoryId: 2, categoryName: 'Entertainment', spendingLimit: 200, notificationThreshold: 80 }
          ]
        });
      } else if (url.includes('/categories/1/spending')) {
        return Promise.resolve({
          data: { currentMonthSpending: 80, limit: 100, percentage: 80 }
        });
      } else if (url.includes('/categories/2/spending')) {
        return Promise.resolve({
          data: { currentMonthSpending: 180, limit: 200, percentage: 90 }
        });
      }
      return Promise.reject(new Error('Not found'));
    });
  });

  test('renders the dashboard with spending data', async () => {
    render(<SpendingLimitDashboard />);
    
    // Wait for data to load
    await waitFor(() => {
      expect(screen.getByText('Spending Limits Dashboard')).toBeInTheDocument();
      expect(screen.getByText('Food')).toBeInTheDocument();
      expect(screen.getByText('Entertainment')).toBeInTheDocument();
      expect(screen.getByText('80%')).toBeInTheDocument();
      expect(screen.getByText('90%')).toBeInTheDocument();
    });
  });
});

describe('NotificationCenter Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    instance.get.mockImplementation((url) => {
      if (url === '/notifications') {
        return Promise.resolve({
          data: [
            { 
              notificationId: 1, 
              message: "You've reached 80% of your spending limit for Food", 
              isRead: false,
              createdAt: '2025-04-17T12:00:00'
            },
            { 
              notificationId: 2, 
              message: "You've reached 90% of your spending limit for Entertainment", 
              isRead: true,
              createdAt: '2025-04-16T12:00:00'
            }
          ]
        });
      }
      return Promise.resolve({ data: 'success' });
    });
    instance.post.mockResolvedValue({ data: 'success' });
  });

  test('renders notifications', async () => {
    render(<NotificationCenter />);
    
    // Wait for notifications to load
    await waitFor(() => {
      expect(screen.getByText("You've reached 80% of your spending limit for Food")).toBeInTheDocument();
      expect(screen.getByText("You've reached 90% of your spending limit for Entertainment")).toBeInTheDocument();
      expect(screen.getByText('New')).toBeInTheDocument(); // Badge for unread notification
    });
  });

  test('allows marking a notification as read', async () => {
    render(<NotificationCenter />);
    
    // Wait for notifications to load
    await waitFor(() => {
      expect(screen.getByText('Mark as read')).toBeInTheDocument();
    });
    
    // Click mark as read button
    fireEvent.click(screen.getByText('Mark as read'));
    
    // Verify API call
    await waitFor(() => {
      expect(instance.post).toHaveBeenCalledWith('/notifications/1/read');
    });
  });

  test('allows marking all notifications as read', async () => {
    render(<NotificationCenter />);
    
    // Wait for notifications to load
    await waitFor(() => {
      expect(screen.getByText('Mark All as Read')).toBeInTheDocument();
    });
    
    // Click mark all as read button
    fireEvent.click(screen.getByText('Mark All as Read'));
    
    // Verify API call
    await waitFor(() => {
      expect(instance.post).toHaveBeenCalledWith('/notifications/read-all');
    });
  });
});
