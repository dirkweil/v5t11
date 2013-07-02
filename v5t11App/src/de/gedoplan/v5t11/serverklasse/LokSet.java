package de.gedoplan.v5t11.serverklasse;

import java.util.Set;

public class LokSet
{
  private Set<ClientLok> lokset;

  public Set<ClientLok> getLokset()
  {
    return this.lokset;
  }

  public void setLokset(Set<ClientLok> lokset)
  {
    this.lokset = lokset;
  }

}
