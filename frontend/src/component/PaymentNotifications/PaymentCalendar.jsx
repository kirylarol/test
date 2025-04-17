import React, { useState, useEffect } from 'react';
import { Container, Row, Col, Card, Button, Spinner, Alert } from 'react-bootstrap';
import { instance } from '../../../axios/axiosConfig';
import { Calendar, momentLocalizer } from 'react-big-calendar';
import moment from 'moment';
import 'react-big-calendar/lib/css/react-big-calendar.css';
import { Link } from 'react-router-dom';

// Setup the localizer for the calendar
const localizer = momentLocalizer(moment);

const PaymentCalendar = () => {
  const [events, setEvents] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [currentDate, setCurrentDate] = useState(new Date());

  useEffect(() => {
    fetchCalendarData(currentDate);
  }, [currentDate]);

  const fetchCalendarData = async (date) => {
    try {
      setLoading(true);
      const month = date.getMonth() + 1; // JavaScript months are 0-indexed
      const year = date.getFullYear();
      
      const response = await instance.get('/dashboard/calendar', {
        params: { month, year }
      });
      
      // Transform the data into events for the calendar
      const calendarEvents = response.data.events.map(event => ({
        id: event.id,
        title: `${event.title} ${event.amount ? `($${event.amount})` : ''}`,
        start: new Date(event.date),
        end: new Date(event.date),
        allDay: true,
        resource: {
          id: event.id,
          title: event.title,
          amount: event.amount,
          status: event.status,
          category: event.category
        }
      }));
      
      setEvents(calendarEvents);
      setLoading(false);
    } catch (err) {
      setError('Failed to load calendar data. Please try again.');
      setLoading(false);
    }
  };

  const handleNavigate = (date) => {
    setCurrentDate(date);
  };

  const eventStyleGetter = (event) => {
    let backgroundColor;
    
    // Set color based on payment status
    switch (event.resource.status) {
      case 'PAID':
        backgroundColor = '#28a745'; // green
        break;
      case 'OVERDUE':
        backgroundColor = '#dc3545'; // red
        break;
      case 'PENDING':
        backgroundColor = '#ffc107'; // yellow
        break;
      default:
        backgroundColor = '#6c757d'; // gray
    }
    
    const style = {
      backgroundColor,
      borderRadius: '5px',
      opacity: 0.8,
      color: 'white',
      border: '0',
      display: 'block'
    };
    
    return {
      style
    };
  };

  const handleSelectEvent = (event) => {
    // Navigate to edit page for the selected payment
    window.location.href = `/payment-reminders/edit/${event.id}`;
  };

  return (
    <Container className="mt-4">
      <h2>Payment Calendar</h2>
      
      {error && <Alert variant="danger">{error}</Alert>}
      
      <Row className="mb-4">
        <Col>
          <Card>
            <Card.Body>
              <div className="d-flex justify-content-between align-items-center mb-3">
                <h4 className="mb-0">Upcoming Payments</h4>
                <Link to="/payment-reminders/new" className="btn btn-primary">
                  Add New Payment
                </Link>
              </div>
              
              <div className="mb-3">
                <small className="text-muted">
                  <span className="me-3">
                    <span className="badge bg-warning me-1">&nbsp;</span> Pending
                  </span>
                  <span className="me-3">
                    <span className="badge bg-success me-1">&nbsp;</span> Paid
                  </span>
                  <span>
                    <span className="badge bg-danger me-1">&nbsp;</span> Overdue
                  </span>
                </small>
              </div>
              
              {loading ? (
                <div className="text-center my-5">
                  <Spinner animation="border" />
                </div>
              ) : (
                <div style={{ height: 600 }}>
                  <Calendar
                    localizer={localizer}
                    events={events}
                    startAccessor="start"
                    endAccessor="end"
                    style={{ height: '100%' }}
                    onNavigate={handleNavigate}
                    date={currentDate}
                    eventPropGetter={eventStyleGetter}
                    onSelectEvent={handleSelectEvent}
                    views={['month', 'week', 'day']}
                    popup
                    selectable
                  />
                </div>
              )}
            </Card.Body>
          </Card>
        </Col>
      </Row>
    </Container>
  );
};

export default PaymentCalendar;
