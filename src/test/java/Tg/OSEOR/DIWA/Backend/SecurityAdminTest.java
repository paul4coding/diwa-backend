package Tg.OSEOR.DIWA.Backend;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import Tg.OSEOR.DIWA.Backend.dto.FinitionDTO.FinitionDTOResponse;
import Tg.OSEOR.DIWA.Backend.service.FinitionService;

/**
 * Tests de sécurité sur l'endpoint POST /api/v1/finitions/save.
 *
 * La route est en permitAll() au niveau URL, mais le contrôleur est protégé
 * par @PreAuthorize("hasRole('ADMIN')") → c'est la sécurité méthode qui s'exprime.
 *
 * Corrections apportées par rapport à la version initiale :
 *  1. JSON corrigé  : "prix" → "prixSupplement" + "vehiculeId" ajouté
 *     (sinon @Valid échoue en 400 avant que la sécurité s'exprime)
 *  2. FinitionService mocké (évite l'accès BDD dans le test admin)
 *  3. Admin → attend 201 CREATED (le contrôleur retourne CREATED, pas OK)
 *  4. Non-authentifié → attend 401 UNAUTHORIZED
 *     (JwtAuthenticationEntryPoint retourne SC_UNAUTHORIZED pour les anonymes)
 */
@SpringBootTest
@AutoConfigureMockMvc
class SecurityAdminTest {

    @Autowired
    private MockMvc mockMvc;

    /** Mocké pour éviter tout accès DB dans le test admin. */
    @MockBean
    private FinitionService finitionService;

    /** JSON valide pour FinitionDTORequest (tous les @NotNull renseignés). */
    private static final String VALID_BODY =
            "{\"nom\":\"Elite\",\"prixSupplement\":1500000,\"vehiculeId\":1}";

    @BeforeEach
    void setUp() {
        FinitionDTOResponse stub = new FinitionDTOResponse();
        stub.setId(1L);
        stub.setNom("Elite");
        stub.setPrixSupplement(1500000.0);
        stub.setVehiculeId(1L);
        when(finitionService.create(any())).thenReturn(stub);
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    @DisplayName("CLIENT → 403 FORBIDDEN (insuffisant pour @PreAuthorize('ADMIN'))")
    void testSaveFinition_AsClient_ReturnsForbidden() throws Exception {
        mockMvc.perform(post("/api/v1/finitions/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(VALID_BODY))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Non-authentifié → 401 UNAUTHORIZED (JwtAuthenticationEntryPoint)")
    void testSaveFinition_Unauthenticated_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(post("/api/v1/finitions/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(VALID_BODY))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("ADMIN → 201 CREATED (sécurité OK + service mocké)")
    void testSaveFinition_AsAdmin_ReturnsCreated() throws Exception {
        mockMvc.perform(post("/api/v1/finitions/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(VALID_BODY))
                .andExpect(status().isCreated());
    }
}
