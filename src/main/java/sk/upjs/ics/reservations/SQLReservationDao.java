package sk.upjs.ics.reservations;

import sk.upjs.ics.entities.Reservation;

import java.sql.Connection;

public class SQLReservationDao implements ReservationDao {

    private final Connection connection;

    public SQLReservationDao(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void create(Reservation reservation) {

    }

    @Override
    public void delete(Reservation reservation) {

    }

    @Override
    public void update(Reservation reservation) {

    }

    @Override
    public Reservation findById(Long id) {
        return null;
    }

    @Override
    public Iterable<Reservation> findAll() {
        return null;
    }
}
