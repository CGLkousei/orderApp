package com.example.web01.Repository;

import com.example.web01.Entity.SeatEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface SeatRepository extends JpaRepository<SeatEntity, Long> {
    Optional<SeatEntity> findByIdAndRestaurantId(Long id, Long restaurant_id);
    List<SeatEntity> findByRestaurantId(Long restaurantId);
}
