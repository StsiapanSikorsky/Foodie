package com.Foodie.booking_service.entity;

import com.Foodie.booking_service.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "booking")
@Getter
@Setter
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "restaurant_id", nullable = false)
    private Integer restaurantId;

    @Column(name = "table_number", nullable = false)
    private Integer tableNumber;

    @Column(name = "guests", nullable = false)
    private Integer guests;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private BookingStatus status;

    @Column(name = "description")
    private String description;

    @Column(name = "booking_from", nullable = false)
    private LocalDateTime bookingFrom;

    @Column(name = "booking_to", nullable = false)
    private LocalDateTime bookingTo;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
