package sk.upjs.ics.daos.interfaces;

import sk.upjs.ics.entities.ReservationStatus;

public interface ReservationStatusDao {

    void create(String status);

    void delete(String status);

    void update(String status);

    ReservationStatus findById(Long id);

    Iterable<ReservationStatus> findAll();
}
