package com.Foodie.restaurant_service.entity;

import com.Foodie.restaurant_service.constants.RestaurantType;
import com.Foodie.restaurant_service.constants.RestaurantWeekends;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import jakarta.persistence.Table;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "restaurants")
@Getter
@Setter
public class Restaurant {

    public static final String RESTAURANT_NAME = "restaurantName";
    public static final String CITY = "city";
    public static final String ADDRESS = "address";
    public static final String TYPE = "type";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "restaurant_name", nullable = false)
    private String restaurantName;

    @Column(name = "description")
    private String description;

    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "address", nullable = false)
    private String address;

    @Enumerated(EnumType.STRING)
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

    @Column(name = "restaurants_weekends", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private List<RestaurantWeekends> restaurantWeekends;

    @Column(name = "deleted")
    private Boolean deleted = false;

    @CreationTimestamp
    @Column(name = "created")
    private LocalDateTime created;

    @UpdateTimestamp
    @Column(name = "updated")
    private LocalDateTime updated;

    @Column(name = "owner_id")
    private Integer ownerId;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RestaurantTable> restaurantTables = new ArrayList<>();

    //TODO:Попытаться связать дни недели и время, ка лучше?
    //TODO:Добавить ссылки на картинки в S3, подключить S3
    //TODO:Добавить оценки
}
