package sk.upjs.ics.daos.interfaces;

import sk.upjs.ics.entities.TrainerInterval;

public interface TrainerIntervalDao {

    void create(TrainerInterval interval);

    void delete(TrainerInterval interval);

    void update(TrainerInterval interval);

    TrainerInterval findById(Long id);

    Iterable<TrainerInterval> findAll();
}
