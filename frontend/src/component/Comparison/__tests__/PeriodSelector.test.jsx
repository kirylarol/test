import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { PeriodSelector } from '../PeriodSelector';
import { instance } from '../../../../axios/axiosConfig';

// Mock axios instance
jest.mock('../../../../axios/axiosConfig', () => ({
  instance: {
    get: jest.fn()
  }
}));

describe('PeriodSelector Component', () => {
  const mockOnPeriodsSelected = jest.fn();
  
  beforeEach(() => {
    jest.clearAllMocks();
    
    // Mock API response for predefined periods
    instance.get.mockResolvedValue({
      data: {
        currentVsPreviousMonth: {
          period1Start: '2025-03-01',
          period1End: '2025-03-31',
          period2Start: '2025-04-01',
          period2End: '2025-04-30'
        },
        currentVsSameMonthLastYear: {
          period1Start: '2024-04-01',
          period1End: '2024-04-30',
          period2Start: '2025-04-01',
          period2End: '2025-04-30'
        }
      }
    });
  });

  test('renders the period selector form', async () => {
    render(<PeriodSelector onPeriodsSelected={mockOnPeriodsSelected} />);
    
    // Wait for data to load
    await waitFor(() => {
      expect(screen.getByText('Select Periods to Compare')).toBeInTheDocument();
    });
    
    // Check if form elements are rendered
    expect(screen.getByText('Quick Selection')).toBeInTheDocument();
    expect(screen.getByText('Period 1')).toBeInTheDocument();
    expect(screen.getByText('Period 2')).toBeInTheDocument();
    expect(screen.getByText('Compare Periods')).toBeInTheDocument();
  });

  test('loads predefined periods on mount', async () => {
    render(<PeriodSelector onPeriodsSelected={mockOnPeriodsSelected} />);
    
    // Verify API call
    await waitFor(() => {
      expect(instance.get).toHaveBeenCalledWith('/comparison/predefined-periods');
    });
    
    // Check if predefined options are available
    expect(screen.getByText('Current Month vs Previous Month')).toBeInTheDocument();
    expect(screen.getByText('Current Month vs Same Month Last Year')).toBeInTheDocument();
  });

  test('selects predefined period when option is chosen', async () => {
    render(<PeriodSelector onPeriodsSelected={mockOnPeriodsSelected} />);
    
    // Wait for data to load
    await waitFor(() => {
      expect(screen.getByText('Current Month vs Previous Month')).toBeInTheDocument();
    });
    
    // Select a predefined period
    fireEvent.change(screen.getByLabelText('Quick Selection'), { 
      target: { value: 'currentVsPreviousMonth' } 
    });
    
    // Check if date fields are populated
    const startDateInputs = screen.getAllByLabelText('Start Date');
    const endDateInputs = screen.getAllByLabelText('End Date');
    
    expect(startDateInputs[0]).toHaveValue('2025-03-01');
    expect(endDateInputs[0]).toHaveValue('2025-03-31');
    expect(startDateInputs[1]).toHaveValue('2025-04-01');
    expect(endDateInputs[1]).toHaveValue('2025-04-30');
  });

  test('submits selected periods when form is submitted', async () => {
    render(<PeriodSelector onPeriodsSelected={mockOnPeriodsSelected} />);
    
    // Wait for data to load
    await waitFor(() => {
      expect(screen.getByText('Select Periods to Compare')).toBeInTheDocument();
    });
    
    // Fill out the form manually
    const startDateInputs = screen.getAllByLabelText('Start Date');
    const endDateInputs = screen.getAllByLabelText('End Date');
    
    fireEvent.change(startDateInputs[0], { target: { value: '2025-01-01' } });
    fireEvent.change(endDateInputs[0], { target: { value: '2025-01-31' } });
    fireEvent.change(startDateInputs[1], { target: { value: '2025-02-01' } });
    fireEvent.change(endDateInputs[1], { target: { value: '2025-02-28' } });
    
    // Submit the form
    fireEvent.click(screen.getByText('Compare Periods'));
    
    // Verify callback was called with correct periods
    expect(mockOnPeriodsSelected).toHaveBeenCalledWith({
      period1Start: '2025-01-01',
      period1End: '2025-01-31',
      period2Start: '2025-02-01',
      period2End: '2025-02-28'
    });
  });

  test('shows error when dates are invalid', async () => {
    render(<PeriodSelector onPeriodsSelected={mockOnPeriodsSelected} />);
    
    // Wait for data to load
    await waitFor(() => {
      expect(screen.getByText('Select Periods to Compare')).toBeInTheDocument();
    });
    
    // Fill out the form with invalid dates (end before start)
    const startDateInputs = screen.getAllByLabelText('Start Date');
    const endDateInputs = screen.getAllByLabelText('End Date');
    
    fireEvent.change(startDateInputs[0], { target: { value: '2025-01-31' } });
    fireEvent.change(endDateInputs[0], { target: { value: '2025-01-01' } });
    fireEvent.change(startDateInputs[1], { target: { value: '2025-02-28' } });
    fireEvent.change(endDateInputs[1], { target: { value: '2025-02-01' } });
    
    // Submit the form
    fireEvent.click(screen.getByText('Compare Periods'));
    
    // Check for error message
    expect(screen.getByText('End date cannot be before start date.')).toBeInTheDocument();
    
    // Verify callback was not called
    expect(mockOnPeriodsSelected).not.toHaveBeenCalled();
  });

  test('handles API errors gracefully', async () => {
    // Mock API error
    instance.get.mockRejectedValue(new Error('API Error'));
    
    render(<PeriodSelector onPeriodsSelected={mockOnPeriodsSelected} />);
    
    // Check for error message
    expect(await screen.findByText('Failed to load predefined periods. Please try again.')).toBeInTheDocument();
  });
});
