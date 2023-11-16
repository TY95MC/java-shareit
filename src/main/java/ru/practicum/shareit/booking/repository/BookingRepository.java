package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerIdOrderByStartDesc(Long id);

    List<Booking> findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartAsc(Long id,
                                                                                LocalDateTime start,
                                                                                LocalDateTime end);

    List<Booking> findAllByBookerIdAndEndIsBeforeOrderByStartDesc(Long id, LocalDateTime end);

    List<Booking> findAllByBookerIdAndStartIsAfterOrderByStartDesc(Long id, LocalDateTime end);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long id, String status);

    List<Booking> findAllByItemOwnerIdOrderByStartDesc(Long id);

    List<Booking> findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(Long id,
                                                                                    LocalDateTime start,
                                                                                    LocalDateTime end);

    List<Booking> findAllByItemOwnerIdAndEndIsBeforeOrderByStartDesc(Long id, LocalDateTime end);

    List<Booking> findAllByItemOwnerIdAndStartIsAfterOrderByStartDesc(Long id, LocalDateTime start);

    List<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(Long id, String status);

    Optional<Booking> findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(Long itemId,
                                                                             LocalDateTime start, String status);

    Optional<Booking> findFirstByItemIdAndStartBeforeAndStatusOrderByEndDesc(Long itemId,
                                                                             LocalDateTime start, String status);

    Optional<List<Booking>> findByBookerIdAndItemIdAndEndBefore(Long bookerId, Long itemId, LocalDateTime end);
}
