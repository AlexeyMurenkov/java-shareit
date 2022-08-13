package ru.practicum.shareit.common;

import ru.practicum.shareit.common.exceptoins.BadRequestException;
import ru.practicum.shareit.common.exceptoins.NotFoundException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/*
Временный класс для CRUD-операций в памяти c эмуляцией инкрементного присвоения ID.
До тех пор, пока не готово хранение в БД
 */
public class TempStorage<T> {

    private final ModelValidator<T> modelValidator = new ModelValidator<>();
    private final Map<Long, T> storage = new HashMap<>();
    private long counter = 0;

    public long getNext() {
        return counter + 1;
    }
    public Collection<T> getAll() {
        return storage.values();
    }

    public T getById(long id) {
        if (storage.containsKey(id)) {
            return storage.get(id);
        }
        throw new NotFoundException(String.format("Объект с id %s не найден", id));
    }

    public T create(T t) {
        modelValidator.apply(t);
        storage.put(++counter, t);
        return storage.get(counter);
    }

    public T update(long id, T t) {
        if (storage.containsKey(id)) {
            modelValidator.apply(t);
            storage.put(id, t);
            return t;
        }
        throw new BadRequestException("Попытка обновить несуществующий объект");
    }

    public void remove(long id) {
        if (storage.remove(id) == null) {
            throw new BadRequestException("Попытка удалить несуществующий объект");
        }
    }
}