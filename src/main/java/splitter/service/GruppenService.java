package splitter.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import splitter.domain.gruppe.Gruppe;

@Service
public class GruppenService {

  private final GruppeRepository repository;

  public GruppenService(GruppeRepository repository) {
    this.repository = repository;
  }


  public Long addGruppe(String name, String githubName) {
    Gruppe neueGruppe = new Gruppe(name, githubName);
    Gruppe gruppe = repository.save(neueGruppe);
    return gruppe.getId();
  }

  public List<Gruppe> getGruppen() {
    return List.copyOf(repository.findAll());
  }

  public List<Gruppe> getNutzergruppen(String githubName) {
    return getGruppen().stream().filter(g -> g.findMitglied(githubName) != null).toList();
  }

  public Gruppe getGruppeById(Long id) {
    return repository.findById(id)
            .orElseThrow(NichtVorhandenException::new);
  }

  public void addMitgliedZuGruppe(String zielName, Long id) {
    Gruppe gruppe = getGruppeById(id);
    gruppe.addMitglied(zielName);
    repository.save(gruppe);
  }

  @Transactional
  public synchronized void addRechnungZuGruppe(String rechnungName, String bezahltVonName,
                                  List<String> teilnehmerNamen, double betrag, Long id) {
    Gruppe gruppe = getGruppeById(id);
    gruppe.addRechnung(rechnungName, bezahltVonName, teilnehmerNamen, betrag);
    repository.save(gruppe);
  }

  public List<String> getUebersicht(Long id) {
    Gruppe gruppe = getGruppeById(id);
    return gruppe.erhalteUebersichtString();
  }

  public void schliesseGruppe(Long id) {
    Gruppe gruppe = getGruppeById(id);
    gruppe.schliesseGruppe();
    repository.save(gruppe);
  }
}
