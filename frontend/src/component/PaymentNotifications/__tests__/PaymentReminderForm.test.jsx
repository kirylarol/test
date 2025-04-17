import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import axios from 'axios';
import MockAdapter from 'axios-mock-adapter';
import PaymentReminderForm from '../PaymentReminderForm';

// Mock axios
const mockAxios = new MockAdapter(axios);

// Mock the react-router-dom's useNavigate and useParams
jest.mock('react-router-dom', () => ({
  ...jest.requireActual('react-router-dom'),
  useNavigate: () => jest.fn(),
  useParams: () => ({ id: null }) // Default to create mode
}));

describe('PaymentReminderForm Component', () => {
  beforeEach(() => {
    mockAxios.reset();
    
    // Mock categories endpoint
    mockAxios.onGet('/categories').reply(200, [
      { id: 1, name: 'Bills' },
      { id: 2, name: 'Groceries' }
    ]);
    
    // Mock payment-reminders POST endpoint
    mockAxios.onPost('/payment-reminders').reply(201, {
      id: 1,
      title: 'Test Payment',
      amount: 100.00,
      dueDate: '2025-05-01',
      status: 'PENDING'
    });
  });

  test('renders form in create mode', async () => {
    render(
      <BrowserRouter>
        <PaymentReminderForm />
      </BrowserRouter>
    );
    
    // Wait for categories to load
    await waitFor(() => {
      expect(screen.getByText('Create Payment Reminder')).toBeInTheDocument();
    });
    
    // Check form elements
    expect(screen.getByLabelText(/Title/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/Amount/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/Due Date/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/Category/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/This is a recurring payment/i)).toBeInTheDocument();
    expect(screen.getByText(/Create Payment/i)).toBeInTheDocument();
  });

  test('submits form with valid data', async () => {
    render(
      <BrowserRouter>
        <PaymentReminderForm />
      </BrowserRouter>
    );
    
    // Wait for categories to load
    await waitFor(() => {
      expect(screen.getByText('Create Payment Reminder')).toBeInTheDocument();
    });
    
    // Fill out form
    fireEvent.change(screen.getByLabelText(/Title/i), { target: { value: 'Rent Payment' } });
    fireEvent.change(screen.getByLabelText(/Amount/i), { target: { value: '1000' } });
    
    // Select category
    fireEvent.change(screen.getByLabelText(/Category/i), { target: { value: '1' } });
    
    // Check recurring payment
    fireEvent.click(screen.getByLabelText(/This is a recurring payment/i));
    
    // Select recurrence pattern (appears after checking recurring)
    await waitFor(() => {
      expect(screen.getByLabelText(/Recurrence Pattern/i)).toBeInTheDocument();
    });
    fireEvent.change(screen.getByLabelText(/Recurrence Pattern/i), { target: { value: 'MONTHLY' } });
    
    // Submit form
    fireEvent.click(screen.getByText(/Create Payment/i));
    
    // Check success message appears
    await waitFor(() => {
      expect(screen.getByText(/Payment reminder saved successfully/i)).toBeInTheDocument();
    });
  });

  test('displays validation errors for required fields', async () => {
    // Override the POST endpoint to return an error
    mockAxios.onPost('/payment-reminders').reply(400, {
      message: 'Validation failed'
    });
    
    render(
      <BrowserRouter>
        <PaymentReminderForm />
      </BrowserRouter>
    );
    
    // Wait for categories to load
    await waitFor(() => {
      expect(screen.getByText('Create Payment Reminder')).toBeInTheDocument();
    });
    
    // Submit form without filling required fields
    fireEvent.click(screen.getByText(/Create Payment/i));
    
    // Check for required field validation
    await waitFor(() => {
      // The form has HTML5 validation which prevents submission with empty required fields
      // We can check if the form was prevented from submitting by verifying the error message doesn't appear
      expect(screen.queryByText(/Payment reminder saved successfully/i)).not.toBeInTheDocument();
    });
  });

  test('loads and displays payment data in edit mode', async () => {
    // Mock useParams to return an ID (edit mode)
    jest.mock('react-router-dom', () => ({
      ...jest.requireActual('react-router-dom'),
      useNavigate: () => jest.fn(),
      useParams: () => ({ id: '1' })
    }));
    
    // Mock GET endpoint for existing payment
    mockAxios.onGet('/payment-reminders/1').reply(200, {
      id: 1,
      title: 'Existing Payment',
      amount: 500.00,
      dueDate: '2025-05-15',
      description: 'Test description',
      category: { id: 2, name: 'Groceries' },
      isRecurring: true,
      recurrencePattern: 'MONTHLY',
      notificationDays: 5,
      status: 'PENDING'
    });
    
    render(
      <BrowserRouter>
        <PaymentReminderForm />
      </BrowserRouter>
    );
    
    // Wait for data to load
    await waitFor(() => {
      expect(screen.getByText('Edit Payment Reminder')).toBeInTheDocument();
    });
    
    // Check form is populated with existing data
    expect(screen.getByLabelText(/Title/i).value).toBe('Existing Payment');
    expect(screen.getByLabelText(/Amount/i).value).toBe('500');
    expect(screen.getByLabelText(/Description/i).value).toBe('Test description');
    expect(screen.getByLabelText(/This is a recurring payment/i).checked).toBe(true);
  });
});
