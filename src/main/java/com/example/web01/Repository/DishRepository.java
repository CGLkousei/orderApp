package com.example.web01.Repository;

import com.example.web01.Entity.DishEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DishRepository extends JpaRepository<DishEntity, Long> {
    List<DishEntity> findByCategoryId(Long categoryId);
    List<DishEntity> findByCategoryIdIn(List<Long> categoryIds);
}
