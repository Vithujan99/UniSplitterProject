package splitter.service;

import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.query.Param;
import splitter.domain.gruppe.Gruppe;

public interface GruppeRepository {


  Gruppe save(Gruppe gruppe);

  List<Gruppe> findAll();

  List<String> findTeilnehmer(@Param("uuid") String uuid);

  Optional<Gruppe> findById(Long id);
}
