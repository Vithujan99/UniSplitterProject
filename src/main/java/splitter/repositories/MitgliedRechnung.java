package splitter.repositories;

//@Table("mitglied_rechnung")
public class MitgliedRechnung {
  private final String mitgliedName;
  private final String rechnung;

  public MitgliedRechnung(String mitglied, String uuid) {
    this.mitgliedName = mitglied;
    this.rechnung = uuid;
  }

  public String getMitgliedName() {
    return mitgliedName;
  }
}
