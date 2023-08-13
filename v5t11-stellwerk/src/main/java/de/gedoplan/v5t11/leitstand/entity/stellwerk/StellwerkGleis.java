package de.gedoplan.v5t11.leitstand.entity.stellwerk;

import de.gedoplan.v5t11.leitstand.entity.fahrweg.Gleis;
import de.gedoplan.v5t11.leitstand.persistence.GleisRepository;
import de.gedoplan.v5t11.util.misc.V5t11Exception;

import java.util.List;
import java.util.stream.Stream;

import jakarta.inject.Inject;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;

import lombok.Getter;

/**
 * Leeres Stellwerkselement
 *
 * @author dw
 */
@XmlAccessorType(XmlAccessType.NONE)
public class StellwerkGleis extends StellwerkElement {

  @XmlAttribute
  @Getter
  boolean label;

  @Inject
  GleisRepository gleisRepository;

  @Getter
  private List<StellwerkRichtung> richtungen;

  @Getter
  private StellwerkRichtung labelPos;

  private void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {
    try {
      this.richtungen = Stream.of(this.lage.split(","))
        .map(l -> StellwerkRichtung.valueOf(l))
        .toList();
    } catch (Exception e) {
      throw new V5t11Exception("lage muss komma-separierte Liste von Richtungen sein");
    }

    if (this.richtungen.size() != 2) {
      throw new V5t11Exception("lage muss zwei Elemente haben");
    }

    if (this.label) {
      this.labelPos = findBestLabelPos(this.richtungen.get(0), this.richtungen.get(1));
    }
  }

  @Override
  public void addPersistentEntries() {
    super.addPersistentEntries();
    createIfNotPresent(this.gleisRepository, getId(), Gleis::new);
  }

  /**
   * Zugehörigen Gleis aus der DB lesen.
   *
   * @return Gleis
   */
  public Gleis findGleis() {
    return this.gleisRepository.findById(getId()).get();
  }

}
