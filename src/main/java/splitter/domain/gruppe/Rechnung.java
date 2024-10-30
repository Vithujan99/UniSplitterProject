package splitter.domain.gruppe;

import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.javamoney.moneta.Money;

class Rechnung {

  String id;
  private final String rechnungName;
  private final Mitglied bezahltVon;
  private final List<Mitglied> teilnehmer;
  private final Money betrag;

  public Rechnung(String id, String rechnungName, Mitglied bezahltVon,
                  List<Mitglied> teilnehmer, double betrag) {
    this.id = id;
    this.rechnungName = rechnungName;
    this.bezahltVon = bezahltVon;
    this.teilnehmer = List.copyOf(teilnehmer);
    this.betrag = Money.of(betrag, "EUR");
  }

  public Rechnung(String rechnungName, Mitglied bezahltVon,
                  List<Mitglied> teilnehmer, double betrag) {
    this(null, rechnungName, bezahltVon, teilnehmer, betrag);
  }

  public String getId() {
    return id;
  }

  public String getGrund() {
    return rechnungName;
  }

  public String getGlaeubiger() {
    return bezahltVon.getGithubName();
  }

  public Money getBetrag() {
    return betrag;
  }

  public List<String> getSchuldner() {
    return teilnehmer.stream().map(Mitglied::getGithubName).toList();
  }

  public void berechneEinzelbetraege() {
    bezahltVon.increaseAusgaben(anteilProPerson().multiply(teilnehmer.size()));
    teilnehmer.forEach(m -> m.increaseAnteil(anteilProPerson()));
  }

  Money anteilProPerson() {
    int anzahlTeilnehmer = teilnehmer.size();
    int out = betrag.multiply(100).getNumber().intValue();

    return Money.of(out / anzahlTeilnehmer, "EUR").divide(100);
  }

  //VERÄNDERT
  private String getTeilnehmerNamen(List<Mitglied> teilnehmer) {
    StringBuilder result = new StringBuilder();

    for (Mitglied m : teilnehmer) {
      result.append(m.getGithubName()).append(", ");
    }
    String out = result.toString();
    return StringUtils.removeEnd(out, ", ");
  }

  //Wird von gruppe.html benutzt
  public boolean istBeteiligt(String gitHubName) {
    return gitHubName.equals(bezahltVon.getGithubName())
               || teilnehmer.stream().anyMatch(t -> t.getGithubName().equals(gitHubName));
  }


  //VERÄNDERT
  @Override
  public String toString() {
    return rechnungName
        + ": " + bezahltVon.getGithubName()
        + " hat " + betrag
        + " für " + getTeilnehmerNamen(teilnehmer)
        + " ausgegeben.";
  }
}
