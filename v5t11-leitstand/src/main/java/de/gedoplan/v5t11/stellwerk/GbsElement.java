package de.gedoplan.v5t11.stellwerk;

import de.gedoplan.baselibs.utils.exception.BugException;
import de.gedoplan.baselibs.utils.inject.InjectionUtil;
import de.gedoplan.v5t11.leitstand.entity.Leitstand;
import de.gedoplan.v5t11.leitstand.entity.fahrweg.Gleis;
import de.gedoplan.v5t11.leitstand.entity.fahrweg.Signal;
import de.gedoplan.v5t11.leitstand.entity.stellwerk.StellwerkDkw2;
import de.gedoplan.v5t11.leitstand.entity.stellwerk.StellwerkElement;
import de.gedoplan.v5t11.leitstand.entity.stellwerk.StellwerkGleis;
import de.gedoplan.v5t11.leitstand.entity.stellwerk.StellwerkWeiche;
import de.gedoplan.v5t11.leitstand.persistence.SignalRepository;
import de.gedoplan.v5t11.leitstand.service.FahrstrassenManager;
import de.gedoplan.v5t11.stellwerk.util.GbsFarben;
import de.gedoplan.v5t11.util.domain.attribute.BereichselementId;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.MemoryImageSource;

import javax.inject.Inject;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.jboss.logging.Logger;

/**
 * GBS-Element.
 *
 * Ein Objekt dieser Klasse stellt ein Feld im Gleisbildstellwerk dar.
 *
 * @author dw
 */
public abstract class GbsElement extends JPanel {
  /*
   * Tatsächliche Größe des GBS-Elements;
   */
  private static int width;
  private static int height;

  private static double WIDTH_HEIGHT_RATIO = 5.0 / 4.0;

  /**
   * Virtuelle Größe eines GBS-Elements (Breite und Höhe).
   */
  public static final int VIRTUAL_SIZE = 2700;

  /**
   * Strichbreite des Gleises.
   */
  public static final int VIRTUAL_STROKEWIDTH_GLEIS = VIRTUAL_SIZE / 4;

  /**
   * Stroke für Gleis.
   */
  private static final BasicStroke STROKE_GLEIS = new BasicStroke(VIRTUAL_STROKEWIDTH_GLEIS, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

  /**
   * Strichbreite der Fahrstrasse innerhalb des Gleises.
   */
  public static final int VIRTUAL_STROKEWIDTH_FAHRSTRASSE = VIRTUAL_STROKEWIDTH_GLEIS / 3;

  /**
   * Stroke für Fahrstrasse.
   */
  private static final BasicStroke STROKE_FAHRSTRASSE_ROUND = new BasicStroke(VIRTUAL_STROKEWIDTH_FAHRSTRASSE, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

  /**
   * Stroke für Fahrstrasse.
   */
  private static final BasicStroke STROKE_FAHRSTRASSE_BUTT = new BasicStroke(VIRTUAL_STROKEWIDTH_FAHRSTRASSE, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);

  /**
   * Lücke an Beginn und Ende des horizontalen oder vertikalen Fahrstrassenstrichs. Entspricht der zusätzlichen Länge einer
   * Diagonalen im Vergleich.
   */
  public static final int VIRTUAL_GAP_FAHRSTRASSE = (int) ((Math.sqrt(2) - 1) * VIRTUAL_SIZE / 2);

  /**
   * Strichbreite der Signale.
   */
  public static final int VIRTUAL_STROKEWIDTH_SIGNAL = VIRTUAL_SIZE / 4;

  /**
   * Fontgröße der Gleisnummern.
   */
  public static final int VIRTUAL_FONTSIZE_GLEIS = VIRTUAL_SIZE / 4;

  /**
   * Fontgröße der Signalbezeichnungen.
   */
  public static final int VIRTUAL_FONTSIZE_SIGNAL = VIRTUAL_SIZE / 5;

  /**
   * Fontgröße der Weichennamen.
   */
  public static final int VIRTUAL_FONTSIZE_WEICHE = VIRTUAL_SIZE / 5;

  protected String bereich;
  protected String name;

  private static Cursor invisibleCursor = Toolkit.getDefaultToolkit().createCustomCursor(
      Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(16, 16, new int[16 * 16], 0, 16)), new Point(0, 0), "noCursor");

  protected Signal signal = null;
  protected GbsRichtung signalPosition = null;

  protected GbsInputPanel inputPanel = null;

  protected Logger logger = Logger.getLogger(this.getClass());

  @Inject
  Leitstand leitstand;

  @Inject
  FahrstrassenManager fahrstrassenManager;

  @Inject
  StatusDispatcher statusDispatcher;

  @Inject
  SignalRepository signalRepository;

  public GbsElement(String bereich, StellwerkElement stellwerkElement) {
    InjectionUtil.injectFields(this);

    this.bereich = bereich;
    this.name = stellwerkElement.getName();

    setBackground(Color.lightGray.brighter());
    setBorder(BorderFactory.createLineBorder(Color.gray, 1));

    Dimension dim = new Dimension(width, height);
    setPreferredSize(dim);
    setMaximumSize(dim);
    setMinimumSize(dim);

    String signalName = stellwerkElement.getSignalName();
    if (signalName != null) {
      this.signal = this.signalRepository.findById(new BereichselementId(bereich, signalName));
      if (this.signal != null) {
        this.statusDispatcher.addListener(this.signal, s -> {
          this.signal = s;
          repaint();
        });
      }

      this.signalPosition = stellwerkElement.getSignalPosition() != null ? GbsRichtung.valueOf(stellwerkElement.getSignalPosition()) : GbsRichtung.N;
    }

    addMouseListener(new MouseAdapter() {
      @Override
      public void mouseReleased(MouseEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
          @Override
          public void run() {
            processMouseClick();
          }
        });

        setCursor(invisibleCursor);
      }
    });

    addMouseMotionListener(new MouseMotionAdapter() {
      @Override
      public void mouseMoved(MouseEvent ev) {
        setCursor(null);
      }
    });
  }

  // @Override
  // public void setBounds(int x, int y, int width, int height) {
  // if (width > height) {
  // width = height;
  // }
  // if (height > width) {
  // height = width;
  // }
  // super.setBounds(x, y, width, height);
  // }

  @Override
  public void setBounds(Rectangle r) {
    throw new BugException("setBounds(Rectangle) sollte nie aufgerufen werden");
  }

  protected void processMouseClick() {
    if (this.inputPanel != null) {
      this.inputPanel.reset();

      this.inputPanel.addSignal(this.signal);
    }

  }

  @Override
  public void paintComponent(Graphics g) {
    super.paintComponent(g);

    Graphics2D g2d = (Graphics2D) g;
    // Stroke oldStroke = g2d.getStroke();
    // Font oldFont = g2d.getFont();
    AffineTransform oldTransform = g2d.getTransform();

    // Skalierung: Komponente virtuell auf VIRTUAL_SIZE * VIRTUAL_SIZE dimensionieren
    g2d.scale((double) getWidth() / VIRTUAL_SIZE, (double) getHeight() / VIRTUAL_SIZE);

    // BasicStroke stroke = new BasicStroke(VIRTUAL_STROKEWIDTH_GLEIS, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
    // g2d.setStroke(stroke);

    doPaintGleis(g2d);

    if (this.signal != null && this.signal.getTyp() != null) {
      doPaintSignal(g2d);
    }

    g2d.setTransform(oldTransform);
    // g2d.setStroke(oldStroke);
    // g2d.setFont(oldFont);
  }

  public void doPaintGleis(Graphics2D g2d) {
  }

  private void doPaintSignal(Graphics2D g2d) {
    // Koordinatensystem so rotieren, dass das Signal oben liegt
    g2d.rotate(Math.toRadians(this.signalPosition.getWinkel()), VIRTUAL_SIZE / 2, VIRTUAL_SIZE / 2);

    // Einige GbsElemente haben noch eine zusätzliche Verschiebung
    translate(g2d);

    BasicStroke stroke = new BasicStroke(VIRTUAL_STROKEWIDTH_SIGNAL, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    g2d.setStroke(stroke);
    // g2d.setColor(Color.BLACK);

    Color[] signalFarbe = getSignalFarben(this.signal);

    // Zunächst die Umrandungen der Lichter zeichnen, beginnend mit dem obersten
    Point curPoint = new Point(VIRTUAL_SIZE / 4, VIRTUAL_STROKEWIDTH_SIGNAL);
    int radiusRahmen = VIRTUAL_STROKEWIDTH_SIGNAL / 2;
    for (@SuppressWarnings("unused")
    Color color : signalFarbe) {
      g2d.setColor(Color.BLACK);
      g2d.fillOval(curPoint.x - radiusRahmen, curPoint.y - radiusRahmen, 2 * radiusRahmen, 2 * radiusRahmen);

      curPoint.translate(VIRTUAL_STROKEWIDTH_SIGNAL / 2, 0);
    }

    // Dann die Lichter darauf zeichnen
    curPoint = new Point(VIRTUAL_SIZE / 4, VIRTUAL_STROKEWIDTH_SIGNAL);
    int radiusLicht = VIRTUAL_STROKEWIDTH_SIGNAL / 5;
    for (Color color : signalFarbe) {
      g2d.setColor(color != null ? color : Color.GRAY);
      g2d.fillOval(curPoint.x - radiusLicht, curPoint.y - radiusLicht, 2 * radiusLicht, 2 * radiusLicht);

      curPoint.translate(VIRTUAL_STROKEWIDTH_SIGNAL / 2, 0);
    }

    // Nun den Signalfuß zeichnen
    stroke = new BasicStroke(VIRTUAL_STROKEWIDTH_SIGNAL / 4, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
    g2d.setStroke(stroke);
    g2d.setColor(Color.BLACK);
    g2d.drawLine(curPoint.x, curPoint.y - VIRTUAL_STROKEWIDTH_SIGNAL / 2, curPoint.x, curPoint.y + VIRTUAL_STROKEWIDTH_SIGNAL / 2);

    String label = this.signal.getName();

    // Aktuellen Punkt für die Signalbezeichnung etwas verschieben
    curPoint.translate(VIRTUAL_STROKEWIDTH_SIGNAL, 0);
    if (label.length() > 3) {
      curPoint.translate(VIRTUAL_STROKEWIDTH_SIGNAL / 2, 0);
    }

    // Koordinatensystem dort wieder so drehen, dass der Text später waagerecht liegt
    g2d.rotate(-Math.toRadians(this.signalPosition.getWinkel()), curPoint.x, curPoint.y);

    // TODO Signalbezeichnug ausgeben?
    // drawString(g2d, Color.BLACK, VIRTUAL_FONTSIZE_SIGNAL, label, curPoint.x, curPoint.y);
  }

  /**
   * Gleis- oder Fahrstrassensegment zeichnen.
   *
   * @param g2d
   *        Grafik-Kontext
   * @param color
   *        Farbe
   * @param gbsRichtung
   *        Richtung
   */
  protected void drawGleisSegment(Graphics2D g2d, Color color, GbsRichtung gbsRichtung) {
    AffineTransform oldTransform = g2d.getTransform();

    // Koordinatensystem so drehen und verschieben, dass das zu zeichnende Segment oben liegt
    g2d.rotate(Math.toRadians(gbsRichtung.getWinkel()), VIRTUAL_SIZE / 2, VIRTUAL_SIZE / 2);

    // Strich rund enden lassen, um die Verbindung mehrerer Segmente in der Mitte des Segments ohne Lücken zu gestalten
    g2d.setStroke(STROKE_GLEIS);
    g2d.setColor(color);

    // Strich im Negativen anfangen lassen, damit er auch für Diagonalen passt; der überstehende Anteil wird ohnehin wegge'clipt'
    g2d.drawLine(VIRTUAL_SIZE / 2, -VIRTUAL_SIZE, VIRTUAL_SIZE / 2, VIRTUAL_SIZE / 2);

    // Altes Koordinatensystem wieder aktivieren
    g2d.setTransform(oldTransform);
  }

  /**
   * Fahrstrassensegment zeichnen.
   *
   * @param g2d
   *        Grafik-Kontext
   * @param color
   *        Farbe
   * @param gbsRichtung
   *        Richtung
   * @param spitze
   *        <code>true</code>, wenn dieses Segment in Fahrtrichtung weist
   */
  protected void drawFahrstrassenSegment(Graphics2D g2d, Color color, GbsRichtung gbsRichtung, boolean spitze) {
    AffineTransform oldTransform = g2d.getTransform();

    // Koordinatensystem so drehen und verschieben, dass das zu zeichnende Segment oben liegt
    g2d.rotate(Math.toRadians(gbsRichtung.getWinkel()), VIRTUAL_SIZE / 2, VIRTUAL_SIZE / 2);

    g2d.setColor(color);

    g2d.setStroke(STROKE_FAHRSTRASSE_BUTT);
    int x = VIRTUAL_SIZE / 2;
    int y = 0;
    if (!gbsRichtung.isDiagonal()) {
      y += VIRTUAL_GAP_FAHRSTRASSE;
    }
    if (spitze) {
      y += VIRTUAL_STROKEWIDTH_FAHRSTRASSE;
    }
    g2d.drawLine(x, y, x, VIRTUAL_SIZE / 2);

    // Mittleren Teil des Strichs nochmals mit rundem Ende zeichnen, damit die Mitte bei abknickenden Elementen keine Lücke hat.
    g2d.setStroke(STROKE_FAHRSTRASSE_ROUND);
    g2d.drawLine(VIRTUAL_SIZE / 2, y + VIRTUAL_STROKEWIDTH_FAHRSTRASSE, VIRTUAL_SIZE / 2, VIRTUAL_SIZE / 2);

    if (spitze) {
      // Falls Spitze gewünscht, kompletten Strich mit rundem Ende zeichnen
      g2d.drawLine(VIRTUAL_SIZE / 2, y, VIRTUAL_SIZE / 2, VIRTUAL_SIZE / 2);
    }

    // Altes Koordinatensystem wieder aktivieren
    g2d.setTransform(oldTransform);
  }

  protected void drawString(Graphics2D g2d, Color color, int size, String string, int x, int y) {
    Font font = g2d.getFont();
    g2d.setFont(font.deriveFont((float) size));

    FontMetrics fontMetrics = g2d.getFontMetrics();
    Rectangle2D bounds = fontMetrics.getStringBounds(string, g2d);
    int maxDescent = fontMetrics.getMaxDescent();
    int stringWidth = (int) bounds.getWidth();
    int stringHeight = (int) bounds.getHeight();
    g2d.setColor(color);
    g2d.drawString(string, x - stringWidth / 2, y + stringHeight / 2 - maxDescent);

  }

  // protected static void drawLine(Graphics2D g2d, Color color, Point startPoint, Point endPoint)
  // {
  // g2d.setColor(color);
  // g2d.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y);
  // }

  /**
   * Grafik-Transformation durchführen.
   *
   * Einige GBS-Elemente benötigen eine Transformation des Grafik-Kontextes. In dem Fall wird diese Methode entsprechend
   * überschrieben.
   *
   * @param g2d
   *        Grafik-Kontext
   */
  protected void translate(Graphics2D g2d) {
  }

  public void setInputPanel(GbsInputPanel inputPanel) {
    this.inputPanel = inputPanel;
  }

  /**
   * GbsElement passend zum angegebenen Stellwerkelement herstellen.
   *
   * @param bereich
   *        Bereich
   * @param stellwerkElement
   *        Stellwerkelement
   * @return GbsElement
   */
  public static GbsElement createInstance(String bereich, StellwerkElement stellwerkElement) {
    // if (stellwerkElement instanceof StellwerkDkw1)
    // return new GbsDkw1(bereich, stellwerkElement);

    if (stellwerkElement instanceof StellwerkDkw2) {
      return new GbsDkw2(bereich, stellwerkElement);
    }

    if (stellwerkElement instanceof StellwerkGleis) {
      return new GbsGleis(bereich, stellwerkElement);
    }

    if (stellwerkElement instanceof StellwerkWeiche) {
      return new GbsEinfachWeiche(bereich, stellwerkElement);
    }

    return new GbsLeer(bereich, stellwerkElement);
  }

  public static GbsRichtung findBestFreePosition(GbsRichtung... usedPos) {
    /*
     * Position für die Beschriftung bestimmen: Gesucht ist die Position, die möglichst viel Raum bietet. Dazu zunächst ein Array
     * mit Negativwerten aufbauen: Ist die dem Array-Index entsprechende Position durch ein Gleis belegt, erhält sie einen Malus
     * von 100. Ist eine Nachbarposition besetzt, erhält sie einen Malus von 10.
     */
    int posCount = GbsRichtung.values().length;
    int[] posMalus = new int[posCount];
    for (GbsRichtung pos : usedPos) {
      if (pos != null) {
        int ordinal = pos.ordinal();
        posMalus[ordinal] += 100;
        posMalus[(ordinal + 1) % posCount] += 10;
        posMalus[(ordinal - 1 + posCount) % posCount] += 10;
      }
    }

    /*
     * Die Ecken haben etwas mehr Platz. Daher bekommen die anderen Positionen einen Malus von 1;
     */
    posMalus[GbsRichtung.N.ordinal()] += 1;
    posMalus[GbsRichtung.O.ordinal()] += 1;
    posMalus[GbsRichtung.S.ordinal()] += 1;
    posMalus[GbsRichtung.W.ordinal()] += 1;

    /*
     * Dann die Position mit dem geringsten Malus ermitteln.
     */
    int minMalus = Integer.MAX_VALUE;
    GbsRichtung bestPos = null;
    for (GbsRichtung pos : GbsRichtung.values()) {
      int ordinal = pos.ordinal();
      if (posMalus[ordinal] < minMalus) {
        minMalus = posMalus[ordinal];
        bestPos = pos;
      }
    }

    return bestPos;
  }

  protected static Color getGleisFarbe(Gleis gleis) {
    if (gleis != null && gleis.isBesetzt()) {
      return GbsFarben.GLEIS_BESETZT;
    }

    return GbsFarben.GLEIS_FREI;
  }

  private static final Color[] FARBEN_HP0 = new Color[] { null, Color.red };
  private static final Color[] FARBEN_HP00 = new Color[] { null, Color.red };
  private static final Color[] FARBEN_HP0SH1 = new Color[] { Color.red, Color.white };
  private static final Color[] FARBEN_HP1 = new Color[] { Color.green, null };
  private static final Color[] FARBEN_HP2 = new Color[] { Color.green, Color.yellow };
  private static final Color[] FARBEN_SH0 = new Color[] { Color.red };
  private static final Color[] FARBEN_SH1 = new Color[] { Color.white };
  private static final Color[] FARBEN_NULL = new Color[] { null, null };

  @SuppressWarnings("incomplete-switch")
  private static Color[] getSignalFarben(Signal signal) {
    if (signal.getTyp() != null) {
      switch (signal.getTyp()) {
      case HAUPTSIGNAL_RT_GE:
      case HAUPTSIGNAL_RT_GN:
      case HAUPTSIGNAL_RT_GE_GN:
        switch (signal.getStellung()) {
        case HALT:
          return FARBEN_HP0;
        case FAHRT:
          return FARBEN_HP1;
        case LANGSAMFAHRT:
          return FARBEN_HP2;
        }
        break;

      case HAUPTSPERRSIGNAL:
        switch (signal.getStellung()) {
        case HALT:
          return FARBEN_HP00;
        case FAHRT:
          return FARBEN_HP1;
        case LANGSAMFAHRT:
          return FARBEN_HP2;
        case RANGIERFAHRT:
          return FARBEN_HP0SH1;
        }
        break;

      case SPERRSIGNAL:
        switch (signal.getStellung()) {
        case HALT:
          return FARBEN_SH0;
        case RANGIERFAHRT:
          return FARBEN_SH1;
        }
        break;
      }
    }

    return FARBEN_NULL;
  }

  public static void setDimensions(int screenWidth, int screenHeight) {
    width = screenWidth / 38;
    height = screenHeight / 27;

    int w = (int) (height * WIDTH_HEIGHT_RATIO);
    if (width > w) {
      width = w;
    }

    int h = (int) (width / WIDTH_HEIGHT_RATIO);
    if (height > h) {
      height = h;
    }
  }

}
