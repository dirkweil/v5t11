package de.gedoplan.v5t11.leitstand.entity.fahrweg;

import de.gedoplan.v5t11.util.domain.attribute.BereichselementId;
import de.gedoplan.v5t11.util.domain.entity.fahrweg.AbstractGleis;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@Entity
@Table(name = Gleis.TABLE_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@NoArgsConstructor
public class Gleis extends AbstractGleis {

  public static final String TABLE_NAME = "SW_GLEIS";

  public Gleis(BereichselementId id) {
    super(id);
  }

}
