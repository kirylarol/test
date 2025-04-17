import React from 'react';
import { Container, Tabs, Tab } from 'react-bootstrap';
import { SpendingLimitManager } from './SpendingLimitManager';
import { SpendingLimitDashboard } from './SpendingLimitDashboard';
import { NotificationCenter } from './NotificationCenter';

export const SpendingLimitsPage = () => {
  return (
    <Container className="mt-4">
      <h1>Spending Limits</h1>
      <Tabs defaultActiveKey="dashboard" className="mb-4">
        <Tab eventKey="dashboard" title="Dashboard">
          <SpendingLimitDashboard />
        </Tab>
        <Tab eventKey="manage" title="Manage Limits">
          <SpendingLimitManager />
        </Tab>
        <Tab eventKey="notifications" title="Notifications">
          <NotificationCenter />
        </Tab>
      </Tabs>
    </Container>
  );
};

export default SpendingLimitsPage;
