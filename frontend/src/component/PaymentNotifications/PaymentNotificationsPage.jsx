import React, { useState } from 'react';
import { Container, Tabs, Tab } from 'react-bootstrap';
import PaymentDashboard from './PaymentDashboard';
import NotificationCenter from './NotificationCenter';
import PaymentCalendar from './PaymentCalendar';

const PaymentNotificationsPage = () => {
  const [activeTab, setActiveTab] = useState('dashboard');

  return (
    <Container fluid className="p-4">
      <h1 className="mb-4">Payment Notifications</h1>
      
      <Tabs
        id="payment-notifications-tabs"
        activeKey={activeTab}
        onSelect={(k) => setActiveTab(k)}
        className="mb-4"
      >
        <Tab eventKey="dashboard" title="Dashboard">
          <PaymentDashboard />
        </Tab>
        <Tab eventKey="notifications" title="Notifications">
          <NotificationCenter />
        </Tab>
        <Tab eventKey="calendar" title="Calendar">
          <PaymentCalendar />
        </Tab>
      </Tabs>
    </Container>
  );
};

export default PaymentNotificationsPage;
