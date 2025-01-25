package sk.upjs.ics.daos.sql;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.ResultSetExtractor;
import sk.upjs.ics.daos.interfaces.SpecializationDao;
import sk.upjs.ics.entities.Specialization;
import sk.upjs.ics.exceptions.CouldNotAccessFileException;
import sk.upjs.ics.exceptions.NotFoundException;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * SQLSpecializationDao is an implementation of the SpecializationDao interface
 * that provides methods to interact with the specializations in the database.
 */
public class SQLSpecializationDao implements SpecializationDao {

    private final JdbcOperations jdbcOperations;

    /**
     * Constructs a new SQLSpecializationDao with the specified database connection.
     *
     * @param jdbcOperations the database connection via jdbc
     */
    public SQLSpecializationDao(JdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    private final ResultSetExtractor<ArrayList<Specialization>> resultSetExtractor = rs -> {
      ArrayList<Specialization> specializations = new ArrayList<>();

      while(rs.next()) {
          specializations.add(Specialization.fromResultSet(rs));
      }

      return specializations;
    };

    private final String selectQuery = "SELECT id, name FROM trainer_specializations";
    private final String insertQuery = "INSERT INTO trainer_specializations (name) VALUES (?)";

    /**
     * Loads specializations from a CSV file and inserts them into the database.
     *
     * @param file the CSV file containing specialization data
     * @throws CouldNotAccessFileException if the file cannot be accessed
     */
    @Override
    public void loadFromCsv(File file) {
        try (Scanner scanner = new Scanner(file)) {
            // skip header
            scanner.nextLine();

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();

                if (line.isEmpty()) {
                    continue;
                }

                jdbcOperations.update(connection -> {
                    PreparedStatement pstmt = connection.prepareStatement(insertQuery);
                    pstmt.setString(1, line.trim()); // can set the whole line since it only cotains the names
                    return pstmt;
                });

            }
        } catch (FileNotFoundException e) {
            throw new CouldNotAccessFileException("Could not access file");
        }
    }

    /**
     * Creates a new specialization in the database.
     *
     * @param specialization the specialization to create
     * @throws IllegalArgumentException if the specialization or its name is null
     */
    @Override
    public void create(Specialization specialization) {
        if (specialization == null) {
            throw new IllegalArgumentException("Specialization cannot be null");
        }

        if (specialization.getName() == null) {
            throw new IllegalArgumentException("Specialization name cannot be null");
        }

        if (findById(specialization.getId()) != null) {
            throw new IllegalArgumentException("Specialization with id " + specialization.getId() + " already exists");
        }

        jdbcOperations.update(connection -> {
            PreparedStatement pstmt = connection.prepareStatement(insertQuery);
            pstmt.setString(1, specialization.getName());
            return pstmt;
        });
    }

    /**
     * Deletes a specialization from the database.
     *
     * @param specialization the specialization to delete
     * @throws IllegalArgumentException if the specialization or its ID is null
     */
    @Override
    public void delete(Specialization specialization) {
        if (specialization == null) {
            throw new IllegalArgumentException("Specialization cannot be null");
        }

        if (specialization.getId() == null) {
            throw new IllegalArgumentException("The specialization does not have an id");
        }

        if (findById(specialization.getId()) == null) {
            throw new NotFoundException("Specialization with id " + specialization.getId() + " not found");
        }

        jdbcOperations.update("DELETE FROM trainer_specializations WHERE id = ?", specialization.getId());
    }

    /**
     * Updates an existing specialization in the database.
     *
     * @param specialization the specialization to update
     * @throws IllegalArgumentException if the specialization or any of its required fields are null
     */
    @Override
    public void update(Specialization specialization) {
        if (specialization == null) {
            throw new IllegalArgumentException("Specialization cannot be null");
        }

        if (specialization.getId() == null) {
            throw new IllegalArgumentException("The specialization does not have an id");
        }

        if (specialization.getName() == null) {
            throw new IllegalArgumentException("Specialization name cannot be null");
        }

        if (findById(specialization.getId()) == null) {
            throw new NotFoundException("Specialization with id " + specialization.getId() + " not found");
        }

        String updateQuery = "UPDATE trainer_specializations SET name = ? WHERE id = ?";

        jdbcOperations.update(connection -> {
            PreparedStatement pstmt = connection.prepareStatement(updateQuery);
            pstmt.setString(1, specialization.getName());
            pstmt.setLong(2, specialization.getId());
            return pstmt;
        });
    }

    /**
     * Finds a specialization by its ID.
     *
     * @param id the ID of the specialization to find
     * @return the specialization with the specified ID
     * @throws IllegalArgumentException if the ID is null
     * @throws NotFoundException if the specialization with the specified ID is not found
     */
    @Override
    public Specialization findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }

        ArrayList<Specialization> specializations = jdbcOperations.query(selectQuery + " WHERE id = ?", resultSetExtractor, id);

        if (specializations == null || specializations.isEmpty()) {
            throw new NotFoundException("Specialization with id " + id + " not found!");
        }

        return specializations.getFirst();
    }

    /**
     * Finds all specializations in the database.
     *
     * @return a list of all specializations
     * @throws NotFoundException if no specializations are found
     */
    @Override
    public ArrayList<Specialization> findAll() {
        ArrayList<Specialization> specializations = jdbcOperations.query(selectQuery + " WHERE id = ?", resultSetExtractor);

        if (specializations == null) {
            throw new NotFoundException("Error while finding specializations");
        }

        return specializations;
    }
}
