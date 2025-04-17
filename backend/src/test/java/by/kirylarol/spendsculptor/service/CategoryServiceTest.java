package by.kirylarol.spendsculptor.service;

import by.kirylarol.spendsculptor.entities.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(
        locations = "classpath:application-integrationtest.properties")
public class CategoryServiceTest {
    @Autowired
    private CategoryService categoryService;

    public void processResult(List<Object> list) {
        list.add(0);
        list.add(new Object());
    }


    @Test
    @Rollback
    public void createCategoryTest() {
        Category category = categoryService.createCategory("Алкоголь");
        assert (category != null);
        List<Category> categoryList = categoryService.getAll();
        assertNotEquals(0, categoryList.size());
        category.categoryId();
    }

    @Test
    @Rollback
    public void updateCategoryTest() {
        Category category = categoryService.createCategory("Алкоголь");
        Category category2 = categoryService.getByName("Алкоголь");
        Category category1 = categoryService.updateCategory("Выпивка", category);
        assertEquals("Выпивка", category1.categoryName());
        int id = category1.categoryId();
        Category categoryById = categoryService.getById(id);
        assertEquals("Выпивка", categoryById.categoryName());
        List<Category> categoryList = categoryService.getAll();
        assertEquals(1, categoryList.size());
    }

    @Test
    @Transactional
    @Rollback
    public void deleteCategoryTest() {
        Category category = categoryService.createCategory("Абоба");
        assertNotNull(categoryService.getByName("Абоба"));
        categoryService.deleteCategory("Абоба");
        assertNull(categoryService.getById(category.categoryId()));
    }


}
