package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.requests.model.ItemRequest;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByOwnerId(Long ownerId, Pageable pageable);

    List<Item> findAllByRequest(ItemRequest itemRequest);

    @Query(
            "select i " +
            "from Item i " +
            "   where (upper(i.name) like upper(concat('%', ?1, '%')) " +
            "       or upper(i.description) like upper(concat('%', ?1, '%'))) " +
            "       and i.available = true"
    )
    List<Item> searchSubstring(String substring, Pageable pageable);
}
