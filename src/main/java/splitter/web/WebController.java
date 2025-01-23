package splitter.web;

import java.util.List;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import splitter.domain.gruppe.Gruppe;
import splitter.service.GruppenService;

@Controller
public class WebController {
  private final GruppenService service;

  private WebController(GruppenService service) {
    this.service = service;
  }

  @GetMapping("/")
  public String getIndex(OAuth2AuthenticationToken token, Model model) {
    String login = token.getPrincipal().getAttribute("login");
    List<Gruppe> meineGruppen = service.getNutzergruppen(login);
    model.addAttribute("meineGruppen", meineGruppen);
    return "index";
  }

  @PostMapping("/gruppe")
  public String erstelleGruppe(OAuth2AuthenticationToken token, String gruppenName) {
    String login = token.getPrincipal().getAttribute("login");
    Long id = service.addGruppe(gruppenName, login);
    return "redirect:/gruppe?id=" + id;
  }

  @GetMapping("/gruppe")
  public String zeigeGruppe(OAuth2AuthenticationToken token, Long id, Model m) {
    Gruppe gruppe = service.getGruppeById(id);
    List<String> ueberweisungen = service.getUebersicht(id);
    m.addAttribute("ueberweisungen", ueberweisungen);
    m.addAttribute("gruppe", gruppe);
    m.addAttribute("username", token.getPrincipal().getAttribute("login"));
    return "gruppe";
  }

  @PostMapping("/mitglied")
  public String fuegeMitgliedHinzu(Long id, String mitgliedName) {
    service.addMitgliedZuGruppe(mitgliedName, id);
    return "redirect:/gruppe?id=" + id;
  }

  @GetMapping("/rechnung")
  public String zeigeRechnung(Long id, Model m) {
    Gruppe gruppe = service.getGruppeById(id);
    m.addAttribute("gruppe", gruppe);
    return "rechnung";
  }

  @PostMapping("/rechnung")
  public String fuegeRechnungHinzu(String rechnungName, String bezahler,
                      @RequestParam(value = "teilnehmerNamen") List<String> teilnehmerNamen,
                      double betrag,
                      Long id, Model model) {
    Gruppe gruppe = service.getGruppeById(id);
    service.addRechnungZuGruppe(rechnungName, bezahler, teilnehmerNamen, betrag, id);
    model.addAttribute("gruppe", gruppe);
    return "redirect:/gruppe?id=" + id;
  }

  @PostMapping("/schliessen")
  public String schliesseGruppe(Long id) {
    service.schliesseGruppe(id);
    return "redirect:/gruppe?id=" + id;
  }
}
