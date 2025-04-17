import React, { useState, useEffect } from 'react';
import { Container, Row, Col, Card, Badge, ListGroup, Button, Spinner, Alert } from 'react-bootstrap';
import { instance } from '../../../axios/axiosConfig';

export const NotificationCenter = () => {
  const [notifications, setNotifications] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [successMessage, setSuccessMessage] = useState('');

  useEffect(() => {
    fetchNotifications();
  }, []);

  const fetchNotifications = async () => {
    try {
      setLoading(true);
      const response = await instance.get('/notifications');
      setNotifications(response.data);
      setLoading(false);
    } catch (err) {
      setError('Failed to load notifications. Please try again later.');
      setLoading(false);
    }
  };

  const markAsRead = async (notificationId) => {
    try {
      await instance.post(`/notifications/${notificationId}/read`);
      setNotifications(prevNotifications => 
        prevNotifications.map(notification => 
          notification.notificationId === notificationId 
            ? { ...notification, isRead: true } 
            : notification
        )
      );
    } catch (err) {
      setError('Failed to mark notification as read.');
      setTimeout(() => setError(null), 3000);
    }
  };

  const markAllAsRead = async () => {
    try {
      setLoading(true);
      await instance.post('/notifications/read-all');
      setNotifications(prevNotifications => 
        prevNotifications.map(notification => ({ ...notification, isRead: true }))
      );
      setSuccessMessage('All notifications marked as read');
      setTimeout(() => setSuccessMessage(''), 3000);
      setLoading(false);
    } catch (err) {
      setError('Failed to mark all notifications as read.');
      setTimeout(() => setError(null), 3000);
      setLoading(false);
    }
  };

  const checkLimits = async () => {
    try {
      setLoading(true);
      await instance.get('/categories/check-limits');
      fetchNotifications();
      setSuccessMessage('Spending limits checked successfully');
      setTimeout(() => setSuccessMessage(''), 3000);
    } catch (err) {
      setError('Failed to check spending limits.');
      setTimeout(() => setError(null), 3000);
      setLoading(false);
    }
  };

  if (loading && notifications.length === 0) {
    return (
      <Container className="mt-4">
        <div className="d-flex justify-content-center">
          <Spinner animation="border" />
        </div>
      </Container>
    );
  }

  const unreadCount = notifications.filter(notification => !notification.isRead).length;

  return (
    <Container className="mt-4">
      <Row className="mb-3">
        <Col>
          <h2>
            Notifications 
            {unreadCount > 0 && (
              <Badge bg="danger" className="ms-2">{unreadCount}</Badge>
            )}
          </h2>
        </Col>
        <Col className="text-end">
          <Button 
            variant="outline-primary" 
            className="me-2" 
            onClick={checkLimits}
            disabled={loading}
          >
            Check Limits Now
          </Button>
          {unreadCount > 0 && (
            <Button 
              variant="outline-secondary" 
              onClick={markAllAsRead}
              disabled={loading}
            >
              Mark All as Read
            </Button>
          )}
        </Col>
      </Row>
      
      {error && <Alert variant="danger">{error}</Alert>}
      {successMessage && <Alert variant="success">{successMessage}</Alert>}
      
      <Card>
        <Card.Body>
          {notifications.length === 0 ? (
            <Alert variant="info">
              No notifications to display. You're doing great with your spending!
            </Alert>
          ) : (
            <ListGroup variant="flush">
              {notifications.map(notification => (
                <ListGroup.Item 
                  key={notification.notificationId}
                  className={notification.isRead ? '' : 'bg-light'}
                >
                  <Row>
                    <Col>
                      <div className="d-flex justify-content-between">
                        <div>
                          {!notification.isRead && (
                            <Badge bg="primary" className="me-2">New</Badge>
                          )}
                          {notification.message}
                        </div>
                        <small className="text-muted">
                          {new Date(notification.createdAt).toLocaleString()}
                        </small>
                      </div>
                      {!notification.isRead && (
                        <div className="mt-2">
                          <Button 
                            variant="light" 
                            size="sm"
                            onClick={() => markAsRead(notification.notificationId)}
                          >
                            Mark as read
                          </Button>
                        </div>
                      )}
                    </Col>
                  </Row>
                </ListGroup.Item>
              ))}
            </ListGroup>
          )}
        </Card.Body>
      </Card>
    </Container>
  );
};

export default NotificationCenter;
