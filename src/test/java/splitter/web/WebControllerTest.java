package splitter.web;

import splitter.domain.gruppe.Gruppe;
import splitter.helper.WithMockOAuth2User;
import splitter.service.GruppenService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest
public class WebControllerTest {
  @Autowired
  MockMvc mvc;

  @MockBean
  GruppenService service;

  @Test
  @WithMockOAuth2User(login = "Hans")
  @DisplayName("Index wird aufgerufen")
  void test1() throws Exception{
    mvc.perform(get("/"))
        .andExpect(view().name("index"))
        .andExpect(status().isOk());
  }

  @Test
  @WithMockOAuth2User(login = "Hans")
  @DisplayName("Redirect auf die erstellte Gruppe")
  void test2() throws Exception{
    when(service.addGruppe("Reise nach ..","Hans")).thenReturn(1L);
    mvc.perform(post("/gruppe").param("gruppenName","Reise nach ..").with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/gruppe?id=1"));
  }

  @Test
  @WithMockOAuth2User(login = "Hans")
  @DisplayName("Url /gruppe zeigt richtige Gruppe an")
  void test3() throws Exception{
    Gruppe fakeGruppe = new Gruppe(1L,"Reise nach ..","Hans");
    when(service.addGruppe("Reise nach ..","Hans")).thenReturn(1L);
    when(service.getGruppeById(1L)).thenReturn(fakeGruppe);
    mvc.perform(get("/gruppe").param("id","1"))
            .andExpect(status().isOk())
            .andExpect(model().attribute("gruppe",fakeGruppe));
  }
  @Test
  @WithMockOAuth2User(login = "Hans")
  @DisplayName("Rechnung wird aufgerufen")
  void test4() throws Exception{
    Gruppe fakeGruppe = new Gruppe(1L,"Reise nach ..","Hans");
    when(service.addGruppe("Reise nach ..","Hans")).thenReturn(1L);
    when(service.getGruppeById(1L)).thenReturn(fakeGruppe);
    mvc.perform(get("/rechnung")
                    .param("id","1"))
        .andExpect(view().name("rechnung"))
        .andExpect(status().isOk());
  }
  @Test
  @WithMockOAuth2User(login = "Hans")
  @DisplayName("Rechnung hinzugefügt. Leitet wieder auf die richtige Gruppen Seite")
  void test5() throws Exception{
    Gruppe fakeGruppe = new Gruppe(1L,"Reise nach ..","Hans");
    fakeGruppe.addMitglied("Otto");
    when(service.addGruppe("Reise nach ..","Hans")).thenReturn(1L);
    when(service.getGruppeById(1L)).thenReturn(fakeGruppe);
    mvc.perform(post("/rechnung")
                    .param("rechnungName","Urlaub")
                    .param("bezahler","Hans")
                    .param("teilnehmerNamen","Hans")
                    .param("teilnehmerNamen","Otto")
                    .param("betrag","99.99")
                    .param("id","1")
                    .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/gruppe?id=1"));
  }
}
