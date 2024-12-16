package sk.upjs.ics.daos.interfaces;

import sk.upjs.ics.entities.Reservation;

import java.io.File;
import java.util.ArrayList;

/**
 * Interface for Reservation Data Access Object (DAO).
 * Provides methods to perform CRUD operations and load data from a CSV file.
 */
public interface ReservationDao {

    /**
     * Loads reservations from a CSV file.
     *
     * @param file the CSV file containing reservations
     */
    void loadFromCsv(File file);

    /**
     * Creates a new reservation.
     *
     * @param reservation the reservation to create
     */
    void create(Reservation reservation);

    /**
     * Deletes an existing reservation.
     *
     * @param reservation the reservation to delete
     */
    void delete(Reservation reservation);

    /**
     * Updates an existing reservation.
     *
     * @param reservation the reservation to update
     */
    void update(Reservation reservation);

    /**
     * Finds a reservation by its ID.
     *
     * @param id the ID of the reservation to find
     * @return the found reservation, or null if not found
     */
    Reservation findById(Long id);

    /**
     * Finds all reservations.
     *
     * @return a list of all reservations
     */
    ArrayList<Reservation> findAll();

    /**
     * Finds all reservations of a specific user.
     *
     * @param userId the ID of the user whose reservations to find
     * @return a list of reservations of the specified user
     */
    ArrayList<Reservation> findAllOfOneUser(Long userId);
}
