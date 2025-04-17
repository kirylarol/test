import React from 'react';
import { Container, Tabs, Tab } from 'react-bootstrap';
import SpendingLimitsPage from '../SpendingLimits/SpendingLimitsPage';
import IncomePage from '../Income/IncomePage';
import ComparisonPage from '../Comparison/ComparisonPage';
import PaymentNotificationsPage from '../PaymentNotifications/PaymentNotificationsPage';

const FinancialManagementDashboard = () => {
  const [activeTab, setActiveTab] = React.useState('spending-limits');

  return (
    <Container fluid className="p-4">
      <h1 className="mb-4">Financial Management Dashboard</h1>
      
      <Tabs
        id="financial-management-tabs"
        activeKey={activeTab}
        onSelect={(k) => setActiveTab(k)}
        className="mb-4"
      >
        <Tab eventKey="spending-limits" title="Spending Limits">
          <SpendingLimitsPage />
        </Tab>
        <Tab eventKey="income" title="Income Analysis">
          <IncomePage />
        </Tab>
        <Tab eventKey="comparison" title="Period Comparison">
          <ComparisonPage />
        </Tab>
        <Tab eventKey="payments" title="Payment Notifications">
          <PaymentNotificationsPage />
        </Tab>
      </Tabs>
    </Container>
  );
};

export default FinancialManagementDashboard;
