package de.gedoplan.v5t11.stellwerk;

/**
 * Himmelsrichtungen als Positionsangaben im GBS.
 * 
 * Die Richtungen sind im Uhrzeigersinn angeordnet.
 * 
 * @author dw
 */
public enum GbsRichtung
{
  /**
   * Nord.
   */
  N
  {
    @Override
    public int getWinkel()
    {
      return 0;
    }

    @Override
    public GbsRichtung getOpposite()
    {
      return S;
    }
  },

  /**
   * Nord-Ost.
   */
  NO
  {
    @Override
    public int getWinkel()
    {
      return 45;
    }

    @Override
    public GbsRichtung getOpposite()
    {
      return SW;
    }
  },

  /**
   * Ost.
   */
  O
  {
    @Override
    public int getWinkel()
    {
      return 90;
    }

    @Override
    public GbsRichtung getOpposite()
    {
      return W;
    }
  },

  /**
   * Süd-Ost.
   */
  SO
  {
    @Override
    public int getWinkel()
    {
      return 135;
    }

    @Override
    public GbsRichtung getOpposite()
    {
      return NW;
    }
  },

  /**
   * Süd.
   */
  S
  {
    @Override
    public int getWinkel()
    {
      return 180;
    }

    @Override
    public GbsRichtung getOpposite()
    {
      return N;
    }
  },

  /**
   * Süd-West.
   */
  SW
  {
    @Override
    public int getWinkel()
    {
      return 225;
    }

    @Override
    public GbsRichtung getOpposite()
    {
      return NO;
    }
  },

  /**
   * West.
   */
  W
  {
    @Override
    public int getWinkel()
    {
      return 270;
    }

    @Override
    public GbsRichtung getOpposite()
    {
      return O;
    }
  },

  /**
   * Nord-West.
   */
  NW
  {
    @Override
    public int getWinkel()
    {
      return 315;
    }

    @Override
    public GbsRichtung getOpposite()
    {
      return SO;
    }
  };

  public abstract int getWinkel();

  public abstract GbsRichtung getOpposite();

  public boolean isDiagonal()
  {
    return (getWinkel() % 2) != 0;
  }
}
