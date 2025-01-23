package splitter.domain.gruppe;

import org.javamoney.moneta.Money;

class Mitglied {

  private final String githubName;
  private Money ausgaben;
  private Money eigentlicherAnteil;

  public Mitglied(String githubName) {
    this.githubName = githubName;
    ausgaben = Money.of(0, "EUR");
    eigentlicherAnteil = Money.of(0, "EUR");
  }

  public String getGithubName() {
    return githubName;
  }

  public void increaseAusgaben(Money ausgabe) {
    ausgaben = ausgaben.add(ausgabe);
  }

  public void increaseAnteil(Money anteil) {
    eigentlicherAnteil = eigentlicherAnteil.add(anteil);
  }

  public Money berechneDifferenz() {
    return ausgaben.subtract(eigentlicherAnteil);
  }
}
