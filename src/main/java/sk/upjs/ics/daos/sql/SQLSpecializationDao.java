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

public class SQLSpecializationDao implements SpecializationDao {

    private final Connection connection;

    public SQLSpecializationDao(Connection connection) {
        this.connection = connection;
    }

    private final String selectQuery = "SELECT id, name FROM roles";
    private final String insertQuery = "INSERT INTO trainer_specializations (name) VALUES (?)";

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

    @Override
    public void create(Specialization specialization) {
        if (specialization == null) {
            throw new IllegalArgumentException("Specialization cannot be null");
        }

        if (specialization.getId() != null) {
            throw new IllegalArgumentException("The specialization already has an id");
        }

        try (PreparedStatement pstmt = connection.prepareStatement(insertQuery)) {
            pstmt.setString(1, specialization.getName());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new CouldNotAccessDatabaseException("Database not accessible", e);
        }
    }

    @Override
    public void delete(Specialization specialization) {
        if (specialization == null) {
            throw new IllegalArgumentException("Specialization cannot be null");
        }

        if (specialization.getId() == null) {
            throw new IllegalArgumentException("The specialization does not have an id");
        }

        String deleteQuery = "DELETE FROM trainer_specializations WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(deleteQuery)) {
            pstmt.setLong(1, specialization.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new CouldNotAccessDatabaseException("Database not accessible", e);
        }
    }

    @Override
    public void update(Specialization specialization) {
        if (specialization == null) {
            throw new IllegalArgumentException("Specialization cannot be null");
        }

        if (specialization.getId() == null) {
            throw new IllegalArgumentException("The specialization does not have an id");
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
