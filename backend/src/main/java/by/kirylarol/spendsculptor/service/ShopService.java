package by.kirylarol.spendsculptor.service;


import by.kirylarol.spendsculptor.entities.Shop;
import by.kirylarol.spendsculptor.repos.ShopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional (readOnly = true)
public class ShopService {
    private final ShopRepository shopRepository;
    @Autowired
    public ShopService(ShopRepository shopRepository) {
        this.shopRepository = shopRepository;
    }

    @Transactional
    public Shop addShop (String name){
        List<Shop> list= shopRepository.findByName(name);
        if (!list.isEmpty())return list.get(0);
        Shop shop = new Shop();
        shop.setName(name);
        return shopRepository.save(shop);
    }

    @Transactional
    public Shop updateShop (Shop shop, String name){
        shop.setName(name);
        return shopRepository.save(shop);
    }

    @Transactional
    public void deleteShop (Shop shop){
        shopRepository.delete(shop);
    }

    public List<Shop> getAll(){
       return shopRepository.findAll();
    }

}
