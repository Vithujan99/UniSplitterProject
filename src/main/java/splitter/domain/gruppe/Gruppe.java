package splitter.domain.gruppe;

import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.javamoney.moneta.Money;
import splitter.annotations.AggregateRoot;

@AggregateRoot
public class Gruppe {

  private final Long id;
  private final List<Mitglied> mitglieder;
  private final String gruppenName;
  private final List<Rechnung> rechnungen;
  List<Ueberweisung> ueberweisungen;
  private boolean geschlossen;



  public Gruppe(String name, String gruenderName) {
    this(null, name, new Mitglied(gruenderName));
  }

  public Gruppe(Long id, String name, String gruenderName) {
    this(id, name, new Mitglied(gruenderName));
  }

  public Gruppe(Long id, String name, Mitglied gruender) {
    this.id = id;
    this.gruppenName = name;
    mitglieder = new ArrayList<>(List.of(gruender));
    rechnungen = new ArrayList<>();
    ueberweisungen = new ArrayList<>();
    this.geschlossen = false;
  }

  public Long getGruppe() {
    return id;
  }

  public String getName() {
    return gruppenName;
  }

  public List<String> getPersonenNamen() {
    return mitglieder.stream().map(Mitglied::getGithubName).toList();
  }

  public List<List<String>> getRechnungenAttribute() {
    List<List<String>> list = new ArrayList<>();
    for (Rechnung rechnung : rechnungen) {
      List<String> list2 = new ArrayList<>(List.of(rechnung.getGrund(),
          rechnung.getGlaeubiger(),
          rechnung.getBetrag().multiply(100).getNumber().intValue() + ""));
      list2.addAll(rechnung.getSchuldner());
      list.add(list2);
    }
    return list;
  }

  public List<List<String>> getUeberweisungAttribute() {
    List<List<String>> list = new ArrayList<>();
    for (Ueberweisung ueberweisung : ueberweisungen) {
      List<String> list2 = new ArrayList<>(List.of(ueberweisung.getVon(),
          ueberweisung.getAn(),
          ueberweisung.getBetrag().multiply(100).getNumber().intValue() + ""));
      list.add(list2);
    }
    return list;
  }

  public List<Rechnung> getRechnungen() {
    return List.copyOf(rechnungen);
  }

  public boolean istGeschlossen() {
    return geschlossen;
  }

  public void schliesseGruppe() {
    geschlossen = true;
  }

  public Long getId() {
    return id;
  }

  public List<Mitglied> getMitglieder() {
    return List.copyOf(mitglieder);
  }

  public void addMitglied(String githubName) {
    Mitglied mitglied = new Mitglied(githubName);
    String loweCaseName = githubName.toLowerCase();
    if (mitglieder.stream().noneMatch(m -> m.getGithubName().toLowerCase().equals(loweCaseName))) {
      mitglieder.add(mitglied);
    }
  }

  public Mitglied findMitglied(String name) {
    return mitglieder.stream().filter(m -> name.equals(m.getGithubName())).findFirst().orElse(null);
  }

  public void addRechnung(String rechnungName, String bezahltVonName,
                          List<String> teilnehmerNamen, double betrag) {
    Mitglied bezahltVon = findMitglied(bezahltVonName);
    List<Mitglied> teilnehmer = teilnehmerNamen.stream().map(this::findMitglied).toList();
    Rechnung rechnung = new Rechnung(rechnungName, bezahltVon, teilnehmer, betrag);
    rechnungen.add(rechnung);
    rechnung.berechneEinzelbetraege();
    ueberweisungen = berechneUebersicht();
  }

  //----Übersichtsberechnung----
  public List<String> erhalteUebersichtString() {
    List<String> uebersichtString = new ArrayList<>(ueberweisungen.stream()
                                                        .map(Ueberweisung::toString).toList());
    if (uebersichtString.isEmpty()) {
      uebersichtString.add("Es sind keine Ausgleichszahlungen notwendig.");
    }
    return uebersichtString;
  }

  public List<Ueberweisung> berechneUebersicht() {
    List<Mitglied> differenzPositiv = mitglieder.stream()
                                          .filter(m -> m.berechneDifferenz().isPositive()).toList();
    Set<Mitglied> posSet = new LinkedHashSet<>(differenzPositiv);
    List<Mitglied> differenzNegativ = mitglieder.stream()
                                          .filter(m -> m.berechneDifferenz().isNegative()).toList();
    Set<Mitglied> negSet = new LinkedHashSet<>(differenzNegativ);

    Set<Set<Mitglied>> posPotenz = potenzMenge(posSet);
    Set<Set<Mitglied>> negPotenz = potenzMenge(negSet);

    return berechneUebersicht(posPotenz, negPotenz);
  }

  private List<Ueberweisung> berechneUebersicht(Set<Set<Mitglied>> differenzPositiv,
                                                Set<Set<Mitglied>> differenzNegativ) {
    List<Ueberweisung> uebList = new ArrayList<>();
    Map<Mitglied, Money> restZahlung = mitglieder.stream()
        .collect(Collectors.toMap(Function.identity(), Mitglied::berechneDifferenz));

    for (Set<Mitglied> empfaengerSet : differenzPositiv) {
      if (hasNoRestzahlungZero(restZahlung, empfaengerSet)) {

        for (Set<Mitglied> schuldnerSet : differenzNegativ) {
          if (hasNoRestzahlungZero(restZahlung, schuldnerSet)) {

            Money geschuldet = getTeilgruppenSumme(empfaengerSet);
            Money schulden = getTeilgruppenSumme(schuldnerSet);
            if (schulden.isEqualTo(geschuldet.negate())) {
              uebList.addAll(berechneTeilUebersicht(restZahlung, empfaengerSet, schuldnerSet));
              break;
            }

          }
        }

      }
    }
    return uebList;
  }

  private List<Ueberweisung> berechneTeilUebersicht(Map<Mitglied, Money> restZahlung,
                                                    Set<Mitglied> empfaengerSet,
                                                    Set<Mitglied> schuldnerSet) {
    List<Ueberweisung> uebList = new ArrayList<>();

    for (Mitglied empfaenger : empfaengerSet) {
      for (Mitglied schuldner : schuldnerSet) {
        Money schulden = restZahlung.get(schuldner);
        if (!schulden.isZero()) {
          restZahlung.put(empfaenger, restZahlung.get(empfaenger).add(schulden));
          Money summe = restZahlung.get(empfaenger);
          if (summe.isPositiveOrZero()) {
            uebList.add(new Ueberweisung(schuldner.getGithubName(),
                    empfaenger.getGithubName(),
                    schulden.abs()));
            restZahlung.put(schuldner, Money.of(0, "EUR"));
            if (summe.isZero()) {
              break;
            }
          } else {
            uebList.add(new Ueberweisung(schuldner.getGithubName(),
                    empfaenger.getGithubName(),
                summe.subtract(schulden)));
            restZahlung.put(schuldner, summe);
            break;
          }
        }
      }
    }
    return uebList;
  }

  private Set<Set<Mitglied>> potenzMenge(Set<Mitglied> set) {
    Set<Set<Mitglied>> potenzSet = new LinkedHashSet<>();
    Set<Set<Mitglied>> potenz = Sets.powerSet(set).stream()
            .filter(s -> !s.isEmpty()).collect(Collectors.toSet());
    for (int i = 1; i <= set.size(); i++) {
      int potenzSize = i;
      potenzSet.addAll(potenz.stream()
              .filter(mitgliederSet -> mitgliederSet.size() == potenzSize).toList());
    }
    return potenzSet;
  }

  private Money getTeilgruppenSumme(Set<Mitglied> teilgruppe) {
    Money geld = Money.of(0, "EUR");
    for (Mitglied mitglied : teilgruppe) {
      geld = geld.add(mitglied.berechneDifferenz());
    }
    return geld;
  }

  private boolean hasNoRestzahlungZero(Map<Mitglied, Money> restZahlung,
                                       Set<Mitglied> teilmenge) {
    return teilmenge.stream().noneMatch(m -> restZahlung.get(m).isZero());
  }
}


