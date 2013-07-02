package de.gedoplan.v5t11;


public class SteuerungRestWSTest
{
  // public static void main(String[] args) {
  // SteuerungRestWSTest test = new SteuerungRestWSTest();
  // // test.testGetSteuerung();
  // // test.testGetAllLoks();
  // // test.testGetLokByAdresse(10);
  // test.testPostLokStop(12);
  // // test.testPostSetSpeed(12, 15);
  // // test.testPostSetRichtung(12, 0);
  // // test.testPostSetLicht(12, 1);
  // // test.testGetAnlagenStatus();
  // // test.testSetAnlagenStatus(false);
  // // test.testPostSetSignal("Demoanlage", "N1", Stellung.HALT);
  // // test.testPostSetWeiche(
  // // "Demoanlage",
  // // "3",
  // // de.gedoplan.v5t11.steuerung.fahrweg.geraet.Weiche.Stellung.GERADE);
  //
  // // TODO: folgende Methoden noch testen
  // // test.testpostSetFahrwegreservierung("Demoanlage", "S->2->12");
  // // test.testPostSetFahrstrasseFreigabe("Demoanlage", "12->2->S");
  // }
  //
  // private void testPostSetFahrstrasseFreigabe(String bereich, String name) {
  // try {
  // org.apache.commons.httpclient.HttpClient httpclient = new org.apache.commons.httpclient.HttpClient();
  // PostMethod pm = new PostMethod(
  // "http://localhost:8080/v5t11-betriebssteuerung/steuerung/fahrstrasse/freigeben");
  // pm.addParameter("bereich", bereich);
  // pm.addParameter("name", name);
  // int res = httpclient.executeMethod(pm);
  // System.out.println("testPostSetFahrstrasseFreigabe server status: "
  // + res);
  // String response = pm.getResponseBodyAsString();
  // System.out.println(response);
  //
  // } catch (Exception e) {
  // e.printStackTrace();
  // }
  //
  // }
  //
  // private void testpostSetFahrwegreservierung(String bereich, String name) {
  // try {
  // org.apache.commons.httpclient.HttpClient httpclient = new org.apache.commons.httpclient.HttpClient();
  // PostMethod pm = new PostMethod(
  // "http://localhost:8080/v5t11-betriebssteuerung/steuerung/fahrstrasse/resevieren");
  // pm.addParameter("bereich", bereich);
  // pm.addParameter("name", name);
  // int res = httpclient.executeMethod(pm);
  // System.out.println("testpostSetFahrwegreservierung server status: "
  // + res);
  // String response = pm.getResponseBodyAsString();
  // System.out.println(response);
  //
  // } catch (Exception e) {
  // e.printStackTrace();
  // }
  // }
  //
  // private void testPostSetWeiche(String bereich, String name,
  // Stellung stellung) {
  // try {
  // org.apache.commons.httpclient.HttpClient httpclient = new org.apache.commons.httpclient.HttpClient();
  // PostMethod pm = new PostMethod(
  // "http://localhost:8080/v5t11-betriebssteuerung/steuerung/weiche/stellung");
  // pm.addParameter("bereich", bereich);
  // pm.addParameter("name", name);
  // pm.addParameter("stellung", stellung.toString());
  // int res = httpclient.executeMethod(pm);
  // System.out.println("testPostSetWeiche server status: " + res);
  // String response = pm.getResponseBodyAsString();
  // System.out.println(response);
  //
  // } catch (Exception e) {
  // e.printStackTrace();
  // }
  //
  // }
  //
  // private void testPostSetLicht(int adresse, int licht) {
  // try {
  // org.apache.commons.httpclient.HttpClient httpclient = new org.apache.commons.httpclient.HttpClient();
  // PostMethod pm = new PostMethod(
  // "http://localhost:8080/v5t11-betriebssteuerung/steuerung/loks/licht");
  // pm.addParameter("adresse", "" + adresse);
  // pm.addParameter("licht", "" + licht);
  // int res = httpclient.executeMethod(pm);
  // System.out.println("testPostSetLicht server status: " + res);
  // String response = pm.getResponseBodyAsString();
  // System.out.println(response);
  //
  // } catch (Exception e) {
  // e.printStackTrace();
  // }
  // }
  //
  // private void testPostSetRichtung(int adresse, int richtung) {
  // try {
  // org.apache.commons.httpclient.HttpClient httpclient = new org.apache.commons.httpclient.HttpClient();
  // PostMethod pm = new PostMethod(
  // "http://localhost:8080/v5t11-betriebssteuerung/steuerung/loks/richtung");
  // pm.addParameter("adresse", "" + adresse);
  // pm.addParameter("richtung", "" + richtung);
  // int res = httpclient.executeMethod(pm);
  // System.out.println("testPostSetRichtung server status: " + res);
  // String response = pm.getResponseBodyAsString();
  // System.out.println(response);
  //
  // } catch (Exception e) {
  // e.printStackTrace();
  // }
  //
  // }
  //
  // private void testPostSetSpeed(int adresse, int speed) {
  // try {
  // org.apache.commons.httpclient.HttpClient httpclient = new org.apache.commons.httpclient.HttpClient();
  // PostMethod pm = new PostMethod(
  // "http://localhost:8080/v5t11-betriebssteuerung/steuerung/loks/speed");
  // pm.addParameter("adresse", "" + adresse);
  // pm.addParameter("speed", "" + speed);
  // int res = httpclient.executeMethod(pm);
  // System.out.println("testPostSetSpeed server status: " + res);
  // String response = pm.getResponseBodyAsString();
  // System.out.println(response);
  //
  // } catch (Exception e) {
  // e.printStackTrace();
  // }
  //
  // }
  //
  // private void testPostLokStop(int id) {
  // try {
  // org.apache.commons.httpclient.HttpClient httpclient = new org.apache.commons.httpclient.HttpClient();
  // PostMethod pm = new PostMethod(
  // "http://localhost:8080/v5t11-betriebssteuerung/steuerung/loks/stop");
  // pm.addParameter("adresse", "" + id);
  // int res = httpclient.executeMethod(pm);
  // System.out.println("testPostLokStop server status: " + res);
  // String response = pm.getResponseBodyAsString();
  // System.out.println(response);
  //
  // } catch (Exception e) {
  // e.printStackTrace();
  // }
  //
  // }
  //
  // private void testGetSteuerung() {
  // try {
  // HttpClient httpclient = new DefaultHttpClient();
  // URI uri = new URI(
  // "http://localhost:8080/v5t11-betriebssteuerung/steuerung");
  // HttpGet request = new HttpGet(uri);
  // request.addHeader("accept", "application/xml");
  // HttpResponse response = httpclient.execute(request);
  // StatusLine statusLine = response.getStatusLine();
  // if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
  // ByteArrayOutputStream out = new ByteArrayOutputStream();
  // response.getEntity().writeTo(out);
  // // out.close();
  // String responseString = out.toString();
  //
  // System.out.println("testGetSteuerung: " + responseString);
  // Steuerung steuerung = XmlConverter.fromXml(Steuerung.class,
  // new StringReader(responseString));
  // System.out.println(steuerung);
  // // Gson gson = new Gson();
  // // Steuerung steuerung = gson.fromJson(responseString,
  // // Steuerung.class);
  // // System.out.println(steuerung);
  //
  // }
  // } catch (Exception e) {
  // e.printStackTrace();
  // }
  // }
  //
  // private void testGetAllLoks() {
  // try {
  // HttpClient httpclient = new DefaultHttpClient();
  // URI uri = new URI(
  // "http://localhost:8080/v5t11-betriebssteuerung/steuerung/loks");
  // HttpGet request = new HttpGet(uri);
  // request.addHeader("accept", "application/json");
  // HttpResponse response = httpclient.execute(request);
  // StatusLine statusLine = response.getStatusLine();
  // if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
  // ByteArrayOutputStream out = new ByteArrayOutputStream();
  // response.getEntity().writeTo(out);
  // // out.close();
  // String responseString = out.toString();
  //
  // System.out.println("testGetAllLoks: " + responseString);
  //
  // Gson gson = new Gson();
  // LokSet loks = gson.fromJson(responseString, LokSet.class);
  // for (Lok lok : loks.getLokset()) {
  // System.out.println(lok.getName());
  // }
  //
  // }
  // } catch (Exception e) {
  // e.printStackTrace();
  // }
  // }
  //
  // public void testGetLokByAdresse(int adresse) {
  // try {
  // HttpClient httpclient = new DefaultHttpClient();
  // URI uri = new URI(
  // "http://localhost:8080/v5t11-betriebssteuerung/steuerung/loks/"
  // + adresse);
  // HttpGet request = new HttpGet(uri);
  // request.addHeader("accept", "application/json");
  // HttpResponse response = httpclient.execute(request);
  // StatusLine statusLine = response.getStatusLine();
  // if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
  // ByteArrayOutputStream out = new ByteArrayOutputStream();
  // response.getEntity().writeTo(out);
  // // out.close();
  // String responseString = out.toString();
  //
  // System.out.println("testGetLokById: " + responseString);
  //
  // Gson gson = new Gson();
  // Lok lok = gson.fromJson(responseString, Lok.class);
  // try {
  // System.out.println(lok.getName());
  // } catch (Exception e) {
  // System.out.println("lok nicht gefunden");
  // }
  //
  // }
  // } catch (Exception e) {
  // e.printStackTrace();
  // }
  // }
  //
  // private void testPostSetSignal(String bereich, String name,
  // de.gedoplan.v5t11.steuerung.fahrweg.geraet.Signal.Stellung stellung) {
  // try {
  // org.apache.commons.httpclient.HttpClient httpclient = new org.apache.commons.httpclient.HttpClient();
  // PostMethod pm = new PostMethod(
  // "http://localhost:8080/v5t11-betriebssteuerung/steuerung/signal/stellung");
  // pm.addParameter("bereich", bereich);
  // pm.addParameter("name", name);
  // pm.addParameter("stellung", stellung.toString());
  // int res = httpclient.executeMethod(pm);
  // System.out.println("testPostSetSignal server status: " + res);
  // String response = pm.getResponseBodyAsString();
  // System.out.println(response);
  //
  // } catch (Exception e) {
  // e.printStackTrace();
  // }
  //
  // }
  //
  // public void testGetAnlagenStatus() {
  // try {
  // HttpClient httpclient = new DefaultHttpClient();
  // URI uri = new URI(
  // "http://localhost:8080/v5t11-betriebssteuerung/steuerung/anlagenstatus/");
  // HttpGet request = new HttpGet(uri);
  // request.addHeader("accept", "application/json");
  // HttpResponse response = httpclient.execute(request);
  // StatusLine statusLine = response.getStatusLine();
  // if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
  // ByteArrayOutputStream out = new ByteArrayOutputStream();
  // response.getEntity().writeTo(out);
  // // out.close();
  // String responseString = out.toString();
  //
  // System.out.println("testGetAnlagenStatus: " + responseString);
  //
  // Gson gson = new Gson();
  // Boolean status = gson.fromJson(responseString, Boolean.class);
  // System.out.println(status);
  //
  // }
  // } catch (Exception e) {
  // e.printStackTrace();
  // }
  // }
  //
  // private void testSetAnlagenStatus(boolean status) {
  // try {
  // org.apache.commons.httpclient.HttpClient httpclient = new org.apache.commons.httpclient.HttpClient();
  // PostMethod pm = new PostMethod(
  // "http://localhost:8080/v5t11-betriebssteuerung/steuerung/anlagenstatus");
  // pm.addParameter("an", Boolean.toString(status));
  // int res = httpclient.executeMethod(pm);
  // System.out.println("testSetAnlagenStatus server status: " + res);
  // String response = pm.getResponseBodyAsString();
  // System.out.println(response);
  //
  // } catch (Exception e) {
  // e.printStackTrace();
  // }
  // }
}
