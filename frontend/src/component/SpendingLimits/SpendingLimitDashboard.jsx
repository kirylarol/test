import React, { useState, useEffect } from 'react';
import { Container, Row, Col, Card, ProgressBar, Spinner, Alert } from 'react-bootstrap';
import { instance } from '../../../axios/axiosConfig';

export const SpendingLimitDashboard = () => {
  const [categories, setCategories] = useState([]);
  const [categorySpending, setCategorySpending] = useState({});
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchCategories();
  }, []);

  const fetchCategories = async () => {
    try {
      setLoading(true);
      const response = await instance.get('/categories/all');
      setCategories(response.data);
      
      // Fetch spending data for each category with a limit
      const categoriesWithLimits = response.data.filter(cat => cat.spendingLimit);
      
      if (categoriesWithLimits.length > 0) {
        const spendingData = {};
        
        for (const category of categoriesWithLimits) {
          try {
            const spendingResponse = await instance.get(`/categories/${category.categoryId}/spending`);
            spendingData[category.categoryId] = spendingResponse.data;
          } catch (err) {
            console.error(`Failed to fetch spending for category ${category.categoryName}`, err);
          }
        }
        
        setCategorySpending(spendingData);
      }
      
      setLoading(false);
    } catch (err) {
      setError('Failed to load categories. Please try again later.');
      setLoading(false);
    }
  };

  const getProgressVariant = (percentage) => {
    if (percentage >= 100) return 'danger';
    if (percentage >= 80) return 'warning';
    return 'success';
  };

  if (loading && Object.keys(categorySpending).length === 0) {
    return (
      <Container className="mt-4">
        <div className="d-flex justify-content-center">
          <Spinner animation="border" />
        </div>
      </Container>
    );
  }

  const categoriesWithLimits = categories.filter(cat => cat.spendingLimit);

  return (
    <Container className="mt-4">
      <h2>Spending Limits Dashboard</h2>
      
      {error && <Alert variant="danger">{error}</Alert>}
      
      {categoriesWithLimits.length === 0 ? (
        <Alert variant="info">
          No spending limits have been set yet. Use the Spending Limits Manager to set limits for your categories.
        </Alert>
      ) : (
        <Row className="mt-4">
          {categoriesWithLimits.map(category => {
            const spendingData = categorySpending[category.categoryId];
            
            if (!spendingData) return null;
            
            const { currentMonthSpending, limit, percentage } = spendingData;
            const progressVariant = getProgressVariant(percentage);
            
            return (
              <Col md={6} lg={4} className="mb-4" key={category.categoryId}>
                <Card>
                  <Card.Header>{category.categoryName}</Card.Header>
                  <Card.Body>
                    <Card.Title>
                      {currentMonthSpending.toFixed(2)} / {limit.toFixed(2)}
                    </Card.Title>
                    <ProgressBar 
                      now={Math.min(percentage, 100)} 
                      variant={progressVariant} 
                      label={`${percentage.toFixed(0)}%`}
                    />
                    <Card.Text className="mt-2">
                      {percentage >= 100 ? (
                        <span className="text-danger">You've exceeded your spending limit!</span>
                      ) : percentage >= category.notificationThreshold ? (
                        <span className="text-warning">Approaching your spending limit!</span>
                      ) : (
                        <span className="text-success">Within budget</span>
                      )}
                    </Card.Text>
                  </Card.Body>
                </Card>
              </Col>
            );
          })}
        </Row>
      )}
    </Container>
  );
};

export default SpendingLimitDashboard;
