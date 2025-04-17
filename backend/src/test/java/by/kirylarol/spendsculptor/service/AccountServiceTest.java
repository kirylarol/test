package by.kirylarol.spendsculptor.service;


import by.kirylarol.spendsculptor.entities.Account;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(
        locations = "classpath:application-integrationtest.properties")
public class AccountServiceTest {
    @Autowired
    private AccountService accountService;

    @Test
    public void addAccountTest() {
        Account account = new Account();
        account.setName("Alexander");
        account = accountService.addAccount(account);
        assert (account.getId() != 0);
    }

    @Test
    public void removeAccountTest() {
        Account account = new Account();
        addAccountTest();
        accountService.removeAccount(account);
        assert (accountService.getAccount(account) == null);

    }
}
