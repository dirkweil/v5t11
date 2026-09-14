package de.gedoplan.v5t11.vaadincommon.ui;

import com.vaadin.quarkus.QuarkusVaadinServlet;

import jakarta.servlet.annotation.WebServlet;

/**
 * Ersetzt den von der vaadin-quarkus-extension sonst automatisch auf die Root ("/") der Anwendung gemappten
 * Default-Servlet durch eine eigene, auf "/ui/*" beschränkte Instanz. Andernfalls würde Vaadin jeden Request -
 * auch die JSF-Views (z. B. "*.xhtml") - an sich ziehen, siehe
 * https://vaadin.com/docs/latest/flow/integrations/quarkus#integrating-vaadin-with-existing-quarkus-application
 * <p>
 * {@code @Route}-Werte sind relativ zu diesem Präfix anzugeben, z. B. {@code @Route("system-status")} für
 * {@code /ui/system-status}.
 */
@WebServlet(urlPatterns = "/ui/*", name = "V5t11VaadinServlet", asyncSupported = true)
public class V5t11VaadinServlet extends QuarkusVaadinServlet {
}
