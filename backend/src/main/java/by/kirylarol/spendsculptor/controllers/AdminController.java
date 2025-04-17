package by.kirylarol.spendsculptor.controllers;

import by.kirylarol.spendsculptor.dto.AccountInfoDTO;
import by.kirylarol.spendsculptor.dto.UserInfoDTO;
import by.kirylarol.spendsculptor.entities.*;
import by.kirylarol.spendsculptor.service.AccountService;
import by.kirylarol.spendsculptor.service.AccountUserService;
import by.kirylarol.spendsculptor.service.UserService;
import by.kirylarol.spendsculptor.utils.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class AdminController {

    Util util;
    UserService userService;
    AccountUserService accountUserService;
    AccountService accountService;


    @Autowired
    public AdminController(Util util, UserService userService, AccountUserService accountUserService, AccountService accountService) {
        this.util = util;
        this.userService = userService;
        this.accountUserService = accountUserService;
        this.accountService = accountService;
    }

    @GetMapping("/admin")
    boolean getAccess() throws Exception {
        return true;
    }


    @GetMapping("admin/users")
    List<UserInfoDTO> getUsers() throws Exception {
        List<UserInfoDTO> userInfoDTOS = new ArrayList<>();
        userService.getUsers().forEach(item ->
                userInfoDTOS.add(
                        new UserInfoDTO(item.getLogin(), item.getIdentity().getName(), item.getIdentity().getSurname(), item.getRole())
                )
        );
        return userInfoDTOS;
    }


    @DeleteMapping("admin/users/{login}")
    boolean deleteUser(@PathVariable String login) throws Exception {
        User user = userService.getUser(login);
        if (user == null) return false;
        if (user.getLogin().equals(login)){
            throw new Exception("Вы не можете удалить сами себя");
        }
        List<AccountUser> accountUserList = accountUserService.getByUser(user.getId());
        accountUserList.forEach(
                item -> accountUserService.removeUser(item.getAccount(), user)
        );
        userService.deleteUser(user);
        return true;
    }

    @DeleteMapping ("admin/accounts/{id}")
    boolean deleteAccount (@PathVariable int id){
        Account account = accountService.getAccount(id);
        accountService.removeAccount(account);
        return true;
    }


    @GetMapping("admin/accounts")
    List<AccountInfoDTO> getAll() {
        List<Account> accountList = accountService.getAll();
        List<AccountInfoDTO> accountInfoDTOS = new ArrayList<>();

        accountList.forEach(
                item -> {
                    List<AccountUser> accountUserList = item.getAccountUsers();
                    String owner;
                    try {
                        owner = accountUserList.stream().filter(
                                user -> user.getPermission() == ACCOUNT_ENUM.ACCOUNT_CREATOR
                        ).findFirst().orElseGet(null).getUser().getLogin();
                    } catch (NullPointerException e) {
                        owner = "Создатель не найден";
                    }
                    accountInfoDTOS.add(
                            new AccountInfoDTO(
                                    item.getId(),
                                    item.getName(),
                                    accountUserList.size(),
                                    owner
                            )
                    );
                    return;
                }
        );
        return accountInfoDTOS;
    }
}
