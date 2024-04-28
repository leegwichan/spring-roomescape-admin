package roomescape.console.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import roomescape.domain.Reservation;
import roomescape.domain.ReservationRepository;
import roomescape.domain.ReservationTime;
import roomescape.domain.ReservationTimeRepository;

public class MemoryReservationRepository implements ReservationRepository {

    private final List<Reservation> repository = new ArrayList<>();
    private final AtomicLong autoIncreaseId = new AtomicLong(1L);
    private final ReservationTimeRepository reservationTimeRepository;

    public MemoryReservationRepository(ReservationTimeRepository reservationTimeRepository) {
        this.reservationTimeRepository = reservationTimeRepository;
    }

    @Override
    public Optional<Reservation> findById(Long id) {
        return repository.stream()
                .filter(reservation -> reservation.id().equals(id))
                .findAny();
    }

    @Override
    public List<Reservation> findAll() {
        return List.copyOf(repository);
    }

    @Override
    public Reservation create(Reservation reservation) {
        Long timeId = reservation.time().id();
        ReservationTime reservationTime = reservationTimeRepository.findById(timeId).orElseThrow();

        Long newReservationId = autoIncreaseId.getAndIncrement();
        Reservation newReservation = new Reservation(
                newReservationId, reservation.name(), reservation.date(), reservationTime);

        repository.add(newReservation);
        return newReservation;
    }

    @Override
    public void deleteById(Long id) {
        findById(id).ifPresent(repository::remove);
    }

}
