package splitter.repositories;

import java.util.List;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface SpringDataGruppeRepository extends CrudRepository<Gruppe, Long> {
  List<Gruppe> findAll();

  @Query("SELECT mr.mitglied_name "
      + "FROM mitglied_rechnung mr "
      + "WHERE mr.rechnung = :uuid")
  List<String> findTeilnehmer(@Param("uuid") String uuid);
}
