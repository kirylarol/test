import React, { useState, useEffect } from 'react';
import { Container, Row, Col, Card, Form, Button, Alert, Spinner } from 'react-bootstrap';
import { instance } from '../../../axios/axiosConfig';

export const SpendingLimitManager = () => {
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [selectedCategory, setSelectedCategory] = useState(null);
  const [spendingLimit, setSpendingLimit] = useState('');
  const [notificationThreshold, setNotificationThreshold] = useState(80);
  const [successMessage, setSuccessMessage] = useState('');

  useEffect(() => {
    fetchCategories();
  }, []);

  const fetchCategories = async () => {
    try {
      setLoading(true);
      const response = await instance.get('/categories/all');
      setCategories(response.data);
      setLoading(false);
    } catch (err) {
      setError('Failed to load categories. Please try again later.');
      setLoading(false);
    }
  };

  const handleCategorySelect = (categoryId) => {
    const category = categories.find(cat => cat.categoryId === parseInt(categoryId));
    setSelectedCategory(category);
    setSpendingLimit(category.spendingLimit || '');
    setNotificationThreshold(category.notificationThreshold || 80);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!selectedCategory) return;

    try {
      setLoading(true);
      await instance.post(`/categories/${selectedCategory.categoryId}/limit?limit=${spendingLimit}&threshold=${notificationThreshold}`);
      setSuccessMessage(`Spending limit for ${selectedCategory.categoryName} has been set successfully!`);
      fetchCategories(); // Refresh categories to get updated data
      setTimeout(() => setSuccessMessage(''), 3000);
    } catch (err) {
      setError('Failed to set spending limit. Please try again.');
      setTimeout(() => setError(null), 3000);
    } finally {
      setLoading(false);
    }
  };

  if (loading && categories.length === 0) {
    return (
      <Container className="mt-4">
        <div className="d-flex justify-content-center">
          <Spinner animation="border" />
        </div>
      </Container>
    );
  }

  return (
    <Container className="mt-4">
      <h2>Spending Limits Management</h2>
      
      {error && <Alert variant="danger">{error}</Alert>}
      {successMessage && <Alert variant="success">{successMessage}</Alert>}
      
      <Row className="mt-4">
        <Col md={6}>
          <Card>
            <Card.Header>Set Category Spending Limit</Card.Header>
            <Card.Body>
              <Form onSubmit={handleSubmit}>
                <Form.Group className="mb-3">
                  <Form.Label>Select Category</Form.Label>
                  <Form.Select 
                    onChange={(e) => handleCategorySelect(e.target.value)}
                    required
                  >
                    <option value="">Choose a category...</option>
                    {categories.map(category => (
                      <option key={category.categoryId} value={category.categoryId}>
                        {category.categoryName}
                      </option>
                    ))}
                  </Form.Select>
                </Form.Group>
                
                {selectedCategory && (
                  <>
                    <Form.Group className="mb-3">
                      <Form.Label>Spending Limit</Form.Label>
                      <Form.Control 
                        type="number" 
                        min="0" 
                        step="0.01"
                        value={spendingLimit} 
                        onChange={(e) => setSpendingLimit(e.target.value)}
                        required
                      />
                      <Form.Text className="text-muted">
                        Set the maximum amount you want to spend in this category per month.
                      </Form.Text>
                    </Form.Group>
                    
                    <Form.Group className="mb-3">
                      <Form.Label>Notification Threshold (%)</Form.Label>
                      <Form.Control 
                        type="number" 
                        min="1" 
                        max="100"
                        value={notificationThreshold} 
                        onChange={(e) => setNotificationThreshold(e.target.value)}
                        required
                      />
                      <Form.Text className="text-muted">
                        You'll receive a notification when your spending reaches this percentage of the limit.
                      </Form.Text>
                    </Form.Group>
                    
                    <Button variant="primary" type="submit" disabled={loading}>
                      {loading ? <Spinner animation="border" size="sm" /> : 'Save Limit'}
                    </Button>
                  </>
                )}
              </Form>
            </Card.Body>
          </Card>
        </Col>
      </Row>
    </Container>
  );
};

export default SpendingLimitManager;
