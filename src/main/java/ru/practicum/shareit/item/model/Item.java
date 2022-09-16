package ru.practicum.shareit.item.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.requests.model.ItemRequest;

import javax.persistence.*;

@Entity
@Table(name = "items", schema = "public")
@Getter
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String name;
    String description;
    @Column(name = "is_available")
    boolean available;
    @Column(name = "owner_id")
    Long ownerId;
    @ManyToOne
    @JoinColumn(name = "request_id")
    ItemRequest request;
}
