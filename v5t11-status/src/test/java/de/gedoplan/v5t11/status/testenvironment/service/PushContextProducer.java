package de.gedoplan.v5t11.status.testenvironment.service;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.faces.push.Push;
import javax.faces.push.PushContext;

@Dependent
public class PushContextProducer {

  @Produces
  @Push
  PushContext getPushContext() {
    return new PushContext() {

      @Override
      public <S extends Serializable> Map<S, Set<Future<Void>>> send(Object message, Collection<S> users) {
        throw new UnsupportedOperationException();
      }

      @Override
      public <S extends Serializable> Set<Future<Void>> send(Object message, S user) {
        throw new UnsupportedOperationException();
      }

      @Override
      public Set<Future<Void>> send(Object message) {
        throw new UnsupportedOperationException();
      }
    };
  }
}
