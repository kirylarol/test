import React, { useState } from 'react';
import { Container, Row, Col, Nav, Tab } from 'react-bootstrap';
import PeriodSelector from './PeriodSelector';
import ExpenseComparison from './ExpenseComparison';
import IncomeComparison from './IncomeComparison';
import NetIncomeComparison from './NetIncomeComparison';

export const ComparisonPage = () => {
  const [activeTab, setActiveTab] = useState('expenses');
  const [selectedPeriods, setSelectedPeriods] = useState(null);

  const handlePeriodsSelected = (periods) => {
    setSelectedPeriods(periods);
  };

  return (
    <Container fluid className="p-4">
      <h1 className="mb-4">Period Comparison</h1>
      
      <PeriodSelector onPeriodsSelected={handlePeriodsSelected} />
      
      <Tab.Container id="comparison-tabs" activeKey={activeTab} onSelect={(k) => setActiveTab(k)}>
        <Row>
          <Col sm={12}>
            <Nav variant="tabs" className="mb-4">
              <Nav.Item>
                <Nav.Link eventKey="expenses">Expense Comparison</Nav.Link>
              </Nav.Item>
              <Nav.Item>
                <Nav.Link eventKey="income">Income Comparison</Nav.Link>
              </Nav.Item>
              <Nav.Item>
                <Nav.Link eventKey="netIncome">Net Income Comparison</Nav.Link>
              </Nav.Item>
            </Nav>
          </Col>
        </Row>
        
        <Row>
          <Col sm={12}>
            <Tab.Content>
              <Tab.Pane eventKey="expenses">
                <ExpenseComparison periods={selectedPeriods} />
              </Tab.Pane>
              <Tab.Pane eventKey="income">
                <IncomeComparison periods={selectedPeriods} />
              </Tab.Pane>
              <Tab.Pane eventKey="netIncome">
                <NetIncomeComparison periods={selectedPeriods} />
              </Tab.Pane>
            </Tab.Content>
          </Col>
        </Row>
      </Tab.Container>
    </Container>
  );
};

export default ComparisonPage;
