package by.kirylarol.spendsculptor.controllers;


import by.kirylarol.spendsculptor.dto.FullIdentityDTO;
import by.kirylarol.spendsculptor.dto.IdentityDTO;
import by.kirylarol.spendsculptor.service.AccountUserService;
import by.kirylarol.spendsculptor.service.ReceiptService;
import by.kirylarol.spendsculptor.entities.AccountUser;
import by.kirylarol.spendsculptor.entities.Receipt;
import by.kirylarol.spendsculptor.entities.User;
import by.kirylarol.spendsculptor.utils.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserController {


    Util util;

    ReceiptService receiptService;
    AccountUserService accountUserService;

    @Autowired
    public UserController(Util util, ReceiptService receiptService, AccountUserService accountUserService) {
        this.util = util;
        this.receiptService = receiptService;
        this.accountUserService = accountUserService;
    }


    @GetMapping("/user")
    FullIdentityDTO returnIdentity() throws Exception {
        User user = util.getUser();
        if (user == null) {
            throw new Exception("Пользователь не найден");
        }
        return new FullIdentityDTO(user, user.getIdentity());
    }

    @GetMapping("/user/accountinfo")
    List<AccountUser> getAccessForAccounts() throws Exception {
        User user = util.getUser();
        return accountUserService.getByUser(user.getId());
    }

    @GetMapping("/user/receipts")
    List<Receipt> getReceiptsForUser() throws Exception {
        User user = util.getUser();
        return receiptService.getAllReceiptsForUser(user.getId());
    }


}
