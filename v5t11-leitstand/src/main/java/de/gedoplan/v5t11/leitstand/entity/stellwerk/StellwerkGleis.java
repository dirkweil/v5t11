package de.gedoplan.v5t11.leitstand.entity.stellwerk;

import de.gedoplan.v5t11.leitstand.entity.fahrweg.Gleis;
import de.gedoplan.v5t11.leitstand.persistence.GleisRepository;
import de.gedoplan.v5t11.util.misc.V5t11Exception;
import lombok.Getter;

import javax.inject.Inject;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import java.util.List;
import java.util.stream.Stream;

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
    Gleis gleis = this.gleisRepository.findById(getId());
    assert gleis != null;
    return gleis;
  }

}
