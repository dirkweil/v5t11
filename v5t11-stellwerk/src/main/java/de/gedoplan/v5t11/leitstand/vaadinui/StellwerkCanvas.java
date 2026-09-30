package de.gedoplan.v5t11.leitstand.vaadinui;

import de.gedoplan.v5t11.leitstand.entity.stellwerk.StellwerkElement;

import com.vaadin.flow.component.ClickNotifier;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.dom.Element;

import lombok.Getter;

/**
 * Eine einzelne Gleisplan-Zelle des Stellwerk-Grids, als {@code <canvas>}-Element. Vaadin Flow hat keine eingebaute
 * Canvas-Komponente, daher dieser schlanke Low-Level-Wrapper (Standard-Vaadin-Idiom über den geschützten
 * {@code Component(Element)}-Konstruktor).
 * <p>
 * Positionierung erfolgt wie im JSF-Original über {@code grid-row-start}/{@code grid-column-start} auf Basis der
 * unveränderten {@link StellwerkElement#getZeilenNr()}/{@link StellwerkElement#getSpaltenNr()}. Das tatsächliche
 * Zeichnen (Teilschritt 3c) erfolgt extern über {@code stellwerk-draw.js}, das per {@code id} (= {@code uiId}) auf
 * das DOM-Element zugreift.
 */
public class StellwerkCanvas extends Component implements ClickNotifier<StellwerkCanvas> {

  @Getter
  private final StellwerkElement stellwerkElement;

  public StellwerkCanvas(StellwerkElement stellwerkElement) {
    super(new Element("canvas"));
    this.stellwerkElement = stellwerkElement;

    getElement().setAttribute("id", stellwerkElement.getUiId());
    getElement().setAttribute("width", "1000");
    getElement().setAttribute("height", "1000");
    getElement().getStyle().set("grid-row-start", String.valueOf(stellwerkElement.getZeilenNr()));
    getElement().getStyle().set("grid-column-start", String.valueOf(stellwerkElement.getSpaltenNr()));
  }
}
