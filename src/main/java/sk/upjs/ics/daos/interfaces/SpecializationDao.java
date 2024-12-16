package sk.upjs.ics.daos.interfaces;

import sk.upjs.ics.entities.Specialization;

import java.io.File;
import java.util.ArrayList;

public interface SpecializationDao {

    void loadFromCsv(File file);

    void create(Specialization specialization);

    void delete(Specialization specialization);

    void update(Specialization specialization);

    Specialization findById(Long id);

    ArrayList<Specialization> findAll();
}
