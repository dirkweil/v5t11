package de.gedoplan.v5t11.betriebssteuerung.model;

import de.gedoplan.v5t11.betriebssteuerung.entity.BausteinConfiguration;
import de.gedoplan.v5t11.betriebssteuerung.service.BausteinConfigurationService;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.Steuerung;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein.Baustein;

import java.io.Serializable;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Model;
import javax.inject.Inject;

/**
 * Presentation Model für die Bausteinprogrammierung.
 * 
 * @author dw
 */
@Model
@SessionScoped
public class BausteinProgrammierungModel implements Serializable
{
  @Inject
  Steuerung                     steuerung;

  @Inject
  Instance<Baustein>            bausteinInstanzen;

  @Inject
  BausteinConfigurationService  bausteinConfigurationService;

  @Inject
  Conversation                  conversation;

  private static final Collator COLLATOR = Collator.getInstance();

  /**
   * Liste aller konfigurierter Bausteine.
   */
  private List<Baustein>        konfigurierteBausteine;

  /**
   * Liste aller nicht-konfigurierter Bausteine.
   */
  private List<Baustein>        neueBausteine;

  /**
   * Aktueller Baustein.
   */
  private Baustein              currentBaustein;

  /**
   * Sollkonfiguration des aktuellen Bausteins.
   */
  private BausteinConfiguration currentBausteinSollConfiguration;

  /**
   * Istkonfiguration des aktuellen Bausteins.
   * 
   * Dieser Wert wird vom Presentation Model des jeweilligen Bausteins geliefert, wenn die Werte dauerhaft gespeichert werden
   * sollen. Ansonsten bleibt der Wert <code>null</code>.
   */
  private BausteinConfiguration currentBausteinIstConfiguration;

  /**
   * Wert liefern: {@link #konfigurierteBausteine}.
   * 
   * @return Wert
   */
  public List<Baustein> getKonfigurierteBausteine()
  {
    return this.konfigurierteBausteine;
  }

  /**
   * Wert liefern: {@link #neueBausteine}.
   * 
   * @return Wert
   */
  public List<Baustein> getNeueBausteine()
  {
    return this.neueBausteine;
  }

  /**
   * Wert liefern: {@link #currentBaustein}.
   * 
   * @return Wert
   */
  public Baustein getCurrentBaustein()
  {
    return this.currentBaustein;
  }

  /**
   * Wert liefern: {@link #currentBausteinSollConfiguration}.
   * 
   * @return Wert
   */
  public BausteinConfiguration getCurrentBausteinSollConfiguration()
  {
    return this.currentBausteinSollConfiguration;
  }

  /**
   * Wert liefern: {@link #currentBausteinIstConfiguration}.
   * 
   * @return Wert
   */
  public BausteinConfiguration getCurrentBausteinIstConfiguration()
  {
    return this.currentBausteinIstConfiguration;
  }

  /**
   * Wert setzen: {@link #currentBausteinIstConfiguration}.
   * 
   * @param currentBausteinIstConfiguration Wert
   */
  public void setCurrentBausteinIstConfiguration(BausteinConfiguration currentBausteinIstConfiguration)
  {
    this.currentBausteinIstConfiguration = currentBausteinIstConfiguration;
  }

  /**
   * Aktuellen Baustein wählen und Programm-Session beginnen.
   * 
   * @param baustein Wert
   */
  public String selectBaustein(Baustein baustein)
  {
    this.steuerung.getZentrale().setAktiv(false);

    this.currentBaustein = baustein;
    if (this.currentBaustein == null)
    {
      return null;
    }

    if (this.conversation.isTransient())
    {
      this.conversation.begin();
    }

    // TODO: Aktuelle Werte der Kanäle 0..9 sichern

    this.currentBausteinSollConfiguration = this.bausteinConfigurationService.getBausteinConfiguration(this.currentBaustein);

    return "openProgMode";
  }

  /**
   * Sollkonfiguration zum aktuellen Baustein besorgen und Einstelldialog beginnen.
   * 
   * @return Outcome
   */
  public String edit()
  {
    if (this.currentBaustein == null)
    {
      return null;
    }

    this.currentBausteinSollConfiguration = this.bausteinConfigurationService.getBausteinConfiguration(this.currentBaustein);

    return "/view/bausteinProgrammierung_" + this.currentBaustein.getClass().getSimpleName() + "?faces-redirect=true";
  }

  @SuppressWarnings("unused")
  @PostConstruct
  private void init()
  {
    this.konfigurierteBausteine = new ArrayList<Baustein>(this.steuerung.getBesetztmelder());
    this.konfigurierteBausteine.addAll(this.steuerung.getFunktionsdecoder());
    Collections.sort(this.konfigurierteBausteine);

    this.neueBausteine = new ArrayList<>();
    for (Baustein b : this.bausteinInstanzen)
    {
      this.neueBausteine.add(b);
    }
    Collections.sort(this.neueBausteine, new Comparator<Baustein>()
    {
      @Override
      public int compare(Baustein b1, Baustein b2)
      {
        return COLLATOR.compare(b1.getClass().getSimpleName(), b2.getClass().getSimpleName());
      }
    });

  }

  /**
   * Bausteinkonfiguration persistent ablegen, falls nicht <code>null</code>.
   * 
   * @return Outcome
   */
  public String store()
  {
    if (this.currentBausteinIstConfiguration != null)
    {
      this.bausteinConfigurationService.save(this.currentBausteinIstConfiguration);
    }
    return abort();
  }

  /**
   * Programmier-Session beenden.
   * 
   * @return Outcome
   */
  public String abort()
  {
    // TODO: Alte Werte der Kanäle 0..9 restaurieren

    if (!this.conversation.isTransient())
    {
      this.conversation.end();
    }

    return "bausteinProgrammierung";
  }
}
