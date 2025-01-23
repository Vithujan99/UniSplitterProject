package splitter.api;

import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import splitter.api.records.*;
import splitter.domain.gruppe.Gruppe;
import splitter.service.GruppenService;

@RestController
public class ApiController {

  private final GruppenService service;

  private ApiController(GruppenService service) {
    this.service = service;
  }

  @PostMapping("/api/gruppen")
  public ResponseEntity<Long> erstelleGruppe(@RequestBody GruppeErstellenRecord gruppe) {
    if (gruppe.name() == null || gruppe.personen() == null || gruppe.personen().isEmpty()) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    Long id = service.addGruppe(gruppe.name(), gruppe.personen().get(0));
    gruppe.personen().stream().skip(1).forEach(p -> service.addMitgliedZuGruppe(p, id));
    return new ResponseEntity<>(id, HttpStatus.CREATED);
  }

  @GetMapping("/api/user/{GITHUB-LOGIN}/gruppen")
  public ResponseEntity<List<GruppeRecord>> getNutzergruppen(
      @PathVariable("GITHUB-LOGIN") String name) {
    List<Gruppe> nutzergruppen = service.getNutzergruppen(name);
    List<GruppeRecord>  fakenutzerGruppen = new ArrayList<>();
    for (Gruppe gruppe : nutzergruppen) {
      fakenutzerGruppen
          .add(new GruppeRecord(gruppe.getId().toString(),
              gruppe.getName(), gruppe.getPersonenNamen()));
    }
    return new ResponseEntity<>(fakenutzerGruppen, HttpStatus.OK);
  }

  @GetMapping("/api/gruppen/{ID}")
  public ResponseEntity<GruppeFullRecord> getGruppeById(@PathVariable("ID") String id) {
    Long idL;
    try {
      idL = Long.parseLong(id);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    Gruppe gruppe = service.getGruppeById(idL);
    if (gruppe == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    List<RechnungRecord> auslagen = new ArrayList<>();
    List<List<String>> ausgaben = gruppe.getRechnungenAttribute();
    for (List<String> ausgabe : ausgaben) {
      String grund = ausgabe.get(0);
      String glaeubiger = ausgabe.get(1);
      int cents = Integer.parseInt(ausgabe.get(2));
      List<String> schuldner = ausgabe.stream().skip(3).toList();
      auslagen.add(new RechnungRecord(grund, glaeubiger, cents, schuldner));
    }
    GruppeFullRecord gruppeFullRecord =
        new GruppeFullRecord(id, gruppe.getName(),
            gruppe.getPersonenNamen(), gruppe.istGeschlossen(), auslagen);
    return new ResponseEntity<>(gruppeFullRecord, HttpStatus.OK);
  }

  @PostMapping("/api/gruppen/{ID}/schliessen")
  public ResponseEntity<String> gruppeSchliessen(@PathVariable("ID") String id) {
    Long idL;
    try {
      idL = Long.parseLong(id);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    if (service.getGruppeById(idL) == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    service.schliesseGruppe(idL);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @PostMapping("/api/gruppen/{ID}/auslagen")
  public ResponseEntity<String> auslage(@PathVariable("ID") String id,
                                        @RequestBody RechnungRecord rechnung) {
    Long idL;
    try {
      idL = Long.parseLong(id);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    if (service.getGruppeById(idL) == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    if (rechnung.grund() == null || rechnung.cent() == 0 || rechnung.schuldner() == null
            || rechnung.schuldner().isEmpty() || rechnung.glaeubiger() == null) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    if (service.getGruppeById(idL).istGeschlossen()) {
      return new ResponseEntity<>(HttpStatus.CONFLICT);
    }
    service.addRechnungZuGruppe(rechnung.grund(), rechnung.glaeubiger(),
        rechnung.schuldner(), rechnung.cent() / 100.0, idL);
    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  @GetMapping("/api/gruppen/{ID}/ausgleich")
  public ResponseEntity<List<AusgleichRecord>> ausgleichzahlung(@PathVariable("ID") String id) {
    Long idL;
    try {
      idL = Long.parseLong(id);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    if (service.getGruppeById(idL) == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    List<List<String>> ausgleichzahlung = service.getGruppeById(idL).getUeberweisungAttribute();
    List<AusgleichRecord> ausgleichRecords = new ArrayList<>();
    for (List<String> ausgleich : ausgleichzahlung) {
      ausgleichRecords.add(new AusgleichRecord(ausgleich.get(0),
          ausgleich.get(1), Integer.parseInt(ausgleich.get(2))));
    }
    return new ResponseEntity<>(ausgleichRecords, HttpStatus.OK);
  }
}
