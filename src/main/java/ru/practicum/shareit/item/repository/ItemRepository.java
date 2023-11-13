package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;


@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByOwnerId(Long userId);

//    @Query(value = "SELECT * FROM items AS i " +
//            "LEFT OUTER JOIN users AS u ON i.owner_id = u.id " +
//            "WHERE i.name LIKE % ?1 % OR i.description LIKE %?1% " +
//            "AND i.available = true", nativeQuery = true)
//    List<Item> search(String text);
//    К сожалению этот метод не работает. Выдает ошибку "Сравнение массива (ARRAY) со скалярным значением
//    Values of types "CHARACTER VARYING" and "BOOLEAN" are not comparable" Я это никак исправить не смог

    List<Item> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String str, String str1);
}
