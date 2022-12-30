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
 * Leeres Stellwerkselement
 *
 * @author dw
 */
@XmlAccessorType(XmlAccessType.NONE)
public class StellwerkEinfachWeiche extends StellwerkElement {

  @Getter
  private StellwerkRichtung stammRichtung;

  @Getter
  private StellwerkRichtung geradeRichtung;

  @Getter
  private StellwerkRichtung abzweigendRichtung;

  @Getter
  private boolean stammIstEinfahrt;

  private static final Pattern PATTERN_VORWAERTS = Pattern.compile("(\\w+),(\\w+)\\|(\\w+)");
  private static final Pattern PATTERN_RUECKWAERTS = Pattern.compile("(\\w+)\\|(\\w+),(\\w+)");

  @Inject
  GleisRepository gleisRepository;

  @Inject
  WeicheRepository weicheRepository;

  @Getter
  private BereichselementId gleisId;

  private void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {
    Matcher matcher = PATTERN_VORWAERTS.matcher(this.lage);
    if (matcher.matches()) {
      this.stammRichtung = StellwerkRichtung.valueOf(matcher.group(1));
      this.geradeRichtung = StellwerkRichtung.valueOf(matcher.group(2));
      this.abzweigendRichtung = StellwerkRichtung.valueOf(matcher.group(3));
      this.stammIstEinfahrt = true;
    } else {
      matcher = PATTERN_RUECKWAERTS.matcher(this.lage);
      if (matcher.matches()) {
        this.stammRichtung = StellwerkRichtung.valueOf(matcher.group(3));
        this.geradeRichtung = StellwerkRichtung.valueOf(matcher.group(1));
        this.abzweigendRichtung = StellwerkRichtung.valueOf(matcher.group(2));
        this.stammIstEinfahrt = false;
      } else {
        throw new IllegalArgumentException("Lage muss bei Weichen das Format s,g|a oder g|a,s haben");
      }
    }
  }

  @Override
  public void addPersistentEntries() {
    super.addPersistentEntries();

    Weiche weiche = createIfNotPresent(this.weicheRepository, getId(), Weiche::new);

    this.gleisId = new BereichselementId(getBereich(), weiche.getGleisName());
    createIfNotPresent(this.gleisRepository, this.gleisId, Gleis::new);
  }

  /**
   * Zugehörige Weiche aus der DB lesen.
   *
   * @return Weiche
   */
  public Weiche findWeiche() {
    Weiche weiche = this.weicheRepository.findById(this.getId());
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
