package de.gedoplan.v5t11.leitstand.entity.stellwerk;

import de.gedoplan.v5t11.leitstand.entity.fahrweg.Gleis;
import de.gedoplan.v5t11.leitstand.entity.fahrweg.Weiche;
import de.gedoplan.v5t11.leitstand.persistence.GleisRepository;
import de.gedoplan.v5t11.leitstand.persistence.WeicheRepository;
import de.gedoplan.v5t11.util.domain.attribute.BereichselementId;
import lombok.Getter;

import javax.inject.Inject;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Stellwerkselement für eine Doppelkreuzungsweiche mit 2 Antrieben.
 *
 * @author dw
 */
@XmlAccessorType(XmlAccessType.NONE)
public class StellwerkDkw2 extends StellwerkElement {

  @Inject
  GleisRepository gleisRepository;

  @Inject
  WeicheRepository weicheRepository;

  @Getter
  private BereichselementId weicheAId;

  @Getter
  private BereichselementId weicheBId;

  @Getter
  private BereichselementId gleisId;

  @Getter
  private StellwerkRichtung labelPos;

  private static final Pattern PATTERN = Pattern.compile("(\\w+)\\|(\\w+),(\\w+)\\|(\\w+)");

  @Getter
  private StellwerkRichtung[] geradeRichtung = new StellwerkRichtung[2];

  @Getter
  private StellwerkRichtung[] abzweigRichtung = new StellwerkRichtung[2];

  private void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {
    Matcher matcher = PATTERN.matcher(this.lage);
    if (matcher.matches()) {
      this.geradeRichtung[0] = StellwerkRichtung.valueOf(matcher.group(1));
      this.abzweigRichtung[0] = StellwerkRichtung.valueOf(matcher.group(2));
      this.geradeRichtung[1] = StellwerkRichtung.valueOf(matcher.group(3));
      this.abzweigRichtung[1] = StellwerkRichtung.valueOf(matcher.group(4));
    } else {
      throw new IllegalArgumentException("Lage muss bei Dkw das Format g|a,g|a haben");
    }

    this.labelPos = findBestLabelPos(this.abzweigRichtung[0], this.abzweigRichtung[1], this.geradeRichtung[0], this.geradeRichtung[1]);
  }

  @Override
  public void addPersistentEntries() {
    super.addPersistentEntries();

    this.weicheAId = new BereichselementId(getBereich(), getName() + "a");
    Weiche weicheA = createIfNotPresent(this.weicheRepository, this.weicheAId, Weiche::new);
    this.weicheBId = new BereichselementId(getBereich(), getName() + "b");
    createIfNotPresent(this.weicheRepository, this.weicheBId, Weiche::new);

    this.gleisId = new BereichselementId(getBereich(), weicheA.getGleisName());
    createIfNotPresent(this.gleisRepository, this.gleisId, Gleis::new);
  }

  /**
   * Zugehörige Weiche aus der DB lesen.
   *
   * @return Weiche
   */
  public Weiche findWeicheA() {
    Weiche weiche = this.weicheRepository.findById(this.weicheAId);
    assert weiche != null;
    return weiche;
  }

  /**
   * Zugehörige Weiche aus der DB lesen.
   *
   * @return Weiche
   */
  public Weiche findWeicheB() {
    Weiche weiche = this.weicheRepository.findById(this.weicheBId);
    assert weiche != null;
    return weiche;
  }

  /**
   * Zugehörigen Gleis aus der DB lesen.
   *
   * @return Gleis
   */
  public Gleis findGleis() {
    Gleis gleis = this.gleisRepository.findById(this.gleisId);
    assert gleis != null;
    return gleis;
  }
}
