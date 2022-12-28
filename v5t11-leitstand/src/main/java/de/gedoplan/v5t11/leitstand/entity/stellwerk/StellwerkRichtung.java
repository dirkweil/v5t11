package de.gedoplan.v5t11.leitstand.entity.stellwerk;

/**
 * Himmelsrichtungen als Positionsangaben im GBS.
 *
 * Die Richtungen sind im Uhrzeigersinn angeordnet.
 *
 * @author dw
 */
public enum StellwerkRichtung {
  /**
   * Nord.
   */
  N {
    @Override
    public int getWinkel() {
      return 0;
    }

    @Override
    public StellwerkRichtung getOpposite() {
      return S;
    }
  },

  /**
   * Nord-Ost.
   */
  NO {
    @Override
    public int getWinkel() {
      return 45;
    }

    @Override
    public StellwerkRichtung getOpposite() {
      return SW;
    }
  },

  /**
   * Ost.
   */
  O {
    @Override
    public int getWinkel() {
      return 90;
    }

    @Override
    public StellwerkRichtung getOpposite() {
      return W;
    }
  },

  /**
   * Süd-Ost.
   */
  SO {
    @Override
    public int getWinkel() {
      return 135;
    }

    @Override
    public StellwerkRichtung getOpposite() {
      return NW;
    }
  },

  /**
   * Süd.
   */
  S {
    @Override
    public int getWinkel() {
      return 180;
    }

    @Override
    public StellwerkRichtung getOpposite() {
      return N;
    }
  },

  /**
   * Süd-West.
   */
  SW {
    @Override
    public int getWinkel() {
      return 225;
    }

    @Override
    public StellwerkRichtung getOpposite() {
      return NO;
    }
  },

  /**
   * West.
   */
  W {
    @Override
    public int getWinkel() {
      return 270;
    }

    @Override
    public StellwerkRichtung getOpposite() {
      return O;
    }
  },

  /**
   * Nord-West.
   */
  NW {
    @Override
    public int getWinkel() {
      return 315;
    }

    @Override
    public StellwerkRichtung getOpposite() {
      return SO;
    }
  };

  public abstract int getWinkel();

  public abstract StellwerkRichtung getOpposite();

  public boolean isDiagonal() {
    return (getWinkel() % 2) != 0;
  }
}
