package de.gedoplan.v5t11.status;

import de.gedoplan.baselibs.utils.util.ApplicationProperties;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.se.SeContainer;
import javax.enterprise.inject.se.SeContainerInitializer;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionTarget;

import org.apache.deltaspike.cdise.api.ContextControl;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

public abstract class CdiTestBase extends TestBase {

  protected static SeContainer container;

  protected static String cdiProviderName;

  @BeforeAll
  static void startCdiContainer() {

    cdiProviderName = ApplicationProperties.getProperty("cdi.provider.name");

    container = SeContainerInitializer.newInstance().initialize();
  }

  @BeforeEach
  @SuppressWarnings({ "rawtypes", "unchecked" })
  void startRequestContextAndHandleInjectsInTestClass() {

    ContextControl contextControl = container.select(ContextControl.class).get();
    contextControl.startContext(RequestScoped.class);

    BeanManager beanManager = container.getBeanManager();

    CreationalContext creationalContext = beanManager.createCreationalContext(null);

    AnnotatedType annotatedType = beanManager.createAnnotatedType(this.getClass());
    InjectionTarget injectionTarget = beanManager.createInjectionTarget(annotatedType);
    injectionTarget.inject(this, creationalContext);
  }

  @AfterEach
  void stopRequestContext() {
    ContextControl contextControl = container.select(ContextControl.class).get();
    contextControl.stopContext(RequestScoped.class);
  }

  @AfterAll
  static void stopContainer() {
    container.close();
  }

}
