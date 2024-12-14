package sk.upjs.ics.daos.interfaces;

import sk.upjs.ics.entities.Visit;

public interface VisitDao {

    void create(Visit visit);

    void delete(Long id);

    void update(Visit visit);

    Visit findById(Long id);

    Iterable<Visit> findAll();
}
