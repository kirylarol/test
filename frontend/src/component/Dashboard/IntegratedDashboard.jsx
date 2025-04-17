import React, { useState, useEffect } from 'react';
import { Container, Row, Col, Card, Alert } from 'react-bootstrap';
import { instance } from '../../axios/axiosConfig';
import { Link } from 'react-router-dom';
import { PieChart, Pie, Cell, BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';

const IntegratedDashboard = () => {
  const [spendingData, setSpendingData] = useState([]);
  const [incomeData, setIncomeData] = useState([]);
  const [paymentSummary, setPaymentSummary] = useState({ pending: 0, paid: 0, overdue: 0 });
  const [unreadNotifications, setUnreadNotifications] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchDashboardData();
  }, []);

  const fetchDashboardData = async () => {
    try {
      setLoading(true);
      
      // Fetch spending data
      const spendingResponse = await instance.get('/categories/spending');
      setSpendingData(spendingResponse.data);
      
      // Fetch income data
      const incomeResponse = await instance.get('/income/summary');
      setIncomeData(incomeResponse.data);
      
      // Fetch payment summary
      const paymentResponse = await instance.get('/dashboard/payment-summary');
      setPaymentSummary(paymentResponse.data);
      
      // Fetch notification count
      const notificationResponse = await instance.get('/notifications/count');
      setUnreadNotifications(notificationResponse.data.count);
      
      setLoading(false);
    } catch (err) {
      setError('Failed to load dashboard data. Please try again.');
      setLoading(false);
    }
  };

  // Colors for charts
  const COLORS = ['#0088FE', '#00C49F', '#FFBB28', '#FF8042', '#8884d8', '#82ca9d'];
  
  // Format currency
  const formatCurrency = (amount) => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD'
    }).format(amount);
  };

  return (
    <Container className="mt-4">
      <h2>Financial Overview</h2>
      
      {error && <Alert variant="danger">{error}</Alert>}
      
      {loading ? (
        <Alert variant="info">Loading dashboard data...</Alert>
      ) : (
        <>
          <Row className="mb-4">
            <Col md={6}>
              <Card className="h-100">
                <Card.Header>
                  <div className="d-flex justify-content-between align-items-center">
                    <h5 className="mb-0">Spending by Category</h5>
                    <Link to="/spending-limits" className="btn btn-sm btn-primary">View Details</Link>
                  </div>
                </Card.Header>
                <Card.Body>
                  {spendingData.length > 0 ? (
                    <ResponsiveContainer width="100%" height={300}>
                      <PieChart>
                        <Pie
                          data={spendingData}
                          cx="50%"
                          cy="50%"
                          labelLine={false}
                          outerRadius={80}
                          fill="#8884d8"
                          dataKey="amount"
                          nameKey="categoryName"
                          label={({ name, percent }) => `${name}: ${(percent * 100).toFixed(0)}%`}
                        >
                          {spendingData.map((entry, index) => (
                            <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                          ))}
                        </Pie>
                        <Tooltip formatter={(value) => formatCurrency(value)} />
                      </PieChart>
                    </ResponsiveContainer>
                  ) : (
                    <div className="text-center py-5">
                      <p>No spending data available</p>
                    </div>
                  )}
                </Card.Body>
              </Card>
            </Col>
            
            <Col md={6}>
              <Card className="h-100">
                <Card.Header>
                  <div className="d-flex justify-content-between align-items-center">
                    <h5 className="mb-0">Income vs. Expenses</h5>
                    <Link to="/income" className="btn btn-sm btn-primary">View Details</Link>
                  </div>
                </Card.Header>
                <Card.Body>
                  {incomeData.length > 0 ? (
                    <ResponsiveContainer width="100%" height={300}>
                      <BarChart data={incomeData}>
                        <CartesianGrid strokeDasharray="3 3" />
                        <XAxis dataKey="month" />
                        <YAxis />
                        <Tooltip formatter={(value) => formatCurrency(value)} />
                        <Legend />
                        <Bar dataKey="income" name="Income" fill="#82ca9d" />
                        <Bar dataKey="expenses" name="Expenses" fill="#8884d8" />
                      </BarChart>
                    </ResponsiveContainer>
                  ) : (
                    <div className="text-center py-5">
                      <p>No income data available</p>
                    </div>
                  )}
                </Card.Body>
              </Card>
            </Col>
          </Row>
          
          <Row className="mb-4">
            <Col md={6}>
              <Card className="h-100">
                <Card.Header>
                  <div className="d-flex justify-content-between align-items-center">
                    <h5 className="mb-0">Payment Reminders</h5>
                    <Link to="/payment-reminders" className="btn btn-sm btn-primary">View Details</Link>
                  </div>
                </Card.Header>
                <Card.Body>
                  <Row>
                    <Col xs={4}>
                      <div className="text-center">
                        <h3 className="text-warning">{paymentSummary.pending}</h3>
                        <p>Pending</p>
                      </div>
                    </Col>
                    <Col xs={4}>
                      <div className="text-center">
                        <h3 className="text-success">{paymentSummary.paid}</h3>
                        <p>Paid</p>
                      </div>
                    </Col>
                    <Col xs={4}>
                      <div className="text-center">
                        <h3 className="text-danger">{paymentSummary.overdue}</h3>
                        <p>Overdue</p>
                      </div>
                    </Col>
                  </Row>
                  
                  <div className="mt-3 text-center">
                    <Link to="/payment-reminders/new" className="btn btn-outline-primary">
                      Add New Payment
                    </Link>
                    
                    {unreadNotifications > 0 && (
                      <div className="mt-3 alert alert-info">
                        You have {unreadNotifications} unread notification{unreadNotifications !== 1 ? 's' : ''}
                      </div>
                    )}
                  </div>
                </Card.Body>
              </Card>
            </Col>
            
            <Col md={6}>
              <Card className="h-100">
                <Card.Header>
                  <div className="d-flex justify-content-between align-items-center">
                    <h5 className="mb-0">Period Comparison</h5>
                    <Link to="/comparison" className="btn btn-sm btn-primary">View Details</Link>
                  </div>
                </Card.Header>
                <Card.Body className="text-center">
                  <p>Compare your financial data across different time periods</p>
                  <div className="mt-4">
                    <Link to="/comparison" className="btn btn-outline-primary">
                      View Comparisons
                    </Link>
                  </div>
                  <div className="mt-4">
                    <p>Analyze trends in your spending and income over time</p>
                    <p>Identify patterns and make informed financial decisions</p>
                  </div>
                </Card.Body>
              </Card>
            </Col>
          </Row>
        </>
      )}
    </Container>
  );
};

export default IntegratedDashboard;
