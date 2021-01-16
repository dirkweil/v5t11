package de.gedoplan.v5t11.util.jsf;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

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
