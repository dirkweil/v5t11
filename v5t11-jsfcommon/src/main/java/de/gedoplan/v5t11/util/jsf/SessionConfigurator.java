package de.gedoplan.v5t11.util.jsf;

import jakarta.inject.Inject;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import org.eclipse.microprofile.config.inject.ConfigProperty;

@WebListener
public class SessionConfigurator implements ServletContextListener {

  @Inject
  @ConfigProperty(name = "v5t11.artifactId")
  String artifactId;

  @Override
  public void contextInitialized(ServletContextEvent sce) {
    ServletContext servletContext = sce.getServletContext();

    // Session Timeout abschalten
    servletContext.setSessionTimeout(0);

    // Session Cookie anwendungsspezifisch nennen
    servletContext.getSessionCookieConfig().setName(this.artifactId + "_SESSIONID");
  }

}
