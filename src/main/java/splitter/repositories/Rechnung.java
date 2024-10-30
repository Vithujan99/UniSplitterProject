package splitter.repositories;

import static java.util.UUID.randomUUID;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Rechnung {

  String uuid;
  String rechnungName;
  String bezahltVon;
  double betrag;
  Set<MitgliedRechnung> teilnehmerRef = new HashSet<>();

  public Rechnung(String rechnungName, String bezahltVon, double betrag) {
    this.rechnungName = rechnungName;
    this.bezahltVon = bezahltVon;
    this.betrag = betrag;
    this.uuid = randomUUID() + "";
  }

  void addMitglied(List<String> mitgliedNamen) {
    mitgliedNamen.forEach(t -> teilnehmerRef.add(new MitgliedRechnung(t, this.uuid)));
  }
}
