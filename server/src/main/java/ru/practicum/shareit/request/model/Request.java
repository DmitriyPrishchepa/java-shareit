package ru.practicum.shareit.request.model;

import jakarta.persistence.*;
import lombok.Data;
import ru.practicum.shareit.item.model.Item;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "requests")
public class Request implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String description;
    private LocalDateTime created;

    @Column(name = "owner_id")
    private Long ownerId;

    @OneToMany(mappedBy = "request")
    private List<Response> responses;

    @OneToMany(mappedBy = "request")
    private List<Item> items;
}
