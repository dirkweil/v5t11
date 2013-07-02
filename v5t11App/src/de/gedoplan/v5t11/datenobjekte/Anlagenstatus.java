package de.gedoplan.v5t11.datenobjekte;

public class Anlagenstatus
{

  private static Anlagenstatus anlagenstatus = null;

  private boolean              eingeschaltet;

  public boolean isEingeschaltet()
  {
    return eingeschaltet;
  }

  public void setEingeschaltet(boolean eingeschaltet)
  {
    this.eingeschaltet = eingeschaltet;
  }

  private Anlagenstatus()
  {

  }

  public static Anlagenstatus getInstance()
  {
    if (anlagenstatus == null)
    {
      anlagenstatus = new Anlagenstatus();
    }
    return anlagenstatus;
  }

  public void changeAnlagenstatus()
  {
    if (eingeschaltet)
    {
      eingeschaltet = false;
    }
    else
    {
      eingeschaltet = true;
    }
  }

}
