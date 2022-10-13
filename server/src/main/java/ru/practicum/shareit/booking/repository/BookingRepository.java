package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.dto.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBooker(User booker, Pageable pageable);

    @Query(
            "select b " +
            "from Booking b " +
            "   where CURRENT_TIMESTAMP between b.start and b.end " +
            "       and b.booker = :booker"
    )
    List<Booking> findAllByBookerStateCurrent(User booker, Pageable pageable);

    List<Booking> findAllByBookerAndStartAfter(User booker, LocalDateTime present, Pageable pageable);

    List<Booking> findAllByBookerAndEndBefore(User booker, LocalDateTime present, Pageable pageable);

    List<Booking> findAllByBookerAndStatus(User booker, BookingStatus status, Pageable pageable);

    List<Booking> findAllByItemOwnerId(Long ownerId, Pageable pageable);

    @Query(
            "select b " +
            "from Booking b " +
            "   where CURRENT_TIMESTAMP between b.start and b.end " +
            "       and b.item.ownerId = :bookerId"
    )
    List<Booking> findAllByItemOwnerIdStateCurrent(Long bookerId, Pageable pageable);

    List<Booking> findAllByItemOwnerIdAndStartAfter(Long bookerId, LocalDateTime present, Pageable pageable);

    List<Booking> findAllByItemOwnerIdAndEndBefore(Long bookerId, LocalDateTime present, Pageable pageable);

    List<Booking> findAllByItemOwnerIdAndStatus(Long bookerId, BookingStatus status, Pageable pageable);

    Set<Booking> findAllByItemIdAndBookerIdAndEndBefore(Long itemId, Long bookerId, LocalDateTime end);

    Optional<Booking> findFirstByItemAndEndBeforeOrderByEndDesc(Item item, LocalDateTime end);

    Optional<Booking> findFirstByItemAndStartAfterOrderByStartDesc(Item item, LocalDateTime end);

}
