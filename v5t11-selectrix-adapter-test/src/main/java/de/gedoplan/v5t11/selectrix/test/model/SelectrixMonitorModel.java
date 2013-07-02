package de.gedoplan.v5t11.selectrix.test.model;

import de.gedoplan.v5t11.selectrix.SelectrixConnection;
import de.gedoplan.v5t11.selectrix.SelectrixConnectionFactory;

import java.io.Serializable;

import javax.annotation.Resource;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Model;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

// REVIEW: Ohne SessionScoped wirft der JBoss "javax.faces.FacesException: Cannot create a session after the response has been committed" ???
@Model
@SessionScoped
public class SelectrixMonitorModel implements Serializable
{
  @Resource(mappedName = "java:/eis/SelectrixConnectionFactory")
  private SelectrixConnectionFactory connectionFactory;

  private int[]                      wert    = new int[128];
  private boolean[]                  changed = new boolean[128];

  private int                        address;
  private int                        value;

  public int getWert(int adresse)
  {
    return this.wert[adresse];
  }

  public boolean isChanged(int adresse)
  {
    return this.changed[adresse];
  }

  public void refresh()
  {
    try (SelectrixConnection connection = this.connectionFactory.getConnection();)
    {
      for (int addresse = 0; addresse < this.wert.length; ++addresse)
      {
        try
        {
          int neuerWert = connection.getValue(addresse);
          this.changed[addresse] = (neuerWert != this.wert[addresse]);
          if (this.changed[addresse])
          {
            this.wert[addresse] = neuerWert;
          }
        }
        catch (Exception e)
        {
          addFacesMessage(e);
        }
      }
    }
  }

  /**
   * Wert liefern: {@link #address}.
   * 
   * @return Wert
   */
  public int getAddress()
  {
    return this.address;
  }

  /**
   * Wert setzen: {@link #address}.
   * 
   * @param address Wert
   */
  public void setAddress(int address)
  {
    this.address = address;
  }

  /**
   * Wert liefern: {@link #value}.
   * 
   * @return Wert
   */
  public int getValue()
  {
    return this.value;
  }

  /**
   * Wert setzen: {@link #value}.
   * 
   * @param value Wert
   */
  public void setValue(int value)
  {
    this.value = value;
  }

  public void write()
  {
    try (SelectrixConnection connection = this.connectionFactory.getConnection();)
    {
      connection.setValue(this.address, this.value);
    }
    catch (Exception e)
    {
      addFacesMessage(e);
    }
  }

  private void addFacesMessage(Exception e)
  {
    FacesMessage facesMessage = new FacesMessage(e.getMessage());
    facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
    FacesContext.getCurrentInstance().addMessage(null, facesMessage);
  }

}
