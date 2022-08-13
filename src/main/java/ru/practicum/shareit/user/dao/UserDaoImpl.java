package ru.practicum.shareit.user.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.common.TempStorage;
import ru.practicum.shareit.common.exceptoins.ConflictException;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Objects;

@Repository
public class UserDaoImpl implements UserDao {

    private final TempStorage<User> userStorage = new TempStorage<>();

    // пока проверяю на дубли email здесь, а при работе с настоящей базой это можно переложить на СУБД
    private void checkDuplicateEmail(User user) {
        final Collection<User> users = userStorage.getAll();
        if (users.stream().anyMatch(u -> Objects.equals(u.getEmail(), user.getEmail()) && u.getId() != user.getId())) {
            throw new ConflictException(String.format("Пользователь с email %s уже существует", user.getEmail()));
        }
    }

    @Override
    public Collection<User> getAll() {
        return userStorage.getAll();
    }

    @Override
    public User getById(long userId) {
        return userStorage.getById(userId);
    }

    @Override
    public User create(User user) {
        checkDuplicateEmail(user);

        return userStorage.create(User.of(userStorage.getNext(), user.getName(), user.getEmail()));
    }

    @Override
    public User update(User user) {
        checkDuplicateEmail(user);
        return userStorage.update(user.getId(), user);
    }

    @Override
    public void remove(long userId) {
        userStorage.remove(userId);
    }
}
