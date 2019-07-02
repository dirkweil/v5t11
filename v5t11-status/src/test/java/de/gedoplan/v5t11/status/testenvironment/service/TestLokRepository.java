package de.gedoplan.v5t11.status.testenvironment.service;

import de.gedoplan.v5t11.status.entity.SystemTyp;
import de.gedoplan.v5t11.status.entity.lok.Lok;
import de.gedoplan.v5t11.status.persistence.LokRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Specializes;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;

@ApplicationScoped
@Specializes
public class TestLokRepository extends LokRepository {

  public static final Lok lok103_003_0 = new Lok("103 003-0", null, SystemTyp.DCC, false, 1103, 126,
      1, new Lok.FunktionConfig("Maschinenraumbeleuchtung", false),
      2, new Lok.FunktionConfig("Betriebsgeräuschgeräusch", false),
      3, new Lok.FunktionConfig("Pfeife", true),
      4, new Lok.FunktionConfig("ABV aus", false),
      5, new Lok.FunktionConfig("Führerstandsbeleuchtung", false),
      6, new Lok.FunktionConfig("Spitzensignal 1 aus", false),
      7, new Lok.FunktionConfig("Schaffnerpfiff", true),
      8, new Lok.FunktionConfig("Spitzensignal 2 aus", false),
      9, new Lok.FunktionConfig("Bremsenquietschen aus", false),
      10, new Lok.FunktionConfig("Bahnhofsansage", true),
      11, new Lok.FunktionConfig("Lüfter", false),
      12, new Lok.FunktionConfig("Sanden", true),
      13, new Lok.FunktionConfig("Bahnhofsansage", true),
      14, new Lok.FunktionConfig("Bahnhofsansage", true),
      15, new Lok.FunktionConfig("Lüfter", false));
  public static final Lok lok103_118_6 = new Lok("103 118-6", "DHL100", SystemTyp.SX1, false, 13, 31);
  public static final Lok lok110_222_7 = new Lok("110 222-7", "DHL100", SystemTyp.SX1, false, 17, 31,
      1, new Lok.FunktionConfig("Horn", true, true));
  public static final Lok lok111_205_1 = new Lok("111 205-1", "DHL100", SystemTyp.SX1, false, 15, 31);
  public static final Lok lok120_002_1 = new Lok("120 002-1", "Tr66830", SystemTyp.SX1, false, 18, 31);
  public static final Lok lok14283 = new Lok("14283", "DHL050", SystemTyp.SX1, false, 3, 31);
  public static final Lok lok151_032_0 = new Lok("151 032-0", "DHL100", SystemTyp.SX1, false, 20, 31);
  public static final Lok lok151_073_4 = new Lok("151 073-4", "DH10", SystemTyp.SX1, false, 22, 31);
  public static final Lok lok212_216_6 = new Lok("212 216-6", "Tr66825", SystemTyp.SX1, false, 5, 31);
  public static final Lok lok216_xxx_x = new Lok("216 xxx-x", "DHL100", SystemTyp.SX1, false, 2, 31);
  public static final Lok lok217_001_7 = new Lok("217 001-7", null, SystemTyp.SX2, false, 1217, 127,
      2, new Lok.FunktionConfig("Motorgeräusch", false),
      7, new Lok.FunktionConfig("Pfiff", true),
      9, new Lok.FunktionConfig("Bahnsteigansage", true));
  public static final Lok lok221_137_3 = new Lok("221 137-3", "Tr66835", SystemTyp.SX1, false, 19, 31);
  public static final Lok lok323_673_4 = new Lok("323 673-4", "DHL050", SystemTyp.SX1, false, 4, 31);
  public static final Lok lok332_262_5 = new Lok("332 262-5", "DHL100", SystemTyp.SX1, false, 14, 31);
  public static final Lok lok430_119_8 = new Lok("430 119-8", "DHL100", SystemTyp.SX1, false, 16, 31);
  public static final Lok lok614_083_4 = new Lok("614 083-4", "Tr66832", SystemTyp.SX1, false, 11, 31);
  public static final Lok lok89_005 = new Lok("89 005", "DH05", SystemTyp.SX1, false, 7, 31);
  public static final Lok lokET_90_5019 = new Lok("ET 90.5019", "DHL100", SystemTyp.SX1, false, 21, 31);
  public static final Lok lokVT_11_5019 = new Lok("VT 11.5019", "DHL100", SystemTyp.SX1, false, 1, 31);
  public static final Lok lokVT_98_9731 = new Lok("VT 98.9731", "DHL100", SystemTyp.SX1, false, 6, 31);

  private ConcurrentMap<String, Lok> loks = new ConcurrentHashMap<>();

  @PostConstruct
  void postConstruct() {
    persist(lok103_003_0);
    persist(lok103_118_6);
    persist(lok110_222_7);
    persist(lok111_205_1);
    persist(lok120_002_1);
    persist(lok14283);
    persist(lok151_032_0);
    persist(lok151_073_4);
    persist(lok212_216_6);
    persist(lok216_xxx_x);
    persist(lok217_001_7);
    persist(lok221_137_3);
    persist(lok323_673_4);
    persist(lok332_262_5);
    persist(lok430_119_8);
    persist(lok614_083_4);
    persist(lok89_005);
    persist(lokET_90_5019);
    persist(lokVT_11_5019);
    persist(lokVT_98_9731);
  }

  @Override
  public List<Lok> findAll() {
    return new ArrayList<>(this.loks.values());
  }

  @Override
  public void persist(Lok lok) {
    if (this.loks.putIfAbsent(lok.getId(), lok) != null) {
      throw new EntityExistsException();
    }
  }

  @Override
  public Lok merge(Lok lok) {
    this.loks.put(lok.getId(), lok);
    return lok;
  }

  @Override
  public boolean removeById(String id) {
    return this.loks.remove(id) != null;
  }

  /*
   * Keinen EntityManager nutzen.
   * Die überschriebene Methode fordert einen EM per Injektion an, was im Testumfeld fehlschlägt.
   */
  @Override
  public void setEntityManager(EntityManager entityManager) {
  }

}
