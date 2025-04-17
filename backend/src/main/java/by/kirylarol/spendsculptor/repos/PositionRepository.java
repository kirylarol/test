package by.kirylarol.spendsculptor.repos;

import by.kirylarol.spendsculptor.entities.Category;
import by.kirylarol.spendsculptor.entities.Position;
import by.kirylarol.spendsculptor.entities.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PositionRepository extends JpaRepository<Position, Integer> {
    List<Position> getAllByReceipt(Receipt id);
    List<Position> getAllByCategory(Category category);

}
