package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerIdOrderByStartDesc(Long bookerId);

    @Query(
            "select b " +
            "from Booking b " +
            "   where CURRENT_TIMESTAMP between b.start and b.end " +
            "       and b.booker.id = :bookerId " +
            "order by b.start desc"
    )
    List<Booking> findAllByBookerIdStateCurrent(Long bookerId);

    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime present);

    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime present);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status);

    List<Booking> findAllByItemOwnerIdOrderByStartDesc(Long ownerId);

    @Query(
            "select b " +
            "from Booking b " +
            "   where CURRENT_TIMESTAMP between b.start and b.end " +
            "       and b.item.ownerId = :bookerId " +
            "order by b.start desc"
    )
    List<Booking> findAllByItemOwnerIdStateCurrent(Long bookerId);

    List<Booking> findAllByItemOwnerIdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime present);

    List<Booking> findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime present);

    List<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status);

    List<Booking> findAllByItemIdAndBookerIdAndEndBefore(Long itemId, Long bookerId, LocalDateTime end);

    Optional<Booking> findFirstByItemAndEndBeforeOrderByEndDesc(Item item, LocalDateTime end);

    Optional<Booking> findFirstByItemAndStartAfterOrderByStartDesc(Item item, LocalDateTime end);

}
