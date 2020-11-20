package de.gedoplan.v5t11.util.transaction;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

@ApplicationScoped
public class TransactionChecker {

  @Transactional(TxType.MANDATORY)
  public void assureActiveTransaction() {

  }
}
