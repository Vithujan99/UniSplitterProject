package splitter.repositories;

import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import splitter.service.GruppeRepository;

@Repository
public class GruppeRepositoryImpl implements GruppeRepository {

  private final SpringDataGruppeRepository repository;

  public GruppeRepositoryImpl(SpringDataGruppeRepository repository) {
    this.repository = repository;
  }


  @Override
  public splitter.domain.gruppe.Gruppe save(splitter.domain.gruppe.Gruppe gruppe) {
    Gruppe dto = fromGruppe(gruppe);
    Gruppe saved = repository.save(dto);
    return toGruppe(saved);
  }


  @Override
  public List<splitter.domain.gruppe.Gruppe> findAll() {
    List<Gruppe> all = repository.findAll();
    return all.stream().map(this::toGruppe).toList();
  }

  @Override
  public List<String> findTeilnehmer(@Param("uuid") String uuid) {
    List<String> teilnehmer = repository.findTeilnehmer(uuid);
    return teilnehmer;
  }

  @Override
  public Optional<splitter.domain.gruppe.Gruppe> findById(Long id) {
    return repository.findById(id).map(this::toGruppe);
  }



  private splitter.domain.gruppe.Gruppe toGruppe(Gruppe gruppeDto) {
    splitter.domain.gruppe.Gruppe gruppe =
            new splitter.domain.gruppe.Gruppe(gruppeDto.id(),
                    gruppeDto.gruppenName(), gruppeDto.mitglieder().get(0).githubName());
    gruppeDto.mitglieder().stream().skip(1).forEach(m -> gruppe.addMitglied(m.githubName()));
    gruppeDto.rechnungen().forEach(r -> gruppe
            .addRechnung(r.rechnungName, r.bezahltVon, findTeilnehmer(r.uuid), r.betrag));
    if (gruppeDto.geschlossen()) {
      gruppe.schliesseGruppe();
    }
    return gruppe;
  }

  private Gruppe fromGruppe(splitter.domain.gruppe.Gruppe gruppe) {
    List<Mitglied> mitglieder =
            gruppe.getPersonenNamen().stream().map(Mitglied::new).toList();

    List<List<String>> rechnungAttribute = gruppe.getRechnungenAttribute();
    List<Rechnung> rechnungen =
            rechnungAttribute.stream()
                    .map(r -> new Rechnung(r.get(0), r.get(1),
                        Double.parseDouble(r.get(2)) / 100.0))
                    .toList();

    for (int i = 0; i < rechnungen.size(); i++) {
      rechnungen.get(i).addMitglied(rechnungAttribute.get(i).stream().skip(3).toList());
    }

    return new Gruppe(gruppe.getId(), gruppe.getName(),
        mitglieder, rechnungen, gruppe.istGeschlossen());
  }
}
