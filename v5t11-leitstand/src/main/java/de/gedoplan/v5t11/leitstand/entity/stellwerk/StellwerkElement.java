package de.gedoplan.v5t11.leitstand.entity.stellwerk;

import de.gedoplan.baselibs.persistence.entity.SingleIdEntity;
import de.gedoplan.baselibs.persistence.repository.SingleIdEntityRepository;
import de.gedoplan.baselibs.utils.inject.InjectionUtil;
import de.gedoplan.v5t11.leitstand.entity.fahrweg.Signal;
import de.gedoplan.v5t11.leitstand.persistence.SignalRepository;
import de.gedoplan.v5t11.util.domain.attribute.BereichselementId;
import de.gedoplan.v5t11.util.domain.entity.Bereichselement;
import lombok.Getter;

import javax.inject.Inject;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import java.util.function.Function;

/**
 * Stellwerkselement.
 *
 * @author dw
 */
@XmlAccessorType(XmlAccessType.NONE)
public abstract class StellwerkElement extends Bereichselement implements Cloneable {

  @XmlAttribute
  @Getter
  String lage;

  /** Wiederholungsanzahl. Wird nach Unmarshall "ausgeführt" */
  @XmlAttribute
  int anzahl = 1;

  /** Zeilen-Nummer (1-basiert!) */
  @Getter
  int zeilenNr;

  /** Spalten-Nummer (1-basiert!) */
  @Getter
  int spaltenNr;

  /** ID auf der Webseite */
  @Getter
  String uiId;

  @XmlAttribute(name = "signal")
  @Getter
  String signalName;

  @XmlAttribute(name = "signalPos")
  @Getter
  String signalPosition;

  @Getter
  BereichselementId signalId;

  @Inject
  SignalRepository signalRepository;

  public boolean isLabel() {
    return false;
  }

  public void injectFields() {
    InjectionUtil.injectFields(this);
  }

  /**
   * Persistente Elemente erzeugen, wenn noch nicht vorhanden.
   * Diese Methode muss von den abgeleiteten Klassen ergänzt werden, d. h. sie muss überschrieben werden
   * mit addPersistentEntries() im Methodencode.
   */
  public void addPersistentEntries() {
    if (this.signalName != null) {
      this.signalId = new BereichselementId(this.getBereich(), this.signalName);
      createIfNotPresent(this.signalRepository, this.signalId, Signal::new);
    }
  }

  protected <K, E extends SingleIdEntity<K>, R extends SingleIdEntityRepository<K, E>> E createIfNotPresent(R repository, K id, Function<K, E> creator) {
    E entity = repository.findById(id);
    if (entity == null) {
      entity = creator.apply(id);
      repository.persist(entity);
    }
    return entity;
  }

  /**
   * Zugehöriges Signal aus der DB lesen.
   *
   * @return Signal oder <code>null, wenn kein Signal zugeordnet ist</code>
   */
  public Signal findSignal() {
    if (this.signalId == null) {
      return null;
    }

    Signal signal = this.signalRepository.findById(this.signalId);
    assert signal != null;
    return signal;
  }

  public StellwerkElement clone() {
    try {
      return (StellwerkElement) super.clone();
    } catch (CloneNotSupportedException e) {
      throw new AssertionError();
    }
  }
}
