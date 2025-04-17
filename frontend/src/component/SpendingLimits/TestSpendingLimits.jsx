import React from 'react';
import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';
import { Nav } from 'react-bootstrap';
import SpendingLimitsPage from './component/SpendingLimits/SpendingLimitsPage';

// This is a test component to verify the SpendingLimits feature works correctly
const TestSpendingLimits = () => {
  return (
    <Router>
      <div>
        <Nav className="mb-3">
          <Nav.Item>
            <Nav.Link as={Link} to="/">Home</Nav.Link>
          </Nav.Item>
          <Nav.Item>
            <Nav.Link as={Link} to="/spending-limits">Spending Limits</Nav.Link>
          </Nav.Item>
        </Nav>
        
        <Routes>
          <Route path="/spending-limits" element={<SpendingLimitsPage />} />
          <Route path="/" element={<h1>Home Page</h1>} />
        </Routes>
      </div>
    </Router>
  );
};

export default TestSpendingLimits;
