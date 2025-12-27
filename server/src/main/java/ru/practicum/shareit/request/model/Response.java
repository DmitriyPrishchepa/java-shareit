package ru.practicum.shareit.request.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@Entity
@Table(name = "responses")
@JsonIgnoreProperties({"request"})
public class Response implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name = "request_id")
    private Request request;

    @Column(name = "item_id")
    private long itemId;

    @Column(name = "item_name")
    private String name;

    @Column(name = "owner_id")
    private long ownerId;
}
