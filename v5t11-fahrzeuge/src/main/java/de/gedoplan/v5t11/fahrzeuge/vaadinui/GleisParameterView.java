package de.gedoplan.v5t11.fahrzeuge.vaadinui;

import de.gedoplan.v5t11.fahrzeuge.entity.fahrweg.Gleis;
import de.gedoplan.v5t11.fahrzeuge.service.ParcoursService;
import de.gedoplan.v5t11.vaadincommon.ui.MainLayout;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;

import java.util.ArrayList;

/**
 * Vaadin-Pendant zum inzwischen abgelösten {@code view/gleisParameter.xhtml}. Erste migrierte View der Phase 2
 * (v5t11-fahrzeuge) der JSF->Vaadin-Migration (siehe /home/dw/.claude/plans/functional-singing-canyon.md).
 * <p>
 * Übernimmt nur die tatsächlich funktionierende Kernfunktion des alten Presenters (Inline-Bearbeitung von
 * Länge/verdeckt je Gleis, automatisches Speichern pro Zeile über {@link ParcoursService#saveGleis}) – die Buttons
 * "messen" und "speichern" waren im JSF-Original bereits toter/kaputter Code (keine navigation-case bzw. keine
 * passende Presenter-Methode) und wurden daher nicht nachgebaut.
 */
@Route(value = "gleis-parameter", layout = MainLayout.class)
@PageTitle("Gleis-Parameter - v5t11")
public class GleisParameterView extends VerticalLayout {

  @Inject
  ParcoursService parcoursService;

  @PostConstruct
  void init() {
    setSizeFull();

    HorizontalLayout toolbar = new HorizontalLayout(new H2("Gleis-Parameter"), new Button("zurück", event -> navigateBack()));
    toolbar.setWidthFull();
    toolbar.setAlignItems(FlexComponent.Alignment.CENTER);
    toolbar.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

    add(toolbar, buildGrid());
  }

  private Grid<Gleis> buildGrid() {
    Grid<Gleis> grid = new Grid<>();
    grid.setSizeFull();
    grid.setItems(new ArrayList<>(this.parcoursService.getGleise()));

    grid.addColumn(Gleis::getBereich).setHeader("Bereich");
    grid.addColumn(Gleis::getName).setHeader("Name");

    Binder<Gleis> binder = new Binder<>(Gleis.class);
    Editor<Gleis> editor = grid.getEditor();
    editor.setBinder(binder);
    editor.setBuffered(true);

    Grid.Column<Gleis> laengeColumn = grid.addColumn(Gleis::getLaenge).setHeader("Länge [mm]");
    IntegerField laengeField = new IntegerField();
    laengeField.setWidthFull();
    binder.forField(laengeField).bind(Gleis::getLaenge, Gleis::setLaenge);
    laengeColumn.setEditorComponent(laengeField);

    Grid.Column<Gleis> verdecktColumn = grid.addColumn(new ComponentRenderer<Checkbox, Gleis>(gleis -> {
      Checkbox readonlyCheckbox = new Checkbox(gleis.isVerdeckt());
      readonlyCheckbox.setReadOnly(true);
      return readonlyCheckbox;
    })).setHeader("verdeckt");
    Checkbox verdecktField = new Checkbox();
    binder.forField(verdecktField).bind(Gleis::isVerdeckt, Gleis::setVerdeckt);
    verdecktColumn.setEditorComponent(verdecktField);

    Grid.Column<Gleis> actionsColumn = grid.addColumn(new ComponentRenderer<Button, Gleis>(gleis -> {
      Button editButton = new Button(VaadinIcon.EDIT.create());
      editButton.addClickListener(event -> {
        if (editor.isOpen()) {
          editor.cancel();
        }
        editor.editItem(gleis);
      });
      return editButton;
    })).setWidth("100px").setFlexGrow(0);

    Button saveButton = new Button(VaadinIcon.CHECK.create(), event -> editor.save());
    saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    Button cancelButton = new Button(VaadinIcon.CLOSE.create(), event -> editor.cancel());
    actionsColumn.setEditorComponent(new HorizontalLayout(saveButton, cancelButton));

    editor.addSaveListener(event -> {
      Gleis gleis = event.getItem();
      this.parcoursService.saveGleis(gleis.getId());
      grid.getDataProvider().refreshItem(gleis);
    });

    return grid;
  }

  private void navigateBack() {
    getUI().ifPresent(ui -> ui.getPage().setLocation("/view/fahrzeugList.xhtml"));
  }
}
