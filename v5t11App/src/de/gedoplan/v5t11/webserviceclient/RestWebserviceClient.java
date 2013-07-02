package de.gedoplan.v5t11.webserviceclient;

import de.gedoplan.v5t11.datenobjekte.AktiveLok;
import de.gedoplan.v5t11.datenobjekte.Anlagenstatus;
import de.gedoplan.v5t11.datenobjekte.LokomotiveDAO;
import de.gedoplan.v5t11.serverklasse.ClientLok;
import de.gedoplan.v5t11.serverklasse.LokSet;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import com.google.gson.Gson;

public class RestWebserviceClient
{
  // TODO: Konfigurierbar machen
  public static String HOST = "192.168.10.53";
  public static String PORT = "8080";

  private static String getUrl(String serviceUri)
  {
    return "http://" + HOST + ":" + PORT + "/v5t11-betriebssteuerung/rs/" + serviceUri;
  }

  private String executeGetRequest(String serviceUri)
  {
    try
    {
      HttpClient httpclient = new DefaultHttpClient();
      URI uri = new URI(getUrl(serviceUri));
      HttpGet request = new HttpGet(uri);
      request.addHeader("accept", "application/json");
      HttpResponse response = httpclient.execute(request);
      StatusLine statusLine = response.getStatusLine();
      if (statusLine.getStatusCode() == HttpStatus.SC_OK)
      {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        response.getEntity().writeTo(out);
        return out.toString();
      }
    }
    catch (Exception exception)
    {
      // TODO: Fehlerbehandlung
      exception.printStackTrace();
    }
    return null;
  }

  public void getAnlagenstatus()
  {
    String responseString = executeGetRequest("steuerung/zentrale/aktiv");
    Gson gson = new Gson();
    Boolean status = gson.fromJson(responseString, Boolean.class);
    Anlagenstatus.getInstance().setEingeschaltet(status);
  }

  public void getAktiveLok(String adresse)
  {

    String responseString = executeGetRequest("steuerung/lok/" + adresse);
    if (responseString != null)
    {

      Gson gson = new Gson();
      ClientLok lok = gson.fromJson(responseString, ClientLok.class);
      System.out.println("String: " + responseString + "ClientLok" + lok.toString());

      // REVIEW: Warum werden die Daten umkopiert? Einfacher: lok komplett ablegen
      AktiveLok.getInstance().setAdresse(lok.getAdr());
      AktiveLok.getInstance().setBezeichnung(lok.getName());
      AktiveLok.getInstance().setTempo(lok.getGeschwindigkeit());
      AktiveLok.getInstance().setLicht(lok.isLicht());
      AktiveLok.getInstance().setRueckwaerts(lok.isRueckwaerts());
      System.out.println("Aktive Lok auf: " + AktiveLok.getInstance().toString() + " gesetzt.");
    }
    else
    {
      AktiveLok.getInstance().setAdresse(999);
      AktiveLok.getInstance().setBezeichnung("???");
      AktiveLok.getInstance().setTempo(0);
      AktiveLok.getInstance().setLicht(false);
      AktiveLok.getInstance().setRueckwaerts(false);
      System.out.println("Fehler, Lok " + adresse + " nicht gefunden. Aktive Lok auf: " + AktiveLok.getInstance().toString() + " gesetzt.");
    }

  }

  public void updateAllLoks()
  {
    String responseString = executeGetRequest("steuerung/lok");
    Gson gson = new Gson();
    LokSet loks = gson.fromJson(responseString, LokSet.class);
    LokomotiveDAO.fuelleLokListeViaWebservice(loks.getLokset().iterator());
  }

  private void executePostRequest(String serviceUri, NameValuePair... postParameter)
  {
    try
    {
      HttpClient httpclient = new DefaultHttpClient();
      HttpPost pm = new HttpPost(getUrl(serviceUri));
      List<NameValuePair> parameterList = Arrays.asList(postParameter);
      pm.setEntity(new UrlEncodedFormEntity(parameterList, HTTP.UTF_8));
      httpclient.execute(pm);
    }
    catch (Exception e)
    {
      // TODO Fehlerbehandlung
      e.printStackTrace();
    }
  }

  public void setAnlagenstatus()
  {
    Anlagenstatus.getInstance().changeAnlagenstatus();
    executePostRequest("steuerung/zentrale/aktiv", new BasicNameValuePair("aktiv", Boolean.toString(Anlagenstatus.getInstance().isEingeschaltet())));
  }

  public void setAktiveLokLicht()
  {
    AktiveLok.getInstance().changeLicht();
    executePostRequest("steuerung/lok/" + AktiveLok.getInstance().getAdresse() + "/licht", new BasicNameValuePair("licht", Boolean.toString(AktiveLok.getInstance().isLicht())));
  }

  public void setAktiveLokRichtung()
  {
    AktiveLok.getInstance().changeRichtung();
    executePostRequest("steuerung/lok/" + AktiveLok.getInstance().getAdresse() + "/rueckwaerts", new BasicNameValuePair("rueckwaerts", Boolean.toString(AktiveLok.getInstance().isRueckwaerts())));
  }

  public void setAktiveLokTempo(int tempo)
  {
    AktiveLok.getInstance().setTempo(tempo);
    executePostRequest("steuerung/lok/" + AktiveLok.getInstance().getAdresse() + "/geschwindigkeit", new BasicNameValuePair("geschwindigkeit", Integer.toString(AktiveLok.getInstance().getTempo())));
  }

}
