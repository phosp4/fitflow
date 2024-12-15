package sk.upjs.ics.daos.sql;

import sk.upjs.ics.daos.interfaces.SpecializationDao;
import sk.upjs.ics.entities.Specialization;

import java.sql.Connection;

public class SQLSpecializationDao implements SpecializationDao {

    private final Connection connection;

    public SQLSpecializationDao(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void create(Specialization specialization) {

    }

    @Override
    public void delete(Specialization specialization) {

    }

    @Override
    public void update(Specialization specialization) {

    }

    @Override
    public Specialization findById(Long id) {
        return null;
    }

    @Override
    public Iterable<Specialization> findAll() {
        return null;
    }
}
