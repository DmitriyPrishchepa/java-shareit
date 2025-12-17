package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.util.BookingStateSearchParams;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Override
    <S extends Booking> S save(S entity);

    @Override
    Booking getReferenceById(Long aLong);

    List<Booking> findAllByItemIdIn(List<Long> itemIds);

    List<Booking> findAllByBookerId(Long bookerId);

    List<Booking> findAllByItemOwnerId(Long ownerId);

    List<Booking> findAllByItemId(Long itemId);

    @Query(
            "select b " +
                    "from Booking b " +
                    "where b.item.id = :itemId"
    )
    Booking findByItemId(@Param("itemId") Long itemId);

    @Query("select b " +
            "from Booking b " +
            "where b.booker.id = :bookerId " +
            "and b.end < :currentDate " +
            "order by b.start desc")
    List<Booking> findPastBookingsByBookerId(
            @Param("bookerId") Long bookerId,
            @Param("currentDate") LocalDateTime currentDate
    );

    @Query("select b " +
            "from Booking b " +
            "where b.booker.id = :bookerId " +
            "and b.start <= :currentDate " +
            "and b.end >= :currentDate " +
            "order by b.start desc")
    List<Booking> findCurrentBookingsByBookerId(
            @Param("bookerId") Long bookerId,
            @Param("currentDate") LocalDateTime currentDate
    );

    @Query("select b " +
            "from Booking b " +
            "where b.booker.id = :bookerId " +
            "and b.start > :currentDate " +
            "order by b.start desc")
    List<Booking> findFutureBookingsByBookerId(
            @Param("bookerId") Long bookerId,
            @Param("currentDate") LocalDateTime currentDate
    );

    @Query(
            "select b " +
                    "from Booking b " +
                    "where b.booker.id = :bookerId " +
                    "and status = :status " +
                    "order by b.start desc"
    )
    List<Booking> findAllByBookerIdAndStatus(
            @Param("bookerId") Long bookerId,
            @Param("status") BookingStateSearchParams status);

    //-----------------------------------------------------

    @Query("select b " +
            "from Booking b " +
            "where b.item.ownerId = :ownerId " +
            "and b.end < :currentDate " +
            "order by b.start desc")
    List<Booking> findPastBookingsByOwnerId(
            @Param("ownerId") Long ownerId,
            @Param("currentDate") LocalDateTime currentDate);

    @Query("select b " +
            "from Booking b " +
            "where b.item.ownerId = :ownerId " +
            "and b.start <= :currentDate " +
            "and b.end >= :currentDate " +
            "order by b.start desc")
    List<Booking> findCurrentBookingsByOwnerId(
            @Param("ownerId") Long ownerId,
            @Param("currentDate") LocalDateTime currentDate);

    @Query("select b " +
            "from Booking b " +
            "where b.item.ownerId = :ownerId " +
            "and b.start > :currentDate " +
            "order by b.start desc")
    List<Booking> findFutureBookingsByOwnerId(
            @Param("ownerId") Long ownerId,
            @Param("currentDate") LocalDateTime currentDate);

    @Query(
            "select b " +
                    "from Booking b " +
                    "where b.item.ownerId = :ownerId " +
                    "and status = :status " +
                    "order by b.start desc"
    )
    List<Booking> findAllByOwnerIdAndStatus(
            @Param("ownerId") Long ownerId,
            @Param("status") BookingStateSearchParams status);
}
