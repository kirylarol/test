package by.kirylarol.spendsculptor.repos;

import by.kirylarol.spendsculptor.entities.CategoryLimitNotification;
import by.kirylarol.spendsculptor.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryLimitNotificationRepository extends JpaRepository<CategoryLimitNotification, Integer> {
    
    List<CategoryLimitNotification> findByUserOrderByCreatedAtDesc(User user);
    
    List<CategoryLimitNotification> findByUserAndIsReadOrderByCreatedAtDesc(User user, Boolean isRead);
    
    long countByUserAndIsRead(User user, Boolean isRead);
}
