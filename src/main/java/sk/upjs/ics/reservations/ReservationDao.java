package sk.upjs.ics.reservations;

public interface ReservationDao {
    void create(Reservation reservation);

    void delete(Reservation reservation);

    void update(Reservation reservation);

    Reservation findById(Long id);

    Iterable<Reservation> findAll();
}
