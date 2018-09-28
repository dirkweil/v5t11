package de.gedoplan.v5t11.fahrstrassen.gateway;

import de.gedoplan.v5t11.fahrstrassen.entity.fahrweg.Signal;
import de.gedoplan.v5t11.fahrstrassen.service.ConfigService;
import de.gedoplan.v5t11.util.domain.attribute.SignalStellung;
import de.gedoplan.v5t11.util.webservice.ResourceClientBase;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

//TODO Dies ist eine exakte Kopie aus v5t11-fahrstrassen; geht das eleganter?

@ApplicationScoped
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SignalResourceClient extends ResourceClientBase {

  @Inject
  public SignalResourceClient(ConfigService configService) {
    super(configService.getStatusRestUrl(), "signal");
  }

  public Set<Signal> getSignale() {
    return this.baseTarget
        .request()
        .accept(MediaType.APPLICATION_JSON)
        .get(new GenericType<Set<Signal>>() {});
  }

  public void signalStellen(Signal signal, List<SignalStellung> stellungen) {
    String stellungenAsString = stellungen.stream().map(Object::toString).collect(Collectors.joining(","));
    this.baseTarget
        .path(signal.getBereich())
        .path(signal.getName())
        .request()
        .put(Entity.text(stellungenAsString));

  }

  public void signalStellen(Signal signal, SignalStellung stellung) {
    this.baseTarget
        .path(signal.getBereich())
        .path(signal.getName())
        .request()
        .put(Entity.text(stellung));
  }
}
