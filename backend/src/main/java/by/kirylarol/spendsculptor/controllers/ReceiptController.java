package by.kirylarol.spendsculptor.controllers;


import by.kirylarol.spendsculptor.service.*;
import by.kirylarol.spendsculptor.dto.ReceiptDTO;
import by.kirylarol.spendsculptor.entities.*;
import by.kirylarol.spendsculptor.utils.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RestController
public class ReceiptController {

    ReceiptService receiptService;
    CategoryService categoryService;
    GoalService goalService;

    AccountUserService accountUserService;
    PositionService positionService;
    Util util;

    @Autowired
    public ReceiptController(ReceiptService receiptService, CategoryService categoryService, GoalService goalService, AccountUserService accountUserService, PositionService positionService, Util util) {
        this.receiptService = receiptService;
        this.categoryService = categoryService;
        this.goalService = goalService;
        this.accountUserService = accountUserService;
        this.positionService = positionService;
        this.util = util;
    }

    @PostMapping("receipt/{id}/update")
    public String updateReceipt(@PathVariable int id, @RequestBody ReceiptDTO receiptDTO) throws Exception {
        User user = util.getUser();
        Receipt receipt = receiptService.getReceipt(id);
        BigDecimal totalBefore = receipt.getTotal();
        if (receipt == null || !checkAccess(user, receipt))
            throw new HttpClientErrorException(HttpStatusCode.valueOf(400));
        util.toReceipt(receipt, receiptDTO);
        List<Position> positionList = receipt.getPositionList();
        receipt.setPositionList(new ArrayList<>());
        BigDecimal totalAfter = receipt.getTotal();
        receipt.setDate(Util.javaScriptMilisToLocalDate(receiptDTO.getDate()));
        BigDecimal add = totalAfter.subtract(totalBefore);
        goalService.changeStateOfGoals(receipt.getAccount().getAccount(), receipt.getDate(), add);
        receiptService.update(receipt);
        positionService.updateList(positionList, receipt);
        return "Успешно";
    }

    boolean checkAccess(User user, Receipt receipt) {
        Account account = receipt.getAccount().getAccount();
        AccountUser accountUser = accountUserService.getByUserAndAccount(account.getId(), user.getId());
        return accountUser != null && (accountUser.getPermission() != ACCOUNT_ENUM.ACCOUNT_USER || receipt.getAccount() == accountUser);
    }

    @PostMapping("/receipt/upload/image")
    public Receipt uploadImage(@RequestParam("image") MultipartFile file) throws Exception {
        if (file != null && !file.isEmpty()) {
            User user = util.getUser();
            Receipt receipt = new Receipt();
            receiptService.parseReceipt(file, receipt);
            categoryService.predictCategory(receipt.getPositionList(), user.getId());
            return receipt;
        }
        return null;
    }


}
