package de.gedoplan.v5t11.status.entity.fahrweg.geraet;

import de.gedoplan.baselibs.utils.inject.InjectionUtil;

public interface FunktionsdecoderGeraet {
  void adjustStatus();

  default void injectFields() {
    InjectionUtil.injectFields(this);
  }
}
