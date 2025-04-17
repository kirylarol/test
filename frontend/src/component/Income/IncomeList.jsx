import React, { useState, useEffect } from 'react';
import { Container, Row, Col, Table, Button, Card, Spinner, Alert, Form, Pagination } from 'react-bootstrap';
import { instance } from '../../../axios/axiosConfig';
import IncomeForm from './IncomeForm';

export const IncomeList = () => {
  const [incomes, setIncomes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [showAddForm, setShowAddForm] = useState(false);
  const [editIncome, setEditIncome] = useState(null);
  const [categories, setCategories] = useState([]);
  const [accounts, setAccounts] = useState([]);
  
  // Filtering and pagination
  const [filters, setFilters] = useState({
    startDate: '',
    endDate: '',
    categoryId: '',
    accountId: ''
  });
  const [currentPage, setCurrentPage] = useState(1);
  const [itemsPerPage] = useState(10);

  useEffect(() => {
    fetchIncomes();
    fetchCategories();
    fetchAccounts();
  }, []);

  const fetchIncomes = async () => {
    try {
      setLoading(true);
      let url = '/incomes';
      
      // Add filters if they exist
      if (filters.startDate && filters.endDate) {
        url = `/incomes/date-range?startDate=${filters.startDate}&endDate=${filters.endDate}`;
      }
      
      const response = await instance.get(url);
      setIncomes(response.data);
      setLoading(false);
    } catch (err) {
      setError('Failed to load income data. Please try again.');
      setLoading(false);
    }
  };

  const fetchCategories = async () => {
    try {
      const response = await instance.get('/income-categories');
      setCategories(response.data);
    } catch (err) {
      console.error('Failed to load categories', err);
    }
  };

  const fetchAccounts = async () => {
    try {
      const response = await instance.get('/accounts/all');
      setAccounts(response.data);
    } catch (err) {
      console.error('Failed to load accounts', err);
    }
  };

  const handleIncomeAdded = () => {
    fetchIncomes();
    setShowAddForm(false);
    setEditIncome(null);
  };

  const handleEdit = (income) => {
    setEditIncome(income);
    setShowAddForm(false);
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Are you sure you want to delete this income entry?')) {
      return;
    }
    
    try {
      setLoading(true);
      await instance.delete(`/incomes/${id}`);
      fetchIncomes();
    } catch (err) {
      setError('Failed to delete income entry. Please try again.');
      setTimeout(() => setError(null), 3000);
    } finally {
      setLoading(false);
    }
  };

  const handleFilterChange = (e) => {
    const { name, value } = e.target;
    setFilters(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const applyFilters = () => {
    fetchIncomes();
  };

  const resetFilters = () => {
    setFilters({
      startDate: '',
      endDate: '',
      categoryId: '',
      accountId: ''
    });
    fetchIncomes();
  };

  // Get current incomes for pagination
  const filteredIncomes = incomes.filter(income => {
    let matchesCategory = true;
    let matchesAccount = true;
    
    if (filters.categoryId) {
      matchesCategory = income.incomeCategory.incomeCategoryId === parseInt(filters.categoryId);
    }
    
    if (filters.accountId) {
      matchesAccount = income.account.id === parseInt(filters.accountId);
    }
    
    return matchesCategory && matchesAccount;
  });
  
  const indexOfLastItem = currentPage * itemsPerPage;
  const indexOfFirstItem = indexOfLastItem - itemsPerPage;
  const currentIncomes = filteredIncomes.slice(indexOfFirstItem, indexOfLastItem);
  
  // Change page
  const paginate = (pageNumber) => setCurrentPage(pageNumber);

  const formatDate = (dateString) => {
    const date = new Date(dateString);
    return date.toLocaleDateString();
  };

  const getCategoryName = (categoryId) => {
    const category = categories.find(c => c.incomeCategoryId === categoryId);
    return category ? category.name : 'Unknown';
  };

  const getAccountName = (accountId) => {
    const account = accounts.find(a => a.id === accountId);
    return account ? account.name : 'Unknown';
  };

  return (
    <Container>
      <h2 className="my-4">Income Management</h2>
      
      {error && <Alert variant="danger">{error}</Alert>}
      
      <Card className="mb-4">
        <Card.Header>
          <Row className="align-items-center">
            <Col>
              <h5 className="mb-0">Filter Income</h5>
            </Col>
          </Row>
        </Card.Header>
        <Card.Body>
          <Row>
            <Col md={3}>
              <Form.Group className="mb-3">
                <Form.Label>Start Date</Form.Label>
                <Form.Control
                  type="date"
                  name="startDate"
                  value={filters.startDate}
                  onChange={handleFilterChange}
                />
              </Form.Group>
            </Col>
            <Col md={3}>
              <Form.Group className="mb-3">
                <Form.Label>End Date</Form.Label>
                <Form.Control
                  type="date"
                  name="endDate"
                  value={filters.endDate}
                  onChange={handleFilterChange}
                />
              </Form.Group>
            </Col>
            <Col md={3}>
              <Form.Group className="mb-3">
                <Form.Label>Category</Form.Label>
                <Form.Select
                  name="categoryId"
                  value={filters.categoryId}
                  onChange={handleFilterChange}
                >
                  <option value="">All Categories</option>
                  {categories.map(category => (
                    <option key={category.incomeCategoryId} value={category.incomeCategoryId}>
                      {category.name}
                    </option>
                  ))}
                </Form.Select>
              </Form.Group>
            </Col>
            <Col md={3}>
              <Form.Group className="mb-3">
                <Form.Label>Account</Form.Label>
                <Form.Select
                  name="accountId"
                  value={filters.accountId}
                  onChange={handleFilterChange}
                >
                  <option value="">All Accounts</option>
                  {accounts.map(account => (
                    <option key={account.id} value={account.id}>
                      {account.name}
                    </option>
                  ))}
                </Form.Select>
              </Form.Group>
            </Col>
          </Row>
          <Row>
            <Col className="d-flex justify-content-end">
              <Button variant="secondary" className="me-2" onClick={resetFilters}>
                Reset
              </Button>
              <Button variant="primary" onClick={applyFilters}>
                Apply Filters
              </Button>
            </Col>
          </Row>
        </Card.Body>
      </Card>
      
      <div className="d-flex justify-content-between align-items-center mb-4">
        <h3>Income Entries</h3>
        <Button 
          variant="success" 
          onClick={() => {
            setShowAddForm(!showAddForm);
            setEditIncome(null);
          }}
        >
          {showAddForm ? 'Hide Form' : 'Add New Income'}
        </Button>
      </div>
      
      {showAddForm && (
        <IncomeForm onIncomeAdded={handleIncomeAdded} />
      )}
      
      {editIncome && (
        <div className="mb-4">
          <h4>Edit Income</h4>
          <IncomeForm onIncomeAdded={handleIncomeAdded} editIncome={editIncome} />
        </div>
      )}
      
      {loading && !currentIncomes.length ? (
        <div className="d-flex justify-content-center my-5">
          <Spinner animation="border" />
        </div>
      ) : (
        <>
          {currentIncomes.length === 0 ? (
            <Alert variant="info">
              No income entries found. Add your first income entry using the form above.
            </Alert>
          ) : (
            <>
              <Table striped bordered hover responsive>
                <thead>
                  <tr>
                    <th>Date</th>
                    <th>Category</th>
                    <th>Account</th>
                    <th>Amount</th>
                    <th>Description</th>
                    <th>Recurring</th>
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {currentIncomes.map(income => (
                    <tr key={income.incomeId}>
                      <td>{formatDate(income.date)}</td>
                      <td>{income.incomeCategory ? income.incomeCategory.name : getCategoryName(income.incomeCategory.incomeCategoryId)}</td>
                      <td>{income.account ? income.account.name : getAccountName(income.account.id)}</td>
                      <td>${parseFloat(income.amount).toFixed(2)}</td>
                      <td>{income.description || '-'}</td>
                      <td>{income.isRecurring ? `Yes (${income.recurrencePeriod})` : 'No'}</td>
                      <td>
                        <Button 
                          variant="outline-primary" 
                          size="sm" 
                          className="me-2"
                          onClick={() => handleEdit(income)}
                        >
                          Edit
                        </Button>
                        <Button 
                          variant="outline-danger" 
                          size="sm"
                          onClick={() => handleDelete(income.incomeId)}
                        >
                          Delete
                        </Button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </Table>
              
              {/* Pagination */}
              {filteredIncomes.length > itemsPerPage && (
                <div className="d-flex justify-content-center">
                  <Pagination>
                    <Pagination.First onClick={() => paginate(1)} disabled={currentPage === 1} />
                    <Pagination.Prev onClick={() => paginate(currentPage - 1)} disabled={currentPage === 1} />
                    
                    {[...Array(Math.ceil(filteredIncomes.length / itemsPerPage)).keys()].map(number => (
                      <Pagination.Item 
                        key={number + 1} 
                        active={number + 1 === currentPage}
                        onClick={() => paginate(number + 1)}
                      >
                        {number + 1}
                      </Pagination.Item>
                    ))}
                    
                    <Pagination.Next 
                      onClick={() => paginate(currentPage + 1)} 
                      disabled={currentPage === Math.ceil(filteredIncomes.length / itemsPerPage)} 
                    />
                    <Pagination.Last 
                      onClick={() => paginate(Math.ceil(filteredIncomes.length / itemsPerPage))} 
                      disabled={currentPage === Math.ceil(filteredIncomes.length / itemsPerPage)} 
                    />
                  </Pagination>
                </div>
              )}
            </>
          )}
        </>
      )}
    </Container>
  );
};

export default IncomeList;
