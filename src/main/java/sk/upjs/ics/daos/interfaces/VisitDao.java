package sk.upjs.ics.daos.interfaces;

import sk.upjs.ics.entities.Visit;

import java.io.File;
import java.util.ArrayList;

/**
 * Interface for Visit Data Access Object (DAO).
 * Provides methods to perform CRUD operations and load data from a CSV file.
 */
public interface VisitDao {

    /**
     * Loads visits from a CSV file.
     *
     * @param file the CSV file containing visits
     */
    void loadFromCsv(File file);

    /**
     * Creates a new visit.
     *
     * @param visit the visit to create
     */
    void create(Visit visit);

    /**
     * Deletes an existing visit by its ID.
     *
     * @param id the ID of the visit to delete
     */
    void delete(Visit id);

    /**
     * Updates an existing visit.
     *
     * @param visit the visit to update
     */
    void update(Visit visit);

    /**
     * Finds a visit by its ID.
     *
     * @param id the ID of the visit to find
     * @return the found visit, or null if not found
     */
    Visit findById(Long id);

    /**
     * Finds all visits.
     *
     * @return a list of all visits
     */
    ArrayList<Visit> findAll();

    Visit findByVisitSecret(String uid);
}