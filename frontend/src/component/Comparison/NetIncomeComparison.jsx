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

export const NetIncomeComparison = ({ periods }) => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [comparisonData, setComparisonData] = useState(null);

  useEffect(() => {
    if (periods) {
      fetchComparisonData();
    }
  }, [periods]);

  const fetchComparisonData = async () => {
    try {
      setLoading(true);
      const response = await instance.get('/comparison/net-income', {
        params: periods
      });
      setComparisonData(response.data);
      setLoading(false);
    } catch (err) {
      setError('Failed to load net income comparison data. Please try again.');
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
          label: 'Income',
          data: [
            comparisonData.period1.income,
            comparisonData.period2.income
          ],
          backgroundColor: 'rgba(75, 192, 192, 0.6)',
          borderColor: 'rgba(75, 192, 192, 1)',
          borderWidth: 1
        },
        {
          label: 'Expenses',
          data: [
            comparisonData.period1.expenses,
            comparisonData.period2.expenses
          ],
          backgroundColor: 'rgba(255, 99, 132, 0.6)',
          borderColor: 'rgba(255, 99, 132, 1)',
          borderWidth: 1
        },
        {
          label: 'Net Income',
          data: [
            comparisonData.period1.amount,
            comparisonData.period2.amount
          ],
          backgroundColor: 'rgba(54, 162, 235, 0.6)',
          borderColor: 'rgba(54, 162, 235, 1)',
          borderWidth: 1
        }
      ]
    };
  };

  // Prepare data for the net income chart
  const prepareNetIncomeChartData = () => {
    if (!comparisonData) return null;

    return {
      labels: ['Period 1', 'Period 2'],
      datasets: [
        {
          label: 'Net Income',
          data: [
            comparisonData.period1.amount,
            comparisonData.period2.amount
          ],
          backgroundColor: [
            comparisonData.period1.amount >= 0 ? 'rgba(75, 192, 192, 0.6)' : 'rgba(255, 99, 132, 0.6)',
            comparisonData.period2.amount >= 0 ? 'rgba(75, 192, 192, 0.6)' : 'rgba(255, 99, 132, 0.6)'
          ],
          borderColor: [
            comparisonData.period1.amount >= 0 ? 'rgba(75, 192, 192, 1)' : 'rgba(255, 99, 132, 1)',
            comparisonData.period2.amount >= 0 ? 'rgba(75, 192, 192, 1)' : 'rgba(255, 99, 132, 1)'
          ],
          borderWidth: 1
        }
      ]
    };
  };

  return (
    <Container>
      <h2 className="my-4">Net Income Comparison</h2>
      
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
                      <h5>Net Income Comparison</h5>
                    </Card.Header>
                    <Card.Body>
                      <Row>
                        <Col md={4}>
                          <Card className="text-center mb-3">
                            <Card.Header>Period 1</Card.Header>
                            <Card.Body>
                              <h3 className={comparisonData.period1.amount >= 0 ? 'text-success' : 'text-danger'}>
                                {formatCurrency(comparisonData.period1.amount)}
                              </h3>
                              <p className="text-muted">
                                {formatDate(comparisonData.period1.startDate)} - {formatDate(comparisonData.period1.endDate)}
                              </p>
                              <div>
                                <p className="mb-1">Income: {formatCurrency(comparisonData.period1.income)}</p>
                                <p className="mb-1">Expenses: {formatCurrency(comparisonData.period1.expenses)}</p>
                              </div>
                            </Card.Body>
                          </Card>
                        </Col>
                        
                        <Col md={4}>
                          <Card className="text-center mb-3">
                            <Card.Header>Change</Card.Header>
                            <Card.Body>
                              <h3 className={comparisonData.improved ? 'text-success' : 'text-danger'}>
                                {comparisonData.improved ? '+' : ''}{formatCurrency(comparisonData.difference)}
                              </h3>
                              <p className={comparisonData.improved ? 'text-success' : 'text-danger'}>
                                {formatPercentage(comparisonData.percentageChange)}
                              </p>
                              <p className="text-muted">
                                {comparisonData.improved ? 'Financial situation improved' : 'Financial situation declined'}
                              </p>
                            </Card.Body>
                          </Card>
                        </Col>
                        
                        <Col md={4}>
                          <Card className="text-center mb-3">
                            <Card.Header>Period 2</Card.Header>
                            <Card.Body>
                              <h3 className={comparisonData.period2.amount >= 0 ? 'text-success' : 'text-danger'}>
                                {formatCurrency(comparisonData.period2.amount)}
                              </h3>
                              <p className="text-muted">
                                {formatDate(comparisonData.period2.startDate)} - {formatDate(comparisonData.period2.endDate)}
                              </p>
                              <div>
                                <p className="mb-1">Income: {formatCurrency(comparisonData.period2.income)}</p>
                                <p className="mb-1">Expenses: {formatCurrency(comparisonData.period2.expenses)}</p>
                              </div>
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
                                text: 'Income, Expenses, and Net Income Comparison'
                              }
                            }
                          }}
                        />
                      </div>
                    </Card.Body>
                  </Card>
                </Col>
              </Row>
              
              <Row className="mb-4">
                <Col md={12}>
                  <Card>
                    <Card.Header>
                      <h5>Net Income Change</h5>
                    </Card.Header>
                    <Card.Body>
                      <div className="mb-4">
                        <Bar 
                          data={prepareNetIncomeChartData()} 
                          options={{
                            responsive: true,
                            plugins: {
                              legend: {
                                position: 'top',
                              },
                              title: {
                                display: true,
                                text: 'Net Income Comparison'
                              }
                            }
                          }}
                        />
                      </div>
                      
                      <Table striped bordered hover responsive>
                        <thead>
                          <tr>
                            <th>Metric</th>
                            <th>Period 1</th>
                            <th>Period 2</th>
                            <th>Change</th>
                            <th>% Change</th>
                          </tr>
                        </thead>
                        <tbody>
                          <tr>
                            <td>Income</td>
                            <td>{formatCurrency(comparisonData.period1.income)}</td>
                            <td>{formatCurrency(comparisonData.period2.income)}</td>
                            <td className={comparisonData.period2.income >= comparisonData.period1.income ? 'text-success' : 'text-danger'}>
                              {comparisonData.period2.income >= comparisonData.period1.income ? '+' : ''}
                              {formatCurrency(comparisonData.period2.income - comparisonData.period1.income)}
                            </td>
                            <td className={comparisonData.period2.income >= comparisonData.period1.income ? 'text-success' : 'text-danger'}>
                              {formatPercentage(((comparisonData.period2.income - comparisonData.period1.income) / comparisonData.period1.income) * 100)}
                            </td>
                          </tr>
                          <tr>
                            <td>Expenses</td>
                            <td>{formatCurrency(comparisonData.period1.expenses)}</td>
                            <td>{formatCurrency(comparisonData.period2.expenses)}</td>
                            <td className={comparisonData.period2.expenses <= comparisonData.period1.expenses ? 'text-success' : 'text-danger'}>
                              {comparisonData.period2.expenses >= comparisonData.period1.expenses ? '+' : ''}
                              {formatCurrency(comparisonData.period2.expenses - comparisonData.period1.expenses)}
                            </td>
                            <td className={comparisonData.period2.expenses <= comparisonData.period1.expenses ? 'text-success' : 'text-danger'}>
                              {formatPercentage(((comparisonData.period2.expenses - comparisonData.period1.expenses) / comparisonData.period1.expenses) * 100)}
                            </td>
                          </tr>
                          <tr>
                            <td>Net Income</td>
                            <td className={comparisonData.period1.amount >= 0 ? 'text-success' : 'text-danger'}>
                              {formatCurrency(comparisonData.period1.amount)}
                            </td>
                            <td className={comparisonData.period2.amount >= 0 ? 'text-success' : 'text-danger'}>
                              {formatCurrency(comparisonData.period2.amount)}
                            </td>
                            <td className={comparisonData.improved ? 'text-success' : 'text-danger'}>
                              {comparisonData.improved ? '+' : ''}
                              {formatCurrency(comparisonData.difference)}
                            </td>
                            <td className={comparisonData.improved ? 'text-success' : 'text-danger'}>
                              {formatPercentage(comparisonData.percentageChange)}
                            </td>
                          </tr>
                        </tbody>
                      </Table>
                    </Card.Body>
                  </Card>
                </Col>
              </Row>
            </>
          )}
          
          {!comparisonData && !loading && (
            <Alert variant="info">
              Select periods to compare net income.
            </Alert>
          )}
        </>
      )}
    </Container>
  );
};

export default NetIncomeComparison;
