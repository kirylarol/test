package by.kirylarol.spendsculptor.repos;

import by.kirylarol.spendsculptor.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    Category findDistinctFirstByCategoryName(String name);


    @Query("SELECT DISTINCT  ca  FROM Category ca " +
            "INNER JOIN ca.positions p " +
            "INNER JOIN p.receipt r " +
            "INNER JOIN r.account a " +
            "WHERE a.user.id = :userId")
    List<Category> findAllByUser(int userId);


    @Query("SELECT DISTINCT  ca FROM Category ca " +
            "INNER JOIN  ca.positions p " +
            "INNER JOIN p.receipt r WHERE r.account.id = :accountuserid")
    List<Category> findAllByAccountUser(int accountuserid);


    @Query("SELECT ca  FROM Category ca " +
            "INNER JOIN ca.positions p " +
            "INNER JOIN p.receipt r " +
            "INNER JOIN r.account a " +
            "WHERE a.user.id = :userId AND p.name LIKE :name")
    List<Category> findTopCategoryByNameAndUserId(@Param("name") String name, @Param("userId") int userId);

    Category findByCategoryName(String name);


}
