package by.kirylarol.spendsculptor;

import by.kirylarol.spendsculptor.entities.IncomeCategory;
import by.kirylarol.spendsculptor.repos.IncomeCategoryRepository;
import by.kirylarol.spendsculptor.service.IncomeCategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class IncomeCategoryServiceTest {

    @Mock
    private IncomeCategoryRepository incomeCategoryRepository;

    @InjectMocks
    private IncomeCategoryService incomeCategoryService;

    private IncomeCategory testCategory;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testCategory = new IncomeCategory("Test Category", "Test Description");
        testCategory.setIncomeCategoryId(1);
    }

    @Test
    void testGetAllCategories() {
        // Arrange
        List<IncomeCategory> expectedCategories = Arrays.asList(
            testCategory,
            new IncomeCategory("Another Category")
        );
        when(incomeCategoryRepository.findAllByOrderByNameAsc()).thenReturn(expectedCategories);

        // Act
        List<IncomeCategory> actualCategories = incomeCategoryService.getAllCategories();

        // Assert
        assertEquals(expectedCategories.size(), actualCategories.size());
        assertEquals(expectedCategories, actualCategories);
        verify(incomeCategoryRepository).findAllByOrderByNameAsc();
    }

    @Test
    void testGetCategoryById() {
        // Arrange
        when(incomeCategoryRepository.findById(1)).thenReturn(Optional.of(testCategory));
        when(incomeCategoryRepository.findById(2)).thenReturn(Optional.empty());

        // Act
        Optional<IncomeCategory> foundCategory = incomeCategoryService.getCategoryById(1);
        Optional<IncomeCategory> notFoundCategory = incomeCategoryService.getCategoryById(2);

        // Assert
        assertTrue(foundCategory.isPresent());
        assertEquals(testCategory, foundCategory.get());
        assertFalse(notFoundCategory.isPresent());
        verify(incomeCategoryRepository).findById(1);
        verify(incomeCategoryRepository).findById(2);
    }

    @Test
    void testGetCategoryByName() {
        // Arrange
        when(incomeCategoryRepository.findByName("Test Category")).thenReturn(Optional.of(testCategory));
        when(incomeCategoryRepository.findByName("Nonexistent")).thenReturn(Optional.empty());

        // Act
        Optional<IncomeCategory> foundCategory = incomeCategoryService.getCategoryByName("Test Category");
        Optional<IncomeCategory> notFoundCategory = incomeCategoryService.getCategoryByName("Nonexistent");

        // Assert
        assertTrue(foundCategory.isPresent());
        assertEquals(testCategory, foundCategory.get());
        assertFalse(notFoundCategory.isPresent());
        verify(incomeCategoryRepository).findByName("Test Category");
        verify(incomeCategoryRepository).findByName("Nonexistent");
    }

    @Test
    void testCreateCategory() {
        // Arrange
        IncomeCategory newCategory = new IncomeCategory("New Category");
        when(incomeCategoryRepository.existsByName("New Category")).thenReturn(false);
        when(incomeCategoryRepository.save(any(IncomeCategory.class))).thenReturn(newCategory);

        // Act
        IncomeCategory createdCategory = incomeCategoryService.createCategory(newCategory);

        // Assert
        assertEquals(newCategory, createdCategory);
        verify(incomeCategoryRepository).existsByName("New Category");
        verify(incomeCategoryRepository).save(newCategory);
    }

    @Test
    void testCreateCategoryWithExistingName() {
        // Arrange
        IncomeCategory newCategory = new IncomeCategory("Existing Category");
        when(incomeCategoryRepository.existsByName("Existing Category")).thenReturn(true);

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            incomeCategoryService.createCategory(newCategory);
        });
        
        assertTrue(exception.getMessage().contains("already exists"));
        verify(incomeCategoryRepository).existsByName("Existing Category");
        verify(incomeCategoryRepository, never()).save(any(IncomeCategory.class));
    }

    @Test
    void testUpdateCategory() {
        // Arrange
        IncomeCategory updatedDetails = new IncomeCategory("Updated Name", "Updated Description");
        when(incomeCategoryRepository.findById(1)).thenReturn(Optional.of(testCategory));
        when(incomeCategoryRepository.existsByName("Updated Name")).thenReturn(false);
        when(incomeCategoryRepository.save(any(IncomeCategory.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        IncomeCategory updatedCategory = incomeCategoryService.updateCategory(1, updatedDetails);

        // Assert
        assertEquals("Updated Name", updatedCategory.getName());
        assertEquals("Updated Description", updatedCategory.getDescription());
        verify(incomeCategoryRepository).findById(1);
        verify(incomeCategoryRepository).existsByName("Updated Name");
        verify(incomeCategoryRepository).save(any(IncomeCategory.class));
    }

    @Test
    void testUpdateCategoryWithExistingName() {
        // Arrange
        IncomeCategory updatedDetails = new IncomeCategory("Existing Name", "Updated Description");
        when(incomeCategoryRepository.findById(1)).thenReturn(Optional.of(testCategory));
        when(incomeCategoryRepository.existsByName("Existing Name")).thenReturn(true);

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            incomeCategoryService.updateCategory(1, updatedDetails);
        });
        
        assertTrue(exception.getMessage().contains("already exists"));
        verify(incomeCategoryRepository).findById(1);
        verify(incomeCategoryRepository).existsByName("Existing Name");
        verify(incomeCategoryRepository, never()).save(any(IncomeCategory.class));
    }

    @Test
    void testDeleteCategory() {
        // Arrange
        when(incomeCategoryRepository.findById(1)).thenReturn(Optional.of(testCategory));

        // Act
        incomeCategoryService.deleteCategory(1);

        // Assert
        verify(incomeCategoryRepository).findById(1);
        verify(incomeCategoryRepository).delete(testCategory);
    }

    @Test
    void testDeleteCategoryWithIncomes() {
        // Arrange
        testCategory.getIncomes().add(mock(by.kirylarol.spendsculptor.entities.Income.class));
        when(incomeCategoryRepository.findById(1)).thenReturn(Optional.of(testCategory));

        // Act & Assert
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            incomeCategoryService.deleteCategory(1);
        });
        
        assertTrue(exception.getMessage().contains("Cannot delete category"));
        verify(incomeCategoryRepository).findById(1);
        verify(incomeCategoryRepository, never()).delete(any(IncomeCategory.class));
    }

    @Test
    void testInitializeDefaultCategories() {
        // Arrange
        when(incomeCategoryRepository.count()).thenReturn(0L);
        when(incomeCategoryRepository.existsByName(anyString())).thenReturn(false);

        // Act
        incomeCategoryService.initializeDefaultCategories();

        // Assert
        verify(incomeCategoryRepository).count();
        verify(incomeCategoryRepository, times(5)).existsByName(anyString());
        verify(incomeCategoryRepository, times(5)).save(any(IncomeCategory.class));
    }

    @Test
    void testInitializeDefaultCategoriesWhenCategoriesExist() {
        // Arrange
        when(incomeCategoryRepository.count()).thenReturn(5L);

        // Act
        incomeCategoryService.initializeDefaultCategories();

        // Assert
        verify(incomeCategoryRepository).count();
        verify(incomeCategoryRepository, never()).existsByName(anyString());
        verify(incomeCategoryRepository, never()).save(any(IncomeCategory.class));
    }
}
