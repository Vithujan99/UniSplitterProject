package splitter.api;

import splitter.api.records.GruppeErstellenRecord;
import splitter.api.records.RechnungRecord;
import splitter.config.SecurityConfig;
import splitter.domain.gruppe.Gruppe;
import splitter.service.GruppenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.assertj.core.api.Assertions.*;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@Import(SecurityConfig.class)
public class ApiControllerTest {
  @Autowired
  MockMvc mvc;

  @MockBean
  GruppenService service;

  @Autowired
  private ObjectMapper mapper;

  @Test
  @DisplayName("Neue Gruppe kann erstellt werden")
  void test1() throws Exception {
    GruppeErstellenRecord gruppe = new GruppeErstellenRecord("Urlaub", List.of("Hans", "Otto"));
    mvc.perform(post("/api/gruppen").content(mapper.writeValueAsString(gruppe)).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is(201));
  }

  @Test
  @DisplayName("Leere Gruppe wird nicht erstellt und gibt Status 400")
  void test2() throws Exception {
    GruppeErstellenRecord gruppe = new GruppeErstellenRecord("Urlaub", List.of());
    mvc.perform(post("/api/gruppen").content(mapper.writeValueAsString(gruppe)).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is(400));
  }

  @Test
  @DisplayName("Anzeigen aller Gruppen für eine Person")
  void test3() throws Exception {
    Gruppe fakeGruppe = new Gruppe(1L,"Reise nach ..","Hans");
    when(service.getNutzergruppen("Hans")).thenReturn(List.of(fakeGruppe));

    mvc.perform(get("/api/user/Hans/gruppen"))
        .andExpect(status().is(200));
  }

  @Test
  @DisplayName("Mitglied ohne Gruppen erhält leere Liste")
  void test4() throws Exception {
    Gruppe fakeGruppe = new Gruppe(1L,"Reise nach ..","Hans");
    when(service.getNutzergruppen("Hans")).thenReturn(List.of(fakeGruppe));

    String response = mvc.perform(get("/api/user/Peter/gruppen"))
        .andExpect(status().is(200))
        .andReturn().getResponse().getContentAsString();
    assertThat(response).isEqualTo("[]");
  }

  @Test
  @DisplayName("Existierende Gruppe wird gefunden")
  void test5() throws Exception {
    Gruppe fakeGruppe = new Gruppe(1L,"Reise nach ..","Hans");
    when(service.getGruppeById(1L)).thenReturn(fakeGruppe);

    mvc.perform(get("/api/gruppen/1"))
        .andExpect(status().is(200));
  }

  @Test
  @DisplayName("Nicht existierende Gruppe gibt Status 404")
  void test6() throws Exception {
    Gruppe fakeGruppe = new Gruppe(1L,"Reise nach ..","Hans");
    when(service.getGruppeById(1L)).thenReturn(fakeGruppe);

    mvc.perform(get("/api/gruppen/2"))
        .andExpect(status().is(404));
  }

  @Test
  @DisplayName("Existierende Gruppe wird geschlossen")
  void test7() throws Exception {
    Gruppe fakeGruppe = new Gruppe(1L,"Reise nach ..","Hans");
    when(service.getGruppeById(1L)).thenReturn(fakeGruppe);

    mvc.perform(post("/api/gruppen/1/schliessen"))
        .andExpect(status().is(200));
  }

  @Test
  @DisplayName("Nicht existierende Gruppe wird nicht geschlossen und gibt Status 404")
  void test8() throws Exception {
    Gruppe fakeGruppe = new Gruppe(1L,"Reise nach ..","Hans");
    when(service.getGruppeById(1L)).thenReturn(fakeGruppe);

    mvc.perform(post("/api/gruppen/2/schliessen"))
        .andExpect(status().is(404));
  }

  @Test
  @DisplayName("Richtige Auslage kann erstellt werden")
  void test9() throws Exception {
    Gruppe fakeGruppe = new Gruppe(1L,"Reise nach ..","Hans");
    RechnungRecord rechnung = new RechnungRecord("Black Paint", "Keith", 2599, List.of("Keith", "Mick", "Ronnie"));
    when(service.getGruppeById(1L)).thenReturn(fakeGruppe);

    mvc.perform(post("/api/gruppen/1/auslagen").content(mapper.writeValueAsString(rechnung)).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is(201));
  }

  @Test
  @DisplayName("Auslage für nicht vorhandene Gruppe gibt Status 404")
  void test10() throws Exception {
    Gruppe fakeGruppe = new Gruppe(1L,"Reise nach ..","Hans");
    RechnungRecord rechnung = new RechnungRecord("Black Paint", "Keith", 2599, List.of("Keith", "Mick", "Ronnie"));
    when(service.getGruppeById(1L)).thenReturn(fakeGruppe);

    mvc.perform(post("/api/gruppen/2/auslagen").content(mapper.writeValueAsString(rechnung)).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is(404));
  }

  @Test
  @DisplayName("Falsche Auslage für vorhandene Gruppe gibt Status 400")
  void test11() throws Exception {
    Gruppe fakeGruppe = new Gruppe(1L,"Reise nach ..","Hans");
    RechnungRecord rechnung = new RechnungRecord("Black Paint", "Keith", 2599, List.of());
    when(service.getGruppeById(1L)).thenReturn(fakeGruppe);

    mvc.perform(post("/api/gruppen/1/auslagen").content(mapper.writeValueAsString(rechnung)).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is(400));
  }

  @Test
  @DisplayName("Richtige Auslage für geschlossene Gruppe gibt Status 409")
  void test12() throws Exception {
    Gruppe fakeGruppe = new Gruppe(1L,"Reise nach ..","Hans");
    RechnungRecord rechnung = new RechnungRecord("Black Paint", "Keith", 2599, List.of("Keith", "Mick", "Ronnie"));
    fakeGruppe.schliesseGruppe();
    when(service.getGruppeById(1L)).thenReturn(fakeGruppe);

    mvc.perform(post("/api/gruppen/1/auslagen").content(mapper.writeValueAsString(rechnung)).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is(409));
  }

  @Test
  @DisplayName("Ausgleich für vorhandene Gruppe")
  void test13() throws Exception {
    Gruppe fakeGruppe = new Gruppe(1L,"Reise nach ..","Hans");
    when(service.getGruppeById(1L)).thenReturn(fakeGruppe);

    mvc.perform(get("/api/gruppen/1/ausgleich"))
        .andExpect(status().is(200));
  }

  @Test
  @DisplayName("Ausgleich für nicht vorhandene Gruppe gibt Status 404")
  void test14() throws Exception {
    Gruppe fakeGruppe = new Gruppe(1L,"Reise nach ..","Hans");
    when(service.getGruppeById(1L)).thenReturn(fakeGruppe);

    mvc.perform(get("/api/gruppen/2/ausgleich"))
        .andExpect(status().is(404));
  }
}
