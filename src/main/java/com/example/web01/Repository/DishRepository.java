package com.example.web01.Repository;

import com.example.web01.Entity.DishEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DishRepository extends JpaRepository<DishEntity, Long> {
}
