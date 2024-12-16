package sk.upjs.ics.daos.sql;

import sk.upjs.ics.daos.interfaces.ReservationStatusDao;
import sk.upjs.ics.entities.ReservationStatus;
import sk.upjs.ics.exceptions.CouldNotAccessDatabaseException;
import sk.upjs.ics.exceptions.CouldNotAccessFileException;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

public class SQLReservationStatusDao implements ReservationStatusDao {

    private final Connection connection;

    public SQLReservationStatusDao(Connection connection) {
        this.connection = connection;
    }

    private final String insertQuery = "INSERT INTO reservation_statuses (name) VALUES (?)";

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
                    pstmt.setString(1, line);
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
    public void create(String status) {

    }

    @Override
    public void delete(String status) {

    }

    @Override
    public void update(String status) {

    }

    @Override
    public ReservationStatus findById(Long id) {
        return null;
    }

    @Override
    public ArrayList<ReservationStatus> findAll() {
        return null;
    }
}
