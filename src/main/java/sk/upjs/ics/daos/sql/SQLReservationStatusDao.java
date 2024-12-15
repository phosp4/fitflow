package sk.upjs.ics.daos.sql;

import sk.upjs.ics.daos.interfaces.ReservationStatusDao;
import sk.upjs.ics.entities.ReservationStatus;

import java.sql.Connection;
import java.util.ArrayList;

public class SQLReservationStatusDao implements ReservationStatusDao {

    private final Connection connection;

    public SQLReservationStatusDao(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void create(String status) {

    }

    @Override
    public void delete(String status) {

    }

    @Override
    public void update(String status) {

    }

    @Override
    public ReservationStatus findById(Long id) {
        return null;
    }

    @Override
    public ArrayList<ReservationStatus> findAll() {
        return null;
    }
}
