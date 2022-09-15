package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.requests.model.ItemRequest;

import javax.persistence.*;

@Entity
@Table(name = "items", schema = "public")
@Getter
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    @Column(name = "is_available")
    private boolean available;
    @Column(name = "owner_id")
    private Long ownerId;
    @ManyToOne
    @JoinColumn(name = "request_id")
    private ItemRequest request;
}
