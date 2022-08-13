package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserDao {
    Collection<User> getAll();

    User getById(long id);

    User create(User user);

    User update(User user);

    void remove(long userId);
}