package sk.upjs.ics.daos.sql;

import sk.upjs.ics.daos.interfaces.VisitDao;
import sk.upjs.ics.entities.Visit;
import sk.upjs.ics.exceptions.CouldNotAccessDatabaseException;

import java.sql.*;
import java.util.ArrayList;

public class SQLVisitDao implements VisitDao {

    private final Connection connection;

    public SQLVisitDao(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void create(Visit visit) {
        String insertQuery = "INSERT INTO visits (user_id, visit_secret, transaction_id) VALUES (?, ?, ?)";
        try ( PreparedStatement pstmt = connection.prepareStatement(insertQuery)) {
            pstmt.setLong(1, visit.getUser().getId());
            pstmt.setString(2, visit.getVisitSecret());
            pstmt.setLong(3, visit.getCreditTransaction().getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new CouldNotAccessDatabaseException("Database not accessible", e);
        }
    }

    @Override
    public void delete(Long id) {
        String deleteQuery = "DELETE FROM visits WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(deleteQuery)) {
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new CouldNotAccessDatabaseException("Database not accessible", e);
        }
    }

    @Override
    public void update(Visit visit) {
    }

    @Override
    public Visit findById(Long id) {
        return null;
    }

    @Override
    public ArrayList<Visit> findAll() {
        return null;
    }
}
