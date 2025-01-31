package sk.upjs.ics.daos.interfaces;

import sk.upjs.ics.entities.TrainerInterval;
import sk.upjs.ics.entities.User;

import java.io.File;
import java.util.ArrayList;

/**
 * Interface for TrainerInterval Data Access Object (DAO).
 * Provides methods to perform CRUD operations and load data from a CSV file.
 */
public interface TrainerIntervalDao {

    /**
     * Loads trainer intervals from a CSV file.
     *
     * @param file the CSV file containing trainer intervals
     */
    void loadFromCsv(File file);

    /**
     * Creates a new trainer interval.
     *
     * @param interval the trainer interval to create
     */
    void create(TrainerInterval interval);

    /**
     * Deletes an existing trainer interval.
     *
     * @param interval the trainer interval to delete
     */
    void delete(TrainerInterval interval);

    /**
     * Updates an existing trainer interval.
     *
     * @param interval the trainer interval to update
     */
    void update(TrainerInterval interval);

    /**
     * Finds a trainer interval by its ID.
     *
     * @param id the ID of the trainer interval to find
     * @return the found trainer interval, or null if not found
     */
    TrainerInterval findById(Long id);

    /**
     * Finds all trainer intervals.
     *
     * @return a list of all trainer intervals
     */
    ArrayList<TrainerInterval> findAll();

    /**
     * Finds all trainers intervals by its trainers id
     */
    ArrayList<TrainerInterval> findByTrainer(User trainer);
}