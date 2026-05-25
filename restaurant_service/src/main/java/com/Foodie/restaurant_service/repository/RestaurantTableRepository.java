package com.Foodie.restaurant_service.repository;

import com.Foodie.restaurant_service.entity.RestaurantTable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RestaurantTableRepository extends JpaRepository<RestaurantTable, Long> {

    boolean existsByRestaurantIdAndNumberOfTable(Integer restaurantId, Integer numberOfTable);

    Page<RestaurantTable> findByRestaurantId(Integer restaurantId, Pageable pageable);

    Optional<RestaurantTable> findByRestaurantIdAndNumberOfTable(Integer restaurantId, Integer numberOfTable);


}
