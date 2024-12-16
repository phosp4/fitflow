package sk.upjs.ics.daos.interfaces;

import sk.upjs.ics.entities.ReservationStatus;

import java.util.ArrayList;

public interface ReservationStatusDao {

    void create(String status);

    void delete(String status);

    void update(String status);

    ReservationStatus findById(Long id);

    ArrayList<ReservationStatus> findAll();
}
