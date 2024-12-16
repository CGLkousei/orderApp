package com.example.web01.Repository;

import com.example.web01.Entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
    List<CategoryEntity> findByRestaurantId(Long restaurantId);
}
