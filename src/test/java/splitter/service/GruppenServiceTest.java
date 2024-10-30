package splitter.service;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.jdbc.Sql;
import splitter.domain.gruppe.Gruppe;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import splitter.repositories.GruppeRepositoryImpl;
import splitter.repositories.SpringDataGruppeRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJdbcTest
public class GruppenServiceTest {

    @Autowired
    SpringDataGruppeRepository springrepository;
    GruppeRepository repository;


    @BeforeEach
    void init() {
        repository = new GruppeRepositoryImpl(springrepository);
    }

    @Test
    @DisplayName("Eine Gruppe wird hinzugefügt")
    void test1() {
        GruppenService service = new GruppenService(repository);
        service.addGruppe("gruppe1", "Hans");

        assertThat(service.getGruppen()).hasSize(1);
    }

    @Test
    @DisplayName("Gruppen werden richtig nach Nutzer gefiltert")
    void test2() {
        GruppenService service = new GruppenService(repository);
        Long id = service.addGruppe("gruppe1", "Hans");
        Long id2 = service.addGruppe("gruppe2", "Hans");
        Long id3 = service.addGruppe("gruppe3", "Peter");

        List<Gruppe> hansGruppen = service.getNutzergruppen("Hans");

        assertThat(hansGruppen).hasSize(2);
    }

    @Test
    @Sql("clear_tables.sql")
    @DisplayName("Gruppe wird mit Id gefunden")
    void test3() {
        GruppenService service = new GruppenService(repository);
        Long id = service.addGruppe("gruppe1", "Hans");
        Long id2 = service.addGruppe("gruppe2", "Hans");

        assertThat(service.getGruppeById(id).getId()).isEqualTo(1);
        assertThat(service.getGruppeById(id2).getId()).isEqualTo(2);
    }

    @Test
    @DisplayName("Mitglied wird zu Gruppe hinzugefuegt")
    void test4() {
        GruppenService service = new GruppenService(repository);
        Long id = service.addGruppe("gruppe1", "Hans");

        service.addMitgliedZuGruppe("Otto", id);
        Gruppe gruppe = service.getGruppeById(id);

        assertThat(gruppe.getMitglieder()).hasSize(2);
    }

    @Test
    @DisplayName("Eine Rechnung wird zur Gruppe hinzugefuegt")
    void test5() {
        GruppenService service = new GruppenService(repository);
        Long id = service.addGruppe("gruppe1", "Hans");
        Gruppe gruppe = service.getGruppeById(id);

        service.addMitgliedZuGruppe("Otto", id);
        service.addRechnungZuGruppe("GetraenkeRechnung","Hans", List.of("Hans","Otto"), 100.0, id);

        assertThat(service.getGruppeById(id).getRechnungen()).hasSize(1);
    }

    @Test
    @DisplayName("Test Szenario 1")
    void test6(){
        GruppenService service = new GruppenService(repository);
        Long id = service.addGruppe("gruppe1", "A");
        Gruppe gruppe = service.getGruppeById(id);

        service.addMitgliedZuGruppe("B", id);

        service.addRechnungZuGruppe("","A", List.of("A", "B"), 10.00, id);
        service.addRechnungZuGruppe("","A", List.of("A", "B"), 20.00, id);



        assertThat(service.getUebersicht(id).stream().map(u -> u.toString()).toList())
                .containsExactlyInAnyOrder("B schuldet A EUR 15.00");


    }

    @Test
    @DisplayName("Test Szenario 2")
    void test7(){
        GruppenService service = new GruppenService(repository);
        Long id = service.addGruppe("gruppe1", "A");
        Gruppe gruppe = service.getGruppeById(id);

        service.addMitgliedZuGruppe("B", id);

        service.addRechnungZuGruppe("","A", List.of("A", "B"), 10.00, id);
        service.addRechnungZuGruppe("","B", List.of("A", "B"), 20.00, id);

        assertThat(service.getUebersicht(id).stream().map(u -> u.toString()).toList())
                .containsExactlyInAnyOrder("A schuldet B EUR 5.00");


    }

    @Test
    @DisplayName("Test Szenario 3")
    void test8(){
        GruppenService service = new GruppenService(repository);
        Long id = service.addGruppe("gruppe1", "A");
        Gruppe gruppe = service.getGruppeById(id);

        service.addMitgliedZuGruppe("B", id);

        service.addRechnungZuGruppe("","A", List.of( "B"), 10.00, id);
        service.addRechnungZuGruppe("","A", List.of("A", "B"), 20.00, id);

        assertThat(service.getUebersicht(id).stream().map(u -> u.toString()).toList())
                .containsExactlyInAnyOrder("B schuldet A EUR 20.00");

    }

    @Test
    @DisplayName("Test Szenario 4")
    void test9(){
        GruppenService service = new GruppenService(repository);
        Long id = service.addGruppe("gruppe1", "A");
        Gruppe gruppe = service.getGruppeById(id);

        service.addMitgliedZuGruppe("B", id);
        service.addMitgliedZuGruppe("C", id);

        service.addRechnungZuGruppe("","A", List.of( "A", "B"), 10.00, id);
        service.addRechnungZuGruppe("","B", List.of("B", "C"), 10.00, id);
        service.addRechnungZuGruppe("","C", List.of("C", "A"), 10.00, id);

        assertThat(service.getUebersicht(id))
                .containsExactlyInAnyOrder("Es sind keine Ausgleichszahlungen notwendig.");

    }

    @Test
    @DisplayName("Test Szenario 5")
    void test10(){
        GruppenService service = new GruppenService(repository);
        Long id = service.addGruppe("gruppe1", "Anton");
        Gruppe gruppe = service.getGruppeById(id);

        service.addMitgliedZuGruppe("Berta", id);
        service.addMitgliedZuGruppe("Christian", id);

        service.addRechnungZuGruppe("","Anton", List.of("Anton", "Berta", "Christian"), 60.00, id);
        service.addRechnungZuGruppe("","Berta", List.of("Anton", "Berta", "Christian"), 30.00, id);
        service.addRechnungZuGruppe("","Christian", List.of("Berta", "Christian"), 100.00, id);

        assertThat(service.getUebersicht(id))
                .containsExactlyInAnyOrder("Berta schuldet Anton EUR 30.00", "Berta schuldet Christian EUR 20.00");

    }

    @Test
    @DisplayName("Test Szenario 6")
    void test12(){
        GruppenService service = new GruppenService(repository);
        Long id = service.addGruppe("gruppe1", "A");
        Gruppe gruppe = service.getGruppeById(id);

        service.addMitgliedZuGruppe("B", id);
        service.addMitgliedZuGruppe("C", id);
        service.addMitgliedZuGruppe("D", id);
        service.addMitgliedZuGruppe("E", id);
        service.addMitgliedZuGruppe("F", id);

        service.addRechnungZuGruppe("Hotel","A", List.of("A", "B", "C","D","E", "F"), 564.0, id);
        service.addRechnungZuGruppe("Hinfahrt","B", List.of("A", "B"), 38.58, id);
        service.addRechnungZuGruppe("Rueckfahrt","B", List.of("A", "D", "B"), 38.58, id);
        service.addRechnungZuGruppe("C Auto","C", List.of("C", "E", "F"), 82.11, id);
        service.addRechnungZuGruppe("Stadttour","D", List.of("A", "B", "C","D","E", "F"), 96.00, id);
        service.addRechnungZuGruppe("Theater","F", List.of("B", "E", "F"), 95.37, id);

        assertThat(service.getUebersicht(id))
                .containsExactlyInAnyOrder("B schuldet A EUR 96.78", "C schuldet A EUR 55.26", "D schuldet A EUR 26.86", "E schuldet A EUR 169.16", "F schuldet A EUR 73.79");
    }

    @Test
    @DisplayName("Test Szenario 7")
    void test13(){
        GruppenService service = new GruppenService(repository);
        Long id = service.addGruppe("gruppe1", "A");
        Gruppe gruppe = service.getGruppeById(id);

        service.addMitgliedZuGruppe("B", id);
        service.addMitgliedZuGruppe("C", id);
        service.addMitgliedZuGruppe("D", id);
        service.addMitgliedZuGruppe("E", id);
        service.addMitgliedZuGruppe("F", id);
        service.addMitgliedZuGruppe("G", id);

        service.addRechnungZuGruppe("","D", List.of("D", "F"), 20.0, id);
        service.addRechnungZuGruppe("","G", List.of("B"), 10.0, id);
        service.addRechnungZuGruppe("","E", List.of("A", "C", "E"), 75.0, id);
        service.addRechnungZuGruppe("","F", List.of("A", "F"), 50.0, id);
        service.addRechnungZuGruppe("","E", List.of("D"), 40.0, id);
        service.addRechnungZuGruppe("","F", List.of("B", "F"), 40.0, id);
        service.addRechnungZuGruppe("","F", List.of("C"), 5.0, id);
        service.addRechnungZuGruppe("","G", List.of("A"), 30.0, id);


        assertThat(service.getUebersicht(id))
            .containsExactlyInAnyOrder("A schuldet F EUR 40.00", "A schuldet G EUR 40.00", "B schuldet E EUR 30.00", "C schuldet E EUR 30.00", "D schuldet E EUR 30.00");
    }


}