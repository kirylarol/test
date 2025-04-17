package by.kirylarol.spendsculptor.controllers;


import by.kirylarol.spendsculptor.dto.CategoryWithPositionsDTO;
import by.kirylarol.spendsculptor.entities.Category;
import by.kirylarol.spendsculptor.entities.User;
import by.kirylarol.spendsculptor.service.AccountUserService;
import by.kirylarol.spendsculptor.service.CategoryService;
import by.kirylarol.spendsculptor.utils.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class CategoryController {

    Util util;



    CategoryService categoryService;
    AccountUserService accountUserService;

    @Autowired
    public CategoryController(Util util, CategoryService categoryService, AccountUserService accountUserService) {
        this.util = util;
        this.categoryService = categoryService;
        this.accountUserService = accountUserService;
    }

    @GetMapping("categories/all")
    List<Category> getAll() throws Exception {
        User user = util.getUser();
        List<Category> categoryList = categoryService.findAllByUser(user.getId());
        if (categoryList.isEmpty()){
            categoryList.add( new Category("Без категории"));
        }
        return categoryList;
    }

    @GetMapping ("account/{accountid}/receipts/categories")
    List<CategoryWithPositionsDTO> getSpendsByCategories(@PathVariable int accountid) throws Exception{
        User user = util.getUser();

        if (user == null || accountUserService.getByUserAndAccount(accountid, user.getId()) == null){
            throw new Exception("Нет доступа к этому аккаунту");
        }
        int accountuserid = accountUserService.getByUserAndAccount(accountid, user.getId()).getId();
        List<Category> categoryList = categoryService.findAllByAccountUser(accountuserid);
        categoryList.forEach(
                category ->
                        category.setPositions(category.getPositions().stream().filter(
                                position -> position.getReceipt().getAccount().getId() == accountuserid
                        ).toList())
        );
        List<CategoryWithPositionsDTO> categoryWithPositionsDTOList = new ArrayList<>();
        categoryList.forEach(
                category -> {
                    categoryWithPositionsDTOList.add(new CategoryWithPositionsDTO(category));
                }
        );
        return categoryWithPositionsDTOList;
    }

}
