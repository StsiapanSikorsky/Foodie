package com.Foodie.restaurant_service.entity;

import com.Foodie.restaurant_service.constants.RestaurantType;
import com.Foodie.restaurant_service.constants.RestaurantWeekends;
import jakarta.persistence.*;
import jdk.jfr.Enabled;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Entity
@Table(name = "restaurants")
@Getter
@Setter
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "restaurant_name")
    private String restaurantName;

    @Column(name = "city")
    private String city;

    @Column(name = "address")
    private String address;

    @Enumerated()
    @Column(name = "type")
    private RestaurantType type;

    @Column(name = "work_from_at_weekend")
    private LocalTime workFromAtWeekend;

    @Column(name = "work_to_at_weekend")
    private LocalTime workToAtWeekend;

    @Column(name = "work_from_at_workday")
    private LocalTime workFromAtWorkday;

    @Column(name = "work_to_at_workday")
    private LocalTime workToAtWorkday;

    @Column(name = "restaurants_weekends")
    private RestaurantWeekends restaurantWeekends;

    //TODO:Попытаться связать дни недели и время, ка лучше?
}
