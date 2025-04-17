import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate, Link } from 'react-router-dom';
import { Navbar, Nav, Container } from 'react-bootstrap';
import FinancialManagementDashboard from './component/Dashboard/FinancialManagementDashboard';
import IntegratedDashboard from './component/Dashboard/IntegratedDashboard';
import SpendingLimitsPage from './component/SpendingLimits/SpendingLimitsPage';
import IncomePage from './component/Income/IncomePage';
import ComparisonPage from './component/Comparison/ComparisonPage';
import PaymentNotificationsPage from './component/PaymentNotifications/PaymentNotificationsPage';
import PaymentReminderForm from './component/PaymentNotifications/PaymentReminderForm';

const App = () => {
  return (
    <Router>
      <Navbar bg="dark" variant="dark" expand="lg">
        <Container>
          <Navbar.Brand as={Link} to="/">SpendSculptor</Navbar.Brand>
          <Navbar.Toggle aria-controls="basic-navbar-nav" />
          <Navbar.Collapse id="basic-navbar-nav">
            <Nav className="me-auto">
              <Nav.Link as={Link} to="/">Dashboard</Nav.Link>
              <Nav.Link as={Link} to="/features">Features</Nav.Link>
              <Nav.Link as={Link} to="/spending-limits">Spending Limits</Nav.Link>
              <Nav.Link as={Link} to="/income">Income Analysis</Nav.Link>
              <Nav.Link as={Link} to="/comparison">Period Comparison</Nav.Link>
              <Nav.Link as={Link} to="/payment-reminders">Payment Notifications</Nav.Link>
            </Nav>
          </Navbar.Collapse>
        </Container>
      </Navbar>

      <Routes>
        <Route path="/" element={<IntegratedDashboard />} />
        <Route path="/features" element={<FinancialManagementDashboard />} />
        <Route path="/spending-limits" element={<SpendingLimitsPage />} />
        <Route path="/income" element={<IncomePage />} />
        <Route path="/comparison" element={<ComparisonPage />} />
        <Route path="/payment-reminders" element={<PaymentNotificationsPage />} />
        <Route path="/payment-reminders/new" element={<PaymentReminderForm />} />
        <Route path="/payment-reminders/edit/:id" element={<PaymentReminderForm />} />
      </Routes>
    </Router>
  );
};

export default App;
