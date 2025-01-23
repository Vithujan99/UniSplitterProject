package splitter.domain.gruppe;

import org.javamoney.moneta.Money;

class Ueberweisung {
  String von;
  String an;
  Money betrag;

  public Ueberweisung(String von, String an, Money betragEuro) {
    this.von = von;
    this.an = an;
    betrag = betragEuro;
  }

  public Ueberweisung(String von, String an, int betragCent) {
    this(von, an, Money.of(betragCent, "EUR"));
  }

  public String getVon() {
    return von;
  }

  public String getAn() {
    return an;
  }

  public Money getBetrag() {
    return betrag;
  }

  @Override
  public String toString() {
    return von + " schuldet " + an + " " + betrag;
  }


}
