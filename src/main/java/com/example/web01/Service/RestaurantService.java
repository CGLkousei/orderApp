package com.example.web01.Service;

import com.example.web01.Entity.RestaurantEntity;
import com.example.web01.Repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RestaurantService {
    @Autowired
    private RestaurantRepository restaurantRepository;

    //新しいレストランを保存
    public RestaurantEntity saveRestaurant(RestaurantEntity restaurant){
        return restaurantRepository.save(restaurant);   // save() メソッドで保存
    }

    // IDでレストランを検索
    public Optional<RestaurantEntity> getRestaurantById(Long id) {
        return restaurantRepository.findById(id);  // findById() メソッドで検索
    }

    // 全レストランを取得
    public List<RestaurantEntity> getAllRestaurants() {
        return restaurantRepository.findAll();  // findAll() メソッドで全件取得
    }

    // IDでレストランを削除
    public void deleteRestaurantById(Long id) {
        restaurantRepository.deleteById(id);  // deleteById() メソッドで削除
    }
}
