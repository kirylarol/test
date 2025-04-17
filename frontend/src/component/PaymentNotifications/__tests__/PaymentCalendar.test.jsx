import React from 'react';
import { render, screen, waitFor } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import axios from 'axios';
import MockAdapter from 'axios-mock-adapter';
import PaymentCalendar from '../PaymentCalendar';

// Mock axios
const mockAxios = new MockAdapter(axios);

// Mock the react-router-dom's useNavigate
jest.mock('react-router-dom', () => ({
  ...jest.requireActual('react-router-dom'),
  useNavigate: () => jest.fn()
}));

// Mock react-big-calendar
jest.mock('react-big-calendar', () => {
  return {
    momentLocalizer: () => ({}),
    Calendar: props => (
      <div data-testid="mock-calendar">
        <div>Calendar Mock Component</div>
        <div>Events: {props.events.length}</div>
        {props.events.map(event => (
          <div key={event.id} data-testid={`event-${event.id}`}>
            {event.title}
          </div>
        ))}
      </div>
    )
  };
});

describe('PaymentCalendar Component', () => {
  beforeEach(() => {
    mockAxios.reset();
    
    // Mock dashboard/calendar endpoint
    mockAxios.onGet('/dashboard/calendar').reply(200, {
      month: 4,
      year: 2025,
      startDate: '2025-04-01',
      endDate: '2025-04-30',
      events: [
        {
          id: 1,
          title: 'Rent Payment',
          amount: 1000.00,
          date: '2025-04-01',
          status: 'PENDING',
          category: 'Housing'
        },
        {
          id: 2,
          title: 'Electricity Bill',
          amount: 150.00,
          date: '2025-04-15',
          status: 'OVERDUE',
          category: 'Utilities'
        },
        {
          id: 3,
          title: 'Internet Bill',
          amount: 80.00,
          date: '2025-04-20',
          status: 'PAID',
          category: 'Utilities'
        }
      ]
    });
  });

  test('renders calendar with payment events', async () => {
    render(
      <BrowserRouter>
        <PaymentCalendar />
      </BrowserRouter>
    );
    
    // Wait for calendar data to load
    await waitFor(() => {
      expect(screen.getByText('Payment Calendar')).toBeInTheDocument();
    });
    
    // Check that the calendar component is rendered
    expect(screen.getByTestId('mock-calendar')).toBeInTheDocument();
    expect(screen.getByText('Calendar Mock Component')).toBeInTheDocument();
    
    // Check that events are passed to the calendar
    expect(screen.getByText('Events: 3')).toBeInTheDocument();
    
    // Check that event titles are displayed
    expect(screen.getByTestId('event-1')).toHaveTextContent('Rent Payment');
    expect(screen.getByTestId('event-2')).toHaveTextContent('Electricity Bill');
    expect(screen.getByTestId('event-3')).toHaveTextContent('Internet Bill');
  });

  test('displays loading state while fetching data', async () => {
    // Delay the API response
    mockAxios.onGet('/dashboard/calendar').reply(() => {
      return new Promise(resolve => {
        setTimeout(() => resolve([200, {
          month: 4,
          year: 2025,
          events: []
        }]), 100);
      });
    });
    
    render(
      <BrowserRouter>
        <PaymentCalendar />
      </BrowserRouter>
    );
    
    // Check that loading spinner is displayed
    expect(screen.getByRole('status')).toBeInTheDocument();
    
    // Wait for data to load
    await waitFor(() => {
      expect(screen.queryByRole('status')).not.toBeInTheDocument();
    });
  });

  test('displays empty calendar when no events', async () => {
    // Override the calendar endpoint to return empty events
    mockAxios.onGet('/dashboard/calendar').reply(200, {
      month: 4,
      year: 2025,
      startDate: '2025-04-01',
      endDate: '2025-04-30',
      events: []
    });
    
    render(
      <BrowserRouter>
        <PaymentCalendar />
      </BrowserRouter>
    );
    
    // Wait for calendar data to load
    await waitFor(() => {
      expect(screen.getByText('Payment Calendar')).toBeInTheDocument();
    });
    
    // Check that the calendar shows zero events
    expect(screen.getByText('Events: 0')).toBeInTheDocument();
  });

  test('displays error message when API fails', async () => {
    // Override the calendar endpoint to return an error
    mockAxios.onGet('/dashboard/calendar').reply(500);
    
    render(
      <BrowserRouter>
        <PaymentCalendar />
      </BrowserRouter>
    );
    
    // Wait for error to be displayed
    await waitFor(() => {
      expect(screen.getByText(/Failed to load calendar data/i)).toBeInTheDocument();
    });
  });
});
