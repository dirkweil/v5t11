package de.gedoplan.v5t11.betriebssteuerung.model;

import de.gedoplan.v5t11.betriebssteuerung.steuerung.Steuerung;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.lok.Lok;

import java.io.Serializable;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Model;
import javax.inject.Inject;

/**
 * Presentation Model für die Bausteinprogrammierung.
 *
 * @author dw
 */
@Model
@SessionScoped
public class LokdecoderProgrammierungModel implements Serializable
{
  @Inject
  Steuerung                     steuerung;

  // @Inject
  // Instance<Baustein> bausteinInstanzen;

  // @Inject
  // BausteinConfigurationService bausteinConfigurationService;

  @Inject
  Conversation                  conversation;

  private static final Collator COLLATOR = Collator.getInstance();

  /**
   * Liste aller konfigurierter Loks.
   */
  private List<Lok>             konfigurierteBausteine;

  /**
   * Liste aller nicht-konfigurierter Loks.
   */
  private List<Lok>             neueBausteine;

  /**
   * Aktuelle Lok.
   */
  private Lok                   currentBaustein;

  // /**
  // * Sollkonfiguration des aktuellen Bausteins.
  // */
  // private BausteinConfiguration currentBausteinSollConfiguration;

  // /**
  // * Istkonfiguration des aktuellen Bausteins.
  // *
  // * Dieser Wert wird vom Presentation Model des jeweilligen Bausteins geliefert, wenn die Werte dauerhaft gespeichert werden
  // * sollen. Ansonsten bleibt der Wert <code>null</code>.
  // */
  // private BausteinConfiguration currentBausteinIstConfiguration;

  public List<Lok> getKonfigurierteBausteine()
  {
    return this.konfigurierteBausteine;
  }

  public List<Lok> getNeueBausteine()
  {
    return this.neueBausteine;
  }

  public Lok getCurrentBaustein()
  {
    return this.currentBaustein;
  }

  // public BausteinConfiguration getCurrentBausteinSollConfiguration()
  // {
  // return this.currentBausteinSollConfiguration;
  // }

  // public BausteinConfiguration getCurrentBausteinIstConfiguration()
  // {
  // return this.currentBausteinIstConfiguration;
  // }

  // public void setCurrentBausteinIstConfiguration(BausteinConfiguration currentBausteinIstConfiguration)
  // {
  // this.currentBausteinIstConfiguration = currentBausteinIstConfiguration;
  // }

  /**
   * Aktuellen Baustein wählen und Programm-Session beginnen.
   *
   * @param baustein Wert
   */
  public String selectBaustein(Lok baustein)
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

    // this.currentBausteinSollConfiguration = this.bausteinConfigurationService.getBausteinConfiguration(this.currentBaustein);

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

    // this.currentBausteinSollConfiguration = this.bausteinConfigurationService.getBausteinConfiguration(this.currentBaustein);

    return "/view/lokdecoderProgrammierung_" + this.currentBaustein.getClass().getSimpleName() + "?faces-redirect=true";
  }

  @SuppressWarnings("unused")
  @PostConstruct
  private void init()
  {
    this.konfigurierteBausteine = new ArrayList<>(this.steuerung.getLoks());
    Collections.sort(this.konfigurierteBausteine);

    this.neueBausteine = new ArrayList<>();
    // for (LokDecoder lokDecoder : LokDecoder.values())
    // {
    // this.neueBausteine.add(new Lok(lokDecoder));
    // }
    // Collections.sort(this.neueBausteine, new Comparator<Lok>()
    // {
    // @Override
    // public int compare(Lok b1, Lok b2)
    // {
    // return COLLATOR.compare(b1.getDecoder().name(), b2.getDecoder().name());
    // }
    // });

  }

  /**
   * Bausteinkonfiguration persistent ablegen, falls nicht <code>null</code>.
   *
   * @return Outcome
   */
  public String store()
  {
    // if (this.currentBausteinIstConfiguration != null)
    // {
    // this.bausteinConfigurationService.save(this.currentBausteinIstConfiguration);
    // }
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

    return "lokdecoderProgrammierung";
  }
}
