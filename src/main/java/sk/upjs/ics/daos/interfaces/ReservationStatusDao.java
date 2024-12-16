package sk.upjs.ics.daos.interfaces;

import sk.upjs.ics.entities.ReservationStatus;

import java.io.File;
import java.util.ArrayList;

public interface ReservationStatusDao {

    void loadFromCsv(File file);

    void create(ReservationStatus status);

    void delete(ReservationStatus status);

    void update(ReservationStatus status);

    ReservationStatus findById(Long id);

    ArrayList<ReservationStatus> findAll();
}
