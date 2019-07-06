package de.gedoplan.v5t11.status.entity;

import de.gedoplan.baselibs.persistence.entity.SingleIdEntity;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import lombok.Getter;
import lombok.Setter;

/**
 * Bausteinkonfiguration.
 *
 * @author dw
 */
@Entity
@Access(AccessType.FIELD)
@Getter
public class BausteinConfiguration extends SingleIdEntity<String> {
  /**
   * Id des Bausteins.
   */
  @Id
  private String id;

  /**
   * Adresse des Bausteins am SX-Bus.
   */
  @Min(0)
  @Max(1103)
  @Setter
  private int adresse;

  /**
   * Konfigurationswerte.
   */
  @ElementCollection(fetch = FetchType.EAGER)
  private Map<String, String> properties;

  /**
   * Konstruktor.
   *
   * @param id
   *        Id des Bausteins
   */
  public BausteinConfiguration(String id) {
    this.id = id;
    this.properties = new HashMap<>();
  }

  /**
   * Konstruktor (nur für JPA).
   */
  protected BausteinConfiguration() {
  }
}
