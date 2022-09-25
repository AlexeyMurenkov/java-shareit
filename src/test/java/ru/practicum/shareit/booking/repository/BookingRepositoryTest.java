package ru.practicum.shareit.booking.repository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
class BookingRepositoryTest {

    static LocalDateTime TEST_TIME = LocalDateTime.now();

    UserRepository userRepository;
    ItemRepository itemRepository;
    BookingRepository bookingRepository;

    User testUser1 = User.of(1L, "User 1", "user1@email.ru");
    User testUser2 = User.of(2L, "User 2", "user2@email.ru");

    Item testItem1 = Item.of(1L, "Item 1", "Item 1 description", true, 2L,
            null);
    Item testItem2 = Item.of(2L, "Item 2", "Item 2 description", true, 1L,
            null);

    List<Booking> testBookings = List.of(
            Booking.of(1L, TEST_TIME.plusHours(1), TEST_TIME.plusHours(2), testItem1, testUser1,
                    BookingStatus.APPROVED),
            Booking.of(2L, TEST_TIME.plusHours(1), TEST_TIME.plusHours(2), testItem1, testUser1,
                    BookingStatus.REJECTED),
            Booking.of(3L, TEST_TIME.plusHours(1), TEST_TIME.plusHours(2), testItem1, testUser1,
                    BookingStatus.WAITING),
            Booking.of(4L, TEST_TIME.plusHours(1), TEST_TIME.plusHours(2), testItem1, testUser1,
                    BookingStatus.CANCELED),
            Booking.of(5L, TEST_TIME.minusHours(2), TEST_TIME.minusHours(1), testItem2, testUser1,
                    BookingStatus.APPROVED),
            Booking.of(6L, TEST_TIME.minusHours(2), TEST_TIME.minusHours(1), testItem2, testUser1,
                    BookingStatus.REJECTED),
            Booking.of(7L, TEST_TIME.minusHours(2), TEST_TIME.minusHours(1), testItem2, testUser1,
                    BookingStatus.WAITING),
            Booking.of(8L, TEST_TIME.minusHours(2), TEST_TIME.minusHours(1), testItem2, testUser1,
                    BookingStatus.CANCELED),
            Booking.of(9L, TEST_TIME.minusMinutes(1), TEST_TIME.plusMinutes(1), testItem1, testUser2,
                    BookingStatus.APPROVED),
            Booking.of(10L, TEST_TIME.minusMinutes(1), TEST_TIME.plusMinutes(1), testItem1, testUser2,
                    BookingStatus.REJECTED),
            Booking.of(11L, TEST_TIME.minusMinutes(1), TEST_TIME.plusMinutes(1), testItem1, testUser2,
                    BookingStatus.WAITING),
            Booking.of(12L, TEST_TIME.minusMinutes(1), TEST_TIME.plusMinutes(1), testItem1, testUser2,
                    BookingStatus.CANCELED)
            );

    private static void assertEqualsBookings(Booking booking1, Booking booking2) {
        assertEquals(booking1.getId(), booking2.getId(), "Возвращается неверный id аренды");
        assertEquals(booking1.getStart(), booking2.getStart(), "Возвращается неверное начала аренды");
        assertEquals(booking1.getEnd(), booking2.getEnd(), "Возвращается неверное время окончания аренды");
        assertEquals(booking1.getStatus(), booking2.getStatus(), "Возвращается неверный статус аренды");
    }

    @BeforeEach
    void beforeEachTest() {
        userRepository.save(testUser1);
        userRepository.save(testUser2);
        itemRepository.save(testItem1);
        itemRepository.save(testItem2);
        bookingRepository.saveAll(testBookings);
    }

    @Test
    @DirtiesContext
    void findAllByBooker() {
        final List<Booking> bookingsByBooker1 = bookingRepository.findAllByBooker(testUser1, Pageable.unpaged());
        assertEquals(8, bookingsByBooker1.size(), "Неверное количество аренд для арендатора 1");
        bookingsByBooker1.forEach(
                booking -> assertEqualsBookings(booking, testBookings.get(booking.getId().intValue() - 1))
                );

        final List<Booking> bookingsByBooker2 = bookingRepository.findAllByBooker(testUser2, Pageable.unpaged());
        assertEquals(4, bookingsByBooker2.size(), "Неверное количество аренд для арендатора 2");
        bookingsByBooker2.forEach(
                booking -> assertEqualsBookings(booking, testBookings.get(booking.getId().intValue() - 1))
        );
    }

    @Test
    @DirtiesContext
    void findAllByBookerStateCurrent() {
        final List<Booking> bookingsByBooker1 = bookingRepository.findAllByBookerStateCurrent(testUser1,
                Pageable.unpaged());
        assertTrue(bookingsByBooker1.isEmpty(), "Для арендатора 1 возвращается непустой список текущих аренд");
        final List<Booking> bookingsByBooker2 = bookingRepository.findAllByBookerStateCurrent(testUser2,
                Pageable.unpaged());
        assertEquals(4, bookingsByBooker2.size(), "Неверное количество текущих аренд для арендатора 2");
        bookingsByBooker2.forEach(
                booking -> assertEqualsBookings(booking, testBookings.get(booking.getId().intValue() - 1))
        );
    }

    @Test
    @DirtiesContext
    void findAllByBookerAndStartAfter() {
        final List<Booking> bookingsByBooker2 = bookingRepository.findAllByBookerAndStartAfter(testUser2, TEST_TIME,
                Pageable.unpaged());
        assertTrue(bookingsByBooker2.isEmpty(), "Для арендатора 2 возвращается непустой список будущих аренд");
        final List<Booking> bookingsByBooker1 = bookingRepository.findAllByBookerAndStartAfter(testUser1, TEST_TIME,
                Pageable.unpaged());
        assertEquals(4, bookingsByBooker1.size(), "Неверное количество будущих аренд для арендатора 1");
        bookingsByBooker1.forEach(
                booking -> assertEqualsBookings(booking, testBookings.get(booking.getId().intValue() - 1))
        );
    }

    @Test
    @DirtiesContext
    void findAllByBookerAndEndBefore() {
        final List<Booking> bookingsByBooker2 = bookingRepository.findAllByBookerAndEndBefore(testUser2, TEST_TIME,
                Pageable.unpaged());
        assertTrue(bookingsByBooker2.isEmpty(), "Для арендатора 2 возвращается непустой список прошлых аренд");
        final List<Booking> bookingsByBooker1 = bookingRepository.findAllByBookerAndEndBefore(testUser1, TEST_TIME,
                Pageable.unpaged());
        assertEquals(4, bookingsByBooker1.size(), "Неверное количество прошлых аренд для арендатора 1");
        bookingsByBooker1.forEach(
                booking -> assertEqualsBookings(booking, testBookings.get(booking.getId().intValue() - 1))
        );
    }

    @Test
    @DirtiesContext
    void findAllByBookerAndStatus() {
        final List<Booking> bookingsByBooker1AndWaiting = bookingRepository.findAllByBookerAndStatus(testUser1,
                BookingStatus.WAITING, Pageable.unpaged());
        assertEquals(2, bookingsByBooker1AndWaiting.size(),
                "Неверное количество аренд в статусе WAITING");
        bookingsByBooker1AndWaiting.forEach(
                booking -> assertEqualsBookings(booking, testBookings.get(booking.getId().intValue() - 1))
        );
        final List<Booking> bookingsByBooker1AndCanceled = bookingRepository.findAllByBookerAndStatus(testUser1,
                BookingStatus.CANCELED, Pageable.unpaged());
        assertEquals(2, bookingsByBooker1AndCanceled.size(),
                "Неверное количество аренд в статусе CANCELED");
        bookingsByBooker1AndCanceled.forEach(
                booking -> assertEqualsBookings(booking, testBookings.get(booking.getId().intValue() - 1))
        );
        final List<Booking> bookingsByBooker2AndApproved = bookingRepository.findAllByBookerAndStatus(testUser2,
                BookingStatus.APPROVED, Pageable.unpaged());
        assertEquals(1, bookingsByBooker2AndApproved.size(),
                "Неверное количество аренд в статусе APPROVED");
        bookingsByBooker2AndApproved.forEach(
                booking -> assertEqualsBookings(booking, testBookings.get(booking.getId().intValue() - 1))
        );
        final List<Booking> bookingsByBooker2AndRejected = bookingRepository.findAllByBookerAndStatus(testUser2,
                BookingStatus.REJECTED, Pageable.unpaged());
        assertEquals(1, bookingsByBooker2AndRejected.size(),
                "Неверное количество аренд в статусе REJECTED");
        bookingsByBooker2AndRejected.forEach(
                booking -> assertEqualsBookings(booking, testBookings.get(booking.getId().intValue() - 1))
        );
    }

    @Test
    @DirtiesContext
    void findAllByItemOwnerId() {
        final List<Booking> bookingsByOwner1 = bookingRepository.findAllByItemOwnerId(1L, Pageable.unpaged());
        assertEquals(4, bookingsByOwner1.size(), "Неверное количество аренд для владельца 1");
        bookingsByOwner1.forEach(
                booking -> assertEqualsBookings(booking, testBookings.get(booking.getId().intValue() - 1))
        );
        final List<Booking> bookingsByOwner2 = bookingRepository.findAllByItemOwnerId(2L, Pageable.unpaged());
        assertEquals(8, bookingsByOwner2.size(), "Неверное количество аренд для владельца 2");
        bookingsByOwner2.forEach(
                booking -> assertEqualsBookings(booking, testBookings.get(booking.getId().intValue() - 1))
        );
    }

    @Test
    @DirtiesContext
    void findAllByItemOwnerIdStateCurrent() {
        final List<Booking> bookingsByOwner1 = bookingRepository.findAllByItemOwnerIdStateCurrent(1L,
                Pageable.unpaged());
        assertTrue(bookingsByOwner1.isEmpty(), "Для владельца 1 возвращается непустой список текущих аренд");
        final List<Booking> bookingsByOwner2 = bookingRepository.findAllByItemOwnerIdStateCurrent(2L,
                Pageable.unpaged());
        assertEquals(4, bookingsByOwner2.size(), "Неверное количество текущих аренд для владельца 2");
        bookingsByOwner2.forEach(
                booking -> assertEqualsBookings(booking, testBookings.get(booking.getId().intValue() - 1))
        );
    }

    @Test
    @DirtiesContext
    void findAllByItemOwnerIdAndStartAfter() {
        final List<Booking> bookingsByOwner1 = bookingRepository.findAllByItemOwnerIdAndStartAfter(1L,
                TEST_TIME, Pageable.unpaged());
        assertTrue(bookingsByOwner1.isEmpty(), "Для владельца 1 возвращается непустой список будущих аренд");
        final List<Booking> bookingsByOwner2 = bookingRepository.findAllByItemOwnerIdAndStartAfter(2L,
                TEST_TIME, Pageable.unpaged());
        assertEquals(4, bookingsByOwner2.size(), "Неверное количество будущих аренд для владельца 1");
        bookingsByOwner2.forEach(
                booking -> assertEqualsBookings(booking, testBookings.get(booking.getId().intValue() - 1))
        );
    }

    @Test
    @DirtiesContext
    void findAllByItemOwnerIdAndEndBefore() {
        final List<Booking> bookingsByOwner2 = bookingRepository.findAllByItemOwnerIdAndEndBefore(2L,
                TEST_TIME, Pageable.unpaged());
        assertTrue(bookingsByOwner2.isEmpty(), "Для арендатора 2 возвращается непустой список прошлых аренд");
        final List<Booking> bookingsByOwner1 = bookingRepository.findAllByItemOwnerIdAndEndBefore(1L,
                TEST_TIME, Pageable.unpaged());
        assertEquals(4, bookingsByOwner1.size(), "Неверное количество прошлых аренд для арендатора 1");
        bookingsByOwner1.forEach(
                booking -> assertEqualsBookings(booking, testBookings.get(booking.getId().intValue() - 1))
        );
    }

    @Test
    @DirtiesContext
    void findAllByItemOwnerIdAndStatus() {
        final List<Booking> bookingsByOwner1AndWaiting = bookingRepository.findAllByItemOwnerIdAndStatus(1L,
                BookingStatus.WAITING, Pageable.unpaged());
        assertEquals(1, bookingsByOwner1AndWaiting.size(),
                "Неверное количество аренд в статусе WAITING");
        bookingsByOwner1AndWaiting.forEach(
                booking -> assertEqualsBookings(booking, testBookings.get(booking.getId().intValue() - 1))
        );
        final List<Booking> bookingsByOwner1AndCanceled = bookingRepository.findAllByItemOwnerIdAndStatus(1L,
                BookingStatus.CANCELED, Pageable.unpaged());
        assertEquals(1, bookingsByOwner1AndCanceled.size(),
                "Неверное количество аренд в статусе CANCELED");
        bookingsByOwner1AndCanceled.forEach(
                booking -> assertEqualsBookings(booking, testBookings.get(booking.getId().intValue() - 1))
        );
        final List<Booking> bookingsByOwner2AndApproved = bookingRepository.findAllByItemOwnerIdAndStatus(2L,
                BookingStatus.APPROVED, Pageable.unpaged());
        assertEquals(2, bookingsByOwner2AndApproved.size(),
                "Неверное количество аренд в статусе APPROVED");
        bookingsByOwner2AndApproved.forEach(
                booking -> assertEqualsBookings(booking, testBookings.get(booking.getId().intValue() - 1))
        );
        final List<Booking> bookingsByOwner2AndRejected = bookingRepository.findAllByItemOwnerIdAndStatus(2L,
                BookingStatus.REJECTED, Pageable.unpaged());
        assertEquals(2, bookingsByOwner2AndRejected.size(),
                "Неверное количество аренд в статусе REJECTED");
        bookingsByOwner2AndRejected.forEach(
                booking -> assertEqualsBookings(booking, testBookings.get(booking.getId().intValue() - 1))
        );
    }

    @Test
    @DirtiesContext
    void findAllByItemIdAndBookerIdAndEndBefore() {
        final List<Booking> bookingsByBooker2 = bookingRepository.findAllByItemIdAndBookerIdAndEndBefore(1L,
                2L, TEST_TIME);
        assertTrue(bookingsByBooker2.isEmpty(),
                "Для вещи 2 арендатора 2 возвращается непустой список прошлых аренд");
        final List<Booking> bookingsByBooker1 = bookingRepository.findAllByItemIdAndBookerIdAndEndBefore(2L,
                1L, TEST_TIME);
        assertEquals(4, bookingsByBooker1.size(),
                "Неверное количество прошлых аренд для веши 2 для арендатора 1");
        bookingsByBooker1.forEach(
                booking -> assertEqualsBookings(booking, testBookings.get(booking.getId().intValue() - 1))
        );
    }

    @Test
    @DirtiesContext
    void findFirstByItemAndEndBeforeOrderByEndDesc() {
        final Optional<Booking> booking = bookingRepository.findFirstByItemAndEndBeforeOrderByEndDesc(testItem2, TEST_TIME);
        assertTrue(booking.isPresent(), "Возвращается пустая аренда");
        assertEqualsBookings(testBookings.get(4), booking.get());
    }

    @Test
    @DirtiesContext
    void findFirstByItemAndStartAfterOrderByStartDesc() {
        final Optional<Booking> booking = bookingRepository.findFirstByItemAndStartAfterOrderByStartDesc(testItem1, TEST_TIME);
        assertTrue(booking.isPresent(), "Возвращается пустая аренда");
        assertEqualsBookings(testBookings.get(0), booking.get());
    }
}