package de.gedoplan.v5t11.stellwerk;

import javax.swing.JPanel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Generelle Klasse von der alle ApplicationPanels ableiten, die in das TabPane eingeh�ngt werden.
 */

public abstract class ApplicationPanel extends JPanel
{
  protected Log logger = LogFactory.getLog(this.getClass());

  /**
   * Namen des Dialogs liefern.
   */
  @Override
  public abstract String getName();

  /**
   * Dialog starten. Wird aufgerufen, wenn das entsprechende Tab aktiviert wird.
   */
  public void start()
  {
  }

  /**
   * Dialog stoppen. Wird aufgerufen, wenn das entsprechende Tab deaktiviert wird.
   * 
   * @return true: Verlassen des Dialogs ist erlaubt false: Dialog darf nicht verlassen werden.
   */
  public boolean stop()
  {
    return true;
  }
}
