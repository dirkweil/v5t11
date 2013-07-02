package de.gedoplan.v5t11.selectrix.impl;

public class InterfaceTester
{
  public static void main(String[] args)
  {
    try
    {
      Selectrix.getInstance().start();
    }
    catch (Exception e)
    {
      e.printStackTrace();

      System.exit(2);
    }
  }
}
