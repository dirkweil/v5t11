package de.gedoplan.v5t11.status.vaadinui;

import de.gedoplan.v5t11.status.entity.Steuerung;
import de.gedoplan.v5t11.status.entity.baustein.Zentrale;
import de.gedoplan.v5t11.status.entity.fahrweg.Gleis;
import de.gedoplan.v5t11.status.entity.fahrweg.geraet.Signal;
import de.gedoplan.v5t11.status.entity.fahrweg.geraet.Weiche;
import de.gedoplan.v5t11.status.entity.fahrzeug.Fahrzeugdecoder;
import de.gedoplan.v5t11.status.webui.VaadinChangePushBroadcaster;
import de.gedoplan.v5t11.util.cdi.Changed;
import de.gedoplan.v5t11.util.cdi.EventFirer;
import de.gedoplan.v5t11.util.domain.attribute.DecoderAdr;
import de.gedoplan.v5t11.util.domain.attribute.SignalStellung;
import de.gedoplan.v5t11.util.domain.attribute.SystemTyp;
import de.gedoplan.v5t11.util.domain.attribute.WeichenStellung;
import de.gedoplan.v5t11.vaadincommon.ui.MainLayout;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.FieldSet;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.slider.IntegerSlider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;

/**
 * Vaadin-Pendant zu {@code view/systemControl.xhtml} / {@code SystemControlPresenter}. Anders als bei
 * {@code SystemStatusView} wird der bestehende {@code SystemControlPresenter} NICHT wiederverwendet, da er
 * {@code @SessionScoped} ist und direkt {@code FacesContext}/{@code FacesMessage} nutzt (JSF-spezifisch). Diese View
 * ruft stattdessen dieselben Fachklassen ({@link Steuerung}, {@link Zentrale}, {@link Gleis}, {@link Weiche},
 * {@link Signal}, {@link Fahrzeugdecoder}) direkt auf, mit View-lokalem statt sessionweitem Auswahl-State.
 * <p>
 * Verhalten wird bewusst 1:1 vom JSF-Original übernommen: keine Confirm-Dialoge, jede Aktion wirkt sofort. Der
 * bestehende {@code SystemControlPresenter}/{@code systemControl.xhtml} bleiben unverändert (Rollback-Pfad).
 */
@Route(value = "system-control", layout = MainLayout.class)
@PageTitle("System-Control - v5t11")
public class SystemControlView extends VerticalLayout {

  @Inject
  Steuerung steuerung;

  @Inject
  EventFirer eventFirer;

  @Inject
  VaadinChangePushBroadcaster pushBroadcaster;

  private final Consumer<Object> changeListener = this::onChanged;

  private String bereich;
  private Gleis gleis;
  private Weiche weiche;
  private Signal signal;
  private Fahrzeugdecoder lok;

  private Checkbox connectedField;
  private Checkbox gleisspannungField;
  private ComboBox<String> bereichField;
  private ComboBox<Gleis> gleisField;
  private Checkbox gleisBesetztField;
  private ComboBox<Weiche> weicheField;
  private RadioButtonGroup<WeichenStellung> weichenStellungField;
  private ComboBox<Signal> signalField;
  private RadioButtonGroup<SignalStellung> signalStellungField;
  private ComboBox<Fahrzeugdecoder> lokField;
  private Checkbox lokAktivField;
  private IntegerSlider lokFahrstufeField;
  private Span lokFahrstufeValueField;
  private Checkbox lokRueckwaertsField;
  private Checkbox lokLichtField;
  private final List<Checkbox> lokFunktionFields = new ArrayList<>();

  @PostConstruct
  void init() {
    setSizeFull();
    getStyle().set("overflow", "auto");

    resetBereich();

    add(buildAllgemeinSection());
    add(buildFahrwegSection());
    add(buildLokSection());
  }

  @Override
  protected void onAttach(AttachEvent attachEvent) {
    super.onAttach(attachEvent);
    this.pushBroadcaster.addListener(this.changeListener);
  }

  @Override
  protected void onDetach(DetachEvent detachEvent) {
    this.pushBroadcaster.removeListener(this.changeListener);
    super.onDetach(detachEvent);
  }

  private void onChanged(Object changed) {
    getUI().ifPresent(ui -> ui.access(() -> {
      if (changed instanceof Zentrale) {
        refreshZentrale();
      } else if (changed != null && changed == this.gleis) {
        refreshGleis();
      } else if (changed != null && changed == this.weiche) {
        refreshWeiche();
      } else if (changed != null && changed == this.signal) {
        refreshSignal();
      } else if (changed != null && changed == this.lok) {
        refreshLok();
      }
    }));
  }

  // ---------- Layout-Helfer ----------

  private FormLayout newLabelledFormLayout() {
    FormLayout form = new FormLayout();
    form.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));
    form.setLabelWidth("160px");
    return form;
  }

  // ---------- Allgemein ----------

  private FieldSet buildAllgemeinSection() {
    FieldSet fieldset = new FieldSet();
    fieldset.setLegendText("Allgemein");
    fieldset.setWidthFull();

    this.connectedField = new Checkbox("nicht verbunden");
    this.connectedField.addClassName("toggle-buttons");
    this.connectedField.addValueChangeListener(event -> {
      if (event.isFromClient()) {
        setZentraleConnected(event.getValue());
      }
    });

    this.gleisspannungField = new Checkbox("aus");
    this.gleisspannungField.addClassName("toggle-buttons");
    this.gleisspannungField.addValueChangeListener(event -> {
      this.gleisspannungField.setLabel(event.getValue() ? "an" : "aus");
      if (event.isFromClient()) {
        this.steuerung.getZentrale().setGleisspannung(event.getValue());
      }
    });

    FormLayout form = newLabelledFormLayout();
    form.addFormItem(this.connectedField, "Anlagenverbindung:");
    form.addFormItem(this.gleisspannungField, "Gleisspannung:");
    fieldset.add(form);

    refreshZentrale();

    return fieldset;
  }

  private void setZentraleConnected(boolean connected) {
    Zentrale zentrale = this.steuerung.getZentrale();
    if (!zentrale.isEchtbetrieb()) {
      if (connected) {
        zentrale.open(null);
      } else {
        zentrale.close();
      }
      refreshZentrale();
    }
  }

  private void refreshZentrale() {
    Zentrale zentrale = this.steuerung.getZentrale();
    boolean echtbetrieb = zentrale.isEchtbetrieb();
    boolean connected = zentrale.isConnected();
    this.connectedField.setValue(connected);
    this.connectedField.setEnabled(!echtbetrieb);
    this.connectedField.setLabel(connected ? (echtbetrieb ? zentrale.getPortName() : "Dummyport") : "nicht verbunden");
    this.gleisspannungField.setValue(zentrale.isGleisspannung());
  }

  // ---------- Fahrweg ----------

  private FieldSet buildFahrwegSection() {
    FieldSet fieldset = new FieldSet();
    fieldset.setLegendText("Fahrweg");
    fieldset.setWidthFull();

    this.bereichField = new ComboBox<>();
    this.bereichField.setItems(this.steuerung.getBereiche());
    this.bereichField.setValue(this.bereich);
    this.bereichField.addValueChangeListener(event -> {
      if (event.isFromClient() && event.getValue() != null) {
        selectBereich(event.getValue());
      }
    });

    this.gleisField = new ComboBox<>();
    this.gleisField.setItemLabelGenerator(Gleis::getName);
    this.gleisField.addValueChangeListener(event -> {
      if (event.isFromClient()) {
        selectGleis(event.getValue());
      }
    });

    this.gleisBesetztField = new Checkbox("frei");
    this.gleisBesetztField.addClassName("toggle-buttons");
    this.gleisBesetztField.addValueChangeListener(event -> {
      this.gleisBesetztField.setLabel(event.getValue() ? "besetzt" : "frei");
      if (event.isFromClient() && this.gleis != null) {
        if (this.gleis.changeBesetzt(event.getValue())) {
          this.eventFirer.fire(this.gleis, Changed.Literal.INSTANCE);
        }
      }
    });

    this.weicheField = new ComboBox<>();
    this.weicheField.setItemLabelGenerator(Weiche::getName);
    this.weicheField.addValueChangeListener(event -> {
      if (event.isFromClient()) {
        selectWeiche(event.getValue());
      }
    });

    this.weichenStellungField = new RadioButtonGroup<>();
    this.weichenStellungField.addClassName("toggle-buttons");
    this.weichenStellungField.setItems(WeichenStellung.values());
    this.weichenStellungField.addValueChangeListener(event -> {
      if (event.isFromClient() && this.weiche != null && event.getValue() != null) {
        this.weiche.setStellung(event.getValue());
      }
    });

    this.signalField = new ComboBox<>();
    this.signalField.setItemLabelGenerator(Signal::getName);
    this.signalField.addValueChangeListener(event -> {
      if (event.isFromClient()) {
        selectSignal(event.getValue());
      }
    });

    this.signalStellungField = new RadioButtonGroup<>();
    this.signalStellungField.addClassName("toggle-buttons");
    this.signalStellungField.addValueChangeListener(event -> {
      if (event.isFromClient() && this.signal != null && event.getValue() != null) {
        this.signal.setStellung(event.getValue());
      }
    });

    Button alleSignaleHaltButton = new Button("alle: H", event -> {
      if (this.bereich != null) {
        this.steuerung.getSignale(this.bereich).forEach(s -> s.setStellung(SignalStellung.HALT));
        refreshSignal();
      }
    });

    populateGleisField();
    populateWeicheField();
    populateSignalField();

    FormLayout form = newLabelledFormLayout();
    form.addFormItem(this.bereichField, "Bereich:");
    form.addFormItem(new HorizontalLayout(this.gleisField, this.gleisBesetztField), "Gleis:");
    form.addFormItem(new HorizontalLayout(this.weicheField, this.weichenStellungField), "Weiche:");
    form.addFormItem(new HorizontalLayout(this.signalField, this.signalStellungField, alleSignaleHaltButton), "Signal:");
    fieldset.add(form);

    return fieldset;
  }

  private void resetBereich() {
    Collection<String> bereiche = this.steuerung.getBereiche();
    this.bereich = bereiche.isEmpty() ? null : bereiche.iterator().next();
  }

  private void selectBereich(String bereich) {
    this.bereich = bereich;
    populateGleisField();
    populateWeicheField();
    populateSignalField();
  }

  private void populateGleisField() {
    Collection<Gleis> gleise = this.bereich != null ? this.steuerung.getGleise(this.bereich) : Collections.emptySet();
    this.gleisField.setItems(gleise);
    this.gleis = gleise.isEmpty() ? null : gleise.iterator().next();
    this.gleisField.setValue(this.gleis);
    refreshGleis();
  }

  private void selectGleis(Gleis gleis) {
    this.gleis = gleis;
    refreshGleis();
  }

  private void refreshGleis() {
    boolean echtbetrieb = this.steuerung.getZentrale().isEchtbetrieb();
    this.gleisBesetztField.setValue(this.gleis != null && this.gleis.isBesetzt());
    this.gleisBesetztField.setEnabled(this.gleis != null && !echtbetrieb);
  }

  private void populateWeicheField() {
    Collection<Weiche> weichen = this.bereich != null ? this.steuerung.getWeichen(this.bereich) : Collections.emptySet();
    this.weicheField.setItems(weichen);
    this.weiche = weichen.isEmpty() ? null : weichen.iterator().next();
    this.weicheField.setValue(this.weiche);
    refreshWeiche();
  }

  private void selectWeiche(Weiche weiche) {
    this.weiche = weiche;
    refreshWeiche();
  }

  private void refreshWeiche() {
    this.weichenStellungField.setValue(this.weiche != null ? this.weiche.getStellung() : WeichenStellung.GERADE);
    this.weichenStellungField.setEnabled(this.weiche != null);
  }

  private void populateSignalField() {
    Collection<Signal> signale = this.bereich != null ? this.steuerung.getSignale(this.bereich) : Collections.emptySet();
    this.signalField.setItems(signale);
    this.signal = signale.isEmpty() ? null : signale.iterator().next();
    this.signalField.setValue(this.signal);
    refreshSignal();
  }

  private void selectSignal(Signal signal) {
    this.signal = signal;
    refreshSignal();
  }

  private void refreshSignal() {
    Set<SignalStellung> erlaubte = this.signal != null ? this.signal.getTyp().getErlaubteStellungen() : Collections.emptySet();
    this.signalStellungField.setItems(erlaubte);
    this.signalStellungField.setValue(this.signal != null ? this.signal.getStellung() : null);
    this.signalStellungField.setEnabled(this.signal != null);
  }

  // ---------- Lok ----------

  private FieldSet buildLokSection() {
    FieldSet fieldset = new FieldSet();
    fieldset.setLegendText("Lok");
    fieldset.setWidthFull();

    this.lokField = new ComboBox<>();
    this.lokField.setItemLabelGenerator(this::formatLokLabel);
    this.lokField.setAllowCustomValue(true);
    this.lokField.setItems(this.steuerung.getFahrzeugdecoder());
    this.lokField.addCustomValueSetListener(event -> selectLokByText(event.getDetail()));
    this.lokField.addValueChangeListener(event -> {
      if (event.isFromClient() && event.getValue() != null) {
        selectLok(event.getValue());
      }
    });

    this.lokAktivField = new Checkbox("inaktiv");
    this.lokAktivField.addClassName("toggle-buttons");
    this.lokAktivField.addValueChangeListener(event -> {
      this.lokAktivField.setLabel(event.getValue() ? "aktiv" : "inaktiv");
      if (event.isFromClient() && this.lok != null) {
        this.lok.setAktiv(event.getValue());
      }
    });

    this.lokFahrstufeField = new IntegerSlider();
    this.lokFahrstufeField.setMin(0);
    this.lokFahrstufeValueField = new Span("0");
    this.lokFahrstufeValueField.getStyle().set("width", "3em").set("flex", "0 0 auto");
    this.lokFahrstufeField.addValueChangeListener(event -> {
      this.lokFahrstufeValueField.setText(String.valueOf(event.getValue() != null ? event.getValue() : 0));
      if (event.isFromClient() && this.lok != null && event.getValue() != null) {
        int fahrstufe = event.getValue();
        int max = this.lok.getId().getSystemTyp().getMaxFahrstufe();
        if (fahrstufe >= 0 && fahrstufe <= max) {
          this.lok.setFahrstufe(fahrstufe);
        } else {
          Notification notification = Notification.show("ungültige Fahrstufe: " + fahrstufe, 3000, Notification.Position.BOTTOM_START);
          notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
          this.lokFahrstufeField.setValue(this.lok.getFahrstufe());
        }
      }
    });

    this.lokRueckwaertsField = new Checkbox("vorwärts");
    this.lokRueckwaertsField.addClassName("toggle-buttons");
    this.lokRueckwaertsField.addValueChangeListener(event -> {
      this.lokRueckwaertsField.setLabel(event.getValue() ? "rückwärts" : "vorwärts");
      if (event.isFromClient() && this.lok != null) {
        this.lok.setRueckwaerts(event.getValue());
      }
    });

    this.lokLichtField = new Checkbox("Licht");
    this.lokLichtField.addClassName("toggle-buttons");
    this.lokLichtField.addValueChangeListener(event -> {
      if (event.isFromClient() && this.lok != null) {
        this.lok.setLicht(event.getValue());
      }
    });

    FlexLayout funktionenLayout = new FlexLayout();
    funktionenLayout.getStyle().set("flex-wrap", "wrap").set("gap", "0.5rem");
    funktionenLayout.add(this.lokLichtField);
    for (int nr = 0; nr < 16; nr++) {
      int mask = 1 << nr;
      Checkbox funktionField = new Checkbox("F" + (nr + 1));
      funktionField.addClassName("toggle-buttons");
      funktionField.addValueChangeListener(event -> {
        if (event.isFromClient() && this.lok != null) {
          int fktBits = this.lok.getFktBits();
          this.lok.setFktBits(event.getValue() ? (fktBits | mask) : (fktBits & ~mask));
        }
      });
      this.lokFunktionFields.add(funktionField);
      funktionenLayout.add(funktionField);
    }

    FormLayout form = newLabelledFormLayout();
    form.addFormItem(new HorizontalLayout(this.lokField, this.lokAktivField), "Lok:");
    HorizontalLayout fahrstufeLayout = new HorizontalLayout(this.lokFahrstufeValueField, this.lokFahrstufeField, this.lokRueckwaertsField);
    fahrstufeLayout.setAlignItems(FlexComponent.Alignment.CENTER);
    form.addFormItem(fahrstufeLayout, "Fahrstufe:");
    form.addFormItem(funktionenLayout, "Funktionen:");
    fieldset.add(form);

    resetLok();

    return fieldset;
  }

  private void resetLok() {
    Collection<Fahrzeugdecoder> loks = this.steuerung.getFahrzeugdecoder();
    this.lok = loks.isEmpty() ? null : loks.iterator().next();
    this.lokField.setValue(this.lok);
    refreshLok();
  }

  private void selectLok(Fahrzeugdecoder lok) {
    this.lok = lok;
    refreshLok();
  }

  private void selectLokByText(String text) {
    if (text == null || text.isBlank()) {
      return;
    }

    try {
      String[] parts = text.split("@");
      int adresse = Integer.parseInt(parts[0].trim());
      SystemTyp systemTyp = SystemTyp.valueOf(parts[1].trim());
      DecoderAdr decoderAdr = new DecoderAdr(systemTyp, adresse);

      Fahrzeugdecoder found = this.steuerung.getFahrzeugdecoder(decoderAdr);
      if (found == null) {
        found = new Fahrzeugdecoder(decoderAdr);
        found.injectFields();
        this.steuerung.addFahrzeugdecoder(found);
      }

      this.lok = found;
      this.lokField.setItems(this.steuerung.getFahrzeugdecoder());
      this.lokField.setValue(this.lok);
      refreshLok();
    } catch (RuntimeException e) {
      Notification notification = Notification.show("ungültige Lok-Adresse: " + text, 3000, Notification.Position.BOTTOM_START);
      notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
    }
  }

  private String formatLokLabel(Fahrzeugdecoder lok) {
    return lok.getId().getAdresse() + "@" + lok.getId().getSystemTyp();
  }

  private void refreshLok() {
    this.lokAktivField.setValue(this.lok != null && this.lok.isAktiv());
    this.lokFahrstufeField.setValue(this.lok != null ? this.lok.getFahrstufe() : 0);
    this.lokFahrstufeField.setMax(this.lok != null ? this.lok.getId().getSystemTyp().getMaxFahrstufe() : 31);
    this.lokRueckwaertsField.setValue(this.lok != null && this.lok.isRueckwaerts());
    this.lokLichtField.setValue(this.lok != null && this.lok.isLicht());

    int fktBits = this.lok != null ? this.lok.getFktBits() : 0;
    for (int nr = 0; nr < this.lokFunktionFields.size(); nr++) {
      this.lokFunktionFields.get(nr).setValue((fktBits & (1 << nr)) != 0);
    }
  }
}
