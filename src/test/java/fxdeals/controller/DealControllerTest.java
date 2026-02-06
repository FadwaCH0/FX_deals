package fxdeals.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fxdeals.entity.Deal;
import fxdeals.service.DealService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DealController.class)
public class DealControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DealService dealService;

    @Autowired
    private ObjectMapper objectMapper;

    // ===== Test deal vide (null / violations @NotNull/@NotBlank) =====
    @Test
    void whenEmptyDeal_thenReturnsValidationErrors() throws Exception {
        Deal invalidDeal = new Deal(); // tous les champs null

        mockMvc.perform(post("/deals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDeal)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.dealId").value("Le dealId est obligatoire"))
                .andExpect(jsonPath("$.fromCurrency").value("Source currency is required"))
                .andExpect(jsonPath("$.toCurrency").value("Target currency is required"))
                .andExpect(jsonPath("$.amount").value("Transaction amount is required"));
    }

    // ===== Test deal avec amount = 0 (DecimalMin) =====
    @Test
    void whenAmountZero_thenReturnsDecimalMinError() throws Exception {
        Deal invalidDeal = new Deal();
        invalidDeal.setDealId("D003");
        invalidDeal.setFromCurrency("USD");
        invalidDeal.setToCurrency("EUR");
        invalidDeal.setAmount(BigDecimal.ZERO); // déclenche @DecimalMin

        mockMvc.perform(post("/deals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDeal)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.amount").value("Transaction amount must be greater than 0"));
    }

    // ===== Test deal déjà existant (conflit) =====
    @Test
    void whenDealAlreadyExists_thenReturnsConflict() throws Exception {
        Deal deal = new Deal();
        deal.setDealId("D001");
        deal.setFromCurrency("USD");
        deal.setToCurrency("EUR");
        deal.setAmount(BigDecimal.valueOf(100));

        when(dealService.saveDeal(any(Deal.class))).thenReturn("Deal already exists"); // <-- ; ajouté

        mockMvc.perform(post("/deals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deal)))
                .andExpect(status().isConflict())
                .andExpect(content().string("Deal already exists"));
    }


    // ===== Test deal valide (succès) =====
    @Test
    void whenValidDeal_thenReturnsCreated() throws Exception {
        Deal deal = new Deal();
        deal.setDealId("D002");
        deal.setFromCurrency("USD");
        deal.setToCurrency("EUR");
        deal.setAmount(BigDecimal.valueOf(100));

        when(dealService.saveDeal(any(Deal.class))).thenReturn("Deal saved successfully");

        mockMvc.perform(post("/deals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deal)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Deal saved successfully"));
    }
}
