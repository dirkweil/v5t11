package de.gedoplan.v5t11.status.vaadinui;

import de.gedoplan.v5t11.status.service.ConfigurationAdapter;
import de.gedoplan.v5t11.status.service.ConfigurationAdapter.ConfigurationPropertyAdapter;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;

/**
 * Baut je Konfigurationswert eine "Soll (readonly) | Reset(&quot;&gt;&quot;) | Ist (editierbar)"-Zeile in ein
 * {@link FormLayout} ein. Wiederverwendet von allen Geräte-Formularen in {@link BausteinProgrammierungView} – das
 * "einmal bauen, 11× replizieren"-Muster aus dem Migrationsplan, analog zum gemeinsamen
 * {@code h:panelGrid columns="5"}-Aufbau der ehemaligen bausteinProgrammierung_*.xhtml-Fragmente.
 */
final class ConfigurationFormFields {

  private ConfigurationFormFields() {
  }

  static void addAddressRow(FormLayout form, ConfigurationAdapter config) {
    TextField sollField = new TextField();
    sollField.setValue(String.valueOf(config.getLocalAdrSoll()));
    sollField.setReadOnly(true);

    IntegerField istField = new IntegerField();
    istField.setValue(config.getLocalAdrIst());
    istField.addValueChangeListener(event -> {
      if (event.isFromClient() && event.getValue() != null) {
        config.setLocalAdrIst(event.getValue());
      }
    });

    Button resetButton = new Button(">", event -> {
      config.localAdrResetToSoll();
      istField.setValue(config.getLocalAdrIst());
    });

    form.addFormItem(row(sollField, resetButton, istField), "Adresse:");
  }

  static void addIntRow(FormLayout form, String label, ConfigurationPropertyAdapter<Integer> prop) {
    form.addFormItem(intRow(prop, null, null), label);
  }

  static void addIntRowWithButton(FormLayout form, String label, ConfigurationPropertyAdapter<Integer> prop, String buttonLabel, Runnable onButtonClick) {
    form.addFormItem(intRow(prop, buttonLabel, onButtonClick), label);
  }

  private static Component intRow(ConfigurationPropertyAdapter<Integer> prop, String extraButtonLabel, Runnable onExtraButtonClick) {
    TextField sollField = new TextField();
    sollField.setValue(String.valueOf(prop.getSoll()));
    sollField.setReadOnly(true);

    IntegerField istField = new IntegerField();
    istField.setValue(prop.getIst());
    istField.addValueChangeListener(event -> {
      if (event.isFromClient() && event.getValue() != null) {
        prop.setIst(event.getValue());
      }
    });

    Button resetButton = new Button(">", event -> {
      prop.resetToSoll();
      istField.setValue(prop.getIst());
    });

    if (extraButtonLabel == null) {
      return row(sollField, resetButton, istField);
    }

    Button extraButton = new Button(extraButtonLabel, event -> onExtraButtonClick.run());
    return row(sollField, resetButton, istField, extraButton);
  }

  static void addBooleanRow(FormLayout form, String label, ConfigurationPropertyAdapter<Boolean> prop) {
    Checkbox sollField = new Checkbox();
    sollField.setValue(prop.getSoll());
    sollField.setReadOnly(true);

    Checkbox istField = new Checkbox();
    istField.setValue(prop.getIst());
    istField.addValueChangeListener(event -> {
      if (event.isFromClient()) {
        prop.setIst(event.getValue());
      }
    });

    Button resetButton = new Button(">", event -> {
      prop.resetToSoll();
      istField.setValue(prop.getIst());
    });

    form.addFormItem(row(sollField, resetButton, istField), label);
  }

  static <E extends Enum<E>> void addEnumRow(FormLayout form, String label, ConfigurationPropertyAdapter<E> prop) {
    TextField sollField = new TextField();
    sollField.setValue(String.valueOf(prop.getSoll()));
    sollField.setReadOnly(true);

    ComboBox<E> istField = new ComboBox<>();
    istField.setItems(prop.getValues());
    istField.setValue(prop.getIst());
    istField.addValueChangeListener(event -> {
      if (event.isFromClient() && event.getValue() != null) {
        prop.setIst(event.getValue());
      }
    });

    Button resetButton = new Button(">", event -> {
      prop.resetToSoll();
      istField.setValue(prop.getIst());
    });

    form.addFormItem(row(sollField, resetButton, istField), label);
  }

  static void addBooleanArrayRow(FormLayout form, String label, ConfigurationPropertyAdapter<Boolean>[] props, Runnable resetAll) {
    HorizontalLayout sollLayout = new HorizontalLayout();
    HorizontalLayout istLayout = new HorizontalLayout();
    Checkbox[] istFields = new Checkbox[props.length];

    for (int i = 0; i < props.length; i++) {
      ConfigurationPropertyAdapter<Boolean> prop = props[i];

      Checkbox sollField = new Checkbox();
      sollField.setValue(prop.getSoll());
      sollField.setReadOnly(true);
      sollLayout.add(sollField);

      Checkbox istField = new Checkbox();
      istField.setValue(prop.getIst());
      istField.addValueChangeListener(event -> {
        if (event.isFromClient()) {
          prop.setIst(event.getValue());
        }
      });
      istFields[i] = istField;
      istLayout.add(istField);
    }

    Button resetButton = new Button(">", event -> {
      resetAll.run();
      for (int i = 0; i < props.length; i++) {
        istFields[i].setValue(props[i].getIst());
      }
    });

    form.addFormItem(row(sollLayout, resetButton, istLayout), label);
  }

  private static HorizontalLayout row(Component sollField, Button resetButton, Component istField, Component... extra) {
    HorizontalLayout row = new HorizontalLayout(sollField, resetButton, istField);
    row.add(extra);
    row.setAlignItems(FlexComponent.Alignment.CENTER);
    return row;
  }
}
