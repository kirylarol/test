import React, { useState, useEffect } from 'react';
import { Container, Row, Col, Card, Spinner, Alert, Table } from 'react-bootstrap';
import { instance } from '../../../axios/axiosConfig';
import { Bar } from 'react-chartjs-2';
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend
} from 'chart.js';

// Register ChartJS components
ChartJS.register(
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend
);

export const IncomeComparison = ({ periods }) => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [comparisonData, setComparisonData] = useState(null);
  const [categoryComparisonData, setCategoryComparisonData] = useState(null);

  useEffect(() => {
    if (periods) {
      fetchComparisonData();
      fetchCategoryComparisonData();
    }
  }, [periods]);

  const fetchComparisonData = async () => {
    try {
      setLoading(true);
      const response = await instance.get('/comparison/income', {
        params: periods
      });
      setComparisonData(response.data);
      setLoading(false);
    } catch (err) {
      setError('Failed to load income comparison data. Please try again.');
      setLoading(false);
    }
  };

  const fetchCategoryComparisonData = async () => {
    try {
      setLoading(true);
      const response = await instance.get('/comparison/income-by-category', {
        params: periods
      });
      setCategoryComparisonData(response.data);
      setLoading(false);
    } catch (err) {
      setError('Failed to load category comparison data. Please try again.');
      setLoading(false);
    }
  };

  // Format currency values
  const formatCurrency = (value) => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD'
    }).format(value);
  };

  // Format percentage values
  const formatPercentage = (value) => {
    return `${value > 0 ? '+' : ''}${value.toFixed(2)}%`;
  };

  // Format date for display
  const formatDate = (dateString) => {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric'
    });
  };

  // Prepare data for the comparison bar chart
  const prepareComparisonChartData = () => {
    if (!comparisonData) return null;

    return {
      labels: ['Period 1', 'Period 2'],
      datasets: [
        {
          label: 'Total Income',
          data: [
            comparisonData.period1.amount,
            comparisonData.period2.amount
          ],
          backgroundColor: [
            'rgba(75, 192, 192, 0.6)',
            'rgba(54, 162, 235, 0.6)'
          ],
          borderColor: [
            'rgba(75, 192, 192, 1)',
            'rgba(54, 162, 235, 1)'
          ],
          borderWidth: 1
        }
      ]
    };
  };

  // Prepare data for the category comparison chart
  const prepareCategoryChartData = () => {
    if (!categoryComparisonData || !categoryComparisonData.categoryComparisons) return null;

    const categories = Object.keys(categoryComparisonData.categoryComparisons);
    const period1Data = [];
    const period2Data = [];

    categories.forEach(category => {
      const comparison = categoryComparisonData.categoryComparisons[category];
      period1Data.push(comparison.period1Amount);
      period2Data.push(comparison.period2Amount);
    });

    return {
      labels: categories,
      datasets: [
        {
          label: 'Period 1',
          data: period1Data,
          backgroundColor: 'rgba(75, 192, 192, 0.6)',
          borderColor: 'rgba(75, 192, 192, 1)',
          borderWidth: 1
        },
        {
          label: 'Period 2',
          data: period2Data,
          backgroundColor: 'rgba(54, 162, 235, 0.6)',
          borderColor: 'rgba(54, 162, 235, 1)',
          borderWidth: 1
        }
      ]
    };
  };

  return (
    <Container>
      <h2 className="my-4">Income Comparison</h2>
      
      {error && <Alert variant="danger">{error}</Alert>}
      
      {loading ? (
        <div className="d-flex justify-content-center my-5">
          <Spinner animation="border" />
        </div>
      ) : (
        <>
          {comparisonData && (
            <>
              <Row className="mb-4">
                <Col md={12}>
                  <Card>
                    <Card.Header>
                      <h5>Total Income Comparison</h5>
                    </Card.Header>
                    <Card.Body>
                      <Row>
                        <Col md={4}>
                          <Card className="text-center mb-3">
                            <Card.Header>Period 1</Card.Header>
                            <Card.Body>
                              <h3 className="text-success">{formatCurrency(comparisonData.period1.amount)}</h3>
                              <p className="text-muted">
                                {formatDate(comparisonData.period1.startDate)} - {formatDate(comparisonData.period1.endDate)}
                              </p>
                              <p className="text-muted">
                                {comparisonData.period1.durationDays} days
                              </p>
                            </Card.Body>
                          </Card>
                        </Col>
                        
                        <Col md={4}>
                          <Card className="text-center mb-3">
                            <Card.Header>Change</Card.Header>
                            <Card.Body>
                              <h3 className={comparisonData.increased ? 'text-success' : 'text-danger'}>
                                {comparisonData.increased ? '+' : ''}{formatCurrency(comparisonData.difference)}
                              </h3>
                              <p className={comparisonData.increased ? 'text-success' : 'text-danger'}>
                                {formatPercentage(comparisonData.percentageChange)}
                              </p>
                              <p className="text-muted">
                                {comparisonData.increased ? 'Income increased' : 'Income decreased'}
                              </p>
                            </Card.Body>
                          </Card>
                        </Col>
                        
                        <Col md={4}>
                          <Card className="text-center mb-3">
                            <Card.Header>Period 2</Card.Header>
                            <Card.Body>
                              <h3 className="text-success">{formatCurrency(comparisonData.period2.amount)}</h3>
                              <p className="text-muted">
                                {formatDate(comparisonData.period2.startDate)} - {formatDate(comparisonData.period2.endDate)}
                              </p>
                              <p className="text-muted">
                                {comparisonData.period2.durationDays} days
                              </p>
                            </Card.Body>
                          </Card>
                        </Col>
                      </Row>
                      
                      <div className="mt-4">
                        <Bar 
                          data={prepareComparisonChartData()} 
                          options={{
                            responsive: true,
                            plugins: {
                              legend: {
                                position: 'top',
                              },
                              title: {
                                display: true,
                                text: 'Total Income Comparison'
                              }
                            }
                          }}
                        />
                      </div>
                    </Card.Body>
                  </Card>
                </Col>
              </Row>
            </>
          )}
          
          {categoryComparisonData && categoryComparisonData.categoryComparisons && (
            <Row className="mb-4">
              <Col md={12}>
                <Card>
                  <Card.Header>
                    <h5>Income by Category</h5>
                  </Card.Header>
                  <Card.Body>
                    <div className="mb-4">
                      <Bar 
                        data={prepareCategoryChartData()} 
                        options={{
                          responsive: true,
                          plugins: {
                            legend: {
                              position: 'top',
                            },
                            title: {
                              display: true,
                              text: 'Income by Category Comparison'
                            }
                          }
                        }}
                      />
                    </div>
                    
                    <Table striped bordered hover responsive>
                      <thead>
                        <tr>
                          <th>Category</th>
                          <th>Period 1</th>
                          <th>Period 2</th>
                          <th>Change</th>
                          <th>% Change</th>
                        </tr>
                      </thead>
                      <tbody>
                        {Object.entries(categoryComparisonData.categoryComparisons).map(([category, data]) => (
                          <tr key={category}>
                            <td>{category}</td>
                            <td>{formatCurrency(data.period1Amount)}</td>
                            <td>{formatCurrency(data.period2Amount)}</td>
                            <td className={data.increased ? 'text-success' : 'text-danger'}>
                              {data.increased ? '+' : ''}{formatCurrency(data.difference)}
                            </td>
                            <td className={data.increased ? 'text-success' : 'text-danger'}>
                              {formatPercentage(data.percentageChange)}
                            </td>
                          </tr>
                        ))}
                      </tbody>
                    </Table>
                  </Card.Body>
                </Card>
              </Col>
            </Row>
          )}
          
          {!comparisonData && !loading && (
            <Alert variant="info">
              Select periods to compare income.
            </Alert>
          )}
        </>
      )}
    </Container>
  );
};

export default IncomeComparison;
