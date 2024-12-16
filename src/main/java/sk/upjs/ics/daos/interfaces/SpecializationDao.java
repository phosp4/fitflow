package sk.upjs.ics.daos.interfaces;

import sk.upjs.ics.entities.Specialization;

import java.io.File;
import java.util.ArrayList;

/**
 * Interface for Specialization Data Access Object (DAO).
 * Provides methods to perform CRUD operations and load data from a CSV file.
 */
public interface SpecializationDao {

    /**
     * Loads specializations from a CSV file.
     *
     * @param file the CSV file containing specializations
     */
    void loadFromCsv(File file);

    /**
     * Creates a new specialization.
     *
     * @param specialization the specialization to create
     */
    void create(Specialization specialization);

    /**
     * Deletes an existing specialization.
     *
     * @param specialization the specialization to delete
     */
    void delete(Specialization specialization);

    /**
     * Updates an existing specialization.
     *
     * @param specialization the specialization to update
     */
    void update(Specialization specialization);

    /**
     * Finds a specialization by its ID.
     *
     * @param id the ID of the specialization to find
     * @return the found specialization, or null if not found
     */
    Specialization findById(Long id);

    /**
     * Finds all specializations.
     *
     * @return a list of all specializations
     */
    ArrayList<Specialization> findAll();
}