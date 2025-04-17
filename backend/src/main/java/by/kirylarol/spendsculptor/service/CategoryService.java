package by.kirylarol.spendsculptor.service;


import by.kirylarol.spendsculptor.entities.Category;
import by.kirylarol.spendsculptor.entities.Position;
import by.kirylarol.spendsculptor.repos.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class CategoryService {
    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> getAll() {
        return categoryRepository.findAll();
    }

    public Category getById(int id) {
        return categoryRepository.findById(id).orElse(null);
    }

    public Category getByName(String name) {
        return categoryRepository.findDistinctFirstByCategoryName(name);
    }

    @Transactional
    public Category createCategory(Category category) {
        if (category != null) {
            Category dbCategory = categoryRepository.findByCategoryName(category.categoryName());
            if (dbCategory != null) return dbCategory;
            category.setPositions(new ArrayList<>());
            return categoryRepository.save(category);
        }
        return null;
    }


    public void predictCategory(List<Position> positionList, int userId) {
        for (var elem : positionList) {
            if (elem.getCategory() == null) {
                List<Category> res = categoryRepository.findTopCategoryByNameAndUserId(elem.getName(), userId);
                if (!res.isEmpty()) {
                    elem.setCategory(res.get(0));
                }
            }
        }
    }

    @Transactional
    public void deleteCategory(String name) {
        Category category = categoryRepository.findDistinctFirstByCategoryName(name);
        if (category != null) deleteCategory(category);
    }

    @Transactional
    public void deleteCategory(Category category) {
        categoryRepository.delete(category);
    }

    @Transactional
    public Category updateCategory(String newName, Category category) {
        if (this.getByName(newName) != null) {
            categoryRepository.deleteById(category.categoryId());
            return this.getByName(newName);
        }
        category.setCategoryName(newName);
        return categoryRepository.save(category);
    }

    @Transactional
    public Category createCategory(String name) {

        Category category1 = categoryRepository.findByCategoryName(name);
        if (category1 != null) return category1;
        Category category = new Category(name);
        return createCategory(category);
    }


    public List<Category> findAllByUser(int id) {
        return categoryRepository.findAllByUser(id);
    }

    public List<Category> findAllByAccountUser(int id){
        return categoryRepository.findAllByAccountUser(id);
    }
}
