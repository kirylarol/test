package by.kirylarol.spendsculptor.service;


import by.kirylarol.spendsculptor.entities.Account;
import by.kirylarol.spendsculptor.entities.AccountUser;
import by.kirylarol.spendsculptor.entities.ACCOUNT_ENUM;
import by.kirylarol.spendsculptor.entities.User;
import by.kirylarol.spendsculptor.repos.AccountUserRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@Transactional (readOnly = true)
public class AccountUserService {
    private final AccountUserRepository accountUserRepository;

    private final AccountService accountService;

    @Autowired
    public AccountUserService(AccountUserRepository accountUserRepository, AccountService accountService) {
        this.accountUserRepository = accountUserRepository;
        this.accountService = accountService;
    }

    @Transactional
    public AccountUser addAccount(String name, User user ){
        Account account = new Account();
        account.setName(name);
        account = accountService.addAccount(account);
        Date date = Date.valueOf(LocalDate.now());

        AccountUser accountUser = new AccountUser();
        accountUser.setUser(user);
        accountUser.setAccount(account);
        accountUser.setWeight(1);
        accountUser.setPermission(ACCOUNT_ENUM.ACCOUNT_CREATOR);
        return accountUserRepository.save(accountUser);
    }

    @Transactional
    public void removeUser (int accountid, User user){
        Account account =  accountService.getAccount(accountid);
        removeUser(account,user);
    }

    @Transactional
    public AccountUser addAccount (Account account, User user){
        account = accountService.addAccount(account);
        AccountUser accountUser = new AccountUser();
        accountUser.setUser(user);
        accountUser.setAccount(account);
        accountUser.setWeight(1);
        accountUser.setPermission(ACCOUNT_ENUM.ACCOUNT_CREATOR);
        return accountUserRepository.save(accountUser);
    }


    public AccountUser getByUserAndAccount (Account account, User user){
        return accountUserRepository.findAccountUserByAccountAndUser(account,user);
    }

    public boolean accessForUser (int accountid, int userid){
        return  (accountUserRepository.findAccountUserByAccountAndUser(accountid,userid)) != null;
    }



    public boolean accessForUser (Account account, User user){
        return  (accountUserRepository.findAccountUserByAccountAndUser(account, user)) != null;
    }

    @Transactional
    public AccountUser addUser (Account account, User user, Double weight, ACCOUNT_ENUM permission){
        return addAccountUser(user, weight, permission, account);
    }

    @Transactional
    public AccountUser addUser (int accountid, User user, Double weight, ACCOUNT_ENUM permission){
        Account account = accountService.getAccount(accountid);
        return addAccountUser(user, weight, permission, account);
    }

    @NotNull
    private AccountUser addAccountUser(User user, Double weight, ACCOUNT_ENUM permission, Account account) {
        List<AccountUser> accountUserList =  accountUserRepository.findAccountUsersByAccount(account);
        double leftWeight = 1 - weight;
        for (var elem : accountUserList){
            elem.setWeight(leftWeight * elem.getWeight());
            accountUserRepository.save(elem);
        }
        AccountUser accountUser = new AccountUser();
        accountUser.setWeight(weight);
        accountUser.setPermission(permission);
        accountUser.setAccount(account);
        accountUser.setUser(user);
        return accountUserRepository.save(accountUser);
    }


    public AccountUser getByUserAndAccount(int accountid, int userid){
        return accountUserRepository.findAccountUserByAccountAndUser(accountid,userid);
    }

    @Transactional
    public void removeUser (Account account, User user){
        List<AccountUser> accountUserList = accountUserRepository.findAccountUsersByAccount(account);
        AccountUser removeEntity = accountUserList.stream().filter( accountUser -> accountUser.getUser().equals(user)).findFirst().orElse(null);
        if (removeEntity != null){
            double weight =Math.floor((1 - removeEntity.getWeight())*100)/100;
            accountUserRepository.delete(removeEntity);
            for (var elem : accountUserList){
                if (!elem.equals(removeEntity)) {
                    elem.setWeight( Math.floor(1/weight * elem.getWeight() * 100) / 100);
                    accountUserRepository.save(elem);
                }
            }
        }
    }

    public List<AccountUser> getUsersByAccount (Account account){
        List<AccountUser> accountUsers = accountUserRepository.findAccountUsersByAccount(account);
        return  accountUsers;
    }

    public BigDecimal getSpendOfUserOfAccount(Account account, User user, Date start, Date end, BigDecimal spend){
        AccountUser accountUser = accountUserRepository.findAccountUserByAccountAndUser(account, user);
        double weight = accountUser.getWeight();
        //receiptService.getAllSpends(account, start, end); -- add this to controller level
        return spend.multiply(BigDecimal.valueOf(weight));
    }


    public Map<String, String> getAllPermissions(int userID) {
        return accountUserRepository.returnAllAccessLevels(userID);
    }

    public List<Account> getAccountByUser (User user){
        return accountUserRepository.findAccountByUser(user);
    }

    public List<AccountUser> getByUser(int id) {
        return accountUserRepository.findAccountUsersByUserId(id);
    }
}
