import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { IncomeAnalytics } from '../IncomeAnalytics';
import { instance } from '../../../../axios/axiosConfig';

// Mock Chart.js
jest.mock('chart.js');
jest.mock('react-chartjs-2', () => ({
  Bar: () => <div data-testid="mock-bar-chart" />,
  Pie: () => <div data-testid="mock-pie-chart" />,
  Line: () => <div data-testid="mock-line-chart" />
}));

// Mock axios instance
jest.mock('../../../../axios/axiosConfig', () => ({
  instance: {
    get: jest.fn()
  }
}));

describe('IncomeAnalytics Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    
    // Mock API responses
    instance.get.mockImplementation((url) => {
      if (url === '/incomes/analysis/current-month') {
        return Promise.resolve({
          data: {
            month: '2025-04',
            totalIncome: 5000
          }
        });
      } else if (url.includes('/incomes/analysis/by-category')) {
        return Promise.resolve({
          data: {
            startDate: '2025-04-01',
            endDate: '2025-04-30',
            categoryTotals: {
              'Salary': 3500,
              'Freelance': 1000,
              'Investments': 500
            }
          }
        });
      } else if (url === '/incomes/analysis/monthly') {
        return Promise.resolve({
          data: {
            '2025-01': 4200,
            '2025-02': 4500,
            '2025-03': 4800,
            '2025-04': 5000
          }
        });
      }
      return Promise.reject(new Error('Not found'));
    });
  });

  test('renders the component with analytics data', async () => {
    render(<IncomeAnalytics />);
    
    // Wait for data to load
    await waitFor(() => {
      expect(screen.getByText('Income Analytics')).toBeInTheDocument();
    });
    
    // Check if current month income is displayed
    expect(await screen.findByText('$5000.00')).toBeInTheDocument();
    
    // Check if charts are rendered
    expect(screen.getByTestId('mock-line-chart')).toBeInTheDocument();
    expect(screen.getByTestId('mock-pie-chart')).toBeInTheDocument();
    
    // Check if category data is displayed in the table
    expect(screen.getByText('Salary')).toBeInTheDocument();
    expect(screen.getByText('Freelance')).toBeInTheDocument();
    expect(screen.getByText('Investments')).toBeInTheDocument();
  });

  test('allows changing date range', async () => {
    render(<IncomeAnalytics />);
    
    // Wait for data to load
    await waitFor(() => {
      expect(screen.getByText('Income Analytics')).toBeInTheDocument();
    });
    
    // Change date range
    fireEvent.change(screen.getByLabelText('Start Date'), { 
      target: { value: '2025-03-01' } 
    });
    
    fireEvent.change(screen.getByLabelText('End Date'), { 
      target: { value: '2025-03-31' } 
    });
    
    // Click apply button
    fireEvent.click(screen.getByText('Apply'));
    
    // Verify API call with new date range
    await waitFor(() => {
      expect(instance.get).toHaveBeenCalledWith(
        '/incomes/analysis/by-category?startDate=2025-03-01&endDate=2025-03-31'
      );
    });
  });

  test('handles API errors gracefully', async () => {
    // Mock API error
    instance.get.mockRejectedValue(new Error('API Error'));
    
    render(<IncomeAnalytics />);
    
    // Check for error message
    expect(await screen.findByText('Failed to load income analytics data. Please try again.')).toBeInTheDocument();
  });

  test('displays empty state when no data is available', async () => {
    // Mock empty data responses
    instance.get.mockImplementation((url) => {
      if (url === '/incomes/analysis/current-month') {
        return Promise.resolve({
          data: {
            month: '2025-04',
            totalIncome: 0
          }
        });
      } else if (url.includes('/incomes/analysis/by-category')) {
        return Promise.resolve({
          data: {
            startDate: '2025-04-01',
            endDate: '2025-04-30',
            categoryTotals: {}
          }
        });
      } else if (url === '/incomes/analysis/monthly') {
        return Promise.resolve({
          data: {}
        });
      }
      return Promise.reject(new Error('Not found'));
    });
    
    render(<IncomeAnalytics />);
    
    // Wait for data to load
    await waitFor(() => {
      expect(screen.getByText('Income Analytics')).toBeInTheDocument();
    });
    
    // Check if empty state messages are displayed
    expect(await screen.findByText('No monthly income data available.')).toBeInTheDocument();
    expect(screen.getByText('No category data available for the selected period.')).toBeInTheDocument();
  });
});
