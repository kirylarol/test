package by.kirylarol.spendsculptor.repos;

import by.kirylarol.spendsculptor.entities.IncomeCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IncomeCategoryRepository extends JpaRepository<IncomeCategory, Integer> {
    
    Optional<IncomeCategory> findByName(String name);
    
    boolean existsByName(String name);
    
    List<IncomeCategory> findAllByOrderByNameAsc();
}
