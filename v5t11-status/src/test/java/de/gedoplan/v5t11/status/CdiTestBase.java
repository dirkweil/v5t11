package de.gedoplan.v5t11.status;

import de.gedoplan.baselibs.utils.util.ApplicationProperties;
import de.gedoplan.v5t11.util.config.ConfigBase;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.se.SeContainer;
import javax.enterprise.inject.se.SeContainerInitializer;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionTarget;

import org.apache.deltaspike.cdise.api.ContextControl;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

public abstract class CdiTestBase extends TestBase {

  protected static SeContainer container;

  protected static String cdiProviderName;

  @BeforeClass
  public static void startCdiContainer() {

    System.setProperty(ConfigBase.PROPERTY_ANLAGE, "test");

    cdiProviderName = ApplicationProperties.getProperty("cdi.provider.name");

    container = SeContainerInitializer.newInstance().initialize();
  }

  @Before
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public void startRequestContextAndHandleInjectsInTestClass() {

    ContextControl contextControl = container.select(ContextControl.class).get();
    contextControl.startContext(RequestScoped.class);

    BeanManager beanManager = container.getBeanManager();

    CreationalContext creationalContext = beanManager.createCreationalContext(null);

    AnnotatedType annotatedType = beanManager.createAnnotatedType(this.getClass());
    InjectionTarget injectionTarget = beanManager.createInjectionTarget(annotatedType);
    injectionTarget.inject(this, creationalContext);
  }

  @After
  public void stopRequestContext() {
    ContextControl contextControl = container.select(ContextControl.class).get();
    contextControl.stopContext(RequestScoped.class);
  }

  @AfterClass
  public static void stopContainer() {
    container.close();
  }

}