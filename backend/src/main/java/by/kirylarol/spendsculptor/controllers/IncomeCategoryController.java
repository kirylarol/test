package by.kirylarol.spendsculptor.controllers;

import by.kirylarol.spendsculptor.entities.IncomeCategory;
import by.kirylarol.spendsculptor.service.IncomeCategoryService;
import by.kirylarol.spendsculptor.utils.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/income-categories")
public class IncomeCategoryController {
    
    private final IncomeCategoryService incomeCategoryService;
    private final Util util;
    
    @Autowired
    public IncomeCategoryController(IncomeCategoryService incomeCategoryService, Util util) {
        this.incomeCategoryService = incomeCategoryService;
        this.util = util;
    }
    
    @GetMapping
    public ResponseEntity<List<IncomeCategory>> getAllCategories() {
        try {
            util.getUser(); // Verify user is authenticated
            List<IncomeCategory> categories = incomeCategoryService.getAllCategories();
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getCategoryById(@PathVariable Integer id) {
        try {
            util.getUser(); // Verify user is authenticated
            return incomeCategoryService.getCategoryById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    
    @PostMapping
    public ResponseEntity<?> createCategory(@RequestBody IncomeCategory category) {
        try {
            util.getUser(); // Verify user is authenticated
            IncomeCategory createdCategory = incomeCategoryService.createCategory(category);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdCategory);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable Integer id, @RequestBody IncomeCategory categoryDetails) {
        try {
            util.getUser(); // Verify user is authenticated
            IncomeCategory updatedCategory = incomeCategoryService.updateCategory(id, categoryDetails);
            return ResponseEntity.ok(updatedCategory);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Integer id) {
        try {
            util.getUser(); // Verify user is authenticated
            incomeCategoryService.deleteCategory(id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    
    @PostMapping("/initialize")
    public ResponseEntity<?> initializeDefaultCategories() {
        try {
            util.getUser(); // Verify user is authenticated
            incomeCategoryService.initializeDefaultCategories();
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
