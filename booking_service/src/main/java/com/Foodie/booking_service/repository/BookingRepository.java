package com.Foodie.booking_service.repository;

import com.Foodie.booking_service.entity.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long>, JpaSpecificationExecutor<Booking> {

    @Query("SELECT COUNT(b) > 0 FROM Booking b " +
            "WHERE b.restaurantId = :restaurantId " +
            "AND b.tableNumber = :tableNumber " +
            "AND b.status != 'CANCELED' " +
            "AND ((b.bookingFrom < :to AND b.bookingTo > :from))")
    boolean existsConflictingBooking(
            @Param ("restaurantId") Integer restaurantId,
            @Param("tableNumber") Integer tableNumber,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
            );

    @Query("SELECT COUNT(b) > 0 FROM Booking b " +
            "WHERE b.restaurantId = :restaurantId " +
            "AND b.tableNumber = :tableNumber " +
            "AND b.id != :excludeId " +
            "AND b.status != 'CANCELED' " +
            "AND b.bookingFrom < :to " +
            "AND b.bookingTo > :from")
    boolean existsConflictingBookingExcludingId(
            @Param("restaurantId") Integer restaurantId,
            @Param("tableNumber") Integer tableNumber,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            @Param("excludeId") Long excludeId
    );

    Page<Booking> findAllByRestaurantId(Integer restaurantId, Pageable pageable);

    Page<Booking> findAllByUserId(Integer userid, Pageable pageable);


}
