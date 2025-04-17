package by.kirylarol.spendsculptor.repos;

import by.kirylarol.spendsculptor.entities.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShopRepository extends JpaRepository<Shop, Integer> {
    List<Shop> findByName(String name);
}
