import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { IncomeForm } from '../IncomeForm';
import { instance } from '../../../../axios/axiosConfig';

// Mock axios instance
jest.mock('../../../../axios/axiosConfig', () => ({
  instance: {
    get: jest.fn(),
    post: jest.fn(),
    put: jest.fn()
  }
}));

describe('IncomeForm Component', () => {
  const mockOnIncomeAdded = jest.fn();
  
  beforeEach(() => {
    jest.clearAllMocks();
    
    // Mock API responses
    instance.get.mockImplementation((url) => {
      if (url === '/income-categories') {
        return Promise.resolve({
          data: [
            { incomeCategoryId: 1, name: 'Salary' },
            { incomeCategoryId: 2, name: 'Freelance' }
          ]
        });
      } else if (url === '/accounts/all') {
        return Promise.resolve({
          data: [
            { id: 1, name: 'Checking Account' },
            { id: 2, name: 'Savings Account' }
          ]
        });
      }
      return Promise.reject(new Error('Not found'));
    });
    
    instance.post.mockResolvedValue({
      data: {
        incomeId: 1,
        amount: 1000,
        description: 'Test income',
        date: '2025-04-17',
        account: { id: 1 },
        incomeCategory: { incomeCategoryId: 1 },
        isRecurring: false
      }
    });
  });

  test('renders the form with all fields', async () => {
    render(<IncomeForm onIncomeAdded={mockOnIncomeAdded} />);
    
    // Wait for data to load
    await waitFor(() => {
      expect(screen.getByText('Add New Income')).toBeInTheDocument();
    });
    
    // Check if all form fields are rendered
    expect(screen.getByLabelText('Amount')).toBeInTheDocument();
    expect(screen.getByLabelText('Date')).toBeInTheDocument();
    expect(screen.getByLabelText('Account')).toBeInTheDocument();
    expect(screen.getByLabelText('Category')).toBeInTheDocument();
    expect(screen.getByLabelText('Description')).toBeInTheDocument();
    expect(screen.getByLabelText('This is a recurring income')).toBeInTheDocument();
    expect(screen.getByText('Add Income')).toBeInTheDocument();
  });

  test('submits the form with valid data', async () => {
    render(<IncomeForm onIncomeAdded={mockOnIncomeAdded} />);
    
    // Wait for data to load
    await waitFor(() => {
      expect(screen.getByText('Add New Income')).toBeInTheDocument();
    });
    
    // Fill out the form
    fireEvent.change(screen.getByLabelText('Amount'), { target: { value: '1000' } });
    fireEvent.change(screen.getByLabelText('Date'), { target: { value: '2025-04-17' } });
    fireEvent.change(screen.getByLabelText('Account'), { target: { value: '1' } });
    fireEvent.change(screen.getByLabelText('Category'), { target: { value: '1' } });
    fireEvent.change(screen.getByLabelText('Description'), { target: { value: 'Test income' } });
    
    // Submit the form
    fireEvent.click(screen.getByText('Add Income'));
    
    // Verify API call
    await waitFor(() => {
      expect(instance.post).toHaveBeenCalledWith('/incomes', expect.objectContaining({
        amount: 1000,
        description: 'Test income',
        date: '2025-04-17',
        account: { id: 1 },
        incomeCategory: { incomeCategoryId: 1 },
        isRecurring: false
      }));
      expect(mockOnIncomeAdded).toHaveBeenCalled();
    });
    
    // Check for success message
    expect(await screen.findByText('Income added successfully!')).toBeInTheDocument();
  });

  test('handles recurring income fields', async () => {
    render(<IncomeForm onIncomeAdded={mockOnIncomeAdded} />);
    
    // Wait for data to load
    await waitFor(() => {
      expect(screen.getByText('Add New Income')).toBeInTheDocument();
    });
    
    // Check recurring checkbox
    fireEvent.click(screen.getByLabelText('This is a recurring income'));
    
    // Verify recurrence period field appears
    expect(screen.getByLabelText('Recurrence Period')).toBeInTheDocument();
    expect(screen.getByText('Monthly')).toBeInTheDocument();
    
    // Change recurrence period
    fireEvent.change(screen.getByLabelText('Recurrence Period'), { target: { value: 'WEEKLY' } });
    
    // Fill out other form fields
    fireEvent.change(screen.getByLabelText('Amount'), { target: { value: '500' } });
    fireEvent.change(screen.getByLabelText('Date'), { target: { value: '2025-04-17' } });
    fireEvent.change(screen.getByLabelText('Account'), { target: { value: '1' } });
    fireEvent.change(screen.getByLabelText('Category'), { target: { value: '1' } });
    
    // Submit the form
    fireEvent.click(screen.getByText('Add Income'));
    
    // Verify API call includes recurring data
    await waitFor(() => {
      expect(instance.post).toHaveBeenCalledWith('/incomes', expect.objectContaining({
        amount: 500,
        isRecurring: true,
        recurrencePeriod: 'WEEKLY'
      }));
    });
  });

  test('renders in edit mode with populated data', async () => {
    const editIncome = {
      incomeId: 1,
      amount: 2000,
      description: 'Existing income',
      date: '2025-03-15',
      account: { id: 2 },
      incomeCategory: { incomeCategoryId: 2 },
      isRecurring: true,
      recurrencePeriod: 'MONTHLY'
    };
    
    render(<IncomeForm onIncomeAdded={mockOnIncomeAdded} editIncome={editIncome} />);
    
    // Wait for data to load
    await waitFor(() => {
      expect(screen.getByText('Edit Income')).toBeInTheDocument();
    });
    
    // Check if form is populated with existing data
    expect(screen.getByLabelText('Amount')).toHaveValue(2000);
    expect(screen.getByLabelText('Date')).toHaveValue('2025-03-15');
    expect(screen.getByLabelText('Description')).toHaveValue('Existing income');
    expect(screen.getByLabelText('This is a recurring income')).toBeChecked();
    expect(screen.getByLabelText('Recurrence Period')).toHaveValue('MONTHLY');
    
    // Change some values
    fireEvent.change(screen.getByLabelText('Amount'), { target: { value: '2500' } });
    
    // Submit the form
    fireEvent.click(screen.getByText('Update Income'));
    
    // Verify API call for update
    await waitFor(() => {
      expect(instance.put).toHaveBeenCalledWith('/incomes/1', expect.objectContaining({
        amount: 2500,
        description: 'Existing income',
        isRecurring: true,
        recurrencePeriod: 'MONTHLY'
      }));
    });
  });

  test('handles API errors', async () => {
    // Mock API error
    instance.post.mockRejectedValue(new Error('API Error'));
    
    render(<IncomeForm onIncomeAdded={mockOnIncomeAdded} />);
    
    // Wait for data to load
    await waitFor(() => {
      expect(screen.getByText('Add New Income')).toBeInTheDocument();
    });
    
    // Fill out the form
    fireEvent.change(screen.getByLabelText('Amount'), { target: { value: '1000' } });
    fireEvent.change(screen.getByLabelText('Date'), { target: { value: '2025-04-17' } });
    fireEvent.change(screen.getByLabelText('Account'), { target: { value: '1' } });
    fireEvent.change(screen.getByLabelText('Category'), { target: { value: '1' } });
    
    // Submit the form
    fireEvent.click(screen.getByText('Add Income'));
    
    // Check for error message
    expect(await screen.findByText('Failed to save income. Please check your data and try again.')).toBeInTheDocument();
    expect(mockOnIncomeAdded).not.toHaveBeenCalled();
  });
});
