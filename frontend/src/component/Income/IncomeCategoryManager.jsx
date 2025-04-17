import React, { useState, useEffect } from 'react';
import { Container, Row, Col, Card, Form, Button, Table, Spinner, Alert } from 'react-bootstrap';
import { instance } from '../../../axios/axiosConfig';

export const IncomeCategoryManager = () => {
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(null);
  
  const [newCategory, setNewCategory] = useState({
    name: '',
    description: ''
  });
  
  const [editingCategory, setEditingCategory] = useState(null);

  useEffect(() => {
    fetchCategories();
  }, []);

  const fetchCategories = async () => {
    try {
      setLoading(true);
      const response = await instance.get('/income-categories');
      setCategories(response.data);
      setLoading(false);
    } catch (err) {
      setError('Failed to load income categories. Please try again.');
      setLoading(false);
    }
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    if (editingCategory) {
      setEditingCategory({
        ...editingCategory,
        [name]: value
      });
    } else {
      setNewCategory({
        ...newCategory,
        [name]: value
      });
    }
  };

  const handleCreateCategory = async (e) => {
    e.preventDefault();
    
    if (!newCategory.name.trim()) {
      setError('Category name is required');
      return;
    }
    
    try {
      setLoading(true);
      await instance.post('/income-categories', newCategory);
      setSuccess('Category created successfully!');
      setNewCategory({ name: '', description: '' });
      fetchCategories();
      
      // Clear success message after 3 seconds
      setTimeout(() => setSuccess(null), 3000);
    } catch (err) {
      setError('Failed to create category. It might already exist.');
      // Clear error message after 3 seconds
      setTimeout(() => setError(null), 3000);
    } finally {
      setLoading(false);
    }
  };

  const handleUpdateCategory = async (e) => {
    e.preventDefault();
    
    if (!editingCategory.name.trim()) {
      setError('Category name is required');
      return;
    }
    
    try {
      setLoading(true);
      await instance.put(`/income-categories/${editingCategory.incomeCategoryId}`, editingCategory);
      setSuccess('Category updated successfully!');
      setEditingCategory(null);
      fetchCategories();
      
      // Clear success message after 3 seconds
      setTimeout(() => setSuccess(null), 3000);
    } catch (err) {
      setError('Failed to update category. The name might already be in use.');
      // Clear error message after 3 seconds
      setTimeout(() => setError(null), 3000);
    } finally {
      setLoading(false);
    }
  };

  const handleDeleteCategory = async (id) => {
    if (!window.confirm('Are you sure you want to delete this category? This action cannot be undone.')) {
      return;
    }
    
    try {
      setLoading(true);
      await instance.delete(`/income-categories/${id}`);
      setSuccess('Category deleted successfully!');
      fetchCategories();
      
      // Clear success message after 3 seconds
      setTimeout(() => setSuccess(null), 3000);
    } catch (err) {
      setError('Failed to delete category. It might be in use by income entries.');
      // Clear error message after 3 seconds
      setTimeout(() => setError(null), 3000);
    } finally {
      setLoading(false);
    }
  };

  const handleEditClick = (category) => {
    setEditingCategory(category);
    setNewCategory({ name: '', description: '' });
  };

  const handleCancelEdit = () => {
    setEditingCategory(null);
  };

  const handleInitializeDefaultCategories = async () => {
    try {
      setLoading(true);
      await instance.post('/income-categories/initialize');
      setSuccess('Default categories initialized successfully!');
      fetchCategories();
      
      // Clear success message after 3 seconds
      setTimeout(() => setSuccess(null), 3000);
    } catch (err) {
      setError('Failed to initialize default categories.');
      // Clear error message after 3 seconds
      setTimeout(() => setError(null), 3000);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Container>
      <h2 className="my-4">Income Categories</h2>
      
      {error && <Alert variant="danger">{error}</Alert>}
      {success && <Alert variant="success">{success}</Alert>}
      
      <Row>
        <Col md={6}>
          <Card className="mb-4">
            <Card.Header as="h5">
              {editingCategory ? 'Edit Category' : 'Add New Category'}
            </Card.Header>
            <Card.Body>
              <Form onSubmit={editingCategory ? handleUpdateCategory : handleCreateCategory}>
                <Form.Group className="mb-3">
                  <Form.Label>Name</Form.Label>
                  <Form.Control
                    type="text"
                    name="name"
                    value={editingCategory ? editingCategory.name : newCategory.name}
                    onChange={handleInputChange}
                    placeholder="Enter category name"
                    required
                  />
                </Form.Group>
                
                <Form.Group className="mb-3">
                  <Form.Label>Description</Form.Label>
                  <Form.Control
                    as="textarea"
                    name="description"
                    value={editingCategory ? editingCategory.description || '' : newCategory.description}
                    onChange={handleInputChange}
                    placeholder="Enter category description (optional)"
                    rows={3}
                  />
                </Form.Group>
                
                <div className="d-flex justify-content-between">
                  {editingCategory ? (
                    <>
                      <Button variant="secondary" onClick={handleCancelEdit}>
                        Cancel
                      </Button>
                      <Button variant="primary" type="submit" disabled={loading}>
                        {loading ? <Spinner animation="border" size="sm" /> : 'Update Category'}
                      </Button>
                    </>
                  ) : (
                    <Button variant="primary" type="submit" className="w-100" disabled={loading}>
                      {loading ? <Spinner animation="border" size="sm" /> : 'Add Category'}
                    </Button>
                  )}
                </div>
              </Form>
            </Card.Body>
          </Card>
          
          <Button 
            variant="outline-secondary" 
            onClick={handleInitializeDefaultCategories}
            className="mb-4"
          >
            Initialize Default Categories
          </Button>
        </Col>
        
        <Col md={6}>
          <Card>
            <Card.Header as="h5">Existing Categories</Card.Header>
            <Card.Body>
              {loading && !categories.length ? (
                <div className="d-flex justify-content-center my-3">
                  <Spinner animation="border" />
                </div>
              ) : categories.length === 0 ? (
                <Alert variant="info">
                  No income categories found. Add your first category using the form or initialize default categories.
                </Alert>
              ) : (
                <Table striped bordered hover>
                  <thead>
                    <tr>
                      <th>Name</th>
                      <th>Description</th>
                      <th>Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    {categories.map(category => (
                      <tr key={category.incomeCategoryId}>
                        <td>{category.name}</td>
                        <td>{category.description || '-'}</td>
                        <td>
                          <Button 
                            variant="outline-primary" 
                            size="sm" 
                            className="me-2"
                            onClick={() => handleEditClick(category)}
                          >
                            Edit
                          </Button>
                          <Button 
                            variant="outline-danger" 
                            size="sm"
                            onClick={() => handleDeleteCategory(category.incomeCategoryId)}
                          >
                            Delete
                          </Button>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </Table>
              )}
            </Card.Body>
          </Card>
        </Col>
      </Row>
    </Container>
  );
};

export default IncomeCategoryManager;
