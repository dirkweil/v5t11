package de.gedoplan.v5t11.util.transaction;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.transaction.Transactional.TxType;

@ApplicationScoped
public class TransactionChecker {

  @Transactional(TxType.MANDATORY)
  public void assureActiveTransaction() {

  }
}
