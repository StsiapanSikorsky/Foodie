package com.Foodie.restaurant_service.repository;

import com.Foodie.restaurant_service.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Integer>, JpaSpecificationExecutor<Restaurant> {

    Optional<Restaurant> findByIdAndDeletedFalse(Integer id);

    boolean existsByRestaurantName(String restaurantName);

    Optional<Restaurant> findByOwnerId(Integer ownerId);
}
