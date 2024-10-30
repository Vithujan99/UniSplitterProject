package splitter.domain.gruppe;

import org.javamoney.moneta.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class RechnungTest {

    @Test
    @DisplayName("anteilProPerson stimmt")
    void test1() {
        Mitglied hans = new Mitglied("Hans");
        Mitglied otto = new Mitglied("Otto");
        Mitglied greta = new Mitglied("Greta");
        Rechnung rechnung = new Rechnung("GetraenkeRechnung",hans, List.of(hans,otto,greta), 90);

        assertThat(rechnung.anteilProPerson()).isEqualTo(Money.of(30,"EUR"));
    }

    @Test
    @DisplayName("Einzelbetraege werden richtig berechnet")
    void test2() {
        Mitglied hans = new Mitglied("Hans");
        Mitglied otto = new Mitglied("Otto");
        Mitglied greta = new Mitglied("Greta");
        Rechnung rechnung = new Rechnung("GetraenkeRechnung",hans, List.of(hans,otto,greta), 90);
        rechnung.berechneEinzelbetraege();

        assertThat(hans.berechneDifferenz()).isEqualTo(Money.of(60,"EUR"));
        assertThat(otto.berechneDifferenz()).isEqualTo(Money.of(-30,"EUR"));
        assertThat(greta.berechneDifferenz()).isEqualTo(Money.of(-30,"EUR"));
    }

    @Test
    @DisplayName("istBeteiligt findet Mitglied")
    void test3() {
        Mitglied hans = new Mitglied("Hans");
        Mitglied otto = new Mitglied("Otto");
        Mitglied greta = new Mitglied("Greta");
        Rechnung rechnung = new Rechnung("GetraenkeRechnung",hans, List.of(hans,otto,greta), 90);

        assertThat(rechnung.istBeteiligt("Hans")).isTrue();
    }

    @Test
    @DisplayName("istBeteiligt mit nicht beteiligtem Mitglied")
    void test4() {
        Mitglied hans = new Mitglied("Hans");
        Mitglied otto = new Mitglied("Otto");
        Mitglied greta = new Mitglied("Greta");
        Rechnung rechnung = new Rechnung("GetraenkeRechnung",hans, List.of(hans,otto,greta), 90);

        assertThat(rechnung.istBeteiligt("ichBinNichtDrin")).isFalse();
    }
}
