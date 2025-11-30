package de.gedoplan.v5t11.fahrstrassen.testenvironment.profile;

import java.util.HashMap;
import java.util.Map;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import io.smallrye.reactive.messaging.memory.InMemoryConnector;

public class InMemoryConnectorLifecycleManager implements QuarkusTestResourceLifecycleManager {

  @Override
  public Map<String, String> start() {
    Map<String, String> env = new HashMap<>();
    env.putAll(InMemoryConnector.switchIncomingChannelsToInMemory("status"));
    env.putAll(InMemoryConnector.switchOutgoingChannelsToInMemory("fahrstrasse"));
    return env;
  }

  @Override
  public void stop() {
    InMemoryConnector.clear();
  }
}