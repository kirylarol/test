import React, { useState } from 'react';
import { Container, Row, Col, Nav, Tab } from 'react-bootstrap';
import IncomeForm from './IncomeForm';
import IncomeList from './IncomeList';
import IncomeCategoryManager from './IncomeCategoryManager';
import IncomeAnalytics from './IncomeAnalytics';
import IncomeVsExpense from './IncomeVsExpense';

export const IncomePage = () => {
  const [activeTab, setActiveTab] = useState('list');

  return (
    <Container fluid className="p-4">
      <h1 className="mb-4">Income Management</h1>
      
      <Tab.Container id="income-tabs" activeKey={activeTab} onSelect={(k) => setActiveTab(k)}>
        <Row>
          <Col sm={12}>
            <Nav variant="tabs" className="mb-4">
              <Nav.Item>
                <Nav.Link eventKey="list">Income Entries</Nav.Link>
              </Nav.Item>
              <Nav.Item>
                <Nav.Link eventKey="add">Add Income</Nav.Link>
              </Nav.Item>
              <Nav.Item>
                <Nav.Link eventKey="categories">Categories</Nav.Link>
              </Nav.Item>
              <Nav.Item>
                <Nav.Link eventKey="analytics">Analytics</Nav.Link>
              </Nav.Item>
              <Nav.Item>
                <Nav.Link eventKey="comparison">Income vs Expenses</Nav.Link>
              </Nav.Item>
            </Nav>
          </Col>
        </Row>
        
        <Row>
          <Col sm={12}>
            <Tab.Content>
              <Tab.Pane eventKey="list">
                <IncomeList />
              </Tab.Pane>
              <Tab.Pane eventKey="add">
                <div className="p-3">
                  <h2 className="mb-4">Add New Income</h2>
                  <IncomeForm onIncomeAdded={() => setActiveTab('list')} />
                </div>
              </Tab.Pane>
              <Tab.Pane eventKey="categories">
                <IncomeCategoryManager />
              </Tab.Pane>
              <Tab.Pane eventKey="analytics">
                <IncomeAnalytics />
              </Tab.Pane>
              <Tab.Pane eventKey="comparison">
                <IncomeVsExpense />
              </Tab.Pane>
            </Tab.Content>
          </Col>
        </Row>
      </Tab.Container>
    </Container>
  );
};

export default IncomePage;
