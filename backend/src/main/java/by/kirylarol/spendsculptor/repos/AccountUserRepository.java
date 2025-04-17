package by.kirylarol.spendsculptor.repos;

import by.kirylarol.spendsculptor.entities.Account;
import by.kirylarol.spendsculptor.entities.AccountUser;
import by.kirylarol.spendsculptor.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Repository
public interface AccountUserRepository extends JpaRepository<AccountUser, Integer> {
    List<AccountUser> findAccountUsersByAccount(Account account);


    @Query("SELECT ua.user FROM AccountUser  ua WHERE ua.account = :account")
    List<User> findUserByAccount(@Param("account")Account account);

    @Query ("SELECT ua.account FROM AccountUser ua WHERE ua.user = :user")
    List<Account> findAccountByUser(@Param("user")User user);

    AccountUser findAccountUserByAccountAndUser(Account accountid, User userid);

    @Query ("SELECT ua FROM AccountUser ua WHERE ua.account.id = :accountid AND ua.user.id = :userid")
    AccountUser findAccountUserByAccountAndUser(int accountid, int userid);


    @Query ("SELECT (au.account.id, au.permission) FROM AccountUser au WHERE au.user.id = :userid")
    Map<String,String> returnAllAccessLevels (int userid);



    List<AccountUser> findAccountUsersByUserId(int userid);
}
