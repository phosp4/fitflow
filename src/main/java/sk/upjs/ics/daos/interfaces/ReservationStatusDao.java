package sk.upjs.ics.daos.interfaces;

import sk.upjs.ics.entities.ReservationStatus;

import java.io.File;
import java.util.ArrayList;

/**
 * Interface for ReservationStatus Data Access Object (DAO).
 * Provides methods to perform CRUD operations and load data from a CSV file.
 */
public interface ReservationStatusDao {

    /**
     * Loads reservation statuses from a CSV file.
     *
     * @param file the CSV file containing reservation statuses
     */
    void loadFromCsv(File file);

    /**
     * Creates a new reservation status.
     *
     * @param status the reservation status to create
     */
    void create(ReservationStatus status);

    /**
     * Deletes an existing reservation status.
     *
     * @param status the reservation status to delete
     */
    void delete(ReservationStatus status);

    /**
     * Updates an existing reservation status.
     *
     * @param status the reservation status to update
     */
    void update(ReservationStatus status);

    /**
     * Finds a reservation status by its ID.
     *
     * @param id the ID of the reservation status to find
     * @return the found reservation status, or null if not found
     */
    ReservationStatus findById(Long id);

    /**
     * Finds all reservation statuses.
     *
     * @return a list of all reservation statuses
     */
    ArrayList<ReservationStatus> findAll();
}
