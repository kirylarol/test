import React, { useState, useEffect } from 'react';
import { Container, Row, Col, Card, Form, Button, Spinner, Alert } from 'react-bootstrap';
import { instance } from '../../../axios/axiosConfig';
import { format } from 'date-fns';

export const PeriodSelector = ({ onPeriodsSelected }) => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [predefinedPeriods, setPredefinedPeriods] = useState({});
  const [selectedPredefined, setSelectedPredefined] = useState('');
  
  const [customPeriods, setCustomPeriods] = useState({
    period1Start: '',
    period1End: '',
    period2Start: '',
    period2End: ''
  });

  useEffect(() => {
    fetchPredefinedPeriods();
  }, []);

  const fetchPredefinedPeriods = async () => {
    try {
      setLoading(true);
      const response = await instance.get('/comparison/predefined-periods');
      setPredefinedPeriods(response.data);
      setLoading(false);
    } catch (err) {
      setError('Failed to load predefined periods. Please try again.');
      setLoading(false);
    }
  };

  const handlePredefinedChange = (e) => {
    const selected = e.target.value;
    setSelectedPredefined(selected);
    
    if (selected && predefinedPeriods[selected]) {
      const periods = predefinedPeriods[selected];
      setCustomPeriods({
        period1Start: formatDateForInput(periods.period1Start),
        period1End: formatDateForInput(periods.period1End),
        period2Start: formatDateForInput(periods.period2Start),
        period2End: formatDateForInput(periods.period2End)
      });
    }
  };

  const handleCustomPeriodChange = (e) => {
    const { name, value } = e.target;
    setCustomPeriods(prev => ({
      ...prev,
      [name]: value
    }));
    
    // Clear predefined selection when manually changing dates
    setSelectedPredefined('');
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    
    // Validate dates
    if (!customPeriods.period1Start || !customPeriods.period1End || 
        !customPeriods.period2Start || !customPeriods.period2End) {
      setError('Please select all date fields.');
      return;
    }
    
    // Convert string dates to Date objects
    const periods = {
      period1Start: new Date(customPeriods.period1Start),
      period1End: new Date(customPeriods.period1End),
      period2Start: new Date(customPeriods.period2Start),
      period2End: new Date(customPeriods.period2End)
    };
    
    // Validate date ranges
    if (periods.period1End < periods.period1Start || periods.period2End < periods.period2Start) {
      setError('End date cannot be before start date.');
      return;
    }
    
    // Pass selected periods to parent component
    onPeriodsSelected({
      period1Start: customPeriods.period1Start,
      period1End: customPeriods.period1End,
      period2Start: customPeriods.period2Start,
      period2End: customPeriods.period2End
    });
    
    // Clear any error
    setError(null);
  };

  // Helper function to format date for input field
  const formatDateForInput = (dateString) => {
    const date = new Date(dateString);
    return date.toISOString().split('T')[0];
  };

  // Helper function to format date for display
  const formatDateForDisplay = (dateString) => {
    if (!dateString) return '';
    const date = new Date(dateString);
    return format(date, 'MMM d, yyyy');
  };

  return (
    <Card className="mb-4">
      <Card.Header>
        <h5>Select Periods to Compare</h5>
      </Card.Header>
      <Card.Body>
        {error && <Alert variant="danger">{error}</Alert>}
        
        <Form onSubmit={handleSubmit}>
          <Form.Group className="mb-3">
            <Form.Label>Quick Selection</Form.Label>
            <Form.Select 
              value={selectedPredefined}
              onChange={handlePredefinedChange}
              disabled={loading}
            >
              <option value="">Custom Selection</option>
              <option value="currentVsPreviousMonth">Current Month vs Previous Month</option>
              <option value="currentVsSameMonthLastYear">Current Month vs Same Month Last Year</option>
              <option value="currentVsPreviousQuarter">Current Quarter vs Previous Quarter</option>
              <option value="currentVsPreviousYear">Current Year vs Previous Year</option>
            </Form.Select>
          </Form.Group>
          
          <Row className="mb-3">
            <Col md={6}>
              <Card>
                <Card.Header>Period 1</Card.Header>
                <Card.Body>
                  <Form.Group className="mb-3">
                    <Form.Label>Start Date</Form.Label>
                    <Form.Control
                      type="date"
                      name="period1Start"
                      value={customPeriods.period1Start}
                      onChange={handleCustomPeriodChange}
                      required
                    />
                  </Form.Group>
                  
                  <Form.Group className="mb-3">
                    <Form.Label>End Date</Form.Label>
                    <Form.Control
                      type="date"
                      name="period1End"
                      value={customPeriods.period1End}
                      onChange={handleCustomPeriodChange}
                      required
                    />
                  </Form.Group>
                  
                  {customPeriods.period1Start && customPeriods.period1End && (
                    <div className="text-muted">
                      {formatDateForDisplay(customPeriods.period1Start)} - {formatDateForDisplay(customPeriods.period1End)}
                    </div>
                  )}
                </Card.Body>
              </Card>
            </Col>
            
            <Col md={6}>
              <Card>
                <Card.Header>Period 2</Card.Header>
                <Card.Body>
                  <Form.Group className="mb-3">
                    <Form.Label>Start Date</Form.Label>
                    <Form.Control
                      type="date"
                      name="period2Start"
                      value={customPeriods.period2Start}
                      onChange={handleCustomPeriodChange}
                      required
                    />
                  </Form.Group>
                  
                  <Form.Group className="mb-3">
                    <Form.Label>End Date</Form.Label>
                    <Form.Control
                      type="date"
                      name="period2End"
                      value={customPeriods.period2End}
                      onChange={handleCustomPeriodChange}
                      required
                    />
                  </Form.Group>
                  
                  {customPeriods.period2Start && customPeriods.period2End && (
                    <div className="text-muted">
                      {formatDateForDisplay(customPeriods.period2Start)} - {formatDateForDisplay(customPeriods.period2End)}
                    </div>
                  )}
                </Card.Body>
              </Card>
            </Col>
          </Row>
          
          <div className="d-grid">
            <Button variant="primary" type="submit" disabled={loading}>
              {loading ? <Spinner animation="border" size="sm" /> : 'Compare Periods'}
            </Button>
          </div>
        </Form>
      </Card.Body>
    </Card>
  );
};

export default PeriodSelector;
