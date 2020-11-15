package de.gedoplan.v5t11.fahrstrassen.persistence;

import de.gedoplan.baselibs.persistence.repository.SingleIdEntityRepository;
import de.gedoplan.v5t11.fahrstrassen.entity.Parcours;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ParcoursRepository extends SingleIdEntityRepository<String, Parcours> {

}
