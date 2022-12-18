package de.gedoplan.v5t11.leitstand.entity.stellwerk;

import de.gedoplan.baselibs.utils.inject.InjectionUtil;
import lombok.Getter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import java.io.Serializable;
import java.util.List;

/**
 * Stellwerk.
 *
 * @author dw
 */
@XmlAccessorType(XmlAccessType.NONE)
public class StellwerkZeile implements Serializable {
  @XmlElements({
    @XmlElement(name = "Leer", type = StellwerkLeer.class),
    @XmlElement(name = "Gleis", type = StellwerkGleis.class),
    @XmlElement(name = "Weiche", type = StellwerkWeiche.class),
    @XmlElement(name = "Dkw2", type = StellwerkDkw2.class),
  })
  @Getter
  private List<StellwerkElement> elemente;

  //  private void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {
  //    ListIterator<StellwerkElement> iterator = this.elemente.listIterator(this.elemente.size());
  //    while (iterator.hasPrevious()) {
  //      StellwerkElement element = iterator.previous();
  //      int anzahl = element.anzahl;
  //      element.anzahl = 1;
  //      for (int i = 1; i < anzahl; ++i) {
  //        iterator.add(element.clone());
  //      }
  //    }
  //  }

  public void injectFields() {
    InjectionUtil.injectFields(this);
    this.elemente.forEach(StellwerkElement::injectFields);
  }

  public void addPersistentEntries() {
    this.elemente.forEach(StellwerkElement::addPersistentEntries);
  }

}
