package sk.upjs.ics.daos.interfaces;

import sk.upjs.ics.entities.Reservation;

import java.io.File;
import java.util.ArrayList;

public interface ReservationDao {

    void loadFromCsv(File file);

    void create(Reservation reservation);

    void delete(Reservation reservation);

    void update(Reservation reservation);

    Reservation findById(Long id);

    ArrayList<Reservation> findAll();
}
