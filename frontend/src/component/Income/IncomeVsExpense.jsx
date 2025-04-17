import React, { useState, useEffect } from 'react';
import { Container, Row, Col, Card, Spinner, Alert, Form } from 'react-bootstrap';
import { instance } from '../../../axios/axiosConfig';
import { Bar } from 'react-chartjs-2';
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend,
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

export const IncomeVsExpense = () => {
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [incomeData, setIncomeData] = useState({});
  const [expenseData, setExpenseData] = useState({});
  const [netIncome, setNetIncome] = useState({});
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
      
      // Fetch income data
      const incomeResponse = await instance.get(`/incomes/analysis/total?startDate=${dateRange.startDate}&endDate=${dateRange.endDate}`);
      setIncomeData(incomeResponse.data);
      
      // Fetch expense data (from receipts)
      // This assumes there's an endpoint to get total expenses for a date range
      const expenseResponse = await instance.get(`/receipts/analysis/total?startDate=${dateRange.startDate}&endDate=${dateRange.endDate}`);
      setExpenseData(expenseResponse.data);
      
      // Calculate net income
      const incomeAmount = parseFloat(incomeResponse.data.totalIncome || 0);
      const expenseAmount = parseFloat(expenseResponse.data.totalExpense || 0);
      const netAmount = incomeAmount - expenseAmount;
      
      setNetIncome({
        amount: netAmount,
        positive: netAmount >= 0
      });
      
      // Fetch monthly data for the chart
      const monthlyIncomeResponse = await instance.get('/incomes/analysis/monthly');
      const monthlyExpenseResponse = await instance.get('/receipts/analysis/monthly');
      
      // Process monthly data for the chart
      processMonthlyData(monthlyIncomeResponse.data, monthlyExpenseResponse.data);
      
      setLoading(false);
    } catch (err) {
      setError('Failed to load income vs expense data. Please try again.');
      setLoading(false);
    }
  };

  const processMonthlyData = (incomeByMonth, expenseByMonth) => {
    // This function would process the monthly income and expense data
    // to prepare it for the chart
    // For now, we'll use placeholder data
    
    // In a real implementation, you would merge the income and expense data
    // by month and calculate the net income for each month
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

  // Prepare data for the comparison bar chart
  const prepareComparisonChartData = () => {
    // For demonstration purposes, we'll use the last 6 months
    // In a real implementation, you would use the actual data from the API
    
    const months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun'];
    
    // Sample data - in a real implementation, this would come from the API
    const incomeValues = [4500, 4800, 5200, 4900, 5100, 5300];
    const expenseValues = [3800, 4200, 4500, 4300, 4600, 4100];
    
    return {
      labels: months,
      datasets: [
        {
          label: 'Income',
          data: incomeValues,
          backgroundColor: 'rgba(75, 192, 192, 0.6)',
          borderColor: 'rgba(75, 192, 192, 1)',
          borderWidth: 1
        },
        {
          label: 'Expenses',
          data: expenseValues,
          backgroundColor: 'rgba(255, 99, 132, 0.6)',
          borderColor: 'rgba(255, 99, 132, 1)',
          borderWidth: 1
        }
      ]
    };
  };

  // Prepare data for the net income bar chart
  const prepareNetIncomeChartData = () => {
    // For demonstration purposes, we'll use the last 6 months
    // In a real implementation, you would use the actual data from the API
    
    const months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun'];
    
    // Sample data - in a real implementation, this would be calculated from income and expense data
    const netIncomeValues = [700, 600, 700, 600, 500, 1200];
    
    return {
      labels: months,
      datasets: [
        {
          label: 'Net Income',
          data: netIncomeValues,
          backgroundColor: netIncomeValues.map(value => 
            value >= 0 ? 'rgba(75, 192, 192, 0.6)' : 'rgba(255, 99, 132, 0.6)'
          ),
          borderColor: netIncomeValues.map(value => 
            value >= 0 ? 'rgba(75, 192, 192, 1)' : 'rgba(255, 99, 132, 1)'
          ),
          borderWidth: 1
        }
      ]
    };
  };

  return (
    <Container>
      <h2 className="my-4">Income vs Expenses</h2>
      
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
              <Card className="mb-4 text-center">
                <Card.Header>
                  <h5>Total Income</h5>
                  <small className="text-muted">
                    {incomeData.startDate && incomeData.endDate ? 
                      `${new Date(incomeData.startDate).toLocaleDateString()} - ${new Date(incomeData.endDate).toLocaleDateString()}` : 
                      'Selected Period'}
                  </small>
                </Card.Header>
                <Card.Body>
                  <h2 className="text-success">
                    ${parseFloat(incomeData.totalIncome || 0).toFixed(2)}
                  </h2>
                </Card.Body>
              </Card>
            </Col>
            
            <Col md={4}>
              <Card className="mb-4 text-center">
                <Card.Header>
                  <h5>Total Expenses</h5>
                  <small className="text-muted">
                    {expenseData.startDate && expenseData.endDate ? 
                      `${new Date(expenseData.startDate).toLocaleDateString()} - ${new Date(expenseData.endDate).toLocaleDateString()}` : 
                      'Selected Period'}
                  </small>
                </Card.Header>
                <Card.Body>
                  <h2 className="text-danger">
                    ${parseFloat(expenseData.totalExpense || 0).toFixed(2)}
                  </h2>
                </Card.Body>
              </Card>
            </Col>
            
            <Col md={4}>
              <Card className="mb-4 text-center">
                <Card.Header>
                  <h5>Net Income</h5>
                  <small className="text-muted">Income - Expenses</small>
                </Card.Header>
                <Card.Body>
                  <h2 className={netIncome.positive ? 'text-success' : 'text-danger'}>
                    ${Math.abs(netIncome.amount || 0).toFixed(2)}
                    {!netIncome.positive && netIncome.amount !== 0 && ' (Deficit)'}
                  </h2>
                  <p className={netIncome.positive ? 'text-success' : 'text-danger'}>
                    {netIncome.positive ? 'You earned more than you spent!' : 'You spent more than you earned.'}
                  </p>
                </Card.Body>
              </Card>
            </Col>
          </Row>
          
          <Row>
            <Col md={12}>
              <Card className="mb-4">
                <Card.Header>
                  <h5>Income vs Expenses Comparison</h5>
                </Card.Header>
                <Card.Body>
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
                          text: 'Monthly Income vs Expenses'
                        }
                      }
                    }}
                  />
                </Card.Body>
              </Card>
            </Col>
          </Row>
          
          <Row>
            <Col md={12}>
              <Card className="mb-4">
                <Card.Header>
                  <h5>Net Income Trend</h5>
                </Card.Header>
                <Card.Body>
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
                          text: 'Monthly Net Income'
                        }
                      }
                    }}
                  />
                </Card.Body>
              </Card>
            </Col>
          </Row>
          
          <Row>
            <Col md={12}>
              <Card className="mb-4">
                <Card.Header>
                  <h5>Savings Rate</h5>
                </Card.Header>
                <Card.Body>
                  {incomeData.totalIncome && parseFloat(incomeData.totalIncome) > 0 ? (
                    <>
                      <div className="progress mb-3" style={{ height: '25px' }}>
                        <div 
                          className={`progress-bar ${netIncome.positive ? 'bg-success' : 'bg-danger'}`}
                          role="progressbar"
                          style={{ 
                            width: `${Math.min(Math.abs(netIncome.amount / parseFloat(incomeData.totalIncome) * 100), 100)}%` 
                          }}
                          aria-valuenow={Math.abs(netIncome.amount / parseFloat(incomeData.totalIncome) * 100)}
                          aria-valuemin="0"
                          aria-valuemax="100"
                        >
                          {netIncome.positive ? 
                            `${(netIncome.amount / parseFloat(incomeData.totalIncome) * 100).toFixed(1)}% Saved` : 
                            `${Math.abs(netIncome.amount / parseFloat(incomeData.totalIncome) * 100).toFixed(1)}% Overspent`}
                        </div>
                      </div>
                      <p className="text-muted">
                        {netIncome.positive ? 
                          `You saved ${(netIncome.amount / parseFloat(incomeData.totalIncome) * 100).toFixed(1)}% of your income during this period.` : 
                          `You overspent by ${Math.abs(netIncome.amount / parseFloat(incomeData.totalIncome) * 100).toFixed(1)}% of your income during this period.`}
                      </p>
                    </>
                  ) : (
                    <Alert variant="info">
                      No income data available for the selected period to calculate savings rate.
                    </Alert>
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

export default IncomeVsExpense;
