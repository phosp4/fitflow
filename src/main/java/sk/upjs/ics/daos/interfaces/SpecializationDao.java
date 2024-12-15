package sk.upjs.ics.daos.interfaces;

import sk.upjs.ics.entities.Specialization;

public interface SpecializationDao {

    void create(Specialization specialization);

    void delete(Specialization specialization);

    void update(Specialization specialization);

    Specialization findById(Long id);

    Iterable<Specialization> findAll();
}
