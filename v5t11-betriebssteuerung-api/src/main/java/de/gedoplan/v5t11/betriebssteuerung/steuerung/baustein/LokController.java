package de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein;

import de.gedoplan.v5t11.betriebssteuerung.listener.ConfigChangedEvent;
import de.gedoplan.v5t11.betriebssteuerung.listener.ConfigChangedListener;
import de.gedoplan.v5t11.betriebssteuerung.listener.ConfigChangedListenerRegistry;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.lok.Lok;

import javax.enterprise.inject.Typed;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonMethod;

/**
 * Besetztmelder.
 *
 * @author dw
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
@JsonAutoDetect(JsonMethod.NONE)
@Typed
public class LokController extends Baustein implements Encoder
{
  /**
   * Zugewiesene Lok oder <code>null</code>.
   */
  protected Lok                                   lok;

  private transient ConfigChangedListenerRegistry configChangedListenerRegistry;

  private long                                    invertMask;

  /**
   * Konstruktor für JPA und JAXB.
   */
  protected LokController()
  {
    super(1);
  }

  /**
   * Wert liefern: {@link #lok}.
   *
   * @return Wert
   */
  public Lok getLok()
  {
    return this.lok;
  }

  /**
   * Wert setzen: {@link #lok}.
   *
   * @param lok Wert
   */
  public void setLok(Lok lok)
  {
    if (lok != null)
    {
      if (lok.equals(this.lok))
      {
        return;
      }
    }
    else if (this.lok == null)
    {
      return;
    }

    if (lok != null)
    {
      this.invertMask = (getWert() & (Lok.MASK_LICHT | Lok.MASK_RICHTUNG)) ^ (lok.getWert() & (Lok.MASK_LICHT | Lok.MASK_RICHTUNG));
    }

    this.lok = lok;

    if (this.configChangedListenerRegistry != null)
    {
      this.configChangedListenerRegistry.sendEvent(new ConfigChangedEvent(this));
    }
  }

  /**
   * {@inheritDoc}
   *
   * @see de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein.Baustein#setWert(int, boolean)
   */
  @Override
  public void setWert(long wert, boolean updateInterface)
  {
    super.setWert(wert, updateInterface);

    if (this.lok != null)
    {
      this.lok.setWert(wert ^ this.invertMask);
    }
  }

  /**
   * Listener für Konfigurationsänderungen hinzufügen.
   *
   * @param configChangedListener Listener
   * @see de.gedoplan.v5t11.betriebssteuerung.listener.ConfigChangedListenerRegistry#addListener(de.gedoplan.v5t11.betriebssteuerung.listener.ConfigChangedListener)
   */
  public void addConfigChangedListener(ConfigChangedListener valueChangedListener)
  {
    if (this.configChangedListenerRegistry == null)
    {
      this.configChangedListenerRegistry = new ConfigChangedListenerRegistry();
    }
    this.configChangedListenerRegistry.addListener(valueChangedListener);
  }

}
