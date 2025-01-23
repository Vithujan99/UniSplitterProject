package splitter.api.records;

import java.util.List;

public record GruppeFullRecord(String gruppe, String name, List<String> personen,
                               Boolean geschlossen, List<RechnungRecord> ausgaben) {
}
