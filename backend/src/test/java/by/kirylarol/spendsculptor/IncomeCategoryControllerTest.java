package by.kirylarol.spendsculptor;

import by.kirylarol.spendsculptor.controllers.IncomeCategoryController;
import by.kirylarol.spendsculptor.entities.IncomeCategory;
import by.kirylarol.spendsculptor.entities.User;
import by.kirylarol.spendsculptor.service.IncomeCategoryService;
import by.kirylarol.spendsculptor.utils.Util;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

public class IncomeCategoryControllerTest {

    @Mock
    private IncomeCategoryService incomeCategoryService;

    @Mock
    private Util util;

    @InjectMocks
    private IncomeCategoryController incomeCategoryController;

    private User testUser;
    private IncomeCategory testCategory;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testUser = new User();
        testUser.setId(1);

        testCategory = new IncomeCategory("Test Category", "Test Description");
        testCategory.setIncomeCategoryId(1);
    }

    @Test
    void testGetAllCategories() throws Exception {
        // Arrange
        List<IncomeCategory> expectedCategories = Arrays.asList(testCategory);
        when(util.getUser()).thenReturn(testUser);
        when(incomeCategoryService.getAllCategories()).thenReturn(expectedCategories);

        // Act
        ResponseEntity<List<IncomeCategory>> response = incomeCategoryController.getAllCategories();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedCategories, response.getBody());
        verify(util).getUser();
        verify(incomeCategoryService).getAllCategories();
    }

    @Test
    void testGetAllCategoriesUnauthorized() throws Exception {
        // Arrange
        when(util.getUser()).thenThrow(new RuntimeException("Unauthorized"));

        // Act
        ResponseEntity<List<IncomeCategory>> response = incomeCategoryController.getAllCategories();

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        verify(util).getUser();
        verify(incomeCategoryService, never()).getAllCategories();
    }

    @Test
    void testGetCategoryById() throws Exception {
        // Arrange
        when(util.getUser()).thenReturn(testUser);
        when(incomeCategoryService.getCategoryById(1)).thenReturn(Optional.of(testCategory));

        // Act
        ResponseEntity<?> response = incomeCategoryController.getCategoryById(1);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testCategory, response.getBody());
        verify(util).getUser();
        verify(incomeCategoryService).getCategoryById(1);
    }

    @Test
    void testGetCategoryByIdNotFound() throws Exception {
        // Arrange
        when(util.getUser()).thenReturn(testUser);
        when(incomeCategoryService.getCategoryById(999)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> response = incomeCategoryController.getCategoryById(999);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(util).getUser();
        verify(incomeCategoryService).getCategoryById(999);
    }

    @Test
    void testCreateCategory() throws Exception {
        // Arrange
        when(util.getUser()).thenReturn(testUser);
        when(incomeCategoryService.createCategory(any(IncomeCategory.class))).thenReturn(testCategory);

        // Act
        ResponseEntity<?> response = incomeCategoryController.createCategory(testCategory);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(testCategory, response.getBody());
        verify(util).getUser();
        verify(incomeCategoryService).createCategory(testCategory);
    }

    @Test
    void testCreateCategoryWithError() throws Exception {
        // Arrange
        when(util.getUser()).thenReturn(testUser);
        when(incomeCategoryService.createCategory(any(IncomeCategory.class)))
            .thenThrow(new IllegalArgumentException("Category already exists"));

        // Act
        ResponseEntity<?> response = incomeCategoryController.createCategory(testCategory);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Category already exists", response.getBody());
        verify(util).getUser();
        verify(incomeCategoryService).createCategory(testCategory);
    }

    @Test
    void testUpdateCategory() throws Exception {
        // Arrange
        when(util.getUser()).thenReturn(testUser);
        when(incomeCategoryService.updateCategory(anyInt(), any(IncomeCategory.class))).thenReturn(testCategory);

        // Act
        ResponseEntity<?> response = incomeCategoryController.updateCategory(1, testCategory);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testCategory, response.getBody());
        verify(util).getUser();
        verify(incomeCategoryService).updateCategory(1, testCategory);
    }

    @Test
    void testUpdateCategoryWithError() throws Exception {
        // Arrange
        when(util.getUser()).thenReturn(testUser);
        when(incomeCategoryService.updateCategory(anyInt(), any(IncomeCategory.class)))
            .thenThrow(new IllegalArgumentException("Category name already exists"));

        // Act
        ResponseEntity<?> response = incomeCategoryController.updateCategory(1, testCategory);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Category name already exists", response.getBody());
        verify(util).getUser();
        verify(incomeCategoryService).updateCategory(1, testCategory);
    }

    @Test
    void testDeleteCategory() throws Exception {
        // Arrange
        when(util.getUser()).thenReturn(testUser);
        doNothing().when(incomeCategoryService).deleteCategory(1);

        // Act
        ResponseEntity<?> response = incomeCategoryController.deleteCategory(1);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(util).getUser();
        verify(incomeCategoryService).deleteCategory(1);
    }

    @Test
    void testDeleteCategoryNotFound() throws Exception {
        // Arrange
        when(util.getUser()).thenReturn(testUser);
        doThrow(new IllegalArgumentException("Category not found")).when(incomeCategoryService).deleteCategory(999);

        // Act
        ResponseEntity<?> response = incomeCategoryController.deleteCategory(999);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(util).getUser();
        verify(incomeCategoryService).deleteCategory(999);
    }

    @Test
    void testDeleteCategoryWithAssociatedIncomes() throws Exception {
        // Arrange
        when(util.getUser()).thenReturn(testUser);
        doThrow(new IllegalStateException("Cannot delete category with associated incomes"))
            .when(incomeCategoryService).deleteCategory(1);

        // Act
        ResponseEntity<?> response = incomeCategoryController.deleteCategory(1);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Cannot delete category with associated incomes", response.getBody());
        verify(util).getUser();
        verify(incomeCategoryService).deleteCategory(1);
    }

    @Test
    void testInitializeDefaultCategories() throws Exception {
        // Arrange
        when(util.getUser()).thenReturn(testUser);
        doNothing().when(incomeCategoryService).initializeDefaultCategories();

        // Act
        ResponseEntity<?> response = incomeCategoryController.initializeDefaultCategories();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(util).getUser();
        verify(incomeCategoryService).initializeDefaultCategories();
    }
}
