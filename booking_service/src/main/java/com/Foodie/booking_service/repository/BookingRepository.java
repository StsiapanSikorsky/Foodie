package com.Foodie.booking_service.repository;

import com.Foodie.booking_service.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {


    //TODO: Учитывать проверку статусов
    @Query("SELECT COUNT(b) > 0 FROM Booking b " +
            "WHERE b.restaurantId = :restaurantId " +
            "AND b.tableNumber = :tableNumber " +
            "AND ((b.bookingFrom < :to AND b.bookingTo > :from))")
    boolean existsConflictingBooking(
            @Param ("restaurantId") Integer restaurantId,
            @Param("tableNumber") Integer tableNumber,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
            );
}
