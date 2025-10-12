package ru.practicum.shareit.booking.model;

import lombok.Data;

import java.util.Date;

@Data
public class Booking {
    private long id; //id брони
    private Date startDate; //дата начала бронирования
    private Date finishDate; //дата окончания бронирования
    private String status; //статус бронирования
    private long ownerId; //id владельца вещи
    private long userId; //id пользователя, который арендует
    private long itemId; //id вещи, которую берут в аренду
    private int rentalPrice; //стоимость аренды
}
