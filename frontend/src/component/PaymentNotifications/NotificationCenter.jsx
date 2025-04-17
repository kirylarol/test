import React, { useState, useEffect } from 'react';
import { Container, Row, Col, Card, ListGroup, Badge, Button, Spinner, Alert } from 'react-bootstrap';
import { instance } from '../../../axios/axiosConfig';
import { format } from 'date-fns';
import { Link } from 'react-router-dom';

const NotificationCenter = () => {
  const [notifications, setNotifications] = useState([]);
  const [unreadCount, setUnreadCount] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchNotifications();
    fetchUnreadCount();
  }, []);

  const fetchNotifications = async () => {
    try {
      setLoading(true);
      const response = await instance.get('/notifications');
      setNotifications(response.data);
      setLoading(false);
    } catch (err) {
      setError('Failed to load notifications. Please try again.');
      setLoading(false);
    }
  };

  const fetchUnreadCount = async () => {
    try {
      const response = await instance.get('/notifications/count');
      setUnreadCount(response.data.count);
    } catch (err) {
      console.error('Failed to load unread count:', err);
    }
  };

  const handleMarkAsRead = async (id) => {
    try {
      await instance.patch(`/notifications/${id}/read`);
      
      // Update the notification in the local state
      setNotifications(prevNotifications => 
        prevNotifications.map(notification => 
          notification.id === id ? { ...notification, isRead: true } : notification
        )
      );
      
      // Update the unread count
      fetchUnreadCount();
    } catch (err) {
      setError('Failed to mark notification as read. Please try again.');
    }
  };

  const handleMarkAllAsRead = async () => {
    try {
      await instance.patch('/notifications/read-all');
      
      // Update all notifications in the local state
      setNotifications(prevNotifications => 
        prevNotifications.map(notification => ({ ...notification, isRead: true }))
      );
      
      // Update the unread count
      setUnreadCount(0);
    } catch (err) {
      setError('Failed to mark all notifications as read. Please try again.');
    }
  };

  const handleTestNotification = async () => {
    try {
      await instance.post('/notifications/test');
      
      // Refresh notifications
      fetchNotifications();
      fetchUnreadCount();
    } catch (err) {
      setError('Failed to create test notification. Please try again.');
    }
  };

  const formatDateTime = (dateTimeString) => {
    const date = new Date(dateTimeString);
    return format(date, 'MMM d, yyyy h:mm a');
  };

  const getNotificationIcon = (type) => {
    switch (type) {
      case 'DUE_DATE':
        return '📅';
      case 'OVERDUE':
        return '⚠️';
      case 'REMINDER':
        return '🔔';
      case 'PAYMENT_CREATED':
        return '✅';
      case 'PAYMENT_UPDATED':
        return '✏️';
      case 'PAYMENT_DELETED':
        return '🗑️';
      default:
        return '📌';
    }
  };

  const getNotificationTitle = (notification) => {
    const { notificationType, paymentReminder } = notification;
    
    switch (notificationType) {
      case 'DUE_DATE':
        return `Payment due soon: ${paymentReminder.title}`;
      case 'OVERDUE':
        return `Overdue payment: ${paymentReminder.title}`;
      case 'REMINDER':
        return `Reminder: ${paymentReminder.title}`;
      case 'PAYMENT_CREATED':
        return `New payment created: ${paymentReminder.title}`;
      case 'PAYMENT_UPDATED':
        return `Payment updated: ${paymentReminder.title}`;
      case 'PAYMENT_DELETED':
        return `Payment deleted: ${paymentReminder.title}`;
      default:
        return `Notification: ${paymentReminder.title}`;
    }
  };

  return (
    <Container className="mt-4">
      <h2>Notification Center</h2>
      
      {error && <Alert variant="danger">{error}</Alert>}
      
      <Row className="mb-4">
        <Col md={6}>
          <Card>
            <Card.Body>
              <div className="d-flex justify-content-between align-items-center">
                <div>
                  <h4>Notifications</h4>
                  <p className="text-muted mb-0">
                    You have {unreadCount} unread {unreadCount === 1 ? 'notification' : 'notifications'}
                  </p>
                </div>
                <div>
                  <Button 
                    variant="outline-primary" 
                    className="me-2"
                    onClick={handleMarkAllAsRead}
                    disabled={unreadCount === 0}
                  >
                    Mark All as Read
                  </Button>
                  <Button 
                    variant="outline-secondary"
                    onClick={handleTestNotification}
                  >
                    Test Notification
                  </Button>
                </div>
              </div>
            </Card.Body>
          </Card>
        </Col>
      </Row>
      
      <Card>
        <Card.Body>
          {loading ? (
            <div className="text-center my-5">
              <Spinner animation="border" />
            </div>
          ) : (
            <>
              {notifications.length === 0 ? (
                <Alert variant="info">
                  No notifications found. Click "Test Notification" to create a test notification.
                </Alert>
              ) : (
                <ListGroup>
                  {notifications.map(notification => (
                    <ListGroup.Item 
                      key={notification.id}
                      className={notification.isRead ? '' : 'bg-light'}
                    >
                      <div className="d-flex justify-content-between align-items-center">
                        <div>
                          <div className="d-flex align-items-center">
                            <span className="me-2 fs-4">{getNotificationIcon(notification.notificationType)}</span>
                            <div>
                              <h5 className="mb-1">{getNotificationTitle(notification)}</h5>
                              <p className="text-muted mb-1">
                                {formatDateTime(notification.notificationDate)}
                              </p>
                              <p className="mb-0">
                                {notification.paymentReminder.amount && (
                                  <span className="me-2">
                                    Amount: ${notification.paymentReminder.amount}
                                  </span>
                                )}
                                <span className="me-2">
                                  Due: {format(new Date(notification.paymentReminder.dueDate), 'MMM d, yyyy')}
                                </span>
                                <Badge bg={
                                  notification.paymentReminder.status === 'PAID' ? 'success' :
                                  notification.paymentReminder.status === 'OVERDUE' ? 'danger' : 'warning'
                                }>
                                  {notification.paymentReminder.status}
                                </Badge>
                              </p>
                            </div>
                          </div>
                        </div>
                        <div>
                          {!notification.isRead && (
                            <Button 
                              variant="outline-primary" 
                              size="sm"
                              className="me-2"
                              onClick={() => handleMarkAsRead(notification.id)}
                            >
                              Mark as Read
                            </Button>
                          )}
                          <Link 
                            to={`/payment-reminders/edit/${notification.paymentReminder.id}`}
                            className="btn btn-outline-secondary btn-sm"
                          >
                            View Payment
                          </Link>
                        </div>
                      </div>
                    </ListGroup.Item>
                  ))}
                </ListGroup>
              )}
            </>
          )}
        </Card.Body>
      </Card>
    </Container>
  );
};

export default NotificationCenter;
