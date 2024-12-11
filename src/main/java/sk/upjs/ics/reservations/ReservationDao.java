package sk.upjs.ics.reservations;

import java.io.File;

public interface ReservationDao {

    void loadFromCsv(File file);

    void create(Reservation reservation);

    void delete(Reservation reservation);

    void update(Reservation reservation);

    Reservation findById(Long id);

    Iterable<Reservation> findAll();
}
