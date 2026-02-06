package fxdeals.controller;

import fxdeals.entity.Deal;
import fxdeals.repository.DealRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class DealControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DealRepository dealRepository;

    @BeforeEach
    void setUp() {
        dealRepository.deleteAll();
    }
    @Test
    void whenValidDeal_thenReturnsCreated() throws Exception {
        String json = """
        {
            "dealId": "D002",
            "fromCurrency": "USD",
            "toCurrency": "EUR",
            "amount": 150
        }
        """;

        mockMvc.perform(post("/deals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());
    }

    @Test
    void whenDealAlreadyExists_thenReturnsConflict() throws Exception {
        Deal deal = new Deal();
        deal.setDealId("D001");
        deal.setFromCurrency("USD");
        deal.setToCurrency("EUR");
        deal.setAmount(new BigDecimal("100"));

        dealRepository.save(deal);

        String json = """
            {
                "dealId": "D001",
                "fromCurrency": "USD",
                "toCurrency": "EUR",
                "amount": 100
            }
            """;

        mockMvc.perform(post("/deals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isConflict());
    }

    @Test
    void whenAmountZero_thenReturnsBadRequest() throws Exception {
        String json = """
        {
            "dealId": "D003",
            "fromCurrency": "USD",
            "toCurrency": "EUR",
            "amount": 0
        }
        """;

        mockMvc.perform(post("/deals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }
    @Test
    void whenDealIdEmpty_thenReturnsBadRequest() throws Exception {
        String json = """
        {
            "dealId": "",
            "fromCurrency": "USD",
            "toCurrency": "EUR",
            "amount": 100
        }
        """;

        mockMvc.perform(post("/deals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

}
