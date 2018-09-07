package de.gedoplan.v5t11.betriebssteuerung.steuerung;

import de.gedoplan.baselibs.utils.xml.XmlConverter;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein.Besetztmelder;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein.Funktionsdecoder;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrweg.Gleisabschnitt;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrweg.geraet.Signal;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrweg.geraet.Weiche;
import de.gedoplan.v5t11.selectrix.SelectrixConnection;
import de.gedoplan.v5t11.selectrix.SelectrixMessage;

import junit.framework.Assert;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

/**
 * Testklasse zu Steuerung.
 *
 * @author dw
 */
public class SteuerungTest implements SelectrixConnection {
  private Map<Integer, Integer> cache = new HashMap<>();
  private Steuerung steuerung;

  /**
   * Initialisierung vor jedem Test.
   *
   * @throws Exception
   */
  @Before
  public void before() throws Exception // CHECKSTYLE:IGNORE
  {
    this.steuerung = XmlConverter.fromXml(Steuerung.class, "DemoAnlage.xml");
  }

  /**
   * Simulation von Gleisbelegungen per Hardware und Test der Belegung der entsprechenden Gleisabschnitte.
   *
   * @throws Exception
   */
  @Test
  // @Ignore
  public void testGleisBesetzt() throws Exception // CHECKSTYLE:IGNORE
  {
    Gleisabschnitt gleis1 = this.steuerung.getGleisabschnitt("show", "1");
    Gleisabschnitt gleis2 = this.steuerung.getGleisabschnitt("show", "2");
    Gleisabschnitt gleis11 = this.steuerung.getGleisabschnitt("show", "11");

    setBesetzt(gleis1, false);
    setBesetzt(gleis2, false);
    setBesetzt(gleis11, false);
    Assert.assertFalse("Gleis 1 besetzt", gleis1.isBesetzt());
    Assert.assertFalse("Gleis 2 besetzt", gleis2.isBesetzt());
    Assert.assertFalse("Gleis 11 besetzt", gleis11.isBesetzt());

    setBesetzt(gleis1, true);
    Assert.assertTrue("Gleis 1 besetzt", gleis1.isBesetzt());
    Assert.assertFalse("Gleis 2 besetzt", gleis2.isBesetzt());
    Assert.assertFalse("Gleis 11 besetzt", gleis11.isBesetzt());

    setBesetzt(gleis2, true);
    Assert.assertTrue("Gleis 1 besetzt", gleis1.isBesetzt());
    Assert.assertTrue("Gleis 2 besetzt", gleis2.isBesetzt());
    Assert.assertFalse("Gleis 11 besetzt", gleis11.isBesetzt());

    setBesetzt(gleis11, true);
    Assert.assertTrue("Gleis 1 besetzt", gleis1.isBesetzt());
    Assert.assertTrue("Gleis 2 besetzt", gleis2.isBesetzt());
    Assert.assertTrue("Gleis 11 besetzt", gleis11.isBesetzt());

    setBesetzt(gleis2, false);
    Assert.assertTrue("Gleis 1 besetzt", gleis1.isBesetzt());
    Assert.assertFalse("Gleis 2 besetzt", gleis2.isBesetzt());
    Assert.assertTrue("Gleis 11 besetzt", gleis11.isBesetzt());
  }

  /**
   * Hilfsmethode zum Setzen der simulierten Hardware-Bits für eine Gleisbelegung.
   *
   * @param gleisabschnitt
   *          Gleisabschnitt
   * @param besetzt
   *          <code>true</code>, wenn besetzt
   */
  private void setBesetzt(Gleisabschnitt gleisabschnitt, boolean besetzt) {
    Besetztmelder besetztmelder = gleisabschnitt.getBesetztmelder();
    int adresse = besetztmelder.getAdresse();
    int anschluss = gleisabschnitt.getAnschluss();
    int wert = getValue(adresse);
    if (besetzt) {
      wert |= (1 << anschluss);
    } else {
      wert &= ~(1 << anschluss);
    }
    setValue(adresse, wert);
  }

  /**
   * Stellung von Weichenobjekten verändern und Testen der entsprechenden simulierten Hardware-Bits.
   *
   * @throws Exception
   */
  @Test
  // @Ignore
  public void testWeicheStellenPerSoftware() throws Exception // CHECKSTYLE:IGNORE
  {
    Weiche weiche1 = this.steuerung.getWeiche("show", "1");
    Weiche weiche2 = this.steuerung.getWeiche("show", "2");
    Weiche weiche3 = this.steuerung.getWeiche("show", "3");

    weiche1.setStellung(Weiche.Stellung.GERADE);
    weiche2.setStellung(Weiche.Stellung.GERADE);
    weiche3.setStellung(Weiche.Stellung.GERADE);
    Assert.assertEquals("Weiche 1", Weiche.Stellung.GERADE, getWeichenStellung(weiche1));
    Assert.assertEquals("Weiche 2", Weiche.Stellung.GERADE, getWeichenStellung(weiche2));
    Assert.assertEquals("Weiche 3", Weiche.Stellung.GERADE, getWeichenStellung(weiche3));

    weiche1.setStellung(Weiche.Stellung.ABZWEIGEND);
    Assert.assertEquals("Weiche 1", Weiche.Stellung.ABZWEIGEND, getWeichenStellung(weiche1));
    Assert.assertEquals("Weiche 2", Weiche.Stellung.GERADE, getWeichenStellung(weiche2));
    Assert.assertEquals("Weiche 3", Weiche.Stellung.GERADE, getWeichenStellung(weiche3));

    weiche2.setStellung(Weiche.Stellung.ABZWEIGEND);
    Assert.assertEquals("Weiche 1", Weiche.Stellung.ABZWEIGEND, getWeichenStellung(weiche1));
    Assert.assertEquals("Weiche 2", Weiche.Stellung.ABZWEIGEND, getWeichenStellung(weiche2));
    Assert.assertEquals("Weiche 3", Weiche.Stellung.GERADE, getWeichenStellung(weiche3));

    weiche3.setStellung(Weiche.Stellung.ABZWEIGEND);
    Assert.assertEquals("Weiche 1", Weiche.Stellung.ABZWEIGEND, getWeichenStellung(weiche1));
    Assert.assertEquals("Weiche 2", Weiche.Stellung.ABZWEIGEND, getWeichenStellung(weiche2));
    Assert.assertEquals("Weiche 3", Weiche.Stellung.ABZWEIGEND, getWeichenStellung(weiche3));

    weiche2.setStellung(Weiche.Stellung.GERADE);
    Assert.assertEquals("Weiche 1", Weiche.Stellung.ABZWEIGEND, getWeichenStellung(weiche1));
    Assert.assertEquals("Weiche 2", Weiche.Stellung.GERADE, getWeichenStellung(weiche2));
    Assert.assertEquals("Weiche 3", Weiche.Stellung.ABZWEIGEND, getWeichenStellung(weiche3));
  }

  /**
   * Weichenstellung aus simulierten Hardware-Bits ermitteln.
   *
   * @param weiche
   *          Weiche
   * @return Weichenstellung
   */
  private Weiche.Stellung getWeichenStellung(Weiche weiche) {
    Funktionsdecoder funktionsdecoder = weiche.getFunktionsdecoder();
    int adresse = funktionsdecoder.getAdresse();
    int anschluss = weiche.getAnschluss();
    int wert = getValue(adresse);
    return Weiche.Stellung.getInstance((wert >> anschluss) & 1);
  }

  /**
   * Weichenstellung mittels simulierter Hardware-Bits ändern und Zustand der entsprechenden Weichenobjekte testen.
   *
   * @throws Exception
   *           bei Fehlern
   */
  @Test
  // @Ignore
  public void testWeicheStellenPerHardware() throws Exception // CHECKSTYLE:IGNORE
  {
    Weiche weiche1 = this.steuerung.getWeiche("show", "1");
    Weiche weiche2 = this.steuerung.getWeiche("show", "2");
    Weiche weiche3 = this.steuerung.getWeiche("show", "3");

    setWeiche(weiche1, Weiche.Stellung.GERADE);
    setWeiche(weiche2, Weiche.Stellung.GERADE);
    setWeiche(weiche3, Weiche.Stellung.GERADE);
    Assert.assertEquals("Weiche 1", Weiche.Stellung.GERADE, weiche1.getStellung());
    Assert.assertEquals("Weiche 2", Weiche.Stellung.GERADE, weiche2.getStellung());
    Assert.assertEquals("Weiche 3", Weiche.Stellung.GERADE, weiche3.getStellung());

    setWeiche(weiche3, Weiche.Stellung.ABZWEIGEND);
    Assert.assertEquals("Weiche 1", Weiche.Stellung.GERADE, weiche1.getStellung());
    Assert.assertEquals("Weiche 2", Weiche.Stellung.GERADE, weiche2.getStellung());
    Assert.assertEquals("Weiche 3", Weiche.Stellung.ABZWEIGEND, weiche3.getStellung());

    setWeiche(weiche1, Weiche.Stellung.ABZWEIGEND);
    Assert.assertEquals("Weiche 1", Weiche.Stellung.ABZWEIGEND, weiche1.getStellung());
    Assert.assertEquals("Weiche 2", Weiche.Stellung.GERADE, weiche2.getStellung());
    Assert.assertEquals("Weiche 3", Weiche.Stellung.ABZWEIGEND, weiche3.getStellung());
  }

  /**
   * Simulierte Hardware-Bits für eine Weiche setzen.
   *
   * @param weiche
   *          Weiche
   * @param stellung
   *          Weichenstellung
   */
  private void setWeiche(Weiche weiche, Weiche.Stellung stellung) {
    Funktionsdecoder funktionsdecoder = weiche.getFunktionsdecoder();
    int adresse = funktionsdecoder.getAdresse();
    int anschluss = weiche.getAnschluss();
    int wert = getValue(adresse);
    wert &= ~(1 << anschluss);
    wert |= stellung.getValue() << anschluss;
    setValue(adresse, wert);
  }

  /**
   * Stellung von Signalobjekten verändern und Testen der entsprechenden simulierten Hardware-Bits.
   *
   * @throws Exception
   */
  @Test
  // @Ignore
  public void testSignalStellenPerSoftware() throws Exception // CHECKSTYLE:IGNORE
  {
    Signal signalN1 = this.steuerung.getSignal("show", "N1");
    Signal signalN2 = this.steuerung.getSignal("show", "N2");
    Signal signalF = this.steuerung.getSignal("show", "F");

    signalN1.setStellung(Signal.Stellung.HALT);
    signalN2.setStellung(Signal.Stellung.HALT);
    signalF.setStellung(Signal.Stellung.HALT);
    Assert.assertEquals("Signal N1", Signal.Stellung.HALT, getSignalStellung(signalN1));
    Assert.assertEquals("Signal N2", Signal.Stellung.HALT, getSignalStellung(signalN2));
    Assert.assertEquals("Signal F", Signal.Stellung.HALT, getSignalStellung(signalF));

    signalN1.setStellung(Signal.Stellung.LANGSAMFAHRT);
    Assert.assertEquals("Signal N1", Signal.Stellung.LANGSAMFAHRT, getSignalStellung(signalN1));
    Assert.assertEquals("Signal N2", Signal.Stellung.HALT, getSignalStellung(signalN2));
    Assert.assertEquals("Signal F", Signal.Stellung.HALT, getSignalStellung(signalF));

    signalN2.setStellung(Signal.Stellung.FAHRT);
    Assert.assertEquals("Signal N1", Signal.Stellung.LANGSAMFAHRT, getSignalStellung(signalN1));
    Assert.assertEquals("Signal N2", Signal.Stellung.FAHRT, getSignalStellung(signalN2));
    Assert.assertEquals("Signal F", Signal.Stellung.HALT, getSignalStellung(signalF));

    signalF.setStellung(Signal.Stellung.FAHRT);
    Assert.assertEquals("Signal N1", Signal.Stellung.LANGSAMFAHRT, getSignalStellung(signalN1));
    Assert.assertEquals("Signal N2", Signal.Stellung.FAHRT, getSignalStellung(signalN2));
    Assert.assertEquals("Signal F", Signal.Stellung.FAHRT, getSignalStellung(signalF));

    signalN2.setStellung(Signal.Stellung.HALT);
    Assert.assertEquals("Signal N1", Signal.Stellung.LANGSAMFAHRT, getSignalStellung(signalN1));
    Assert.assertEquals("Signal N2", Signal.Stellung.HALT, getSignalStellung(signalN2));
    Assert.assertEquals("Signal F", Signal.Stellung.FAHRT, getSignalStellung(signalF));

    signalF.setStellung(Signal.Stellung.LANGSAMFAHRT);
    Assert.assertEquals("Signal N1", Signal.Stellung.LANGSAMFAHRT, getSignalStellung(signalN1));
    Assert.assertEquals("Signal N2", Signal.Stellung.HALT, getSignalStellung(signalN2));
    Assert.assertEquals("Signal F", Signal.Stellung.LANGSAMFAHRT, getSignalStellung(signalF));
  }

  private Object getSignalStellung(Signal signal) {
    Funktionsdecoder funktionsdecoder = signal.getFunktionsdecoder();
    int adresse = funktionsdecoder.getAdresse();
    int anschluss = signal.getAnschluss();
    int bitCount = signal.getBitCount();
    int mask = ~((-1) << bitCount);
    int wert = getValue(adresse);
    return signal.getStellungForWert((wert >> anschluss) & mask);
  }

  /**
   * Signalstellung mittels simulierter Hardware-Bits ändern und Zustand der entsprechenden Signalobjekte testen.
   *
   * @throws Exception
   *           bei Fehlern
   */
  @Test
  // @Ignore
  public void testSignalStellenPerHardware() throws Exception // CHECKSTYLE:IGNORE
  {
    Signal signalN1 = this.steuerung.getSignal("show", "N1");
    Signal signalN2 = this.steuerung.getSignal("show", "N2");
    Signal signalF = this.steuerung.getSignal("show", "F");

    setSignal(signalN1, Signal.Stellung.HALT);
    setSignal(signalN2, Signal.Stellung.HALT);
    setSignal(signalF, Signal.Stellung.HALT);
    Assert.assertEquals("Signal N1", Signal.Stellung.HALT, signalN1.getStellung());
    Assert.assertEquals("Signal N2", Signal.Stellung.HALT, signalN2.getStellung());
    Assert.assertEquals("Signal F", Signal.Stellung.HALT, signalF.getStellung());

    setSignal(signalF, Signal.Stellung.FAHRT);
    Assert.assertEquals("Signal N1", Signal.Stellung.HALT, signalN1.getStellung());
    Assert.assertEquals("Signal N2", Signal.Stellung.HALT, signalN2.getStellung());
    Assert.assertEquals("Signal F", Signal.Stellung.FAHRT, signalF.getStellung());

    setSignal(signalN1, Signal.Stellung.LANGSAMFAHRT);
    Assert.assertEquals("Signal N1", Signal.Stellung.LANGSAMFAHRT, signalN1.getStellung());
    Assert.assertEquals("Signal N2", Signal.Stellung.HALT, signalN2.getStellung());
    Assert.assertEquals("Signal F", Signal.Stellung.FAHRT, signalF.getStellung());
  }

  private void setSignal(Signal signal, Signal.Stellung stellung) {
    Funktionsdecoder funktionsdecoder = signal.getFunktionsdecoder();
    int adresse = funktionsdecoder.getAdresse();
    int anschluss = signal.getAnschluss();
    int bitCount = signal.getBitCount();
    int mask = ~((-1) << bitCount);
    int wert = getValue(adresse);
    wert &= ~(mask << anschluss);
    wert |= signal.getWertForStellung(stellung) << anschluss;
    setValue(adresse, wert);
  }

  @Override
  public void close() {
  }

  @Override
  public int getValue(int address) {
    assert address >= 0 && address <= SelectrixConnection.MAX_ADDRESSE : "Ungueltige Adresse";

    Integer value = this.cache.get(address);

    assert value != null : "Nicht überwachte Adresse";

    return value;
  }

  /**
   * {@inheritDoc}
   *
   * @see de.gedoplan.v5t11.selectrix.SelectrixConnection#getValue(int, boolean)
   */
  @Override
  public int getValue(int address, boolean refresh) {
    return getValue(address);
  }

  @Override
  public void setValue(int address, int value) {
    assert address >= 0 && address <= SelectrixConnection.MAX_ADDRESSE : "Ungueltige Adresse";
    assert value >= 0 && value < 256 : "Ungueltiger Wert";
    assert this.cache.containsKey(address) : "Nicht überwachte Adresse";

    this.cache.put(address, value);

    this.steuerung.onMessage(new SelectrixMessage(address, value));
  }

  @Override
  public void start(String serialPortName, int serialPortSpeed, String interfaceTyp, Collection<Integer> adressen) {
    for (int a : adressen) {
      this.cache.put(a, 0);
    }
  }

  @Override
  public void stop() {
  }

  @Override
  public void addWatchAddress(int address) {
    this.cache.putIfAbsent(address, 0);
  }
}
