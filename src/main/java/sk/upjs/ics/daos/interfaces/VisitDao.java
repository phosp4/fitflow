package sk.upjs.ics.daos.interfaces;

import sk.upjs.ics.entities.Visit;

import java.util.ArrayList;

public interface VisitDao {

    void create(Visit visit);

    void delete(Long id);

    void update(Visit visit);

    Visit findById(Long id);

    ArrayList<Visit> findAll();
}
