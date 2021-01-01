package de.gedoplan.v5t11.status.entity;

import de.gedoplan.baselibs.persistence.entity.SingleIdEntity;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Bausteinkonfiguration.
 *
 * @author dw
 */
@Entity
@Access(AccessType.FIELD)
@Table(name = BausteinConfiguration.TABLE_NAME)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class BausteinConfiguration extends SingleIdEntity<String> {

  public static final String TABLE_NAME = "ST_BAUSTEIN_CONFIG";
  public static final String TABLE_NAME_PROPS = "ST_BAUSTEIN_CONFIG_PROPS";

  /**
   * Id des Bausteins.
   */
  @Id
  private String id;

  /**
   * Adresse des Bausteins am SX-Bus.
   */
  @Min(0)
  @Max(103)
  @Setter
  @Column(name = "ADRESSE")
  private int localAdr;

  /**
   * Konfigurationswerte.
   */
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = TABLE_NAME_PROPS)
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

}
