package splitter.api.records;

import java.util.List;

public record RechnungRecord(String grund, String glaeubiger, int cent, List<String> schuldner) {
}
