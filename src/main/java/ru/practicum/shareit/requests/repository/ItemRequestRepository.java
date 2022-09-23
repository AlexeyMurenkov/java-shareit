package ru.practicum.shareit.requests.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findAllByRequestorOrderByCreated(User requestor);

    @Query(
            "select ir " +
            "from ItemRequest ir " +
            "   where ir.requestor <> :requestor " +
            "order by ir.created"
    )
    Page<ItemRequest> findAllByNotRequestorOrderByCreated(User requestor, Pageable pageable);
}
