package by.kirylarol.spendsculptor.controllers;


import by.kirylarol.spendsculptor.service.*;
import by.kirylarol.spendsculptor.dto.*;
import by.kirylarol.spendsculptor.entities.*;
import by.kirylarol.spendsculptor.utils.Util;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class AccountController {

    AccountUserService accountUserService;
    ReceiptService receiptService;
    ShopService shopService;

    CategoryService categoryService;
    UserService userService;


    Util util;


    AccountService accountService;

    GoalService goalService;

    PositionService positionService;

    public AccountController(AccountUserService accountUserService, ReceiptService receiptService, ShopService shopService, CategoryService categoryService, UserService userService, Util util, AccountService accountService, GoalService goalService, PositionService positionService) {
        this.accountUserService = accountUserService;
        this.receiptService = receiptService;
        this.shopService = shopService;
        this.categoryService = categoryService;
        this.userService = userService;
        this.util = util;
        this.accountService = accountService;
        this.goalService = goalService;
        this.positionService = positionService;
    }

    @DeleteMapping("account/delete/{id}")
    ResponseEntity<?> leaveUser(@PathVariable int id) throws Exception {
        User user = util.getUser();
        accountUserService.removeUser(id, user);
        return ResponseEntity.noContent().build();
    }


    @PostMapping("account/{id}/goal/")
    ResponseEntity<?> createGoal(@PathVariable int id, @RequestBody GoalDTO goalDTO) throws Exception {
        Map<String, String> response = new HashMap<>();
        Account account = accountService.getAccount(id);
        if (account == null) {
            response.put("message", "Счет не найден");
            return ResponseEntity.badRequest().body(response);
        }
        if (goalDTO.getTitle() == null || goalDTO.getTitle().length() < 3) {
            response.put("message", "Название должно быть длиннее 3 символов");
            return ResponseEntity.badRequest().body(response);
        }
        if (goalDTO.getDateStart() == 0 || goalDTO.getDateEnd() == 0 || goalDTO.getDateStart() > goalDTO.getDateEnd()) {
            response.put("message", "Ошибка с датами");
            return ResponseEntity.badRequest().body(response);
        }
        if (goalDTO.getTarget().compareTo(BigDecimal.ZERO) < 0) {
            response.put("message", "Цель должна быть положительной");
        }

        BigDecimal state = receiptService.getAllSpends(account, Util.javaScriptMilisToLocalDate(goalDTO.getDateStart()), Util.javaScriptMilisToLocalDate(goalDTO.getDateEnd()));
        Goal goal = new Goal();
        goal.setState(state);
        goal.setAccount(account);
        toGoal(goal, goalDTO);
        goal.setState(state);
        response.put("message", "Успешно");
        return ResponseEntity.ok(response);
    }


    void toGoal(Goal goal, GoalDTO goalDTO) {
        goal.setCreated(Util.javaScriptMilisToLocalDate(goalDTO.getDateStart()));
        goal.setName(goalDTO.getTitle());
        goal.setValid(Util.javaScriptMilisToLocalDate(goalDTO.getDateEnd()));
        goal.setTarget(goalDTO.getTarget());
        goalService.createGoal(goal);
    }


    @GetMapping("/accounts")
    public List<Account> getAllAccounts() throws Exception {
        User user = util.getUser();
        if (user == null) {
            throw new Exception("Please login before");
        }
        List<Account> accountList = accountUserService.getAccountByUser(user);
        return accountList;
    }

    @PostMapping("accounts/new")
    public Map<String, String> createNewAccount(@RequestBody @Valid AccountDTO accountDTO, BindingResult bindingResult) throws Exception {

        if (bindingResult.hasErrors()) {
            return Map.of("message", bindingResult.getAllErrors()
                    .stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.joining(", ")));
        }

        Account account = toAccount(accountDTO);
        User user = util.getUser();
        accountUserService.addAccount(account, user);
        return Map.of("message", "Аккаунт " + account.getName() + " успешно добавлен");
    }

    @GetMapping("account/{id}")
    public FullAccountDTO accountPage(@PathVariable int id) throws Exception {
        User user = util.getUser();
        if (!accountUserService.accessForUser(id, user.getId()))
            throw new BadCredentialsException("Нет доступа к этому счету");
        AccountUser current = accountUserService.getByUserAndAccount(id, user.getId());
        Account account = current.getAccount();
        return new FullAccountDTO(current.getPermission(),
                current.getAccount(),
                accountUserService.getUsersByAccount(current.getAccount()),
                account.getGoalList(),
                current.getReceiptList(),
                current.getWeight());
    }

    @DeleteMapping("account/{id}/goal/{goalid}")
    public ResponseEntity<?> deleteGoal(@PathVariable int id, @PathVariable int goalid) throws Exception {
        Map<String, String> response = new HashMap<>();
        User user = util.getUser();
        AccountUser accountUser = accountUserService.getByUserAndAccount(id, user.getId());
        if (accountUser == null || accountUser.getPermission() == ACCOUNT_ENUM.ACCOUNT_USER) {
            response.put("message", "Недостаточно прав");
            return ResponseEntity.badRequest().body(response);
        }
        try {
            goalService.deleteGoal(goalid);
        } catch (Exception e) {
            response.put("message", "Ошибка при удалении");
            return ResponseEntity.badRequest().body(response);
        }
        response.put("message", "Успешно");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping ("account/{id}")
    public boolean deleteAccount (@PathVariable int id) throws Exception{
        User user = util.getUser();
        AccountUser accountUser = accountUserService.getByUserAndAccount(id, user.getId());
        if (accountUser.getPermission() == ACCOUNT_ENUM.ACCOUNT_CREATOR){
            accountService.removeAccount(accountUser.getAccount());
        }else{
            accountUserService.removeUser(accountUser.getAccount(),accountUser.getUser());
        }
        return true;
    }

    @PostMapping("account/{id}/adduser")
    public ResponseEntity<?> addUser(@PathVariable int id, @RequestBody @Valid NewAccountUserDTO newAccountUserDTO, BindingResult bindingResult) throws Exception {
        Map<String, Object> response = new HashMap<>();

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(Map.of("message", bindingResult.getAllErrors()
                    .stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.joining(", "))));
        }
        User user = util.getUser();
        AccountUser accountUser = accountUserService.getByUserAndAccount(id, user.getId());
        if (accountUser == null || accountUser.getPermission() == ACCOUNT_ENUM.ACCOUNT_USER) {
            response.put("message", "Недостаточно прав");
            return ResponseEntity.badRequest().body(response);
        }
        User user1 = userService.getUser(newAccountUserDTO.getUsername());
        if (user1 == null) {
            response.put("message", "Пользователь не найден");
            return ResponseEntity.badRequest().body(response);
        }
        if (accountUserService.getByUserAndAccount(id, user1.getId()) != null) {
            response.put("message", "Пользователь уже добавлен в счет");
            return ResponseEntity.badRequest().body(response);
        }
        AccountUser newAccountUser = accountUserService.addUser(id, user1, (double) newAccountUserDTO.getWeight() / 100, newAccountUserDTO.getROLE());
        if (newAccountUser != null) {
            response.put("message", "ok");
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.internalServerError().build();
    }

    ;


    @GetMapping("account/{id}/receipts")
    public PartAccountDTO accountReceipts(@PathVariable int id) throws Exception {
        User user = util.getUser();
        if (!accountUserService.accessForUser(id, user.getId()))
            throw new BadCredentialsException("Нет доступа к этому счету");
        AccountUser current = accountUserService.getByUserAndAccount(id, user.getId());
        PartAccountDTO receiptsDTO = new PartAccountDTO();
        receiptsDTO.setAccountUser(current);
        receiptsDTO.setReceiptList(current.getReceiptList());
        return receiptsDTO;
    }

    @PostMapping("account/{id}/receipt/new")
    public ResponseEntity<?> addReceipt(@PathVariable int id, @RequestBody ReceiptDTO receiptDTO) throws Exception {
        User user = util.getUser();
        AccountUser accountUser = accountUserService.getByUserAndAccount(id, user.getId());
        Receipt newReceipt = new Receipt();
        util.toReceipt(newReceipt, receiptDTO);
        newReceipt.setAccount(accountUser);
        goalService.changeStateOfGoals(accountUser.getAccount(), newReceipt.getDate(), newReceipt.getTotal());
        List<Position> positionList = newReceipt.getPositionList();
        newReceipt.setPositionList(new ArrayList<>());
        receiptService.addReceipt(newReceipt);
        positionService.updateList(positionList,newReceipt);
        return ResponseEntity.ok().build();
    }


    Account toAccount(AccountDTO accountDTO) {
        return new Account(accountDTO.getName(), Util.javaScriptMilisToLocalDate(accountDTO.getDate()));
    }

    @DeleteMapping("account/{id}/deleteuser/{username}")
    ResponseEntity<?> deleteUserFromAccount(@PathVariable int id, @PathVariable String username) throws Exception {
        HashMap<String, String> response = new HashMap<>();
        User currUser = util.getUser();
        User userForDelete = userService.getUser(username);
        AccountUser currAccountUser = accountUserService.getByUserAndAccount(id, currUser.getId());
        AccountUser accountUserForDelete = accountUserService.getByUserAndAccount(id, userForDelete.getId());
        if (accountUserForDelete == null) {
            response.put("message", "Пользователь не найден");
            return ResponseEntity.badRequest().body(response);
        }
        if (userForDelete.getId() != currUser.getId() && currAccountUser.getPermission() == ACCOUNT_ENUM.ACCOUNT_USER || (accountUserForDelete.getPermission() == ACCOUNT_ENUM.ACCOUNT_ADMIN && currAccountUser.getPermission() != ACCOUNT_ENUM.ACCOUNT_CREATOR) || accountUserForDelete.getPermission() == ACCOUNT_ENUM.ACCOUNT_CREATOR) {
            response.put("message", "Недостаточно прав");
            return ResponseEntity.badRequest().body(response);
        }
        accountUserService.removeUser(id, userForDelete);
        response.put("message", "Успешно");
        return ResponseEntity.ok(response);
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handeExc(Exception exception) {
        return ResponseEntity.badRequest().body(Map.of("message", exception.getMessage()));
    }

}
