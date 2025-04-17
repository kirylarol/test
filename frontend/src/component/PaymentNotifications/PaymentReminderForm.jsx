import React, { useState, useEffect } from 'react';
import { Container, Form, Button, Row, Col, Card, Alert } from 'react-bootstrap';
import { instance } from '../../../axios/axiosConfig';
import { useNavigate, useParams } from 'react-router-dom';
import DatePicker from 'react-datepicker';
import 'react-datepicker/dist/react-datepicker.css';

const PaymentReminderForm = () => {
  const navigate = useNavigate();
  const { id } = useParams();
  const isEditMode = !!id;

  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(false);

  const [formData, setFormData] = useState({
    title: '',
    amount: '',
    dueDate: new Date(),
    description: '',
    category: '',
    isRecurring: false,
    recurrencePattern: 'MONTHLY',
    notificationDays: 3,
    status: 'PENDING'
  });

  const recurrenceOptions = [
    { value: 'DAILY', label: 'Daily' },
    { value: 'WEEKLY', label: 'Weekly' },
    { value: 'BIWEEKLY', label: 'Bi-weekly' },
    { value: 'MONTHLY', label: 'Monthly' },
    { value: 'QUARTERLY', label: 'Quarterly' },
    { value: 'SEMIANNUALLY', label: 'Semi-annually' },
    { value: 'ANNUALLY', label: 'Annually' }
  ];

  useEffect(() => {
    fetchCategories();
    if (isEditMode) {
      fetchPaymentReminder();
    }
  }, [id]);

  const fetchCategories = async () => {
    try {
      const response = await instance.get('/categories');
      setCategories(response.data);
    } catch (err) {
      setError('Failed to load categories. Please try again.');
    }
  };

  const fetchPaymentReminder = async () => {
    try {
      setLoading(true);
      const response = await instance.get(`/payment-reminders/${id}`);
      const reminder = response.data;
      
      setFormData({
        title: reminder.title,
        amount: reminder.amount,
        dueDate: new Date(reminder.dueDate),
        description: reminder.description || '',
        category: reminder.category ? reminder.category.id : '',
        isRecurring: reminder.isRecurring,
        recurrencePattern: reminder.recurrencePattern || 'MONTHLY',
        notificationDays: reminder.notificationDays,
        status: reminder.status
      });
      
      setLoading(false);
    } catch (err) {
      setError('Failed to load payment reminder. Please try again.');
      setLoading(false);
    }
  };

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value
    }));
  };

  const handleDateChange = (date) => {
    setFormData(prev => ({
      ...prev,
      dueDate: date
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    try {
      setLoading(true);
      setError(null);
      
      const payload = {
        ...formData,
        amount: parseFloat(formData.amount),
        category: formData.category ? { id: parseInt(formData.category) } : null,
        dueDate: formData.dueDate.toISOString().split('T')[0]
      };
      
      if (isEditMode) {
        await instance.put(`/payment-reminders/${id}`, payload);
      } else {
        await instance.post('/payment-reminders', payload);
      }
      
      setSuccess(true);
      setLoading(false);
      
      // Redirect after a short delay
      setTimeout(() => {
        navigate('/payment-reminders');
      }, 1500);
      
    } catch (err) {
      setError('Failed to save payment reminder. Please try again.');
      setLoading(false);
    }
  };

  return (
    <Container className="mt-4">
      <Card>
        <Card.Header>
          <h2>{isEditMode ? 'Edit Payment Reminder' : 'Create Payment Reminder'}</h2>
        </Card.Header>
        <Card.Body>
          {error && <Alert variant="danger">{error}</Alert>}
          {success && <Alert variant="success">Payment reminder saved successfully!</Alert>}
          
          <Form onSubmit={handleSubmit}>
            <Row>
              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label>Title*</Form.Label>
                  <Form.Control
                    type="text"
                    name="title"
                    value={formData.title}
                    onChange={handleChange}
                    required
                    placeholder="e.g., Rent Payment"
                  />
                </Form.Group>
              </Col>
              
              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label>Amount</Form.Label>
                  <Form.Control
                    type="number"
                    name="amount"
                    value={formData.amount}
                    onChange={handleChange}
                    step="0.01"
                    min="0"
                    placeholder="e.g., 1000.00"
                  />
                </Form.Group>
              </Col>
            </Row>
            
            <Row>
              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label>Due Date*</Form.Label>
                  <DatePicker
                    selected={formData.dueDate}
                    onChange={handleDateChange}
                    className="form-control"
                    dateFormat="yyyy-MM-dd"
                    minDate={new Date()}
                  />
                </Form.Group>
              </Col>
              
              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label>Category</Form.Label>
                  <Form.Select
                    name="category"
                    value={formData.category}
                    onChange={handleChange}
                  >
                    <option value="">Select a category</option>
                    {categories.map(category => (
                      <option key={category.id} value={category.id}>
                        {category.name}
                      </option>
                    ))}
                  </Form.Select>
                </Form.Group>
              </Col>
            </Row>
            
            <Form.Group className="mb-3">
              <Form.Label>Description</Form.Label>
              <Form.Control
                as="textarea"
                name="description"
                value={formData.description}
                onChange={handleChange}
                rows={3}
                placeholder="Add any additional details about this payment"
              />
            </Form.Group>
            
            <Row>
              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Check
                    type="checkbox"
                    label="This is a recurring payment"
                    name="isRecurring"
                    checked={formData.isRecurring}
                    onChange={handleChange}
                  />
                </Form.Group>
              </Col>
              
              {formData.isRecurring && (
                <Col md={6}>
                  <Form.Group className="mb-3">
                    <Form.Label>Recurrence Pattern</Form.Label>
                    <Form.Select
                      name="recurrencePattern"
                      value={formData.recurrencePattern}
                      onChange={handleChange}
                    >
                      {recurrenceOptions.map(option => (
                        <option key={option.value} value={option.value}>
                          {option.label}
                        </option>
                      ))}
                    </Form.Select>
                  </Form.Group>
                </Col>
              )}
            </Row>
            
            <Row>
              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label>Notification Days Before Due Date</Form.Label>
                  <Form.Control
                    type="number"
                    name="notificationDays"
                    value={formData.notificationDays}
                    onChange={handleChange}
                    min="0"
                    max="30"
                  />
                  <Form.Text className="text-muted">
                    How many days before the due date should we notify you?
                  </Form.Text>
                </Form.Group>
              </Col>
              
              {isEditMode && (
                <Col md={6}>
                  <Form.Group className="mb-3">
                    <Form.Label>Status</Form.Label>
                    <Form.Select
                      name="status"
                      value={formData.status}
                      onChange={handleChange}
                    >
                      <option value="PENDING">Pending</option>
                      <option value="PAID">Paid</option>
                      <option value="OVERDUE">Overdue</option>
                    </Form.Select>
                  </Form.Group>
                </Col>
              )}
            </Row>
            
            <div className="d-flex justify-content-between mt-4">
              <Button variant="secondary" onClick={() => navigate('/payment-reminders')}>
                Cancel
              </Button>
              <Button variant="primary" type="submit" disabled={loading}>
                {loading ? 'Saving...' : (isEditMode ? 'Update Payment' : 'Create Payment')}
              </Button>
            </div>
          </Form>
        </Card.Body>
      </Card>
    </Container>
  );
};

export default PaymentReminderForm;
