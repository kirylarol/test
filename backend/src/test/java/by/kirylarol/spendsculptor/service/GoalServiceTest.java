package by.kirylarol.spendsculptor.service;

import by.kirylarol.spendsculptor.entities.AccountUser;
import by.kirylarol.spendsculptor.entities.Goal;
import by.kirylarol.spendsculptor.entities.Identity;
import by.kirylarol.spendsculptor.entities.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@TestPropertySource(
        locations = "classpath:application-integrationtest.properties")
public class GoalServiceTest {
    @Autowired
    private GoalService goalService;

    @Autowired
    private AccountUserService accountUserService;

    private static User user1;

    @BeforeAll
    public static void init() {
        user1 = new User();
        Identity identity = new Identity();
        identity.setName("Alex");
        identity.setSurname("Ivanov");
        user1.setIdentity(identity);
        user1.setLogin("login");
        user1.setPassword("password");
    }

    static AccountUser accountUser;

    @Test
    public void createGoalTest() {
        accountUser = accountUserService.addAccount("TEST ACCOUNT", user1);
        Goal goal = goalService.createGoal(accountUser.getAccount(), "TEST GOAL", LocalDate.parse("2023-10-09"), LocalDate.parse("2023-11-15"), BigDecimal.valueOf(4000), BigDecimal.valueOf(400));
        assertNotNull(goal);
        List<Goal> list = goalService.takeAllGoals(accountUser.getAccount());
        assertEquals(1, list.size());
    }

    @Test
    public void testInRange() {
        createGoalTest();
        LocalDate date1 = LocalDate.parse("2023-10-08");
        LocalDate date2 = LocalDate.parse("2023-11-13");
        List<Goal> list = goalService.takeGoalValidUntilDate(accountUser.getAccount(), date1);
        List<Goal> list1 = goalService.takeGoalValidUntilDate(accountUser.getAccount(), date2);
        assertEquals(0, list.size());
        assertEquals(1, list1.size());
    }
}