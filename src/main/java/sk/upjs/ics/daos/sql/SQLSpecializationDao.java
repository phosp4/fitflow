package sk.upjs.ics.daos.sql;

import sk.upjs.ics.daos.interfaces.SpecializationDao;
import sk.upjs.ics.entities.Specialization;
import sk.upjs.ics.exceptions.CouldNotAccessDatabaseException;
import sk.upjs.ics.exceptions.CouldNotAccessFileException;
import sk.upjs.ics.exceptions.NotFoundException;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * SQLSpecializationDao is an implementation of the SpecializationDao interface
 * that provides methods to interact with the specializations in the database.
 */
public class SQLSpecializationDao implements SpecializationDao {

    private final Connection connection;

    /**
     * Constructs a new SQLSpecializationDao with the specified database connection.
     *
     * @param connection the database connection
     */
    public SQLSpecializationDao(Connection connection) {
        this.connection = connection;
    }

    private final String selectQuery = "SELECT id, name FROM trainer_specializations";
    private final String insertQuery = "INSERT INTO trainer_specializations (name) VALUES (?)";

    /**
     * Loads specializations from a CSV file and inserts them into the database.
     *
     * @param file the CSV file containing specialization data
     * @throws CouldNotAccessFileException if the file cannot be accessed
     * @throws CouldNotAccessDatabaseException if the database cannot be accessed
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

                try (PreparedStatement pstmt = connection.prepareStatement(insertQuery)) {
                    pstmt.setString(1, line.trim()); // can set the whole line since it only cotains the names
                    pstmt.executeUpdate();
                } catch (SQLException e) {
                    throw new CouldNotAccessDatabaseException("Database not accessible", e);
                }
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
     * @throws CouldNotAccessDatabaseException if the database cannot be accessed
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

        try (PreparedStatement pstmt = connection.prepareStatement(insertQuery)) {
            pstmt.setString(1, specialization.getName());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new CouldNotAccessDatabaseException("Database not accessible", e);
        }
    }

    /**
     * Deletes a specialization from the database.
     *
     * @param specialization the specialization to delete
     * @throws IllegalArgumentException if the specialization or its ID is null
     * @throws CouldNotAccessDatabaseException if the database cannot be accessed
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

        String deleteQuery = "DELETE FROM trainer_specializations WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(deleteQuery)) {
            pstmt.setLong(1, specialization.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new CouldNotAccessDatabaseException("Database not accessible", e);
        }
    }

    /**
     * Updates an existing specialization in the database.
     *
     * @param specialization the specialization to update
     * @throws IllegalArgumentException if the specialization or any of its required fields are null
     * @throws CouldNotAccessDatabaseException if the database cannot be accessed
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

        try (PreparedStatement pstmt = connection.prepareStatement(updateQuery)) {
            pstmt.setString(1, specialization.getName());
            pstmt.setLong(2, specialization.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new CouldNotAccessDatabaseException("Database not accessible", e);
        }
    }

    /**
     * Finds a specialization by its ID.
     *
     * @param id the ID of the specialization to find
     * @return the specialization with the specified ID
     * @throws IllegalArgumentException if the ID is null
     * @throws CouldNotAccessDatabaseException if the database cannot be accessed
     * @throws NotFoundException if the specialization with the specified ID is not found
     */
    @Override
    public Specialization findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }

        try (PreparedStatement pstmt = connection.prepareStatement(selectQuery + " WHERE id = ?")) {
            pstmt.setLong(1, id);

            ResultSet rs = pstmt.executeQuery();

            if (!rs.next()) {
                throw new NotFoundException("Specialization with id " + id + " not found");
            }

            return Specialization.fromResultSet(rs);
        } catch (SQLException e) {
            throw new CouldNotAccessDatabaseException("Database not accessible", e);
        }
    }

    /**
     * Finds all specializations in the database.
     *
     * @return a list of all specializations
     * @throws CouldNotAccessDatabaseException if the database cannot be accessed
     * @throws NotFoundException if no specializations are found
     */
    @Override
    public ArrayList<Specialization> findAll() {
        ArrayList<Specialization> specializations = new ArrayList<>();

        try (PreparedStatement pstmt = connection.prepareStatement(selectQuery)) {
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                specializations.add(Specialization.fromResultSet(rs));
            }

            if (specializations.isEmpty()) {
                throw new NotFoundException("No specializations found");
            }

            return specializations;
        } catch (SQLException e) {
            throw new CouldNotAccessDatabaseException("Database not accessible", e);
        }
    }
}
