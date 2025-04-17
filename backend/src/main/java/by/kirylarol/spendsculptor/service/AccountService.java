package by.kirylarol.spendsculptor.service;


import by.kirylarol.spendsculptor.entities.Account;
import by.kirylarol.spendsculptor.repos.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional (readOnly = true)
@Service
public class AccountService {
    private AccountRepository accountRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }


    public Account getAccount (int id){
        return accountRepository.findById(id).orElse(null);
    }
    @Transactional
    public Account addAccount (Account account) {
       return accountRepository.save(account);
    }

    @Transactional
    public void removeAccount (Account account){
        accountRepository.delete(account);
    }

    public Account getAccount (Account account){
        return accountRepository.findById(account.getId()).orElse(null);
    }

    public List<Account> getAll() {
        return accountRepository.findAll();
    }
}
