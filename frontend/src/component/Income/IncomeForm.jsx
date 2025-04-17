import React, { useState, useEffect } from 'react';
import { Container, Row, Col, Form, Button, Card, Spinner, Alert } from 'react-bootstrap';
import { instance } from '../../../axios/axiosConfig';

export const IncomeForm = ({ onIncomeAdded, editIncome = null }) => {
  const [categories, setCategories] = useState([]);
  const [accounts, setAccounts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(null);
  
  const [formData, setFormData] = useState({
    amount: '',
    description: '',
    date: new Date().toISOString().split('T')[0],
    accountId: '',
    incomeCategoryId: '',
    isRecurring: false,
    recurrencePeriod: 'MONTHLY'
  });

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        const [categoriesResponse, accountsResponse] = await Promise.all([
          instance.get('/income-categories'),
          instance.get('/accounts/all')
        ]);
        
        setCategories(categoriesResponse.data);
        setAccounts(accountsResponse.data);
        
        // If we're editing an existing income, populate the form
        if (editIncome) {
          setFormData({
            amount: editIncome.amount,
            description: editIncome.description || '',
            date: editIncome.date,
            accountId: editIncome.account.id,
            incomeCategoryId: editIncome.incomeCategory.incomeCategoryId,
            isRecurring: editIncome.isRecurring,
            recurrencePeriod: editIncome.recurrencePeriod || 'MONTHLY'
          });
        } else if (accountsResponse.data.length > 0) {
          // Set default account if available
          setFormData(prev => ({
            ...prev,
            accountId: accountsResponse.data[0].id
          }));
        }
        
        setLoading(false);
      } catch (err) {
        setError('Failed to load data. Please try again.');
        setLoading(false);
      }
    };
    
    fetchData();
  }, [editIncome]);

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    try {
      setLoading(true);
      
      const payload = {
        amount: parseFloat(formData.amount),
        description: formData.description,
        date: formData.date,
        account: { id: parseInt(formData.accountId) },
        incomeCategory: { incomeCategoryId: parseInt(formData.incomeCategoryId) },
        isRecurring: formData.isRecurring,
        recurrencePeriod: formData.isRecurring ? formData.recurrencePeriod : null
      };
      
      let response;
      if (editIncome) {
        response = await instance.put(`/incomes/${editIncome.incomeId}`, payload);
        setSuccess('Income updated successfully!');
      } else {
        response = await instance.post('/incomes', payload);
        setSuccess('Income added successfully!');
        
        // Reset form for new entry
        setFormData({
          amount: '',
          description: '',
          date: new Date().toISOString().split('T')[0],
          accountId: formData.accountId, // Keep the same account
          incomeCategoryId: formData.incomeCategoryId, // Keep the same category
          isRecurring: false,
          recurrencePeriod: 'MONTHLY'
        });
      }
      
      // Notify parent component
      if (onIncomeAdded) {
        onIncomeAdded(response.data);
      }
      
      // Clear success message after 3 seconds
      setTimeout(() => setSuccess(null), 3000);
    } catch (err) {
      setError('Failed to save income. Please check your data and try again.');
      // Clear error message after 3 seconds
      setTimeout(() => setError(null), 3000);
    } finally {
      setLoading(false);
    }
  };

  if (loading && (!categories.length || !accounts.length)) {
    return (
      <div className="d-flex justify-content-center my-5">
        <Spinner animation="border" />
      </div>
    );
  }

  return (
    <Card className="mb-4">
      <Card.Header as="h5">{editIncome ? 'Edit Income' : 'Add New Income'}</Card.Header>
      <Card.Body>
        {error && <Alert variant="danger">{error}</Alert>}
        {success && <Alert variant="success">{success}</Alert>}
        
        <Form onSubmit={handleSubmit}>
          <Row>
            <Col md={6}>
              <Form.Group className="mb-3">
                <Form.Label>Amount</Form.Label>
                <Form.Control
                  type="number"
                  name="amount"
                  value={formData.amount}
                  onChange={handleChange}
                  placeholder="Enter amount"
                  step="0.01"
                  min="0.01"
                  required
                />
              </Form.Group>
            </Col>
            
            <Col md={6}>
              <Form.Group className="mb-3">
                <Form.Label>Date</Form.Label>
                <Form.Control
                  type="date"
                  name="date"
                  value={formData.date}
                  onChange={handleChange}
                  required
                />
              </Form.Group>
            </Col>
          </Row>
          
          <Row>
            <Col md={6}>
              <Form.Group className="mb-3">
                <Form.Label>Account</Form.Label>
                <Form.Select
                  name="accountId"
                  value={formData.accountId}
                  onChange={handleChange}
                  required
                >
                  <option value="">Select Account</option>
                  {accounts.map(account => (
                    <option key={account.id} value={account.id}>
                      {account.name}
                    </option>
                  ))}
                </Form.Select>
              </Form.Group>
            </Col>
            
            <Col md={6}>
              <Form.Group className="mb-3">
                <Form.Label>Category</Form.Label>
                <Form.Select
                  name="incomeCategoryId"
                  value={formData.incomeCategoryId}
                  onChange={handleChange}
                  required
                >
                  <option value="">Select Category</option>
                  {categories.map(category => (
                    <option key={category.incomeCategoryId} value={category.incomeCategoryId}>
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
              placeholder="Enter description (optional)"
              rows={2}
            />
          </Form.Group>
          
          <Form.Group className="mb-3">
            <Form.Check
              type="checkbox"
              name="isRecurring"
              label="This is a recurring income"
              checked={formData.isRecurring}
              onChange={handleChange}
            />
          </Form.Group>
          
          {formData.isRecurring && (
            <Form.Group className="mb-3">
              <Form.Label>Recurrence Period</Form.Label>
              <Form.Select
                name="recurrencePeriod"
                value={formData.recurrencePeriod}
                onChange={handleChange}
                required={formData.isRecurring}
              >
                <option value="DAILY">Daily</option>
                <option value="WEEKLY">Weekly</option>
                <option value="BIWEEKLY">Bi-weekly</option>
                <option value="MONTHLY">Monthly</option>
                <option value="QUARTERLY">Quarterly</option>
                <option value="YEARLY">Yearly</option>
              </Form.Select>
            </Form.Group>
          )}
          
          <div className="d-grid gap-2">
            <Button variant="primary" type="submit" disabled={loading}>
              {loading ? <Spinner animation="border" size="sm" /> : (editIncome ? 'Update Income' : 'Add Income')}
            </Button>
          </div>
        </Form>
      </Card.Body>
    </Card>
  );
};

export default IncomeForm;
