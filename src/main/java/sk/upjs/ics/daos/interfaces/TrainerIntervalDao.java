package sk.upjs.ics.daos.interfaces;

import sk.upjs.ics.entities.TrainerInterval;

import java.io.File;
import java.util.ArrayList;

public interface TrainerIntervalDao {

    void loadFromCsv(File file);

    void create(TrainerInterval interval);

    void delete(TrainerInterval interval);

    void update(TrainerInterval interval);

    TrainerInterval findById(Long id);

    ArrayList<TrainerInterval> findAll();
}
