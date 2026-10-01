package de.gedoplan.v5t11.leitstand.webui;

import de.gedoplan.v5t11.leitstand.entity.fahrstrasse.Fahrstrasse;
import de.gedoplan.v5t11.leitstand.entity.fahrstrasse.Fahrstrassenelement;
import de.gedoplan.v5t11.leitstand.entity.fahrweg.Gleis;
import de.gedoplan.v5t11.leitstand.entity.fahrweg.Signal;
import de.gedoplan.v5t11.leitstand.entity.fahrweg.Weiche;
import de.gedoplan.v5t11.leitstand.entity.stellwerk.StellwerkDkw2;
import de.gedoplan.v5t11.leitstand.entity.stellwerk.StellwerkEinfachWeiche;
import de.gedoplan.v5t11.leitstand.entity.stellwerk.StellwerkElement;
import de.gedoplan.v5t11.leitstand.entity.stellwerk.StellwerkGleis;
import de.gedoplan.v5t11.leitstand.entity.stellwerk.StellwerkRichtung;
import de.gedoplan.v5t11.leitstand.service.FahrstrassenManager;
import de.gedoplan.v5t11.util.domain.attribute.WeichenStellung;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

/**
 * Baut die Zeichenbefehle (JSON, ein Objekt pro {@link StellwerkElement}) für {@code stellwerk-draw.js} auf – aus
 * {@code PushService.createDrawCommand}/{@code addFahrstrasse}/{@code addRichtungen}/{@code addSignal} extrahiert
 * (Teilschritt 3c), fachlich unverändert, aber framework-neutral (kein {@code PushService}/WebSocket-Bezug mehr).
 * {@code PushService} selbst bleibt bis zur Löschung in 3e unangetastet (Rollback-Pfad).
 * <p>
 * Das Fahrstraßen-Vorschlags-Overlay (Attribute {@code f="V"}/{@code f="A"}, im Original über das session-keyed
 * {@code StellwerkVorschlagService} bezogen) wird seit Teilschritt 3d über den {@code vorschlagResolver}-Parameter von
 * {@link #createDrawCommands} abgebildet: Der Auswahl-/Vorschlagszustand lebt view-lokal auf {@code StellwerkView}
 * (kein Session-Keying mehr nötig, da jede View-Instanz ohnehin genau einer Browser-Session entspricht). Der Zustand
 * "Gleis ist Teil einer bereits reservierten Fahrstrasse" ({@code f} = Reservierungstyp) braucht dagegen nach wie vor
 * keinen Session-/View-Bezug und hat Vorrang vor dem Vorschlags-Overlay (siehe {@link #addFahrstrasse}).
 */
@ApplicationScoped
public class StellwerkDrawCommandBuilder {

  /**
   * Ergebnis eines {@code vorschlagResolver} (siehe {@link #createDrawCommands}): {@code typ} ist {@code "V"}
   * (aktuell angewählter Vorschlag) oder {@code "A"} (Alternative), {@code zaehlrichtung} identisch zur gleichnamigen
   * Eigenschaft von {@link de.gedoplan.v5t11.leitstand.entity.fahrstrasse.Fahrstrassenelement}.
   */
  public record FahrstrassenVorschlag(String typ, boolean zaehlrichtung) {
  }

  public static final List<String> FARBEN_SPERR_SH0 = List.of("r");
  public static final List<String> FARBEN_SPERR_SH1 = List.of("w");
  public static final List<String> FARBEN_HAUPT_HP0 = List.of("r", "-");
  public static final List<String> FARBEN_HAUPT_HP1 = List.of("g", "-");
  public static final List<String> FARBEN_HAUPT_HP2 = List.of("g", "y");
  public static final List<String> FARBEN_HAUPT_HP0_SH1 = List.of("r", "w");
  public static final List<String> FARBEN_BLOCK_HP0 = List.of("-", "r");
  public static final List<String> FARBEN_BLOCK_HP1 = List.of("-", "g");

  public static final String ATTR_UIID = "uiId";
  public static final String ATTR_GLEIS_BESETZT = "b";
  public static final String ATTR_AKTIVE_RICHTUNGEN = "a";
  public static final String ATTR_INAKTIVE_RICHTUNGEN = "i";
  public static final String ATTR_NAME = "n";
  public static final String ATTR_NAMENS_POSITION = "p";
  public static final String ATTR_FAHRSTRASSEN_TYP = "f";
  public static final String ATTR_FAHRSTRASSEN_ZAEHLRICHTUNG = "z";
  public static final String ATTR_SIGNAL_LICHTER = "l";
  public static final String ATTR_SIGNAL_POSITION = "s";

  @Inject
  FahrstrassenManager fahrstrassenManager;

  /**
   * Zeichenbefehle für mehrere Stellwerkselemente auf einmal erzeugen (ein Redraw-Batch). Die aktuell reservierten
   * Fahrstrassen werden dabei nur einmal für den ganzen Batch ermittelt, nicht pro Element.
   *
   * @param vorschlagResolver liefert für ein Gleis das Fahrstraßen-Vorschlags-Overlay (oder {@code null}, falls
   *          keines gilt) – view-lokaler Zustand, siehe Klassen-Javadoc.
   */
  public JsonArray createDrawCommands(Collection<StellwerkElement> elemente, Function<Gleis, FahrstrassenVorschlag> vorschlagResolver) {
    List<Fahrstrasse> reservierteFahrstrassen = this.fahrstrassenManager.getReservierteFahrstrassen();

    JsonArrayBuilder builder = Json.createArrayBuilder();
    elemente.forEach(element -> builder.add(createDrawCommand(element, reservierteFahrstrassen, vorschlagResolver)));
    return builder.build();
  }

  private JsonObject createDrawCommand(StellwerkElement element, List<Fahrstrasse> reservierteFahrstrassen, Function<Gleis, FahrstrassenVorschlag> vorschlagResolver) {
    JsonObjectBuilder builder = Json.createObjectBuilder();
    builder.add(ATTR_UIID, element.getUiId());

    Gleis gleis = null;
    String name = null;
    StellwerkRichtung namensPosition = null;

    List<StellwerkRichtung> aktiveRichtungen = null;
    List<StellwerkRichtung> inaktiveRichtungen = null;

    if (element instanceof StellwerkGleis stellwerkGleis) {
      if (stellwerkGleis.isLabel()) {
        name = stellwerkGleis.getName();
        namensPosition = stellwerkGleis.getLabelPos();
      }

      aktiveRichtungen = stellwerkGleis.getRichtungen();

      gleis = stellwerkGleis.findGleis();

    } else if (element instanceof StellwerkEinfachWeiche stellwerkEinfachWeiche) {
      name = stellwerkEinfachWeiche.getName();
      namensPosition = stellwerkEinfachWeiche.getLabelPos();

      aktiveRichtungen = new ArrayList<>();
      inaktiveRichtungen = new ArrayList<>();
      addRichtungen(stellwerkEinfachWeiche.findWeiche(), stellwerkEinfachWeiche.getGeradeRichtung(), stellwerkEinfachWeiche.getAbzweigendRichtung(), aktiveRichtungen, inaktiveRichtungen);
      if (stellwerkEinfachWeiche.isStammIstEinfahrt()) {
        aktiveRichtungen.add(0, stellwerkEinfachWeiche.getStammRichtung());
      } else {
        aktiveRichtungen.add(stellwerkEinfachWeiche.getStammRichtung());
      }

      gleis = stellwerkEinfachWeiche.findGleis();

    } else if (element instanceof StellwerkDkw2 stellwerkDkw2) {
      name = stellwerkDkw2.getName();
      namensPosition = stellwerkDkw2.getLabelPos();

      aktiveRichtungen = new ArrayList<>();
      inaktiveRichtungen = new ArrayList<>();
      addRichtungen(stellwerkDkw2.findWeicheA(), stellwerkDkw2.getGeradeRichtung()[0], stellwerkDkw2.getAbzweigRichtung()[0], aktiveRichtungen, inaktiveRichtungen);
      addRichtungen(stellwerkDkw2.findWeicheB(), stellwerkDkw2.getGeradeRichtung()[1], stellwerkDkw2.getAbzweigRichtung()[1], aktiveRichtungen, inaktiveRichtungen);

      gleis = stellwerkDkw2.findGleis();
    }

    if (gleis != null) {
      builder.add(ATTR_GLEIS_BESETZT, gleis.isBesetzt());

      addFahrstrasse(gleis, builder, reservierteFahrstrassen, vorschlagResolver);
    }

    if (aktiveRichtungen != null) {
      builder.add(ATTR_AKTIVE_RICHTUNGEN, Json.createArrayBuilder(aktiveRichtungen.stream().map(Enum::name).toList()));
    }

    if (inaktiveRichtungen != null) {
      builder.add(ATTR_INAKTIVE_RICHTUNGEN, Json.createArrayBuilder(inaktiveRichtungen.stream().map(Enum::name).toList()));
    }

    if (element.getSignalId() != null) {
      addSignal(element.findSignal(), element.getSignalPosition(), builder);
    }

    if (name != null && namensPosition != null) {
      builder.add(ATTR_NAME, name);
      builder.add(ATTR_NAMENS_POSITION, namensPosition.toString());
    }

    return builder.build();
  }

  private void addFahrstrasse(Gleis gleis, JsonObjectBuilder builder, List<Fahrstrasse> reservierteFahrstrassen, Function<Gleis, FahrstrassenVorschlag> vorschlagResolver) {
    Fahrstrassenelement fahrstrassenelement = null;
    String fahrstrassenTyp = null;

    for (Fahrstrasse fs : reservierteFahrstrassen) {
      fahrstrassenelement = fs.getElement(gleis, true);
      if (fahrstrassenelement != null) {
        fahrstrassenTyp = fs.getReservierungsTyp().toString();
        break;
      }
    }

    if (fahrstrassenTyp != null) {
      builder.add(ATTR_FAHRSTRASSEN_TYP, fahrstrassenTyp);
      builder.add(ATTR_FAHRSTRASSEN_ZAEHLRICHTUNG, fahrstrassenelement.isZaehlrichtung());
      return;
    }

    // Keine reservierte Fahrstrasse auf diesem Gleis - Vorschlags-Overlay prüfen (hat niedrigere Prioriät).
    if (vorschlagResolver != null) {
      FahrstrassenVorschlag vorschlag = vorschlagResolver.apply(gleis);
      if (vorschlag != null) {
        builder.add(ATTR_FAHRSTRASSEN_TYP, vorschlag.typ());
        builder.add(ATTR_FAHRSTRASSEN_ZAEHLRICHTUNG, vorschlag.zaehlrichtung());
      }
    }
  }

  private static void addRichtungen(Weiche weiche, StellwerkRichtung geradeRichtung, StellwerkRichtung abzweigendRichtung, List<StellwerkRichtung> aktiveRichtungen,
    List<StellwerkRichtung> inaktiveRichtungen) {
    if (weiche.getStellung().equals(WeichenStellung.GERADE)) {
      aktiveRichtungen.add(geradeRichtung);
      inaktiveRichtungen.add(abzweigendRichtung);
    } else {
      aktiveRichtungen.add(abzweigendRichtung);
      inaktiveRichtungen.add(geradeRichtung);
    }
  }

  private static void addSignal(Signal signal, String signalPosition, JsonObjectBuilder builder) {
    if (signal.getTyp() == null) {
      return;
    }

    List<String> lichter = switch (signal.getTyp()) {
      case SPERRSIGNAL -> switch (signal.getStellung()) {
        default -> FARBEN_SPERR_SH0;
        case FAHRT, LANGSAMFAHRT, RANGIERFAHRT -> FARBEN_SPERR_SH1;
      };
      case HAUPTSPERRSIGNAL -> switch (signal.getStellung()) {
        default -> FARBEN_HAUPT_HP0;
        case FAHRT -> FARBEN_HAUPT_HP1;
        case LANGSAMFAHRT -> FARBEN_HAUPT_HP2;
        case RANGIERFAHRT -> FARBEN_HAUPT_HP0_SH1;
      };
      case HAUPTSIGNAL_RT_GE -> switch (signal.getStellung()) {
        default -> FARBEN_HAUPT_HP0;
        case FAHRT, LANGSAMFAHRT -> FARBEN_HAUPT_HP2;
      };
      case HAUPTSIGNAL_RT_GN -> switch (signal.getStellung()) {
        default -> FARBEN_BLOCK_HP0;
        case FAHRT, LANGSAMFAHRT -> FARBEN_BLOCK_HP1;
      };
      case HAUPTSIGNAL_RT_GE_GN -> switch (signal.getStellung()) {
        default -> FARBEN_HAUPT_HP0;
        case FAHRT -> FARBEN_BLOCK_HP1;
        case LANGSAMFAHRT -> FARBEN_HAUPT_HP2;
      };
      default -> List.of();
    };

    if (!lichter.isEmpty()) {
      builder.add(ATTR_SIGNAL_LICHTER, Json.createArrayBuilder(lichter));
      builder.add(ATTR_SIGNAL_POSITION, signalPosition != null ? signalPosition : "N");
    }
  }
}
