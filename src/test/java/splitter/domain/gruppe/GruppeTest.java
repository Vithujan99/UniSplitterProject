package splitter.domain.gruppe;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.assertj.core.api.Assertions.*;

public class GruppeTest {
  private Gruppe gruppe;
  @BeforeEach
  void function() {
    gruppe = new Gruppe("Gruppe1", "Hans");
  }
  @Test
  @DisplayName("Eine neue Gruppe hat genau eine Person")
  void test1(){
    assertThat(gruppe.getMitglieder()).hasSize(1);
  }

  @Test
  @DisplayName("Ein Mitglied wird der Gruppe hinzugefuegt")
  void test2(){
    gruppe.addMitglied("Otto");
    assertThat(gruppe.getMitglieder()).hasSize(2);
  }

  @Test
  @DisplayName("Doppeltes Mitglied wird nicht hinzugefuegt")
  void test3(){
    gruppe.addMitglied("Otto");
    gruppe.addMitglied("OTTO");
    assertThat(gruppe.getMitglieder()).hasSize(2);
  }

  @Test
  @DisplayName("Eine Rechnung wird hinzugefuegt")
  void test4() {
    int anzahlRechnungen = gruppe.getRechnungen().size();
    gruppe.addMitglied("Otto");
    gruppe.addRechnung("GetraenkeRechnung","Hans", List.of("Hans","Otto"), 100);

    assertThat(gruppe.getRechnungen()).hasSize(anzahlRechnungen+1);
  }

  @Test
  @DisplayName("Ueberweisungsberechnung funktioniert")
  void test5() {
    gruppe.addMitglied("Otto");
    gruppe.addMitglied("Jerome");
    gruppe.addMitglied("Anna");
    gruppe.addMitglied("Peter");

    gruppe.addRechnung("GetraenkeRechnung","Hans", List.of("Hans","Otto", "Jerome", "Anna", "Peter"), 100);
    assertThat(gruppe.berechneUebersicht().stream().map(u -> u.toString()).toList()).containsExactlyInAnyOrder("Otto schuldet Hans EUR 20.00", "Jerome schuldet Hans EUR 20.00", "Anna schuldet Hans EUR 20.00", "Peter schuldet Hans EUR 20.00");
  }

  @Test
  @DisplayName("Ueberweisungsberechnung mit 2 Rechnungen funktioniert")
  void test6() {
    gruppe.addMitglied("Otto");
    gruppe.addMitglied("Jerome");
    gruppe.addMitglied("Anna");
    gruppe.addMitglied("Peter");

    gruppe.addRechnung("GetraenkeRechnung","Hans", List.of("Hans","Otto", "Jerome", "Anna", "Peter"), 100);
    gruppe.addRechnung("GetraenkeRechnung","Peter", List.of("Hans", "Jerome", "Anna"), 30);
    assertThat(gruppe.berechneUebersicht().stream().map(u -> u.toString()).toList())
        .containsExactlyInAnyOrder("Otto schuldet Hans EUR 20.00", "Jerome schuldet Hans EUR 30.00", "Anna schuldet Hans EUR 20.00", "Anna schuldet Peter EUR 10.00");
  }

  @Test
  @DisplayName("Ueberweisungsberechnung funktioniert mit identischen Differenzen")
  void test7() {
    gruppe.addMitglied("Otto");
    gruppe.addMitglied("Jerome");
    gruppe.addMitglied("Anna");
    gruppe.addMitglied("Peter");

    gruppe.addRechnung("GetraenkeRechnung","Otto", List.of("Hans"), 30);
    gruppe.addRechnung("GetraenkeRechnung2","Anna", List.of("Peter", "Jerome", "Anna"), 30);
    assertThat(gruppe.berechneUebersicht().stream().map(u -> u.toString()).toList()).containsExactlyInAnyOrder("Hans schuldet Otto EUR 30.00", "Jerome schuldet Anna EUR 10.00", "Peter schuldet Anna EUR 10.00");
  }
}
