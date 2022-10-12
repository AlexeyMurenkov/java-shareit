package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.Set;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    Set<ItemRequest> findAllByRequestorOrderByCreated(User requestor);

    @Query(
            "select ir " +
            "from ItemRequest ir " +
            "   where ir.requestor <> :requestor " +
            "order by ir.created"
    )
    Page<ItemRequest> findAllByNotRequestorOrderByCreated(User requestor, Pageable pageable);
}
