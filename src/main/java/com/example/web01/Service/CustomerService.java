package com.example.web01.Service;

import com.example.web01.Class.Customer;
import com.example.web01.Repository.RestaurantRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CustomerService {
    private final Map<Long, Map<Integer, Customer>> restaurantCustomers = new HashMap<>();

    @Autowired
    private RestaurantRepository restaurantRepository;

    // アプリケーション起動時にrestaurantCustomersを初期化
    @PostConstruct
    public void initializeRestaurantCustomers() {
        // Restaurantテーブルから全てのレストランを取得
        restaurantRepository.findAll().forEach(restaurant -> {
            // 各レストランIDごとに空のMapを初期化
            restaurantCustomers.put(restaurant.getId(), new HashMap<>());
        });
    }

    // 客を追加
    public void addCustomer(long restaurantId, Customer customer) {
        restaurantCustomers.putIfAbsent(restaurantId, new HashMap<>());
        restaurantCustomers.get(restaurantId).put(customer.getSeatId(), customer);
    }

    // 特定のレストランの客リストを取得
    public Map<Integer, Customer> getCustomersByRestaurant(long restaurantId) {
        return restaurantCustomers.getOrDefault(restaurantId, new HashMap<>());
    }

    // 特定の席IDの客を取得
    public Customer getCustomer(long restaurantId, long seatId) {
        return restaurantCustomers.getOrDefault(restaurantId, new HashMap<>()).get((int)seatId);
    }

    //特定の席IDの客を初期化
    public void initializeCustomer(long restaurantId, long seatId){
        if(restaurantCustomers.containsKey(restaurantId)){
            if(restaurantCustomers.get(restaurantId).containsKey((int)seatId)){
                restaurantCustomers.get(restaurantId).remove((int)seatId);
            }
        }
    }
}
