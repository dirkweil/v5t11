package de.gedoplan.v5t11.betriebssteuerung.listener;

/**
 * Event-Basisklasse.
 * 
 * @author dw
 */
public abstract class Event
{
  /**
   * Event-Quelle.
   */
  protected Object source;

  /**
   * Konstruktor.
   * 
   * @param source Event-Quelle
   */
  public Event(Object source)
  {
    this.source = source;
  }

  /**
   * Wert liefern: {@link #source}.
   * 
   * @return Wert
   */
  public Object getSource()
  {
    return this.source;
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    return this.getClass().getSimpleName() + " [source=" + this.source + "]";
  }

}
