package de.gedoplan.v5t11.vaadincommon.ui;

import java.io.IOException;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Leitet Browser-Navigation auf den Context-Root ("/") auf {@code /ui/home} (und damit auf
 * {@link HomeView}) um. Das url-pattern "" ist ein exakter Context-Root-Match und kollidiert daher
 * nicht mit dem auf "/ui/*" beschränkten {@link V5t11VaadinServlet}.
 */
@WebServlet(urlPatterns = "")
public class RootRedirectServlet extends HttpServlet {

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.sendRedirect("ui/home");
  }
}
