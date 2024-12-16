package sk.upjs.ics.daos.sql;

import sk.upjs.ics.daos.interfaces.TrainerIntervalDao;
import sk.upjs.ics.entities.TrainerInterval;

import java.sql.Connection;
import java.util.ArrayList;

public class SQLTrainerIntervalDao implements TrainerIntervalDao {

    private final Connection connection;

    public SQLTrainerIntervalDao(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void loadFromCsv(String file) {

    }

    @Override
    public void create(TrainerInterval interval) {

    }

    @Override
    public void delete(TrainerInterval interval) {

    }

    @Override
    public void update(TrainerInterval interval) {

    }

    @Override
    public TrainerInterval findById(Long id) {
        return null;
    }

    @Override
    public ArrayList<TrainerInterval> findAll() {
        return null;
    }
}
