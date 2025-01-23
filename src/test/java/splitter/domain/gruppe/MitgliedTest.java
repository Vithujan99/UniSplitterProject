package splitter.domain.gruppe;

import org.javamoney.moneta.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

public class MitgliedTest {

    @Test
    @DisplayName("getDifferenz funktioniert")
    void test1(){
        Mitglied testM = new Mitglied("hans");
        testM.increaseAusgaben(Money.of(20,"EUR"));
        assertThat(testM.berechneDifferenz()).isEqualTo(Money.of(20,"EUR"));
    }
}
