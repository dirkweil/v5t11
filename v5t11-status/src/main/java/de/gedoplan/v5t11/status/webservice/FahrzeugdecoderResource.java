package de.gedoplan.v5t11.status.webservice;

import de.gedoplan.v5t11.status.entity.Steuerung;
import de.gedoplan.v5t11.status.entity.fahrzeug.Fahrzeugdecoder;
import de.gedoplan.v5t11.util.domain.attribute.DecoderAdr;
import de.gedoplan.v5t11.util.domain.attribute.SystemTyp;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import org.jboss.logging.Logger;

@Path("fahrzeugdecoder")
@Dependent
public class FahrzeugdecoderResource {

  @Inject
  Steuerung steuerung;

  @Inject
  Logger logger;

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Collection<Fahrzeugdecoder> getAll() {

    return this.steuerung.getFahrzeugdecoder();
  }

  @PUT
  @Path("{id}")
  @Consumes(MediaType.MEDIA_TYPE_WILDCARD)
  public void change(
    @PathParam("id") DecoderAdr id,
    @QueryParam("aktiv") Boolean aktiv,
    @QueryParam("fahrstufe") Integer fahrstufe,
    @QueryParam("fktBits") Integer fktBits,
    @QueryParam("licht") Boolean licht,
    @QueryParam("rueckwaerts") Boolean rueckwaerts) {

    Fahrzeugdecoder fahrzeugdecoder = this.steuerung.getOrCreateFahrzeugdecoder(id);
    if (aktiv != null) {
      fahrzeugdecoder.setAktiv(aktiv);
    }
    if (fahrstufe != null) {
      fahrzeugdecoder.setFahrstufe(fahrstufe);
    }
    if (fktBits != null) {
      fahrzeugdecoder.setFktBits(fktBits);
    }
    if (licht != null) {
      fahrzeugdecoder.setLicht(licht);
    }
    if (rueckwaerts != null) {
      fahrzeugdecoder.setRueckwaerts(rueckwaerts);
    }

  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("config/{systemTyp}")
  public Map<Integer, Integer> getFahrzeugdecoderConfig(@PathParam("systemTyp") SystemTyp systemTyp, @QueryParam("key") List<Integer> keys) {
    this.logger.debugf("getFahrzeugdecoderConfig(%s): keys=%s", systemTyp, keys);

    return this.steuerung.getZentrale().readFahrzeugdecoderConfig(systemTyp, keys);
  }

  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("config/{systemTyp}")
  public void setFahrzeugdecoderConfig(@PathParam("systemTyp") SystemTyp systemTyp, Map<Integer, Integer> fahrzeugConfigParameters) {
    this.logger.infof("setFahrzeugdecoderConfig(%s): fahrzeugConfigParameters=%s", systemTyp, fahrzeugConfigParameters);

    this.steuerung.getZentrale().writeFahrzeugdecoderConfig(systemTyp, fahrzeugConfigParameters);
  }

}
