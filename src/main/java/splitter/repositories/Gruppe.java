package splitter.repositories;

import java.util.List;
import org.springframework.data.annotation.Id;

public record Gruppe(@Id Long id,
                     String gruppenName,
                     List<Mitglied> mitglieder,
                     List<Rechnung> rechnungen,
                     boolean geschlossen) {
}
