import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import axios from 'axios';
import MockAdapter from 'axios-mock-adapter';
import PaymentDashboard from '../PaymentDashboard';

// Mock axios
const mockAxios = new MockAdapter(axios);

// Mock the react-router-dom's useNavigate
jest.mock('react-router-dom', () => ({
  ...jest.requireActual('react-router-dom'),
  useNavigate: () => jest.fn()
}));

describe('PaymentDashboard Component', () => {
  beforeEach(() => {
    mockAxios.reset();
    
    // Mock payment-reminders endpoint
    mockAxios.onGet('/payment-reminders').reply(200, [
      {
        id: 1,
        title: 'Rent Payment',
        amount: 1000.00,
        dueDate: '2025-05-01',
        category: { id: 1, name: 'Housing' },
        status: 'PENDING',
        isRecurring: true,
        recurrencePattern: 'MONTHLY'
      },
      {
        id: 2,
        title: 'Electricity Bill',
        amount: 150.00,
        dueDate: '2025-04-20',
        category: { id: 2, name: 'Utilities' },
        status: 'OVERDUE',
        isRecurring: false
      },
      {
        id: 3,
        title: 'Internet Bill',
        amount: 80.00,
        dueDate: '2025-04-15',
        category: { id: 2, name: 'Utilities' },
        status: 'PAID',
        isRecurring: true,
        recurrencePattern: 'MONTHLY'
      }
    ]);
    
    // Mock dashboard/payment-summary endpoint
    mockAxios.onGet('/dashboard/payment-summary').reply(200, {
      pending: 1,
      paid: 1,
      overdue: 1
    });
    
    // Mock payment status update endpoint
    mockAxios.onPatch('/payment-reminders/2/status').reply(200, {
      id: 2,
      status: 'PAID'
    });
    
    // Mock payment delete endpoint
    mockAxios.onDelete('/payment-reminders/3').reply(204);
  });

  test('renders dashboard with payment reminders', async () => {
    render(
      <BrowserRouter>
        <PaymentDashboard />
      </BrowserRouter>
    );
    
    // Wait for payments to load
    await waitFor(() => {
      expect(screen.getByText('Payment Reminders')).toBeInTheDocument();
    });
    
    // Check summary cards are displayed
    expect(screen.getByText('1')).toBeInTheDocument(); // Pending count
    expect(screen.getByText('Pending Payments')).toBeInTheDocument();
    expect(screen.getByText('Paid Payments')).toBeInTheDocument();
    expect(screen.getByText('Overdue Payments')).toBeInTheDocument();
    
    // Check payment table is displayed
    expect(screen.getByText('Rent Payment')).toBeInTheDocument();
    expect(screen.getByText('Electricity Bill')).toBeInTheDocument();
    expect(screen.getByText('Internet Bill')).toBeInTheDocument();
    
    // Check action buttons are displayed
    expect(screen.getAllByText('Mark Paid').length).toBe(2); // For PENDING and OVERDUE payments
    expect(screen.getAllByText('Edit').length).toBe(3);
    expect(screen.getAllByText('Delete').length).toBe(3);
  });

  test('filters payments by status', async () => {
    render(
      <BrowserRouter>
        <PaymentDashboard />
      </BrowserRouter>
    );
    
    // Wait for payments to load
    await waitFor(() => {
      expect(screen.getByText('Payment Reminders')).toBeInTheDocument();
    });
    
    // Find and change the filter dropdown
    const filterDropdown = screen.getByRole('combobox');
    fireEvent.change(filterDropdown, { target: { value: 'PAID' } });
    
    // Check that only paid payments are displayed
    expect(screen.queryByText('Rent Payment')).not.toBeInTheDocument();
    expect(screen.queryByText('Electricity Bill')).not.toBeInTheDocument();
    expect(screen.getByText('Internet Bill')).toBeInTheDocument();
  });

  test('searches payments by title', async () => {
    render(
      <BrowserRouter>
        <PaymentDashboard />
      </BrowserRouter>
    );
    
    // Wait for payments to load
    await waitFor(() => {
      expect(screen.getByText('Payment Reminders')).toBeInTheDocument();
    });
    
    // Find and use the search input
    const searchInput = screen.getByPlaceholderText('Search by title');
    fireEvent.change(searchInput, { target: { value: 'Rent' } });
    
    // Check that only matching payments are displayed
    expect(screen.getByText('Rent Payment')).toBeInTheDocument();
    expect(screen.queryByText('Electricity Bill')).not.toBeInTheDocument();
    expect(screen.queryByText('Internet Bill')).not.toBeInTheDocument();
  });

  test('marks payment as paid', async () => {
    render(
      <BrowserRouter>
        <PaymentDashboard />
      </BrowserRouter>
    );
    
    // Wait for payments to load
    await waitFor(() => {
      expect(screen.getByText('Payment Reminders')).toBeInTheDocument();
    });
    
    // Find and click the "Mark Paid" button for the overdue payment
    const markPaidButtons = screen.getAllByText('Mark Paid');
    fireEvent.click(markPaidButtons[1]); // Second button (for Electricity Bill)
    
    // Check that the API was called
    await waitFor(() => {
      expect(mockAxios.history.patch.length).toBe(1);
      expect(mockAxios.history.patch[0].url).toBe('/payment-reminders/2/status');
      expect(JSON.parse(mockAxios.history.patch[0].data)).toEqual({ status: 'PAID' });
    });
  });

  test('deletes payment', async () => {
    // Mock window.confirm to always return true
    window.confirm = jest.fn(() => true);
    
    render(
      <BrowserRouter>
        <PaymentDashboard />
      </BrowserRouter>
    );
    
    // Wait for payments to load
    await waitFor(() => {
      expect(screen.getByText('Payment Reminders')).toBeInTheDocument();
    });
    
    // Find and click the "Delete" button for the paid payment
    const deleteButtons = screen.getAllByText('Delete');
    fireEvent.click(deleteButtons[2]); // Third button (for Internet Bill)
    
    // Check that confirmation was requested
    expect(window.confirm).toHaveBeenCalled();
    
    // Check that the API was called
    await waitFor(() => {
      expect(mockAxios.history.delete.length).toBe(1);
      expect(mockAxios.history.delete[0].url).toBe('/payment-reminders/3');
    });
  });

  test('displays empty state when no payments', async () => {
    // Override the payment-reminders endpoint to return empty array
    mockAxios.onGet('/payment-reminders').reply(200, []);
    
    render(
      <BrowserRouter>
        <PaymentDashboard />
      </BrowserRouter>
    );
    
    // Wait for payments to load
    await waitFor(() => {
      expect(screen.getByText('Payment Reminders')).toBeInTheDocument();
    });
    
    // Check empty state message is displayed
    expect(screen.getByText(/No payment reminders found/i)).toBeInTheDocument();
  });
});
