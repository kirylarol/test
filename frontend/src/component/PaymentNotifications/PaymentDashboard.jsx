import React, { useState, useEffect } from 'react';
import { Container, Row, Col, Card, Table, Badge, Button, Spinner, Alert, Form } from 'react-bootstrap';
import { instance } from '../../../axios/axiosConfig';
import { Link } from 'react-router-dom';
import { format } from 'date-fns';

const PaymentDashboard = () => {
  const [payments, setPayments] = useState([]);
  const [summary, setSummary] = useState({ pending: 0, paid: 0, overdue: 0 });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [filter, setFilter] = useState('all');
  const [searchTerm, setSearchTerm] = useState('');

  useEffect(() => {
    fetchPayments();
    fetchSummary();
  }, []);

  const fetchPayments = async () => {
    try {
      setLoading(true);
      const response = await instance.get('/payment-reminders');
      setPayments(response.data);
      setLoading(false);
    } catch (err) {
      setError('Failed to load payment reminders. Please try again.');
      setLoading(false);
    }
  };

  const fetchSummary = async () => {
    try {
      const response = await instance.get('/dashboard/payment-summary');
      setSummary(response.data);
    } catch (err) {
      console.error('Failed to load payment summary:', err);
    }
  };

  const handleStatusChange = async (id, newStatus) => {
    try {
      await instance.patch(`/payment-reminders/${id}/status`, { status: newStatus });
      
      // Update the payment in the local state
      setPayments(prevPayments => 
        prevPayments.map(payment => 
          payment.id === id ? { ...payment, status: newStatus } : payment
        )
      );
      
      // Update the summary counts
      fetchSummary();
    } catch (err) {
      setError('Failed to update payment status. Please try again.');
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm('Are you sure you want to delete this payment reminder?')) {
      try {
        await instance.delete(`/payment-reminders/${id}`);
        
        // Remove the payment from the local state
        setPayments(prevPayments => prevPayments.filter(payment => payment.id !== id));
        
        // Update the summary counts
        fetchSummary();
      } catch (err) {
        setError('Failed to delete payment reminder. Please try again.');
      }
    }
  };

  const handleFilterChange = (e) => {
    setFilter(e.target.value);
  };

  const handleSearchChange = (e) => {
    setSearchTerm(e.target.value);
  };

  const filteredPayments = payments.filter(payment => {
    // Apply status filter
    if (filter !== 'all' && payment.status !== filter) {
      return false;
    }
    
    // Apply search filter
    if (searchTerm && !payment.title.toLowerCase().includes(searchTerm.toLowerCase())) {
      return false;
    }
    
    return true;
  });

  const formatDate = (dateString) => {
    const date = new Date(dateString);
    return format(date, 'MMM d, yyyy');
  };

  const formatCurrency = (amount) => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD'
    }).format(amount);
  };

  const getStatusBadge = (status) => {
    switch (status) {
      case 'PENDING':
        return <Badge bg="warning">Pending</Badge>;
      case 'PAID':
        return <Badge bg="success">Paid</Badge>;
      case 'OVERDUE':
        return <Badge bg="danger">Overdue</Badge>;
      default:
        return <Badge bg="secondary">{status}</Badge>;
    }
  };

  return (
    <Container className="mt-4">
      <h2>Payment Reminders</h2>
      
      {error && <Alert variant="danger">{error}</Alert>}
      
      <Row className="mb-4">
        <Col md={4}>
          <Card className="text-center">
            <Card.Body>
              <h3>{summary.pending}</h3>
              <p className="text-warning mb-0">Pending Payments</p>
            </Card.Body>
          </Card>
        </Col>
        <Col md={4}>
          <Card className="text-center">
            <Card.Body>
              <h3>{summary.paid}</h3>
              <p className="text-success mb-0">Paid Payments</p>
            </Card.Body>
          </Card>
        </Col>
        <Col md={4}>
          <Card className="text-center">
            <Card.Body>
              <h3>{summary.overdue}</h3>
              <p className="text-danger mb-0">Overdue Payments</p>
            </Card.Body>
          </Card>
        </Col>
      </Row>
      
      <Card className="mb-4">
        <Card.Body>
          <div className="d-flex justify-content-between align-items-center mb-3">
            <div>
              <Link to="/payment-reminders/new" className="btn btn-primary">
                Add New Payment
              </Link>
            </div>
            <div className="d-flex">
              <Form.Group className="me-2">
                <Form.Control
                  type="text"
                  placeholder="Search by title"
                  value={searchTerm}
                  onChange={handleSearchChange}
                />
              </Form.Group>
              <Form.Group>
                <Form.Select value={filter} onChange={handleFilterChange}>
                  <option value="all">All Payments</option>
                  <option value="PENDING">Pending</option>
                  <option value="PAID">Paid</option>
                  <option value="OVERDUE">Overdue</option>
                </Form.Select>
              </Form.Group>
            </div>
          </div>
          
          {loading ? (
            <div className="text-center my-5">
              <Spinner animation="border" />
            </div>
          ) : (
            <>
              {filteredPayments.length === 0 ? (
                <Alert variant="info">
                  No payment reminders found. Click "Add New Payment" to create one.
                </Alert>
              ) : (
                <Table responsive striped hover>
                  <thead>
                    <tr>
                      <th>Title</th>
                      <th>Amount</th>
                      <th>Due Date</th>
                      <th>Category</th>
                      <th>Status</th>
                      <th>Recurring</th>
                      <th>Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    {filteredPayments.map(payment => (
                      <tr key={payment.id}>
                        <td>{payment.title}</td>
                        <td>{payment.amount ? formatCurrency(payment.amount) : '-'}</td>
                        <td>{formatDate(payment.dueDate)}</td>
                        <td>{payment.category ? payment.category.name : '-'}</td>
                        <td>{getStatusBadge(payment.status)}</td>
                        <td>{payment.isRecurring ? 
                          <Badge bg="info">{payment.recurrencePattern.toLowerCase()}</Badge> : 
                          <Badge bg="secondary">No</Badge>}
                        </td>
                        <td>
                          <div className="d-flex">
                            {payment.status === 'PENDING' && (
                              <Button 
                                variant="outline-success" 
                                size="sm" 
                                className="me-1"
                                onClick={() => handleStatusChange(payment.id, 'PAID')}
                              >
                                Mark Paid
                              </Button>
                            )}
                            {payment.status === 'OVERDUE' && (
                              <Button 
                                variant="outline-success" 
                                size="sm" 
                                className="me-1"
                                onClick={() => handleStatusChange(payment.id, 'PAID')}
                              >
                                Mark Paid
                              </Button>
                            )}
                            <Link 
                              to={`/payment-reminders/edit/${payment.id}`} 
                              className="btn btn-outline-primary btn-sm me-1"
                            >
                              Edit
                            </Link>
                            <Button 
                              variant="outline-danger" 
                              size="sm"
                              onClick={() => handleDelete(payment.id)}
                            >
                              Delete
                            </Button>
                          </div>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </Table>
              )}
            </>
          )}
        </Card.Body>
      </Card>
    </Container>
  );
};

export default PaymentDashboard;
