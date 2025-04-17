import React, { useState, useEffect } from 'react';
import { Container, Row, Col, Card, Spinner, Alert, Form } from 'react-bootstrap';
import { instance } from '../../../axios/axiosConfig';
import { Bar, Pie, Line } from 'react-chartjs-2';
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  BarElement,
  PointElement,
  LineElement,
  ArcElement,
  Title,
  Tooltip,
  Legend,
} from 'chart.js';

// Register ChartJS components
ChartJS.register(
  CategoryScale,
  LinearScale,
  BarElement,
  PointElement,
  LineElement,
  ArcElement,
  Title,
  Tooltip,
  Legend
);

export const IncomeAnalytics = () => {
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [currentMonthIncome, setCurrentMonthIncome] = useState(null);
  const [incomeByCategory, setIncomeByCategory] = useState({});
  const [monthlyIncome, setMonthlyIncome] = useState({});
  const [dateRange, setDateRange] = useState({
    startDate: new Date(new Date().getFullYear(), new Date().getMonth(), 1).toISOString().split('T')[0],
    endDate: new Date(new Date().getFullYear(), new Date().getMonth() + 1, 0).toISOString().split('T')[0]
  });

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      setLoading(true);
      
      // Fetch current month income
      const currentMonthResponse = await instance.get('/incomes/analysis/current-month');
      setCurrentMonthIncome(currentMonthResponse.data);
      
      // Fetch income by category
      const categoryResponse = await instance.get(`/incomes/analysis/by-category?startDate=${dateRange.startDate}&endDate=${dateRange.endDate}`);
      setIncomeByCategory(categoryResponse.data);
      
      // Fetch monthly income
      const monthlyResponse = await instance.get('/incomes/analysis/monthly');
      setMonthlyIncome(monthlyResponse.data);
      
      setLoading(false);
    } catch (err) {
      setError('Failed to load income analytics data. Please try again.');
      setLoading(false);
    }
  };

  const handleDateRangeChange = (e) => {
    const { name, value } = e.target;
    setDateRange(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleApplyDateRange = () => {
    fetchData();
  };

  // Prepare data for pie chart
  const preparePieChartData = () => {
    if (!incomeByCategory.categoryTotals) return null;
    
    const labels = Object.keys(incomeByCategory.categoryTotals);
    const data = Object.values(incomeByCategory.categoryTotals).map(value => parseFloat(value));
    
    // Generate random colors for each category
    const backgroundColors = labels.map(() => 
      `rgba(${Math.floor(Math.random() * 255)}, ${Math.floor(Math.random() * 255)}, ${Math.floor(Math.random() * 255)}, 0.6)`
    );
    
    return {
      labels,
      datasets: [
        {
          data,
          backgroundColor: backgroundColors,
          borderWidth: 1
        }
      ]
    };
  };

  // Prepare data for monthly income line chart
  const prepareLineChartData = () => {
    if (!monthlyIncome) return null;
    
    // Sort months chronologically
    const sortedMonths = Object.keys(monthlyIncome).sort();
    const data = sortedMonths.map(month => parseFloat(monthlyIncome[month]));
    
    // Format month labels to be more readable
    const labels = sortedMonths.map(month => {
      const [year, monthNum] = month.split('-');
      return `${monthNum}/${year.slice(2)}`;
    });
    
    return {
      labels,
      datasets: [
        {
          label: 'Monthly Income',
          data,
          fill: false,
          borderColor: 'rgb(75, 192, 192)',
          tension: 0.1
        }
      ]
    };
  };

  return (
    <Container>
      <h2 className="my-4">Income Analytics</h2>
      
      {error && <Alert variant="danger">{error}</Alert>}
      
      <Card className="mb-4">
        <Card.Header>
          <h5>Date Range Filter</h5>
        </Card.Header>
        <Card.Body>
          <Row>
            <Col md={5}>
              <Form.Group className="mb-3">
                <Form.Label>Start Date</Form.Label>
                <Form.Control
                  type="date"
                  name="startDate"
                  value={dateRange.startDate}
                  onChange={handleDateRangeChange}
                />
              </Form.Group>
            </Col>
            <Col md={5}>
              <Form.Group className="mb-3">
                <Form.Label>End Date</Form.Label>
                <Form.Control
                  type="date"
                  name="endDate"
                  value={dateRange.endDate}
                  onChange={handleDateRangeChange}
                />
              </Form.Group>
            </Col>
            <Col md={2} className="d-flex align-items-end">
              <button 
                className="btn btn-primary mb-3 w-100" 
                onClick={handleApplyDateRange}
              >
                Apply
              </button>
            </Col>
          </Row>
        </Card.Body>
      </Card>
      
      {loading ? (
        <div className="d-flex justify-content-center my-5">
          <Spinner animation="border" />
        </div>
      ) : (
        <>
          <Row>
            <Col md={4}>
              <Card className="mb-4">
                <Card.Header>
                  <h5>Current Month Income</h5>
                </Card.Header>
                <Card.Body className="text-center">
                  <h2 className="text-primary">
                    ${currentMonthIncome?.totalIncome.toFixed(2) || '0.00'}
                  </h2>
                  <p className="text-muted">
                    {currentMonthIncome?.month || 'Current Month'}
                  </p>
                </Card.Body>
              </Card>
            </Col>
            
            <Col md={8}>
              <Card className="mb-4">
                <Card.Header>
                  <h5>Income Trend</h5>
                </Card.Header>
                <Card.Body>
                  {prepareLineChartData() ? (
                    <Line 
                      data={prepareLineChartData()} 
                      options={{
                        responsive: true,
                        plugins: {
                          legend: {
                            position: 'top',
                          },
                          title: {
                            display: true,
                            text: 'Monthly Income Trend'
                          }
                        }
                      }}
                    />
                  ) : (
                    <Alert variant="info">No monthly income data available.</Alert>
                  )}
                </Card.Body>
              </Card>
            </Col>
          </Row>
          
          <Row>
            <Col md={6}>
              <Card className="mb-4">
                <Card.Header>
                  <h5>Income by Category</h5>
                  <small className="text-muted">
                    {incomeByCategory.startDate && incomeByCategory.endDate ? 
                      `${new Date(incomeByCategory.startDate).toLocaleDateString()} - ${new Date(incomeByCategory.endDate).toLocaleDateString()}` : 
                      'Selected Period'}
                  </small>
                </Card.Header>
                <Card.Body>
                  {preparePieChartData() ? (
                    <Pie 
                      data={preparePieChartData()} 
                      options={{
                        responsive: true,
                        plugins: {
                          legend: {
                            position: 'right',
                          },
                          title: {
                            display: true,
                            text: 'Income Distribution by Category'
                          }
                        }
                      }}
                    />
                  ) : (
                    <Alert variant="info">No category data available for the selected period.</Alert>
                  )}
                </Card.Body>
              </Card>
            </Col>
            
            <Col md={6}>
              <Card className="mb-4">
                <Card.Header>
                  <h5>Income by Category (Table)</h5>
                </Card.Header>
                <Card.Body>
                  {incomeByCategory.categoryTotals && Object.keys(incomeByCategory.categoryTotals).length > 0 ? (
                    <table className="table table-striped">
                      <thead>
                        <tr>
                          <th>Category</th>
                          <th className="text-end">Amount</th>
                          <th className="text-end">Percentage</th>
                        </tr>
                      </thead>
                      <tbody>
                        {Object.entries(incomeByCategory.categoryTotals).map(([category, amount]) => {
                          const totalAmount = Object.values(incomeByCategory.categoryTotals)
                            .reduce((sum, val) => sum + parseFloat(val), 0);
                          const percentage = (parseFloat(amount) / totalAmount * 100).toFixed(1);
                          
                          return (
                            <tr key={category}>
                              <td>{category}</td>
                              <td className="text-end">${parseFloat(amount).toFixed(2)}</td>
                              <td className="text-end">{percentage}%</td>
                            </tr>
                          );
                        })}
                        <tr className="table-active fw-bold">
                          <td>Total</td>
                          <td className="text-end">
                            ${Object.values(incomeByCategory.categoryTotals)
                              .reduce((sum, val) => sum + parseFloat(val), 0)
                              .toFixed(2)}
                          </td>
                          <td className="text-end">100%</td>
                        </tr>
                      </tbody>
                    </table>
                  ) : (
                    <Alert variant="info">No category data available for the selected period.</Alert>
                  )}
                </Card.Body>
              </Card>
            </Col>
          </Row>
        </>
      )}
    </Container>
  );
};

export default IncomeAnalytics;
