package de.gedoplan.v5t11.fahrzeuge.service;

import de.gedoplan.baselibs.utils.xml.XmlConverter;
import de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.Fahrzeug;
import de.gedoplan.v5t11.fahrzeuge.persistence.FahrzeugRepository;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.xml.bind.JAXBException;

import org.jboss.logging.Logger;

@Dependent
public class FahrzeugeInitService {

  @Inject
  FahrzeugRepository fahrzeugRepository;

  @Inject
  Logger logger;

  @Transactional
  public void loadFahrzeuge(Path path) {
    logger.debugf("Fahrzeuge aus %s importieren", path);
    File dir = path.toFile();
    if (dir.exists() && dir.isDirectory()) {
      for (File xmlFile : dir.listFiles((f, n) -> n.endsWith(".xml"))) {
        loadFahrzeug(xmlFile);
      }
    }
  }

  private void loadFahrzeug(File xmlFile) {
    try (Reader reader = new FileReader(xmlFile)) {
      Fahrzeug fahrzeug = XmlConverter.fromXml(Fahrzeug.class, reader);

      if (this.fahrzeugRepository.findById(fahrzeug.getId()).isEmpty()) {
        this.fahrzeugRepository.persist(fahrzeug);
        logger.debugf("Fahrzeug %s aus %s importiert", fahrzeug.getBetriebsnummer(), xmlFile);
      } else {
        logger.debugf("Fahrzeug %s existiert bereits", fahrzeug.getBetriebsnummer(), xmlFile);
      }
    } catch (JAXBException e) {
      logger.warnf("Datei %s enthält keine gültige Fahrzeugdefinition", xmlFile);
    } catch (FileNotFoundException e) {
      // ignore
    } catch (IOException e) {
      logger.errorf(e, "Kann Datei %s nicht lesen", xmlFile);
    }
  }
}
