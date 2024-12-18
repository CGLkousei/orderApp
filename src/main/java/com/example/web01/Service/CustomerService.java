package com.example.web01.Service;

import com.example.web01.Class.Customer;
import com.example.web01.Repository.RestaurantRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CustomerService {
    private final Map<Long, Map<Long, Customer>> restaurantCustomers = new HashMap<>();

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
        // `restaurantId`がすでに存在する場合、直接`restaurantCustomers.get(restaurantId)`を使えば良い
        restaurantCustomers.get(restaurantId).put(customer.getSeatId(), customer);
    }

    // 特定のレストランの客リストを取得
    public Map<Long, Customer> getCustomersByRestaurant(long restaurantId) {
        return restaurantCustomers.getOrDefault(restaurantId, new HashMap<>());
    }

    // 特定の席IDの客を取得
    public Customer getCustomer(long restaurantId, long seatId) {
        return restaurantCustomers.getOrDefault(restaurantId, new HashMap<>()).get(seatId);
    }
}
